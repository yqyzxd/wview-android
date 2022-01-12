package com.wind.view.util;

import android.content.Context;
import android.text.ClipboardManager;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtil {





	public static String replaceHtml(String html, String tag) {
		Pattern p = Pattern.compile(tag);
		Matcher m = p.matcher(html);
		String s = m.replaceAll("");
		return s;
	}

	public static String getOnlyNumStr(String str) {
		String regEx = "[^0-9]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return (m.replaceAll("").trim());
	}

	public static int countChinese(String s) {
		int count = 0;
//		Pattern p = Pattern.compile("[^\\x00-\\xff]");//包括中文标点
		Pattern p = Pattern.compile("[\\u4e00-\\u9fa5]");//不包括中文标点
		Matcher m = p.matcher(s);
		while (m.find()) {
			count++;
		}
		return count;

	}

	public static int getWordCount(String s) {

		s = s.replaceAll("[^\\x00-\\xff]", "**");
		int length = s.length();
		return length;
	}

	/**
	 * 返回  还可输入多少字
	 *
	 * @param context
	 * @param hasChars 已经输入了多少字符
	 * @return
	 */
	public static String formatLeftHint(Context context, int hasChars, int maxChars) {
//        return hasChars + "/" + maxChars;
		int max = maxChars / 2;
		int has = hasChars / 2;
		int remainder=hasChars%2;
		if(remainder>0){
			has++;
		}
		return has + "/" + max;
	}

	public static String delHTMLTag(String htmlStr) {
		String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; // 定义script的正则表达式
		String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; // 定义style的正则表达式
		String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式

		Pattern p_script = Pattern.compile(regEx_script,
				Pattern.CASE_INSENSITIVE);
		Matcher m_script = p_script.matcher(htmlStr);
		htmlStr = m_script.replaceAll(""); // 过滤script标签

		Pattern p_style = Pattern
				.compile(regEx_style, Pattern.CASE_INSENSITIVE);
		Matcher m_style = p_style.matcher(htmlStr);
		htmlStr = m_style.replaceAll(""); // 过滤style标签

		Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
		Matcher m_html = p_html.matcher(htmlStr);
		htmlStr = m_html.replaceAll(""); // 过滤html标签
		return htmlStr; // 返回文本字符串
	}

	/**
	 * 判断给定字符串是否空白串。<br>
	 * 空白串是指由空格、制表符、回车符、换行符组成的字符串<br>
	 * 若输入字符串为null或空字符串，返回true
	 * 
	 * @param input
	 * @return boolean
	 */
	public static boolean isBlank(String input) {
		if (input == null || "".equals(input))
			return true;

		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
				return false;
			}
		}
		return true;
	}

	public static void testCharset(String datastr) {
		try {
			String temp = new String(datastr.getBytes(), "GBK");
			temp = new String(datastr.getBytes("GBK"), "UTF-8");
			temp = new String(datastr.getBytes("GBK"), "ISO-8859-1");
			temp = new String(datastr.getBytes("ISO-8859-1"), "UTF-8");
			temp = new String(datastr.getBytes("ISO-8859-1"), "GBK");
			temp = new String(datastr.getBytes("UTF-8"), "GBK");
			temp = new String(datastr.getBytes("UTF-8"), "ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 作者: peijiangping<BR>
	 * 时间:2013-1-7下午2:45:33<BR>
	 * 功能:全角转半角<BR>
	 * 返回值:String<BR>
	 */
	public static final String QBchange(String QJstr) {
		StringBuffer outStrBuf = new StringBuffer("");
		String Tstr = "";
		try {
			byte[] b = null;
			for (int i = 0; i < QJstr.length(); i++) {
				Tstr = QJstr.substring(i, i + 1);
				if (Tstr.equals(" ")) {
					// 半角空格
					outStrBuf.append(Tstr);
					continue;
				}
				b = Tstr.getBytes("unicode");
				if (b[2] == 0) {
					// 半角?
					b[3] = (byte) (b[3] - 32);
					b[2] = -1;
					outStrBuf.append(new String(b, "unicode"));
				} else {
					outStrBuf.append(Tstr);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outStrBuf.toString();

	}

	/**
	 * 作者: peijiangping<BR>
	 * 时间:2013-1-7下午2:46:03<BR>
	 * 功能:半角转全角<BR>
	 * 返回值:String<BR>
	 */
	public static final String BQchange(String QJstr, String code) {
		String outStr = "";
		String Tstr = "";
		byte[] b = null;
		for (int i = 0; i < QJstr.length(); i++) {
			try {
				Tstr = QJstr.substring(i, i + 1);
				b = Tstr.getBytes(code);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			if (b[3] != -1) {
				b[2] = (byte) (b[2] - 32);
				b[3] = -1;
				try {
					outStr = outStr + new String(b, code);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			} else
				outStr = outStr + Tstr;
		}
		return outStr;
	}

	public static boolean notNull(String str) {
		if (str != null && str.equals("") == false
				&& str.equals("null") == false) {
			return true;
		}
		return false;
	}

	/**
	 * 实现文本复制功能 add by wangqianzhou
	 * 
	 * @param content
	 */
	public static void copy(String content, Context context) {
		// 得到剪贴板管理器
		ClipboardManager cmb = (ClipboardManager) context
				.getSystemService(Context.CLIPBOARD_SERVICE);
		cmb.setText(content.trim());
	}

	/**
	 * 实现粘贴功能 add by wangqianzhou
	 * 
	 * @param context
	 * @return
	 */
	public static String paste(Context context) {
		// 得到剪贴板管理器
		ClipboardManager cmb = (ClipboardManager) context
				.getSystemService(Context.CLIPBOARD_SERVICE);
		return cmb.getText().toString().trim();
	}

	/**
	 * 检测是否有emoji表情
	 *
	 * @param source
	 * @return
	 */
	public static boolean containsEmoji(String source) {
		int len = source.length();
		for (int i = 0; i < len; i++) {
			char codePoint = source.charAt(i);
			if (!isEmojiCharacter(codePoint)) { //如果不能匹配,则该字符是Emoji表情
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断是否是Emoji
	 *
	 * @param codePoint 比较的单个字符
	 * @return
	 */
	private static boolean isEmojiCharacter(char codePoint) {
		return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) ||
				(codePoint == 0xD) || ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
				((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000)
				&& (codePoint <= 0x10FFFF));
	}

	private static String NAME_PATTERN = "^[\\\\s\\u4e00-\\u9fa5a-zA-Z0-9\\s\\p{P}+~$`^=|<>～｀＄＾＋＝｜＜＞￥×£€]+$";

	public static boolean isQualifiedName(String name) {

		Pattern p = Pattern.compile(NAME_PATTERN);
		Matcher m = p.matcher(name);

		return m.matches();
	}
}
