package com.ernie.authentication

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import android.util.Log
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
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_registration_form.*


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [RegistrationFormFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [RegistrationFormFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class RegistrationFormFragment : Fragment() {

    val firestoreDB = FirebaseFirestore.getInstance()
    private var appDatabase: AppDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appDatabase = AppDatabase.newInstance()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_registration_form, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val bundle = this.arguments

        val contractHoursFields = listOf(monStartTime, monEndTime, tueStartTime, tueEndTime, wedStartTime, wedEndTime,
                thuStartTime, thuEndTime, friStartTime, friEndTime, satStartTime, satEndTime, sunStartTime, sunEndTime)

        for (c in contractHoursFields) {
            c.setOnClickListener {
                // Launch Time Picker Dialog
                val timePickerDialog = TimePickerDialog(activity,
                        TimePickerDialog.OnTimeSetListener { timePicker: TimePicker, hourOfDay: Int, minuteOfDay: Int ->
                            var hour = hourOfDay.toString()
                            var minute = minuteOfDay.toString()

                            if (hour.length == 1) hour = "0$hour"
                            if (minute.length == 1) minute = "0$minute"

                            c.setText(hour.plus(":").plus(minute))
                        }, 12, 0, true)
                timePickerDialog.show()
            }
        }

        val switches = listOf(monSwitch, tueSwitch, wedSwitch, thuSwitch, friSwitch, satSwitch, sunSwitch)

        for (i in 0 until switches.size) {
            switches[i].setOnClickListener {
                if (contractHoursFields[i * 2].visibility == View.VISIBLE) {
                    contractHoursFields[i * 2].visibility = View.INVISIBLE
                } else {
                    contractHoursFields[i * 2].visibility = View.VISIBLE
                }

                if (contractHoursFields[i * 2 + 1].visibility == View.VISIBLE) {
                    contractHoursFields[i * 2 + 1].visibility = View.INVISIBLE
                } else {
                    contractHoursFields[i * 2 + 1].visibility = View.VISIBLE
                }
            }
        }

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

        btnSubmit.setOnClickListener {
            btnSubmit.isEnabled = false

            var hasUserFilledOutForm = true

            for (i in 0 until switches.size) {
                Log.d("MERT", "entered loop")
                if (switches[i].isChecked) {
                    Log.d("MERT", switches[i].id.toString() + " - switch selected")

                    if (contractHoursFields[i * 2].text.isBlank()) {
                        Log.d("MERT", "start is blank")

                        hasUserFilledOutForm = false
                        contractHoursFields[i * 2].error = ""
                    }

                    if (contractHoursFields[i * 2 + 1].text.isBlank()) {
                        Log.d("MERT", "end is blank")

                        hasUserFilledOutForm = false
                        contractHoursFields[i * 2 + 1].error = ""
                    }
                }
            }

            if (hasUserFilledOutForm) {
                //TODO: continue to main, create acc, pass data to firestore

                val fireAuth = FirebaseAuth.getInstance()



                if (bundle!!.getBoolean("isGoogle")) {

                    val userAccount = bundle.getParcelable<GoogleSignInAccount>("account")

                    fireAuth.signInWithCredential(bundle.getParcelable("credential"))
                            .addOnSuccessListener {
                                val firestoreUser = hashMapOf(
                                        "name" to userAccount.displayName,
                                        "email" to userAccount.email,
                                        "hourly_rate" to hourlyRate.text.toString())

                                firestoreDB.collection("users").document(fireAuth.currentUser?.uid!!).set(firestoreUser)

                                updateContract(fireAuth)
                                guideUserHome()
                            }
                } else {
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(bundle.getString("userEmail"), bundle.getString("userPassword"))
                            .addOnSuccessListener {
                                val firestoreUser = hashMapOf(
                                        "name" to bundle.getString("userName"),
                                        "email" to bundle.getString("userEmail"),
                                        "hourly_rate" to hourlyRate.text.toString())

                                firestoreDB.collection("users").document(fireAuth.currentUser?.uid!!).set(firestoreUser)

                                updateContract(fireAuth)
                                appDatabase!!.addEntry("", "", 0, 0)
                                guideUserHome()
                            }
                }
            }
            Handler().postDelayed({
                if (btnSubmit != null) {
                    btnSubmit.isEnabled = true
                }
            }, 3000)
        }
    }

    private fun updateContract(fireAuth: FirebaseAuth) {
        updateContractDay("Monday", monSwitch, monStartTime, monEndTime, fireAuth)
        updateContractDay("Tuesday", tueSwitch, tueStartTime, tueEndTime, fireAuth)
        updateContractDay("Wednesday", wedSwitch, wedStartTime, wedEndTime, fireAuth)
        updateContractDay("Thursday", thuSwitch, thuStartTime, thuEndTime, fireAuth)
        updateContractDay("Friday", friSwitch, friStartTime, friEndTime, fireAuth)
        updateContractDay("Satday", satSwitch, satStartTime, satEndTime, fireAuth)
        updateContractDay("Sunday", sunSwitch, sunStartTime, sunEndTime, fireAuth)
    }

    private fun updateContractDay(day: String, switch: Switch, startTime: EditText, endTime: EditText, fireAuth: FirebaseAuth) {
        firestoreDB.collection("users").document(fireAuth.currentUser?.uid!!).collection("contract").document(day).set(hashMapOf(
                "start" to if (switch.isChecked) startTime.text.toString() else "",
                "end" to if (switch.isChecked) endTime.text.toString() else ""
        ))
    }


    private fun guideUserHome() {
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
        activity!!.finish()
    }

    companion object {
        fun launchRegistrationFormWithGoogleAccount(activity: FragmentActivity, credential: AuthCredential, account: GoogleSignInAccount) {
            val fragmentManager: FragmentManager = activity.supportFragmentManager
            val transaction = fragmentManager.beginTransaction()

            val bundle = Bundle()
            bundle.putBoolean("isGoogle", true)
            bundle.putParcelable("credential", credential)
            bundle.putParcelable("account", account)

            val registrationFormFragment = RegistrationFormFragment()
            registrationFormFragment.arguments = bundle

            transaction.replace(R.id.authenticationFrame, registrationFormFragment)
            transaction.commit()
        }
    }
}