package com.werfen.report.test.utils.excel;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ExcelCellExclusion
{
  private final int sheet;
  private final int row;
  private final int cell;
}

