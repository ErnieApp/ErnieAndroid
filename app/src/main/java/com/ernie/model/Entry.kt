package com.ernie.model

data class Entry(
        val id: String,
        val start_time: String,
        val end_time: String,
        val break_duration: Int,
        val earned: Int,
        val date_recorded: String
)
