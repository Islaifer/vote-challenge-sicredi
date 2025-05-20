
package com.challenge.vote_visualizer.configurations;

import com.challenge.vote_visualizer.dto.AgendaResult;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Value("${KAFKA_URL}")
    private String kafkaUtl;

    @Bean
    public ConsumerFactory<String, AgendaResult> agendaResultConsumerFactory(){
        JsonDeserializer<AgendaResult> deserializer = new JsonDeserializer<>(AgendaResult.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);

        return new DefaultKafkaConsumerFactory<>(
                Map.of(
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaUtl + ":9092",
                        ConsumerConfig.GROUP_ID_CONFIG, "vote-visualizer",
                        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer
                ),
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AgendaResult> agendaResultKafkaListenerContainerFactory(){
        ConcurrentKafkaListenerContainerFactory<String, AgendaResult> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(agendaResultConsumerFactory());
        return factory;
    }
}