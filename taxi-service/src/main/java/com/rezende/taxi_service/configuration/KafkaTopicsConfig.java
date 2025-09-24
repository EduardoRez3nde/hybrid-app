package com.rezende.taxi_service.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
public class KafkaTopicsConfig {

    @Value("${app.kafka.topics.driver-rated-event}")
    private String driverRatedTopicName;

    @Value("${app.kafka.topics.ride-accept-event}")
    private String rideAcceptTopicName;

    @Value("${app.kafka.topics.ride-reject-event}")
    private String rideRejectTopicName;

    @Bean
    public KafkaAdmin.NewTopics taxiServiceTopics() {
        return new KafkaAdmin.NewTopics(
                TopicBuilder.name(driverRatedTopicName)
                        .partitions(3)
                        .replicas(1)
                        .build(),
                TopicBuilder.name(rideAcceptTopicName)
                        .partitions(3)
                        .replicas(1)
                        .build(),
                TopicBuilder.name(rideRejectTopicName)
                        .partitions(3)
                        .replicas(1)
                        .build()
        );
    }
}

