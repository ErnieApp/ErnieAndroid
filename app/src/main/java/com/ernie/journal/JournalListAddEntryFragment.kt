package com.ernie.journal

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
    private var listener: OnFragmentInteractionListener? = null

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

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
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
