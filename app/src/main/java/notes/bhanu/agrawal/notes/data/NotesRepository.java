package notes.bhanu.agrawal.notes.data;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Room;

import java.util.List;
import notes.bhanu.agrawal.notes.data.entities.Note;

public class NotesRepository {

    private NotesDatabase database;

    public NotesRepository(Application application) {
        database = Room.databaseBuilder(application,
                NotesDatabase.class, "notes-data").build();
    }

    public LiveData<List<Note>> getNotes() {
        return database.notesDao().getAllNotes();
    }

    public void addNote(final Note note) {
        database.notesDao().insert(note);
    }

    public void deleteNote(Note note) {
        database.notesDao().delete(note);
    }
}
