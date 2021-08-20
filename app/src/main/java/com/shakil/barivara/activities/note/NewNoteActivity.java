package com.shakil.barivara.activities.note;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.shakil.barivara.R;
import com.shakil.barivara.databinding.ActivityNewNoteBinding;
import com.shakil.barivara.firebasedb.FirebaseCrudHelper;
import com.shakil.barivara.model.note.Note;
import com.shakil.barivara.utils.Tools;
import com.shakil.barivara.utils.Validation;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class NewNoteActivity extends AppCompatActivity {
    private ActivityNewNoteBinding activityNewNoteBinding;
    private ImageView listenIcon;
    private TextView listenHint;
    private TextToSpeech textToSpeech;
    private boolean isTextToSpeechOn = false;
    private Note note = new Note();
    private String command = "add";
    private FirebaseCrudHelper firebaseCrudHelper;
    private Map<String, String[]> hashMap = new HashMap();
    private Tools tools;
    private Validation validation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityNewNoteBinding = DataBindingUtil.setContentView(this, R.layout.activity_new_note);

        //region get intent data
        getIntentData();
        //endregion

        init();
        setSupportActionBar(activityNewNoteBinding.toolBar);

        activityNewNoteBinding.toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        bindUiWithComponents();

        //region load intent data to UI
        loadData();
        //endregion
    }

    //region get intent data
    private void getIntentData(){
        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().getParcelable("note") != null){
                note = getIntent().getExtras().getParcelable("note");
            }
        }
    }
    //endregion

    //region load intent data to UI
    private void loadData(){
        if (note.getNoteId() != null) {
            command = "update";
            activityNewNoteBinding.Title.setText(note.getTitle());
            activityNewNoteBinding.Description.setText(note.getDescription());
            listenHint.setVisibility(View.VISIBLE);
            listenIcon.setVisibility(View.VISIBLE);

            listenIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listenNote();
                }
            });
        }
    }
    //endregion

    private void init() {
        firebaseCrudHelper = new FirebaseCrudHelper(this);
        validation = new Validation(this, hashMap);
        tools = new Tools(this);
        listenIcon = findViewById(R.id.listenIcon);
        listenHint = findViewById(R.id.listenHint);
    }

    private void bindUiWithComponents() {//region validation
        validation.setEditTextIsNotEmpty(new String[]{"Title", "Description"},
                new String[]{getString(R.string.title_validation), getString(R.string.description_validation)});
        validation.setSpinnerIsNotEmpty(new String[]{"TenantTypeId","StartingMonthId"});
        //endregion
        activityNewNoteBinding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveOrUpdateNote();
            }
        });

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                textToSpeech.setLanguage(Locale.US);
                textToSpeech.setSpeechRate(0.7f);
                textToSpeech.setPitch(1);
            }
        });
    }

    private void saveOrUpdateNote() {
        //region validation and save data
        if (validation.isValid()){
            if (tools.hasConnection()) {
                note.setTitle(activityNewNoteBinding.Title.getText().toString());
                note.setDescription(activityNewNoteBinding.Description.getText().toString());
                if (command.equals("add")) {
                    note.setNoteId(UUID.randomUUID().toString());
                    firebaseCrudHelper.add(note, "note");
                } else {
                    firebaseCrudHelper.update(note, note.getFireBaseKey(), "note");
                }
                Toast.makeText(getApplicationContext(),R.string.success, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(NewNoteActivity.this, NoteListActivity.class));
            } else {
                Toast.makeText(NewNoteActivity.this, getString(R.string.no_internet_title), Toast.LENGTH_SHORT).show();
            }
        }
        //endregion
    }

    private String getCurrentDate() {
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c);
        return formattedDate;
    }

    private void listenNote() {
        if (isTextToSpeechOn) {
            textToSpeech.stop();
            listenIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_record_voice_over));
            isTextToSpeechOn = false;
            Toast.makeText(getApplicationContext(), "Text to Speech Stopped", Toast.LENGTH_SHORT).show();
        }
        else {
            textToSpeech.speak("Dear user your note title is"+activityNewNoteBinding.Title
                            .getText().toString() + "Now you will hear your note description"+
                            activityNewNoteBinding.Description.getText().toString(),
                    TextToSpeech.QUEUE_ADD, null, "onNote");
            listenIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_stop_circle));
            isTextToSpeechOn = true;
            Toast.makeText(getApplicationContext(), "Text to Speech Started", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(NewNoteActivity.this,NoteListActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        textToSpeech.shutdown();
    }

    @Override
    protected void onStop() {
        super.onStop();
        textToSpeech.stop();
    }
}
