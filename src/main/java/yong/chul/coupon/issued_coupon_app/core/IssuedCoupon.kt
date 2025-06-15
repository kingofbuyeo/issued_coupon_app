package yong.chul.coupon.issued_coupon_app.core

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "issued_coupons", uniqueConstraints = [UniqueConstraint(columnNames = ["couponGroupId", "couponId", "userId"])])
data class IssuedCoupon(
    val issuedCouponId: String,
    val couponGroupId: String,
    val couponId: String,
    val userId: String,
    val issuedAt: LocalDateTime,
    var useAvailable: Boolean = true,
){
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id : Long? = null

    fun isUsed(): Boolean {
        return !this.useAvailable
    }

    fun couponUsed() {
        this.useAvailable = false
    }
}
