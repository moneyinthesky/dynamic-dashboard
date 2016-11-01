package io.moneyinthesky.dashboard.nodediscovery.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.route53.model.ResourceRecordSet;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Inject;
import io.moneyinthesky.dashboard.dao.SettingsDao;
import io.moneyinthesky.dashboard.data.settings.Settings;
import io.moneyinthesky.dashboard.nodediscovery.NodeDiscoveryMethod;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.asList;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;

public class AwsDiscoveryMethod implements NodeDiscoveryMethod {

	private static final Logger logger = getLogger(AwsDiscoveryMethod.class);
	private static final String PERSISTED_AWS_RESPONSE_JSON = "persisted/aws-response.json";

	private SettingsDao settingsDao;
	private ObjectMapper objectMapper;
	private Cache<String, List<String>> cache;

	@Inject
	public AwsDiscoveryMethod(SettingsDao settingsDao, ObjectMapper objectMapper) {
		this.settingsDao = settingsDao;
		this.objectMapper = objectMapper;
		this.cache = CacheBuilder.newBuilder()
				.expireAfterWrite(10, MINUTES)
				.build();

		initializeCache();
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
			nodeUrls = getNodeUrls(credentials, region, configuration,
					hostedZoneName, appPrefix, loadBalancer);
			cache.put(key, nodeUrls);

			persistCache();
		}

		return nodeUrls;
	}

	private List<String> getNodeUrls(AWSCredentials credentials, Regions region, Map<String, String> configuration,
									 String hostedZoneName, String appPrefix, String loadBalancer) {
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

		logger.info("Time to query AWS for {}: {}", loadBalancer, (currentTimeMillis() - start) / 1000d);
		return instanceResources.stream()
				.filter(instanceResource -> instanceResource.getName().startsWith(appPrefix))
				.map(instanceResource -> "http://" + instanceResource.getName().substring(0, instanceResource.getName().length()-1))
				.collect(toList());
	}

	private AWSCredentials getCredentials(String accessKey, String secretKey) {
		return new BasicAWSCredentials(accessKey, secretKey);
	}

	private String generateKey(String... keyElements) {
		return asList(keyElements).stream().collect(joining("/"));
	}

	private void initializeCache() {
		try {
			Map<String, List<String>> persistedAwsResponses = objectMapper.readValue(new File(PERSISTED_AWS_RESPONSE_JSON), Map.class);
			cache.putAll(persistedAwsResponses);
		} catch (IOException e) {
			logger.warn("Unable to read persisted AWS responses");
		}
	}

	private void persistCache() {
		try {
			objectMapper.writeValue(new File(PERSISTED_AWS_RESPONSE_JSON), cache.asMap());
		} catch (IOException e) {
			logger.error("Unable to persist AWS response to file", e);
		}
	}
}
