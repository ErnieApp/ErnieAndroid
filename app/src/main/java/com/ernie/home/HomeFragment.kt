package com.ernie.home


import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ernie.AppDatabase
import com.ernie.R
import com.ernie.model.Entry
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.synthetic.main.fragment_home.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment(private val appDatabase: AppDatabase) : Fragment() {

    private var previousPayDate: String = ""
    private var upcomingPayDate: String = ""
    private var currentListOfEntries: ArrayList<Entry>? = null
    private lateinit var entriesSnapshotListener: ListenerRegistration
    private lateinit var userSnapshotListener: ListenerRegistration

    // Piechart entries arraylist
    private var pieEntries = ArrayList<PieEntry>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentListOfEntries = appDatabase.getEntries()
        addListeners()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (previousPayDate.isNotBlank() && upcomingPayDate.isNotBlank()) {
            pieEntries.clear()
            setUpPayDayTimer()
            setUpPayDayDate()
            setUpPieChart()
        } else {
            Toast.makeText(activity!!, "NO INTERNET - GET LOST", Toast.LENGTH_LONG).show()
        }
    }

    fun removeListeners() {
        entriesSnapshotListener.remove()
        userSnapshotListener.remove()
    }

    fun addListeners() {
        addUserListener()
        addEntriesLisener()
    }

    private fun addUserListener() {
        userSnapshotListener = appDatabase.getUserDocumentReference().addSnapshotListener(EventListener { userDoc, e ->
            if (e != null) {
                Log.e(TAG, "Snapshot listener failed")
                Toast.makeText(activity!!, "NO INTERNET - GET LOST", Toast.LENGTH_LONG).show()
                return@EventListener
            }
            previousPayDate = userDoc!!.getString("previous_pay_date")!!
            upcomingPayDate = userDoc.getString("upcoming_pay_date")!!
            redrawView()
        })
    }

    fun addEntriesLisener() {
        entriesSnapshotListener = appDatabase.getEntriesCollectionReference().addSnapshotListener(EventListener { entries, e ->
            if (e != null) {
                Log.e(TAG, "Snapshot listener failed")
                Toast.makeText(activity!!, "NO INTERNET - GET LOST", Toast.LENGTH_LONG).show()
                return@EventListener
            }
            redrawView()
        })
    }

    private fun redrawView() {
        if (view != null) {
            pieEntries.clear()
            setUpPayDayTimer()
            setUpPayDayDate()
            setUpPieChart()
        }
    }

    //COMPLETE
    private fun setUpPayDayTimer() {
        val formatter = SimpleDateFormat("EEE dd MMMM yyyy")

        try {
            //Create date objects
            val dateBefore = formatter.parse(previousPayDate)
            val dateAfter = formatter.parse(upcomingPayDate)
            //Calculate the number of days before the dates
            val difference = dateAfter.time - dateBefore.time
            val daysBetween = (difference / (1000 * 60 * 60 * 24)).toString()

            //Set the string to pass to the view
            val stringToSet = daysBetween + " days left"
            days_left_textview.text = stringToSet

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    //COMPLETE
    private fun setUpPayDayDate() {
        view!!.findViewById<TextView>(R.id.payday_date_textview).text = upcomingPayDate
    }


    private fun setUpPieChart() {

        // SET UP DATA FOR PIECHART
        setPieChartEntries()

        // Set the piechart dataset to the piechart entries that will be displayed
        val pieDataSet = PieDataSet(pieEntries, "")

        //Set the piedata to the data that will be rendered to the view
        val data = PieData(pieDataSet)

        pie_chart_view.data = data
        //Set text size for values on piechart
        pie_chart_view.data.setValueTextSize(20f)

        // Hide values of the piechart
//        pieChart.data.dataSet.setDrawValues(false)
        pie_chart_view.setDrawEntryLabels(false)

        // SET UP KEY FOR PIECHART
        val legend = pie_chart_view.legend
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
        pie_chart_view.centerText = "£" + currentTotalAmountEarned
        pie_chart_view.setCenterTextSize(14f)
        pie_chart_view.setCenterTextColor(Color.BLUE)

        //Hide default description text
        pie_chart_view.description.isEnabled = false

        //Set initial animation speed
//        pie_chart_view.animateXY(5000, 500)


    }

    @SuppressLint("SimpleDateFormat")
    private fun setPieChartEntries() {
        // Create date objects from the previous/upcoming dates strings
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

        for (entry in this.currentListOfEntries!!) {
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
            totalBasePay += entry.earned!!.toFloat()

        }

        // Create pie entries using 'total base pay' calculated as an entry
        pieEntries.add(PieEntry(totalBasePay, "Base Pay"))
        pieEntries.add(PieEntry(2f, "Commission Pay"))
        pieEntries.add(PieEntry(5f, "Tip Pay"))

    }

    override fun onDestroy() {
        super.onDestroy()

        Log.d(TAG, "View is broke")

    }

    companion object {
        private val TAG = HomeFragment::class.simpleName
    }


}
