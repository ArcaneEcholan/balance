package com.example.app.rest

import cn.hutool.core.date.DateTime
import com.example.app.dao.TransactionCategoryDao
import com.example.app.service.TransactionService
import org.springframework.validation.annotation.Validated
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.Positive


class SaveTranDTO {
    @NotNull
    var categoryValue: String? = null

    @NotNull
    @Pattern(regexp = "^[-+]?[0-9]*\\.?[0-9]+$", message = "amount must be double")
    var amount: String? = null

    @Positive(message = "count must be positive")
    var count: Int? = null

    var description: String? = null

    var location: Map<String, String>? = null
}

class SaveTransDTO {
    @NotNull
    var transactionList: List<SaveTranDTO>? = null
}

@RequestMapping("/api")
@RestController
@Validated
class TransController {
    private val log = KotlinLogging.logger {}

    @Autowired
    lateinit var transactionService: TransactionService

    @Autowired
    lateinit var categoryDao: TransactionCategoryDao

    @GetMapping("/test")
    fun test(): ResponseEntity<*> {
        return ResponseEntity.ok("test");
    }

    @PostMapping("/transactions")
    fun saveTransactions(@RequestBody @Valid transactionList: SaveTransDTO):
            ResponseEntity<*> {
        transactionList.transactionList!!.map {
            var cateId = categoryDao.getOneByMap("value", it.categoryValue)?.id

            transactionService.insert(
                amount = BigDecimal(it.amount),
                categoryId = cateId ?: 0,
                count = it.count!!,
                description = it.description,
                datetime = DateTime.now().toString(),
                // leave location as empty, support it later
                location = it.location ?: mapOf(
                )
            )
        }.toList().apply {
            return ResponseEntity.ok(this)
        }
    }

}