package yong.chul.coupon.issued_coupon_app.usecase.command

data class RequestCouponCommand(
    val userId: String,
    val couponGroupId: String,
    val issuedType: IssuedType
)
