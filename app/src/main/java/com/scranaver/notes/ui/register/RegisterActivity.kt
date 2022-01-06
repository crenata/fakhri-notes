package com.scranaver.notes.ui.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.scranaver.notes.R
import com.scranaver.notes.data.Constants
import com.scranaver.notes.databinding.ActivityRegisterBinding
import com.scranaver.notes.ui.login.LoginActivity

class RegisterActivity: AppCompatActivity() {
    private var userDocRef: CollectionReference = FirebaseFirestore.getInstance().collection(Constants.userCollectionKey())
    private var auth: FirebaseAuth = Firebase.auth

    private lateinit var binding: ActivityRegisterBinding

    private lateinit var loading: ProgressBar
    private lateinit var registerButton: Button

    private lateinit var nameEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var tosCheckBox: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = ""
        }

        loading = binding.loading
        registerButton = binding.register
        nameEditText = binding.name
        phoneEditText = binding.phone
        emailEditText = binding.email
        passwordEditText = binding.password
        tosCheckBox = binding.tos

        registerButton.setOnClickListener {
            register()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        goToLogin()
    }

    private fun goToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun register() {
        val name: String = nameEditText.text.toString()
        val phone: String = phoneEditText.text.toString()
        val email: String = emailEditText.text.toString()
        val password: String = passwordEditText.text.toString()
        val tos: Boolean = tosCheckBox.isChecked

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
        if (email.isBlank()) {
            emailEditText.error = "Email is required!"
            emailEditText.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.error = "Email is invalid!"
            emailEditText.requestFocus()
            return
        }
        if (password.isBlank()) {
            passwordEditText.error = "Password is required!"
            passwordEditText.requestFocus()
            return
        }
        if (password.length < 6) {
            passwordEditText.error = "Min 6 characters!"
            passwordEditText.requestFocus()
            return
        }
        if (!tos) {
            tosCheckBox.error = "You must agree with this!"
            tosCheckBox.requestFocus()
            return
        }

        loading.visibility = View.VISIBLE
        registerButton.isEnabled = false

        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
            val newUser = hashMapOf(
                "name" to name,
                "phone" to phone,
                "email" to email
            )
            val user = auth.currentUser
            if (user != null) {
                userDocRef.document(user.uid).set(newUser).addOnSuccessListener {
                    loading.visibility = View.GONE
                    registerButton.isEnabled = true
                    Toast.makeText(applicationContext, R.string.success, Toast.LENGTH_LONG).show()
                    goToLogin()
                }.addOnFailureListener { error ->
                    loading.visibility = View.GONE
                    registerButton.isEnabled = true
                    Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
                }
            } else {
                loading.visibility = View.GONE
                registerButton.isEnabled = true
                Toast.makeText(applicationContext, R.string.error, Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { error ->
            loading.visibility = View.GONE
            registerButton.isEnabled = true
            Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
        }
    }
}