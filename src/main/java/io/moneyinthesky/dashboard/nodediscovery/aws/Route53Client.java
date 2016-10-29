package io.moneyinthesky.dashboard.nodediscovery.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.route53.AmazonRoute53;
import com.amazonaws.services.route53.AmazonRoute53ClientBuilder;
import com.amazonaws.services.route53.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

public class Route53Client {

	private Map<String, String> configuration;
	private AmazonRoute53 amazonRoute53;

	public Route53Client(Map<String, String> configuration, AWSCredentials credentials) {
		this.amazonRoute53 = getRoute53Client(configuration, credentials);
		this.configuration = configuration;
	}

	public Optional<String> getHostedZoneIdByName(String hostedZoneName) {
		ListHostedZonesByNameRequest hostedZonesByNameRequest = new ListHostedZonesByNameRequest().withDNSName(hostedZoneName);
		List<HostedZone> hostedZones = amazonRoute53.listHostedZonesByName(hostedZonesByNameRequest).getHostedZones();

		if(!hostedZones.isEmpty()) {
			if(hostedZones.get(0).getName().equals(hostedZoneName)) {
				String rawHostedZoneId = hostedZones.get(0).getId();
				return Optional.of(rawHostedZoneId.substring(rawHostedZoneId.lastIndexOf('/') + 1));
			}
		}

		return Optional.empty();
	}

	public Optional<String> getLoadBalancerDNS(String hostedZoneId) {
		String loadBalancerName = configuration.get("loadBalancer");

		Optional<ResourceRecord> loadBalancer = getResourceRecordByNameRecursively(hostedZoneId, loadBalancerName);
		if(loadBalancer.isPresent()) {
			return Optional.of(loadBalancer.get().getValue());
		}

		return Optional.empty();
	}

	public List<ResourceRecordSet> getResourceRecordSets(String hostedZoneId,
														 Predicate<ResourceRecordSet> resourceFilter) {
		List<ResourceRecordSet> resourceRecordSets = new ArrayList<>();

		ListResourceRecordSetsRequest resourceRecordSetsRequest;
		ListResourceRecordSetsResult resourceRecordSetsResult = null;

		do {
			if(resourceRecordSetsResult == null) {
				resourceRecordSetsRequest = new ListResourceRecordSetsRequest(hostedZoneId);
			} else {
				resourceRecordSetsRequest = new ListResourceRecordSetsRequest(hostedZoneId)
						.withStartRecordName(resourceRecordSetsResult.getNextRecordName());
			}
			resourceRecordSetsResult = amazonRoute53.listResourceRecordSets(resourceRecordSetsRequest);

			List<ResourceRecordSet> allResourceRecordSets = resourceRecordSetsResult.getResourceRecordSets();
			resourceRecordSets.addAll(allResourceRecordSets.stream()
					.filter(resourceRecordSet -> resourceFilter.test(resourceRecordSet))
					.collect(toList()));

		} while(resourceRecordSetsResult.isTruncated());

		return resourceRecordSets;
	}

	private Optional<ResourceRecord> getResourceRecordByNameRecursively(String hostedZoneId,
																		String resourceRecordSetName) {
		Optional<ResourceRecordSet> resourceRecordSet = getHighestWeightedResourceRecordSetByName(hostedZoneId, resourceRecordSetName);

		if(resourceRecordSet.isPresent()) {
			if(resourceRecordSet.get().getAliasTarget() != null) {
				return getResourceRecordByNameRecursively(hostedZoneId, resourceRecordSet.get().getAliasTarget().getDNSName());
			} else {
				return Optional.of(resourceRecordSet.get().getResourceRecords().get(0));
			}
		}

		return Optional.empty();
	}

	private Optional<ResourceRecordSet> getHighestWeightedResourceRecordSetByName(String hostedZoneId,
																				  String resourceRecordSetName) {
		Optional<ResourceRecordSet> highestWeightedResourceRecordSet = Optional.empty();
		List<ResourceRecordSet> resourceRecordSets = getResourceRecordSets(hostedZoneId,
				resourceRecordSet -> resourceRecordSet.getName().equals(resourceRecordSetName));

		if(resourceRecordSets != null) {
			for (ResourceRecordSet resourceRecordSet : resourceRecordSets) {
				if (!highestWeightedResourceRecordSet.isPresent() ||
						resourceRecordSet.getWeight() > highestWeightedResourceRecordSet.get().getWeight()) {
					highestWeightedResourceRecordSet = Optional.of(resourceRecordSet);
				}
			}
		}

		return highestWeightedResourceRecordSet;
	}

	private AmazonRoute53 getRoute53Client(Map<String, String> configuration, AWSCredentials credentials) {
		return AmazonRoute53ClientBuilder.standard()
				.withRegion(Regions.fromName(configuration.get("region")))
				.withCredentials(new AWSStaticCredentialsProvider(credentials))
				.build();
	}
}
