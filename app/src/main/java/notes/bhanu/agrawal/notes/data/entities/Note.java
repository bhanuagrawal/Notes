package notes.bhanu.agrawal.notes.data.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

@Entity
public class Note {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "notiid")
    @NonNull
    private int noteId;

    @ColumnInfo(name = "title")
    @NonNull
    private String title;

    @ColumnInfo(name = "text")
    @NonNull
    private String text;

    @ColumnInfo(name = "timeCreated")
    private Date timeCreated;

    @ColumnInfo(name = "imageURL")
    private String imageURL;

    public Note(String title, String text, Date timeCreated, String imageURL) {
        this.title = title;
        this.text = text;
        this.timeCreated = timeCreated;
        this.imageURL = imageURL;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    @NonNull
    public int getNoteId() {
        return noteId;
    }

    public void setNoteId(@NonNull int noteId) {
        this.noteId = noteId;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @NonNull
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }
}
