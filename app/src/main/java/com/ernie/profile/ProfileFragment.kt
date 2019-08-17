package com.ernie.profile


import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.fragment.app.Fragment
import com.ernie.AppDatabase
import com.ernie.IntroActivity
import com.ernie.R
import com.firebase.ui.auth.AuthUI
import kotlinx.android.synthetic.main.fragment_profile.*
import java.text.SimpleDateFormat
import java.util.*


@SuppressLint("SimpleDateFormat")
class ProfileFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        previousPayDateProfileEditText.setText(appDatabase!!.getPreviousPayDate())
        upcomingPayDateProfileEditText.setText(appDatabase.getUpcomingPayDate())

        setupDateFieldOnClickListener()

        btnAddToPayDates.setOnClickListener {
            if (isFormValid()) {
                appDatabase.updatePayDates(previousPayDateProfileEditText.text.toString(), upcomingPayDateProfileEditText.text.toString())
            } else {
                previousPayDateProfileEditText.error = "This field is incorrect."

            }
        }


        btnLogOut.setOnClickListener {
            signOut()
        }


    }


    private fun setupDateFieldOnClickListener() {

        previousPayDateProfileEditText.setOnClickListener {
            previousPayDateProfileEditText.error = null
            // Launch Date Picker Dialog
            val calendar = Calendar.getInstance()

            val datePickerDialog1 = DatePickerDialog(activity!!,
                    DatePickerDialog.OnDateSetListener { _: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                        var month = monthOfYear.toString()
                        var day = dayOfMonth.toString()

                        if (month.length == 1) month = "0$month"
                        if (day.length == 1) day = "0$day"

                        val formattedPreviousPayDate = formatDate(day + "/" + month + "/" + year.toString())
                        previousPayDateProfileEditText.setText(formattedPreviousPayDate)
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
            datePickerDialog1.show()
        }

        upcomingPayDateProfileEditText.setOnClickListener {

            // Launch Date Picker Dialog
            val calendar = Calendar.getInstance()

            val datePickerDialog2 = DatePickerDialog(activity!!,
                    DatePickerDialog.OnDateSetListener { _: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                        var month = monthOfYear.toString()
                        var day = dayOfMonth.toString()

                        if (month.length == 1) month = "0$month"
                        if (day.length == 1) day = "0$day"

                        val formattedPreviousPayDate = formatDate(day + "/" + month + "/" + year.toString())
                        upcomingPayDateProfileEditText.setText(formattedPreviousPayDate)
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
            datePickerDialog2.show()
        }
    }


    private fun signOut() {
        AuthUI.getInstance()
                .signOut(activity!!.applicationContext)
                .addOnCompleteListener {
                    val intent = Intent(activity!!.applicationContext, IntroActivity::class.java)
                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
                }
    }


    private fun formatDate(date: String): String {

        val formatFrom = SimpleDateFormat("dd/MM/yyyy")
        val formattedFromDate = formatFrom.parse(date)

        val formatTo = SimpleDateFormat("EEE dd MMMM yyyy")
        val formattedToDate = formatTo.format(formattedFromDate)

        return formattedToDate
    }


    private fun calcNumDaysBetweenDates(previousPayDate: String, upcomingPayDate: String): Long {

        val formatter = SimpleDateFormat("EEE dd MMMM yyyy")

        try {
            //Create date objects
            val dateBefore = formatter.parse(previousPayDate)
            val dateAfter = formatter.parse(upcomingPayDate)
            //Calculate the number of days before the dates
            val difference = dateAfter.time - dateBefore.time
            val daysBetween = (difference / (1000 * 60 * 60 * 24))

            return daysBetween

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0

    }

    private fun isFormValid(): Boolean {
        return calcNumDaysBetweenDates(previousPayDateProfileEditText.text.toString(), upcomingPayDateProfileEditText.text.toString()) > 0
    }

    companion object {
        private const val TAG = "ProfileFragment"
        private val appDatabase: AppDatabase? = AppDatabase()
    }


}
