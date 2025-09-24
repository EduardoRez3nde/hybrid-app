package com.rezende.vehicle_service.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
public class KafkaTopicConfig {

    @Value("${app.kafka.topics.approved-vehicle-event}")
    private String approvedVehicleTopic;

    @Bean
    public KafkaAdmin.NewTopics vehicleEventsTopic() {
        return new KafkaAdmin.NewTopics(
                TopicBuilder.name(approvedVehicleTopic)
                        .partitions(3)
                        .replicas(1)
                        .build()
        );
    }
}
