package yong.chul.coupon.issued_coupon_app.infrastructure

import org.springframework.web.bind.annotation.*
import yong.chul.coupon.issued_coupon_app.infrastructure.dto.IssuedCouponRequest
import yong.chul.coupon.issued_coupon_app.infrastructure.dto.RequestCouponRequest
import yong.chul.coupon.issued_coupon_app.usecase.IssuedCouponFromAdmin
import yong.chul.coupon.issued_coupon_app.usecase.RequestCouponForUser
import yong.chul.coupon.issued_coupon_app.usecase.presenter.IssuedCouponPresenter


@RestController
@RequestMapping("/api/v1/coupon")
class CouponController(
    private val issuedCouponFromAdmin: IssuedCouponFromAdmin,
    private val requestCouponForUser: RequestCouponForUser,
) {

    @PostMapping("request/{couponGroupId}")
    suspend fun requestCoupon(
        @PathVariable("couponGroupId") couponGroupId: String,
        @RequestBody request: RequestCouponRequest
    ) {
        requestCouponForUser.requestCoupon(request.toCommand(couponGroupId))
    }

    @PostMapping("issuedCoupon")
    suspend fun issuedCoupon(
        @RequestBody req: IssuedCouponRequest
    ): IssuedCouponPresenter {
        return issuedCouponFromAdmin.registerCoupon(req.toCommand())
    }
}