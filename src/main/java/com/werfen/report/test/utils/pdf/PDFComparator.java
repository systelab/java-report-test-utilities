package com.werfen.report.test.utils.pdf;

import com.werfen.report.test.utils.exception.FilesNotEqualException;
import com.werfen.report.test.utils.model.FileComparision;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

import static java.util.Objects.isNull;

public class PDFComparator {

    public static FileComparision compareFiles(File expectedFile, File actualFile) throws IOException, FilesNotEqualException {
        try (PDDocument expected = PDDocument.load(expectedFile);
             PDDocument actual = PDDocument.load(actualFile)) {
            PDFTextStripper textStripper = new PDFTextStripper();
            String expectedText = textStripper.getText(expected);
            String actualText = textStripper.getText(actual);
            if (isNull(expectedText) || isNull(actualText) || !expectedText.equals(actualText))
                return FileComparision.DIFFERENT.setDifferences("PDF documents have different text content. Expected=" + expectedText + " Actual=" + actualText);
            else return FileComparision.EQUAL;
        }
    }
}
