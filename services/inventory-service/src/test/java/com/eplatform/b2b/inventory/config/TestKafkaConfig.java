package com.eplatform.b2b.inventory.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.SendResult;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@TestConfiguration
@Profile("test")
public class TestKafkaConfig {

    @Bean
    @Primary
    public ProducerFactory<String, Object> testProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    @Primary
    public KafkaTemplate<String, Object> testKafkaTemplate() {
        return new TestKafkaTemplate<>(testProducerFactory());
    }

    private static class TestKafkaTemplate<K, V> extends KafkaTemplate<K, V> {
        public TestKafkaTemplate(ProducerFactory<K, V> producerFactory) {
            super(producerFactory);
        }

        @Override
        public CompletableFuture<SendResult<K, V>> send(String topic, V data) {
            return CompletableFuture.completedFuture(null);
        }

        @Override
        public CompletableFuture<SendResult<K, V>> send(String topic, K key, V data) {
            return CompletableFuture.completedFuture(null);
        }

        @Override
        public CompletableFuture<SendResult<K, V>> send(String topic, Integer partition, K key, V data) {
            return CompletableFuture.completedFuture(null);
        }

        @Override
        public CompletableFuture<SendResult<K, V>> send(String topic, Integer partition, Long timestamp, K key, V data) {
            return CompletableFuture.completedFuture(null);
        }
    }
}
