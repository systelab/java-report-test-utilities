package com.werfen.report.test.utils.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PDFComparator {
    public static void assertFileEquals(File expectedFile, File actualFile) throws IOException {
        try (PDDocument expected = PDDocument.load(expectedFile);
             PDDocument generated = PDDocument.load(actualFile)) {
            PDFTextStripper textStripper = new PDFTextStripper();
            assertEquals(textStripper.getText(expected), textStripper.getText(generated));
        }
    }
}
