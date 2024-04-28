package com.shakil.barivara.activities.note

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.shakil.barivara.R
import com.shakil.barivara.activities.onboard.MainActivity
import com.shakil.barivara.adapter.NoteRecyclerAdapter
import com.shakil.barivara.databinding.ActivityNoteListBinding
import com.shakil.barivara.firebasedb.FirebaseCrudHelper
import com.shakil.barivara.model.note.Note
import com.shakil.barivara.utils.AppAnalytics
import com.shakil.barivara.utils.Tools
import com.shakil.barivara.utils.UX

class NoteListActivity : AppCompatActivity() {
    private lateinit var activityNoteListBinding: ActivityNoteListBinding
    private var noteList: ArrayList<Note>? = null
    private var firebaseCrudHelper = FirebaseCrudHelper(this)
    private lateinit var ux: UX
    private var appAnalytics = AppAnalytics(this)
    private var tools = Tools(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityNoteListBinding = DataBindingUtil.setContentView(this, R.layout.activity_note_list)
        init()
        setSupportActionBar(activityNoteListBinding.toolBar)
        activityNoteListBinding.toolBar.setNavigationOnClickListener {
            startActivity(
                Intent(
                    this@NoteListActivity,
                    MainActivity::class.java
                )
            )
        }
        bindUiWithComponents()
    }

    private fun init() {
        ux = UX(this)
    }

    private fun bindUiWithComponents() {
        appAnalytics.registerEvent("noteList", appAnalytics.setData("listActivity", ""))
        if (tools.hasConnection()) {
            setData()
        } else {
            Toast.makeText(this, getString(R.string.no_internet_title), Toast.LENGTH_SHORT).show()
        }
        activityNoteListBinding.add.setOnClickListener { //region send analytics report to firebase
            appAnalytics.registerEvent("noteList", appAnalytics.setData("newNote", ""))
            startActivity(Intent(this@NoteListActivity, NewNoteActivity::class.java))
        }
    }

    private fun setData() {
        ux.getLoadingView()
        firebaseCrudHelper.fetchAllNote("note") { objects ->
            noteList = objects
            setRecyclerAdapter()
            ux.removeLoadingView()
            if ((noteList?.size ?: 0) <= 0) {
                activityNoteListBinding.noDataLayout.LayoutNoNotes.visibility = View.VISIBLE
            } else {
                activityNoteListBinding.noDataLayout.LayoutNoNotes.visibility = View.GONE
            }
        }
    }

    private fun setRecyclerAdapter() {
        val noteRecyclerAdapter = NoteRecyclerAdapter(noteList)
        activityNoteListBinding.mRecyclerViewNote.layoutManager = GridLayoutManager(this, 2)
        activityNoteListBinding.mRecyclerViewNote.adapter = noteRecyclerAdapter
        noteRecyclerAdapter.notifyDataSetChanged()
        noteRecyclerAdapter.setOnItemClickListener { note ->
            startActivity(
                Intent(this@NoteListActivity, NewNoteActivity::class.java)
                    .putExtra("note", note)
            )
        }
    }
}
