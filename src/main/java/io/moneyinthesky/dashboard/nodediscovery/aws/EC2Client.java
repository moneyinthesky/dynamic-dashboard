package io.moneyinthesky.dashboard.nodediscovery.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;

import java.util.ArrayList;
import java.util.List;

public class EC2Client {

	private AmazonEC2 amazonEC2;

	public EC2Client(AWSCredentials credentials, Regions region) {
		this.amazonEC2 = getEC2Client(credentials, region);
	}

	public List<String> getPrivateIpsFromInstances(List<String> instanceIds) {
		List<String> privateIps = new ArrayList<>();

		DescribeInstancesRequest ec2Request = new DescribeInstancesRequest().withInstanceIds(instanceIds);
		DescribeInstancesResult result = amazonEC2.describeInstances(ec2Request);

		result.getReservations().forEach(reservation ->
				reservation.getInstances()
						.forEach(instance -> privateIps.add(instance.getPrivateIpAddress())));

		return privateIps;
	}

	private AmazonEC2 getEC2Client(AWSCredentials credentials, Regions region) {
		return AmazonEC2ClientBuilder.standard()
				.withRegion(region)
				.withCredentials(new AWSStaticCredentialsProvider(credentials))
				.build();
	}
}
