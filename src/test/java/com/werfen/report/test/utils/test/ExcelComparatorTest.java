package com.werfen.report.test.utils.test;

import com.werfen.report.test.utils.assertions.ComparisonResultAssertions;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static com.werfen.report.test.utils.excel.ExcelComparator.compareFiles;

public class ExcelComparatorTest {

    private final String RESOURCE_PATH = "src/test/resources/excel/";

    @Test
    public void compareFileAgainstItselfDoesNotFail() throws IOException, InvalidFormatException {
        File file = new File(RESOURCE_PATH + "file1.xlsx");
        ComparisonResultAssertions.assertEquals(compareFiles(file, file));
    }

    @Test
    public void compareDifferentFilesFails() throws IOException, InvalidFormatException {
        File expectedFile = new File(RESOURCE_PATH + "file1.xlsx");
        File actualFile = new File(RESOURCE_PATH + "file2.xlsx");
        ComparisonResultAssertions.assertNotEquals(compareFiles(expectedFile, actualFile));
    }
}
