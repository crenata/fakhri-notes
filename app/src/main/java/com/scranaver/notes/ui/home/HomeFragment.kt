package com.scranaver.notes.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.scranaver.notes.data.Constants
import com.scranaver.notes.databinding.FragmentHomeBinding

@SuppressLint("NotifyDataSetChanged")
class HomeFragment : Fragment() {
    private var auth: FirebaseAuth = Firebase.auth
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private var noteList = ArrayList<Note>()
    private lateinit var noteAdapter: NoteAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        swipeRefreshLayout = binding.swipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener {
            getAllData()
        }

        binding.recyclerview.layoutManager = LinearLayoutManager(context)
        noteAdapter = NoteAdapter(noteList, requireContext())
        binding.recyclerview.adapter = noteAdapter

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        getAllData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getAllData() {
        noteList.clear()
        swipeRefreshLayout.isRefreshing = true
        db.collection("${Constants.userCollectionKey()}/${auth.currentUser?.uid}/${Constants.noteCollectionKey()}").get().addOnSuccessListener { collectionNotes ->
            for (note in collectionNotes.documents) {
                val data = note.data
                noteList.add(Note(note.id, data?.get("title").toString(), data?.get("content").toString()))
            }
            noteAdapter.notifyDataSetChanged()
        }.addOnFailureListener { error ->
            Log.e(Constants.logKey(), "Error: ", error)
        }
        swipeRefreshLayout.isRefreshing = false
    }
}