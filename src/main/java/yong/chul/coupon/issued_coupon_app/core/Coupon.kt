package yong.chul.coupon.issued_coupon_app.core

import jakarta.persistence.*

@Table(name = "coupons")
@Entity
data class Coupon(
    val couponId: String,
    val couponGroupId: String,
    val couponName: String,
    val amount: Int,
    val issuedCount: Int,
    var remindCount: Int = issuedCount
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @Version
    var version: Long = 0

    fun decrease() {
        this.remindCount--
    }

    fun countUpdate(coupon: Coupon): Coupon {
        return Coupon(
            this.couponId,
            this.couponGroupId,
            this.couponName,
            this.amount,
            coupon.issuedCount
        )
    }
}