package com.ernie.journal

import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ernie.AppDatabase
import com.ernie.R


class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var entryDate: TextView = view.findViewById(R.id.entryDate)
    var entryEarned: TextView = view.findViewById(R.id.entryEarned)
    var entryBreakHours: TextView = view.findViewById(R.id.entryBreakHours)
    var entryWorkedTime: TextView = view.findViewById(R.id.entryWorkedTime)
}
class JournalListFragment : Fragment() {

    var dbHandler: AppDatabase? = null
    var entryCursor: Cursor? = null

    companion object {

        fun newInstance(): JournalListFragment {
            return JournalListFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHandler = context?.let { AppDatabase(it, null) }
        entryCursor = dbHandler!!.getAllEntries()
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
            if (entryCursor!!.position < entryCursor!!.count - 1) {
                entryCursor!!.moveToNext()
            }

            viewHolder.entryDate.text = entryCursor!!.getString(entryCursor!!.getColumnIndex("date_recorded"))
            Log.d("MERT", "Mashallah")
            viewHolder.entryWorkedTime.text = entryCursor!!.getString(entryCursor!!.getColumnIndex("start_time")) + " - " + entryCursor!!.getString(entryCursor!!.getColumnIndex("end_time"))
            Log.d("MERT", "Mashallah")
            viewHolder.entryBreakHours.text = entryCursor!!.getString(entryCursor!!.getColumnIndex("break_duration")) + " minutes"
            Log.d("MERT", "Mashallah")
            viewHolder.entryEarned.text = "£" + entryCursor!!.getString(entryCursor!!.getColumnIndex("earned"))
            viewHolder.itemView.setOnClickListener { System.out.println("Clicky") }

        }

        override fun getItemCount(): Int {
            return dbHandler!!.numberOfEntries()
        }
    }



}