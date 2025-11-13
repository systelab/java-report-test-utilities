package com.werfen.report.test.utils.pdf;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class PDFVisualComparatorUtilTest {

  private static final String BASE_PATH = "src/test/resources/pdf/";
  private static final String TARGET_PATH = "target/";

  private PDFVisualComparatorUtil createComparator() {
    return new PDFVisualComparatorUtil();
  }

  private File file(String name) {
    return new File(BASE_PATH + name);
  }

  private File target(String name) {
    return new File(TARGET_PATH + name);
  }

  @Test
  void shouldReturnTrueForIdenticalPdfs() throws IOException {
    PDFVisualComparatorUtil comparator = createComparator();
    File expected = file("ListOfOrders1PageAndDates.pdf");
    File identical = file("ListOfOrders1PageAndDatesIdentical.pdf");

    assertTrue(comparator.compare(expected, identical, target("diff_identical")),
        "Identical PDFs should be equal");
  }

  @Test
  void shouldReturnFalseForDifferentPdfs() throws IOException {
    PDFVisualComparatorUtil comparator = createComparator();
    File expected = file("ListOfOrders1PageAndDates.pdf");
    File different = file("ListOfOrders1PageAndDatesV2.pdf");

    assertFalse(comparator.compare(expected, different, target("diff_different")),
        "Different PDFs should not be equal");
  }

  @Test
  void shouldExcludeRegionAndMatch() throws IOException {
    PDFVisualComparatorUtil comparator = createComparator();
    comparator.excludeRegion(1, 2280, 100, 2880, 200);

    File expected = file("ListOfOrders1PageAndDates.pdf");
    File different = file("ListOfOrders1PageAndDatesV2.pdf");

    assertTrue(comparator.compare(expected, different, target("diff_exclude_region")),
        "PDFs should be equal after excluding the region");
  }

  @Test
  void shouldExcludePagesAndMatch() throws IOException {
    PDFVisualComparatorUtil comparator = createComparator();

    comparator.excludePage(3);

    File multiPage = file("ListOfOrdersWithMultiplePagesAndDates.pdf");
    File multiPageOnePage = file("ListOfOrdersWithMultiplePagesAndDatesV3_OnePage.pdf");

    assertTrue(comparator.compare(multiPage, multiPageOnePage, target("diff_exclude_page")),
        "PDFs should be equal after excluding pages");
  }

  @Test
  void shouldApplyExclusionFileIfExists() throws IOException {
    PDFVisualComparatorUtil comparator = createComparator();
    File exclusionFile = file("exclusions.json");

    if (exclusionFile.exists()) {
      comparator.setExclusionFile(exclusionFile.getAbsolutePath());

      File multiPage = file("ListOfOrders1PageAndDates.pdf");
      File multiPageDiff = file("ListOfOrders1PageAndDatesV2.pdf");

      assertTrue(comparator.compare(multiPage, multiPageDiff, target("diff_exclusion_file")),
          "PDFs should be equal after applying the exclusion file");
    } else {
      System.out.printf("Skipping exclusion file test (file not found): %s%n", exclusionFile.getAbsolutePath());
    }
  }
}