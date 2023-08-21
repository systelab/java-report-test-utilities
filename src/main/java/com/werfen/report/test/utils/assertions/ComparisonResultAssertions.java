package com.werfen.report.test.utils.assertions;

import com.werfen.report.test.utils.model.ComparisonResult;
import org.junit.jupiter.api.Assertions;

public class ComparisonResultAssertions {
    public static void assertEquals(ComparisonResult comparison) {
        Assertions.assertTrue(comparison.areEqual(), comparison.getDifferences());
    }

    public static void assertNotEquals(ComparisonResult comparison) {
        Assertions.assertFalse(comparison.areEqual(), comparison.getDifferences());
    }
}
