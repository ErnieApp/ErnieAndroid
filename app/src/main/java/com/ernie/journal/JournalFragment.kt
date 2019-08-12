package com.ernie.journal

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ernie.R
import com.ernie.model.Entry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_journal.*


private const val TAG = "JournalFragment"

class JournalFragment : Fragment() {

    private lateinit var journalListAddEntryFragment: JournalListAddEntryFragment
    private lateinit var journalListFragment: JournalListFragment
    private lateinit var journalListExpandedEntryFragment: JournalListExpandedEntryFragment

    private var mAdapter: JournalListAdapter? = null
    private var firestoreDB = FirebaseFirestore.getInstance()
    private val currentFirebaseUser = FirebaseAuth.getInstance().currentUser


    companion object {

        fun newInstance(): JournalFragment {
            return JournalFragment()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            loadEntriesList()
            journalListFragment = JournalListFragment(mAdapter)
            journalListAddEntryFragment = JournalListAddEntryFragment.newInstance()
            journalListExpandedEntryFragment = JournalListExpandedEntryFragment.newInstance()
            journalListFragment.setJournalFragment(this)
        } else {
            journalListFragment = savedInstanceState.get("entryList") as JournalListFragment
            journalListAddEntryFragment = savedInstanceState.get("entryAddForm") as JournalListAddEntryFragment
            journalListExpandedEntryFragment = savedInstanceState.get("entryExpanded") as JournalListExpandedEntryFragment
        }


    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("entryList", journalListFragment)
        outState.putSerializable("entryAddForm", journalListAddEntryFragment)
        outState.putSerializable("entryExpanded", journalListExpandedEntryFragment)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_journal, container, false)


        displayFragmentA()

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        floatingActionButton.setOnClickListener { view ->
            displayFragmentB()
        }
        //Begin Transcation

    }


    protected fun displayFragmentA() {
        val ft = childFragmentManager.beginTransaction()

        if (journalListFragment.isAdded) {

            ft.show(journalListFragment)
        } else {
            ft.add(R.id.fragmentContainer, journalListFragment, "journalListFragment")
        }
        // Hide fragment
        if (journalListAddEntryFragment.isAdded) {
            ft.hide(journalListAddEntryFragment)
        }
        if (journalListExpandedEntryFragment.isAdded) {
            ft.hide(journalListExpandedEntryFragment)
        }

        // Commit changes
        ft.commit()
    }


    protected fun displayFragmentB() {
        val ft = childFragmentManager.beginTransaction()

        if (journalListAddEntryFragment.isAdded) {

            ft.show(journalListAddEntryFragment)
        } else {

            ft.add(R.id.fragmentContainer, journalListAddEntryFragment, "journalListFragment")

        }
        // Hide fragment
        if (journalListFragment.isAdded) {
            ft.hide(journalListFragment)
        }
        if (journalListExpandedEntryFragment.isAdded) {
            ft.hide(journalListExpandedEntryFragment)
        }

        // Commit changes
        ft.commit()
    }

    fun displayFragmentC() {
        val ft = childFragmentManager.beginTransaction()

        if (journalListExpandedEntryFragment.isAdded) {

            ft.show(journalListExpandedEntryFragment)
        } else {

            ft.add(R.id.fragmentContainer, journalListExpandedEntryFragment, "journalListFragment")

        }
        // Hide fragment
        if (journalListFragment.isAdded) {
            ft.hide(journalListFragment)
        }
        if (journalListAddEntryFragment.isAdded) {
            ft.hide(journalListAddEntryFragment)
        }

        // Commit changes
        ft.commit()
    }


    private fun loadEntriesList() {

        val collectionPath = "/users/" + currentFirebaseUser?.uid!! + "/entries"

        val activity = activity as Context

        firestoreDB.collection(collectionPath)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val entryList = mutableListOf<Entry>()

                        for (doc in task.result!!) {
                            val entry = doc.toObject<Entry>(Entry::class.java)
                            entryList.add(entry)
                        }

                        mAdapter = JournalListAdapter(entryList, activity, firestoreDB)


                    } else {
                        Log.d(TAG, "Error getting documents: ", task.exception)
                    }
                }
    }

}
