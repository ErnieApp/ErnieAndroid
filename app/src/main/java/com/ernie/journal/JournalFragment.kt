package com.ernie.journal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.ernie.AppDatabase
import com.ernie.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_journal.*

class JournalFragment(appDatabase: AppDatabase) : Fragment() {
    private val journalListAddEntryFragment = JournalListAddEntryFragment(appDatabase)
    private val journalListAdapter = JournalListAdapter(appDatabase.getEntries(), appDatabase)
    private val journalListExpandedEntryFragment = JournalListExpandedEntryFragment(appDatabase)
    private val journalListFragment = JournalListFragment(appDatabase, journalListAdapter, journalListExpandedEntryFragment)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_journal, container, false)
        setupFloatingActionButtonListener(view.findViewById(R.id.floatingActionButton))
        showEntryList()
        journalListAdapter.setJournalListFragment(journalListFragment)
        return view
    }

    private fun setupFloatingActionButtonListener(button: FloatingActionButton) {
        button.setOnClickListener {
            if (journalListAddEntryFragment.isVisible) {
                showEntryList()
            } else {
                showEntryAddForm()
                if (journalListAdapter.isSelectionMode()) {
                    journalListAdapter.toggleSelectionMode()
                }
            }

            if (floatingActionButton.rotation != 45f) {
                floatingActionButton.rotation += 45
            } else {
                floatingActionButton.rotation -= floatingActionButton.rotation
            }
        }
    }

    fun showEntryList() {
        replaceCurrentFragment(R.id.journalFrameContainer, journalListFragment)
    }

    private fun showEntryAddForm() {
        replaceCurrentFragment(R.id.journalFrameContainer, journalListAddEntryFragment)
    }

    fun showExpandedEntry() {
        replaceCurrentFragment(R.id.journalFrameContainer, journalListExpandedEntryFragment)
    }

    private fun replaceCurrentFragment(@IdRes containerViewId: Int, fragment: Fragment) {
        val fragmentManager: FragmentManager = activity!!.supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(containerViewId, fragment)
        transaction.commit()
    }

    fun isAddEntryFormVisible(): Boolean {
        return journalListAddEntryFragment.isVisible
    }

    fun isExpandedEntryVisible(): Boolean {
        return journalListExpandedEntryFragment.isVisible
    }

    fun isSelectionMode(): Boolean {
        return journalListAdapter.isSelectionMode()
    }

    fun toggleSelectionMode() {
        journalListAdapter.toggleSelectionMode()
    }

    fun clickFloatingActionButton() {
        floatingActionButton.callOnClick()
    }

    companion object {
        private val TAG = JournalFragment::class.simpleName
    }
}
