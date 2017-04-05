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
import org.apache.poi.ss.usermodel.CellType;
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
				CellType type = cell.getCellTypeEnum();
				switch (type) {
				case BLANK:
					next.add(null);
					break;
				case BOOLEAN:
					next.add(cell.getBooleanCellValue());
					break;
				case ERROR:
					next.add(null);
					break;
				case FORMULA:
					try {
						CellValue cellValue = eval.evaluate(cell);
						switch (cellValue.getCellTypeEnum()) {
						case NUMERIC:
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
				case NUMERIC:
					if (HSSFDateUtil.isCellDateFormatted(cell)) {
						next.add(HSSFDateUtil.getJavaDate(cell.getNumericCellValue()));
					} else {
						String raw = cell.getRawValue();
						next.add(raw); // cell.getNumericCellValue()
					}
					break;
				case STRING:
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

	public static String getString(List<Object> list, int pos) {
		if (list.size() <= pos)
			return null;
		Object obj = list.get(pos);
		if (obj == null)
			return null;
		return obj.toString();

	}

}
