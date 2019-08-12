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
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import com.ernie.MainActivity
import com.ernie.R
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
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
                                        "hour_rate" to hourlyRate.text.toString())

                                firestoreDB.collection("users").document(fireAuth.currentUser?.uid!!).set(firestoreUser)

                                firestoreDB.collection("users").document(fireAuth.currentUser?.uid!!).collection("contract").document("Monday").set(hashMapOf(
                                        "start" to if (monSwitch.isChecked) monStartTime.text.toString() else "",
                                        "end" to if (monSwitch.isChecked) monEndTime.text.toString() else ""
                                ))

                                firestoreDB.collection("users").document(fireAuth.currentUser?.uid!!).collection("contract").document("Tuesday").set(hashMapOf(
                                        "start" to if (tueSwitch.isChecked) tueStartTime.text.toString() else "",
                                        "end" to if (tueSwitch.isChecked) tueEndTime.text.toString() else ""
                                ))

                                firestoreDB.collection("users").document(fireAuth.currentUser?.uid!!).collection("contract").document("Wednesday").set(hashMapOf(
                                        "start" to if (wedSwitch.isChecked) wedStartTime.text.toString() else "",
                                        "end" to if (wedSwitch.isChecked) wedEndTime.text.toString() else ""
                                ))

                                firestoreDB.collection("users").document(fireAuth.currentUser?.uid!!).collection("contract").document("Thursday").set(hashMapOf(
                                        "start" to if (thuSwitch.isChecked) thuStartTime.text.toString() else "",
                                        "end" to if (thuSwitch.isChecked) thuEndTime.text.toString() else ""
                                ))

                                firestoreDB.collection("users").document(fireAuth.currentUser?.uid!!).collection("contract").document("Friday").set(hashMapOf(
                                        "start" to if (friSwitch.isChecked) friStartTime.text.toString() else "",
                                        "end" to if (friSwitch.isChecked) friEndTime.text.toString() else ""
                                ))

                                firestoreDB.collection("users").document(fireAuth.currentUser?.uid!!).collection("contract").document("Saturday").set(hashMapOf(
                                        "start" to if (satSwitch.isChecked) satStartTime.text.toString() else "",
                                        "end" to if (satSwitch.isChecked) satEndTime.text.toString() else ""
                                ))

                                firestoreDB.collection("users").document(fireAuth.currentUser?.uid!!).collection("contract").document("Sunday").set(hashMapOf(
                                        "start" to if (sunSwitch.isChecked) sunStartTime.text.toString() else "",
                                        "end" to if (sunSwitch.isChecked) sunEndTime.text.toString() else ""
                                ))

                                guideUserHome()
                            }
                } else {
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(bundle.getString("userEmail"), bundle.getString("userPassword"))
                            .addOnSuccessListener {
                                val firestoreUser = hashMapOf(
                                        "name" to bundle.getString("userName"),
                                        "email" to bundle.getString("userEmail"),
                                        "hour_rate" to hourlyRate.text.toString())

                                firestoreDB.collection("users").document(fireAuth.currentUser?.uid!!).set(firestoreUser)

                                firestoreDB.collection("users").document(fireAuth.currentUser?.uid!!).collection("contract").document("Monday").set(hashMapOf(
                                        "start" to if (monSwitch.isChecked) monStartTime.text.toString() else "",
                                        "end" to if (monSwitch.isChecked) monEndTime.text.toString() else ""
                                ))

                                firestoreDB.collection("users").document(fireAuth.currentUser?.uid!!).collection("contract").document("Tuesday").set(hashMapOf(
                                        "start" to if (tueSwitch.isChecked) tueStartTime.text.toString() else "",
                                        "end" to if (tueSwitch.isChecked) tueEndTime.text.toString() else ""
                                ))

                                firestoreDB.collection("users").document(fireAuth.currentUser?.uid!!).collection("contract").document("Wednesday").set(hashMapOf(
                                        "start" to if (wedSwitch.isChecked) wedStartTime.text.toString() else "",
                                        "end" to if (wedSwitch.isChecked) wedEndTime.text.toString() else ""
                                ))

                                firestoreDB.collection("users").document(fireAuth.currentUser?.uid!!).collection("contract").document("Thursday").set(hashMapOf(
                                        "start" to if (thuSwitch.isChecked) thuStartTime.text.toString() else "",
                                        "end" to if (thuSwitch.isChecked) thuEndTime.text.toString() else ""
                                ))

                                firestoreDB.collection("users").document(fireAuth.currentUser?.uid!!).collection("contract").document("Friday").set(hashMapOf(
                                        "start" to if (friSwitch.isChecked) friStartTime.text.toString() else "",
                                        "end" to if (friSwitch.isChecked) friEndTime.text.toString() else ""
                                ))

                                firestoreDB.collection("users").document(fireAuth.currentUser?.uid!!).collection("contract").document("Saturday").set(hashMapOf(
                                        "start" to if (satSwitch.isChecked) satStartTime.text.toString() else "",
                                        "end" to if (satSwitch.isChecked) satEndTime.text.toString() else ""
                                ))

                                firestoreDB.collection("users").document(fireAuth.currentUser?.uid!!).collection("contract").document("Sunday").set(hashMapOf(
                                        "start" to if (sunSwitch.isChecked) sunStartTime.text.toString() else "",
                                        "end" to if (sunSwitch.isChecked) sunEndTime.text.toString() else ""
                                ))

                                guideUserHome()
                            }
                }
            }
            Handler().postDelayed({
                if (btnSubmit != null) {
                    btnSubmit.isEnabled = true
                }
            }, 2000)
        }
    }


    private fun guideUserHome() {
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
        activity!!.finish()
    }
}
