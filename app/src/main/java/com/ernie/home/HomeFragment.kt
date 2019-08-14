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


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


private const val TAG = "HomeFragment"
/**
 * A simple [Fragment] subclass.
 *
 */
class HomeFragment : Fragment() {


    private var currentListOfEntries: ArrayList<Entry>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appDatabase = AppDatabase()
        currentListOfEntries = appDatabase.getEntries()


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
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


}
