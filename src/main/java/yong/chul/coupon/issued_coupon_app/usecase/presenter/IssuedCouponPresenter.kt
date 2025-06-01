package yong.chul.coupon.issued_coupon_app.usecase.presenter

import yong.chul.coupon.issued_coupon_app.core.CouponGroup

data class IssuedCouponPresenter(
    val couponGroupId: String,
    val couponGroupName: String
) {
    constructor(couponGroup: CouponGroup) : this(
        couponGroupId = couponGroup.couponGroupId,
        couponGroupName = couponGroup.couponGroupName
    )
}
