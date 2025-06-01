package yong.chul.coupon.issued_coupon_app.usecase.command

import yong.chul.coupon.issued_coupon_app.core.Coupon
import yong.chul.coupon.issued_coupon_app.core.CouponGroup
import yong.chul.coupon.issued_coupon_app.core.generateId
import java.time.LocalDateTime

data class IssuedCouponCommand(
    val couponGroupName: String,
    val issuedCount: Int,
    val coupons: List<CouponInfo>,
    val issuedAvailableTime: LocalDateTime,
    val expiredAt: LocalDateTime
){
    fun toDomain(groupId: String): List<Coupon> {
        val sorted = coupons.sortedByDescending { it.amount }
        val domains = sorted.take(coupons.size - 1).map {
            Coupon(generateId(), groupId, it.couponName, it.amount, couponCount(it.amount))
        }
        val totalCount = domains.map { it.issuedCount }.reduce { acc, i -> acc + i }
        val min = sorted.last()
        val lastCoupon = Coupon(generateId(), groupId, min.couponName, min.amount, issuedCount - totalCount)
        return ratioReverse(domains + lastCoupon)
    }

    private fun ratioReverse(sorted: List<Coupon>): List<Coupon> {
        val reversed = sorted.reversed()
        return sorted.mapIndexed { idx, it ->
            it.countUpdate(reversed[idx])
        }
    }

    private fun totalAmount(): Double {
        return coupons.map { it.amount }.reduce { acc, t -> acc + t }.toDouble()
    }

    private fun amountRatio(amount: Int): Double {
        return amount / totalAmount()
    }

    fun couponCount(amount: Int): Int {
        return issuedCount.times(amountRatio(amount)).toInt()
    }

    fun toGroup(): CouponGroup {
        return CouponGroup(generateId(), couponGroupName, issuedCount, issuedAvailableTime, expiredAt)
    }
}


data class CouponInfo(
    val couponName: String,
    val amount: Int
)