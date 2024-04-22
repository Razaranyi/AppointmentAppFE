package com.example.eassyappointmentfe.util;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtils {


    /**
     * Converts an image Uri to a byte array.
     *
     * @param context The application context.
     * @param imageUri The Uri of the image to convert.
     * @param compressFormat The format to compress the image (e.g., Bitmap.CompressFormat.JPEG).
     * @param quality The compression quality, between 0 and 100.
     * @return The byte array of the compressed image, or null if an error occurred.
     */
    public static byte[] convertImageToByteArray(Context context, Uri imageUri, Bitmap.CompressFormat compressFormat, int quality) {
        InputStream imageStream = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            imageStream = context.getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
            if (bitmap == null) {
                throw new IllegalArgumentException("Cannot decode bitmap from stream");
            }
            byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(compressFormat, quality, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();

        } finally {
            if (imageStream != null) {
                try {
                    imageStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (byteArrayOutputStream != null) {
                try {
                    byteArrayOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

}
