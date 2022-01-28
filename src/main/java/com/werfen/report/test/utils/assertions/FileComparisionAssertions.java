package com.werfen.report.test.utils.assertions;

import com.werfen.report.test.utils.model.FileComparision;
import org.junit.jupiter.api.Assertions;

public class FileComparisionAssertions {
    public static void assertFilesAreEqual(FileComparision comparision) {
        Assertions.assertEquals(true,comparision.areEqual(),comparision.getDifferences());
    }

    public static void assertFilesAreDifferent(FileComparision comparision) {
        Assertions.assertEquals(false,comparision.areEqual(),comparision.getDifferences());
    }
}
