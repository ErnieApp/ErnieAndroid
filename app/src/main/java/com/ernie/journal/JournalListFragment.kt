package com.ernie.journal

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ernie.R
import com.ernie.model.Entry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.io.Serializable

private const val TAG = "JournalListFragment"

class JournalListFragment : Fragment(), Serializable {

    private val firestoreDB: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var firestoreListener: ListenerRegistration? = null
    private val currentFirebaseUser = FirebaseAuth.getInstance().currentUser
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_journal_list, container, false)

        val activity = activity as Context

        recyclerView = view.findViewById(R.id.journalRecyclerView) as RecyclerView
        recyclerView.layoutManager = GridLayoutManager(activity, 1)

        return view
    }

    override fun onStart() {
        super.onStart()

        Log.d("MERT", "on view created...")

//        loadEntriesList()

        val collectionPath = "/users/" + currentFirebaseUser?.uid!! + "/entries"

        firestoreListener = firestoreDB.collection(collectionPath)
                .addSnapshotListener(EventListener { documentSnapshots, e ->
                    if (e != null) {
                        Log.e("MERT", "Listen failed!", e)
                        return@EventListener
                    }

                    Log.d("MERT", "listener called...")

                    val entryList = mutableListOf<Entry>()

                    for (doc in documentSnapshots!!) {
                        val entry = doc.toObject(Entry::class.java)
                        entryList.add(entry)
                    }

                    if (recyclerView.adapter == null) {
                        recyclerView.adapter = JournalListAdapter(entryList, activity!!.applicationContext, firestoreDB)
                    } else {
                        (recyclerView.adapter as JournalListAdapter).updateRecords(entryList)
                    }
                })

    }

    override fun onDestroy() {
        super.onDestroy()

        firestoreListener!!.remove()
    }

}