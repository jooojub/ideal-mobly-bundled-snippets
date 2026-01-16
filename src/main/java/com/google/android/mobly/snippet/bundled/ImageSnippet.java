package com.google.android.mobly.snippet.bundled;

import android.app.UiAutomation;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import androidx.test.platform.app.InstrumentationRegistry;
import com.google.android.mobly.snippet.Snippet;
import com.google.android.mobly.snippet.rpc.Rpc;
import java.io.IOException;

/** Snippet class for image matching operations. */
public class ImageSnippet implements Snippet {

    private final UiAutomation mUiAutomation;

    public ImageSnippet() {
        mUiAutomation = InstrumentationRegistry.getInstrumentation().getUiAutomation();
    }

    @Rpc(description = "Waits for a specific image to appear on the screen at the given coordinates.")
    public void waitForImageMatching(
            String expectedImageBase64, int x, int y, int width, int height, int timeoutMs)
            throws Exception {
        byte[] decodedBytes = Base64.decode(expectedImageBase64, Base64.DEFAULT);
        Bitmap originalExpected = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        Bitmap expectedBitmap;
        if (originalExpected.getWidth() != width || originalExpected.getHeight() != height) {
            expectedBitmap = Bitmap.createScaledBitmap(originalExpected, width, height, true);
            if (expectedBitmap != originalExpected) {
                originalExpected.recycle();
            }
        } else {
            expectedBitmap = originalExpected;
        }

        long endTime = System.currentTimeMillis() + timeoutMs;
        double threshold = 0.85;

        while (System.currentTimeMillis() < endTime) {
            Bitmap screenshot = mUiAutomation.takeScreenshot();
            if (screenshot != null) {
                // Ensure crop coordinates are within the screenshot bounds
                int safeX = Math.max(0, x);
                int safeY = Math.max(0, y);
                int safeWidth = Math.min(width, screenshot.getWidth() - safeX);
                int safeHeight = Math.min(height, screenshot.getHeight() - safeY);

                if (safeWidth > 0 && safeHeight > 0) {
                    Bitmap cropped = Bitmap.createBitmap(screenshot, safeX, safeY, safeWidth, safeHeight);
                    
                    // Compare bitmaps (similarity match)
                    if (calculateSimilarity(expectedBitmap, cropped) >= threshold) {
                        cropped.recycle();
                        expectedBitmap.recycle();
                        return;
                    }
                    cropped.recycle();
                }
                screenshot.recycle();
            }
            Thread.sleep(500); // Check every 500ms
        }
        
        expectedBitmap.recycle();
        throw new Exception("Timed out waiting for image match.");
    }

    private double calculateSimilarity(Bitmap b1, Bitmap b2) {
        if (b1.getWidth() != b2.getWidth() || b1.getHeight() != b2.getHeight()) {
            return 0.0;
        }
        int width = b1.getWidth();
        int height = b1.getHeight();
        int pixelCount = width * height;
        int[] p1 = new int[pixelCount];
        int[] p2 = new int[pixelCount];

        b1.getPixels(p1, 0, width, 0, 0, width, height);
        b2.getPixels(p2, 0, width, 0, 0, width, height);

        long diffSum = 0;
        for (int i = 0; i < pixelCount; i++) {
            int c1 = p1[i];
            int c2 = p2[i];
            int rDiff = Math.abs(((c1 >> 16) & 0xFF) - ((c2 >> 16) & 0xFF));
            int gDiff = Math.abs(((c1 >> 8) & 0xFF) - ((c2 >> 8) & 0xFF));
            int bDiff = Math.abs((c1 & 0xFF) - (c2 & 0xFF));
            diffSum += rDiff + gDiff + bDiff;
        }
        double maxDiff = pixelCount * 255.0 * 3.0;
        return 1.0 - (diffSum / maxDiff);
    }

    @Override
    public void shutdown() {
        // Clean up resources if needed
    }
}