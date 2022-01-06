package com.scranaver.notes

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.scranaver.notes.data.Constants
import com.scranaver.notes.databinding.ActivityMainBinding
import com.scranaver.notes.ui.about.AboutActivity
import com.scranaver.notes.ui.login.LoginActivity
import com.scranaver.notes.ui.note.AddNoteActivity
import com.scranaver.notes.ui.profile.ProfileActivity

class MainActivity: AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var auth: FirebaseAuth = Firebase.auth
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.fab.setOnClickListener {
            startActivity(Intent(this, AddNoteActivity::class.java))
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        navView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.setNavigationItemSelectedListener(this)

        getData()
    }

    override fun onResume() {
        super.onResume()
        getData()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_profile -> {
                startActivity(Intent(this, ProfileActivity::class.java))
                return true
            }
            R.id.nav_add_note -> {
                startActivity(Intent(this, AddNoteActivity::class.java))
                return true
            }
            R.id.nav_about -> {
                startActivity(Intent(this, AboutActivity::class.java))
                return true
            }
            R.id.nav_logout -> {
                Firebase.auth.signOut()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                return true
            }
        }
        return true
    }

    private fun getData() {
        db.document("${Constants.userCollectionKey()}/${auth.currentUser?.uid}").get().addOnSuccessListener { snapshot ->
            val userData = snapshot.data
            val nameTextView = navView.getHeaderView(0).findViewById<TextView>(R.id.name)
            val emailTextView = navView.getHeaderView(0).findViewById<TextView>(R.id.email)
            nameTextView.text = userData?.get("name").toString()
            emailTextView.text = userData?.get("email").toString()
        }.addOnFailureListener { error ->
            Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
        }
    }
}