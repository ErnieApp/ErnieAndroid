package com.ernie.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
        var username: String? = ""
)