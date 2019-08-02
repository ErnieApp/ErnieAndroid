package com.ernie.journal

import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ernie.AppDatabase
import com.ernie.R
import java.io.Serializable


class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var entryDate: TextView = view.findViewById(R.id.entryDate)
    var entryEarned: TextView = view.findViewById(R.id.entryEarned)
    var entryBreakHours: TextView = view.findViewById(R.id.entryBreakHours)
    var entryWorkedTime: TextView = view.findViewById(R.id.entryWorkedTime)
}

class JournalListFragment : Fragment(), Serializable {

    private var journalFragment: JournalFragment? = null

    var dbHandler: AppDatabase? = null
    var entryCursor: Cursor? = null
    var date_recordedIndex: Int = 0
    var start_timeIndex: Int = 0
    var end_timeIndex: Int = 0
    var break_durationIndex: Int = 0
    var earnedIndex: Int = 0

    companion object {

        fun newInstance(): JournalListFragment {
            return JournalListFragment()
        }
    }

    fun setJournalFragment(jf: JournalFragment) {
        journalFragment = jf
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHandler = context?.let { AppDatabase(it, null) }
        entryCursor = dbHandler!!.getAllEntries()
        entryCursor!!.moveToNext()

        date_recordedIndex = entryCursor!!.getColumnIndex("date_recorded")
        start_timeIndex = entryCursor!!.getColumnIndex("start_time")
        end_timeIndex = entryCursor!!.getColumnIndex("end_time")
        break_durationIndex = entryCursor!!.getColumnIndex("break_duration")
        earnedIndex = entryCursor!!.getColumnIndex("earned")
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_journal_list, container, false)
        val activity = activity as Context
        val recyclerView = view.findViewById<RecyclerView>(R.id.journalRecyclerView) as RecyclerView
        recyclerView.layoutManager = GridLayoutManager(activity, 1)
        recyclerView.adapter = JournalListAdapter(activity)
        return view
    }

    internal inner class JournalListAdapter(context: Context) : RecyclerView.Adapter<ViewHolder>() {

        private val layoutInflater: LayoutInflater

        init {
            layoutInflater = LayoutInflater.from(context)
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_journal_list_entry, parent, false)
            return ViewHolder(view)
        }


        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            viewHolder.entryDate.text = entryCursor!!.getString(date_recordedIndex)
            viewHolder.entryWorkedTime.text = entryCursor!!.getString(start_timeIndex) + " - " + entryCursor!!.getString(end_timeIndex)
            viewHolder.entryBreakHours.text = entryCursor!!.getString(break_durationIndex) + " minutes"
            viewHolder.entryEarned.text = "£" + entryCursor!!.getString(earnedIndex)
            viewHolder.itemView.setOnClickListener {
                journalFragment!!.displayFragmentC()
            }

            if (entryCursor!!.position < entryCursor!!.count) {
                entryCursor!!.moveToNext()
            }
        }

        override fun getItemCount(): Int {
            return dbHandler!!.numberOfEntries()
        }
    }



}