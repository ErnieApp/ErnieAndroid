package com.ernie.journal

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ernie.R


class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var entryDate: TextView = view.findViewById(R.id.entryDate)
    var entryEarned: TextView = view.findViewById(R.id.entryEarned)
    var entryBreakHours: TextView = view.findViewById(R.id.entryBreakHours)
    var entryWorkedTime: TextView = view.findViewById(R.id.entryWorkedTime)
}
class JournalListFragment : Fragment() {

    companion object {

        fun newInstance(): JournalListFragment {
            return JournalListFragment()
        }
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
            viewHolder.entryDate.setText("26/05/2019")
            viewHolder.entryWorkedTime.setText("10:30 - 18:00")
            viewHolder.entryBreakHours.setText("50 minutes")
            viewHolder.entryEarned.setText("$5000")
            viewHolder.itemView.setOnClickListener { System.out.println("Clicky") }
        }

        override fun getItemCount(): Int {
            return 10
        }
    }



}