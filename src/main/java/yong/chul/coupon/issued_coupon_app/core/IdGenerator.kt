package yong.chul.coupon.issued_coupon_app.core

import java.util.*

fun generateId(): String {
    return UUID.randomUUID().toString()
}