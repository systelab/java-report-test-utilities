package com.werfen.report.test.utils.excel;


import com.werfen.report.test.utils.model.ComparisonResult;

import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class ExcelComparator {

    public static ComparisonResult compareFiles(File expectedFile, File actualFile) throws IOException {
                return compareFiles(expectedFile, actualFile, new ExcelComparisonSettings());
    }
    public static ComparisonResult compareFiles(File expectedFile, File actualFile, ExcelComparisonSettings excelComparisonSettings) throws IOException {
        if (nonNull(expectedFile) && nonNull(actualFile)) {
            try (FileInputStream expectedFileInputStream = new FileInputStream(expectedFile);
                 FileInputStream actualFileInputStream = new FileInputStream(actualFile)) {
                return compareStreams(expectedFileInputStream, actualFileInputStream, excelComparisonSettings);
            }
        } else if (isNull(expectedFile) && isNull(actualFile)) {
            return ComparisonResult.EQUAL;
        } else {
            return ComparisonResult.DIFFERENT.setDifferences("Different value for isNull(file). Expected=" + isNull(expectedFile) + " Actual=" + isNull(actualFile));
        }
    }

    public static ComparisonResult compareStreams(InputStream expectedInputStream, InputStream actualInputStream) throws IOException {
        return compareStreams(expectedInputStream, actualInputStream, new ExcelComparisonSettings());
    }

    public static ComparisonResult compareStreams(InputStream expectedInputStream, InputStream actualInputStream, ExcelComparisonSettings excelComparisonSettings) throws IOException {
        if (nonNull(expectedInputStream) && nonNull(actualInputStream)) {
            try (Workbook expectedWorkbook = WorkbookFactory.create(expectedInputStream);
                 Workbook actualWorkbook = WorkbookFactory.create(actualInputStream)) {
                return compareWorkbooks(expectedWorkbook, actualWorkbook, excelComparisonSettings);
            }
        } else if (isNull(expectedInputStream) && isNull(actualInputStream)) {
            return ComparisonResult.EQUAL;
        } else {
            return ComparisonResult.DIFFERENT.setDifferences("Different value for isNull(file). Expected=" + isNull(expectedInputStream) + " Actual=" + isNull(actualInputStream));
        }
    }


    public static ComparisonResult compareWorkbooks(Workbook expectedWorkbook, Workbook actualWorkbook, ExcelComparisonSettings excelComparisonSettings) {
        if (nonNull(expectedWorkbook) && nonNull(actualWorkbook)) {
            int expectedSheetCount = expectedWorkbook.getNumberOfSheets();
            int actualSheetCount = actualWorkbook.getNumberOfSheets();
            if (expectedSheetCount != actualSheetCount)
                return ComparisonResult.DIFFERENT.setDifferences("Excel work books have different number of sheets. Expected=" + expectedSheetCount + " Actual=" + actualSheetCount);

            for (int sheetIndex = 0; sheetIndex < expectedSheetCount; sheetIndex++) {
                ComparisonResult comparisonResult = compareSheets(expectedWorkbook.getSheetAt(sheetIndex), actualWorkbook.getSheetAt(sheetIndex), excelComparisonSettings);
                if (!comparisonResult.areEqual())
                    return ComparisonResult.DIFFERENT.setDifferences("Different data on sheet " + sheetIndex + ": " + comparisonResult.getDifferences());
            }
            return ComparisonResult.EQUAL;
        } else if (isNull(expectedWorkbook) && isNull(actualWorkbook)) {
            return ComparisonResult.EQUAL;
        } else {
            return ComparisonResult.DIFFERENT.setDifferences("Different value for isNull(workbook). Expected=" + isNull(expectedWorkbook) + " Actual=" + isNull(actualWorkbook));
        }
    }


    public static ComparisonResult compareSheets(Sheet expectedSheet, Sheet actualSheet, ExcelComparisonSettings excelComparisonSettings) {
        if (nonNull(expectedSheet) && nonNull(actualSheet)) {
            String expectedSheetName = expectedSheet.getSheetName();
            String actualSheetName = actualSheet.getSheetName();
            if (!expectedSheetName.equals(actualSheetName))
                return ComparisonResult.DIFFERENT.setDifferences("Different names. Expected=" + expectedSheetName + " Actual= " + actualSheetName);

            int expectedRowCount = expectedSheet.getPhysicalNumberOfRows();
            int actualRowCount = actualSheet.getPhysicalNumberOfRows();
            if (expectedRowCount != actualRowCount)
                return ComparisonResult.DIFFERENT.setDifferences("Different number of rows. Expected=" + expectedRowCount + " Actual=" + actualRowCount);

            for (int rowIndex = 0; rowIndex < expectedRowCount; rowIndex++) {
                ComparisonResult comparisonResult = compareRows(expectedSheet.getRow(rowIndex), actualSheet.getRow(rowIndex), excelComparisonSettings);
                if (!comparisonResult.areEqual())
                    return ComparisonResult.DIFFERENT.setDifferences("Different data on row " + rowIndex + ": " + comparisonResult.getDifferences());
            }
            return ComparisonResult.EQUAL;
        } else if (isNull(expectedSheet) && isNull(actualSheet)) {
            return ComparisonResult.EQUAL;
        } else {
            return ComparisonResult.DIFFERENT.setDifferences("Different value for isNull(sheet). Expected=" + isNull(expectedSheet) + " Actual=" + isNull(actualSheet));
        }
    }

    public static ComparisonResult compareRows(Row expectedRow, Row actualRow, ExcelComparisonSettings excelComparisonSettings) {
        if (nonNull(expectedRow) && nonNull(actualRow)) {
            int expectedCellCount = expectedRow.getPhysicalNumberOfCells();
            int actualCellCount = actualRow.getPhysicalNumberOfCells();
            if (expectedCellCount != actualCellCount)
                return ComparisonResult.DIFFERENT.setDifferences("Different number of cells. Expected=" + expectedCellCount + " Actual=" + actualCellCount);

            for (int cellIndex = 0; cellIndex < expectedCellCount; cellIndex++) {
                ComparisonResult comparisonResult = compareCells(expectedRow.getCell(cellIndex), actualRow.getCell(cellIndex), excelComparisonSettings);
                if (!comparisonResult.areEqual())
                    return ComparisonResult.DIFFERENT.setDifferences("Different data on cell " + cellIndex + ": " + comparisonResult.getDifferences());
            }
            return ComparisonResult.EQUAL;
        } else if (isNull(expectedRow) && isNull(actualRow)) {
            return ComparisonResult.EQUAL;
        } else {
            return ComparisonResult.DIFFERENT.setDifferences("Different value for isNull(row). Expected=" + isNull(expectedRow) + " Actual=" + isNull(actualRow));
        }
    }

    public static ComparisonResult compareCells(Cell expectedCell, Cell actualCell, ExcelComparisonSettings excelComparisonSettings) {
        if (nonNull(expectedCell) && nonNull(actualCell)) {

            if (isExcluded(actualCell, excelComparisonSettings.getExcelCellExclusions())){
                return ComparisonResult.EQUAL;
            }

            CellType expectedCellType = expectedCell.getCellType();
            CellType actualCellType = actualCell.getCellType();
            if (!expectedCellType.equals(actualCellType))
                return ComparisonResult.DIFFERENT.setDifferences("Different cell types. Expected=" + expectedCellType + " Actual=" + actualCellType);

            if (excelComparisonSettings.isCompareCellStyle()){
                if (!isCellStylesEqual(expectedCell, actualCell)){
                    return ComparisonResult.DIFFERENT.setDifferences("Different cell styles. Expected=" + expectedCell.getCellStyle() + " Actual=" + actualCell.getCellType());
                }
            }

            switch (actualCellType) {
                case STRING:
                    String expectedCellStringValue = expectedCell.getStringCellValue();
                    String actualCellStringValue = actualCell.getStringCellValue();
                    if (!expectedCellStringValue.equals(actualCellStringValue))
                        return ComparisonResult.DIFFERENT.setDifferences("Different cell values. Expected=" + expectedCellStringValue + " Actual=" + actualCellStringValue);
                    else
                        return ComparisonResult.EQUAL;

                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(expectedCell) || DateUtil.isCellDateFormatted(actualCell)) {
                        DataFormatter df = new DataFormatter();
                        String expectedCellDateValue = df.formatCellValue(expectedCell);
                        String actualCellDateValue = df.formatCellValue(actualCell);
                        if (!expectedCellDateValue.equals(actualCellDateValue))
                            return ComparisonResult.DIFFERENT.setDifferences("Different cell values. Expected=" + expectedCellDateValue + " Actual=" + actualCellDateValue);
                        else
                            return ComparisonResult.EQUAL;
                    } else {
                        double expectedCellNumericValue = expectedCell.getNumericCellValue();
                        double actualCellNumericValue = actualCell.getNumericCellValue();
                        if (expectedCellNumericValue != actualCellNumericValue)
                            return ComparisonResult.DIFFERENT.setDifferences("Different cell values. Expected=" + expectedCellNumericValue + " Actual=" + actualCellNumericValue);
                        else
                            return ComparisonResult.EQUAL;
                    }

                case BOOLEAN:
                    boolean expectedCellBooleanValue = expectedCell.getBooleanCellValue();
                    boolean actualCellBooleanValue = actualCell.getBooleanCellValue();
                    if (expectedCellBooleanValue != actualCellBooleanValue)
                        return ComparisonResult.DIFFERENT.setDifferences("Different cell values. Expected=" + expectedCellBooleanValue + " Actual=" + actualCellBooleanValue);
                    else
                        return ComparisonResult.EQUAL;

                case FORMULA:
                    String expectedCellFormulaValue = expectedCell.getCellFormula();
                    String actualCellFormulaValue = actualCell.getCellFormula();
                    if (!expectedCellFormulaValue.equals(actualCellFormulaValue))
                        return ComparisonResult.DIFFERENT.setDifferences("Different cell values. Expected=" + expectedCellFormulaValue + " Actual=" + actualCellFormulaValue);
                    else
                        return ComparisonResult.EQUAL;

                case BLANK:
                    return ComparisonResult.EQUAL;

                default:
                    return ComparisonResult.DIFFERENT.setDifferences("Unexpected cell type. " + actualCellType);
            }

        } else if (isNull(expectedCell) && isNull(actualCell)) {
            return ComparisonResult.EQUAL;
        } else {
            return ComparisonResult.DIFFERENT.setDifferences("Different value for isNull(cell). Expected=" + isNull(expectedCell) + " Actual=" + isNull(actualCell));
        }
    }

    private static boolean isExcluded(Cell cell, List<ExcelCellExclusion> excelCellExclusions) {
        if (cell == null) {
            return false;
        }

        int sheetIndex = cell.getSheet().getWorkbook().getSheetIndex(cell.getSheet());
        int rowIndex = cell.getRowIndex();
        int columnIndex = cell.getColumnIndex();

        return excelCellExclusions
            .stream()
            .anyMatch(exclusion ->
            sheetIndex == exclusion.getSheet() &&
                rowIndex == exclusion.getRow() &&
                columnIndex == exclusion.getCell()
        );
    }

    private static boolean isCellStylesEqual(Cell cell1, Cell cell2)
    {
        if (cell1 == null || cell2 == null)
        {
            return cell1 == cell2;
        }

        CellStyle style1 = cell1.getCellStyle();
        CellStyle style2 = cell2.getCellStyle();

        // Compare fonts
        Font font1 = cell1.getSheet().getWorkbook().getFontAt(style1.getFontIndex());
        Font font2 = cell2.getSheet().getWorkbook().getFontAt(style2.getFontIndex());
        if (!isFontEqual(font1, font2))
        {
            return false;
        }

        // Compare other style attributes (e.g., fill pattern, border)
        return style1.getFillPattern() == style2.getFillPattern() &&
            style1.getFillBackgroundColor() == style2.getFillBackgroundColor() &&
            style1.getFillForegroundColor() == style2.getFillForegroundColor() &&
            style1.getBorderBottom() == style2.getBorderBottom() &&
            style1.getVerticalAlignment() == style2.getVerticalAlignment() &&
            style1.getAlignment() == style2.getAlignment();
    }

    private  static boolean isFontEqual(Font font1, Font font2) {
        if (font1 == null || font2 == null) {
            return font1 == font2;
        }
        return font1.getBold() == font2.getBold() &&
            font1.getColor() == font2.getColor() &&
            font1.getFontHeight() == font2.getFontHeight() &&
            font1.getFontName().equals(font2.getFontName()) &&
            font1.getItalic() == font2.getItalic();
    }

}
