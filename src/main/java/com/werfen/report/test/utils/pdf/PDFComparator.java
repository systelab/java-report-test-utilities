package com.werfen.report.test.utils.pdf;

import com.werfen.report.test.utils.exception.FilesNotEqualException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

import static java.util.Objects.isNull;

public class PDFComparator {
    public static void assertFileEquals(File expectedFile, File actualFile) throws IOException, FilesNotEqualException {
        try (PDDocument expected = PDDocument.load(expectedFile);
             PDDocument actual = PDDocument.load(actualFile)) {
            PDFTextStripper textStripper = new PDFTextStripper();
            String expectedText = textStripper.getText(expected);
            String actualText = textStripper.getText(actual);
            if (isNull(expectedText) || isNull(actualText) || !expectedText.equals(actualText))
                throw new FilesNotEqualException("PDF documents have different text content", expectedText, actualText);
        }
    }
}
