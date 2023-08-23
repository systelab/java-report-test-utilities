package com.werfen.report.test.utils.test;

import com.werfen.report.test.utils.assertions.ComparisonResultAssertions;
import com.werfen.report.test.utils.pdf.PDFComparator;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.werfen.report.test.utils.pdf.PDFComparator.compareFiles;

class PDFComparatorTest {

    private static final String RESOURCE_PATH = "src/test/resources/pdf/";

    @Test
    void compareFilesEqual() throws IOException {
        File file = new File(RESOURCE_PATH + "file1.pdf");
        ComparisonResultAssertions.assertEquals(PDFComparator.compareFiles(file, file));
    }

    @Test
    void compareStreamsEqual() throws IOException {
        InputStream expectedStream = Files.newInputStream(Paths.get(RESOURCE_PATH + "file1.pdf"));
        InputStream actualStream = Files.newInputStream(Paths.get(RESOURCE_PATH + "file1.clone.pdf"));
        ComparisonResultAssertions.assertEquals(PDFComparator.compareStreams(expectedStream, actualStream));
    }

    @Test
    void compareFilesNotEqual() throws IOException {
        File expectedFile = new File(RESOURCE_PATH + "file1.pdf");
        File actualFile = new File(RESOURCE_PATH + "file2.pdf");
        ComparisonResultAssertions.assertNotEquals(compareFiles(expectedFile, actualFile));
    }

    @Test
    void compareStreamsNotEqual() throws IOException {
        InputStream expectedStream = Files.newInputStream(Paths.get(RESOURCE_PATH + "file1.pdf"));
        InputStream actualStream = Files.newInputStream(Paths.get(RESOURCE_PATH + "file2.pdf"));
        ComparisonResultAssertions.assertNotEquals(PDFComparator.compareStreams(expectedStream, actualStream));
    }
}
