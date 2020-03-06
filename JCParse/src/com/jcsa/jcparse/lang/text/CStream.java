package com.jcsa.jcparse.lang.text;

/**
 * To access characters in CText by sequence
 * 
 * @author yukimula
 */
public class CStream {

	/** text to which this stream accesses **/
	private CText text;
	/** code to the original text in source text **/
	private StringBuilder code;
	/** cursor to the character this stream going to access **/
	private int cursor;

	/**
	 * create a stream for a specified text
	 * 
	 * @param text
	 */
	protected CStream(CText text) {
		this.text = text;
		this.code = text.text;
		this.cursor = 0;
	}

	/**
	 * get the source to this stream points
	 * 
	 * @return
	 */
	public CText get_source() {
		return text;
	}

	/**
	 * cursor to the character for reading by get_char()
	 * 
	 * @return
	 */
	public int get_cursor() {
		return cursor;
	}

	/**
	 * whether the cursor is accessible
	 * 
	 * @return
	 */
	public boolean has_char() {
		return cursor >= 0 && cursor < code.length();
	}

	/**
	 * get the character where cursor points to
	 * 
	 * @return
	 */
	public char get_char() {
		return code.charAt(cursor);
	}

	/**
	 * consume the next k characters
	 * 
	 * @param k
	 * @throws Exception
	 *             : cursor + k > code.length()
	 */
	public void consume(int k) throws Exception {
		if (k < 0)
			throw new IllegalArgumentException("Invalid k: " + k);
		else if (cursor + k > code.length())
			throw new IndexOutOfBoundsException("Out of index: " + k + " on " + cursor);
		else
			cursor = cursor + k;
	}

	/**
	 * consume for one more character
	 * 
	 * @throws Exception
	 */
	public void consume() throws Exception {
		consume(1);
	}

	/**
	 * reset the location of the stream
	 * 
	 * @param loc
	 * @throws Exception
	 */
	public void reset(int loc) throws Exception {
		if (loc < 0 || loc > code.length())
			throw new IndexOutOfBoundsException("Invalid location: " + loc);
		else
			cursor = loc;
	}

}
