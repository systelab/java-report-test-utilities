package com.werfen.report.test.utils.test;

import com.werfen.report.test.utils.assertions.ComparisonResultAssertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static com.werfen.report.test.utils.pdf.PDFComparator.compareFiles;

public class PDFComparatorTest {

    private final String RESOURCE_PATH = "src/test/resources/pdf/";

    @Test
    public void compareFilesEqual() throws IOException {
        File file = new File(RESOURCE_PATH + "file1.pdf");
        ComparisonResultAssertions.assertEquals(compareFiles(file, file));
    }


    @Test
    public void compareFilesNotEqual() throws IOException {
        File expectedFile = new File(RESOURCE_PATH + "file1.pdf");
        File actualFile = new File(RESOURCE_PATH + "file2.pdf");
        ComparisonResultAssertions.assertNotEquals(compareFiles(expectedFile, actualFile));
    }
}
