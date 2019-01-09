package notes.bhanu.agrawal.notes;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import notes.bhanu.agrawal.notes.data.NotesRepository;
import notes.bhanu.agrawal.notes.data.entities.Note;

public class NotesViewModel  extends AndroidViewModel {
    private LiveData<List<Note>> notes;
    private Application application;
    private NotesRepository notesRepository;

    public NotesViewModel(@NonNull Application application) {
        super(application);
        notesRepository = new NotesRepository(application);
    }


    public LiveData<List<Note>> getNotes() {
        return notesRepository.getNotes();
    }

    public void setNotes(LiveData<List<Note>> notes) {
        this.notes = notes;
    }


    public void addNote(Note note){
        notesRepository.addNote(note);
    }

    public void deleteNote(Note note) {
        notesRepository.deleteNote(note);
    }
}
