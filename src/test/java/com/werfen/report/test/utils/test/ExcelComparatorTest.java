package com.werfen.report.test.utils.test;

import com.werfen.report.test.utils.excel.ExcelComparator;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

public class ExcelComparatorTest {

    private final String RESOURCE_PATH = "src/test/resources/excel/";

    @Test
    public void compareFilesEqual() throws IOException, InvalidFormatException {
        File expectedFile = new File(RESOURCE_PATH + "sample_list_selected.xlsx");
        File actualFile = new File(RESOURCE_PATH + "sample_list_selected.copy.xlsx");
        ExcelComparator.assertFileEquals(expectedFile, actualFile);
    }

    @Test
    public void compareFilesNotEqual() throws IOException, InvalidFormatException {
        File expectedFile = new File(RESOURCE_PATH + "sample_list_selected.xlsx");
        File actualFile = new File(RESOURCE_PATH + "sample_list_all.xlsx");
        ExcelComparator.assertFileEquals(expectedFile, actualFile);
    }
}
