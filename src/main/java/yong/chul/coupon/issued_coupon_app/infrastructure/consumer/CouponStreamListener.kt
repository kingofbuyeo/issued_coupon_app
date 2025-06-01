package yong.chul.coupon.issued_coupon_app.infrastructure.consumer

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import yong.chul.coupon.issued_coupon_app.config.CouponStreamNames.COUPON_GROUP
import yong.chul.coupon.issued_coupon_app.config.CouponStreamNames.ISSUED_COUPON_TOPIC
import yong.chul.coupon.issued_coupon_app.core.CouponGroupRepository
import yong.chul.coupon.issued_coupon_app.core.CouponRepository
import yong.chul.coupon.issued_coupon_app.core.IssuedCoupon
import yong.chul.coupon.issued_coupon_app.core.IssuedCouponRepository
import yong.chul.coupon.issued_coupon_app.core.service.FindCouponService
import yong.chul.coupon.issued_coupon_app.infrastructure.producer.message.IssuedCouponMessage
import yong.chul.coupon.issued_coupon_app.infrastructure.redis.RedisService
import yong.chul.coupon.issued_coupon_app.usecase.exception.CouponGroupNotFoundException
import yong.chul.coupon.issued_coupon_app.usecase.exception.CouponNotFoundException
import yong.chul.coupon.issued_coupon_app.usecase.exception.OverIssuedCouponException
import java.time.Instant
import java.time.ZoneId

@Component
class CouponStreamListener(
    private val issuedCouponRepository: IssuedCouponRepository,
    private val couponGroupRepository: CouponGroupRepository,
    private val couponRepository: CouponRepository,
    private val findCouponService: FindCouponService,
    private val redisService: RedisService
){
    private val log = LoggerFactory.getLogger(this::class.java)
    @KafkaListener(topics = [ISSUED_COUPON_TOPIC], groupId = COUPON_GROUP, containerFactory = "kafkaListenerContainerFactory", concurrency = "5")
    @Transactional
    @Retryable(backoff = Backoff(delay = 300), maxAttempts = 2, value = [ObjectOptimisticLockingFailureException::class])
    fun consume(payload: IssuedCouponMessage, ack: Acknowledgment) {
        log.info("Consuming message: $payload")
        try {
            val couponGroup = couponGroupRepository.findByCouponGroupId(payload.couponGroupId)
            if(couponGroup == null) {
                log.error("coupon group coupon not found, payload: $payload")
                throw CouponGroupNotFoundException()
            }
            val coupon = findCouponService.findCoupon(payload.couponGroupId, payload.issuedType)
            if(coupon == null) {
                log.error("coupon coupon not found, payload: $payload")
                throw CouponNotFoundException()
            }
            val result = redisService.issueCoupon(payload.userId, payload.couponGroupId, coupon.couponId)
            if (!result) {
                throw OverIssuedCouponException()
            }
            redisService.saveIssuedCoupon(payload.userId, couponGroup)
//            val coupon = couponRepository.findByCouponId(payload.couponId)
            val record = IssuedCoupon(
                couponGroupId = payload.couponGroupId,
                couponId = coupon.couponId,
                userId = payload.userId,
                issuedAt = Instant.ofEpochMilli(payload.timestamp).atZone(ZoneId.systemDefault()).toLocalDateTime()
            )
            coupon.decrease()
            issuedCouponRepository.save(record)
        }catch (e: RuntimeException){
            log.error("Error while consuming message: ${e.localizedMessage}, payload : $payload")
        }
        ack.acknowledge()

    }

//    private val log = LoggerFactory.getLogger(this::class.java)
//
//    @PostConstruct
//    fun register() {
//        val ops = reactiveRedisTemplate.opsForStream<String, String>()
//        ops.createGroup(STREAM_KEY, ReadOffset.latest(), GROUP)
//            .onErrorResume { Mono.empty() }
//            .subscribe()
//
//        val offset = StreamOffset.create(STREAM_KEY, ReadOffset.lastConsumed())
//        val consumer = Consumer.from(GROUP, CONSUMER)
//
//        container.receive(consumer, offset, this)
//        container.start()
//    }
//    private fun issueCouponWithLua(stockKey: String, userKey: String): Mono<Int> {
//        val script = """
//            local stock = tonumber(redis.call('GET', KEYS[1]))
//            if not stock or stock <= 0 then return 0 end
//            if redis.call('SETNX', KEYS[2], 'issued') == 1 then
//                redis.call('DECR', KEYS[1])
//                return 1
//            else
//                return 2
//            end
//        """.trimIndent()
////redis.call('DECR', KEYS[1])
//        val redisScript = DefaultRedisScript<Long>()
//        redisScript.setScriptText(script)
//        redisScript.resultType = Long::class.java
//        return reactiveRedisTemplate.execute (
//            redisScript,
//            listOf(stockKey, userKey)
//        ).next().map { it?.toInt() ?: 0 }
//    }
//
//    private fun logSuccess(userId: String, couponId: String) {
//        println("✅ 발급 성공 - userId=$userId, couponId=$couponId")
//    }
//
//    private fun logFailure(userId: String, couponId: String, reason: String) {
//        println("❌ 발급 실패 - userId=$userId, couponId=$couponId, 이유=$reason")
//    }
//
//    override fun onMessage(message: MapRecord<String, String, String>?) {
//        if(message == null) return
//        val userId = message.value["userId"] ?: return
//        val couponId = message.value["couponId"] ?: return
//        val stockKey = "coupon:$couponId:stock"
//        val userKey = "coupon:$couponId:user:$userId"
//
//        issueCouponWithLua(stockKey, userKey).subscribe {
//            when (it) {
//                1 -> {
//                    issuedCouponRepository.save(IssuedCoupon(couponId, userId))
//                    logSuccess(userId, couponId)
//                }
//                0 -> logFailure(userId, couponId, "재고 부족")
//                2 -> logFailure(userId, couponId, "중복 발급")
//            }
//        }
//        reactiveRedisTemplate.opsForStream<String, String>()
//            .acknowledge(GROUP, message)
//            .subscribe()
//    }
}