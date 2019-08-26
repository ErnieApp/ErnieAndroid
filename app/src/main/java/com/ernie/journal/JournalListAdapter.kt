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
import com.google.android.material.snackbar.Snackbar

class JournalListAdapter(private var entryList: ArrayList<Entry>, private val appDatabase: AppDatabase) : RecyclerView.Adapter<JournalListAdapter.ViewHolder>() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var journalListFragment: JournalListFragment
    private val selectedEntryViewHolders: MutableMap<String, ViewHolder> = mutableMapOf()
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

        viewHolder.entryID = entry.id!!
        viewHolder.entryDate.text = entry.date_recorded.toString()
        viewHolder.entryWorkedTime.text = entry.start_time.plus(" - ").plus(entry.end_time)
        viewHolder.entryBreakHours.text = entry.break_duration.toString().plus(" minutes")
        viewHolder.entryEarned.text = "£".plus(entry.earned)

        viewHolder.itemView.setOnClickListener {
            if (isSelectionMode) {
                if (selectedEntryViewHolders.contains(viewHolder.entryID)) {
                    removeHighlight(viewHolder)
                    selectedEntryViewHolders.remove(viewHolder.entryID)
                    journalListFragment.setSelectedAllCheckBoxStatus(false)
                } else {
                    highlightEntry(viewHolder)
                    selectedEntryViewHolders[viewHolder.entryID] = viewHolder
                    if (selectedEntryViewHolders.size == entryList.size) {
                        journalListFragment.setSelectedAllCheckBoxStatus(true)
                    }
                }
                viewHolder.checkBox.isChecked = !viewHolder.checkBox.isChecked
                journalListFragment.updateSelectionCountText(selectedEntryViewHolders.size)
            } else {
                journalListFragment.expandEntry(entryList.findLast { entry -> entry.id.equals(viewHolder.entryID) }!!)
            }
        }

        viewHolder.itemView.setOnLongClickListener {
            //TODO: Seems to highlight wrong entry when long click
            if (!isSelectionMode) {
                highlightEntry(viewHolder)
                viewHolder.itemView.findViewById<CheckBox>(R.id.checkBox).isChecked = true
                selectedEntryViewHolders[viewHolder.entryID] = viewHolder
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
        var entryID: String = ""
        var entryDate: TextView = view.findViewById(R.id.entryDate)
        var entryEarned: TextView = view.findViewById(R.id.entryEarned)
        var entryBreakHours: TextView = view.findViewById(R.id.entryBreakHours)
        var entryWorkedTime: TextView = view.findViewById(R.id.entryWorkedTime)
        var checkBox: CheckBox = view.findViewById(R.id.checkBox)
    }

    fun notifyEntryListChanged() {
        notifyDataSetChanged()
    }

    fun setSelectAllEntries(selectAll: Boolean) {
        if (selectAll) {
            for (i in 0 until recyclerView.childCount) {
                val viewHolder = recyclerView.findViewHolderForAdapterPosition(i) as ViewHolder
                viewHolder.checkBox.isChecked = true
                highlightEntry(viewHolder)
                selectedEntryViewHolders[viewHolder.entryID] = viewHolder
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
        val removedEntries = mutableListOf<Entry>()
        entryList.forEach { entry ->
            if (selectedEntryViewHolders.containsKey(entry.id)) {
                removedEntries.add(entry)
            }
        }
        AppDatabase.deleteEntries(removedEntries)

        val snackbar = Snackbar.make(journalListFragment.view!!, "Successfully deleted", Snackbar.LENGTH_LONG)
                .setAction("Undo") {
                    AppDatabase.addEntries(removedEntries)
                }

        snackbar.show()
        toggleSelectionMode()
    }

    fun isSelectionMode(): Boolean {
        return isSelectionMode
    }

    fun toggleSelectionMode() {
        isSelectionMode = !isSelectionMode
        if (isSelectionMode) {
            journalListFragment.setSelectionModeContainerVisibility(View.VISIBLE)
        } else {
            journalListFragment.setSelectionModeContainerVisibility(View.GONE)
            selectedEntryViewHolders.clear()
        }
        notifyEntryListChanged()
    }

    fun setJournalListFragment(jlf: JournalListFragment) {
        journalListFragment = jlf
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