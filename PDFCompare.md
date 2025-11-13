
# PDFCompare 1.2.6 (JDK 21) – User Guide

PDFCompare is an open-source Java library used to compare two PDF documents visually and detect differences between them.
It is widely used in automated testing, document verification, and regression validation to ensure that generated PDFs remain consistent between versions.

Main benefits:
- Detects visual differences (text, images, layout, etc.).
- Supports automatic comparison in CI/CD environments.
- Generates a diff PDF highlighting all discrepancies.
- Allows ignoring specific areas such as timestamps, headers, or signatures. 

## 1 - Requirements and Configuration

* Maven or Gradle for dependency management
* Latest version PDFCompare library version 1.2.6 - JDK21

### Maven Dependency
```xml
<dependency>
    <groupId>de.redsix</groupId>
    <artifactId>pdfcompare</artifactId>
    <version>1.2.6</version>
</dependency>
```
## 2 - Basic Usage

```java
import de.redsix.pdfcompare.PdfComparator;

public class ComparePDF {
  public static void main(String[] args) throws Exception {
    new PdfComparator("expected.pdf", "actual.pdf")
        .compare()
        .writeTo("diff_result");
  }
}
```
The result file will be generated as diff_result.pdf in the working directory.

If the documents are identical, the diff will be empty or marked as "Equal".


## 3 - Result Interpretation (Color Coding)

In the diff PDF, colors represent different types of changes:

| Color                                          | 	Meaning                                                            |
|-----------------------------------------------|---------------------------------------------------------------------|
| 🟥 Red                                         | 	Content present in the expected PDF but missing in the actual one. |
| 🟩 Green                                       | 	Content added in the actual PDF that wasn’t in the expected version. |
| 🟦 Blue|Indicates ignored areas if configured.|
| ⚪ Transparent/No Color                         | 	No difference detected.                                            |

## 4 - Ignoring Specific Areas

Sometimes certain regions, such as headers or timestamps, should be excluded from the comparison.
PDFCompare allows you to define ignore zones by specifying coordinates.
### Java Configuration Example
```java
import de.redsix.pdfcompare.CompareResult;
import de.redsix.pdfcompare.PdfComparator;
import de.redsix.pdfcompare.env.SimpleEnvironment;
import de.redsix.pdfcompare.pagediff.PageArea;

PDFVisualComparatorUtil comparator = createComparator();
    comparator.excludeRegion(1, 2280, 100, 2880, 200);

File expected = file("ListOfOrders1PageAndDates.pdf");
File different = file("ListOfOrders1PageAndDatesV2.pdf");

assertTrue(comparator.compare(expected, different, target("diff_exclude_region")),
    "PDFs should be equal after excluding the region");

```
Each ignore area is defined by:
* Page number
* x1, y1, x2, y2 coordinates (in pixels)

### Exclude one page completely
```java
    PDFVisualComparatorUtil comparator = createComparator();

    comparator.excludePage(3);

File multiPage = file("ListOfOrdersWithMultiplePagesAndDates.pdf");
File multiPageOnePage = file("ListOfOrdersWithMultiplePagesAndDatesV3_OnePage.pdf");

assertTrue(comparator.compare(multiPage, multiPageOnePage, target("diff_exclude_page")),
    "PDFs should be equal after excluding pages");
```
### Exclude using json file
    PDFVisualComparatorUtil comparator = createComparator();
    File exclusionFile = file("exclusions.json");

    if (exclusionFile.exists()) {
      comparator.setExclusionFile(exclusionFile.getAbsolutePath());

      File multiPage = file("ListOfOrders1PageAndDates.pdf");
      File multiPageDiff = file("ListOfOrders1PageAndDatesV2.pdf");

      assertTrue(comparator.compare(multiPage, multiPageDiff, target("diff_exclusion_file")),
          "PDFs should be equal after applying the exclusion file");
    }
#### json Exclusion File Format
```xml
{
  "exclusions": [
    {
      "page": 1,
      "x1": 2280,
      "y1": 100,
      "x2": 2880,
      "y2": 200
    },
    {
      "page": 2,
      "x1": 2280,
      "y1": 100,
      "x2": 2880,
      "y2": 200
    },
    {
      "page": 3,
      "x1": 2280,
      "y1": 100,
      "x2": 2880,
      "y2": 200
    }
  ]
}
```

### How to Obtain Coordinates 
To exclude specific regions accurately, you need to know their coordinates. PDFCompare uses a pixel-based coordinate system (not millimeters or inches).
#### Recommended Approach
* Open your PDF in Adobe Acrobat.
* Enable the Measure Tool (Tools → Measure).
* Measure the distance (in inches) from the bottom-left corner of the page to your target area.
* Convert the values from inches to pixels using the following approximate formula:
```
1 inch = 72 pixels
```
So:
* X (in pixels) = X (in inches) × 72
* Y (in pixels) = Y (in inches) × 72
You can also use this online converter:
👉 https://es.planetcalc.com/8608/

#### Coordinate System Diagram
The coordinate system used by PDFCompare is the standard PDF coordinate system:
```
(0,0) ----------------> X (Right)
|   +--------------------+
|   |                    |
|   |                    |
|   |                    |
|   |                    |
|   |                    |
|   |                    |
|   |                    |
V   +--------------------+
Y (up)
```
* The origin (0,0) is at the bottom-left corner of the page.
* X increases to the right, Y increases upward.
* Coordinates are measured in pixels (typically 72 per inch).

## Links
* PDFCompare GitHub Repository https://github.com/red6/pdfcompare
* PDF Specification (Adobe): https://opensource.adobe.com/dc-acrobat-sdk-docs/pdfstandards/
* Unit Converter (Inches ↔ Pixels): https://es.planetcalc.com/8608/
