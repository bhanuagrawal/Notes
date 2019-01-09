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

    public static String saveToInternalStorage(Context context, Uri uri) throws IOException {
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
