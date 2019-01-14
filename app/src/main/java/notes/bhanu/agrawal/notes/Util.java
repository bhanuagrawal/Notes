package notes.bhanu.agrawal.notes;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Time;

public class Util {
    public enum NoteSaveStatus{
        NOT_SAVED, SAVING, SAVED;
    }
}
