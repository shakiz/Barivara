package com.shakil.barivara.model.note;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Note implements Parcelable {
    private String NoteId;
    private String Title;
    private String Description;
    private String Date;
    private String FireBaseKey;

    public Note() {
    }


    protected Note(Parcel in) {
        NoteId = in.readString();
        Title = in.readString();
        Description = in.readString();
        Date = in.readString();
        FireBaseKey = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(NoteId);
        dest.writeString(Title);
        dest.writeString(Description);
        dest.writeString(Date);
        dest.writeString(FireBaseKey);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("noteId", NoteId);
        result.put("title", Title);
        result.put("description", Description);
        result.put("date", Date);

        return result;
    }

    public String getNoteId() {
        return NoteId;
    }

    public void setNoteId(String noteId) {
        NoteId = noteId;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getFireBaseKey() {
        return FireBaseKey;
    }

    public void setFireBaseKey(String fireBaseKey) {
        FireBaseKey = fireBaseKey;
    }

    public static Creator<Note> getCREATOR() {
        return CREATOR;
    }
}
