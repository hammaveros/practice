package com.practice.producer.config

import com.practice.common.KafkaTopics
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder

/**
 * Kafka 토픽 자동 생성 설정
 *
 * 애플리케이션 시작 시 토픽이 없으면 자동 생성
 * - 파티션 수: 3 (병렬 처리용)
 * - 복제 수: 3 (고가용성)
 */
@Configuration
class KafkaTopicConfig {

    /**
     * 주문 생성 이벤트 토픽
     */
    @Bean
    fun orderCreatedTopic(): NewTopic {
        return TopicBuilder.name(KafkaTopics.ORDER_CREATED)
            .partitions(3)      // 파티션 수
            .replicas(3)        // 복제 수 (브로커 수 이하로 설정)
            .build()
    }

    /**
     * 주문 완료 이벤트 토픽
     */
    @Bean
    fun orderCompletedTopic(): NewTopic {
        return TopicBuilder.name(KafkaTopics.ORDER_COMPLETED)
            .partitions(3)
            .replicas(3)
            .build()
    }

    /**
     * 테스트 토픽
     */
    @Bean
    fun testTopic(): NewTopic {
        return TopicBuilder.name(KafkaTopics.TEST_TOPIC)
            .partitions(3)
            .replicas(3)
            .build()
    }
}