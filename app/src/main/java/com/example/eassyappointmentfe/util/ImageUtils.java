package com.example.eassyappointmentfe.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Utility class to handle image selection and conversion for the application.
 */
public class ImageUtils {

    /**
     * Handles the selected image URI, converts it to a byte array, and sets it to the provided ImageView.
     * It compresses the image with specified format and quality.
     *
     * @param context         The context where this operation is being performed.
     * @param uri             The URI of the selected image.
     * @param imageView       The ImageView to display the selected image.
     * @param compressFormat  The format to compress the image (e.g., Bitmap.CompressFormat.JPEG).
     * @param quality         The quality of the compression (0-100).
     */
    public static void handleImageSelection(Context context, Uri uri, ImageView imageView, Bitmap.CompressFormat compressFormat, int quality) {
        byte[] imageBytes = convertImageToByteArray(context, uri, compressFormat, quality);
        if (imageBytes != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            imageView.setImageBitmap(bitmap);
            imageView.setTag(imageBytes); // Tag used to store the image data within the view for easy access
        } else {
            Toast.makeText(context, "Failed to process selected image", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Converts an image URI to a byte array with the specified compression.
     *
     * @param context         The context used to resolve the image URI.
     * @param imageUri        The URI of the image to convert.
     * @param compressFormat  The format to compress the image (e.g., Bitmap.CompressFormat.JPEG).
     * @param quality         The quality of the compression (0-100).
     * @return                The byte array representation of the image, or null if an error occurred.
     */
    public static byte[] convertImageToByteArray(Context context, Uri imageUri, Bitmap.CompressFormat compressFormat, int quality) {
        if (imageUri == null) {
            return null; // No image to convert
        }

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
            Toast.makeText(context, "Failed to convert image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            closeStream(imageStream);
            closeStream(byteArrayOutputStream);
        }
        return null;
    }

    /**
     * Closes the given InputStream or ByteArrayOutputStream.
     * Suppresses IOException that may be thrown during the stream closure.
     *
     * @param stream The stream to close.
     */
    private static void closeStream(AutoCloseable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (Exception e) {
               e.printStackTrace();
            }
        }
    }

    /**
     *
     * @param context
     * @param image
     * @param title
     * @return
     */

    public static Uri getImageUri(Context context, Bitmap image, String title) {
        String imageFileName = title + "_" + System.currentTimeMillis() + ".jpg"; // Unique filename
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), image, imageFileName, null);
        if (path == null) {
            Log.e("getImageUri", "Failed to insert image into Media Store");
            return null;
        }
        return Uri.parse(path);
    }

}
