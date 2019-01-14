package notes.bhanu.agrawal.notes.workers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import androidx.work.Worker;
import androidx.work.WorkerParameters;
import notes.bhanu.agrawal.notes.Constants;
import notes.bhanu.agrawal.notes.data.NotesRepository;
import notes.bhanu.agrawal.notes.data.entities.Note;

import static android.support.constraint.Constraints.TAG;

public class NoteSaveWorker extends Worker {

    public NoteSaveWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            return Result.success();
        } catch (Exception exception) {
            Log.e(TAG, "Unable to save note", exception);
            return Result.failure();
        }
    }
}
