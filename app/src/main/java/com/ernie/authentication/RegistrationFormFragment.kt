package com.ernie.authentication

import android.app.TimePickerDialog
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Switch
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.ernie.AppDatabase
import com.ernie.MainActivity
import com.ernie.R
import com.ernie.model.Contract
import com.ernie.model.ContractedDay
import com.ernie.model.Day
import com.ernie.model.User
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_registration_form.*

class RegistrationFormFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_registration_form, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val bundle = this.arguments

        val contractHourFields = listOf(monStartTime, monEndTime, tueStartTime, tueEndTime, wedStartTime, wedEndTime,
                thuStartTime, thuEndTime, friStartTime, friEndTime, satStartTime, satEndTime, sunStartTime, sunEndTime)

        val contractDaySwitches = listOf(monSwitch, tueSwitch, wedSwitch, thuSwitch, friSwitch, satSwitch, sunSwitch)

        setupContractHourFieldsListeners(contractHourFields)
        setupSwitchListeners(contractDaySwitches, contractHourFields)
        setupHourlyRateFieldFormatter()

        btnSubmit.setOnClickListener {
            btnSubmit.isEnabled = false
            val fireAuth = FirebaseAuth.getInstance()

            if (hasUserFilledOutForm(contractDaySwitches, contractHourFields)) {
                if (bundle!!.getBoolean("isGoogle")) {
                    addUserToFireAuthWithCredential(fireAuth, bundle)
                } else {
                    addUserToFireAuthWithEmailAndPassword(fireAuth, bundle)
                }
            }
            Handler().postDelayed({
                if (btnSubmit != null) {
                    btnSubmit.isEnabled = true
                }
            }, REGISTER_ATTEMPT_DELAY_MILLIS)
        }
    }

    private fun addUserToFireAuthWithEmailAndPassword(fireAuth: FirebaseAuth, bundle: Bundle) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(bundle.getString("userEmail")!!, bundle.getString("userPassword")!!)
                .addOnSuccessListener {
                    appDatabase.addUser(User(bundle.getString("userName")!!, bundle.getString("userEmail")!!, hourlyRate.text.toString()))
                    updateContract()
                    MainActivity.launchMainActivityAsFreshStart(activity!!)
                }
    }

    private fun addUserToFireAuthWithCredential(fireAuth: FirebaseAuth, bundle: Bundle) {
        val userAccount = bundle.getParcelable<GoogleSignInAccount>("account")!!

        fireAuth.signInWithCredential(bundle.getParcelable("credential")!!)
                .addOnSuccessListener {
                    appDatabase.addUser(User(userAccount.displayName!!, userAccount.email!!, hourlyRate.text.toString()))
                    updateContract()
                    MainActivity.launchMainActivityAsFreshStart(activity!!)
                }
    }

    private fun hasUserFilledOutForm(contractDaySwitches: List<Switch>, contractHourFields: List<EditText>): Boolean {
        var hasUserFilledOutForm = true

        for (i in 0 until contractDaySwitches.size) {
            if (contractDaySwitches[i].isChecked) {
                if (contractHourFields[i * 2].text.isBlank()) {
                    hasUserFilledOutForm = false
                    contractHourFields[i * 2].error = ""
                }

                if (contractHourFields[i * 2 + 1].text.isBlank()) {
                    hasUserFilledOutForm = false
                    contractHourFields[i * 2 + 1].error = ""
                }
            }
        }
        return hasUserFilledOutForm
    }

    private fun setupContractHourFieldsListeners(contractHourFields: List<EditText>) {
        for (field in contractHourFields) {
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
    }

    private fun setupSwitchListeners(contractDaySwitches: List<Switch>, contractHourFields: List<EditText>) {
        for (i in 0 until contractDaySwitches.size) {
            contractDaySwitches[i].setOnClickListener {
                if (contractHourFields[i * 2].visibility == View.VISIBLE) {
                    contractHourFields[i * 2].visibility = View.INVISIBLE
                    contractHourFields[i * 2 + 1].visibility = View.INVISIBLE
                } else {
                    contractHourFields[i * 2].visibility = View.VISIBLE
                    contractHourFields[i * 2 + 1].visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setupHourlyRateFieldFormatter() {
        hourlyRate.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
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

                    hourlyRate.setText(cashAmountBuilder.toString())
                    // keeps the cursor always to the right
                    Selection.setSelection(hourlyRate.text, cashAmountBuilder.toString().length)
                }
            }
        })
    }

    private fun updateContract() {
        val contract = Contract()
        if (monSwitch.isChecked) contract.addContractedDay(ContractedDay(Day.Monday, monStartTime.text.toString(), monEndTime.text.toString()))
        if (tueSwitch.isChecked) contract.addContractedDay(ContractedDay(Day.Tuesday, tueStartTime.text.toString(), tueEndTime.text.toString()))
        if (wedSwitch.isChecked) contract.addContractedDay(ContractedDay(Day.Wednesday, wedStartTime.text.toString(), wedEndTime.text.toString()))
        if (thuSwitch.isChecked) contract.addContractedDay(ContractedDay(Day.Thursday, thuStartTime.text.toString(), thuEndTime.text.toString()))
        if (friSwitch.isChecked) contract.addContractedDay(ContractedDay(Day.Friday, friStartTime.text.toString(), friEndTime.text.toString()))
        if (satSwitch.isChecked) contract.addContractedDay(ContractedDay(Day.Saturday, satStartTime.text.toString(), satEndTime.text.toString()))
        if (sunSwitch.isChecked) contract.addContractedDay(ContractedDay(Day.Sunday, sunStartTime.text.toString(), sunEndTime.text.toString()))
        appDatabase.setContract(contract)
    }

    companion object {
        private const val REGISTER_ATTEMPT_DELAY_MILLIS: Long = 3000
        private val appDatabase: AppDatabase = AppDatabase()

        fun launchRegistrationFormWithGoogleAccount(activity: FragmentActivity, credential: AuthCredential, account: GoogleSignInAccount) {
            val bundle = Bundle()
            bundle.putBoolean("isGoogle", true)
            bundle.putParcelable("credential", credential)
            bundle.putParcelable("account", account)
            launchRegistrationFormWithSuppliedBundle(activity, bundle)
        }

        fun launchRegistrationFormWithNameAndEmailAndPassword(activity: FragmentActivity, userName: String, userEmail: String, userPassword: String) {
            val bundle = Bundle()
            bundle.putBoolean("isGoogle", false)
            bundle.putString("userName", userName)
            bundle.putString("userEmail", userEmail)
            bundle.putString("userPassword", userPassword)
            launchRegistrationFormWithSuppliedBundle(activity, bundle)
        }

        private fun launchRegistrationFormWithSuppliedBundle(activity: FragmentActivity, bundle: Bundle) {
            val fragmentManager: FragmentManager = activity.supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            val registrationFormFragment = RegistrationFormFragment()
            registrationFormFragment.arguments = bundle
            transaction.replace(R.id.authenticationFrame, registrationFormFragment)
            transaction.commit()
        }
    }
}