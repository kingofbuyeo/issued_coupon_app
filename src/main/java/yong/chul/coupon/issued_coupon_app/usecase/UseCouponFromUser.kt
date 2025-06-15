package yong.chul.coupon.issued_coupon_app.usecase

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import yong.chul.coupon.issued_coupon_app.core.CouponGroupRepository
import yong.chul.coupon.issued_coupon_app.core.IssuedCouponRepository
import yong.chul.coupon.issued_coupon_app.usecase.command.UseCouponCommand
import yong.chul.coupon.issued_coupon_app.usecase.exception.CouponExpiredException
import yong.chul.coupon.issued_coupon_app.usecase.exception.CouponGroupNotFoundException
import yong.chul.coupon.issued_coupon_app.usecase.exception.UserCouponAlreadyUsedException
import yong.chul.coupon.issued_coupon_app.usecase.exception.UserCouponNotFoundException

@Service
class UseCouponFromUser(
    private val issuedCouponRepository: IssuedCouponRepository,
    private val couponGroupRepository: CouponGroupRepository
) {

    @Transactional
    fun useCoupon(command: UseCouponCommand) {
        val userCoupon = issuedCouponRepository.findByIssuedCouponIdAndUserId(command.issuedCouponId, command.userId) ?: throw UserCouponNotFoundException()
        val couponGroup = couponGroupRepository.findByCouponGroupId(userCoupon.couponGroupId) ?: throw CouponGroupNotFoundException()

        if(userCoupon.isUsed()) throw UserCouponAlreadyUsedException()
        if(!couponGroup.canUseCoupon()) throw CouponExpiredException()

        userCoupon.couponUsed()
    }
}