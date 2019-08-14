package com.ernie.model

class User(private val name: String, private val email: String, private val hourly_rate: String) {

    fun getName(): String {
        return name
    }

    fun getEmail(): String {
        return email
    }

    fun getHourlyRate(): String {
        return hourly_rate
    }
}
