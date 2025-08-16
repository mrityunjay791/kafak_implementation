package org.example.kafka.consumer;

import org.springframework.stereotype.Component;

@Component
public class KafkaConsumerListener {

//    @KafkaListener(topics = "test-topic", groupId = "my-consumer-group")
    public void listen(String message) {
        System.out.println("Received message: " + message);
    }
}