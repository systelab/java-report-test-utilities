package com.werfen.report.test.utils.test;

import com.werfen.report.test.utils.pdf.PDFComparator;
import org.junit.jupiter.api.Test;

import java.io.File;

public class PDFComparatorTest {

    private final String RESOURCE_PATH = "src/test/resources/pdf/";

    @Test
    public void compareFilesEqual() {
        File file = new File(RESOURCE_PATH + "file1.pdf");
        PDFComparator.assertFileEquals(file, file);
    }

    @Test
    public void compareFilesNotEqual() {
        File expectedFile = new File(RESOURCE_PATH + "file1.pdf");
        File actualFile = new File(RESOURCE_PATH + "file2.pdf");
        PDFComparator.assertFileNotEquals(expectedFile, actualFile);
    }
}
