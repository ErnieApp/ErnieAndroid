package com.ernie.journal

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.ernie.AppDatabase
import com.ernie.R
import com.ernie.model.Entry


private const val TAG = "JournalListAdapter"

class JournalListAdapter(el: MutableList<Entry>, c: Context, appDatabase: AppDatabase) : RecyclerView.Adapter<JournalListAdapter.ViewHolder>() {


    private var entryList: MutableList<Entry> = el
    private val context: Context = c
    private var appDatabase: AppDatabase = appDatabase


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
            Log.d(TAG, "CLICKED")
            Log.d(TAG, "CLICKED")
            Log.d(TAG, "ADAPTER" + entry.id)
            //TODO Implement vertical swipe for delete functionality and update firestore
            appDatabase.deleteEntry(entry)
            entryList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, entryList.size)

            Toast.makeText(context, "Note has been deleted!", Toast.LENGTH_SHORT).show()
            notifyDataSetChanged()
        }

    }


    override fun getItemCount(): Int {
        Log.d("MERT", "NUMBER OF ITEM IN ADAPTER" + entryList.size)
        return entryList.size
    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal var entryDate: TextView
        internal var entryEarned: TextView
        internal var entryBreakHours: TextView
        internal var entryWorkedTime: TextView

        init {
            entryDate = view.findViewById(R.id.entryDate)
            entryEarned = view.findViewById(R.id.entryEarned)

            entryBreakHours = view.findViewById(R.id.entryBreakHours)
            entryWorkedTime = view.findViewById(R.id.entryWorkedTime)
        }
    }


    fun updateRecords(el: MutableList<Entry>) {
        Log.d("MERT", "updating records...")
        entryList = el
        notifyDataSetChanged()
    }
}