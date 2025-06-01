package yong.chul.coupon.issued_coupon_app.usecase.exception

class CouponNotAvailableIssuedException : RuntimeException("coupon issue not available")
class CouponNotFoundException : RuntimeException("coupon not found")
class CouponGroupNotFoundException : RuntimeException("coupon group not found")