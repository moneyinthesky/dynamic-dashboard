package io.moneyinthesky.dashboard.nodediscovery.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.route53.model.ResourceRecordSet;
import com.google.inject.Inject;
import io.moneyinthesky.dashboard.dao.SettingsDao;
import io.moneyinthesky.dashboard.data.settings.Settings;
import io.moneyinthesky.dashboard.nodediscovery.NodeDiscoveryMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;

public class AwsDiscoveryMethod implements NodeDiscoveryMethod {

	private static final Logger logger = LoggerFactory.getLogger(AwsDiscoveryMethod.class);
	private SettingsDao settingsDao;

	@Inject
	public AwsDiscoveryMethod(SettingsDao settingsDao) {
		this.settingsDao = settingsDao;
	}

	@Override
	public List<String> generateNodeUrls(Map<String, String> configuration) {
		//TODO validate input
		Settings settings;
		try {
			 settings = settingsDao.readSettings();
		} catch (IOException e) {
			logger.error("Unable to read settings JSON", e);
			return newArrayList();
		}

		String accessKey = (String) settings.getPlugins().get("aws").get("accessKey");
		String secretKey = (String) settings.getPlugins().get("aws").get("secretKey");
		Regions region = Regions.fromName((String) settings.getPlugins().get("aws").get("region"));
		AWSCredentials credentials = getCredentials(accessKey, secretKey);

		String appPrefix = configuration.get("appPrefix");
		String hostedZoneName = configuration.get("hostedZone");

		ELBClient elbClient = new ELBClient(credentials, region);
		EC2Client ec2Client = new EC2Client(credentials, region);
		Route53Client route53Client = new Route53Client(configuration, credentials, region);

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
				.filter(instanceResource -> instanceResource.getName().startsWith(appPrefix))
				.map(instanceResource -> "http://" + instanceResource.getName().substring(0, instanceResource.getName().length()-1))
				.collect(toList());
	}

	private AWSCredentials getCredentials(String accessKey, String secretKey) {
		return new BasicAWSCredentials(accessKey, secretKey);
	}
}
