package com.shakil.barivara.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.shakil.barivara.R;
import com.shakil.barivara.model.note.Note;

import java.util.ArrayList;

public class NoteRecyclerAdapter extends RecyclerView.Adapter<NoteRecyclerAdapter.ViewHolder> {
    private ArrayList<Note> allNotes;

    public NoteRecyclerAdapter(ArrayList<Note> allNotes) {
        this.allNotes = allNotes;
    }

    //region click adapter
    public onItemClickListener onItemClickListener;
    public interface onItemClickListener{
        void onItemClick(Note note);
    }

    public void setOnItemClickListener(onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    //endregion

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_layout_note_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Note note = allNotes.get(position);
        holder.Title.setText(note.getTitle());
        holder.Date.setText(note.getDate());
        holder.Description.setText(note.getDescription());
        holder.cardItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(note);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (allNotes.size() > 0) return allNotes.size();
        else return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView Title,Date,Description;
        CardView cardItemLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Title = itemView.findViewById(R.id.Title);
            Date = itemView.findViewById(R.id.Date);
            Description = itemView.findViewById(R.id.Description);
            cardItemLayout = itemView.findViewById(R.id.cardItemLayout);
        }
    }
}
