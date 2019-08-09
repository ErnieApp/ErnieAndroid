package com.ernie.journal

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ernie.AppDatabase
import com.ernie.R
import com.ernie.model.Entry
import kotlinx.android.synthetic.main.fragment_journal_list_add_entry.*
import kotlinx.android.synthetic.main.fragment_profile.tvDisplayName
import java.io.Serializable

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private const val TAG = "JournalListFragment"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [JournalListAddEntryFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [JournalListAddEntryFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class JournalListAddEntryFragment : Fragment(), Serializable {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_journal_list_add_entry, container, false)
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnShowDatafromEntryTable.setOnClickListener {
            tvDisplayName.text = ""
            val dbHandler = AppDatabase(this.activity!!, null)
            val cursor = dbHandler.getAllEntries()
            cursor!!.moveToFirst()
            tvDisplayName.append((cursor.getString(cursor.getColumnIndex(AppDatabase.COLUMN_START))))
            while (cursor.moveToNext()) {
                tvDisplayName.append((cursor.getString(cursor.getColumnIndex(AppDatabase.COLUMN_START))))
                tvDisplayName.append("\n")
            }
            cursor.close()

            Log.d(TAG, "btnShowDatafromEntryTable method FINISH EXECUTION")
        }

        btnAddToEntryTable.setOnClickListener {
            val dbHandler = AppDatabase(this.activity!!, null)

            val entry = Entry(entryStartTimeInputTextField.text.toString(), entryEndTimeInputTextField.text.toString(), entryBreakDurationInputTextField.text.toString().toInt(), entryEarnedInputTextField.text.toString().toInt())
            dbHandler.addEntry(entry)
            Toast.makeText(this.activity!!, entryStartTimeInputTextField.text.toString() + "Added to database", Toast.LENGTH_LONG).show()

            Log.d(TAG, "btnAddToEntryTable FINISH EXECUTION")
        }


    }
    companion object {
        fun newInstance(): JournalListAddEntryFragment {
            return JournalListAddEntryFragment()
        }
    }
}
