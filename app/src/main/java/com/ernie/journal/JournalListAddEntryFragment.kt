package com.ernie.journal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ernie.AppDatabase
import com.ernie.R
import kotlinx.android.synthetic.main.fragment_journal_list_add_entry.*

private const val TAG = "JournalListFragment"


class JournalListAddEntryFragment : Fragment() {

    private var appDatabase: AppDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appDatabase = AppDatabase.newInstance()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_journal_list_add_entry, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnAddToEntryTable.setOnClickListener {
            appDatabase?.addEntry(entryStartTimeInputTextField.text.toString(), entryEndTimeInputTextField.text.toString(), entryBreakDurationInputTextField.text.toString().toInt(), entryEarnedInputTextField.text.toString().toInt())
        }
    }
}
