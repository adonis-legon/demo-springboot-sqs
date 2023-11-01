package com.example.demospringbootsqs;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.aws.messaging.listener.Acknowledgment;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;

@Component
public class DemoMessageReader {

    @Autowired
    @Qualifier("threadPoolConsumerQueue")
    private AsyncTaskExecutor threadPoolConsumerQueue;

    @Autowired
    private DemoService demoService;

    @SqsListener(value = "${app.queue.url}", deletionPolicy = SqsMessageDeletionPolicy.NEVER)
    public void processDemoQueue(String message, Acknowledgment acknowledgment, @Headers Map<String, String> headers)
            throws Exception {
        threadPoolConsumerQueue.submit(() -> {
            try {
                String messageId = (String) headers.get("MessageId");
                demoService.process( messageId, message);

                acknowledgment.acknowledge();
            } catch (Exception e) {
                throw new RuntimeException("Failed to submit task to the worker");
            }
        });
    }
}
