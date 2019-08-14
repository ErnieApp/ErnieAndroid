package com.ernie.journal

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import com.ernie.AppDatabase
import com.ernie.R
import com.ernie.model.Entry
import kotlinx.android.synthetic.main.fragment_journal_list_add_entry.*
import java.util.*


class JournalListAddEntryFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_journal_list_add_entry, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnSubmit.setOnClickListener {

            val entryDate = dateUserInput.text.split("/")

            val calendar = Calendar.getInstance()
            calendar.set(entryDate[2].toInt(), entryDate[1].toInt(), entryDate[0].toInt())

            //Create a a timestamp
            val currentDate = DateFormat.format("EEE dd MMMM yyyy", calendar).toString()

            appDatabase?.addEntry(Entry(null,
                    startTimeUserInput.text.toString(),
                    endTimeUserInput.text.toString(),
                    breakDurationUserInput.text.toString().toInt(),
                    earnedUserInput.text.toString(),
                    currentDate))
            clearFields()
            activity!!.onBackPressed()
        }
        setupDateFieldOnClickListener()
        setupTimeFieldOnClickListener(startTimeUserInput)
        setupTimeFieldOnClickListener(endTimeUserInput)
        setupTotalAmountEarnedFormatter()
        setDefaultDateForDateField()
    }

    private fun setDefaultDateForDateField() {
        val calendar = Calendar.getInstance()

        val year = calendar.get(Calendar.YEAR).toString()
        val month = calendar.get(Calendar.MONTH).toString()
        val day = calendar.get(Calendar.DAY_OF_MONTH).toString()
        dateUserInput.setText(day + "/" + month + "/" + year)
    }

    private fun setupDateFieldOnClickListener() {
        dateUserInput.setOnClickListener {
            // Launch Date Picker Dialog
            val calendar = Calendar.getInstance()

            val datePickerDialog = DatePickerDialog(activity!!,
                    DatePickerDialog.OnDateSetListener { _: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                        var month = monthOfYear.toString()
                        var day = dayOfMonth.toString()

                        if (month.length == 1) month = "0$month"
                        if (day.length == 1) day = "0$day"

                        dateUserInput.setText(day + "/" + month + "/" + year.toString())
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
            datePickerDialog.show()
        }
    }

    private fun setupTimeFieldOnClickListener(field: EditText) {
        field.setOnClickListener {
            // Launch Time Picker Dialog
            val timePickerDialog = TimePickerDialog(activity,
                    TimePickerDialog.OnTimeSetListener { _: TimePicker, hourOfDay: Int, minuteOfDay: Int ->
                        var hour = hourOfDay.toString()
                        var minute = minuteOfDay.toString()

                        if (hour.length == 1) hour = "0$hour"
                        if (minute.length == 1) minute = "0$minute"

                        field.setText(hour.plus(":").plus(minute))
                    }, 12, 0, true)
            timePickerDialog.show()
        }
    }

    private fun setupTotalAmountEarnedFormatter() {
        earnedUserInput.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable) {
                if (!s.toString().matches("^(\\d{1,3}(\\,\\d{3})*|(\\d+))(\\.\\d{2})?$".toRegex())) {
                    val userInput = "" + s.toString().replace("[^\\d]".toRegex(), "")
                    val cashAmountBuilder = StringBuilder(userInput)

                    while (cashAmountBuilder.length > 3 && cashAmountBuilder[0] == '0') {
                        cashAmountBuilder.deleteCharAt(0)
                    }
                    while (cashAmountBuilder.length < 3) {
                        cashAmountBuilder.insert(0, '0')
                    }
                    cashAmountBuilder.insert(cashAmountBuilder.length - 2, '.')

                    earnedUserInput.setText(cashAmountBuilder.toString())
                    // keeps the cursor always to the right
                    Selection.setSelection(earnedUserInput.text, cashAmountBuilder.toString().length)
                }
            }
        })
    }

    private fun clearFields() {
        startTimeUserInput.text.clear()
        endTimeUserInput.text.clear()
        breakDurationUserInput.text.clear()
        earnedUserInput.text.clear()
    }

    companion object {
        private const val TAG = "JournalListAddEntryFragment"
        private val appDatabase: AppDatabase? = AppDatabase()
    }
}
