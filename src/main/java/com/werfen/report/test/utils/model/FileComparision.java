package com.werfen.report.test.utils.model;

public class FileComparision {
    public static final FileComparision EQUAL = new FileComparision(true);
    public static final FileComparision DIFFERENT = new FileComparision(false);
    private final boolean filesAreEqual;
    private String differences;

    private FileComparision(boolean equal) {
        this.filesAreEqual = equal;
    }

    public boolean areEqual() {
        return filesAreEqual;
    }

    public String getDifferences() {
        return differences;
    }

    public FileComparision setDifferences(String differences) {
        this.differences = differences;
        return this;
    }
}
