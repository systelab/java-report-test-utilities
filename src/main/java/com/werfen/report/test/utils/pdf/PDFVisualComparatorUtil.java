package com.werfen.report.test.utils.pdf;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import de.redsix.pdfcompare.CompareResult;
import de.redsix.pdfcompare.PageArea;
import de.redsix.pdfcompare.PdfComparator;

/**
 * Utility class for visually comparing two PDF files using pdfcompare (v1.2.6).
 */
public class PDFVisualComparatorUtil {

  private final List<PageArea> excludedAreas = new ArrayList<>();
  private String exclusionFilePath;
  private InputStream exclusionInputStream;

  /**
   * Excludes a rectangular region on a given page.
   * Coordinates are pixel-based (default 300 DPI in pdfcompare).
   *
   * @param pageNumber page number (starting from 1)
   * @param x1 left coordinate in pixels
   * @param y1 bottom coordinate in pixels
   * @param x2 right coordinate in pixels
   * @param y2 top coordinate in pixels
   */
  public void excludeRegion(int pageNumber, int x1, int y1, int x2, int y2) {
    this.excludedAreas.add(new PageArea(pageNumber, x1, y1, x2, y2));
  }

  public void excludeInputStream(InputStream exclusionInputStream) {
    this.exclusionInputStream = exclusionInputStream;
  }

  /**
   * Excludes an entire page from comparison.
   * I have tested this feature to this version, and it is not working as expected. This is the reason to keep it private.
   * @param pageNumber page number (starting from 1)
   */
  private void excludePage(int pageNumber) {
    this.excludedAreas.add(new PageArea(pageNumber));
  }

  /**
   * Specifies an external exclusion file (HOCON/JSON) supported by pdfcompare.
   *
   * @param exclusionFilePath path to exclusion file
   */
  public void setExclusionFile(String exclusionFilePath) {
    this.exclusionFilePath = exclusionFilePath;
  }

  /**
   * Compares two PDF files and optionally writes a visual diff output.
   *
   * @param expected the reference PDF
   * @param actual the actual/generated PDF
   * @param outputDiff optional output file for the visual diff (can be null)
   * @return true if the PDFs are visually equal (ignoring exclusions)
   * @throws IOException if an error occurs during comparison
   */
  public boolean compare(File expected, File actual, File outputDiff) throws IOException {

    PdfComparator comparator = new PdfComparator(expected.getAbsolutePath(), actual.getAbsolutePath());

    // Apply exclusion file if provided
    if (this.exclusionFilePath != null) {
      comparator.withIgnore(this.exclusionFilePath);
    }

    if (this.exclusionInputStream != null) {
      comparator.withIgnore(this.exclusionInputStream);
    }

    // Apply in-memory exclusions
    for (PageArea area : this.excludedAreas) {
      comparator.withIgnore(area);
    }

    CompareResult result = comparator.compare();

    if (outputDiff != null) {
      result.writeTo(outputDiff.getAbsolutePath());
      System.out.println(comparator.getResult().getDifferencesJson());
    }

    return result.isEqual();
  }
}
