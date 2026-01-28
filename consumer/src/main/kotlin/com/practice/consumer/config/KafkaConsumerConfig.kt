package com.practice.consumer.config

import com.practice.common.event.OrderCreatedEventProto.OrderCreatedEvent
import io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializer
import io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializerConfig
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.ContainerProperties

/**
 * Kafka Consumer 설정 (Protobuf + Schema Registry)
 *
 * @EnableKafka: @KafkaListener 어노테이션 활성화
 *
 * Schema Registry를 통해:
 * 1. 스키마 ID로 메시지의 스키마 조회
 * 2. Protobuf 바이너리 → Java 객체 역직렬화
 * 3. 스키마 호환성 자동 검증
 */
@EnableKafka
@Configuration
class KafkaConsumerConfig {

    @Value("\${spring.kafka.bootstrap-servers}")
    private lateinit var bootstrapServers: String

    @Value("\${spring.kafka.consumer.group-id}")
    private lateinit var groupId: String

    @Value("\${spring.kafka.properties.schema.registry.url}")
    private lateinit var schemaRegistryUrl: String

    /**
     * Consumer 설정 맵
     */
    private fun consumerConfigs(): Map<String, Any> {
        return mapOf(
            // 브로커 주소
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,

            // Consumer Group ID
            ConsumerConfig.GROUP_ID_CONFIG to groupId,

            // 키 역직렬화
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,

            // 값 역직렬화: Protobuf (Schema Registry 연동)
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to KafkaProtobufDeserializer::class.java,

            // Schema Registry URL
            "schema.registry.url" to schemaRegistryUrl,

            // 역직렬화할 Protobuf 메시지 타입 지정
            KafkaProtobufDeserializerConfig.SPECIFIC_PROTOBUF_VALUE_TYPE to
                    "com.practice.common.event.OrderCreatedEventProto\$OrderCreatedEvent",

            // 새 Consumer Group이면 처음부터 읽기
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest",

            // 자동 커밋 비활성화 (수동 커밋)
            ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to false,

            // 한 번에 가져올 최대 레코드 수
            ConsumerConfig.MAX_POLL_RECORDS_CONFIG to 500,

            // 세션 타임아웃 (이 시간 안에 heartbeat 안 오면 리밸런싱)
            ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG to 30000,

            // Heartbeat 간격
            ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG to 10000
        )
    }

    /**
     * ConsumerFactory: KafkaConsumer 인스턴스 생성 담당
     * - Protobuf 메시지 타입 사용
     */
    @Bean
    fun consumerFactory(): ConsumerFactory<String, OrderCreatedEvent> {
        return DefaultKafkaConsumerFactory(consumerConfigs())
    }

    /**
     * KafkaListenerContainerFactory
     *
     * @KafkaListener가 사용하는 컨테이너 생성
     * Concurrent = 여러 스레드로 병렬 처리
     */
    @Bean
    fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, OrderCreatedEvent> {
        return ConcurrentKafkaListenerContainerFactory<String, OrderCreatedEvent>().apply {
            consumerFactory = consumerFactory()

            // 동시 처리 스레드 수 (파티션 수에 맞추면 좋음)
            setConcurrency(3)

            // 수동 커밋 모드
            containerProperties.ackMode = ContainerProperties.AckMode.MANUAL

            // 배치 리스너 사용 여부
            isBatchListener = false
        }
    }
}