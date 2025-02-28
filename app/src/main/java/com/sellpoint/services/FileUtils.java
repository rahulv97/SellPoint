package com.sellpoint.services;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class FileUtils {

    public static String getPath(Context context, Uri uri) {
        if (DocumentsContract.isDocumentUri(context, uri)) {
            if (isDownloadsDocument(uri)) {
                String id = DocumentsContract.getDocumentId(uri);
                if (id.startsWith("raw:")) {
                    return id.replaceFirst("raw:", "");
                }
                Uri contentUri = DocumentsContract.buildDocumentUriUsingTree(uri, id);
                return saveFileToPersistentStorage(context, contentUri);
            } else if (isMediaDocument(uri)) {
                String docId = DocumentsContract.getDocumentId(uri);
                String[] split = docId.split(":");
                String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = "_id=?";
                String[] selectionArgs = new String[]{split[1]};
                return saveFileToPersistentStorage(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return saveFileToPersistentStorage(context, uri);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null; // Default case
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private static String saveFileToPersistentStorage(Context context, Uri uri) {
        return saveFileToPersistentStorage(context, uri, null, null);
    }

/*    private static String saveFileToPersistentStorage(Context context, Uri uri, String selection, String[] selectionArgs) {
        try {
            ContentResolver contentResolver = context.getContentResolver();
            Cursor cursor = contentResolver.query(uri, null, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                String fileName = cursor.getString(nameIndex);
                File file = new File(context.getFilesDir(), fileName); // Use internal storage

                InputStream inputStream = contentResolver.openInputStream(uri);
                FileOutputStream outputStream = new FileOutputStream(file);
                byte[] buffers = new byte[1024];
                int read;
                while ((read = inputStream.read(buffers)) != -1) {
                    outputStream.write(buffers, 0, read);
                }
                inputStream.close();
                outputStream.close();
                cursor.close();

                return file.getAbsolutePath();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }*/

    private static String saveFileToPersistentStorage(Context context, Uri uri, String selection, String[] selectionArgs) {
        try {
            ContentResolver contentResolver = context.getContentResolver();
            Cursor cursor = contentResolver.query(uri, null, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                String fileName = cursor.getString(nameIndex);
                File compressedFile = new File(context.getFilesDir(), "COMPRESSED_" + fileName); // Save compressed file

                InputStream inputStream = contentResolver.openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                // Compress the image
                FileOutputStream outputStream = new FileOutputStream(compressedFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream); // Adjust quality (80 is a good balance)
                outputStream.flush();
                outputStream.close();
                inputStream.close();
                cursor.close();

                return compressedFile.getAbsolutePath();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}

