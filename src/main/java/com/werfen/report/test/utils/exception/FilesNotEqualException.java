package com.werfen.report.test.utils.exception;


import java.util.logging.Logger;

public class FilesNotEqualException extends RuntimeException {

    private final String message;
    private final String expectedValue;
    private final String actualValue;
    Logger log = Logger.getLogger(FilesNotEqualException.class.getName());

    public FilesNotEqualException(String message, String expectedValue, String actualValue) {
        this.message = message;
        this.expectedValue = expectedValue;
        this.actualValue = actualValue;

        log.info(message + " expected: " + expectedValue + " actual: " + actualValue);
    }

    public FilesNotEqualException(String message) {
        this.message = message;
        this.expectedValue = null;
        this.actualValue = null;

        log.info(message);
    }
}
