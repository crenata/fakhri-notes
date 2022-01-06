package com.scranaver.notes.ui.note

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.scranaver.notes.R
import com.scranaver.notes.data.Constants
import com.scranaver.notes.databinding.ActivityAddNoteBinding

class AddNoteActivity : AppCompatActivity() {
    private var userDocRef: CollectionReference = FirebaseFirestore.getInstance().collection(Constants.userCollectionKey())
    private var auth: FirebaseAuth = Firebase.auth

    private lateinit var binding: ActivityAddNoteBinding

    private lateinit var loading: ProgressBar
    private lateinit var addButton: Button

    private lateinit var titleEditText: EditText
    private lateinit var contentEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = "Add New"
        }

        loading = binding.loading
        addButton = binding.add
        titleEditText = binding.title
        contentEditText = binding.content

        addButton.setOnClickListener {
            add()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun add() {
        val title: String = titleEditText.text.toString()
        val content: String = contentEditText.text.toString()

        if (title.isBlank()) {
            titleEditText.error = "Title is required!"
            titleEditText.requestFocus()
            return
        }
        if (content.isBlank()) {
            contentEditText.error = "Content is required!"
            contentEditText.requestFocus()
            return
        }

        loading.visibility = View.VISIBLE
        addButton.isEnabled = false

        val newNote = hashMapOf(
            "title" to title,
            "content" to content
        )
        val user = auth.currentUser
        if (user != null) {
            userDocRef.document(user.uid).collection(Constants.noteCollectionKey()).add(newNote).addOnSuccessListener {
                loading.visibility = View.GONE
                addButton.isEnabled = true
                Toast.makeText(applicationContext, R.string.success, Toast.LENGTH_LONG).show()
                finish()
            }.addOnFailureListener { error ->
                loading.visibility = View.GONE
                addButton.isEnabled = true
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }
        } else {
            loading.visibility = View.GONE
            addButton.isEnabled = true
            Toast.makeText(applicationContext, R.string.error, Toast.LENGTH_SHORT).show()
        }
    }
}