package com.scranaver.notes.ui.about

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.scranaver.notes.BuildConfig
import com.scranaver.notes.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAboutBinding
    private lateinit var version: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = "About"
        }

        version = binding.version
        version.text = BuildConfig.VERSION_NAME
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}