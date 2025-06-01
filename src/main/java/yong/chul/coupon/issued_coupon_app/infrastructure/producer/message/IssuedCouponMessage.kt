package yong.chul.coupon.issued_coupon_app.infrastructure.producer.message

import yong.chul.coupon.issued_coupon_app.usecase.command.IssuedType
import yong.chul.coupon.issued_coupon_app.usecase.command.RequestCouponCommand

data class IssuedCouponMessage(
    val couponGroupId: String,
    val userId: String,
    val issuedType: IssuedType,
    val timestamp: Long = System.currentTimeMillis()
){
    constructor(command: RequestCouponCommand): this(
        command.couponGroupId,
        command.userId,
        command.issuedType,
    )
}
