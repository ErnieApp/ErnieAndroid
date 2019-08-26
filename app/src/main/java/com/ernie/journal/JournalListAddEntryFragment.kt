package com.ernie.journal

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import android.text.format.DateFormat
import android.util.Log
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
import java.text.SimpleDateFormat
import java.util.*


class JournalListAddEntryFragment(private val appDatabase: AppDatabase) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_journal_list_add_entry, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnSubmit.setOnClickListener {

            val entryDate = dateUserInput.text!!.split("/")

            val calendar = Calendar.getInstance()
            calendar.set(entryDate[2].toInt(), entryDate[1].toInt(), entryDate[0].toInt())

            //Create a a timestamp
            val currentDate = DateFormat.format("EEE dd MMMM yyyy", calendar).toString()

            AppDatabase.addEntry(Entry(null,
                    startTimeUserInput.text.toString(),
                    endTimeUserInput.text.toString(),
                    breakDurationUserInput.text.toString().toInt(),
                    baseEarnedUserInput.text.toString().substring(1),
                    currentDate))
            clearFields()
            activity!!.onBackPressed()
        }
        setupDateFieldOnClickListener()
        setupTimeFieldOnClickListener(startTimeUserInput)
        setupTimeFieldOnClickListener(endTimeUserInput)
        setupTotalAmountEarnedFormatter()
        setDefaultDateForDateField()

        breakDurationUserInput.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable) {
                if (s.isBlank()) {
                    breakDurationUserInput.setText("0")
                } else if (!s.toString().matches("([1-9][0-9]*|0)".toRegex())) {
                    breakDurationUserInput.setText(breakDurationUserInput.text.toString().substring(1))
                    Selection.setSelection(breakDurationUserInput.text, breakDurationUserInput.text.toString().length)
                }
                attemptToComputeBasePay()
            }
        })
    }

    private fun setDefaultDateForDateField() {
        val calendar = Calendar.getInstance()

        val year = calendar.get(Calendar.YEAR).toString()
        var month = calendar.get(Calendar.MONTH).toString()
        var day = calendar.get(Calendar.DAY_OF_MONTH).toString()
        if (month.length == 1) month = "0$month"
        if (day.length == 1) day = "0$day"

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

                        attemptToComputeBasePay()
                        attemptToDuration()
                    }, 12, 0, true)
            timePickerDialog.show()
        }
    }

    private fun attemptToComputeBasePay() {
        if (startTimeUserInput.text!!.isNotBlank() && endTimeUserInput.text!!.isNotBlank() && breakDurationUserInput.text!!.isNotBlank()) {
            baseEarnedUserInput.setText("£".plus(calculateBasePay()))
            if (baseEarnedUserInput.text.toString().split(".")[1].length == 1) {
                baseEarnedUserInput.setText(baseEarnedUserInput.text.toString().plus(0))
            }
        }
    }

    private fun attemptToDuration() {
        if (startTimeUserInput.text!!.isNotBlank() && endTimeUserInput.text!!.isNotBlank()) {
            val hoursDotMins = calculateShiftDuration(false).toString().split(".")
            val hours = hoursDotMins[0]
            val mins = hoursDotMins[1]
            if (hours.equals("0") && mins.equals("0")) {
                shiftDuration.setText("0 mins")
            } else if (hours.equals("0")) {
                shiftDuration.setText(mins + " min")
            } else if (mins.equals("0")) {
                shiftDuration.setText(hours + " h ")
            } else {
                shiftDuration.setText(hours + " h " + mins + " min")
            }
        }
    }

    private fun calculateShiftDuration(asMinutes: Boolean): Float {
        val simpleDateFormat = SimpleDateFormat("HH:mm")
        val startDate = simpleDateFormat.parse(startTimeUserInput.text.toString())
        val endDate = simpleDateFormat.parse(endTimeUserInput.text.toString())

        var difference = endDate.time - startDate.time
        if (difference < 0) {
            val dateMax = simpleDateFormat.parse("24:00")
            val dateMin = simpleDateFormat.parse("00:00")
            difference = dateMax.time - startDate.time + (endDate.time - dateMin.time)
        }
        val days = (difference / (1000 * 60 * 60 * 24)).toInt()
        val hours = ((difference - 1000 * 60 * 60 * 24 * days) / (1000 * 60 * 60)).toInt()
        val min = (difference - (1000 * 60 * 60 * 24 * days).toLong() - (1000 * 60 * 60 * hours).toLong()).toInt() / (1000 * 60)

        if (asMinutes) {
            return (hours * 60 + min).toFloat()
        } else {
            return ("$hours" + ".$min").toFloat()
        }

    }

    private fun calculateBasePay(): Float {
        val duration = calculateShiftDuration(true)
        val hourlyRate = appDatabase.getHourlyRate().toFloat()

        Log.d("MERT", duration.toString())
        Log.d("MERT", hourlyRate.toString())

        return (duration * ((hourlyRate * 100) / 60)) / 100
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
        startTimeUserInput.text!!.clear()
        endTimeUserInput.text!!.clear()
        shiftDuration.text!!.clear()
        breakDurationUserInput.text!!.clear()
        baseEarnedUserInput.text!!.clear()
        earnedUserInput.text!!.clear()
    }

    companion object {
        private val TAG = JournalListAddEntryFragment::class.simpleName
    }
}
