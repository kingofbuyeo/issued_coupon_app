package yong.chul.coupon.issued_coupon_app.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.connection.stream.MapRecord
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.*
import org.springframework.data.redis.stream.StreamMessageListenerContainer
import org.springframework.data.redis.stream.StreamReceiver
import yong.chul.coupon.issued_coupon_app.core.CouponGroup
import java.time.Duration

@Configuration
class RedisConfig {
    @Bean
    fun redisConnectionFactory(): LettuceConnectionFactory = LettuceConnectionFactory()

    @Bean
    fun reactiveRedisTemplate(factory: ReactiveRedisConnectionFactory): ReactiveRedisTemplate<String, String> {
        val serializationContext = RedisSerializationContext.newSerializationContext<String, String>(
            StringRedisSerializer()
        )
            .key(RedisSerializationContext.SerializationPair.fromSerializer(StringRedisSerializer()))
            .value(RedisSerializationContext.SerializationPair.fromSerializer(StringRedisSerializer()))
            .hashKey(RedisSerializationContext.SerializationPair.fromSerializer(StringRedisSerializer()))
            .hashValue(RedisSerializationContext.SerializationPair.fromSerializer(StringRedisSerializer()))
            .build()
        return ReactiveRedisTemplate(factory, serializationContext)
    }

    @Bean
    fun objectRedisTemplate(factory: ReactiveRedisConnectionFactory): ReactiveRedisTemplate<String, CouponGroup> {
        val keySerializer: RedisSerializer<String> = StringRedisSerializer()
        val valueSerializer = Jackson2JsonRedisSerializer(CouponGroup::class.java).apply {
            objectMapper()
        }

        val serializationContext = RedisSerializationContext
            .newSerializationContext<String, CouponGroup>(keySerializer)
            .value(valueSerializer)
            .build()

        return ReactiveRedisTemplate(factory, serializationContext)
    }

    @Bean
    fun couponRedisTemplate(factory: RedisConnectionFactory): RedisTemplate<String, CouponGroup> {
        val template = RedisTemplate<String, CouponGroup>()
        val serializer = Jackson2JsonRedisSerializer(objectMapper(), CouponGroup::class.java)
        val objectMapper = ObjectMapper().registerModule(kotlinModule())
//        template.keySerializer = template.stringSerializer
//        template.valueSerializer = serializer
//        template.afterPropertiesSet()
        template.connectionFactory = factory
        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = serializer
        template.afterPropertiesSet()
        return template
    }

    private fun objectMapper(): ObjectMapper = jacksonObjectMapper()
        .registerModule(JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS) // LocalDateTime 지원
        .registerModule(kotlinModule())
        .findAndRegisterModules()


    @Bean
    fun redisTemplate(factory: RedisConnectionFactory): RedisTemplate<String, String> {
        val template = RedisTemplate<String, String>()
        template.connectionFactory = factory
        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = StringRedisSerializer()
        template.afterPropertiesSet()
        return template
    }
}