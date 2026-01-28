package com.practice.producer.config

import com.practice.common.event.OrderCreatedEventProto.OrderCreatedEvent
import io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory

/**
 * Kafka Producer 설정 (Protobuf + Schema Registry)
 *
 * Schema Registry를 통해:
 * 1. 스키마 버전 관리
 * 2. 스키마 호환성 검사 (BACKWARD 호환)
 * 3. 스키마 ID로 압축된 메시지 전송
 */
@Configuration
class KafkaProducerConfig {

    @Value("\${spring.kafka.bootstrap-servers}")
    private lateinit var bootstrapServers: String

    @Value("\${spring.kafka.properties.schema.registry.url}")
    private lateinit var schemaRegistryUrl: String

    /**
     * Producer 설정 맵
     */
    private fun producerConfigs(): Map<String, Any> {
        return mapOf(
            // 브로커 주소
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,

            // 키 직렬화: String
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,

            // 값 직렬화: Protobuf (Schema Registry 연동)
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to KafkaProtobufSerializer::class.java,

            // Schema Registry URL
            "schema.registry.url" to schemaRegistryUrl,

            // acks=all: 모든 ISR이 받아야 성공
            ProducerConfig.ACKS_CONFIG to "all",

            // 멱등성 활성화: 중복 메시지 방지
            ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG to true,

            // 재시도 횟수
            ProducerConfig.RETRIES_CONFIG to 3,

            // 배치 크기 (바이트) - 이 정도 모이면 한번에 전송
            ProducerConfig.BATCH_SIZE_CONFIG to 16384,

            // 배치 대기 시간 (ms) - 이 시간 동안 모아서 전송
            ProducerConfig.LINGER_MS_CONFIG to 5,

            // 전송 타임아웃 (2분)
            ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG to 120000
        )
    }

    /**
     * ProducerFactory: KafkaProducer 인스턴스 생성 담당
     * - Protobuf 메시지 타입 사용
     */
    @Bean
    fun producerFactory(): ProducerFactory<String, OrderCreatedEvent> {
        return DefaultKafkaProducerFactory(producerConfigs())
    }

    /**
     * KafkaTemplate: 실제로 메시지 전송할 때 사용
     *
     * 사용 예:
     * kafkaTemplate.send("topic-name", key, protobufEvent)
     */
    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, OrderCreatedEvent> {
        return KafkaTemplate(producerFactory())
    }
}