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
        Bitmap expectedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

        long endTime = System.currentTimeMillis() + timeoutMs;

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
                    
                    // Compare bitmaps (exact match)
                    if (expectedBitmap.sameAs(cropped)) {
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

    @Override
    public void shutdown() {
        // Clean up resources if needed
    }
}
