package com.example.app.rest

import com.example.app.dao.*
import com.example.app.service.TransactionService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
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

class SaveTransDTO {
    @NotNull
    @Valid
    var transactionList: List<SaveTranDTO>? = null

    @NotEmpty
    var ledger_name: String? = null
}

@RequestMapping("/api")
@RestController
@Validated
class TransController {
    private val log = KotlinLogging.logger {}

    @Autowired
    lateinit var transactionService: TransactionService

    @Autowired
    lateinit var userLedgerMapper: UserLedgerMapper

    @Autowired
    lateinit var userDao: UserDao


    @PostMapping("/transactions")
    @Transactional
    @AuthLogin
    fun saveTransactions(
        @RequestBody @Valid transactionList: SaveTransDTO,
    ): ResponseEntity<*> {
        var user = getCurrentUser()

        var userLedgers =
            userLedgerMapper.getUserLedgers(user.id!!)

        var filterList = userLedgers.filter { it -> it.ledgerName == transactionList.ledger_name }
        if (filterList.isEmpty()) {
            return ResponseEntity.badRequest().body(object {
                var message = "ledger not found"
            })
        }

        transactionList.transactionList!!.map {
            userDao.addRecordToUser(it, user.id!!, transactionList.ledger_name!!)
        }.toList().apply {
            return ResponseEntity.ok(this)
        }
    }

    @GetMapping("/transactions")
    @AuthLogin
    @Transactional
    fun getTransactions(
        @RequestParam("ledger_name") ledgerName: String,
        @Valid @NotNull month: String,
    ): ResponseEntity<Any> {
        var user = getCurrentUser()

        var ll = userLedgerMapper.getUserLedgers(user.id!!).filter { it -> it.ledgerName == ledgerName }

        if(ll.isEmpty()) {
            return ResponseEntity.badRequest().body(object  {
                var message = "ledger not found"
            })
        }

        return ResponseEntity.ok(transactionService.list(ll.first().ledgerId!!, month));
    }

    @PutMapping("/transaction")
    @AuthLogin
    @Transactional
    fun updateTransaction(
        @RequestBody
        updateTransactionDTO: @NotNull UpdateTransactionDTO,
    ): ResponseEntity<Any> {

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
    lateinit var userRecordTypeMapper: UserRecordTypeMapper

    @GetMapping("/transaction/category")
    @Transactional
    @AuthLogin
    fun getTransactionCategories(): Any {
        var user = getCurrentUser()
        var userRecordTypeList =
        userRecordTypeMapper.getUserRecordTypes(user.id!!).map {
            object  {
                var id: Long = it.typeId!!
                var value: String = it.typeValue!!
                var icon: String = it.typeIcon!!
            }
        }
        return userRecordTypeList
    }

    @Autowired
    lateinit var recordTypesDao: TransactionCategoryDao

    class CreateTransTypeReq {
        @NotEmpty
        var name: String? = null;

        @NotEmpty
        var icon: String? = null;
    }

    @AuthLogin
    @PostMapping("/transaction/category")
    @Transactional
    fun createTransactionCategories(@RequestBody @Valid createTransTypeReq: CreateTransTypeReq): Any {

        var iconList = mapOf<String, String>(
            "daily" to "ali-international-icon-daily",
            "food_dish" to "ali-international-icon-food_dish",
            "fruit" to "ali-international-icon-fruit",
            "drinks" to "ali-international-icon-drinks",
            "beer" to "ali-international-icon-beer",
            "transport" to "ali-international-icon-transport",
            "entertainment" to "ali-international-icon-entertainment",
            "others" to "ali-international-icon-others",
            "water_cup" to "ali-international-icon-water_cup",
            "icecream" to "ali-international-icon-icecream",
            "mobile_phone" to "ali-international-icon-mobile_phone",
            "medical_care" to "ali-international-icon-medical_care",
            "tools_hardware" to "ali-international-icon-tools_hardware",
            "productivity" to "ali-international-icon-productivity",
            "furniture" to "ali-international-icon-furniture",
        )

        var name = createTransTypeReq.name
        var icon = createTransTypeReq.icon

        if (!iconList.containsKey(icon)) {
            return ResponseEntity.badRequest().body(object {
                var message = "icon not found"
            })
        }

        userRecordTypeMapper.getUserRecordByValue(getCurrentUser().id!!, name!!)?.let {
            return ResponseEntity.badRequest().body(object {
                var message = "name already exists"
            })
        }

        var newCat = TransactionCategoryPO(null, name, icon)
        recordTypesDao.save(newCat)

        var newUserCat = UserRecordTypePO(null, getCurrentUser().id, newCat.id)
        userRecordTypeMapper.insert(newUserCat)

        return ResponseEntity.ok(object {
            var id: Long = newCat.id!!
        })
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
