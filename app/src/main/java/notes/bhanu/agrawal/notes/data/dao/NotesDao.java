package notes.bhanu.agrawal.notes.data.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import notes.bhanu.agrawal.notes.data.entities.Note;

@Dao
public interface NotesDao {


    @Query("SELECT * from Note order by Note.notiid desc")
    public LiveData<List<Note>> getAllNotes();

    @Insert
    public void insert(Note note);

    @Delete
    public void delete(Note note);
}
