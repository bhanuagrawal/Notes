package notes.bhanu.agrawal.notes.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.persistence.room.Room;
import android.content.Context;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import notes.bhanu.agrawal.notes.Util;
import notes.bhanu.agrawal.notes.data.entities.Note;

import static java.lang.Thread.sleep;

public class NotesRepository {

    private NotesDatabase database;
    private Executor executor;

    public NotesRepository(Context context) {
        database = Room.databaseBuilder(context,
                NotesDatabase.class, "notes-data").build();
        executor = Executors.newFixedThreadPool(5);
    }

    public LiveData<List<Note>> getNotes() {
        return database.notesDao().getAllNotes();
    }

    public void addNote(final Note note, MutableLiveData<Util.NoteSaveStatus> noteSaveStatusMutableLiveData) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                noteSaveStatusMutableLiveData.postValue(Util.NoteSaveStatus.SAVING);
                database.notesDao().insert(note);
/*                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
                noteSaveStatusMutableLiveData.postValue(Util.NoteSaveStatus.SAVED);

            }
        });
    }

    public void deleteNote(Note note) {
        database.notesDao().delete(note);
    }
}
