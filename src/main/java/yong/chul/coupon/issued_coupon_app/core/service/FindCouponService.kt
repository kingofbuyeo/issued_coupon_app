package yong.chul.coupon.issued_coupon_app.core.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import yong.chul.coupon.issued_coupon_app.core.Coupon
import yong.chul.coupon.issued_coupon_app.core.CouponRepository
import yong.chul.coupon.issued_coupon_app.usecase.command.IssuedType
import yong.chul.coupon.issued_coupon_app.usecase.exception.CouponNotFoundException

@Service
class FindCouponService(
    private val couponRepository: CouponRepository,
) {

    @Transactional(readOnly = true)
    fun findCoupon(couponGroupId: String, issuedType: IssuedType): Coupon? {
        val coupons = couponRepository.findByCouponGroupIdAndRemindCountGreaterThan(couponGroupId, 0)
        if(coupons.isEmpty()) return null
        return when(issuedType){
            IssuedType.RANDOM -> coupons.random()
            else -> coupons.maxByOrNull { it.amount } ?: throw CouponNotFoundException()
        }
    }
}