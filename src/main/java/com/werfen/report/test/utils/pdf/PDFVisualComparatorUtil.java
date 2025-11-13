package com.werfen.report.test.utils.pdf;

import de.redsix.pdfcompare.CompareResult;
import de.redsix.pdfcompare.PdfComparator;
import de.redsix.pdfcompare.PageArea;
import de.redsix.pdfcompare.env.SimpleEnvironment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for visually comparing two PDF files using pdfcompare (v1.2.6).
 */
public class PDFVisualComparatorUtil {

  private final List<PageArea> excludedAreas = new ArrayList<>();
  private String exclusionFilePath;

  /**
   * Excludes a rectangular region on a given page.
   *
   * Coordinates are pixel-based (default 300 DPI in pdfcompare).
   *
   * @param pageNumber page number (starting from 1)
   * @param x1 left coordinate in pixels
   * @param y1 bottom coordinate in pixels
   * @param x2 right coordinate in pixels
   * @param y2 top coordinate in pixels
   */
  public void excludeRegion(int pageNumber, int x1, int y1, int x2, int y2) {
    excludedAreas.add(new PageArea(pageNumber, x1, y1, x2, y2));
  }

  /**
   * Excludes an entire page from comparison.
   *
   * @param pageNumber page number (starting from 1)
   */
  public void excludePage(int pageNumber) {
    excludedAreas.add(new PageArea(pageNumber));
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
    SimpleEnvironment env = new SimpleEnvironment()
        .setActualColor(java.awt.Color.RED)
        .setExpectedColor(java.awt.Color.BLUE)
        .setAddEqualPagesToResult(true);

    PdfComparator comparator = new PdfComparator(expected.getAbsolutePath(), actual.getAbsolutePath())
        .withEnvironment(env);

    // Apply exclusion file if provided
    if (exclusionFilePath != null) {
      comparator = comparator.withIgnore(exclusionFilePath);
    }

    // Apply in-memory exclusions
    for (PageArea area : excludedAreas) {
      comparator = comparator.withIgnore(area);
    }

    CompareResult result = comparator.compare();

    if (outputDiff != null) {
      result.writeTo(outputDiff.getAbsolutePath());
    }

    return result.isEqual();
  }
}
