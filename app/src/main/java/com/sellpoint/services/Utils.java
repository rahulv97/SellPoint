package com.sellpoint.services;

import android.util.Log;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import java.io.File;

public class Utils {

    // Convert a string to RequestBody
    public static RequestBody createPartFromString(String value) {
        return RequestBody.create(MediaType.parse("text/plain"), value);
    }

    // Convert a file to MultipartBody.Part
    public static MultipartBody.Part prepareFilePart(String partName, File file) {
        Log.d("FileExistance", "prepareFilePart: "+file.getName()+ " "+ file.exists());
        if (file != null && file.exists()) {
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
        }
        return null; // Return null if the file is not provided
    }
}
