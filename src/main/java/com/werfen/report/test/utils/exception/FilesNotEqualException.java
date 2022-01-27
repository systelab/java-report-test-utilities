package com.werfen.report.test.utils.exception;


import static java.util.Objects.nonNull;

public class FilesNotEqualException extends RuntimeException {

    private final String message;
    private final String expectedValue;
    private final String actualValue;


    public FilesNotEqualException(String message, String expectedValue, String actualValue) {
        this.message = message;
        this.expectedValue = expectedValue;
        this.actualValue = actualValue;
    }

    public FilesNotEqualException(String message) {
        this.message = message;
        this.expectedValue = null;
        this.actualValue = null;
    }

    @Override
    public String getMessage() {
        if (nonNull(this.expectedValue) && nonNull(this.actualValue)) {
            return message + " expected: " + expectedValue + " actual: " + actualValue;
        } else {
            return message;
        }
    }
}
