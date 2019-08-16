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
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : Fragment() {


    private lateinit var previousPayDate: String
    private lateinit var upcomingPayDate: String
    private lateinit var currentListOfEntries: ArrayList<Entry>

    // Piechart entries arraylist
    private var pieEntries = ArrayList<PieEntry>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentListOfEntries = appDatabase.getEntries()
        previousPayDate = appDatabase.getPreviousPayDate()
        upcomingPayDate = appDatabase.getUpcomingPayDate()

        Log.d(TAG, previousPayDate)
        Log.d(TAG, upcomingPayDate)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpPayDayTimer()
        setUpPayDayDate()
        setUpPieChart()

    }


    private fun setUpPayDayTimer() {

    }


    private fun setUpPayDayDate() {
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date())
        payday_date_textview.text = currentDate
    }


    private fun setUpPieChart() {

        if (previousPayDate == "" && upcomingPayDate == "") {
            setUpPieChart0()
        } else {
            // SET UP DATA FOR PIECHART
            setPieChartEntries1()
        }


        // Set the piechart dataset to the piechart entries that will be displayed
        val pieDataSet = PieDataSet(pieEntries, "")

        //Set the piedata to the data that will be rendered to the view
        val data = PieData(pieDataSet)

        pieChart.data = data
        //Set text size for values on piechart
        pieChart.data.setValueTextSize(20f)

        // Hide values of the piechart
//        pieChart.data.dataSet.setDrawValues(false)
        pieChart.setDrawEntryLabels(false)

        // SET UP KEY FOR PIECHART
        val legend = pieChart.legend
        legend.verticalAlignment = Legend.LegendVerticalAlignment.CENTER
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        legend.orientation = Legend.LegendOrientation.VERTICAL
        legend.setDrawInside(false)

        // SET UP VIEW FOR PIECHART
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


    private fun setUpPieChart0() {
        //Calculate the total base pay for these entries
        var totalBasePay = 0f

        // Create pie entries using 'total base pay' calculated as an entry
        pieEntries.add(PieEntry(totalBasePay, "Base Pay"))
        pieEntries.add(PieEntry(2f, "Commission Pay"))
        pieEntries.add(PieEntry(5f, "Tip Pay"))

    }

    private fun setPieChartEntries1() {
        // Format previouspaydate and upcomingpaydate to GMT
        val dateFormat = SimpleDateFormat("EEE dd MMMM yyyy")
        val formatPreviousPayDate = dateFormat.parse(previousPayDate)
        val formatUpcomingPayDate = dateFormat.parse(upcomingPayDate)

        // Create collection of dates between previouspaydate and upcomingpaydate
        var lastDate = Calendar.getInstance()
        lastDate.time = formatUpcomingPayDate
        lastDate.add(Calendar.DATE, -1)

        var cal = Calendar.getInstance()
        cal.time = formatPreviousPayDate

        val applicableDates = ArrayList<Date>(25)

        while (cal.before(lastDate)) {
            cal.add(Calendar.DATE, 1)
            applicableDates.add(cal.time)
        }


        // Create collection of data entries that coincide the dates between previouspaydate and upcomingpaydate
        var entriesBetweenDates = ArrayList<Entry>()

        for (entry in currentListOfEntries) {
            val formattedEntryDate = dateFormat.parse(entry.date_recorded!!)
            for (date in applicableDates) {
                if (formattedEntryDate.equals(date)) {
                    entriesBetweenDates.add(entry)
                    Log.d(TAG, " FOR1 " + entry.earned!!.toFloat())
                }
            }
        }

        //Calculate the total base pay for these entries
        var totalBasePay = 0f

        for (entry in entriesBetweenDates) {
            totalBasePay = totalBasePay + entry.earned!!.toFloat()
        }

        // Create pie entries using 'total base pay' calculated as an entry
        pieEntries.add(PieEntry(totalBasePay, "Base Pay"))
        pieEntries.add(PieEntry(2f, "Commission Pay"))
        pieEntries.add(PieEntry(5f, "Tip Pay"))

    }


    companion object {
        private const val TAG = "HomeFragment"
        private val appDatabase = AppDatabase()
    }

    //Ultities method


}
