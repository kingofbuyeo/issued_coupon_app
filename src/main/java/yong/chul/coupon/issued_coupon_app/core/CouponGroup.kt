package yong.chul.coupon.issued_coupon_app.core

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.time.Duration
import java.time.LocalDateTime

@Entity
@Table(name = "coupon_group")
data class CouponGroup(
    val couponGroupId: String,
    val couponGroupName: String,
    val issuedCount: Int,
    val issuedAvailableTime: LocalDateTime,
    val expiredAt: LocalDateTime
){
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @JsonIgnore
    fun isAvailableToIssued(): Boolean {
        return issuedAvailableTime.isBefore(LocalDateTime.now())
    }

    @JsonIgnore
    fun setExpired(): Duration {
        return Duration.between(LocalDateTime.now(), expiredAt)
    }
}
