package com.schednd.domain.model

import java.time.LocalDate

data class DateSummary(
    val date: LocalDate,
    val count: Int,
    val total: Int,
    val absentNames: List<String>,
    val tier: AttendanceTier
)
