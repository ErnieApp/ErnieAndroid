package com.ernie


import android.util.Log
import com.ernie.model.Entry
import com.ernie.model.User
import com.google.firebase.Timestamp

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList


private const val TAG = "Database"

class AppDatabase {


    private var firestoreDB = FirebaseFirestore.getInstance()
    private var currentFirebaseUser = FirebaseAuth.getInstance().currentUser
    private val currentEntriesList = ArrayList<Entry>()


    val currentNumberOfEntries = 0

    constructor() {
        // loadAllEntriesFromFireStore()
    }

    //COMPLETE
    fun addUser(user: User) {
        // Create a new user store it in a hashmap
        val firestoreUser = hashMapOf(
                "name" to user.name,
                "email" to user.email,
                "hour_rate" to user.hourly_rate,
                "contract" to 0
        )
        // Store user in the firestore user collection
        firestoreDB.collection("users").document(currentFirebaseUser?.uid!!).set(firestoreUser)
    }



    //COMPLETE
    fun addEntry(userInputStartTime: String, userInputEndTime: String, userInputBreakDuration: Int, userInputEarned: Int) {


        // Create a new document in firestore
        val newDocument = firestoreDB.collection("users").document(currentFirebaseUser?.uid!!).collection("entries").document()
        val newDocumentId = newDocument.id

        //Create a a timestamp
        val newDataRecord = Timestamp(Date()).toDate()

        //Create an entry object
        val newEntry = Entry(newDocumentId, userInputStartTime, userInputEndTime, userInputBreakDuration, userInputEarned, newDataRecord)


        // Create a new entry store it in a hashmap
        var firestoreEntry = hashMapOf(
                "id" to newEntry.id,
                "start_time" to newEntry.start_time,
                "end_time" to newEntry.end_time,
                "break_duration" to newEntry.break_duration,
                "earned" to newEntry.earned,
                "date_recorded" to newEntry.date_recorded
        )

        // Store entry in the firestore entry collection
        firestoreDB.collection("users").document(currentFirebaseUser?.uid!!).collection("entries").document(newDocumentId).set(firestoreEntry)

    }


    fun deleteEntry(entry: Entry) {

        val collectionPath = "/users/" + currentFirebaseUser?.uid!! + "/entries/"

        Log.d(TAG, "hello" + entry.id.toString())
        firestoreDB.collection(collectionPath)
                .document(entry.id!!)
                .delete()
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
    }


    //COMPLETE
    fun loadAllEntriesFromFireStore() {

        val collectionPath = "/users/" + currentFirebaseUser?.uid!! + "/entries"

        firestoreDB.collection(collectionPath)
                .get()
                .addOnSuccessListener { result ->

                    this.currentEntriesList.clear()
                    this.currentEntriesList.addAll(result.toObjects(Entry::class.java))


                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "Error getting documents: ", exception)
                }

    }


    fun getEntries(): ArrayList<Entry> {
        return currentEntriesList
    }

    companion object {
        fun newInstance(): AppDatabase {
            return AppDatabase()
        }
    }
}


