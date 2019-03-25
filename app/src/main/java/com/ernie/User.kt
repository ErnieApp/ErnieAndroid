package com.ernie

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
        var username: String? = ""
)