package com.ernie.model

class Entry {

    private var _id: Int = 0
    private var user_id: Int = 0
    private var data_recorded: String? = null
    private var start_time: String? = null
    private var end_time: String? = null
    private var break_duration: String? = null
    private var earned: String? = null

    constructor(_id: Int,
                user_id: Int,
                data_recorded: String,
                start_time: String,
                end_time: String,
                break_duration: String,
                earned: String
    ) {

        this._id = _id;
        this.user_id = user_id
        this.data_recorded = data_recorded
        this.start_time = start_time
        this.end_time = end_time
        this.break_duration = break_duration
        this.earned = earned


    }



}

