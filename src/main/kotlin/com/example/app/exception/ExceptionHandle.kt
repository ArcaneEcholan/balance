package com.example.app.exception

import com.example.app.utils.ClassUtil
import com.fasterxml.jackson.databind.ObjectMapper
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.BindException
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.multipart.MaxUploadSizeExceededException
import org.springframework.web.multipart.support.MissingServletRequestPartException
import javax.servlet.http.HttpServletResponse
import javax.validation.ConstraintViolation
import javax.validation.ConstraintViolationException

enum class ErrorCode {
    UNKNOWN,
    SUCCESS,
    INVALID_PARAMETER,
    UPLOAD_FILE_SIZE_EXCEED_UPPER_LIMIT,
    SERVER_ERROR,
    KEY_NOT_EXISTS
}


class ApiException : RuntimeException {
    var httpStatus: HttpStatus

    constructor(httpStatus: HttpStatus, msg: String?) : super(msg) {
        this.httpStatus = httpStatus
    }

    constructor(httpStatus: HttpStatus, cause: Throwable) : super(cause.message, cause) {
        this.httpStatus = httpStatus
    }

    constructor(httpStatus: HttpStatus, message: String?, cause: Throwable?) : super(message, cause) {
        this.httpStatus = httpStatus
    }
}

/**
 * 全局异常处理类
 */
@ControllerAdvice
class GlobalExceptionHandler {

    private val log = KotlinLogging.logger {}


    @Autowired
    lateinit var objectMapper: ObjectMapper

    @ExceptionHandler(
        BindException::class,
        MethodArgumentNotValidException::class
    )
    fun paramValidateException(ex: Exception?): ResponseEntity<*> {
        return try {
            val bindingResult: BindingResult = ClassUtil.getFieldValue(ex!!, "bindingResult", BindingResult::class.java)
            var a = getResponseBody(bindingResult)
            ResponseEntity(a, HttpStatus.BAD_REQUEST)
        } catch (e: NoSuchFieldException) {
            ResponseEntity(Any(), HttpStatus.BAD_REQUEST)
        }
    }

    fun getResponseBody(bindingResult: BindingResult): Any? {
        var a = mutableListOf<Map<String, Any?>>()
        for (objectError in bindingResult.allErrors) {
            val fieldError = objectError as FieldError
            a.add(mapOf("field" to fieldError.field, "message" to fieldError.defaultMessage))
        }
        return a;
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun constraintViolationException(ex: ConstraintViolationException): ResponseEntity<*> {
        val constraintViolations = ex.constraintViolations
        var a = getResponseBoddddy(constraintViolations)
        return ResponseEntity<Any?>(a, HttpStatus.BAD_REQUEST)
    }

    fun getResponseBoddddy(constraintViolations: MutableSet<ConstraintViolation<*>>): Any? {
        var a = mutableListOf<Map<String, Any?>>()
        for (constraintViolation in constraintViolations) {
            a.add(mapOf("field" to constraintViolation.propertyPath, "message" to constraintViolation.message))
        }
        return a;
    }


    @ExceptionHandler(Exception::class)
    fun otherException(req: HttpServletResponse, ex: Exception): ResponseEntity<*> {
        if (ex is ApiException) {
            return logExAndAssembleResponse(ex)
        }
        if (ex is HttpMessageNotReadableException) {
            return logExAndAssembleResponse(
                ApiException(HttpStatus.BAD_REQUEST, ex)
            )
        }
        if (ex is MissingServletRequestParameterException) {
            return logExAndAssembleResponse(
                ApiException(
                    HttpStatus.BAD_REQUEST, ex
                )
            )
        }
        if (ex is MaxUploadSizeExceededException) {
            return logExAndAssembleResponse(
                ApiException(
                    HttpStatus.PAYLOAD_TOO_LARGE, "max file size: 100MB", ex,
                )
            )
        }
        if (ex is MissingServletRequestPartException) {
            return logExAndAssembleResponse(ApiException(HttpStatus.BAD_REQUEST, ex.message))
        }

        return logExAndAssembleResponse(ApiException(HttpStatus.INTERNAL_SERVER_ERROR, ex.message))
    } // endregion

    fun logExAndAssembleResponse(ex: ApiException): ResponseEntity<*> {
        log(ex)

        return ResponseEntity(objectMapper.writeValueAsString(object {
            var message = ex.message
        }), ex.httpStatus)
    }

    fun log(ex: ApiException) {
        var r = getRootCause(ex)
        var exMsgBuilder = StringBuilder("restful server exception caught, ")
        exMsgBuilder.append("cause: [").append(ex.javaClass.typeName).append("] - ")
            .append(ex.message)
        if (r != null) {
            exMsgBuilder.append("cause: [").append(r.javaClass.typeName).append("] - ")
                .append(r.message)
        }
        log.error { exMsgBuilder }
    }

    fun getRootCause(throwable: Throwable): Throwable? {
        var rootCause: Throwable? = throwable.cause
        while (rootCause != null) {
            rootCause = rootCause.cause
        }
        return rootCause
    }

}


