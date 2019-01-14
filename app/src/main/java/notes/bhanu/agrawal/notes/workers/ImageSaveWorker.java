package notes.bhanu.agrawal.notes.workers;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import notes.bhanu.agrawal.notes.Constants;
import notes.bhanu.agrawal.notes.Util;

import static android.support.constraint.Constraints.TAG;

public class ImageSaveWorker extends Worker {

    public ImageSaveWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {


        try {
            String resourceUri = getInputData()
                    .getString(Constants.KEY_IMAGE_URI);

            String imagePath = saveToInternalStorage(getApplicationContext(), Uri.parse(resourceUri));
            Data outputData = new Data.Builder()
                    .putString(Constants.IMAGE_URL, imagePath)
                    .build();
            return Result.success(outputData);
        } catch (Exception exception) {
            Log.e(TAG, "Unable to save image to Gallery", exception);
            return Result.failure();
        }
    }

    public String saveToInternalStorage(Context context, Uri uri) throws IOException {
        Bitmap bitmapImage = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        ContextWrapper cw = new ContextWrapper(context.getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);


        String name = "image_"+ String.valueOf(System.currentTimeMillis() % 1000) + ".jpg";
        File mypath=new File(directory,name);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mypath.getAbsolutePath();
    }
}
