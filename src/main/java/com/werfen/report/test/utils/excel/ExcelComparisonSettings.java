package com.werfen.report.test.utils.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Value;

@Value
public class ExcelComparisonSettings
{

  List<CellExclusion> cellExclusions;

  public ExcelComparisonSettings() {
    this.cellExclusions = Collections.emptyList();
  }

  public ExcelComparisonSettings(List<CellExclusion> cellExclusions) {
    this.cellExclusions = Collections.unmodifiableList(new ArrayList<>(cellExclusions));
  }
}
