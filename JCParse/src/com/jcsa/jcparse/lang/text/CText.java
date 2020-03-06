package com.jcsa.jcparse.lang.text;

import java.util.ArrayList;
import java.util.List;

/**
 * Text of original source program (for both x.i or x.c) <br>
 * - constraint: if `\` and newLine occur together, then both will be escaped.
 * 
 * @author yukimula
 */
public class CText {

	/** newline character **/
	public static final char LINE_SEPARATOR = '\n';

	/** text to logic source code **/
	protected StringBuilder text;
	/**
	 * line_index[0] = 0, line_index[n] = text.length() line_index[k] |--> to
	 * the first character at line k - 1
	 **/
	private List<Integer> line_index;

	/**
	 * create an empty logic text
	 */
	public CText() {
		text = new StringBuilder();
		line_index = new ArrayList<Integer>();
		line_index.add(0);
	}

	/**
	 * get the number of characters in text
	 * 
	 * @return
	 */
	public int length() {
		return text.length();
	}

	/**
	 * get the kth character in text
	 * 
	 * @param k
	 * @return
	 * @throws Exception
	 */
	public char get_char(int k) throws Exception {
		if (k < 0 || k >= text.length())
			throw new IndexOutOfBoundsException("Invalid index: " + k);
		else
			return text.charAt(k);
	}

	/**
	 * get the number of lines in source code
	 * 
	 * @return
	 */
	public int number_of_lines() {
		return line_index.size() - 1;
	}

	/**
	 * get the index to first character in specified line (starting from 1)
	 * 
	 * @param line
	 * @return
	 * @throws Exception
	 */
	public int index_of(int line) throws IndexOutOfBoundsException {
		if (line <= 0 || line >= line_index.size())
			throw new IndexOutOfBoundsException(
					"Invalid line: " + line + " (expected within " + line_index.size() + ")");
		else
			return line_index.get(line - 1);
	}

	/**
	 * get the line of character specified by index
	 * 
	 * @param index
	 * @return
	 * @throws Exception
	 */
	public int line_of(int index) throws Exception {
		if (index < 0 || index >= text.length()) {
			throw new IndexOutOfBoundsException("Invalid index: " + index);
		} else {
			// binary-search
			int s = 0, e = line_index.size();
			while (s < e) {
				int m = (s + e) / 2;
				int head = line_index.get(m);
				int tail;
				tail = line_index.get(m + 1);

				if (index >= tail)
					s = m + 1;
				else if (index < head)
					e = m;
				else
					return m + 1;
			}

			// No target is found, this is impossible.
			throw new RuntimeException("Internal error: no line is found for " + index);
		}
	}

	/**
	 * get the substring of text
	 * 
	 * @param beg
	 *            : to the first character
	 * @param end
	 *            : to the character following last one
	 * @return
	 */
	public String substring(int beg, int end) {
		return text.substring(beg, end);
	}

	/**
	 * append text in the tail of source text, this will update the line-index
	 * in the object
	 * 
	 * @param str
	 * @return
	 */
	public void append(String str) {
		int i, length = str.length();
		for (i = 0; i < length; i++) {
			char ch = str.charAt(i); // get next character

			// skip escaped new_line
			if (ch == '\\' && i < length - 1) {
				if (str.charAt(i + 1) == LINE_SEPARATOR) {
					i = i + 2;
					continue;
				}
			}

			// update line-index
			text.append(ch);
			if (ch == LINE_SEPARATOR)
				line_index.add(text.length());
		} // end for
	}

	/**
	 * get the stream of this text
	 * 
	 * @return
	 */
	public CStream get_stream() {
		return new CStream(this);
	}

	/**
	 * get the location of text by specifying its bias and length where the
	 * segment this location represents
	 * 
	 * @param bias
	 * @param length
	 * @return
	 */
	public CLocation get_location(int bias, int length) {
		return new CLocation(this, bias, length);
	}

	/**
	 * get the text of this source code
	 */
	@Override
	public String toString() {
		return text.toString();
	}

}
