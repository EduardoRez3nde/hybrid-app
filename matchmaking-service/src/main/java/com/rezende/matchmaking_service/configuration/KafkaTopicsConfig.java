package com.rezende.matchmaking_service.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
public class KafkaTopicsConfig {

    @Value("${app.kafka.topics.driver-assigned-ride-event}")
    private String driverAssignedRideTopicName;

    @Value("${app.kafka.topics.no-driver-found-event}")
    private String noDriverFoundTopicName;

    @Bean
    public KafkaAdmin.NewTopics taxiServiceTopics() {
        return new KafkaAdmin.NewTopics(
                TopicBuilder.name(driverAssignedRideTopicName)
                        .partitions(3)
                        .replicas(1)
                        .build(),
                TopicBuilder.name(noDriverFoundTopicName)
                        .partitions(3)
                        .replicas(1)
                        .build()
        );
    }
}

