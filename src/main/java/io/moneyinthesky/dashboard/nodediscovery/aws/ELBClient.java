package io.moneyinthesky.dashboard.nodediscovery.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancing;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClientBuilder;
import com.amazonaws.services.elasticloadbalancing.model.DescribeLoadBalancersResult;
import com.amazonaws.services.elasticloadbalancing.model.LoadBalancerDescription;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;

public class ELBClient {

	private AmazonElasticLoadBalancing elasticLoadBalancing;

	public ELBClient(Map<String, String> configuration, AWSCredentials credentials) {
		this.elasticLoadBalancing = getElbClient(configuration, credentials);
	}

	public List<String> getInstanceIds(String loadBalancerDNS) {
		DescribeLoadBalancersResult elbResult = elasticLoadBalancing.describeLoadBalancers();
		Optional<LoadBalancerDescription> elbDescription = elbResult.getLoadBalancerDescriptions()
				.stream()
				.filter(loadBalancerDescription -> loadBalancerDescription.getDNSName().equals(loadBalancerDNS))
				.findFirst();

		if(elbDescription.isPresent()) {
			return elbDescription.get().getInstances()
					.stream()
					.map(instance -> instance.getInstanceId())
					.collect(toList());
		}

		return newArrayList();
	}

	private AmazonElasticLoadBalancing getElbClient(Map<String, String> configuration, AWSCredentials credentials) {
		return AmazonElasticLoadBalancingClientBuilder.standard()
				.withRegion(Regions.fromName(configuration.get("region")))
				.withCredentials(new AWSStaticCredentialsProvider(credentials))
				.build();
	}
}
