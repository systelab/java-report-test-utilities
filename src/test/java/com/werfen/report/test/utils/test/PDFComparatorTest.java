package com.werfen.report.test.utils.test;

import com.werfen.report.test.utils.pdf.PDFComparator;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

public class PDFComparatorTest {

    private final String RESOURCE_PATH = "src/test/resources/pdf/";

    @Test
    public void compareFilesEqual() throws IOException {
        File expectedFile = new File(RESOURCE_PATH + "sample_list_selected.pdf");
        File actualFile = new File(RESOURCE_PATH + "sample_list_selected.copy.pdf");
        PDFComparator.assertFileEquals(expectedFile, actualFile);
    }

    @Test
    public void compareFilesNotEqual() throws IOException {
        File expectedFile = new File(RESOURCE_PATH + "sample_list_selected.pdf");
        File actualFile = new File(RESOURCE_PATH + "sample_list_all.pdf");
        PDFComparator.assertFileEquals(expectedFile, actualFile);
    }
}
