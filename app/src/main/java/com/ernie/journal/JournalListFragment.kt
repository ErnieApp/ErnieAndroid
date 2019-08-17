package com.ernie.journal

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ernie.AppDatabase
import com.ernie.R
import com.ernie.model.Entry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.synthetic.main.fragment_journal_list.*

private lateinit var snapShotListenerRegistration: ListenerRegistration
class JournalListFragment : Fragment() {

    private val firestoreDB = FirebaseFirestore.getInstance()
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_journal_list, container, false)

        recyclerView = view.findViewById(R.id.journalRecyclerView) as RecyclerView
        recyclerView.adapter = JournalListAdapter(mutableListOf(), appDatabase!!, this)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        val selectAllCheckBox = view.findViewById(R.id.selectAllCheckBox) as CheckBox
        selectAllCheckBox.setOnClickListener {
            (recyclerView.adapter as JournalListAdapter).setSelectAllEntries(selectAllCheckBox.isChecked)
        }

        val deleteButton = view.findViewById(R.id.deleteIcon) as ImageView
        deleteButton.setOnClickListener {
            (recyclerView.adapter as JournalListAdapter).deleteSelectedEntries()
        }

        return view
    }

    override fun onPause() {
        super.onPause()
        snapShotListenerRegistration.remove()
    }

    override fun onStart() {
        super.onStart()
        addSnapshotListener()
    }

    private fun addSnapshotListener() {
        val collectionPath = "/users/" + FirebaseAuth.getInstance().currentUser?.uid + "/entries"
        snapShotListenerRegistration = firestoreDB.collection(collectionPath)
                .addSnapshotListener(EventListener { documents, e ->
                    if (e != null) {
                        Log.e(TAG, "Snapshot listener failed")
                        return@EventListener
                    } else {
                        (recyclerView.adapter as JournalListAdapter).updateRecords(documents!!.toObjects(Entry::class.java))
                    }
                })
    }

    fun setSelectionModeContainerVisibility(value: Int) {
        selectionModeContainer.visibility = value
    }

    fun updateSelectionCountText(count: Int) {
        selectionCount.text = count.toString() + " selected"
    }

    fun setSelectedAllCheckBoxStatus(value: Boolean) {
        (activity!!.findViewById(R.id.selectAllCheckBox) as CheckBox).isChecked = value
    }

    companion object {
        private val TAG = JournalListFragment::class.simpleName
        private val appDatabase: AppDatabase? = AppDatabase()
    }
}

