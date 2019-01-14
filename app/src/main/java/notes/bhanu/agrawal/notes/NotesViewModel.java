package notes.bhanu.agrawal.notes;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkContinuation;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import notes.bhanu.agrawal.notes.data.NotesRepository;
import notes.bhanu.agrawal.notes.data.entities.Note;
import notes.bhanu.agrawal.notes.workers.ImageSaveWorker;
import notes.bhanu.agrawal.notes.workers.NoteSaveWorker;

import static notes.bhanu.agrawal.notes.Constants.KEY_IMAGE_URI;
import static notes.bhanu.agrawal.notes.Constants.KEY_NOTE;
import static notes.bhanu.agrawal.notes.Constants.TAG_IMAGE_SAVE_WORK;

public class NotesViewModel  extends AndroidViewModel {

    private final WorkManager mWorkManager;
    private LiveData<List<WorkInfo>> mImageSaveWorkInfo;
    private Application application;
    private NotesRepository notesRepository;
    private String savedImageUrl;
    private MutableLiveData<Util.NoteSaveStatus> noteSaveStatusMutableLiveData;
    private OneTimeWorkRequest imageSaveWork;


    public NotesViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        notesRepository = new NotesRepository(application.getApplicationContext());
        mWorkManager = WorkManager.getInstance();
        mImageSaveWorkInfo = mWorkManager.getWorkInfosForUniqueWorkLiveData(TAG_IMAGE_SAVE_WORK);

    }

    public OneTimeWorkRequest getImageSaveWork() {
        return imageSaveWork;
    }

    public void setImageSaveWork(OneTimeWorkRequest imageSaveWork) {
        this.imageSaveWork = imageSaveWork;
    }

    public MutableLiveData<Util.NoteSaveStatus> getNoteSaveStatusMutableLiveData() {
        if(noteSaveStatusMutableLiveData == null){
            noteSaveStatusMutableLiveData = new MutableLiveData<>();
            noteSaveStatusMutableLiveData.postValue(Util.NoteSaveStatus.NOT_SAVED);
        }
        return noteSaveStatusMutableLiveData;
    }

    public void setNoteSaveStatusMutableLiveData(MutableLiveData<Util.NoteSaveStatus> noteSaveStatusMutableLiveData) {
        this.noteSaveStatusMutableLiveData = noteSaveStatusMutableLiveData;
    }

    public LiveData<List<WorkInfo>> getmImageSaveWorkInfo() {
        return mImageSaveWorkInfo;
    }

    public void setmImageSaveWorkInfo(LiveData<List<WorkInfo>> mImageSaveWorkInfo) {
        this.mImageSaveWorkInfo = mImageSaveWorkInfo;
    }

    public String getSavedImageUrl() {
        if(savedImageUrl == null){
            savedImageUrl = "";
        }
        return savedImageUrl;
    }

    public void setSavedImageUrl(String savedImageUrl) {
        this.savedImageUrl = savedImageUrl;
    }

    public LiveData<List<Note>> getNotes() {
        return notesRepository.getNotes();
    }

    public void addNote(Note note){
        notesRepository.addNote(note, getNoteSaveStatusMutableLiveData());
    }


    public void deleteNote(Note note) {
        notesRepository.deleteNote(note);
    }

    private Data createInputDataForUri(Uri uri) {
        Data.Builder builder = new Data.Builder();
        if (uri != null) {
            builder.putString(KEY_IMAGE_URI, uri.toString());
        }
        return builder.build();
    }



    public void saveImage(Uri uri) {

        imageSaveWork = new OneTimeWorkRequest.Builder(ImageSaveWorker.class)
                .setInputData(createInputDataForUri(uri))
                .build();

        mWorkManager.enqueueUniqueWork(Constants.TAG_IMAGE_SAVE_WORK,
                        ExistingWorkPolicy.REPLACE,
                        imageSaveWork);
    }
}
