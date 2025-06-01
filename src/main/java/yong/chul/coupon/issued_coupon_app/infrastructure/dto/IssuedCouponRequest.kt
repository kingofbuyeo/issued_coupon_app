package yong.chul.coupon.issued_coupon_app.infrastructure.dto

import yong.chul.coupon.issued_coupon_app.parseToLocalDateTime
import yong.chul.coupon.issued_coupon_app.usecase.command.CouponInfo
import yong.chul.coupon.issued_coupon_app.usecase.command.IssuedCouponCommand

data class IssuedCouponRequest(
    val couponGroupName: String,
    val issuedCount: Int,
    val issuedAvailableTime: String,
    val expiredAt: String,
    val couponGroup: List<CouponTypeInfoRequest>
) {
    fun toCommand(): IssuedCouponCommand {
        return IssuedCouponCommand(this.couponGroupName, this.issuedCount, toCouponDomains(), issuedAvailableTime.parseToLocalDateTime(), expiredAt.parseToLocalDateTime())
    }

    fun toCouponDomains(): List<CouponInfo> {
        return couponGroup.map {
            CouponInfo(
                it.couponName,
                it.amount
            )
        }
    }
}


data class CouponTypeInfoRequest(
    val couponName: String,
    val amount: Int
)