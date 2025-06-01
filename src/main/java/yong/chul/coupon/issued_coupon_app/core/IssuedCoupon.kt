package yong.chul.coupon.issued_coupon_app.core

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "issued_coupons", uniqueConstraints = [UniqueConstraint(columnNames = ["couponGroupId", "userId"])])
data class IssuedCoupon(
    val couponGroupId: String,
    val couponId: String,
    val userId: String,
    val issuedAt: LocalDateTime
){
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id : Long? = null
}
