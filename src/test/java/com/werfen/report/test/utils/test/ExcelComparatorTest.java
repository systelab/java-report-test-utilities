package com.werfen.report.test.utils.test;

import com.werfen.report.test.utils.assertions.ComparisonResultAssertions;
import com.werfen.report.test.utils.excel.ExcelCellExclusion;
import com.werfen.report.test.utils.excel.ExcelComparisonSettings;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.werfen.report.test.utils.excel.ExcelComparator.compareFiles;

class ExcelComparatorTest {

    private static final String RESOURCE_PATH = "src/test/resources/excel/";

    @Test
    void compareFileAgainstItselfDoesNotFail() throws IOException {
        File file = new File(RESOURCE_PATH + "file1.xlsx");
        ComparisonResultAssertions.assertEquals(compareFiles(file, file));
    }

    @Test
    void compareDifferentFilesFails() throws IOException {
        File expectedFile = new File(RESOURCE_PATH + "file1.xlsx");
        File actualFile = new File(RESOURCE_PATH + "file2.xlsx");
        ComparisonResultAssertions.assertNotEquals(compareFiles(expectedFile, actualFile));
    }

    @Test
    /*
      Tests if two Excel files have matching contents, excluding certain cells in the comparison.
      This specifically checks the contents of Financial_Sample.xlsx and Financial_Sample_2.xlsx.
      These files have matching contents, excepts the date in cell 1 of row 6 (in sheet 1 and sheet 2).
      These cells are excluded during the comparison, so it should return that both files have the same content.
     */
    void compareFilesHaveSameContentIgnoringExcludedCellsDoesNotFail() throws IOException
    {
        File expectedFile = new File(RESOURCE_PATH + "Financial_Sample.xlsx");
        File actualFile = new File(RESOURCE_PATH + "Financial_Sample_2.xlsx");

        // Define cells to exclude from comparison (e.g., dates in sheet 1 & 2, row 6, cell 1)
        List<ExcelCellExclusion> excelCellExclusions = new ArrayList<>(
            Arrays.asList(
                new ExcelCellExclusion(0,5, 0),
                new ExcelCellExclusion(1,5, 0)
            )
        );
        ExcelComparisonSettings excelComparisonSettings = new ExcelComparisonSettings(excelCellExclusions);

        // Assert that both files are equal when excluding the specified cells
        ComparisonResultAssertions.assertEquals(compareFiles(expectedFile, actualFile, excelComparisonSettings));
    }

    @Test
    /*
      Tests if two Excel files with same content but different cell style are considered equals.
      This specifically checks the contents of SampleXLSFile.xls and SampleXLSFile_2.xls.
      These files have matching contents, but cell style is different in some cells (column 2 is in Bold in file 2).
      As the compareCellStyle attribute in ExcelComparisonSettings is false, the test should return that files are equals.
     */
    void compareFilesHaveSameContentAndDifferentCellStyleDoesNotFail() throws IOException
    {
        File expectedFile = new File(RESOURCE_PATH + "SampleXLSFile.xls");
        File actualFile = new File(RESOURCE_PATH + "SampleXLSFile_2.xls");

        boolean compareCellStyle = false;
        ExcelComparisonSettings excelComparisonSettings = new ExcelComparisonSettings(compareCellStyle);

        // Assert that both files are equal when comparing cell style is ignored
        ComparisonResultAssertions.assertEquals(compareFiles(expectedFile, actualFile, excelComparisonSettings));
    }

    @Test
    /*
      Tests if two Excel files with same content but different cell style are considered equals.
      This specifically checks the contents of SampleXLSFile.xls and SampleXLSFile_2.xls.
      These files have matching contents, but cell style is different in some cells (column 2 is in Bold in file 2).
      As the compareCellStyle attribute in ExcelComparisonSettings is true, the test should return that files are not equals.
     */
    void compareFilesHaveSameContentAndDifferentCellStyleFails() throws IOException
    {
        File expectedFile = new File(RESOURCE_PATH + "SampleXLSFile.xls");
        File actualFile = new File(RESOURCE_PATH + "SampleXLSFile_2.xls");

        boolean compareCellStyle = true;
        ExcelComparisonSettings excelComparisonSettings = new ExcelComparisonSettings(compareCellStyle);

        // Assert that both files are not equals as there are some style differences (some cells are in bold in file 2 and not in file 1)
        ComparisonResultAssertions.assertNotEquals(compareFiles(expectedFile, actualFile, excelComparisonSettings));

    }

}
