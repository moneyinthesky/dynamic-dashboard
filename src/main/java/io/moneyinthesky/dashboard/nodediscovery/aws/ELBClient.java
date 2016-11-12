package io.moneyinthesky.dashboard.nodediscovery.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancing;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClientBuilder;
import com.amazonaws.services.elasticloadbalancing.model.DescribeLoadBalancersResult;
import com.amazonaws.services.elasticloadbalancing.model.Instance;
import com.amazonaws.services.elasticloadbalancing.model.LoadBalancerDescription;

import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;

class ELBClient {

	private AmazonElasticLoadBalancing elasticLoadBalancing;

	ELBClient(AWSCredentials credentials, Regions region) {
		this.elasticLoadBalancing = getElbClient(credentials, region);
	}

	List<String> getInstanceIds(String loadBalancerDNS) {
		DescribeLoadBalancersResult elbResult = elasticLoadBalancing.describeLoadBalancers();
		Optional<LoadBalancerDescription> elbDescription = elbResult.getLoadBalancerDescriptions()
				.stream()
				.filter(loadBalancerDescription -> loadBalancerDescription.getDNSName().equals(loadBalancerDNS))
				.findFirst();

		if(elbDescription.isPresent()) {
			return elbDescription.get().getInstances()
					.stream()
					.map(Instance::getInstanceId)
					.collect(toList());
		}

		return newArrayList();
	}

	private AmazonElasticLoadBalancing getElbClient(AWSCredentials credentials, Regions region) {
		return AmazonElasticLoadBalancingClientBuilder.standard()
				.withRegion(region)
				.withCredentials(new AWSStaticCredentialsProvider(credentials))
				.build();
	}
}
