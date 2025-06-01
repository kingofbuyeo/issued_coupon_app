package yong.chul.coupon.issued_coupon_app.core

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CouponGroupRepository: JpaRepository<CouponGroup, Long> {
    fun findByCouponGroupId(couponGroupId: String): CouponGroup?
}