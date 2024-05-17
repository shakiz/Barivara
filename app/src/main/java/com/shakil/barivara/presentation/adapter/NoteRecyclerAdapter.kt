package com.shakil.barivara.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.shakil.barivara.R
import com.shakil.barivara.data.model.note.Note

class NoteRecyclerAdapter(private val allNotes: ArrayList<Note>) :
    RecyclerView.Adapter<NoteRecyclerAdapter.ViewHolder>() {
    private var itemClickListener: onItemClickListener? = null

    interface onItemClickListener {
        fun onItemClick(note: Note?)
    }

    fun setOnItemClickListener(onItemClickListener: onItemClickListener?) {
        this.itemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_layout_note_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = allNotes[position]
        holder.Title.text = note.title
        holder.Date.text = note.date
        holder.Description.text = note.description
        holder.cardItemLayout.setOnClickListener { itemClickListener?.onItemClick(note) }
    }

    override fun getItemCount(): Int {
        return if (allNotes.size > 0) allNotes.size else 0
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var Title: TextView
        var Date: TextView
        var Description: TextView
        var cardItemLayout: CardView

        init {
            Title = itemView.findViewById(R.id.Title)
            Date = itemView.findViewById(R.id.Date)
            Description = itemView.findViewById(R.id.Description)
            cardItemLayout = itemView.findViewById(R.id.cardItemLayout)
        }
    }
}
