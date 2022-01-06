package com.scranaver.notes.ui.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.scranaver.notes.R
import com.scranaver.notes.data.Constants
import com.scranaver.notes.databinding.ActivityProfileBinding
import com.scranaver.notes.ui.login.LoginActivity

class ProfileActivity : AppCompatActivity() {
    private var auth: FirebaseAuth = Firebase.auth
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private lateinit var binding: ActivityProfileBinding

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var logout: Button
    private lateinit var edit: Button
    private lateinit var save: Button
    private lateinit var cancel: Button
    private lateinit var separator: TextView

    private lateinit var showProfile: LinearLayout
    private lateinit var editProfile: LinearLayout
    private lateinit var linearSave: LinearLayout

    private lateinit var nameTextView: TextView
    private lateinit var phoneTextView: TextView
    private lateinit var emailTextView: TextView

    private lateinit var nameEditText: EditText
    private lateinit var phoneEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = "Profile"
        }

        swipeRefreshLayout = binding.swipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener {
            getData()
        }

        logout = binding.logout
        logout.setOnClickListener {
            Firebase.auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        edit = binding.edit
        save = binding.save
        cancel = binding.cancel
        separator = binding.separator
        linearSave = binding.linearSave
        editProfile = binding.editProfile
        showProfile = binding.showProfile
        edit.setOnClickListener {
            edit.visibility = View.GONE
            separator.visibility = View.GONE
            showProfile.visibility = View.GONE
            save.visibility = View.VISIBLE
            linearSave.visibility = View.VISIBLE
            editProfile.visibility = View.VISIBLE
        }
        save.setOnClickListener {
            editData()
        }
        cancel.setOnClickListener {
            edit.visibility = View.VISIBLE
            separator.visibility = View.VISIBLE
            showProfile.visibility = View.VISIBLE
            save.visibility = View.GONE
            linearSave.visibility = View.GONE
            editProfile.visibility = View.GONE
        }

        nameTextView = binding.name
        phoneTextView = binding.phone
        emailTextView = binding.email

        nameEditText = binding.editName
        phoneEditText = binding.editPhone

        getData()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun getData() {
        swipeRefreshLayout.isRefreshing = true
        db.document("${Constants.userCollectionKey()}/${auth.currentUser?.uid}").get().addOnSuccessListener { snapshot ->
            val userData = snapshot.data
            nameTextView.text = userData?.get("name").toString()
            phoneTextView.text = userData?.get("phone").toString()
            emailTextView.text = userData?.get("email").toString()

            nameEditText.setText(userData?.get("name").toString())
            phoneEditText.setText(userData?.get("phone").toString())
        }.addOnFailureListener { error ->
            Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
        }
        swipeRefreshLayout.isRefreshing = false
    }

    private fun editData() {
        val name: String = nameEditText.text.toString()
        val phone: String = phoneEditText.text.toString()

        if (name.isBlank()) {
            nameEditText.error = "Name is required!"
            nameEditText.requestFocus()
            return
        }
        if (phone.isBlank()) {
            phoneEditText.error = "Phone is required!"
            phoneEditText.requestFocus()
            return
        }

        val updateUser = hashMapOf(
            "name" to name,
            "phone" to phone
        )
        db.document("${Constants.userCollectionKey()}/${auth.currentUser?.uid}").update(updateUser as Map<String, Any>).addOnSuccessListener {
            edit.visibility = View.VISIBLE
            separator.visibility = View.VISIBLE
            showProfile.visibility = View.VISIBLE
            save.visibility = View.GONE
            linearSave.visibility = View.GONE
            editProfile.visibility = View.GONE
            getData()
            Toast.makeText(applicationContext, R.string.success, Toast.LENGTH_LONG).show()
        }.addOnFailureListener { error ->
            Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
        }
    }
}