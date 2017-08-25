package net.minidev.xlsx;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.TreeMap;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

public class XlsxBuilder {
	public Workbook wb;
	TreeMap<String, Sheet> sheets;
	String currentTab;
	public Sheet sh;
	int rid;
	TreeMap<String, CellStyle> styles = new TreeMap<String, CellStyle>();
	CellStyle style = null;

	public XlsxBuilder() {
		wb = new SXSSFWorkbook(100);
		sheets = new TreeMap<String, Sheet>();
	}

	/**
	 * size in chars count
	 * 
	 * @param sizes
	 */
	public void setColumnWidth(int... sizes) {
		int i = 0;
		for (int size : sizes) {
			if (size == -1)
				sh.autoSizeColumn(i);
			else
				sh.setColumnWidth(i, size * 256);
			i++;
		}
	}

	public void setSheet(String name) {
		currentTab = name;
		sh = wb.getSheet(name);
		if (sh == null) {
			sh = wb.createSheet(name);
			rid = 0;
		} else {
			rid = sh.getLastRowNum() + 1;
		}
	}

	public int getRid() {
		return rid;
	}

	public void save(String fileName) throws IOException {
		save(new File(fileName));
	}

	public void save(File f) throws IOException {
		FileOutputStream out = new FileOutputStream(f);
		wb.write(out);
		out.close();
	}

	public String getCurrentTab() {
		return currentTab;
	}

	public void addLine(Object... data) {
		Row row = sh.createRow(rid++);
		int cellId = 0;
		for (Object s1 : data) {
			Object s;
			if (s1 instanceof StyledElm)
				s = ((StyledElm) s1).data;
			else
				s = s1;

			Cell cell = row.createCell(cellId++);
			if (s instanceof String) {
				cell.setCellValue(s.toString());
			} else if (s instanceof Number) {
				cell.setCellType(CellType.NUMERIC);
				cell.setCellValue(((Number) s).doubleValue());
			} else if (s instanceof URL) {
				org.apache.poi.ss.usermodel.Hyperlink link = null;
				link = wb.getCreationHelper().createHyperlink(HyperlinkType.URL);
				link.setAddress(s.toString());
				cell.setHyperlink(link);
				cell.setCellValue(s.toString());
			} else {
				// cell.setCellType(Cell.CELL_TYPE_FORMULA)
			}
			if (s1 instanceof StyledElm) {
				cell.setCellStyle(((StyledElm) s1).style);
				if (((StyledElm) s1).comment != null) {
					CreationHelper factory = wb.getCreationHelper();
					ClientAnchor anchor = factory.createClientAnchor();
					anchor.setCol1(cell.getColumnIndex());
					anchor.setCol2(cell.getColumnIndex() + 1);
					anchor.setRow1(row.getRowNum());
					anchor.setRow2(row.getRowNum() + 3);

					Drawing<?> drawing = sh.createDrawingPatriarch();

					// Create the comment and set the text+author
					Comment comment = drawing.createCellComment(anchor);
					RichTextString str = factory.createRichTextString(((StyledElm) s1).comment);
					comment.setString(str);
					comment.setAuthor("info");
					cell.setCellComment(comment);

				}
			} else if (style != null)
				cell.setCellStyle(style);
		}
	}

	public CellStyle getCellStyle(String color) {
		CellStyle style = styles.get(color);
		if (style != null)
			return style;

		if (color.equalsIgnoreCase("red")) {
			style = wb.createCellStyle();
			style.setFillForegroundColor(IndexedColors.RED.getIndex());
			Font font = wb.createFont();
			font.setBold(true);
			font.setColor(IndexedColors.WHITE.index);
			style.setFont(font);
		} else if (color.equalsIgnoreCase("small6")) {
			style = wb.createCellStyle();
			Font font = wb.createFont();
			font.setFontHeightInPoints((short) 6);
			style.setFont(font);
			RegisterStyle(color, style);
			return style;
		} else if (color.equalsIgnoreCase("small8")) {
			style = wb.createCellStyle();
			Font font = wb.createFont();
			font.setFontHeightInPoints((short) 8);
			style.setFont(font);
			RegisterStyle(color, style);
			return style;
		} else if (color.equalsIgnoreCase("green")) {
			style = wb.createCellStyle();
			style.setFillForegroundColor(IndexedColors.GREEN.getIndex());
		} else if (color.equalsIgnoreCase("lightBlue")) {
			style = wb.createCellStyle();
			style.setFillForegroundColor(IndexedColors.BLUE.getIndex());

			Font font = wb.createFont();
			font.setBold(true);
			font.setColor(IndexedColors.WHITE.index);
			style.setFont(font);
		} else if (color.equalsIgnoreCase("lightGreen")) {
			style = wb.createCellStyle();
			style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
		} else if (color.equalsIgnoreCase("orange")) {
			style = wb.createCellStyle();
			style.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
		} else if (color.equalsIgnoreCase("yellow")) {
			style = wb.createCellStyle();
			style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
		} else if (color.equalsIgnoreCase("bold")) {
			style = wb.createCellStyle();
			style.setBorderBottom(BorderStyle.MEDIUM);
			style.setBorderLeft(BorderStyle.MEDIUM);
			style.setBorderRight(BorderStyle.MEDIUM);
			style.setBorderTop(BorderStyle.MEDIUM);
			org.apache.poi.ss.usermodel.Font font = wb.createFont();
			font.setBold(true);
			style.setFont(font);
			style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		}
		if (style != null) {
			style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			RegisterStyle(color, style);
		}
		return style;
	}

	public void RegisterStyle(String color, CellStyle style) {
		styles.put(color, style);
	}

	public void setColor(String color) {
		if (color == null) {
			style = null;
			return;
		}
		this.style = getCellStyle(color);
	}

	public class StyledElm {
		public StyledElm(Object data, CellStyle style) {
			this(data, style, null);
		}

		public StyledElm(Object data, CellStyle style, String comment) {
			this.data = data;
			this.style = style;
			this.comment = comment;
		}

		public StyledElm(Object data, String style) {
			this(data, style, null);
		}

		public StyledElm(Object data, String style, String comment) {
			this(data, getCellStyle(style), comment);
		}

		public CellStyle style;
		public Object data;
		public String comment;
	}

}
