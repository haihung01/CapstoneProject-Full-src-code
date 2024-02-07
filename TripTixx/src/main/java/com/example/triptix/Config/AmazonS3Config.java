package com.example.triptix.Config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class AmazonS3Config {
    @Autowired
    Environment env;        //org.springframework.core.env.Environment;

    @Bean
    public AmazonS3 s3() {
        AWSCredentials awsCredentials =
                new BasicAWSCredentials(env.getProperty("aws.access_key"), env.getProperty("aws.secret_key"));
        return AmazonS3ClientBuilder
                .standard()
                .withRegion(env.getProperty("aws.s3.region"))
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }
}