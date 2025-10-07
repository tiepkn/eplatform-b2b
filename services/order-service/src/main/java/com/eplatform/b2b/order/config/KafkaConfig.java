package com.eplatform.b2b.order.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@EnableKafka
public class KafkaConfig {

  @Bean
  public NewTopic topicOrderPlaced() {
    return TopicBuilder.name("order.placed").partitions(3).replicas(1).build();
  }

  @Bean
  public NewTopic topicInventoryReserved() {
    return TopicBuilder.name("inventory.reserved").partitions(3).replicas(1).build();
  }

  @Bean
  public NewTopic topicInventoryRejected() {
    return TopicBuilder.name("inventory.rejected").partitions(3).replicas(1).build();
  }

  @Bean
  public NewTopic topicPaymentSucceeded() {
    return TopicBuilder.name("payment.succeeded").partitions(3).replicas(1).build();
  }

  @Bean
  public NewTopic topicPaymentFailed() {
    return TopicBuilder.name("payment.failed").partitions(3).replicas(1).build();
  }
}
