package com.example.demospringbootsqs.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.config.SimpleMessageListenerContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;

@Configuration
public class SqsConfig {
    @Value("${cloud.aws.credentials.profile-name:}")
    private String awsCredentialsProfileName;

    @Value("${cloud.aws.region.static}")
    private String awsRegion;

    @Value("${app.queue.batch-size}")
    private int batchSize;

    @Value("${app.queue.wait-time}")
    private int queueWaitTime;

    @Value("${app.queue.error-backoff-time}")
    private long errorBackoffTime;

    @Value("${app.queue.batch-concurrency}")
    private int batchConcurrency;

    @Bean
    @Primary
    public AmazonSQSAsync amazonSQSAsync() {
        if (awsCredentialsProfileName != null && !awsCredentialsProfileName.isEmpty()) {
            return AmazonSQSAsyncClientBuilder.standard().withRegion(awsRegion)
                    .withCredentials(new ProfileCredentialsProvider(awsCredentialsProfileName)).build();

        } else {
            return AmazonSQSAsyncClientBuilder.standard().withRegion(awsRegion).build();
        }
    }

    @Bean
    public SimpleMessageListenerContainerFactory simpleMessageListenerContainerFactory(AmazonSQSAsync amazonSqs) {
        SimpleMessageListenerContainerFactory factory = new SimpleMessageListenerContainerFactory();
        factory.setAmazonSqs(amazonSqs);
        factory.setAutoStartup(true);
        factory.setMaxNumberOfMessages(batchSize);
        factory.setWaitTimeOut(queueWaitTime);
        factory.setBackOffTime(errorBackoffTime);
        return factory;
    }

    @Bean(name = "threadPoolConsumerQueue")
    public ThreadPoolTaskExecutor threadPoolConsumerQueue() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(batchConcurrency);
        executor.setQueueCapacity(0);
        executor.setRejectedExecutionHandler(new BlockingTaskSubmissionPolicy(1000));
        return executor;
    }
}
