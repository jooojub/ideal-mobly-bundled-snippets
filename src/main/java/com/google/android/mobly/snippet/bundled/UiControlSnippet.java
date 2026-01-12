/*
 * Copyright (C) 2023 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.android.mobly.snippet.bundled;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import com.google.android.mobly.snippet.Snippet;
import com.google.android.mobly.snippet.rpc.Rpc;

/** Snippet class for UI control. */
public class UiControlSnippet implements Snippet {

    private final UiDevice mDevice;

    public UiControlSnippet() {
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    }

    @Rpc(description = "Click UI element by resource-id")
    public void clickById(String resourceId) throws UiObjectNotFoundException {
        UiObject obj = mDevice.findObject(new UiSelector().resourceId(resourceId));
        obj.click();
    }

    @Rpc(description = "Click UI element by text")
    public void clickByText(String text) throws UiObjectNotFoundException {
        UiObject obj = mDevice.findObject(new UiSelector().text(text));
        obj.click();
    }

    @Rpc(description = "Click UI element by Class Name")
    public void clickByClassName(String className) throws UiObjectNotFoundException {
        UiObject obj = mDevice.findObject(new UiSelector().className(className));
        obj.click();
    }

    @Rpc(description = "Click UI element by Class Name and Text")
    public void clickByClassNameAndText(String className, String text) throws UiObjectNotFoundException {
        UiObject obj = mDevice.findObject(new UiSelector().className(className).text(text));
        obj.click();
    }

    @Rpc(description = "Click UI element containing specific text")
    public void clickByTextContain(String text) throws UiObjectNotFoundException {
        UiObject obj = mDevice.findObject(new UiSelector().textContains(text));
        obj.click();
    }

    @Rpc(description = "Click UI element by Class Name and containing specific text")
    public void clickByClassNameAndTextContain(String className, String text) throws UiObjectNotFoundException {
        UiObject obj = mDevice.findObject(new UiSelector().className(className).textContains(text));
        obj.click();
    }

    @Override
    public void shutdown() {}
}