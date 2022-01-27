package com.werfen.report.test.utils.excel;


import com.werfen.report.test.utils.exception.FilesNotEqualException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ExcelComparator {

    public static void assertFileEquals(File expectedFile, File actualFile) {
        assertDoesNotThrow(() -> ExcelComparator.compareFileEquals(expectedFile, actualFile));
    }

    public static void assertFileNotEquals(File expectedFile, File actualFile) {
        assertThrows(FilesNotEqualException.class, () -> ExcelComparator.compareFileEquals(expectedFile, actualFile));
    }

    public static void compareFileEquals(File expectedFile, File actualFile) throws IOException, InvalidFormatException, FilesNotEqualException {
        try (FileInputStream expectedFileInputStream = new FileInputStream(expectedFile);
             Workbook expected = WorkbookFactory.create(expectedFileInputStream);
             Workbook generated = new XSSFWorkbook(actualFile)) {
            compareWorkbookEquals(expected, generated);
        }
    }

    public static void compareWorkbookEquals(Workbook expected, Workbook actual) throws FilesNotEqualException {
        int expectedSheetCount = expected.getNumberOfSheets();
        int actualSheetCount = actual.getNumberOfSheets();
        if (expectedSheetCount != actualSheetCount)
            throw new FilesNotEqualException("Excel work books have different number of sheets", String.valueOf(expectedSheetCount), String.valueOf(actualSheetCount));

        for (int sheetIndex = 0; sheetIndex < expectedSheetCount; sheetIndex++) {
            compareSheetEquals(expected.getSheetAt(sheetIndex), actual.getSheetAt(sheetIndex));
        }
    }

    public static void compareSheetEquals(Sheet expectedSheet, Sheet actualSheet) throws FilesNotEqualException {
        String expectedSheetName = expectedSheet.getSheetName();
        String actualSheetName = actualSheet.getSheetName();
        if (isNull(expectedSheetName) || isNull(actualSheetName) || !expectedSheetName.equals(actualSheetName))
            throw new FilesNotEqualException("Sheets have different names", expectedSheetName, actualSheetName);

        int expectedRowCount = expectedSheet.getPhysicalNumberOfRows();
        int actualRowCount = actualSheet.getPhysicalNumberOfRows();
        if (expectedRowCount != actualRowCount)
            throw new FilesNotEqualException("Sheet " + actualSheet.getSheetName() + " have different number of rows", String.valueOf(expectedRowCount), String.valueOf(actualRowCount));

        for (int rowIndex = 0; rowIndex < expectedRowCount; rowIndex++) {
            compareRowEquals(expectedSheet.getRow(rowIndex), actualSheet.getRow(rowIndex));
        }
    }

    public static void compareRowEquals(Row expected, Row actual) throws FilesNotEqualException {
        if (nonNull(expected) && nonNull(actual)) {
            int expectedCellCount = expected.getPhysicalNumberOfCells();
            int actualCellCount = actual.getPhysicalNumberOfCells();
            if (expectedCellCount != actualCellCount)
                throw new FilesNotEqualException("Sheet " + actual.getSheet().getSheetName() + " have different number of cells in row " + actual.getRowNum(), String.valueOf(expectedCellCount), String.valueOf(actualCellCount));

            for (int cellIndex = 0; cellIndex < expectedCellCount; cellIndex++) {
                compareCellEquals(expected.getCell(cellIndex), actual.getCell(cellIndex));
            }
        }
    }

    public static void compareCellEquals(Cell expected, Cell actual) throws FilesNotEqualException {
        if (nonNull(expected) && nonNull(actual)) {
            if (expected.getCellType().equals(actual.getCellType())) {
                switch (expected.getCellType()) {
                    case STRING:
                        String expectedCellStringValue = expected.getStringCellValue();
                        String actualCellStringValue = actual.getStringCellValue();
                        if (isNull(expectedCellStringValue) || isNull(actualCellStringValue) || !expectedCellStringValue.equals(actualCellStringValue))
                            throw new FilesNotEqualException("Sheet " + actual.getSheet().getSheetName() + " has different cell " + actual.getColumnIndex() + " values in row " + actual.getRow().getRowNum(), expectedCellStringValue, actualCellStringValue);
                        break;

                    case NUMERIC:
                        if (DateUtil.isCellDateFormatted(expected) | DateUtil.isCellDateFormatted(actual)) {
                            DataFormatter df = new DataFormatter();
                            String expectedCellDateValue = df.formatCellValue(expected);
                            String actualCellDateValue = df.formatCellValue(actual);
                            if (isNull(expectedCellDateValue) || isNull(actualCellDateValue) || !expectedCellDateValue.equals(actualCellDateValue))
                                throw new FilesNotEqualException("Sheet " + actual.getSheet().getSheetName() + " has different cell " + actual.getColumnIndex() + " values in row " + actual.getRow().getRowNum(), expectedCellDateValue, actualCellDateValue);
                        } else {
                            double expectedCellNumericValue = expected.getNumericCellValue();
                            double actualCellNumericValue = actual.getNumericCellValue();
                            if (expectedCellNumericValue != actualCellNumericValue)
                                throw new FilesNotEqualException("Sheet " + actual.getSheet().getSheetName() + " has different cell " + actual.getColumnIndex() + " values in row " + actual.getRow().getRowNum(), String.valueOf(expectedCellNumericValue), String.valueOf(actualCellNumericValue));
                        }
                        break;

                    case BOOLEAN:
                        boolean expectedCellBooleanValue = expected.getBooleanCellValue();
                        boolean actualCellBooleanValue = actual.getBooleanCellValue();
                        if (expectedCellBooleanValue != actualCellBooleanValue)
                            throw new FilesNotEqualException("Sheet " + actual.getSheet().getSheetName() + " has different cell " + actual.getColumnIndex() + " values in row " + actual.getRow().getRowNum(), String.valueOf(expectedCellBooleanValue), String.valueOf(actualCellBooleanValue));
                        break;

                    default:
                        throw new FilesNotEqualException("Unexpected cell type in row " + actual.getRow().getRowNum() + " cell " + actual.getColumnIndex());
                }

            } else {
                throw new FilesNotEqualException("Unexpected cell type in row " + actual.getRow().getRowNum() + " cell " + actual.getColumnIndex());
            }
        }
    }
}
