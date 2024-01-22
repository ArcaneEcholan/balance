package com.example.app.rest

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
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

    @PostMapping("/ledger")
    fun tes5t1(@Valid @RequestBody createLedgerDTO: CreateLedgerDTO): ResponseEntity<Any> {
        val name = createLedgerDTO.name!!

        var b = ledgerMapper.selectList(
            QueryWrapper<LedgerPO>().eq("name", name)
        )

        if (b != null && b.size > 0) {
            return ResponseEntity.badRequest().body(objectMapper.writeValueAsString("name already exists"));
        }

        var a = LedgerPO(null, name, DateTime.now().toString());
        ledgerMapper.insert(a)

        return ResponseEntity.ok(a);
    }

    @DeleteMapping("/ledger/{id}")
    fun tes5t11(@PathVariable id: Long): ResponseEntity<Any> {
        ledgerMapper.deleteById(id)
        return ResponseEntity.ok(null);
    }

    @PutMapping("/ledger/{id}")
    fun tesfas5t1(@PathVariable id: Long, @Valid @RequestBody editLedgerDTO: EditLedgerDTO): ResponseEntity<Any> {
        var e = ledgerMapper.selectById(id)
        if (e == null) {
            return ResponseEntity.ok(null);
        }

        val name = editLedgerDTO.name!!

        var b = ledgerMapper.selectList(
            QueryWrapper<LedgerPO>().eq("name", name)
                .ne("id", id)
        )

        if (b != null && b.size > 0) {
            return ResponseEntity.badRequest().body(objectMapper.writeValueAsString("name already exists"));
        }

        var a = LedgerPO(id, name, null);
        ledgerMapper.updateById(a)

        return ResponseEntity.ok(null);
    }

    @Autowired
    lateinit var ledgerTransactionMapper: LedgerTransactionMapper

    @GetMapping("/ledgers")
    fun getLedgers(@RequestParam("record_id") recordId: Long?): ResponseEntity<Any> {

        var allLedgers = ledgerMapper.selectList(null)

        if (recordId != null) {
            var temp = ledgerTransactionMapper.selectByMap(mapOf("transaction_id" to recordId))
            var recordRelatedLedger = temp.map {
                var tempLedger = ledgerMapper.selectById(it.ledgerId)
                tempLedger
            }.filterNotNull().map { it.id }.toMutableList();

            allLedgers.map {
                if (recordRelatedLedger.contains(it.id)) {
                    object {
                        var id = it.id
                        var name = it.name
                        var related = true
                    }
                } else {
                    object {
                        var id = it.id
                        var name = it.name
                        var related = false
                    }
                }
            }.toList().apply {
                return ResponseEntity.ok(this)
            }

        }

        return ResponseEntity.ok(allLedgers)
    }

    class UpdateRecordLedgersReq {
        @NotNull
        var record_id: Long? = null

        @Valid
        var ledger_ids: List<Long>? = null
    }

    @PutMapping("/record/ledgers")
    @Transactional
    fun updateRecordLedgers(@RequestBody @Valid updateRecordLedgersReq: UpdateRecordLedgersReq): Any {
        if(updateRecordLedgersReq.ledger_ids!!.filterNotNull().size < updateRecordLedgersReq.ledger_ids!!.size){
            return ResponseEntity.badRequest().body(objectMapper.writeValueAsString(object {
                var message = "ledger id can not be null"
            }));
        }

        ledgerTransactionMapper.deleteByMap(mapOf("transaction_id" to updateRecordLedgersReq.record_id))
        updateRecordLedgersReq.ledger_ids!!.forEach {
            ledgerTransactionMapper.insert(LedgerTransactionPO(null, it, updateRecordLedgersReq.record_id ))
        }
        return ResponseEntity.ok().body(object {})
    }
}
