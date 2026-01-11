package com.practice.producer.config

import com.practice.common.event.OrderCreatedEvent
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.serializer.JsonSerializer

/**
 * Kafka Producer 설정
 *
 * application.yml로도 기본 설정 가능하지만,
 * 코드로 설정하면:
 * 1. 타입 안전성 (컴파일 타임 체크)
 * 2. 조건부 설정 가능
 * 3. 여러 Producer 설정 분리 가능
 */
@Configuration
class KafkaProducerConfig {

    @Value("\${spring.kafka.bootstrap-servers}")
    private lateinit var bootstrapServers: String

    /**
     * Producer 설정 맵
     */
    private fun producerConfigs(): Map<String, Any> {
        return mapOf(
            // 브로커 주소
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,

            // 키 직렬화: String
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,

            // 값 직렬화: JSON
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to JsonSerializer::class.java,

            // acks=all: 모든 ISR이 받아야 성공
            ProducerConfig.ACKS_CONFIG to "all",

            // 멱등성 활성화: 중복 메시지 방지
            ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG to true,

            // 재시도 횟수
            ProducerConfig.RETRIES_CONFIG to 3,

            // 배치 크기 (바이트) - 이 정도 모이면 한번에 전송
            ProducerConfig.BATCH_SIZE_CONFIG to 16384,

            // 배치 대기 시간 (ms) - 이 시간 동안 모아서 전송
            ProducerConfig.LINGER_MS_CONFIG to 5
        )
    }

    /**
     * ProducerFactory: KafkaProducer 인스턴스 생성 담당
     */
    @Bean
    fun producerFactory(): ProducerFactory<String, OrderCreatedEvent> {
        return DefaultKafkaProducerFactory(producerConfigs())
    }

    /**
     * KafkaTemplate: 실제로 메시지 전송할 때 사용
     *
     * 사용 예:
     * kafkaTemplate.send("topic-name", key, event)
     */
    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, OrderCreatedEvent> {
        return KafkaTemplate(producerFactory())
    }
}
