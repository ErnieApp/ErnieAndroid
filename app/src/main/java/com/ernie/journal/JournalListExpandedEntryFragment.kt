package com.ernie.journal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ernie.AppDatabase
import com.ernie.R
import com.ernie.model.Entry

class JournalListExpandedEntryFragment(private val appDatabase: AppDatabase) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_journal_list_expanded_entry, container, false)



        return view
    }

    fun setEntry(entry: Entry) {
        currentEntry = entry
    }

    companion object {
        private var currentEntry: Entry? = null
    }
}
