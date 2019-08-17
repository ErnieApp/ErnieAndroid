package com.ernie.journal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ernie.AppDatabase
import com.ernie.R
import com.ernie.model.Entry

class JournalListAdapter(private var entryList: MutableList<Entry>, private val appDatabase: AppDatabase) : RecyclerView.Adapter<JournalListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_journal_list_entry, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val entry = entryList[position]

        viewHolder.entryDate.text = entry.date_recorded.toString()
        viewHolder.entryWorkedTime.text = entry.start_time + " - " + entry.end_time
        viewHolder.entryBreakHours.text = entry.break_duration.toString() + " minutes"
        viewHolder.entryEarned.text = "£" + entry.earned

        viewHolder.itemView.setOnClickListener {
            //TODO Implement vertical swipe for delete functionality and update firestore
            AppDatabase.deleteEntry(entry)
            entryList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, entryList.size)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return entryList.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var entryDate: TextView = view.findViewById(R.id.entryDate)
        var entryEarned: TextView = view.findViewById(R.id.entryEarned)
        var entryBreakHours: TextView = view.findViewById(R.id.entryBreakHours)
        var entryWorkedTime: TextView = view.findViewById(R.id.entryWorkedTime)
    }

    fun updateRecords(el: MutableList<Entry>) {
        entryList = el
        notifyDataSetChanged()
    }

    companion object {
        private const val TAG = "JournalListAdapter"
    }
}