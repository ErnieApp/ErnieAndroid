package com.ernie.journal

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ernie.R

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

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            val recyclerDogModelBinding = RecyclerItemDogModelBinding.inflate(layoutInflater,
                    viewGroup, false)
            return ViewHolder(recyclerDogModelBinding.root, recyclerDogModelBinding)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            val dog = DogModel(imageResIds[position], names[position],
                    descriptions[position], urls[position])
            viewHolder.setData(dog)
            viewHolder.itemView.setOnClickListener { listener.onDogSelected(dog) }
        }

        override fun getItemCount(): Int {
            return names.size
        }
    }

    internal inner class ViewHolder constructor(itemView: View,
                                                val recyclerItemDogListBinding:
                                                RecyclerItemDogModelBinding
    ) :
            RecyclerView.ViewHolder(itemView) {

        fun setData(dogModel: DogModel) {
            recyclerItemDogListBinding.dogModel = dogModel
        }
    }

}