package com.werfen.report.test.utils.test;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static com.werfen.report.test.utils.assertions.FileComparisionAssertions.assertFilesAreDifferent;
import static com.werfen.report.test.utils.assertions.FileComparisionAssertions.assertFilesAreEqual;
import static com.werfen.report.test.utils.pdf.PDFComparator.compareFiles;

public class PDFComparatorTest {

    private final String RESOURCE_PATH = "src/test/resources/pdf/";

    @Test
    public void compareFilesEqual() throws IOException {
        File file = new File(RESOURCE_PATH + "file1.pdf");
        assertFilesAreEqual(compareFiles(file, file));
    }

    @Test
    public void compareFilesNotEqual() throws IOException {
        File expectedFile = new File(RESOURCE_PATH + "file1.pdf");
        File actualFile = new File(RESOURCE_PATH + "file2.pdf");
        assertFilesAreDifferent(compareFiles(expectedFile, actualFile));
    }
}
