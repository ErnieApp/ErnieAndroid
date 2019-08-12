package com.ernie.model

import java.util.*


data class Entry(
        val start_time: String? = null,
        val end_time: String? = null,
        val break_duration: Int = 0,
        val earned: Int = 0,
        val date_recorded: Date? = null

)