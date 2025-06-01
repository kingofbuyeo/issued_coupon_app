package yong.chul.coupon.issued_coupon_app.infrastructure

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.validation.BindingResult
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import yong.chul.coupon.issued_coupon_app.usecase.exception.CouponNotAvailableIssuedException
import yong.chul.coupon.issued_coupon_app.usecase.exception.DuplicateCouponException
import yong.chul.coupon.issued_coupon_app.usecase.exception.OverIssuedCouponException

@RestControllerAdvice(basePackageClasses = [ApiExceptionController::class])
class ApiExceptionController {
    private val logger = LoggerFactory.getLogger(javaClass)

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = [IllegalArgumentException::class])
    fun handleConnectionNotFoundException(e: IllegalArgumentException): ErrorRes {
        return ErrorRes(4000000, e.message)
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = [MethodArgumentNotValidException::class])
    fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): ErrorRes {
        val bindingResult: BindingResult = e.bindingResult

        val builder = StringBuilder()
        for (fieldError in bindingResult.fieldErrors) {
            builder.append("[")
            builder.append(fieldError.field)
            builder.append("](은)는 ")
            builder.append(fieldError.defaultMessage)
            builder.append(", 입력된 값: [")
            builder.append(fieldError.rejectedValue)
            builder.append("].")
        }

        return ErrorRes(400000, builder.toString())
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = [RuntimeException::class])
    fun internalServerExceptionHandler(e: RuntimeException): ErrorRes {
        logger.error("Internal Server Error", e)
        return ErrorRes(500000, "장애가 발생하였습니다. 관리자에게 문의해 주세요.")
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(value = [OverIssuedCouponException::class])
    fun overIssuedCouponException(e: OverIssuedCouponException): ErrorRes {
        return ErrorRes(406010, e.message)
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(value = [DuplicateCouponException::class])
    fun duplicateCouponException(e: DuplicateCouponException): ErrorRes {
        return ErrorRes(406011, e.message)
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(value = [CouponNotAvailableIssuedException::class])
    fun couponNotAvailableExceptionHandler(e: CouponNotAvailableIssuedException): ErrorRes {
        return ErrorRes(406021, e.message)
    }
}