package com.ernie.model

data class User(
        val name: String? = "",
        val email: String? = "",
        val hourly_rate: String? = "",
        val previous_pay_date: String? = "",
        val upcoming_pay_date: String? = ""
)
