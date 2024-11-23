package com.example.theremindful2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageProcessingUtils {
    private static final int MAX_IMAGE_DIMENSION = 1920; // Maximum width or height
    private static final int COMPRESSION_QUALITY = 80;   // JPEG compression quality (0-100)

    public static byte[] processImage(Context context, Uri imageUri) throws IOException {
        // Get input stream from Uri
        InputStream inputStream = context.getContentResolver().openInputStream(imageUri);

        // Get image dimensions without loading full image
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStream, null, options);
        inputStream.close();

        // Calculate scaling factor
        int scaleFactor = calculateScaleFactor(options.outWidth, options.outHeight);

        // Decode with scaling
        inputStream = context.getContentResolver().openInputStream(imageUri);
        options = new BitmapFactory.Options();
        options.inSampleSize = scaleFactor;
        options.inJustDecodeBounds = false;

        Bitmap originalBitmap = BitmapFactory.decodeStream(inputStream, null, options);
        inputStream.close();

        if (originalBitmap == null) {
            throw new IOException("Failed to decode image");
        }

        // Scale if still too large
        Bitmap scaledBitmap = scaleIfNeeded(originalBitmap);

        // Convert to bytes with compression
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY, outputStream);

        // Clean up
        if (scaledBitmap != originalBitmap) {
            originalBitmap.recycle();
        }
        scaledBitmap.recycle();

        return outputStream.toByteArray();
    }

    private static int calculateScaleFactor(int width, int height) {
        int scaleFactor = 1;
        while ((width / scaleFactor) > MAX_IMAGE_DIMENSION ||
                (height / scaleFactor) > MAX_IMAGE_DIMENSION) {
            scaleFactor *= 2;
        }
        return scaleFactor;
    }

    private static Bitmap scaleIfNeeded(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if (width <= MAX_IMAGE_DIMENSION && height <= MAX_IMAGE_DIMENSION) {
            return bitmap;
        }

        float scale = Math.min(
                (float) MAX_IMAGE_DIMENSION / width,
                (float) MAX_IMAGE_DIMENSION / height
        );

        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        return Bitmap.createBitmap(
                bitmap, 0, 0, width, height, matrix, true
        );
    }
}
