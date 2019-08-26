package com.ernie

import android.util.Log
import com.ernie.model.Contract
import com.ernie.model.Entry
import com.ernie.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import java.io.Serializable

class AppDatabase(fireAuth: FirebaseAuth) : Serializable {

    private var currentEntriesList = ArrayList<Entry>()
    private var currentContract = ArrayList<Contract>()
    private var currentUserCached: User? = null

    init {
        firebaseAuth = fireAuth
        if (firebaseAuth!!.currentUser != null) {
            val collectionPath = "/users/" + firebaseAuth!!.currentUser?.uid
            entriesCollectionReference = firestoreDB.collection(collectionPath + "/entries")
            contractCollectionReference = firestoreDB.collection(collectionPath + "/contract")
            userDocumentReference = firestoreDB.document(collectionPath)
        }
    }

    fun updatePayDates(previousPayDateUserInput: String, upcomingPayDateUserInput: String) {
        val userEntryFieldsRef = firestoreDB.collection("users").document(firebaseAuth!!.currentUser?.uid!!)

        userEntryFieldsRef
                .update(mapOf(
                        "previous_pay_date" to previousPayDateUserInput,
                        "upcoming_pay_date" to upcomingPayDateUserInput
                ))
    }

    fun addListeners() {

        Log.d(TAG, "addListeners")
        addEntriesListener()
        addContractListener()
        addUserListener()
    }

    fun removeListeners() {
        Log.d(TAG, "removeListeners")
        entriesSnapshotListener.remove()
        contractSnapshotListener.remove()
        userSnapshotListener.remove()
    }

    fun getEntriesCollectionReference(): CollectionReference {
        return entriesCollectionReference
    }

    fun getContractCollectionReference(): CollectionReference {
        return contractCollectionReference
    }

    fun getUserDocumentReference(): DocumentReference {
        return userDocumentReference
    }

    private fun addEntriesListener() {
        entriesSnapshotListener = entriesCollectionReference
                .addSnapshotListener(EventListener { documents, e ->
                    if (e != null) {
                        Log.e(TAG, "Snapshot listener failed")
                        return@EventListener
                    } else {
                        this.currentEntriesList.clear()
                        this.currentEntriesList.addAll(documents!!.toObjects(Entry::class.java))
                    }
                })
    }

    private fun addContractListener() {
        contractSnapshotListener = contractCollectionReference
                .addSnapshotListener(EventListener { documents, e ->
                    if (e != null) {
                        Log.e(TAG, "Snapshot listener failed")
                        return@EventListener
                    } else {
                        currentContract.clear()
                        //TODO: check if casting to Contract works
                        currentContract.addAll(documents!!.toObjects(Contract::class.java))
                    }
                })
    }

    private fun addUserListener() {
        userSnapshotListener = userDocumentReference
                .addSnapshotListener(EventListener { userDoc, e ->
                    if (e != null) {
                        Log.e(TAG, "Snapshot listener failed")
                        return@EventListener
                    } else {
                        Log.d(TAG, "currentUserCached IS DEFO NOT EMPTY")
                        currentUserCached = userDoc!!.toObject(User::class.java)
                    }
                })
    }

    fun getEntries(): ArrayList<Entry> {
        return currentEntriesList
    }

    fun getPreviousPayDate(): String {
        Log.d(TAG, "getPreviousPayDate CALLED")
        Log.d(TAG, "value of blah = " + currentUserCached!!.email!!)
        return currentUserCached!!.previous_pay_date!!
    }

    fun getUpcomingPayDate(): String {
        return currentUserCached!!.upcoming_pay_date!!
    }

    fun getHourlyRate(): String {
        return currentUserCached!!.hourly_rate!!
    }

    fun getFireAuthInstance(): FirebaseAuth? {
        return firebaseAuth
    }

    companion object {
        private val TAG = AppDatabase::class.simpleName
        private var firebaseAuth: FirebaseAuth? = null
        private var firestoreDB = FirebaseFirestore.getInstance()

        private lateinit var entriesCollectionReference: CollectionReference
        private lateinit var entriesSnapshotListener: ListenerRegistration
        private lateinit var contractCollectionReference: CollectionReference
        private lateinit var contractSnapshotListener: ListenerRegistration
        private lateinit var userDocumentReference: DocumentReference
        private lateinit var userSnapshotListener: ListenerRegistration

        fun getAuthInstance(): FirebaseAuth? {
            return firebaseAuth
        }

        fun addUser(user: User): Task<Void> {
            val firestoreUser = hashMapOf(
                    "name" to user.name,
                    "email" to user.email,
                    "hourly_rate" to user.hourly_rate,
                    "previous_pay_date" to user.previous_pay_date,
                    "upcoming_pay_date" to user.upcoming_pay_date
            )
            return firestoreDB.collection("users").document(firebaseAuth!!.currentUser?.uid!!).set(firestoreUser)
        }

        fun setContract(contract: Contract) {
            for (contractedDay in contract.getContractedDays().values) {
                firestoreDB.collection("users").document(firebaseAuth!!.currentUser?.uid!!).collection("contract").document(contractedDay.day.toString()).set(hashMapOf(
                        "start" to contractedDay.startTime,
                        "end" to contractedDay.endTime
                ))
            }
        }

        fun addEntry(entry: Entry) {
            // Create a new document in firestore
            val newDocument = entriesCollectionReference.document()
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
            entriesCollectionReference.document(newDocumentId).set(firestoreEntry)
        }

        fun addEntries(entries: List<Entry>) {
            entries.forEach { entry ->
                addEntry(entry)
            }
        }

        fun deleteEntry(entry: Entry) {
            entriesCollectionReference
                    .document(entry.id!!)
                    .delete()
                    .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
                    .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
        }

        fun deleteEntries(entries: List<Entry>) {
            val writeBatch = firestoreDB.batch()

            entries.forEach { entry ->
                writeBatch.delete(entriesCollectionReference.document(entry.id!!))
            }

            writeBatch.commit()
                    .addOnSuccessListener { Log.d(TAG, "WriteBatch successfully committed!") }
                    .addOnFailureListener { e -> Log.w(TAG, "Error deleting in a batch", e) }
        }
    }
}
