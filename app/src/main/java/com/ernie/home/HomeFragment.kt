package com.ernie.home


import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ernie.AppDatabase
import com.ernie.R
import com.ernie.model.Entry
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import kotlinx.android.synthetic.main.fragment_home.*


private const val TAG = "HomeFragment"

class HomeFragment : Fragment() {


    private var currentListOfEntries: ArrayList<Entry>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appDatabase = AppDatabase.newInstance()
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

        // Piechart entries arraylist
        val pieEntries = ArrayList<PieEntry>()

        // Store pie entries in arraylist
        pieEntries.add(PieEntry(8f, "Base Pay"))
        pieEntries.add(PieEntry(2f, "Commission Pay"))
        pieEntries.add(PieEntry(5f, "Tip Pay"))

        // Set the piechart dataset to the arraylist
        val pieDataSet = PieDataSet(pieEntries, "")

        //Set the piedata to the data that will be rendered to the view
        val data = PieData(pieDataSet)
        pieChart.data = data

        //Set text size for values on piechart
        pieChart.data.setValueTextSize(20f)


        // Hide values of the piechart
        pieChart.data.dataSet.setDrawValues(false)
        pieChart.setDrawEntryLabels(false)

        // Set key to be horizontal
        val legend = pieChart.legend
        legend.verticalAlignment = Legend.LegendVerticalAlignment.CENTER
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        legend.orientation = Legend.LegendOrientation.VERTICAL
        legend.setDrawInside(false)

        //Create color palette
        val MY_COLORS = intArrayOf(Color.rgb(192, 0, 0),
                Color.rgb(255, 0, 0),
                Color.rgb(255, 192, 0),
                Color.rgb(127, 127, 127),
                Color.rgb(146, 208, 80),
                Color.rgb(0, 176, 80),
                Color.rgb(79, 129, 189))
        val colors = ArrayList<Int>()
        for (c in MY_COLORS) colors.add(c)

        // Set colors for piechart sections
        pieDataSet.colors = colors

        // Set piechart center text
        var currentTotalAmountEarned = 100
        pieChart.centerText = "£" + currentTotalAmountEarned
        pieChart.setCenterTextSize(14f)
        pieChart.setCenterTextColor(Color.BLUE)

        //Hide default description text
        pieChart.description.isEnabled = false

        //Set initial animation speed
        pieChart.animateXY(5000, 500)


    }


}
