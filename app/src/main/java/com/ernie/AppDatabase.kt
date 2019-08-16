package com.ernie


import android.util.Log
import com.ernie.model.Contract
import com.ernie.model.Entry
import com.ernie.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AppDatabase {

    private var firestoreDB = FirebaseFirestore.getInstance()
    private var currentEntriesList = ArrayList<Entry>()
    private var previousPayDateCached = ""
    private var upcomingPayDateCached = ""

    constructor() {
        loadEntriesFromFireStore()
        loadPreviousPayDateFromFireStore()
        loadUpcomingPayDateFromFireStore()
    }

    fun addUser(user: User) {
        val firestoreUser = hashMapOf(
                "name" to user.name,
                "email" to user.email,
                "hourly_rate" to user.hourly_rate,
                "previous_pay_date" to user.previousPayDate,
                "upcoming_pay_date" to user.upcomingPayDate
        )
        firestoreDB.collection("users").document(fireAuth.currentUser?.uid!!).set(firestoreUser)
    }

    fun setContract(contract: Contract) {
        for (contractedDay in contract.getContractedDays().values) {
            firestoreDB.collection("users").document(fireAuth.currentUser?.uid!!).collection("contract").document(contractedDay.day.toString()).set(hashMapOf(
                    "start" to contractedDay.startTime,
                    "end" to contractedDay.endTime
            ))
        }
    }

    fun addEntry(entry: Entry) {
        // Create a new document in firestore
        val newDocument = firestoreDB.collection("users").document(fireAuth.currentUser?.uid!!).collection("entries").document()
        val newDocumentId = newDocument.id

        // Create a new entry store it in a hashmap
        val firestoreEntry = hashMapOf(
                "id" to newDocumentId,
                "start_time" to entry.start_time,
                "end_time" to entry.end_time,
                "break_duration" to entry.break_duration,
                "earned" to entry.earned,
                "date_recorded" to entry.date_recorded
        )

        // Store entry in the firestore entry collection
        firestoreDB.collection("users").document(fireAuth.currentUser?.uid!!).collection("entries").document(newDocumentId).set(firestoreEntry)
    }

    fun deleteEntry(entry: Entry) {
        val collectionPath = "/users/" + fireAuth.currentUser?.uid!! + "/entries/"

        Log.d(TAG, "hello" + entry.id.toString())
        firestoreDB.collection(collectionPath)
                .document(entry.id!!)
                .delete()
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
    }


    fun updatePayDates(previousPayDateUserInput: String, upcomingPayDateUserInput: String) {
        val userEntryFieldsRef = firestoreDB.collection("users").document(fireAuth.currentUser?.uid!!)

        userEntryFieldsRef
                .update(mapOf(
                        "previous_pay_date" to previousPayDateUserInput,
                        "upcoming_pay_date" to upcomingPayDateUserInput
                ))
    }


    //COMPLETE
    private fun loadEntriesFromFireStore() {


        val collectionPath = "/users/" + fireAuth.currentUser?.uid!! + "/entries"


        firestoreDB.collection(collectionPath)
                .addSnapshotListener { value, e ->
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    this.currentEntriesList.clear()
                    this.currentEntriesList.addAll(value!!.toObjects(Entry::class.java))

                    Log.d(TAG, "LoadAllEntries")
                }
    }


    private fun loadPreviousPayDateFromFireStore() {

        val collectionPath = "/users/" + fireAuth.currentUser?.uid!!


        firestoreDB.document(collectionPath)
                .addSnapshotListener { value, e ->
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    if (value?.getString("previous_pay_date") != null) {
                        this.previousPayDateCached = value.getString("previous_pay_date")!!
                    }

                    Log.d(TAG, previousPayDateCached)
                }

    }


    private fun loadUpcomingPayDateFromFireStore() {

        val collectionPath = "/users/" + fireAuth.currentUser?.uid!!

        firestoreDB.document(collectionPath)
                .addSnapshotListener { value, e ->
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    if (value?.getString("previous_pay_date") != null) {
                        this.upcomingPayDateCached = value.getString("upcoming_pay_date")!!
                    }

                }

    }

    fun getEntries(): ArrayList<Entry> {
        return currentEntriesList
    }

    fun getPreviousPayDate(): String {
        return previousPayDateCached
    }

    fun getUpcomingPayDate(): String {
        return upcomingPayDateCached
    }


    companion object {
        private val fireAuth = FirebaseAuth.getInstance()
        private const val TAG = "AppDatabase"
    }


    //    fun addPreviousPayDate(previousPayDateUserInput: String) {
//
//
//        val currentUpComingDate = firestoreDB.collection("users").document(fireAuth.currentUser?.uid!!).get("upcoming_pay_date")
//
//        // Create a new entry store it in a hashmap
//        val firestorePreviousPayDates = hashMapOf(
//                "previous_pay_date" to previousPayDateUserInput
//                "upcoming_pay_date" to
//        )
//        // Store entry in the firestore entry collection
//        firestoreDB.collection("users").document(fireAuth.currentUser?.uid!!).set(firestorePreviousPayDates)
//        Log.d(TAG, "Stored previous paydate in firestore")
//    }
//
//    fun addUpcomingPayDate(upcomingPayDateUserInput: String) {
//        // Create a new entry store it in a hashmap
//        val firestoreUpcomingPayDates = hashMapOf(
//                "upcoming_pay_date" to upcomingPayDateUserInput
//        )
//        // Store entry in the firestore entry collection
//        firestoreDB.collection("users").document(fireAuth.currentUser?.uid!!).
//
//                set(firestoreUpcomingPayDates)
//        Log.d(TAG, "Stored upcoming paydate in firestore")
//    }
}
