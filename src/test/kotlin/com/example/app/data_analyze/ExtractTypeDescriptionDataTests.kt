package com.example.app.data_analyze;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.example.app.dao.TransactionCategoryDao
import com.example.app.dao.TransactionDao
import com.example.app.dao.po.LedgerPO
import com.example.app.dao.po.LedgerTransactionPO
import com.example.app.dao.repo.LedgerDao
import com.example.app.dao.repo.LedgerTransactionDao
import com.opencsv.CSVWriter
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.io.File
import java.io.FileWriter
import java.io.IOException


@ActiveProfiles("data-analyze")
@SpringBootTest
class ExtractTypeDescriptionDataTests {

    @Autowired
    lateinit var transactionDao: TransactionDao

    @Autowired
    lateinit var ledgerDao: LedgerDao

    @Autowired
    lateinit var ledgerTransactionDao: LedgerTransactionDao

    @Autowired
    lateinit var transactionCategoryDao: TransactionCategoryDao


    @BeforeEach
    fun init() {
    }

    @Test
    fun test() {

        var ledgerName = "default"

        var ledger = ledgerDao.getOne(QueryWrapper<LedgerPO>().eq("name", ledgerName))

        println(ledger)

        var ledgerId = ledger.id;
        ledgerId ?: run {
            println("ledgerId is null")
            return
        }

        class TempData {
            var desc:String? = ""
            var type:String? = ""
        }

        var records =
            ledgerTransactionDao.list(
                QueryWrapper<LedgerTransactionPO>()
                    .eq("ledger_id", ledgerId)
            )

        val toList = records.map {

            var rid = it.transactionId
            rid ?: throw Exception("rid is null")
            var transaction = transactionDao.getById(rid)
            transaction

           transaction
        }.filterNotNull().map{transaction->
            var data = TempData()
            val categoryId = transaction.categoryId
            categoryId .let {
                var category = transactionCategoryDao.getById(categoryId)
                category?.let {
                    data.type = category.value
                }
            }

            data.desc = transaction.description

            data
        }.toList()

        println(toList.size)

        val file = File("./desc-type.csv")
        try {
            CSVWriter(FileWriter(file)).use { writer ->
                val header = arrayOf("Description","Type")
                writer.writeNext(header)
                toList.forEach({
                    val data = arrayOf(it.desc, it.type)
                    writer.writeNext(data)
                })
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


}
