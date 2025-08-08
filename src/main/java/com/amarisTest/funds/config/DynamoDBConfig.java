package com.amarisTest.funds.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

@Configuration
public class DynamoDBConfig {

    @Profile("prod")
    @Configuration
    static class ProdConfig {
        @Value("${aws.region:us-east-1}")
        private String region;

        @Bean
        public AmazonDynamoDB amazonDynamoDB() {
            return AmazonDynamoDBClientBuilder.standard()
                    .withRegion(region) // SIN endpoint manual
                    .withCredentials(DefaultAWSCredentialsProviderChain.getInstance()) // Instance Profile en EB
                    .build();
        }

        @Bean
        public DynamoDBMapper dynamoDBMapper(AmazonDynamoDB client) {
            return new DynamoDBMapper(client);
        }
    }

    @Profile("local")
    @Configuration
    static class LocalConfig {
        @Value("${aws.dynamodb.endpoint:http://localhost:8000}")
        private String endpoint;

        @Value("${aws.region:us-east-1}")
        private String region;

        @Bean
        public AmazonDynamoDB amazonDynamoDB() {
            return AmazonDynamoDBClientBuilder.standard()
                    .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, region))
                    .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials("dummy", "dummy"))) // para dynamo local
                    .build();
        }

        @Bean
        public DynamoDBMapper dynamoDBMapper(AmazonDynamoDB client) {
            return new DynamoDBMapper(client);
        }
    }
}
