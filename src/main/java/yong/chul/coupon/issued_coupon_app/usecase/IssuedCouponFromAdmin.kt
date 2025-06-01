package yong.chul.coupon.issued_coupon_app.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import yong.chul.coupon.issued_coupon_app.core.CouponGroup
import yong.chul.coupon.issued_coupon_app.core.CouponGroupRepository
import yong.chul.coupon.issued_coupon_app.core.CouponRepository
import yong.chul.coupon.issued_coupon_app.usecase.command.IssuedCouponCommand
import yong.chul.coupon.issued_coupon_app.usecase.presenter.IssuedCouponPresenter

@Service
class IssuedCouponFromAdmin(
    private val redisTemplate: RedisTemplate<String, String>,
    private val couponRepository: CouponRepository,
    private val couponGroupRepository: CouponGroupRepository,
    private val couponRedisTemplate: RedisTemplate<String, CouponGroup>
) {
    private val log = LoggerFactory.getLogger(this::class.java)
    fun registerCoupon(command: IssuedCouponCommand): IssuedCouponPresenter {
        val couponGroup = couponGroupRepository.save(command.toGroup())
        val coupons = couponRepository.saveAll(command.toDomain(couponGroup.couponGroupId))
        val stockKey = "coupon:${couponGroup.couponGroupId}:stock"
        redisTemplate.opsForValue()
            .set(stockKey, couponGroup.issuedCount.toString())
        couponRedisTemplate.opsForValue().set("couponGroup:${couponGroup.couponGroupId}", couponGroup, couponGroup.setExpired())
        coupons.forEach { coupon ->
            redisTemplate.opsForValue().set("coupon:${coupon.couponGroupId}:${coupon.couponId}:stock", coupon.issuedCount.toString())
        }
        return IssuedCouponPresenter(couponGroup)
    }
}