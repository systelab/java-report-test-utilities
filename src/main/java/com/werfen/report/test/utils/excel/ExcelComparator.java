package com.werfen.report.test.utils.excel;


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
        if (nonNull(expectedFile) && nonNull(actualFile)) {
            try (FileInputStream expectedFileInputStream = new FileInputStream(expectedFile);
                 Workbook expected = WorkbookFactory.create(expectedFileInputStream);
                 Workbook generated = new XSSFWorkbook(actualFile)) {
                return compareWorkbooks(expected, generated);
            }
        } else if (isNull(expectedFile) && isNull(actualFile)) {
            return ComparisonResult.EQUAL;
        } else {
            return ComparisonResult.DIFFERENT.setDifferences("Files are different. Expected=" + expectedFile + " Actual=" + actualFile);
        }
    }

    public static ComparisonResult compareWorkbooks(Workbook expectedWorkbook, Workbook actualWorkbook) {
        if (nonNull(expectedWorkbook) && nonNull(actualWorkbook)) {
            int expectedSheetCount = expectedWorkbook.getNumberOfSheets();
            int actualSheetCount = actualWorkbook.getNumberOfSheets();
            if (expectedSheetCount != actualSheetCount)
                return ComparisonResult.DIFFERENT.setDifferences("Excel work books have different number of sheets. Expected=" + expectedSheetCount + " Actual=" + actualSheetCount);

            for (int sheetIndex = 0; sheetIndex < expectedSheetCount; sheetIndex++) {
                ComparisonResult comparisonResult = compareSheets(expectedWorkbook.getSheetAt(sheetIndex), actualWorkbook.getSheetAt(sheetIndex));
                if (!comparisonResult.areEqual())
                    return comparisonResult;
            }
            return ComparisonResult.EQUAL;
        } else if (isNull(expectedWorkbook) && isNull(actualWorkbook)) {
            return ComparisonResult.EQUAL;
        } else {
            return ComparisonResult.DIFFERENT.setDifferences("Workbooks are different. Expected=" + expectedWorkbook + " Actual=" + actualWorkbook);
        }
    }

    public static ComparisonResult compareSheets(Sheet expectedSheet, Sheet actualSheet) {
        if (nonNull(expectedSheet) && nonNull(actualSheet)) {
            String expectedSheetName = expectedSheet.getSheetName();
            String actualSheetName = actualSheet.getSheetName();
            if (!expectedSheetName.equals(actualSheetName))
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
        } else if (isNull(expectedSheet) && isNull(actualSheet)) {
            return ComparisonResult.EQUAL;
        } else {
            return ComparisonResult.DIFFERENT.setDifferences("Sheets are different. Expected=" + expectedSheet + " Actual=" + actualSheet);
        }
    }

    public static ComparisonResult compareRows(Row expectedRow, Row actualRow) {
        if (nonNull(expectedRow) && nonNull(actualRow)) {
            int expectedCellCount = expectedRow.getPhysicalNumberOfCells();
            int actualCellCount = actualRow.getPhysicalNumberOfCells();
            if (expectedCellCount != actualCellCount)
                return ComparisonResult.DIFFERENT.setDifferences("Sheet " + actualRow.getSheet().getSheetName() + " have different number of cells in row " + actualRow.getRowNum() + ". Expected=" + expectedCellCount + " Actual=" + actualCellCount);

            for (int cellIndex = 0; cellIndex < expectedCellCount; cellIndex++) {
                ComparisonResult comparisonResult = compareCells(expectedRow.getCell(cellIndex), actualRow.getCell(cellIndex));
                if (!comparisonResult.areEqual())
                    return comparisonResult;
            }
            return ComparisonResult.EQUAL;
        } else if (isNull(expectedRow) && isNull(actualRow)) {
            return ComparisonResult.EQUAL;
        } else {
            return ComparisonResult.DIFFERENT.setDifferences("Rows are different. Expected=" + expectedRow + " Actual=" + actualRow);
        }
    }

    public static ComparisonResult compareCells(Cell expectedCell, Cell actualCell) {
        if (nonNull(expectedCell) && nonNull(actualCell)) {
            CellType expectedCellType = expectedCell.getCellType();
            CellType actualCellType = actualCell.getCellType();
            if (expectedCellType.equals(actualCellType)) {
                switch (expectedCell.getCellType()) {
                    case STRING:
                        String expectedCellStringValue = expectedCell.getStringCellValue();
                        String actualCellStringValue = actualCell.getStringCellValue();
                        if (!expectedCellStringValue.equals(actualCellStringValue))
                            return ComparisonResult.DIFFERENT.setDifferences("Sheet " + actualCell.getSheet().getSheetName() + " has different cell " + actualCell.getColumnIndex() + " values in row " + actualCell.getRow().getRowNum() + ". Expected=" + expectedCellStringValue + " Actual=" + actualCellStringValue);
                        else
                            return ComparisonResult.EQUAL;

                    case NUMERIC:
                        if (DateUtil.isCellDateFormatted(expectedCell) | DateUtil.isCellDateFormatted(actualCell)) {
                            DataFormatter df = new DataFormatter();
                            String expectedCellDateValue = df.formatCellValue(expectedCell);
                            String actualCellDateValue = df.formatCellValue(actualCell);
                            if (!expectedCellDateValue.equals(actualCellDateValue))
                                return ComparisonResult.DIFFERENT.setDifferences("Sheet " + actualCell.getSheet().getSheetName() + " has different cell " + actualCell.getColumnIndex() + " values in row " + actualCell.getRow().getRowNum() + ". Expected=" + expectedCellDateValue + " Actual=" + actualCellDateValue);
                            else
                                return ComparisonResult.EQUAL;
                        } else {
                            double expectedCellNumericValue = expectedCell.getNumericCellValue();
                            double actualCellNumericValue = actualCell.getNumericCellValue();
                            if (expectedCellNumericValue != actualCellNumericValue)
                                return ComparisonResult.DIFFERENT.setDifferences("Sheet " + actualCell.getSheet().getSheetName() + " has different cell " + actualCell.getColumnIndex() + " values in row " + actualCell.getRow().getRowNum() + ". Expected=" + expectedCellNumericValue + " Actual=" + actualCellNumericValue);
                            else
                                return ComparisonResult.EQUAL;
                        }

                    case BOOLEAN:
                        boolean expectedCellBooleanValue = expectedCell.getBooleanCellValue();
                        boolean actualCellBooleanValue = actualCell.getBooleanCellValue();
                        if (expectedCellBooleanValue != actualCellBooleanValue)
                            return ComparisonResult.DIFFERENT.setDifferences("Sheet " + actualCell.getSheet().getSheetName() + " has different cell " + actualCell.getColumnIndex() + " values in row " + actualCell.getRow().getRowNum() + ". Expected=" + expectedCellBooleanValue + " Actual=" + actualCellBooleanValue);
                        else
                            return ComparisonResult.EQUAL;

                    default:
                        return ComparisonResult.DIFFERENT.setDifferences("Unexpected cell type in row " + actualCell.getRow().getRowNum() + " cell " + actualCell.getColumnIndex());
                }

            } else {
                return ComparisonResult.DIFFERENT.setDifferences("Sheet " + actualCell.getSheet().getSheetName() + " has different cell " + actualCell.getColumnIndex() + " types in row " + actualCell.getRow().getRowNum() + ". Expected=" + expectedCellType + " Actual=" + actualCellType);
            }
        } else if (isNull(expectedCell) && isNull(actualCell)) {
            return ComparisonResult.EQUAL;
        } else {
            return ComparisonResult.DIFFERENT.setDifferences("Cells are different. Expected=" + expectedCell + " Actual=" + actualCell);
        }
    }
}
