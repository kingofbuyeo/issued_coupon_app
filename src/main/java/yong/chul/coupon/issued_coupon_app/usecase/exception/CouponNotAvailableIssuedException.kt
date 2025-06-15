package yong.chul.coupon.issued_coupon_app.usecase.exception

class CouponNotAvailableIssuedException : RuntimeException("coupon issue not available")
class CouponNotFoundException : RuntimeException("coupon not found")
class CouponGroupNotFoundException : RuntimeException("coupon group not found")
class UserCouponNotFoundException : RuntimeException("user coupon not found")
class UserCouponAlreadyUsedException : RuntimeException("user coupon already used")
class CouponExpiredException : RuntimeException("coupon expired")