package com.ernie.journal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ernie.AppDatabase
import com.ernie.R
import kotlinx.android.synthetic.main.fragment_journal_list_add_entry.*

class JournalListAddEntryFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_journal_list_add_entry, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnAddToEntryTable.setOnClickListener {
            appDatabase?.addEntry(entryStartTimeInputTextField.text.toString(), entryEndTimeInputTextField.text.toString(), entryBreakDurationInputTextField.text.toString().toInt(), entryEarnedInputTextField.text.toString().toInt())
        }
    }

    companion object {
        private const val TAG = "JournalListAddEntryFragment"
        private val appDatabase: AppDatabase? = AppDatabase()
    }
}
