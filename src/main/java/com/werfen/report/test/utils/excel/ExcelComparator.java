package com.werfen.report.test.utils.excel;


import com.werfen.report.test.utils.exception.FilesNotEqualException;
import com.werfen.report.test.utils.model.ComparisonResult;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class ExcelComparator {

    public static ComparisonResult compareFiles(File expectedFile, File actualFile) throws IOException, InvalidFormatException {
        try (FileInputStream expectedFileInputStream = new FileInputStream(expectedFile);
             Workbook expected = WorkbookFactory.create(expectedFileInputStream);
             Workbook generated = new XSSFWorkbook(actualFile)) {
            return compareWorkbooks(expected, generated);
        }
    }

    public static ComparisonResult compareWorkbooks(Workbook expected, Workbook actual) throws FilesNotEqualException {
        int expectedSheetCount = expected.getNumberOfSheets();
        int actualSheetCount = actual.getNumberOfSheets();
        if (expectedSheetCount != actualSheetCount)
            return ComparisonResult.DIFFERENT.setDifferences("Excel work books have different number of sheets. Expected=" + expectedSheetCount + " Actual=" + actualSheetCount);

        for (int sheetIndex = 0; sheetIndex < expectedSheetCount; sheetIndex++) {
            ComparisonResult comparisonResult = compareSheets(expected.getSheetAt(sheetIndex), actual.getSheetAt(sheetIndex));
            if (!comparisonResult.areEqual())
                return comparisonResult;
        }
        return ComparisonResult.EQUAL;
    }

    public static ComparisonResult compareSheets(Sheet expectedSheet, Sheet actualSheet) {
        String expectedSheetName = expectedSheet.getSheetName();
        String actualSheetName = actualSheet.getSheetName();
        if (isNull(expectedSheetName) || isNull(actualSheetName) || !expectedSheetName.equals(actualSheetName))
            return ComparisonResult.DIFFERENT.setDifferences("Sheets have different names. Expected=" + expectedSheetName + " Actual= " + actualSheetName);

        int expectedRowCount = expectedSheet.getPhysicalNumberOfRows();
        int actualRowCount = actualSheet.getPhysicalNumberOfRows();
        if (expectedRowCount != actualRowCount)
            return ComparisonResult.DIFFERENT.setDifferences("Sheet " + actualSheet.getSheetName() + " have different number of rows. Expected=" + expectedRowCount + " Actual=" + actualRowCount);

        for (int rowIndex = 0; rowIndex < expectedRowCount; rowIndex++) {
            ComparisonResult comparisonResult = compareRows(expectedSheet.getRow(rowIndex), actualSheet.getRow(rowIndex));
            if (!comparisonResult.areEqual())
                return comparisonResult;
        }
        return ComparisonResult.EQUAL;
    }

    public static ComparisonResult compareRows(Row expected, Row actual) {
        if (nonNull(expected) && nonNull(actual)) {
            int expectedCellCount = expected.getPhysicalNumberOfCells();
            int actualCellCount = actual.getPhysicalNumberOfCells();
            if (expectedCellCount != actualCellCount)
                return ComparisonResult.DIFFERENT.setDifferences("Sheet " + actual.getSheet().getSheetName() + " have different number of cells in row " + actual.getRowNum() + ". Expected=" + expectedCellCount + " Actual=" + actualCellCount);

            for (int cellIndex = 0; cellIndex < expectedCellCount; cellIndex++) {
                ComparisonResult comparisonResult = compareCells(expected.getCell(cellIndex), actual.getCell(cellIndex));
                if (!comparisonResult.areEqual())
                    return comparisonResult;
            }
            return ComparisonResult.EQUAL;
        } else {
            return ComparisonResult.DIFFERENT.setDifferences("Rows are null. Expected=" + expected + " Actual=" + actual);
        }
    }

    public static ComparisonResult compareCells(Cell expected, Cell actual) {
        if (nonNull(expected) && nonNull(actual)) {
            if (expected.getCellType().equals(actual.getCellType())) {
                switch (expected.getCellType()) {
                    case STRING:
                        String expectedCellStringValue = expected.getStringCellValue();
                        String actualCellStringValue = actual.getStringCellValue();
                        if (isNull(expectedCellStringValue) || isNull(actualCellStringValue) || !expectedCellStringValue.equals(actualCellStringValue))
                            return ComparisonResult.DIFFERENT.setDifferences("Sheet " + actual.getSheet().getSheetName() + " has different cell " + actual.getColumnIndex() + " values in row " + actual.getRow().getRowNum() + ". Expected=" + expectedCellStringValue + " Actual=" + actualCellStringValue);
                        break;

                    case NUMERIC:
                        if (DateUtil.isCellDateFormatted(expected) | DateUtil.isCellDateFormatted(actual)) {
                            DataFormatter df = new DataFormatter();
                            String expectedCellDateValue = df.formatCellValue(expected);
                            String actualCellDateValue = df.formatCellValue(actual);
                            if (isNull(expectedCellDateValue) || isNull(actualCellDateValue) || !expectedCellDateValue.equals(actualCellDateValue))
                                return ComparisonResult.DIFFERENT.setDifferences("Sheet " + actual.getSheet().getSheetName() + " has different cell " + actual.getColumnIndex() + " values in row " + actual.getRow().getRowNum() + ". Expected=" + expectedCellDateValue + " Actual=" + actualCellDateValue);
                        } else {
                            double expectedCellNumericValue = expected.getNumericCellValue();
                            double actualCellNumericValue = actual.getNumericCellValue();
                            if (expectedCellNumericValue != actualCellNumericValue)
                                return ComparisonResult.DIFFERENT.setDifferences("Sheet " + actual.getSheet().getSheetName() + " has different cell " + actual.getColumnIndex() + " values in row " + actual.getRow().getRowNum() + ". Expected=" + expectedCellNumericValue + " Actual=" + actualCellNumericValue);
                        }
                        break;

                    case BOOLEAN:
                        boolean expectedCellBooleanValue = expected.getBooleanCellValue();
                        boolean actualCellBooleanValue = actual.getBooleanCellValue();
                        if (expectedCellBooleanValue != actualCellBooleanValue)
                            return ComparisonResult.DIFFERENT.setDifferences("Sheet " + actual.getSheet().getSheetName() + " has different cell " + actual.getColumnIndex() + " values in row " + actual.getRow().getRowNum() + ". Expected=" + expectedCellBooleanValue + " Actual=" + actualCellBooleanValue);
                        break;

                    default:
                        return ComparisonResult.DIFFERENT.setDifferences("Unexpected cell type in row " + actual.getRow().getRowNum() + " cell " + actual.getColumnIndex());
                }

            } else {
                return ComparisonResult.DIFFERENT.setDifferences("Unexpected cell type in row " + actual.getRow().getRowNum() + " cell " + actual.getColumnIndex());
            }
        }
        return ComparisonResult.EQUAL;
    }
}
