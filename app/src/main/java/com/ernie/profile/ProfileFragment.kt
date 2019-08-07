package com.ernie.profile


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ernie.AppDatabase
import com.ernie.LoginActivity
import com.ernie.R
import com.ernie.model.User
import com.firebase.ui.auth.AuthUI
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_profile.*


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


    val db = FirebaseFirestore.getInstance()


    // Create a new user with a first and last name


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnShowDatafromUserTable.setOnClickListener {
            tvDisplayName.text = ""
            val dbHandler = AppDatabase(this.activity!!, null)
            val cursor = dbHandler.getAllUsers()
            cursor!!.moveToFirst()
            tvDisplayName.append((cursor.getString(cursor.getColumnIndex(AppDatabase.COLUMN_NAME))))
            while (cursor.moveToNext()) {
                tvDisplayName.append((cursor.getString(cursor.getColumnIndex(AppDatabase.COLUMN_NAME))))
                tvDisplayName.append("\n")
            }
            cursor.close()
        }

        btnAddToUserTable.setOnClickListener {
            val dbHandler = AppDatabase(this.activity!!, null)


            val user = User(etName.text.toString(), etEmail.text.toString(), etPassword.text.toString(), etHourlyRate.text.toString(), etContractID.text.toString().toInt())
            dbHandler.addUser(user)
            Toast.makeText(this.activity!!, etName.text.toString() + "Added to database", Toast.LENGTH_LONG).show()


            val userHashMap = hashMapOf(
                    "name" to etName.text.toString(),
                    "email" to etEmail.text.toString(),
                    "hourlyRate" to etHourlyRate.text.toString()


            )

            // Add a new document with a generated ID
            db.collection("entries").document().set(user)

        }

        btnLogOut.setOnClickListener {
            signOut()
        }


    }


    private fun signOut() {
        AuthUI.getInstance()
                .signOut(activity!!.applicationContext)
                .addOnCompleteListener {
                    val intent = Intent(activity!!.applicationContext, LoginActivity::class.java)
                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
                }
    }





}
