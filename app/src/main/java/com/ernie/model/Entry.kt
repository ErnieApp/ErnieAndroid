package com.ernie.model

class Entry {

    var _id: Int = 0
    var user_id: Int = 0
    var date_recorded: String? = null
    var start_time: String? = null
    var end_time: String? = null
    var break_duration: Int = 0
    var earned: Int = 0

    constructor(_id: Int,
                user_id: Int,
                date_recorded: String,
                start_time: String,
                end_time: String,
                break_duration: Int,
                earned: Int
    ) {

        this._id = _id
        this.user_id = user_id
        this.date_recorded = date_recorded
        this.start_time = start_time
        this.end_time = end_time
        this.break_duration = break_duration
        this.earned = earned


    }


    constructor(
            start_time: String,
            end_time: String,
            break_duration: Int,
            earned: Int
    ) {

        this.start_time = start_time
        this.end_time = end_time
        this.break_duration = break_duration
        this.earned = earned


    }


}

