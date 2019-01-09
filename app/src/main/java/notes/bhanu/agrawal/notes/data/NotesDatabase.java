package notes.bhanu.agrawal.notes.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import notes.bhanu.agrawal.notes.data.dao.NotesDao;
import notes.bhanu.agrawal.notes.data.entities.Note;


@Database(entities = {Note.class}
        , version = 1)
@TypeConverters({Converters.class})
public abstract class NotesDatabase extends RoomDatabase {

    public abstract NotesDao notesDao();

}
