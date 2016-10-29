package io.moneyinthesky.dashboard.nodediscovery.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.route53.model.ResourceRecordSet;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Inject;
import io.moneyinthesky.dashboard.dao.SettingsDao;
import io.moneyinthesky.dashboard.data.settings.Settings;
import io.moneyinthesky.dashboard.nodediscovery.NodeDiscoveryMethod;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;

public class AwsDiscoveryMethod implements NodeDiscoveryMethod {

	private static final Logger logger = getLogger(AwsDiscoveryMethod.class);
	private SettingsDao settingsDao;
	private Cache<String, List<String>> cache;

	@Inject
	public AwsDiscoveryMethod(SettingsDao settingsDao) {
		this.settingsDao = settingsDao;
		this.cache = CacheBuilder.newBuilder()
				.expireAfterWrite(10, TimeUnit.MINUTES)
				.build();
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

		Map<String, Object> awsConfiguration = settings.getPlugins().get("aws");
		String accessKey = (String) awsConfiguration.get("accessKey");
		String secretKey = (String) awsConfiguration.get("secretKey");
		String regionAsString = (String) awsConfiguration.get("region");
		Regions region = Regions.fromName(regionAsString);
		AWSCredentials credentials = getCredentials(accessKey, secretKey);

		String appPrefix = configuration.get("appPrefix");
		String hostedZoneName = configuration.get("hostedZone");
		String loadBalancer = configuration.get("loadBalancer");

		String key = generateKey(accessKey, secretKey, regionAsString,
				appPrefix, hostedZoneName, loadBalancer);

		List<String> nodeUrls = cache.getIfPresent(key);
		if(nodeUrls == null) {
			long start = currentTimeMillis();
			ELBClient elbClient = new ELBClient(credentials, region);
			EC2Client ec2Client = new EC2Client(credentials, region);
			Route53Client route53Client = new Route53Client(configuration, credentials, region);

			Optional<String> hostedZoneId = route53Client.getHostedZoneIdByName(hostedZoneName);
			Optional<String> loadBalancerDNS = route53Client.getLoadBalancerDNS(hostedZoneId.get());
			List<String> instanceIds = elbClient.getInstanceIds(loadBalancerDNS.get());

			List<String> privateIps = ec2Client.getPrivateIpsFromInstances(instanceIds);
			List<ResourceRecordSet> instanceResources = route53Client.getResourceRecordSets(hostedZoneId.get(),
					resourceRecordSet -> {
						if (resourceRecordSet.getResourceRecords().size() > 0) {
							return privateIps.contains(resourceRecordSet.getResourceRecords().get(0).getValue());
						}
						return false;
					});

			nodeUrls = instanceResources.stream()
					.filter(instanceResource -> instanceResource.getName().startsWith(appPrefix))
					.map(instanceResource -> "http://" + instanceResource.getName().substring(0, instanceResource.getName().length()-1))
					.collect(toList());

			logger.info("Time to query AWS: {}", (currentTimeMillis() - start) / 1000d);
			cache.put(key, nodeUrls);
		}

		return nodeUrls;
	}

	private String generateKey(String... keyElements) {
		return asList(keyElements).stream().collect(joining("/"));
	}

	private AWSCredentials getCredentials(String accessKey, String secretKey) {
		return new BasicAWSCredentials(accessKey, secretKey);
	}
}
