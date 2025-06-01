package yong.chul.coupon.issued_coupon_app.infrastructure.producer

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import yong.chul.coupon.issued_coupon_app.config.CouponStreamNames.ISSUED_COUPON_TOPIC
import yong.chul.coupon.issued_coupon_app.infrastructure.producer.message.IssuedCouponMessage
import yong.chul.coupon.issued_coupon_app.usecase.command.RequestCouponCommand

@Component
class CouponProducer(
    private val kafkaTemplate: KafkaTemplate<String, IssuedCouponMessage>
) {
    fun sendIssuedCoupon(command: RequestCouponCommand) {
        val payload = IssuedCouponMessage(command)
        kafkaTemplate.send(ISSUED_COUPON_TOPIC, command.couponGroupId, payload)
    }
}