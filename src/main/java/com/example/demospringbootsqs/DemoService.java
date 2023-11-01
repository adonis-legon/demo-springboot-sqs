package com.example.demospringbootsqs;

import java.util.Random;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DemoService {
    public void process(String messageId, String messageContent){
        try {
            long startTime = System.nanoTime();

            // Simulate app logic with a random thread sleep between 500ms to 1500s
            int processingTime = 500 * (new Random().nextInt(3) + 1);
            Thread.sleep(processingTime);

            long endTime = System.nanoTime();
            long executionTime = (endTime - startTime) / 1_000_000;

            log.info("Message " + messageId + " processed in: " + executionTime + " ms");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
