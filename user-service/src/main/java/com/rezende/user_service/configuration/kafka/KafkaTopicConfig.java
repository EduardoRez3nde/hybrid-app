package com.rezende.user_service.configuration.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${app.kafka.topics.user-events}")
    private String userEventsTopicName;

    @Bean
    public NewTopic userEventsTopic() {
        return TopicBuilder
                .name(userEventsTopicName)
                .partitions(1)
                .replicas(0)
                .build();
    }
}
