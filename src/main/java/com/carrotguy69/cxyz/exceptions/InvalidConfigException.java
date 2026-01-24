package com.carrotguy69.cxyz.exceptions;

public class InvalidConfigException extends RuntimeException {
    public InvalidConfigException(String fileName, String path, String message) {
        super("In " + fileName + ", at " + path + ". " + message);
    }
}
