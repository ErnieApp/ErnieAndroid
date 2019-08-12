package com.ernie.journal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ernie.R
import kotlinx.android.synthetic.main.fragment_journal_list_add_entry.*
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



        btnAddToEntryTable.setOnClickListener {
            //TODO: Add logic to add an entry to firestore db
        }


    }
    companion object {
        fun newInstance(): JournalListAddEntryFragment {
            return JournalListAddEntryFragment()
        }
    }
}
