package com.scranaver.notes.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.scranaver.notes.R
import com.scranaver.notes.data.Constants
import com.scranaver.notes.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private var auth: FirebaseAuth = Firebase.auth
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private lateinit var binding: ActivityDetailBinding

    private lateinit var loading: ProgressBar
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var delete: Button
    private lateinit var save: Button

    private lateinit var titleEditText: EditText
    private lateinit var contentEditText: EditText

    private lateinit var bundle: Bundle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bundle = intent.extras!!

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        loading = binding.loading
        swipeRefreshLayout = binding.swipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener {
            getData()
        }

        delete = binding.delete
        delete.setOnClickListener {
            delete()
        }
        save = binding.save
        save.setOnClickListener {
            edit()
        }

        titleEditText = binding.title
        contentEditText = binding.content

        getData()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun getData() {
        swipeRefreshLayout.isRefreshing = true
        db.document("${Constants.userCollectionKey()}/${auth.currentUser?.uid}/${Constants.noteCollectionKey()}/${bundle.get("id").toString()}").get().addOnSuccessListener { snapshot ->
            val userData = snapshot.data
            supportActionBar?.title = userData?.get("title").toString()
            titleEditText.setText(userData?.get("title").toString())
            contentEditText.setText(userData?.get("content").toString())
        }.addOnFailureListener { error ->
            Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
        }
        swipeRefreshLayout.isRefreshing = false
    }

    private fun edit() {
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
        save.isEnabled = false

        val note = hashMapOf(
            "title" to title,
            "content" to content
        )
        val user = auth.currentUser
        if (user != null) {
            db.document("${Constants.userCollectionKey()}/${user.uid}/${Constants.noteCollectionKey()}/${bundle.get("id").toString()}").update(
                note as Map<String, Any>
            ).addOnSuccessListener {
                loading.visibility = View.GONE
                save.isEnabled = true
                Toast.makeText(applicationContext, R.string.success, Toast.LENGTH_LONG).show()
                finish()
            }.addOnFailureListener { error ->
                loading.visibility = View.GONE
                save.isEnabled = true
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }
        } else {
            loading.visibility = View.GONE
            save.isEnabled = true
            Toast.makeText(applicationContext, R.string.error, Toast.LENGTH_SHORT).show()
        }
    }

    private fun delete() {
        loading.visibility = View.VISIBLE
        delete.isEnabled = false

        val user = auth.currentUser
        if (user != null) {
            db.document("${Constants.userCollectionKey()}/${user.uid}/${Constants.noteCollectionKey()}/${bundle.get("id").toString()}").delete().addOnSuccessListener {
                loading.visibility = View.GONE
                delete.isEnabled = true
                Toast.makeText(applicationContext, R.string.success, Toast.LENGTH_LONG).show()
                finish()
            }.addOnFailureListener { error ->
                loading.visibility = View.GONE
                delete.isEnabled = true
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }
        } else {
            loading.visibility = View.GONE
            delete.isEnabled = true
            Toast.makeText(applicationContext, R.string.error, Toast.LENGTH_SHORT).show()
        }
    }
}