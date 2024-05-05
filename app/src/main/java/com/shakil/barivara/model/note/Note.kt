package com.shakil.barivara.model.note

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.Exclude

class Note : Parcelable {
    var noteId: String = ""
    var title: String = ""
    var description: String = ""
    var date: String = ""
    var fireBaseKey: String = ""

    constructor()
    constructor(note: Parcel) {
        noteId = note.readString() ?: ""
        title = note.readString() ?: ""
        description = note.readString() ?: ""
        date = note.readString() ?: ""
        fireBaseKey = note.readString() ?: ""
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(noteId)
        dest.writeString(title)
        dest.writeString(description)
        dest.writeString(date)
        dest.writeString(fireBaseKey)
    }

    @Exclude
    fun toMap(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result["noteId"] = noteId
        result["title"] = title
        result["description"] = description
        result["date"] = date
        return result
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Note> {
        override fun createFromParcel(parcel: Parcel): Note {
            return Note(parcel)
        }

        override fun newArray(size: Int): Array<Note?> {
            return arrayOfNulls(size)
        }
    }
}
