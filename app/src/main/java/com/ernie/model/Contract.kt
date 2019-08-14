package com.ernie.model

class Contract {
    private val contractedDays: HashMap<Day, ContractedDay> = hashMapOf()

    fun addContractedDay(contractedDay: ContractedDay) {
        contractedDays[contractedDay.getDay()] = contractedDay
    }

    fun getContractedDays(): HashMap<Day, ContractedDay> {
        return contractedDays
    }
}