package br.com.redefood.util;

public class PrinterConstants {

	public static final String ESC = "" + (char) 27;
	public static final String UTF8 = ("" + (char) 27 + (char) 116 + (char) 8);
	public static final String HIGH_QUALITY = ("" + (char) 29 + (char) 249 + (char) 45 + (char) 1);
	public static final String FONT_SMALL = (ESC + (char) 33 + (char) 1);
	public static final String FONT_NORMAL = (ESC + (char) 33 + (char) 0);
	public static final String FONT_BIG = (ESC + (char) 33 + (char) 16);
	public static final String FONT_WIDE = (ESC + (char) 33 + (char) 32);
	public static final String FONT_SUPER_BIG = (ESC + (char) 33 + (char) 48);
	public static final String CUT_PAPER = ("" + (char) 27 + (char) 119);
	public static final String PARTIAL_CUT_PAPER = (ESC + (char) 109);
	public static final String BOLD_ON = (ESC + (char) 69);
	public static final String BOLD_OFF = (ESC + (char) 70);
	public static final String ITALIC_ON = (ESC + (char) 52);
	public static final String ITALIC_OFF = (ESC + (char) 53);
	public static final String UNDERLINE_ON = (ESC + (char) 45 + (char) 1);
	public static final String UNDERLINE_OFF = (ESC + (char) 45 + (char) 0);
	public static final String HORIZONTAL_TAB = ("" + (char) 9);
	public static final String LINE_BREAK = ("" + (char) 10);
	public static final String ALLINGMENT_LEFT = (ESC + (char) 97 + (char) 0);
	public static final String ALLINGMENT_CENTER = (ESC + (char) 97 + (char) 1);
	public static final String ALLINGMENT_RIGHT = (ESC + (char) 97 + (char) 2);
	public static final String MARGIN_RIGHT = (ESC + (char) 81 + (char) 0);
	public static final String SUPERSCRIPT = (ESC + (char) 83 + (char) 0);
	public static final String SUBSCRIPT = (ESC + (char) 83 + (char) 1);
	public static final String SUPER_SUB_SCRIPT_OFF = (ESC + (char) 84);
	public static final String CLEAR = (ESC + (char) 64);
	public static final String PAPER_STATUS = "" + (char) 29 + (char) 118;
}
