package com.ernie


import android.util.Log
import com.ernie.model.Entry
import com.ernie.model.EntryData
import com.ernie.model.User

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore


private const val TAG = "Database"

class Database {


    val firestoreDB = FirebaseFirestore.getInstance()
    val currentFirebaseUser = FirebaseAuth.getInstance().currentUser
    var currentEntriesList = ArrayList<EntryData>()

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
    fun addEntry(entry: Entry) {
        // Create a new entry store it in a hashmap
        var firestoreEntry = hashMapOf(
                "date_recorded" to FieldValue.serverTimestamp(),
                "start_time" to entry.start_time,
                "end_time" to entry.end_time,
                "break_duration" to entry.break_duration,
                "earned" to entry.earned
        )
        // Store entry in the firestore entry collection
        firestoreDB.collection("users").document(currentFirebaseUser?.uid!!).collection("entries").add(firestoreEntry)

    }

    //COMPLETE
    fun getAllEntries() {

        val collectionPath = "/users/" + currentFirebaseUser?.uid!! + "/entries"

        firestoreDB.collection(collectionPath)
                .get()
                .addOnSuccessListener { result ->
                    this.currentEntriesList.clear()
                    this.currentEntriesList.addAll(result.toObjects(EntryData::class.java))

                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "Error getting documents: ", exception)
                }


    }

    fun numberOfEntries(): Int {
        return currentEntriesList.size
    }

    companion object {
        fun newInstance(): Database {
            return Database()
        }
    }
}


