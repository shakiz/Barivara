package com.shakil.barivara.activities.note

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.shakil.barivara.R
import com.shakil.barivara.databinding.ActivityNewNoteBinding
import com.shakil.barivara.firebasedb.FirebaseCrudHelper
import com.shakil.barivara.model.note.Note
import com.shakil.barivara.utils.AppAnalytics
import com.shakil.barivara.utils.Constants
import com.shakil.barivara.utils.Constants.mUserId
import com.shakil.barivara.utils.PrefManager
import com.shakil.barivara.utils.Tools
import com.shakil.barivara.utils.UtilsForAll
import com.shakil.barivara.utils.Validation
import java.util.Locale
import java.util.UUID

class NewNoteActivity : AppCompatActivity() {
    private lateinit var activityNewNoteBinding: ActivityNewNoteBinding
    private lateinit var textToSpeech: TextToSpeech
    private var isTextToSpeechOn = false
    private var note: Note = Note()
    private var command = "add"
    private var firebaseCrudHelper = FirebaseCrudHelper(this)
    private val hashMap: Map<String?, Array<String>?> = HashMap()
    private var tools = Tools(this)
    private var validation = Validation(this, hashMap)
    private var appAnalytics = AppAnalytics(this)
    private var utilsForAll = UtilsForAll(this)
    private lateinit var prefManager: PrefManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityNewNoteBinding = DataBindingUtil.setContentView(this, R.layout.activity_new_note)
        prefManager = PrefManager(this)
        intentData()
        setSupportActionBar(activityNewNoteBinding.toolBar)
        activityNewNoteBinding.toolBar.setNavigationOnClickListener { finish() }
        bindUiWithComponents()
        loadData()
    }

    private fun intentData(){
        if (intent.extras != null) {
            if (intent.getParcelableExtra<Parcelable?>("note") != null) {
                note = intent.getParcelableExtra("note")!!
            }
        }
    }

    private fun loadData() {
        if (note.noteId != null) {
            command = "update"
            activityNewNoteBinding.Title.setText(note.title)
            activityNewNoteBinding.Description.setText(note.description)
            activityNewNoteBinding.listenHint.visibility = View.VISIBLE
            activityNewNoteBinding.listenIcon.visibility = View.VISIBLE
        }
    }

    private fun bindUiWithComponents() {
        validation.setEditTextIsNotEmpty(
            arrayOf("Title", "Description"),
            arrayOf(
                getString(R.string.title_validation),
                getString(R.string.description_validation)
            )
        )
        validation.setSpinnerIsNotEmpty(arrayOf("TenantTypeId", "StartingMonthId"))
        activityNewNoteBinding.listenIcon.setOnClickListener { listenNote() }
        appAnalytics.registerEvent("newNote", appAnalytics.setData("newNoteActivity", ""))
        activityNewNoteBinding.save.setOnClickListener { saveOrUpdateNote() }
        try {
            textToSpeech = TextToSpeech(this) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeech.setLanguage(Locale.US)
                    textToSpeech.setSpeechRate(0.7f)
                    textToSpeech.setPitch(1f)
                } else {
                    Log.v(Constants.TAG, "Text To Speech Init Failed")
                }
            }
        } catch (e: Exception) {
            Log.v(Constants.TAG, "::NewNoteActivity" + e.message)
            e.printStackTrace()
        }
    }

    private fun saveOrUpdateNote() {
        if (validation.isValid) {
            if (tools.hasConnection()) {
                note.title = activityNewNoteBinding.Title.text.toString()
                note.description = activityNewNoteBinding.Description.text.toString()
                note.date = utilsForAll.dateTimeWithPM
                if (command == "add") {
                    note.noteId = UUID.randomUUID().toString()
                    firebaseCrudHelper.add(note, "note", prefManager.getString(mUserId))
                } else {
                    firebaseCrudHelper.update(
                        note,
                        note.fireBaseKey,
                        "note",
                        prefManager.getString(mUserId)
                    )
                }
                appAnalytics.registerEvent(
                    "newNote",
                    appAnalytics.setData("newNoteActivity", "Note Created or Updated.")
                )
                Toast.makeText(applicationContext, R.string.success, Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@NewNoteActivity, NoteListActivity::class.java))
            } else {
                Toast.makeText(
                    this@NewNoteActivity,
                    getString(R.string.no_internet_title),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun listenNote() {
        if (isTextToSpeechOn) {
            textToSpeech.stop()
            activityNewNoteBinding.listenIcon.setImageDrawable(resources.getDrawable(R.drawable.ic_record_voice_over))
            isTextToSpeechOn = false
            Toast.makeText(applicationContext, "Text to Speech Stopped", Toast.LENGTH_SHORT).show()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                textToSpeech.speak(
                    "Dear user your note title is" + activityNewNoteBinding.Title
                        .text.toString() + "Now you will hear your note description" +
                            activityNewNoteBinding.Description.text.toString(),
                    TextToSpeech.QUEUE_FLUSH, null, "onNote"
                )
            } else {
                textToSpeech.speak(
                    "Dear user your note title is" + activityNewNoteBinding.Title
                        .text.toString() + "Now you will hear your note description" +
                            activityNewNoteBinding.Description.text.toString(),
                    TextToSpeech.QUEUE_FLUSH, null
                )
            }
            activityNewNoteBinding.listenIcon.setImageDrawable(resources.getDrawable(R.drawable.ic_stop_circle))
            isTextToSpeechOn = true
            Toast.makeText(applicationContext, "Text to Speech Started", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        textToSpeech.shutdown()
    }

    override fun onStop() {
        super.onStop()
        textToSpeech.stop()
    }
}
