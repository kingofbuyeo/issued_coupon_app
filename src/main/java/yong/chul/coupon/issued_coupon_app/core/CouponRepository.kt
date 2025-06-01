package yong.chul.coupon.issued_coupon_app.core

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CouponRepository: JpaRepository<Coupon, Long> {
    fun findByCouponGroupId(couponGroupId: String): List<Coupon>
    fun findByCouponGroupIdAndRemindCountGreaterThan(couponGroupId: String, remindCount: Int): List<Coupon>
    fun findByCouponId(couponId: String): Coupon?
}