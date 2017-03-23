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

import static java.util.Optional.empty;
import static java.util.stream.Collectors.toList;

class Route53Client {

	private Map<String, String> configuration;
	private AmazonRoute53 amazonRoute53;

	Route53Client(Map<String, String> configuration, AWSCredentials credentials, Regions region) {
		this.amazonRoute53 = getRoute53Client(credentials, region);
		this.configuration = configuration;
	}

	Optional<String> getHostedZoneIdByName(String hostedZoneName) {
		ListHostedZonesByNameRequest hostedZonesByNameRequest = new ListHostedZonesByNameRequest().withDNSName(hostedZoneName);
		List<HostedZone> hostedZones = amazonRoute53.listHostedZonesByName(hostedZonesByNameRequest).getHostedZones();

		if(!hostedZones.isEmpty()) {
			if(hostedZones.get(0).getName().equals(hostedZoneName)) {
				String rawHostedZoneId = hostedZones.get(0).getId();
				return Optional.of(rawHostedZoneId.substring(rawHostedZoneId.lastIndexOf('/') + 1));
			}
		}

		return empty();
	}

	Optional<String> getLoadBalancerDNS(String hostedZoneId) {
		String loadBalancerName = configuration.get("loadBalancer");

		return getResourceRecordByNameRecursively(hostedZoneId, loadBalancerName)
				.map(ResourceRecord::getValue)
				.map(Optional::of)
				.orElse(empty());
	}

	List<ResourceRecordSet> getResourceRecordSets(String hostedZoneId,
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
					.filter(resourceFilter)
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

		return empty();
	}

	private Optional<ResourceRecordSet> getHighestWeightedResourceRecordSetByName(String hostedZoneId,
																				  String resourceRecordSetName) {
		Optional<ResourceRecordSet> highestWeightedResourceRecordSet = empty();
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

	private AmazonRoute53 getRoute53Client(AWSCredentials credentials, Regions region) {
		return AmazonRoute53ClientBuilder.standard()
				.withRegion(region)
				.withCredentials(new AWSStaticCredentialsProvider(credentials))
				.build();
	}
}
