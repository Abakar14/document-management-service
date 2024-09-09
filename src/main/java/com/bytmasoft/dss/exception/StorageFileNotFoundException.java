package com.bytmasoft.dss.exception;

public class StorageFileNotFoundException extends StorageException {
    public StorageFileNotFoundException(String s) {
        super(s);
    }

    public StorageFileNotFoundException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
