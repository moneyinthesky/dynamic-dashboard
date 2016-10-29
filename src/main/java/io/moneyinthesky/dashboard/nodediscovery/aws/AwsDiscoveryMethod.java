package io.moneyinthesky.dashboard.nodediscovery.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.route53.model.ResourceRecordSet;
import io.moneyinthesky.dashboard.nodediscovery.NodeDiscoveryMethod;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public class AwsDiscoveryMethod implements NodeDiscoveryMethod {

	@Override
	public List<String> generateNodeUrls(Map<String, String> configuration) {
		//TODO validate input
		AWSCredentials credentials = getCredentials(configuration);

		String appIdentifier = configuration.get("appIdentifier");
		String hostedZoneName = configuration.get("hostedZone");

		ELBClient elbClient = new ELBClient(configuration, credentials);
		EC2Client ec2Client = new EC2Client(configuration, credentials);
		Route53Client route53Client = new Route53Client(configuration, credentials);

		Optional<String> hostedZoneId = route53Client.getHostedZoneIdByName(hostedZoneName);
		Optional<String> loadBalancerDNS = route53Client.getLoadBalancerDNS(hostedZoneId.get());
		List<String> instanceIds = elbClient.getInstanceIds(loadBalancerDNS.get());

		List<String> privateIps = ec2Client.getPrivateIpsFromInstances(instanceIds);
		List<ResourceRecordSet> instanceResources = route53Client.getResourceRecordSets(hostedZoneId.get(),
				resourceRecordSet -> {
					if(resourceRecordSet.getResourceRecords().size() > 0) {
						return privateIps.contains(resourceRecordSet.getResourceRecords().get(0).getValue());
					}
					return false;
				});

		return instanceResources.stream()
				.filter(instanceResource -> instanceResource.getName().startsWith(appIdentifier))
				.map(instanceResource -> "http://" + instanceResource.getName().substring(0, instanceResource.getName().length()-1))
				.collect(toList());
	}

	private AWSCredentials getCredentials(Map<String, String> configuration) {
		return new BasicAWSCredentials(configuration.get("accessKey"), configuration.get("secretKey"));
	}
}
