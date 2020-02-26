package com.mps.insight.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.mps.insight.dao.MyDataTable;
import com.mps.insight.dto.RequestMetaData;

public class ExcelFormatImpl {
	private RequestMetaData rmd; 
	public ExcelFormatImpl(RequestMetaData rmd) {
		this.rmd = rmd;
	}

	//private static final Logger log = LoggerFactory.getLogger(ExcelFormatImpl.class);

	public InputStream getExcelReports(MyDataTable mdt, HashMap<String, String> header) {

		InputStream io = null;

		try {
			int rowCountHeader = Integer.parseInt(header.get("rowcount").toString());
			int rowCount = mdt.getRowCount();
			int colCount = mdt.getColumnCount();

			String sheetName = "Sheet1";// name of sheet

			XSSFWorkbook wb = new XSSFWorkbook();
			XSSFSheet sheet = wb.createSheet(sheetName);
			XSSFRow row = null;
			XSSFCell cell = null;
			for (int i = 1; i <= rowCountHeader; i++) {
				row = sheet.createRow(i - 1);
				cell = row.createCell(0);
				cell.setCellValue(header.get("R" + i + "C1"));
				cell = row.createCell(1);
				try {
					cell.setCellValue(Integer.parseInt(header.get("R" + i + "C2")));
				} catch (NumberFormatException ne) {
					cell.setCellValue(header.get("R" + i + "C2"));
				} catch (Exception e) {
					rmd.log(e.getMessage());
				}
				//cell.setCellValue(header.get("R" + i + "C2"));
			}
			row = sheet.createRow(rowCountHeader);
			for (int colNo = 1; colNo <= colCount; colNo++) {
				cell = row.createCell(colNo - 1);
				/*try {
					cell.setCellValue(Integer.parseInt(mdt.getColumn(colNo).absoluteName));
				} catch (NumberFormatException ne) {
					cell.setCellValue(mdt.getColumn(colNo).absoluteName);
				} catch (Exception e) {
					rmd.log(e.getMessage());
				}*/
				String colName=mdt.getColumn(colNo).absoluteName;
				 colName = colName.equalsIgnoreCase("")?mdt.getColumn(colNo).name : colName;
				cell.setCellValue(colName);
			}
			// iterating r number of rows
			for (int r = rowCountHeader + 1; r <= rowCount + rowCountHeader; r++) {
				row = sheet.createRow(r);

				// iterating c number of columns
				for (int c = 0; c < colCount; c++) {
					cell = row.createCell(c);
					try {
						cell.setCellValue(Integer.parseInt(mdt.getValue(r - rowCountHeader, c + 1)));
					} catch (NumberFormatException ne) {
						cell.setCellValue(mdt.getValue(r - rowCountHeader, c + 1));
					} catch (Exception e) {
						rmd.log(e.getMessage());
					}
				}
			}
			ByteArrayOutputStream fileOut = new ByteArrayOutputStream();

			// write this workbook to an Outputstream.

			wb.write(fileOut);
			io = new ByteArrayInputStream(fileOut.toByteArray());
		} catch (Exception e) {
			rmd.log(e.getMessage());
		}
		return io;
	}

}
