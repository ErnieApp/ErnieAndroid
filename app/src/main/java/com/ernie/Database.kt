package com.ernie


import com.ernie.model.Entry
import com.ernie.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore


private const val TAG = "Database"

class Database {


    val firestoreDB = FirebaseFirestore.getInstance()
    var currentFirebaseUser = FirebaseAuth.getInstance().currentUser


    fun addUser(user: User) {
        // Create a new user store it in a hashmap
        var firestoreUser = hashMapOf(
                "name" to user.name,
                "email" to user.email,
                "hour_rate" to user.hourly_rate,
                "contract" to 0
        )
        // Store user in firestore database
        firestoreDB.collection("users").document(currentFirebaseUser?.uid!!).set(firestoreUser)
    }


    fun addEntry(entry: Entry) {

        // Create a new entry store it in a hashmap
        var firestoreEntry = hashMapOf(
                "date_recorded" to FieldValue.serverTimestamp(),
                "start_time" to entry.start_time,
                "end_time" to entry.end_time,
                "break_duration" to entry.break_duration,
                "earned" to entry.earned
        )

        // Create users collection -> random token -> entries collection
        firestoreDB.collection("users").document(currentFirebaseUser?.uid!!).collection("entries").add(firestoreEntry)

    }

}
