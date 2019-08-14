package com.ernie.model

data class Entry(
        val id: String? = null,
        val start_time: String? = null,
        val end_time: String? = null,
        val break_duration: Int = 0,
        val earned: Int = 0,
        val date_recorded: String? = null
)
