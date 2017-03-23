package io.moneyinthesky.dashboard.nodediscovery.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancing;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClientBuilder;
import com.amazonaws.services.elasticloadbalancing.model.Instance;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;

class ELBClient {

	private AmazonElasticLoadBalancing elasticLoadBalancing;

	ELBClient(AWSCredentials credentials, Regions region) {
		this.elasticLoadBalancing = getElbClient(credentials, region);
	}

	List<String> getInstanceIds(String loadBalancerDNS) {
		return elasticLoadBalancing.describeLoadBalancers().getLoadBalancerDescriptions()
				.stream()
				.filter(loadBalancerDescription -> loadBalancerDescription.getDNSName().equals(loadBalancerDNS))
				.findFirst()
				.map(elbDescription -> elbDescription.getInstances()
						.stream()
						.map(Instance::getInstanceId)
						.collect(toList()))
				.orElse(newArrayList());
	}

	private AmazonElasticLoadBalancing getElbClient(AWSCredentials credentials, Regions region) {
		return AmazonElasticLoadBalancingClientBuilder.standard()
				.withRegion(region)
				.withCredentials(new AWSStaticCredentialsProvider(credentials))
				.build();
	}
}
