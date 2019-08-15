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


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
private const val TAG = "ProfileFragment"


class ProfileFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDateFieldOnClickListener()

        btnAddToPayDates.setOnClickListener {
            val previousPayDate = previousPayDateEditTextField.text.toString()
            val upcomingPayDate = upcomingPayDateEditTextField.text.toString()
            appDatabase!!.addPayDates(formatDate(previousPayDate), formatDate(upcomingPayDate))
            clearFields()
            activity!!.onBackPressed()
        }


        btnLogOut.setOnClickListener {
            signOut()
        }


    }


    @SuppressLint("SimpleDateFormat")
    private fun setupDateFieldOnClickListener() {

        previousPayDateEditTextField.setOnClickListener {
            // Launch Date Picker Dialog
            val calendar = Calendar.getInstance()

            val datePickerDialog1 = DatePickerDialog(activity!!,
                    DatePickerDialog.OnDateSetListener { _: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                        var month = monthOfYear.toString()
                        var day = dayOfMonth.toString()

                        if (month.length == 1) month = "0$month"
                        if (day.length == 1) day = "0$day"



                        previousPayDateEditTextField.setText(day + "/" + month + "/" + year.toString())
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
            datePickerDialog1.show()
        }

        upcomingPayDateEditTextField.setOnClickListener {
            // Launch Date Picker Dialog
            val calendar = Calendar.getInstance()

            val datePickerDialog2 = DatePickerDialog(activity!!,
                    DatePickerDialog.OnDateSetListener { _: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                        var month = monthOfYear.toString()
                        var day = dayOfMonth.toString()

                        if (month.length == 1) month = "0$month"
                        if (day.length == 1) day = "0$day"

                        upcomingPayDateEditTextField.setText(day + "/" + month + "/" + year.toString())

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


    private fun clearFields() {
        upcomingPayDateEditTextField.text.clear()
        previousPayDateEditTextField.text.clear()

    }

    private fun formatDate(date: String): String {
        val formatTo = SimpleDateFormat("EEE dd MMMM yyyy")
        val formatFrom = SimpleDateFormat("dd/MM/yyyy")
        val formattedFromDate = formatFrom.parse(date)
        val formattedToDate = formatTo.format(formattedFromDate)

        return formattedToDate
    }


    companion object {
        private const val TAG = "ProfileFragment"
        private val appDatabase: AppDatabase? = AppDatabase()
    }


}
