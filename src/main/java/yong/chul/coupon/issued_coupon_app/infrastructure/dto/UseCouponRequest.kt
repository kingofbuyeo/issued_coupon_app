package yong.chul.coupon.issued_coupon_app.infrastructure.dto

import yong.chul.coupon.issued_coupon_app.usecase.command.UseCouponCommand

data class UseCouponRequest(
    val userId: String
) {
    fun toCommand(issuedCouponId: String): UseCouponCommand {
        return UseCouponCommand(userId = this.userId, issuedCouponId = issuedCouponId)
    }
}
