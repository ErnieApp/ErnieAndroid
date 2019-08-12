package com.ernie.journal

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.ernie.model.Entry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.synthetic.main.fragment_journal_list.*
import java.io.Serializable

private const val TAG = "JournalListFragment"


class JournalListFragment(mAdapter: JournalListAdapter?) : Fragment(), Serializable {


    private var firestoreDB: FirebaseFirestore? = null
    private var firestoreListener: ListenerRegistration? = null
    private val currentFirebaseUser = FirebaseAuth.getInstance().currentUser
    private var mAdapterr = mAdapter



    private var journalFragment: JournalFragment? = null


    fun setJournalFragment(jf: JournalFragment) {
        journalFragment = jf
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestoreDB = FirebaseFirestore.getInstance()


        val collectionPath = "/users/" + currentFirebaseUser?.uid!! + "/entries"
        val activity = activity as Context


        firestoreListener = firestoreDB!!.collection(collectionPath)
                .addSnapshotListener(EventListener { documentSnapshots, e ->
                    if (e != null) {
                        Log.e(TAG, "Listen failed!", e)
                        return@EventListener
                    }

                    val entryList = mutableListOf<Entry>()

                    for (doc in documentSnapshots!!) {
                        val entry = doc.toObject(Entry::class.java)
                        entryList.add(entry)
                    }

                    
                    journalRecyclerView.adapter = mAdapterr
                })

    }



    override fun onDestroy() {
        super.onDestroy()

        firestoreListener!!.remove()
    }

}