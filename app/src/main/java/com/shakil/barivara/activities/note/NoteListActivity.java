package com.shakil.barivara.activities.note;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.shakil.barivara.R;
import com.shakil.barivara.activities.onboard.MainActivity;
import com.shakil.barivara.adapter.NoteRecyclerAdapter;
import com.shakil.barivara.databinding.ActivityNoteListBinding;
import com.shakil.barivara.firebasedb.FirebaseCrudHelper;
import com.shakil.barivara.model.note.Note;
import com.shakil.barivara.utils.Tools;
import com.shakil.barivara.utils.UX;

import java.util.ArrayList;

public class NoteListActivity extends AppCompatActivity {
    private ActivityNoteListBinding activityNoteListBinding;
    private NoteRecyclerAdapter noteRecyclerAdapter;
    private ArrayList<Note> noteList;
    private LinearLayout LayoutNoNotes;
    private FirebaseCrudHelper firebaseCrudHelper;
    private UX ux;
    private Tools tools;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityNoteListBinding = DataBindingUtil.setContentView(this, R.layout.activity_note_list);

        init();
        setSupportActionBar(activityNoteListBinding.toolBar);

        activityNoteListBinding.toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NoteListActivity.this, MainActivity.class));
            }
        });
        bindUiWithComponents();
    }

    //region init objects
    private void init() {
        ux = new UX(this);
        tools = new Tools(this);
        firebaseCrudHelper = new FirebaseCrudHelper(this);
        LayoutNoNotes = findViewById(R.id.LayoutNoNotes);
    }
    //endregion

    //region perform all UI interactions
    private void bindUiWithComponents() {
        //region check internet connection
        if (tools.hasConnection()) {
            setData();
        } else {
            Toast.makeText(this, getString(R.string.no_internet_title), Toast.LENGTH_SHORT).show();
        }
        //endregion

        activityNoteListBinding.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }
    //endregion

    //region set recycler data
    private void setData() {
        ux.getLoadingView();
        firebaseCrudHelper.fetchAllNote("note", new FirebaseCrudHelper.onNoteDataFetch() {
            @Override
            public void onFetch(ArrayList<Note> objects) {
                noteList = objects;
                setRecyclerAdapter();
                ux.removeLoadingView();

                if (noteList.size() <= 0){
                    LayoutNoNotes.setVisibility(View.VISIBLE);
                }
                else{
                    LayoutNoNotes.setVisibility(View.GONE);
                }
            }
        });
    }
    //endregion


    private void setRecyclerAdapter() {
        noteRecyclerAdapter = new NoteRecyclerAdapter(noteList);
        activityNoteListBinding.mRecyclerViewNote.setLayoutManager(new GridLayoutManager(this, 2));
        activityNoteListBinding.mRecyclerViewNote.setAdapter(noteRecyclerAdapter);
        noteRecyclerAdapter.notifyDataSetChanged();
        noteRecyclerAdapter.setOnItemClickListener(new NoteRecyclerAdapter.onItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                startActivity(new Intent(NoteListActivity.this, NewNoteActivity.class)
                        .putExtra("note", note));
            }
        });
    }

    //region activity components
    @Override
    public void onBackPressed() {
        startActivity(new Intent(NoteListActivity.this, MainActivity.class));
    }
    //endregion
}
