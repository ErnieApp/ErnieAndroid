package com.ernie.journal

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ernie.AppDatabase
import com.ernie.R
import com.ernie.model.Entry

class JournalListAdapter(private var entryList: MutableList<Entry>, private val appDatabase: AppDatabase, private val journalListFragment: JournalListFragment) : RecyclerView.Adapter<JournalListAdapter.ViewHolder>() {
    private lateinit var recyclerView: RecyclerView
    private val selectedEntryViewHolders: MutableSet<ViewHolder> = mutableSetOf()
    private var isSelectionMode = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_journal_list_entry, parent, false)
        return ViewHolder(view)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val entry = entryList[position]

        viewHolder.positionInList = position
        viewHolder.entryDate.text = entry.date_recorded.toString()
        viewHolder.entryWorkedTime.text = entry.start_time + " - " + entry.end_time
        viewHolder.entryBreakHours.text = entry.break_duration.toString() + " minutes"
        viewHolder.entryEarned.text = "£" + entry.earned

        viewHolder.itemView.setOnClickListener {
            if (isSelectionMode) {
                if (selectedEntryViewHolders.contains(viewHolder)) {
                    removeHighlight(viewHolder)
                    selectedEntryViewHolders.remove(viewHolder)
                    journalListFragment.setSelectedAllCheckBoxStatus(false)
                } else {
                    highlightEntry(viewHolder)
                    selectedEntryViewHolders.add(viewHolder)
                    if (selectedEntryViewHolders.size == entryList.size) {
                        journalListFragment.setSelectedAllCheckBoxStatus(true)
                    }
                }
                viewHolder.checkBox.isChecked = !viewHolder.checkBox.isChecked
                journalListFragment.updateSelectionCountText(selectedEntryViewHolders.size)
            }
        }

        viewHolder.itemView.setOnLongClickListener {
            if (!isSelectionMode) {
                highlightEntry(viewHolder)
                viewHolder.itemView.findViewById<CheckBox>(R.id.checkBox).isChecked = true
                selectedEntryViewHolders.add(viewHolder)
                journalListFragment.updateSelectionCountText(selectedEntryViewHolders.size)
                toggleSelectionMode()
            } else {
                viewHolder.itemView.callOnClick()
            }
            true
        }

        if (isSelectionMode) {
            viewHolder.checkBox.visibility = View.VISIBLE
        } else {
            viewHolder.checkBox.visibility = View.GONE
            viewHolder.checkBox.isChecked = false
            journalListFragment.setSelectedAllCheckBoxStatus(false)
            removeHighlight(viewHolder)
        }
    }

    override fun getItemCount(): Int {
        return entryList.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var positionInList: Int = -1
        var entryDate: TextView = view.findViewById(R.id.entryDate)
        var entryEarned: TextView = view.findViewById(R.id.entryEarned)
        var entryBreakHours: TextView = view.findViewById(R.id.entryBreakHours)
        var entryWorkedTime: TextView = view.findViewById(R.id.entryWorkedTime)
        var checkBox: CheckBox = view.findViewById(R.id.checkBox)
    }

    fun updateRecords(el: MutableList<Entry>) {
        entryList = el
        notifyDataSetChanged()
    }

    fun setSelectAllEntries(selectAll: Boolean) {
        if (selectAll) {
            for (i in 0 until recyclerView.childCount) {
                val viewHolder = recyclerView.findViewHolderForAdapterPosition(i) as ViewHolder
                viewHolder.checkBox.isChecked = true
                highlightEntry(viewHolder)
                selectedEntryViewHolders.add(viewHolder)
            }
        } else {
            for (i in 0 until recyclerView.childCount) {
                val viewHolder = recyclerView.findViewHolderForAdapterPosition(i) as ViewHolder
                viewHolder.checkBox.isChecked = false
                removeHighlight(viewHolder)
            }
            selectedEntryViewHolders.clear()
        }
        journalListFragment.updateSelectionCountText(selectedEntryViewHolders.size)
    }

    fun deleteSelectedEntries() {
        var i = 0
        selectedEntryViewHolders.forEach { vh ->
            appDatabase.deleteEntry(entryList.removeAt(vh.positionInList - i))
            i++
        }
        toggleSelectionMode()
    }

    private fun toggleSelectionMode() {
        isSelectionMode = !isSelectionMode
        if (isSelectionMode) {
            journalListFragment.setSelectionModeContainerVisibility(View.VISIBLE)
        } else {
            journalListFragment.setSelectionModeContainerVisibility(View.GONE)
            selectedEntryViewHolders.clear()
        }
        notifyDataSetChanged()
    }

    private fun highlightEntry(viewHolder: ViewHolder) {
        viewHolder.itemView.setBackgroundColor(Color.rgb(230, 230, 230))
    }

    private fun removeHighlight(viewHolder: ViewHolder) {
        viewHolder.itemView.setBackgroundColor(Color.WHITE)
    }

    companion object {
        private val TAG = JournalListAdapter::class.simpleName
    }
}