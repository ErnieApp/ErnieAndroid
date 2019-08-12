package com.ernie.journal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ernie.AppDatabase
import com.ernie.R
import com.ernie.model.Entry
import kotlinx.android.synthetic.main.fragment_journal_list_add_entry.*
import java.io.Serializable


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private const val TAG = "JournalListFragment"


class JournalListAddEntryFragment : Fragment(), Serializable {

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
            val entry = Entry(entryStartTimeInputTextField.text.toString(), entryEndTimeInputTextField.text.toString(), entryBreakDurationInputTextField.text.toString().toInt(), entryEarnedInputTextField.text.toString().toInt())
            appDatabase?.addEntry(entry)
        }


    }
    companion object {
        fun newInstance(): JournalListAddEntryFragment {
            return JournalListAddEntryFragment()
        }
    }
}
