package com.rezende.driver_service.configuration.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
public class KafkaTopicsConfig {

    @Value("${app.kafka.topics.driver-status-updates}")
    private String driverStatusUpdatesTopicName;

    @Value("${app.kafka.topics.driver-lifecycle}")
    private String driverLifecycleTopicName;

    @Bean
    public KafkaAdmin.NewTopics driverServiceTopics() {
        return new KafkaAdmin.NewTopics(
                TopicBuilder.name(driverStatusUpdatesTopicName)
                        .partitions(3)
                        .replicas(1)
                        .build(),
                TopicBuilder.name(driverLifecycleTopicName)
                        .partitions(3)
                        .replicas(1)
                        .build()
        );
    }
}

