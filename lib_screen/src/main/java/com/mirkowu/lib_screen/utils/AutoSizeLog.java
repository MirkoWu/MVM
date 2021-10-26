/*
 * Copyright 2018 JessYan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mirkowu.lib_screen.utils;

import com.mirkowu.lib_util.LogUtil;

/**
 * ================================================
 * Created by JessYan on 2018/8/8 18:48
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */
public class AutoSizeLog {
    private static final String TAG = "AndroidAutoSize";
    private static boolean sDebug;

    private AutoSizeLog() {
        throw new IllegalStateException("you can't instantiate me!");
    }

    public static boolean isDebug() {
        return sDebug;
    }

    public static void setDebug(boolean debug) {
        AutoSizeLog.sDebug = debug;
    }

    public static void d(String message) {
        if (sDebug) {
            LogUtil.d(TAG, message);
        }
    }

    public static void w(String message) {
        if (sDebug) {
            LogUtil.w(TAG, message);
        }
    }

    public static void e(String message) {
        if (sDebug) {
            LogUtil.e(TAG, message);
        }
    }
}
