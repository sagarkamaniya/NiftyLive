package com.example.niftylive.utils

import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

object TimeUtils {
    private val marketOpen = LocalTime.of(9, 15)
    private val marketClose = LocalTime.of(15, 30)
    private val indiaZone = ZoneId.of("Asia/Kolkata")

    fun isMarketOpen(now: ZonedDateTime = ZonedDateTime.now(indiaZone)): Boolean {
        val t = now.toLocalTime()
        val day = now.dayOfWeek
        if (day.name == "SATURDAY" || day.name == "SUNDAY") return false
        return !t.isBefore(marketOpen) && !t.isAfter(marketClose)
    }
}
