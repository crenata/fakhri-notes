package com.scranaver.notes.ui.home

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.scranaver.notes.databinding.ItemsBinding
import com.scranaver.notes.ui.detail.DetailActivity

class NoteAdapter(private var noteList: List<Note>, private var context: Context): RecyclerView.Adapter<NoteAdapter.ViewHolder>() {
    inner class ViewHolder(val noteBinding: ItemsBinding): RecyclerView.ViewHolder(noteBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val noteBinding = ItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(noteBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            val noteItem: Note = noteList[position]
            with(noteItem) {
                val noteId: String = this.id
                noteBinding.title.text = this.title
                noteBinding.content.text = this.content
                noteBinding.card.setOnClickListener {
                    val intent: Intent = Intent(context, DetailActivity::class.java).apply {
                        putExtra("id", noteId)
                    }
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return noteList.size
    }
}