package com.werfen.report.test.utils.test;

import com.werfen.report.test.utils.excel.ExcelComparator;
import org.junit.jupiter.api.Test;

import java.io.File;

public class ExcelComparatorTest {

    private final String RESOURCE_PATH = "src/test/resources/excel/";

    @Test
    public void compareFileAgainstItselfDoesNotFail() {
        File file = new File(RESOURCE_PATH + "file1.xlsx");
        ExcelComparator.assertFileEquals(file, file);
    }

    @Test
    public void compareDifferentFilesFails() {
        File expectedFile = new File(RESOURCE_PATH + "file1.xlsx");
        File actualFile = new File(RESOURCE_PATH + "file2.xlsx");
        ExcelComparator.assertFileNotEquals(expectedFile, actualFile);
    }
}
