package com.example.app.rest

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.example.app.dao.TransactionCategoryDao
import com.example.app.dao.mapper.LedgerMapper
import com.example.app.dao.po.LedgerPO
import com.example.app.service.TransactionCategoryService
import com.example.app.service.TransactionCategoryVO
import com.example.app.service.TransactionService
import com.example.app.service.TransactionVO
import com.example.app.utils.DateTime
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import javax.validation.Valid
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.Positive


class SaveTranDTO {
    @NotEmpty
    var categoryValue: String? = null

    @NotNull
    @Pattern(regexp = "^[-+]?[0-9]*\\.?[0-9]+$", message = "amount must be double")
    var amount: String? = null

    @NotNull
    @Positive(message = "count must be positive")
    var count: Int? = null

    var description: String? = null

    @NotNull
    var location: Map<String, String>? = null
}

//     SaveTranDTO:
//       type: object
//       properties:
//         categoryValue:
//           type: string
//         amount:
//           type: string
//         count:
//           type: integer
//         description:
//           type: string
//         location:
//           type: object
class SaveTransDTO {
    @NotNull
    @Valid
    var transactionList: List<SaveTranDTO>? = null
}

//     SaveTransDTO:
//       type: object
//       properties:
//         transactionList:
//           type: array
//           items:
//             $ref: '#/definitions/SaveTranDTO'

@RequestMapping("/api")
@RestController
@Validated
class TransController {
    private val log = KotlinLogging.logger {}

    @Autowired
    lateinit var transactionService: TransactionService

    @Autowired
    lateinit var categoryDao: TransactionCategoryDao

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


    @PostMapping("/transactions/{ledger_name}")
    fun saveTransactions(
        @PathVariable("ledger_name") ledgerName: String,
        @RequestBody @Valid transactionList: SaveTransDTO,
    ):
            ResponseEntity<*> {
        transactionList.transactionList!!.map {
            var cateId = categoryDao.getOneByMap("value", it.categoryValue)?.id

            transactionService.insert(
                ledgerName = ledgerName,
                amount = BigDecimal(it.amount),
                categoryId = cateId ?: 0,
                count = it.count!!,
                description = it.description,
                datetime = DateTime.now().toString(),
                // leave location as empty, support it later
                location = it.location!!
            )
        }.toList().apply {
            return ResponseEntity.ok(this)
        }
    }

    @Autowired
    lateinit var ledgerMapper: LedgerMapper

    @GetMapping("/transactions/{ledger_name}")
    fun getTransactions(
        @PathVariable("ledger_name") ledgerName: String,
        @Valid @NotNull month: String,
    ): ResponseEntity<List<TransactionVO>> {
        val selectOne = ledgerMapper.selectOne(
            QueryWrapper<LedgerPO>()
                .eq("name", ledgerName)
        )
        if (selectOne == null) {
            return ResponseEntity.ok(listOf());
        }

        return ResponseEntity.ok(transactionService.list(selectOne.id!!, month));
    }

    @GetMapping("/transactions")
    fun getTransactions(
        @Valid @NotNull month: String,
    ): ResponseEntity<List<TransactionVO>> {
        // pick the one with name "default"
        val ledgerId = ledgerMapper.selectList(null).find { it.name == "default" }?.id

        val selectById = ledgerMapper.selectById(ledgerId)
        if (selectById == null) {
            return ResponseEntity.notFound().build()
        }

        return ResponseEntity.ok(transactionService.list(ledgerId!!, month));
    }

    @PutMapping("/transaction")
    fun updateTransaction(
        @RequestBody
        updateTransactionDTO: @NotNull UpdateTransactionDTO,
    ): ResponseEntity<TransactionVO> {
        var updated = transactionService.update(
            updateTransactionDTO.transactionId!!,
            updateTransactionDTO.categoryValue!!,
            BigDecimal(updateTransactionDTO.amount!!),
            updateTransactionDTO.count!!,
            updateTransactionDTO.description,
            updateTransactionDTO.datetime
        )

        return ResponseEntity.ok(updated);
    }

    @Autowired
    lateinit var transactionCategoryService: TransactionCategoryService

    @GetMapping("/transaction/category")
    fun getTransactionCategories(): List<TransactionCategoryVO> {
        return transactionCategoryService.getAll()
    }

}


class UpdateTransactionDTO {
    @NotNull
    var transactionId: Long? = null

    @NotEmpty
    var categoryValue: String? = null

    @NotNull
    @Pattern(regexp = "^[-+]?[0-9]*\\.?[0-9]+$", message = "amount must be double")
    var amount: String? = null

    @NotNull
    @Positive(message = "count must be positive")
    var count: Int? = null

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\$", message = "datetime format error")
    var datetime: String? = null

    var description: String? = null
}
