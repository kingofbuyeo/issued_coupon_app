package yong.chul.coupon.issued_coupon_app.config

import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.*
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerializer
import yong.chul.coupon.issued_coupon_app.config.CouponStreamNames.COUPON_GROUP
import yong.chul.coupon.issued_coupon_app.config.CouponStreamNames.ISSUED_COUPON_TOPIC
import yong.chul.coupon.issued_coupon_app.infrastructure.producer.message.IssuedCouponMessage

@Configuration
@EnableKafka
class KafkaConfig(
    @Value("\${spring.kafka.bootstrap-servers}")
    private val kafkaHost: String
) {
    @Bean
    fun topicExample(): NewTopic {
        return TopicBuilder.name(ISSUED_COUPON_TOPIC)
            .partitions(5) // 원하는 파티션 수
            .replicas(1)   // 복제본 수 (단일 브로커라면 1)
            .build()
    }

    @Bean
    fun producerFactory(): ProducerFactory<String, IssuedCouponMessage> {
        val props = mapOf(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaHost,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to JsonSerializer::class.java
        )
        return DefaultKafkaProducerFactory(props)
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, IssuedCouponMessage> {
        return KafkaTemplate(producerFactory())
    }

    @Bean
    fun consumerFactory(): ConsumerFactory<String, IssuedCouponMessage> {
        val deserializer = JsonDeserializer(IssuedCouponMessage::class.java).apply {
            addTrustedPackages("*")
        }

        val props = mapOf(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaHost,
            ConsumerConfig.GROUP_ID_CONFIG to COUPON_GROUP,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to JsonDeserializer::class.java
        )

        return DefaultKafkaConsumerFactory(props, StringDeserializer(), deserializer)
    }

    @Bean
    fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, IssuedCouponMessage> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, IssuedCouponMessage>()
        factory.consumerFactory = consumerFactory()
        factory.containerProperties.ackMode = ContainerProperties.AckMode.MANUAL
        return factory
    }

//    @Bean
//    fun producerFactory(): ProducerFactory<String, String> {
//        val config = mapOf(
//            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaHost,
//            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
//            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java
//        )
//        return DefaultKafkaProducerFactory(config)
//    }
//
//    @Bean
//    fun kafkaTemplate(): KafkaTemplate<String, String> = KafkaTemplate(producerFactory())
//
//    @Bean
//    fun consumerFactory(): ConsumerFactory<String, String> {
//        val config = mapOf(
//            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaHost,
//            ConsumerConfig.GROUP_ID_CONFIG to COUPON_GROUP,
//            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
//            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java
//        )
//        return DefaultKafkaConsumerFactory(config)
//    }
//
//    @Bean
//    fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, String> {
//        val factory = ConcurrentKafkaListenerContainerFactory<String, String>()
//        factory.consumerFactory = consumerFactory()
//        return factory
//    }
}
