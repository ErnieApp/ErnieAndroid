package com.ernie.model


class User {
    var _id: Int = 0
    var name: String? = null
    var email: String? = null
    var password: String? = null
    var hourly_rate: String? = null
    var contract_id: Int = 0


    constructor   (_id: Int,
                   name: String,
                   email: String,
                   password: String,
                   hourly_rate: String,
                   contract_id: Int
    ) {

        this._id = _id
        this.name = name
        this.email = email
        this.password = password
        this.hourly_rate = hourly_rate
        this.contract_id = contract_id


    }

    constructor   (
            name: String,
            email: String,
            password: String,
            hourly_rate: String,
            contract_id: Int
    ) {


        this.name = name
        this.email = email
        this.password = password
        this.hourly_rate = hourly_rate
        this.contract_id = contract_id


    }


}
