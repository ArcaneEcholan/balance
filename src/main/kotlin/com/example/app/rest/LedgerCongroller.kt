package com.example.app.rest

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.example.app.dao.mapper.LedgerMapper
import com.example.app.dao.po.LedgerPO
import com.example.app.utils.DateTime
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import javax.validation.constraints.NotEmpty

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
        if(e == null) {
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

    @GetMapping("/ledgers")
    fun test(): ResponseEntity<Any> {
        return ResponseEntity.ok(ledgerMapper.selectList(null))
    }

}
