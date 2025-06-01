package yong.chul.coupon.issued_coupon_app.infrastructure.dto

import yong.chul.coupon.issued_coupon_app.usecase.command.IssuedType
import yong.chul.coupon.issued_coupon_app.usecase.command.RequestCouponCommand

data class RequestCouponRequest(
    val userId: String,
    val issuedType: IssuedType = IssuedType.RANDOM
){
    fun toCommand(couponId: String): RequestCouponCommand {
        return RequestCouponCommand(userId, couponId, issuedType)
    }
}
