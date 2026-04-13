package com.schednd.domain.model

enum class AttendanceTier { FULL, VIABLE, LIMITED, INSUFFICIENT }

fun computeAttendanceTier(count: Int, total: Int): AttendanceTier {
    if (total == 0) return AttendanceTier.INSUFFICIENT
    val pct = count.toDouble() / total
    return when {
        pct >= 0.86 -> AttendanceTier.FULL
        pct >= 0.71 -> AttendanceTier.VIABLE
        pct >= 0.57 -> AttendanceTier.LIMITED
        else -> AttendanceTier.INSUFFICIENT
    }
}
