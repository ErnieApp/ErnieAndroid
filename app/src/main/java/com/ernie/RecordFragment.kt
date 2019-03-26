package com.ernie


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_record.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class Record : Fragment() {

    private lateinit var database: DatabaseReference


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        return inflater.inflate(R.layout.fragment_record, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = FirebaseDatabase.getInstance().reference
        // Inflate the layout for this fragment


        val userId = database.push().key
        btnPassData.setOnClickListener {

            Log.d("Hello", "CHECKING")

            writeNewUser(userId!!)

            Log.d("Hello", "CHECKING")
        }

    }

    private fun writeNewUser(userId: String) {
        val text = inMessage.text.toString().trim()

        if (text.isEmpty()) {
            inMessage.error = "Please enter a name"
            Log.d("Hello", "CHECKING")
            return
        }

        val user = User(text)
        database.child("users").child(userId).setValue(user)
    }


}
