package com.practice.consumer.config

import com.practice.common.event.OrderCreatedEvent
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
import org.springframework.kafka.support.serializer.JsonDeserializer

/**
 * Kafka Consumer 설정
 *
 * @EnableKafka: @KafkaListener 어노테이션 활성화
 */
@EnableKafka
@Configuration
class KafkaConsumerConfig {

    @Value("\${spring.kafka.bootstrap-servers}")
    private lateinit var bootstrapServers: String

    @Value("\${spring.kafka.consumer.group-id}")
    private lateinit var groupId: String

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

            // 값 역직렬화
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to JsonDeserializer::class.java,

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
     */
    @Bean
    fun consumerFactory(): ConsumerFactory<String, OrderCreatedEvent> {
        // JsonDeserializer 설정
        val jsonDeserializer = JsonDeserializer(OrderCreatedEvent::class.java).apply {
            // 신뢰할 패키지 설정
            addTrustedPackages("com.practice.*")
            // 타입 헤더 무시 (Producer가 다른 언어일 수도 있으니)
            setUseTypeHeaders(false)
        }

        return DefaultKafkaConsumerFactory(
            consumerConfigs(),
            StringDeserializer(),
            jsonDeserializer
        )
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
