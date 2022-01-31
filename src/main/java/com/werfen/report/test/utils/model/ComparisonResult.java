package com.werfen.report.test.utils.model;

public class ComparisonResult {
    public static final ComparisonResult EQUAL = new ComparisonResult(true);
    public static final ComparisonResult DIFFERENT = new ComparisonResult(false);
    private final boolean elementsAreEqual;
    private String differences;

    private ComparisonResult(boolean equal) {
        this.elementsAreEqual = equal;
    }

    public boolean areEqual() {
        return elementsAreEqual;
    }

    public String getDifferences() {
        return differences;
    }

    public ComparisonResult setDifferences(String differences) {
        this.differences = differences;
        return this;
    }
}
