package yong.chul.coupon.issued_coupon_app.usecase

import kotlinx.coroutines.coroutineScope
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import yong.chul.coupon.issued_coupon_app.core.service.FindCouponService
import yong.chul.coupon.issued_coupon_app.infrastructure.producer.CouponProducer
import yong.chul.coupon.issued_coupon_app.infrastructure.redis.RedisService
import yong.chul.coupon.issued_coupon_app.usecase.command.RequestCouponCommand
import yong.chul.coupon.issued_coupon_app.usecase.exception.CouponNotAvailableIssuedException
import yong.chul.coupon.issued_coupon_app.usecase.exception.CouponNotFoundException
import yong.chul.coupon.issued_coupon_app.usecase.exception.DuplicateCouponException
import yong.chul.coupon.issued_coupon_app.usecase.exception.OverIssuedCouponException


@Service
class RequestCouponForUser(
    private val couponProducer: CouponProducer,
    private val redisService: RedisService,
    private val findCouponService: FindCouponService
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    suspend fun requestCoupon(command: RequestCouponCommand) = coroutineScope {
        log.info("REQUEST ISSUED COUPON USERID: ${command.userId}")
        val group = redisService.findCouponGroup(command.couponGroupId) ?: throw CouponNotFoundException()
        if (!group.isAvailableToIssued()) {
            throw CouponNotAvailableIssuedException()
        }
        val hasCoupon = redisService.hasIssued(command.userId, command.couponGroupId)
        if (hasCoupon) {
            throw DuplicateCouponException()
        }
//        val coupon = findCouponService.findCoupon(command.couponGroupId, command.issuedType)
//        val result = redisService.issueCoupon(command.userId, command.couponGroupId, coupon.couponId)
//        if (!result) throw OverIssuedCouponException()
//        redisService.saveIssuedCoupon(command.userId, group)
        couponProducer.sendIssuedCoupon(command)
    }

}