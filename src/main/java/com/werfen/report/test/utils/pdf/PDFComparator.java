package com.werfen.report.test.utils.pdf;

import com.werfen.report.test.utils.model.ComparisonResult;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static java.util.Objects.isNull;

public class PDFComparator {

    public static ComparisonResult compareFiles(File expectedFile, File actualFile) throws IOException {
        try (PDDocument expected = PDDocument.load(expectedFile);
             PDDocument actual = PDDocument.load(actualFile)) {
            return compareDocuments(expected, actual);
        }
    }

    public static ComparisonResult compareFiles(InputStream expectedStream, InputStream actualStream) throws IOException {
        try (PDDocument expected = PDDocument.load(expectedStream);
             PDDocument actual = PDDocument.load(actualStream)) {
            return compareDocuments(expected, actual);
        }
    }

    private static ComparisonResult compareDocuments(PDDocument expected, PDDocument actual) throws IOException {
        PDFTextStripper textStripper = new PDFTextStripper();
        String expectedText = textStripper.getText(expected);
        String actualText = textStripper.getText(actual);
        if (isNull(expectedText) || isNull(actualText) || !expectedText.equals(actualText))
            return ComparisonResult.DIFFERENT.setDifferences("PDF documents have different text content. Expected=" + expectedText + " Actual=" + actualText);
        else return ComparisonResult.EQUAL;
    }
}
