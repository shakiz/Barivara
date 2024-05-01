package com.shakil.barivara.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DroidFileManager {

    public boolean isFileExist(File backupDBPath){
        if (backupDBPath.exists()){
            return true;
        }
        return false;
    }

    public File createFile(File environment, String path){
        if (environment == null){
            return new File(path);
        }
        else{
            return new File(environment, path);
        }
    }

    //create an InputStream from the URI and then, from this, create an OutputStream by copying the contents of the input stream.
    //http://jtdz-solenoids.com/stackoverflow_/questions/57093479/get-real-path-from-uri-data-is-deprecated-in-android-q
    public String getPath(Context context, Uri uri) {
        final ContentResolver contentResolver = context.getContentResolver();
        if (contentResolver == null)
            return null;

        String filePath = context.getApplicationInfo().dataDir + File.separator
                + System.currentTimeMillis();

        File file = new File(filePath);
        try {
            InputStream inputStream = contentResolver.openInputStream(uri);
            if (inputStream == null)
                return null;

            OutputStream outputStream = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0)
                outputStream.write(buf, 0, len);

            outputStream.close();
            inputStream.close();
        } catch (IOException ignore) {
            return null;
        }

        return file.getAbsolutePath();
    }

    public boolean createFolder(String path){
        boolean isAlreadyCreated = false;
        File dir = new File(path);
        if (!dir.exists()){
            if (dir.mkdir()){
                isAlreadyCreated = true;
            }
        }
        return isAlreadyCreated;
    }

    public boolean folderExists(String path){
        boolean isFolderExists = false;
        File dir = new File(path);
        isFolderExists = !dir.exists();
        return isFolderExists;
    }
}
