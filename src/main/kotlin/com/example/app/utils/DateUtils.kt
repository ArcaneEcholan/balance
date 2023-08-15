package com.example.app.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder


class DateUtils {
}

fun getMaxDays(year: Int, month: Int): Int {
    val yearMonth = YearMonth.of(year, month)
    return yearMonth.lengthOfMonth()
}


fun getMaxDays(formattedInput: String): Int {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM")
    val yearMonth = YearMonth.parse(formattedInput, formatter)
    return yearMonth.lengthOfMonth()
}

fun getMonthRange(formattedInput: String?): MonthRangeBean {
    val formatter = DateTimeFormatterBuilder()
        .parseCaseInsensitive()
        .appendPattern("yyyy-M")
        .toFormatter()

    return try {
        val yearMonth = YearMonth.parse(formattedInput, formatter)
        val startDate = yearMonth.atDay(1)
        val startTime = LocalTime.of(0, 0, 0)
        val startDateTime = LocalDateTime.of(startDate, startTime)
        val lastDayOfMonth = yearMonth.lengthOfMonth()
        val endDate = LocalDate.of(yearMonth.year, yearMonth.month, lastDayOfMonth)
        val endTime = LocalTime.of(23, 59, 59)
        val endDateTime = LocalDateTime.of(endDate, endTime)
        MonthRangeBean(startDateTime, endDateTime)
    } catch (e: Exception) {
        throw java.lang.IllegalArgumentException("Invalid input format. Please use yyyy-MM format.", e)
    }
}

data class MonthRangeBean(val startDateTime: LocalDateTime, val endDateTime: LocalDateTime) {

}

fun main(args: Array<String>) {
    val formattedInput = "2022-02" // Format: yyyy-MM
    try {
        val monthRange = getMonthRange(formattedInput)
        val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val startDateTimeStr = monthRange.startDateTime.format(outputFormatter)
        val endDateTimeStr = monthRange.endDateTime.format(outputFormatter)
        println("Start DateTime: $startDateTimeStr")
        println("End DateTime: $endDateTimeStr")
    } catch (e: IllegalArgumentException) {
        println("Error: " + e.message)
    }
}