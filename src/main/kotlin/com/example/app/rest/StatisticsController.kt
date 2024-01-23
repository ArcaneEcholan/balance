package com.example.app.rest

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.example.app.dao.UserPO
import com.example.app.dao.UserUserConfigMapper
import com.example.app.dao.mapper.LedgerMapper
import com.example.app.dao.po.LedgerPO
import com.example.app.exception.ApiException
import com.example.app.service.TransactionService
import com.example.app.service.TransactionVO
import com.example.app.utils.getLastMonth
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal
import javax.validation.constraints.NotEmpty

@RestController
@Validated
@RequestMapping("/api")
class StatisticsController {

    @Autowired
    lateinit var transactionService: TransactionService

    @Autowired
    lateinit var ledgerMapper: LedgerMapper

    @Autowired
    lateinit var userUserConfigMapper: UserUserConfigMapper

    @GetMapping("/statistics")
    @AuthLogin
    fun s(@NotEmpty month: String): ResponseEntity<Any> {

        var user = requestCtx.get()["user"] as UserPO
        var userDefaultLedger = userUserConfigMapper.getUserConfigByKey(user.id!!, "default_ledger")
        if (userDefaultLedger == null) {
            throw ApiException(HttpStatus.NOT_FOUND, "Ledger not found")
        }

        var ledgerName = userDefaultLedger.value

        val selectOne = ledgerMapper.selectOne(QueryWrapper<LedgerPO>().eq("name", ledgerName))
        if (selectOne == null) {
            throw ApiException(HttpStatus.NOT_FOUND, "Ledger not found")
        }

        var ledger = selectOne

        var ledgerId = ledger.id

        val thisMonthTrans = transactionService.list(ledgerId!!, month)

        var thisMonthTotal = getThisMonthTotal(thisMonthTrans)
        var thisMonthRankList = getThisMonthRankList(thisMonthTrans)
        var thisMonthTypeRankList = getThisMonthTypeRankList(thisMonthTrans)

        var lastMontTrans = getLastMonthTrans(ledgerId, month)

        var lastMonthTotal = getLastMonthTotal(lastMontTrans)

        var percent = getPercent(thisMonthTotal, lastMonthTotal)

        return ResponseEntity.ok(
            object {
                var last_month_total = lastMonthTotal.toString()
                var this_month_total = thisMonthTotal.toString()
                var percent = percent
                var type_rank_list = thisMonthTypeRankList
                var rank_list = thisMonthRankList
            }
        )
    }

    private fun getPercent(thisMonthTotal: BigDecimal, lastMonthTotal: BigDecimal): String {
        var r = ""
        var percent = BigDecimal(0)
        if (lastMonthTotal.equals(BigDecimal(0))) {
            r = "infinite"
        } else {
            percent = (thisMonthTotal - lastMonthTotal) / lastMonthTotal
            r = percent.multiply(BigDecimal(100)).toString() + "%"
        }
        return r
    }

    private fun getThisMonthRankList(thisMonthTrans: List<TransactionVO>): List<Any> {
        class test {
            var id = 0
            var type: String? = ""
            var amount: String = ""
            var description: String? = ""
            var datetime: String? = ""
            var count = 0
            var total: String = ""
            var percent: String = ""

        }

        var r = thisMonthTrans.map {
            if (it.categoryValue == null) {
                it.categoryValue = "unknown"
            }
            it
        }.map {
            var a = it.amount
            if (a.compareTo(BigDecimal(0)) > 0) {
                var b = it.count
                var t = a.multiply(BigDecimal(b))
                t = t.setScale(2)
                test().apply {
                    this.id = it.categoryId.toInt()
                    this.type = it.categoryValue
                    this.amount = it.amount.toString()
                    this.description = it.description
                    this.datetime = it.datetime
                    this.count = it.count
                    this.total = t.toString()
                }
            } else {
                null
            }
        }.filterNotNull().sortedByDescending { BigDecimal(it.total) }

        if (r.isEmpty()) {
            return r
        }
        var f = r.first()
        var ft = f.total
        for (item in r) {
            var it = BigDecimal(item.total)
            item.percent = it.divide(BigDecimal(ft), 2, BigDecimal.ROUND_HALF_UP).toString()
        }

        return r
    }

    private fun getLastMonthTrans(ledgerId: Long, month: String): List<TransactionVO> {
        var lm = getLastMonth(month + "-01")
        val lastMonthLedgerTrans = transactionService.list(ledgerId, lm)
        return lastMonthLedgerTrans
    }

    private fun getThisMonthTotal(ledgerTransactions: List<TransactionVO>): BigDecimal {
        var thisMonthTotal = BigDecimal(0)
        ledgerTransactions.forEach {
            var a = it.amount
            if (a.compareTo(BigDecimal(0)) > 0) {
                var b = it.count
                var t = a.multiply(BigDecimal(b))
                thisMonthTotal = thisMonthTotal.add(t)
            }
        }
        return thisMonthTotal
    }

    private fun getLastMonthTotal(ledgerTransactions: List<TransactionVO>): BigDecimal {

        var lastMonthTotal = BigDecimal(0)

        ledgerTransactions.forEach {
            var a = it.amount
            if (a.compareTo(BigDecimal(0)) > 0) {
                var b = it.count
                var t = a.multiply(BigDecimal(b))
                lastMonthTotal = lastMonthTotal.add(t)
            }
        }
        return lastMonthTotal
    }

    private fun getThisMonthTypeRankList(ledgerTransactions: List<TransactionVO>): Any {
        class test {
            var id = 0
            var type = "unknown"
            var totalDecimal = BigDecimal(0)
            var total = ""
            var percent = ""
            fun add(t: BigDecimal) {
                totalDecimal = totalDecimal.add(t).setScale(2)
            }
        }

        var catMap = mutableMapOf<String, test>()
        ledgerTransactions.map {
            if (it.categoryValue == null) {
                it.categoryValue = "unknown"
            }
            it
        }.forEach {
            var a = it.amount
            if (a.compareTo(BigDecimal(0)) > 0) {
                var b = it.count
                var t = a.multiply(BigDecimal(b))
                if (catMap.containsKey(it.categoryValue)) {
                    catMap[it.categoryValue]!!.add(t)
                } else {
                    catMap[it.categoryValue] = test().apply {
                        id = it.categoryId.toInt()
                        type = it.categoryValue
                        totalDecimal = t.setScale(2)
                    }
                }
            }
        }

        val typeRankList = catMap.values.sortedByDescending { it.totalDecimal }

        var r = typeRankList.map {
            it.total = it.totalDecimal.setScale(2).toString()
            it
        }.toMutableList()

        if (r.isEmpty()) {
            return r
        }
        var f = r.first()
        var ft = f.total
        for (item in r) {
            var it = BigDecimal(item.total)
            item.percent = it.divide(BigDecimal(ft), 2, BigDecimal.ROUND_HALF_UP).toString()
        }

        return r
    }

    private fun checkLedgerExist(ledgerName: String): LedgerPO {
        val selectOne = ledgerMapper.selectOne(QueryWrapper<LedgerPO>().eq("name", ledgerName))
        if (selectOne == null) {
            throw ApiException(HttpStatus.NOT_FOUND, "Ledger not found")
        }
        return selectOne
    }
}
