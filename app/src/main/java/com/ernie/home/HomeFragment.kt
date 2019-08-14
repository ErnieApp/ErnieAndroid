package com.ernie.home


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ernie.AppDatabase
import com.ernie.R
import com.ernie.model.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {


    private var currentListOfEntries: ArrayList<Entry>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentListOfEntries = appDatabase.getEntries()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPieChart()
    }

    private fun setupPieChart() {

        Log.d(TAG, "addDataSet started")
        val entries = ArrayList<PieEntry>()

        entries.add(PieEntry(8f, 0))
        entries.add(PieEntry(2f, 1))
        entries.add(PieEntry(5f, 2))

        val pieDataSet = PieDataSet(entries, "Cells")

        val labels = ArrayList<String>()

        labels.add("18-Jan")
        labels.add("19-Jan")
        labels.add("20-Jan")

        val data = PieData(pieDataSet)

        pieChart.data = data
    }

    companion object {
        private const val TAG = "HomeFragment"
        private val appDatabase = AppDatabase()
    }
}
