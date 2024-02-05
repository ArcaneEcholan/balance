package com.example.app.rest

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.example.app.dao.*
import com.example.app.dao.mapper.LedgerMapper
import com.example.app.dao.mapper.LedgerTransactionMapper
import com.example.app.dao.po.LedgerPO
import com.example.app.dao.po.LedgerTransactionPO
import com.example.app.utils.DateTime
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

class CreateLedgerDTO {
    @NotEmpty
    var name: String? = null
}

class EditLedgerDTO {
    @NotNull
    var id: Long? = null

    @NotEmpty
    var name: String? = null
}

@RequestMapping("/api")
@RestController
@Validated
class LedgerCongroller {

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var ledgerMapper: LedgerMapper

    @Autowired
    lateinit var userLedgerMapper: UserLedgerMapper

    @PostMapping("/ledger")
    @AuthLogin
    @Transactional
    fun tes5t1(@Valid @RequestBody createLedgerDTO: CreateLedgerDTO): ResponseEntity<Any> {
        val name = createLedgerDTO.name!!

        val user = getCurrentUser()
        var b = userLedgerMapper.getUserLedgers(user.id!!)
        var bb = b.filter { it -> it.ledgerName == name }


        if (bb != null && bb.size > 0) {
            return ResponseEntity.badRequest().body(objectMapper.writeValueAsString("name already exists"));
        }

        var newLedger = LedgerPO(null, name, DateTime.now().toString());
        ledgerMapper.insert(newLedger)

        userLedgerMapper.insert(UserLedgerPO(null, user.id!!, newLedger.id!!))

        return ResponseEntity.ok(newLedger);
    }

    @DeleteMapping("/ledger/{id}")
    @AuthLogin
    @Transactional
    fun tes5t11(@PathVariable id: Long): ResponseEntity<Any> {
        var user = getCurrentUser()

        var ledger1: UserLedgerMapperResult? = null

        var found = false
        var jalf = userLedgerMapper.getUserLedgers(user.id!!)
        jalf.forEach {
            if (it.ledgerId == id) {
                found = true
                ledger1 = it
            }
        }

        if (found == false) {
            return ResponseEntity.internalServerError()
                .body(objectMapper.writeValueAsString(object {
                    var message = "ledger not found"
                }));
        }

        var ledger = LedgerPO().apply {
            this.id = ledger1!!.ledgerId
            this.name = ledger1!!.ledgerName
        }

        var ledgername = ledger.name!!
        if(ledgername == "default") {
            return ResponseEntity.internalServerError()
                .body(objectMapper.writeValueAsString(object {
                    var message = "can not delete default ledger"
                }));
        }

        var config = userUserConfigMapper.getUserConfigByKey(user.id!!, "default_ledger")
        if (config != null) {
            var userDefault = config.value
            if (userDefault == ledgername) {
                return ResponseEntity.internalServerError()
                    .body(objectMapper.writeValueAsString(object {
                        var message = "can not delete current ledger"
                    }))
            }
        }

        ledgerMapper.deleteById(id)
        return ResponseEntity.ok(null);
    }

    @PutMapping("/ledger")
    @Transactional
    @AuthLogin
    fun tesfas5t1(@Valid @RequestBody editLedgerDTO: EditLedgerDTO): ResponseEntity<Any> {
        if(editLedgerDTO.name == "default"){
            return ResponseEntity.badRequest().body(object {
                var message = "can not modify default ledger name"
            });
        }

        var user = requestCtx.get()["user"] as UserPO

        var userledgers =
            userLedgerMapper.getUserLedgers(user.id!!)

        userledgers.filter { it -> editLedgerDTO.id == it.ledgerId }
            .ifEmpty {
                return ResponseEntity.ok(null);
            }

        var e = userledgers.map {
            LedgerPO().apply {
                this.id = it.ledgerId
                this.name = it.ledgerName
            }
        }.first()

        val name = editLedgerDTO.name!!

        var b = ledgerMapper.selectList(
            QueryWrapper<LedgerPO>().eq("name", name)
                .ne("id", editLedgerDTO.id)
        )

        if (b != null && b.size > 0) {
            return ResponseEntity.badRequest().body(objectMapper.writeValueAsString("name already exists"));
        }

        var a = LedgerPO(editLedgerDTO.id, name, null);
        ledgerMapper.updateById(a)

        var defaultLedgerName = userUserConfigMapper.getUserConfigByKey(user.id!!, "default_ledger")
        if (defaultLedgerName == null) {
            return ResponseEntity.ok(null);
        }

        if (defaultLedgerName.value == e.name) {
            userConfigMapper.updateById(UserConfigPO(defaultLedgerName.userConfigId!!, defaultLedgerName.key, name))
        }
        return ResponseEntity.ok(null)
    }

    @Autowired
    lateinit var userConfigMapper: UserConfigMapper

    @Autowired
    lateinit var userUserConfigMapper: UserUserConfigMapper

    @Autowired
    lateinit var ledgerTransactionMapper: LedgerTransactionMapper

    @GetMapping("/ledgers")
    @AuthLogin
    @Transactional
    fun getLedgers(@RequestParam(value = "record_id", required = false) recordId: Long?): ResponseEntity<Any> {

        var user = getCurrentUser()

        var userLedgers = userLedgerMapper.getUserLedgers(user.id!!)

        userLedgers.map { it -> it.ledgerName!! }.contains("default").not().apply {
            if (this) {
                var newledger = LedgerPO(null, "default", DateTime.now().toString())
                ledgerMapper.insert(newledger)
                userLedgerMapper.insert(UserLedgerPO(null, user.id!!, newledger.id!!))
            }
        }

        var allLedgers = userLedgers.map {
            LedgerPO().apply {
                this.id = it.ledgerId
                this.name = it.ledgerName
            }
        }

        return ResponseEntity.ok(allLedgers)
    }

    class transferLRecordsReq {

        @Valid
        var record_ids: List<Long>? = null

        @Valid
        var ledger_ids: List<Long>? = null
    }

    @PutMapping("/record/ledgers")
    @AuthLogin
    @Transactional
    fun transferLRecordsBetweenLedgers(@RequestBody @Valid transferLRecordsReq: transferLRecordsReq): Any {
        if (transferLRecordsReq.ledger_ids!!.size > 1 || transferLRecordsReq.ledger_ids!!.size == 0) {
            return ResponseEntity.badRequest().body(objectMapper.writeValueAsString(object {
                var message = "ledger id only supports 1 element"
            }));
        }

        if (transferLRecordsReq.ledger_ids!!.filterNotNull().size < transferLRecordsReq.ledger_ids!!.size) {
            return ResponseEntity.badRequest().body(objectMapper.writeValueAsString(object {
                var message = "ledger id can not be null"
            }));
        }

        if (transferLRecordsReq.record_ids!!.filterNotNull().size < transferLRecordsReq.record_ids!!.size) {
            return ResponseEntity.badRequest().body(objectMapper.writeValueAsString(object {
                var message = "record id can not be null"
            }));
        }

        var user = getCurrentUser()

        var userLedgers =
            userLedgerMapper.getUserLedgers(user.id!!)

        var ledgerId = transferLRecordsReq.ledger_ids!!.first()
        userLedgers.map { it.ledgerId }.contains(ledgerId).not().apply {
            if (this) {
                return ResponseEntity.badRequest().body(objectMapper.writeValueAsString(object {
                    var message = "ledger not found"
                }));
            }
        }

        transferLRecordsReq.record_ids!!.forEach { recordId ->
            ledgerTransactionMapper.deleteByMap(mapOf("transaction_id" to recordId))
            transferLRecordsReq.ledger_ids!!.forEach {
                ledgerTransactionMapper.insert(LedgerTransactionPO(null, it, recordId))
            }
        }

        return ResponseEntity.ok().body(object {})
    }
}
