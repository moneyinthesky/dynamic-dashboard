package io.moneyinthesky.dashboard.nodediscovery.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.Instance;

import java.util.List;

import static java.util.stream.Collectors.toList;

class EC2Client {

	private AmazonEC2 amazonEC2;

	EC2Client(AWSCredentials credentials, Regions region) {
		this.amazonEC2 = getEC2Client(credentials, region);
	}

	List<String> getPrivateIpsFromInstances(List<String> instanceIds) {
		DescribeInstancesRequest ec2Request = new DescribeInstancesRequest().withInstanceIds(instanceIds);

		return amazonEC2.describeInstances(ec2Request).getReservations()
				.stream()
				.flatMap(reservation ->
						reservation.getInstances()
								.stream()
								.map(Instance::getPrivateIpAddress))
				.collect(toList());
	}

	private AmazonEC2 getEC2Client(AWSCredentials credentials, Regions region) {
		return AmazonEC2ClientBuilder.standard()
				.withRegion(region)
				.withCredentials(new AWSStaticCredentialsProvider(credentials))
				.build();
	}
}
