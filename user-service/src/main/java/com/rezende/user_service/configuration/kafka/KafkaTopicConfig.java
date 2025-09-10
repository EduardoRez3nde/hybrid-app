package com.rezende.user_service.configuration.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
public class KafkaTopicConfig {

    @Value("${app.kafka.topics.user-events}")
    private String userEventsTopicName;

    @Bean
    public KafkaAdmin.NewTopics userEventsTopic() {
        return new KafkaAdmin.NewTopics(
                TopicBuilder
                        .name(userEventsTopicName)
                        .partitions(1)
                        .replicas(1)
                        .build()
        );
    }
}
