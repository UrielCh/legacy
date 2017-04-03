package net.minidev.xlsx;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class XlsxReader {
	public static ArrayList<ArrayList<Object>> readXlsx(File filename, String sheetName) throws IOException {
		return readXlsx(filename.getAbsolutePath(), sheetName);
	}

	public static ArrayList<ArrayList<Object>> readXlsx(String filename, String sheetName) throws IOException {
		FileInputStream fis = null;
		fis = new FileInputStream(filename);
		XSSFWorkbook workbook = new XSSFWorkbook(fis);
		XSSFSheet sheet = workbook.getSheet(sheetName);
		ArrayList<ArrayList<Object>> out = readXlsx_(workbook, sheet);
		if (fis != null) {
			fis.close();
		}
		return out;
	}

	public static LinkedHashMap<String, ArrayList<ArrayList<Object>>> readXlsx(String filename) throws IOException {
		LinkedHashMap<String, ArrayList<ArrayList<Object>>> ret = new LinkedHashMap<String, ArrayList<ArrayList<Object>>>();
		FileInputStream fis = null;
		fis = new FileInputStream(filename);
		XSSFWorkbook workbook = new XSSFWorkbook(fis);
		//Iterator<Sheet> workbook.sheetIterator();
		
		for (Sheet sheet : workbook) {
			String sheetName = sheet.getSheetName();
//			if (sheetName.contains("plombier_sub"))
//				System.out.println("aaaAAAaa");
			ArrayList<ArrayList<Object>> out = readXlsx_(workbook, sheet);
			ret.put(sheetName, out);
		}
		if (fis != null) {
			fis.close();
		}
		return ret;
	}

	/**
	 * @param filename
	 * @param sheetNum
	 *            0 base id
	 */
	public static ArrayList<ArrayList<Object>> readXlsx(String filename, Integer sheetNum) throws IOException {
		FileInputStream fis = null;
		fis = new FileInputStream(filename);
		XSSFWorkbook workbook = new XSSFWorkbook(fis);
		XSSFSheet sheet = workbook.getSheetAt(sheetNum);
		ArrayList<ArrayList<Object>> out = readXlsx_(workbook, sheet);
		if (fis != null) {
			fis.close();
		}
		return out;
	}

	private static ArrayList<ArrayList<Object>> readXlsx_(XSSFWorkbook workbook, Sheet sheet) throws IOException {
		ArrayList<ArrayList<Object>> result = new ArrayList<ArrayList<Object>>();
		Iterator<Row> rows = sheet.rowIterator();
		// int number = sheet.getLastRowNum();
		// System.out.println("in " + filename + ":" + sheetName +
		// " number of rows:" + number);
		FormulaEvaluator eval = workbook.getCreationHelper().createFormulaEvaluator();

		while (rows.hasNext()) {
			ArrayList<Object> next = new ArrayList<Object>();

			XSSFRow row = ((XSSFRow) rows.next());
			Iterator<Cell> cells = row.cellIterator();
			while (cells.hasNext()) {
				XSSFCell cell = (XSSFCell) cells.next();
				if (cell == null) {
					next.add(null);
					continue;
				}
				int idx = cell.getColumnIndex();
				while (next.size() < idx)
					next.add(null);
				int type = cell.getCellType();
				switch (type) {
				case Cell.CELL_TYPE_BLANK:
					next.add(null);
					break;
				case Cell.CELL_TYPE_BOOLEAN:
					next.add(cell.getBooleanCellValue());
					break;
				case Cell.CELL_TYPE_ERROR:
					next.add(null);
					break;
				case Cell.CELL_TYPE_FORMULA:
					try {
						CellValue cellValue = eval.evaluate(cell);
						switch (cellValue.getCellType()) {
						case Cell.CELL_TYPE_NUMERIC:
							double v = cellValue.getNumberValue();
							if (DateUtil.isCellDateFormatted(cell)) {
								next.add(DateUtil.getJavaDate(v, true));
								// System.out.print(" = "
								// + );
							} else {
								// System.out.print(" = " + v);
								next.add(v);
							}
							break;
						}
					} catch (Exception e) {
						System.err.println("ERROR evaluating !" + sheet.getSheetName() + " ROW:" + row.getRowNum()
								+ " col:" + cell.getColumnIndex() + " formula:" + cell.getCellFormula());
						next.add(cell.getCellFormula());
						// e.printStackTrace();
					}
					// next.add(cell.getCellFormula());
					break;
				case Cell.CELL_TYPE_NUMERIC:
					if (HSSFDateUtil.isCellDateFormatted(cell)) {
						next.add(HSSFDateUtil.getJavaDate(cell.getNumericCellValue()));
					} else {
						String raw = cell.getRawValue();
						next.add(raw); // cell.getNumericCellValue()
					}
					break;
				case Cell.CELL_TYPE_STRING:
					next.add(cell.getStringCellValue());
					break;
				default:
					next.add(null);
					break;
				}
			}
			result.add(next);
		}
		return result;
	}

	//
	// public static String[][] readXlx(String filename) throws Exception {
	// // String filename = "C:/Users/uriel/Dropbox/sos/tel-OVH.xlsx";
	// XSSFWorkbook wb1 = new XSSFWorkbook(filename);
	// // wb1.get
	// XSSFSheet sheet = wb1.getSheet("tel");
	//
	// int firstRowNum = sheet.getFirstRowNum();
	// int lastRowNum = sheet.getLastRowNum();
	// String[][] datas = new String[lastRowNum - firstRowNum][];
	// for (int i = firstRowNum; i < lastRowNum; i++) {
	// Row row = sheet.getRow(i);
	// if (row == null)
	// continue;
	// int last = row.getLastCellNum();
	// String[] data = new String[last];
	// for (int j = 0; j < data.length; j++) {
	// Cell cell = row.createCell(j);
	// // data[j] = cell.getStringCellValue();
	// int type = cell.getCellType();
	// if (type == Cell.CELL_TYPE_BLANK)
	// data[j] = "";
	// else if (type == Cell.CELL_TYPE_STRING) {
	// data[j] = cell.getStringCellValue();
	// } else if (type == Cell.CELL_TYPE_NUMERIC)
	// data[j] = Double.toString(cell.getNumericCellValue());
	//
	// // * @see Cell#CELL_TYPE_BLANK
	// // * @see Cell#CELL_TYPE_NUMERIC
	// // * @see Cell#CELL_TYPE_STRING
	// // * @see Cell#CELL_TYPE_FORMULA
	// // * @see Cell#CELL_TYPE_BOOLEAN
	// // * @see Cell#CELL_TYPE_ERROR
	// }
	// datas[i - firstRowNum] = data;
	// }
	// return datas;
	// }

	public static String getString(List<Object> list, int pos) {
		if (list.size() <= pos)
			return null;
		Object obj = list.get(pos);
		if (obj == null)
			return null;
		return obj.toString();

	}

}
