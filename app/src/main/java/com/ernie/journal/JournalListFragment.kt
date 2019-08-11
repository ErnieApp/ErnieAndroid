package com.ernie.journal

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ernie.model.EntryData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.synthetic.main.fragment_journal_list.*
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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


                    journalRecyclerView.adapter = mAdapter
                })

        return super.onCreateView(inflater, container, savedInstanceState)
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