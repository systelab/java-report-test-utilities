package com.werfen.report.test.utils.excel;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExcelComparisonSettings
{
  private List<ExcelCellExclusion> excelCellExclusions = new ArrayList<>();
  private boolean compareCellStyle;

  public ExcelComparisonSettings(List<ExcelCellExclusion> excelCellExclusions) {
    this.excelCellExclusions = excelCellExclusions;
  }

  public ExcelComparisonSettings(boolean compareCellStyle) {
    this.compareCellStyle = compareCellStyle;
  }

}
