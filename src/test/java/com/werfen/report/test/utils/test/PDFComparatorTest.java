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

public class PDFComparatorTest {

    private final String RESOURCE_PATH = "src/test/resources/pdf/";

    @Test
    public void compareFilesEqual() throws IOException {
        File file = new File(RESOURCE_PATH + "file1.pdf");
        ComparisonResultAssertions.assertEquals(PDFComparator.compareFiles(file, file));
    }

    @Test
    public void compareStreamsEqual() throws IOException {
        InputStream expectedStream = Files.newInputStream(Paths.get(RESOURCE_PATH + "file1.pdf"));
        InputStream actualStream = Files.newInputStream(Paths.get(RESOURCE_PATH + "file1.clone.pdf"));
        ComparisonResultAssertions.assertEquals(PDFComparator.compareStreams(expectedStream, actualStream));
    }

    @Test
    public void compareFilesNotEqual() throws IOException {
        File expectedFile = new File(RESOURCE_PATH + "file1.pdf");
        File actualFile = new File(RESOURCE_PATH + "file2.pdf");
        ComparisonResultAssertions.assertNotEquals(compareFiles(expectedFile, actualFile));
    }

    @Test
    public void compareStreamsNotEqual() throws IOException {
        InputStream expectedStream = Files.newInputStream(Paths.get(RESOURCE_PATH + "file1.pdf"));
        InputStream actualStream = Files.newInputStream(Paths.get(RESOURCE_PATH + "file2.pdf"));
        ComparisonResultAssertions.assertNotEquals(PDFComparator.compareStreams(expectedStream, actualStream));
    }
}
