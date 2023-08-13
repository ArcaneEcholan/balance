package com.example.app.utils

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper

class MyBatisUtils {
}


object MyBatisUtil {
    fun escapeColumnName(columnName: String): String {
        // Check if the column name is already wrapped in backticks
        return if (columnName.startsWith("`") && columnName.endsWith("`")) {
            columnName // Already escaped, return as is
        } else {
            "`$columnName`" // Wrap in backticks
        }
    }
}

fun <T> create(): QueryWrapper<T> {
    return EscapedQueryWrapper()
}




