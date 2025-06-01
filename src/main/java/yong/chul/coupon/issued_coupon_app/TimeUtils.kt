package yong.chul.coupon.issued_coupon_app

import java.time.LocalDateTime
import java.time.OffsetDateTime

fun String.parseToLocalDateTime(): LocalDateTime {
    val offsetDateTime = OffsetDateTime.parse(this)
    return offsetDateTime.toLocalDateTime()
}