package com.shared.storage.utilities;

import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.IOException;

public class SharedFileUtils {

    public static final String TAG = SharedFileUtils.class.getSimpleName();

    private SharedFileUtils() {
        throw new UnsupportedOperationException("You can't create instance of Util class. Please use as static..");
    }

    /**
     * Create directory
     *
     * Environment.getExternalStorageDirectory(); deprecated in Android Q ( API LEVEL 29 )
     * Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES); deprecated in Android Q ( API LEVEL 29 )
     *
     * @param directoryName
     * @return directory /storage/emulated/0/AppName/Pictures
     */
    public static File createDirectory(String directoryName) {
        File extStorageDirectory = Environment.getExternalStorageDirectory();
        String appRootDirectory = extStorageDirectory + File.separator + directoryName + File.separator;

        String storagePathString = appRootDirectory + Environment.DIRECTORY_PICTURES + File.separator;
        File storageDirectory = new File(storagePathString);

        if (!storageDirectory.exists())
        {
            if (!storageDirectory.mkdirs())
            {
                Log.e(TAG, "Oops! Create Directory Failed....");
                return null;
            }
            else
            {
                Log.e(TAG, "Directory Create Successful....");
            }
        }
        else
        {
            Log.e(TAG, "Directory Already Exit....");
        }

        return storageDirectory;
    }

    /**
     * Create file
     *
     * @param directory
     * @param fileExtension
     * @param fileName
     * @return created file /storage/emulated/0/AppName/Pictures/externalFile.txt
     * @throws IOException
     */
    public static File createFile(File directory, String fileExtension, String fileName) {
        File file = new File(directory, fileName+"."+fileExtension);

        if (!file.exists())
        {
            try {
                if (!file.createNewFile())
                {
                    Log.e(TAG, "Oops! Create File Failed....");
                    return null;
                }
                else
                {
                    Log.e(TAG, "File Create Successful....");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
        {
            Log.e(TAG, "File Already Exit....");
        }

        return file;
    }
}
