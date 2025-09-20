package com.rezende.taxi_service.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
public class KafkaTopicsConfig {

    @Value("${app.kafka.topics.driver-rated}")
    private String driverRatedTopicName;

    @Bean
    public KafkaAdmin.NewTopics taxiServiceTopics() {
        return new KafkaAdmin.NewTopics(
                TopicBuilder.name(driverRatedTopicName)
                        .partitions(3)
                        .replicas(1)
                        .build()
        );
    }
}

