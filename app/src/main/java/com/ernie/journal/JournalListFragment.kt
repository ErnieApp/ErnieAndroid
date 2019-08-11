package com.ernie.journal

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.ernie.model.EntryData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.io.Serializable

private const val TAG = "JournalListFragment"



class JournalListFragment : Fragment(), Serializable {

    private var mAdapter: JournalListAdapter? = null
    private var firestoreDB: FirebaseFirestore? = null
    private var firestoreListener: ListenerRegistration? = null
    private val currentFirebaseUser = FirebaseAuth.getInstance().currentUser



    private var journalFragment: JournalFragment? = null


    fun setJournalFragment(jf: JournalFragment) {
        journalFragment = jf
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firestoreDB = FirebaseFirestore.getInstance()


        val collectionPath = "/users/" + currentFirebaseUser?.uid!! + "/entries"
        val activity = activity as Context


        firestoreListener = firestoreDB!!.collection(collectionPath)
                .addSnapshotListener(EventListener { documentSnapshots, e ->
                    if (e != null) {
                        Log.e(TAG, "Listen failed!", e)
                        return@EventListener
                    }

                    val entryList = mutableListOf<EntryData>()

                    for (doc in documentSnapshots!!) {
                        val entry = doc.toObject(EntryData::class.java)
                        entryList.add(entry)
                    }

                    mAdapter = JournalListAdapter(entryList, activity, firestoreDB!!)


                    recyclerView.adapter = mAdapter
                })

    }





    override fun onDestroy() {
        super.onDestroy()

        firestoreListener!!.remove()
    }

    companion object {

        fun newInstance(): JournalListFragment {
            return JournalListFragment()
        }
    }




}