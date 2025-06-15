package yong.chul.coupon.issued_coupon_app.usecase.command

data class UseCouponCommand(
    val userId: String,
    val issuedCouponId: String
)
