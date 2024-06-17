package br.com.bbtecno.desafio.core.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsConfig {

    @Value("${aws.access.key}")
    private  String awsAccessKey;

    @Value("${aws.secret.key}")
    private  String awsSecretKey;

    @Bean
    public AmazonS3 getAmazonS3Client() {
        AWSCredentials credentails = new BasicAWSCredentials(awsAccessKey,awsSecretKey);
        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentails))
                .withRegion(Regions.US_EAST_1)
                .build();
    }

}
