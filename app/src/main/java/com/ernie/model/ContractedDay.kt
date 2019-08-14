package com.ernie.model

class ContractedDay(private val day: Day, private val startTime: String, private val endTime: String) {

    fun getDay(): Day {
        return day
    }

    fun getStartTime(): String {
        return startTime
    }

    fun getEndTime(): String {
        return endTime
    }
}