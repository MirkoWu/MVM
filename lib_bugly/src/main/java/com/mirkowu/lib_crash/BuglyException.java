package com.mirkowu.lib_crash;

import androidx.annotation.Nullable;

public class BuglyException extends Exception {
    public BuglyException(@Nullable String message) {
        super(message);
    }

    public BuglyException(@Nullable Throwable cause) {
        super(cause);
    }

    public BuglyException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }
}
