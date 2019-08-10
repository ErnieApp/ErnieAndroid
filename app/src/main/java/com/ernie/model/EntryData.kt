package com.ernie.model

import java.util.*


data class EntryData(
        val start_time: String? = null,
        val earned: Int = 0,
        val end_time: String? = null,
        val date_recorded: Date? = null,
        val break_duration: Int = 0
)