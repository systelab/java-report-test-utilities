package com.werfen.report.test.utils.assertions;

import com.werfen.report.test.utils.model.ComparisonResult;
import org.junit.jupiter.api.Assertions;

public class ComparisonResultAssertions {
    public static void assertEquals(ComparisonResult comparision) {
        Assertions.assertEquals(true,comparision.areEqual(),comparision.getDifferences());
    }

    public static void assertNotEquals(ComparisonResult comparision) {
        Assertions.assertEquals(false,comparision.areEqual(),comparision.getDifferences());
    }
}
