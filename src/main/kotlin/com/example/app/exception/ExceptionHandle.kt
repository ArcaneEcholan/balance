package com.example.app.exception

import RespCode
import com.example.app.utils.ClassUtils
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
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.multipart.MaxUploadSizeExceededException
import org.springframework.web.multipart.support.MissingServletRequestPartException
import javax.servlet.http.HttpServletResponse
import javax.validation.ConstraintViolationException
import javax.validation.Path

enum class ErrorCode {
    UNKNOWN,
    SUCCESS,
    INVALID_PARAMETER,
    UPLOAD_FILE_SIZE_EXCEED_UPPER_LIMIT,
    SERVER_ERROR,
    KEY_NOT_EXISTS
}


class BackendException : RuntimeException {
    var data: Any?
    var code: String
    var msg: String

    constructor(data: Any?, msg: String, code: String) {
        this.data = data
        this.code = code
        this.msg = msg
    }

    constructor(data: Any?, respCode: RespCode) {
        this.data = data
        this.code = respCode.getCode()
        msg = respCode.msg
    }

    constructor(cause: Throwable?, data: Any?, respCode: RespCode) : super(cause) {
        this.data = data
        this.code = respCode.getCode()
        msg = respCode.msg
    }
}

class ApiException : RuntimeException {
    var httpStatus: HttpStatus
    var msg: Any?

    constructor(httpStatus: HttpStatus, msg: Any?) {
        this.httpStatus = httpStatus
        this.msg = msg
    }

    constructor(message: String?, httpStatus: HttpStatus, msg: Any?) : super(message) {
        this.httpStatus = httpStatus
        this.msg = msg
    }

    constructor(message: String?, cause: Throwable?, httpStatus: HttpStatus, msg: Any?) : super(message, cause) {
        this.httpStatus = httpStatus
        this.msg = msg
    }

    override fun toString(): String {
        return "ApiException{" +
                "httpStatus=" + httpStatus +
                ", msg=" + msg +
                '}'
    }
}

/**
 * 全局异常处理类
 */
@ControllerAdvice
class GlobalExceptionHandler {

    private val log = KotlinLogging.logger {}

    class ParameterCheckResult {
        var paramCheckMap = mutableMapOf<String, Any?>()
        fun putResult(field: String, message: String?) {
            paramCheckMap.put(field, message)
        }
    }

    // @Value("\${spring.servlet.multipart.max-file-size}")
    var uploadLimit: String? = null

    // region: error code handler
    @ExceptionHandler(ApiException::class)
    @ResponseBody
    fun errorCodeException(req: HttpServletResponse, ex: ApiException): ResponseEntity<*> {
        log.error("[{}] {}", ex.httpStatus, ex.msg)
        return turnBackendExceptionIntoJsonResult(req, ex)
    }

    @Autowired
    lateinit var objectMapper: ObjectMapper

    /**
     * @param ex the exception
     * @return the json result
     */
    private fun turnBackendExceptionIntoJsonResult(req: HttpServletResponse, ex: ApiException): ResponseEntity<*> {
        return ResponseEntity(objectMapper.writeValueAsString(ex.msg), ex.httpStatus)
    }

    // endregion
    // region: validation error
    @ExceptionHandler(
        BindException::class,
        MethodArgumentNotValidException::class
    )
    fun paramValidateException(ex: Exception?): ResponseEntity<*> {
        return try {
            val bindingResult: BindingResult =
                ClassUtils.getFieldValue(ex!!, "bindingResult", BindingResult::class.java)
                ?:  return ResponseEntity(Any(), HttpStatus.BAD_REQUEST )
            val parameterCheckResult = extractValidationErrorEntries(bindingResult)
            log.error("Request parameter error:{}", parameterCheckResult)
            ResponseEntity(parameterCheckResult, HttpStatus.BAD_REQUEST )
        } catch (e: NoSuchFieldException) {
            ResponseEntity(Any(), HttpStatus.BAD_REQUEST )
        }
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun constraintViolationException(ex: ConstraintViolationException): ResponseEntity<*> {
        val constraintViolations = ex.constraintViolations
        val parameterCheckResult = ParameterCheckResult()
        for (constraintViolation in constraintViolations) {
            parameterCheckResult.putResult(
                getLastPathNode(constraintViolation.propertyPath),
                constraintViolation.message
            )
        }
        return ResponseEntity<Any?>(parameterCheckResult, HttpStatus.BAD_REQUEST)
    }

    private fun extractValidationErrorEntries(bindingResult: BindingResult): ParameterCheckResult {
        val parameterCheckResult = ParameterCheckResult()
        for (objectError in bindingResult.allErrors) {
            val fieldError = objectError as FieldError
            parameterCheckResult.putResult(fieldError.field, fieldError.defaultMessage)
        }
        return parameterCheckResult
    }

    // endregion
    // region: other exception
    @ExceptionHandler(Exception::class)
    fun otherException(req: HttpServletResponse, ex: Exception): ResponseEntity<*> {
        log.error(
            "Server Exception-Name:{}，Server Exception-Msg:{}",
            ex.javaClass.typeName,
            ex.message
        )
        if (ex is HttpMessageNotReadableException) {
            return turnBackendExceptionIntoJsonResult(
                req,
                ApiException(HttpStatus.BAD_REQUEST, "Request paramater is invalid")
            )
        }
        if (ex is MissingServletRequestParameterException) {
            return turnBackendExceptionIntoJsonResult(
                req,
                ApiException(HttpStatus.BAD_REQUEST, "Required request body is missing")
            )
        }
        if (ex is MaxUploadSizeExceededException) {
            return turnBackendExceptionIntoJsonResult(
                req, ApiException(
                    HttpStatus.PAYLOAD_TOO_LARGE,
                    "Limitation: $uploadLimit"
                )
            )
        }
        if (ex is MissingServletRequestPartException) {
            return turnBackendExceptionIntoJsonResult(req, ApiException(HttpStatus.BAD_REQUEST, ex.message))
        }
        ex.printStackTrace()
        return turnBackendExceptionIntoJsonResult(req, ApiException(HttpStatus.INTERNAL_SERVER_ERROR, ex.message))
    } // endregion

    companion object {
        private fun getLastPathNode(path: Path): String {
            val wholePath = path.toString()
            val i = wholePath.lastIndexOf(".")
            return if (i != -1) {
                wholePath.substring(i + 1)
            } else wholePath
        }
    }
}


