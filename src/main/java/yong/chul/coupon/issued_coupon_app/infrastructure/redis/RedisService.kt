package yong.chul.coupon.issued_coupon_app.infrastructure.redis

import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.script.DefaultRedisScript
import org.springframework.stereotype.Service
import yong.chul.coupon.issued_coupon_app.core.CouponGroup

@Service
class RedisService(
    private val reactiveRedisTemplate: ReactiveRedisTemplate<String, String>,
    private val objectRedisTemplate: ReactiveRedisTemplate<String, CouponGroup>,
    private val couponRedisTemplate: RedisTemplate<String, CouponGroup>,
    private val redisTemplate: RedisTemplate<String, String>
) {

    suspend fun hasIssued(userId: String, couponGroupId: String): Boolean {
        return reactiveRedisTemplate.hasKey("coupon:${couponGroupId}:user:${userId}").awaitSingleOrNull() ?: false
    }

    fun saveIssuedCoupon(userId: String, couponGroup: CouponGroup) {
        redisTemplate.opsForValue().set("coupon:${couponGroup.couponGroupId}:user:${userId}", "1", couponGroup.setExpired())
    }

    suspend fun findCouponGroup(couponGroupId: String): CouponGroup? {
        return couponRedisTemplate
            .opsForValue()
            .get("couponGroup:${couponGroupId}")
    }

    fun issueCoupon(userId: String, couponGroupId: String, couponId: String): Boolean {
        val script = DefaultRedisScript<Boolean>().apply {
            setScriptText("""
                if redis.call('exists', KEYS[1]) == 1 then
                    return false
                end
                local stock = tonumber(redis.call('GET', KEYS[2]))
                if not stock or stock <= 0 then return false end
                local couponStock = tonumber(redis.call('GET', KEYS[3]))
                if not couponStock or couponStock <= 0 then return false end
                redis.call('set', KEYS[1], '1')
                redis.call('decr', KEYS[2])
                redis.call('decr', KEYS[3])
                return true
            """.trimIndent())
            resultType = Boolean::class.java
        }

        val keys = listOf("coupon:${couponGroupId}:user:${userId}", "coupon:${couponGroupId}:stock", "coupon:${couponGroupId}:${couponId}:stock")
        return reactiveRedisTemplate.execute(script, keys).blockFirst() ?: false
    }
}