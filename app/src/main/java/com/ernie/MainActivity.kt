package com.ernie

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.ernie.journal.JournalListAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private var mAdapter: JournalListAdapter? = null
    private var firestoreDB = FirebaseFirestore.getInstance()
    private val currentFirebaseUser = FirebaseAuth.getInstance().currentUser



    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                findNavController(R.id.nav_host).navigate(R.id.home_dest)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_journal -> {
                findNavController(R.id.nav_host).navigate(R.id.journal_dest)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_profile -> {
                findNavController(R.id.nav_host).navigate(R.id.profile_dest)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fireAuth = FirebaseAuth.getInstance()


        if (fireAuth.currentUser == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
            finish()
        }

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

//        loadEntriesList()

    }

//    private fun loadEntriesList() {
//
//        val collectionPath = "/users/" + currentFirebaseUser?.uid!! + "/entries"
//
//        val bundle = Bundle()
//
//        firestoreDB.collection(collectionPath)
//                .get()
//                .addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        val entryList = mutableListOf<Entry>()
//
//                        for (doc in task.result!!) {
//                            val entry = doc.toObject<Entry>(Entry::class.java)
//                            entryList.add(entry)
//                        }
//
//                        mAdapter = JournalListAdapter(entryList, applicationContext, firestoreDB)
//
//                        bundle.("created", mAdapter)
//                    } else {
//                        Log.d(TAG, "Error getting documents: ", task.exception)
//                    }
//                }
//    }


}