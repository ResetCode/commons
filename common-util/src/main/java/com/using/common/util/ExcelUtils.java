package com.using.common.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExcelUtils {
	private static Logger LOGGER = LoggerFactory.getLogger(ExcelUtils.class);

	/**
	 * 导出数据到Excel<br>
	 * <dt>支持的数据类型为:</dt>
	 * <dd><code>java.lang.String</code></dd>
	 * <dd><code>java.lang.Double</code></dd>
	 * <dd><code>java.util.Date</code></dd>
	 * <dd><code>java.util.Calendar</code></dd>
	 * <dd><code>org.apache.poi.ss.usermodel.RichTextString</code></dd>
	 * <b>其他类型将按照<code>java.lang.String</code>处理</b>
	 * 
	 * @param title
	 *            标题
	 * @param dataList
	 *            数据列表
	 * @return
	 * @throws IOException
	 */
	public static byte[] export(String[] title, List<Object[]> dataList) throws IOException {
		XSSFWorkbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet();
		Row titleRow = sheet.createRow(0);

		// 设置标题格式(居中, 加粗)
		CellStyle titleStyle = workbook.createCellStyle();
		titleStyle.setAlignment(HorizontalAlignment.CENTER);
		Font font = workbook.createFont();
		font.setBold(true);
		titleStyle.setFont(font);
		for (int i = 0; i < title.length; i++) {
			Cell cell = titleRow.createCell(i);
			cell.setCellValue(title[i]);
			cell.setCellStyle(titleStyle);
			// 根据标题计算宽度
			sheet.setColumnWidth(i, title[i].getBytes().length * 2 * 256);
		}

		if (dataList == null || dataList.isEmpty()) {
			Row row = sheet.createRow(1);
			Cell cell = row.createCell(0);
			cell.setCellValue("暂无数据");
		}

		// 设置内容居中
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		cellStyle.setWrapText(true);
		for (int i = 0; i < dataList.size(); i++) {
			Row row = sheet.createRow(i + 1);
			Object[] dataArray = dataList.get(i);
			for (int j = 0; j < dataArray.length; j++) {
				Cell cell = row.createCell(j);
				Object value = dataArray[j];

				if (value == null)
					cell.setCellValue("");
				else if (value instanceof String)
					cell.setCellValue(value.toString());
				else if (value instanceof Double)
					cell.setCellValue((Double) value);
				else if (value instanceof Boolean)
					cell.setCellValue((Boolean) value);
				else if (value instanceof Date) {
					cell.setCellValue((Date) value);
					DataFormat dateFormat = workbook.createDataFormat();
					cellStyle.setDataFormat(dateFormat.getFormat("yyyy-mm-dd hh:mm:ss"));
				} else if (value instanceof Calendar) {
					cell.setCellValue((Calendar) value);
					DataFormat dateFormat = workbook.createDataFormat();
					cellStyle.setDataFormat(dateFormat.getFormat("yyyy-mm-dd hh:mm:ss"));
				} else if (value instanceof RichTextString)
					cell.setCellValue((RichTextString) value);
				else {
					LOGGER.warn(value.getClass() + "不支持的数据类型, 将按String处理");
					cell.setCellValue(value.toString());
				}

				cell.setCellStyle(cellStyle);
			}
		}

		try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
			workbook.write(os);
			workbook.close();
			os.flush();
			byte[] excelData = os.toByteArray();
			os.close();
			return excelData;
		} catch (IOException e) {
			throw e;
		}
	}

	/**
	 * 导出数据到Excel<br>
	 * <dt>支持的数据类型为:</dt>
	 * <dd><code>java.lang.String</code></dd>
	 * <dd><code>java.lang.Double</code></dd>
	 * <dd><code>java.util.Date</code></dd>
	 * <dd><code>java.util.Calendar</code></dd>
	 * <dd><code>org.apache.poi.ss.usermodel.RichTextString</code></dd>
	 * <b>其他类型将按照<code>java.lang.String</code>处理</b>
	 * 
	 * @param title
	 *            标题
	 * @param dataList
	 *            数据列表
	 * @return
	 */
	public static XSSFWorkbook exportToWorkbook(String[] title, List<Object[]> dataList) {
		XSSFWorkbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet();
		Row titleRow = sheet.createRow(0);

		// 设置标题格式(居中, 加粗)
		CellStyle titleStyle = workbook.createCellStyle();
		titleStyle.setAlignment(HorizontalAlignment.CENTER);
		Font font = workbook.createFont();
		font.setBold(true);
		titleStyle.setFont(font);
		for (int i = 0; i < title.length; i++) {
			Cell cell = titleRow.createCell(i);
			cell.setCellValue(title[i]);
			cell.setCellStyle(titleStyle);
			// 根据标题计算宽度
			sheet.setColumnWidth(i, title[i].getBytes().length * 2 * 256);
		}

		if (dataList == null || dataList.isEmpty()) {
			Row row = sheet.createRow(1);
			Cell cell = row.createCell(0);
			cell.setCellValue("暂无数据");
		}

		// 设置内容居中
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		for (int i = 0; i < dataList.size(); i++) {
			Row row = sheet.createRow(i + 1);
			Object[] dataArray = dataList.get(i);
			for (int j = 0; j < dataArray.length; j++) {
				Cell cell = row.createCell(j);
				Object value = dataArray[j];

				if (value == null)
					cell.setCellValue("");
				else if (value instanceof String)
					cell.setCellValue(value.toString());
				else if (value instanceof Double)
					cell.setCellValue((Double) value);
				else if (value instanceof Boolean)
					cell.setCellValue((Boolean) value);
				else if (value instanceof Date) {
					cell.setCellValue((Date) value);
					DataFormat dateFormat = workbook.createDataFormat();
					cellStyle.setDataFormat(dateFormat.getFormat("yyyy-mm-dd hh:mm:ss"));
				} else if (value instanceof Calendar) {
					cell.setCellValue((Calendar) value);
					DataFormat dateFormat = workbook.createDataFormat();
					cellStyle.setDataFormat(dateFormat.getFormat("yyyy-mm-dd hh:mm:ss"));
				} else if (value instanceof RichTextString)
					cell.setCellValue((RichTextString) value);
				else {
					LOGGER.warn(value.getClass() + "不支持的数据类型, 将按String处理");
					cell.setCellValue(value.toString());
				}

				cell.setCellStyle(cellStyle);
			}
		}

		return workbook;
	}
}