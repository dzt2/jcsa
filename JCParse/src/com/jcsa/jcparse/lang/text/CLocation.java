package com.jcsa.jcparse.lang.text;

/**
 * location that points to a segment in source text <br>
 * A location can be seen as a two-dimensional position like: (base, length)
 * where base refers to the first character in segment and length represents the
 * number of characters within.
 * 
 * @author yukimula
 *
 */
public class CLocation {

	/** bias to the first character **/
	private int bias;
	/** number of characters within **/
	private int length;
	/** text to where this location points **/
	private CText text;

	/**
	 * create a location based on given text and pointers
	 * 
	 * @param text
	 * @param bias
	 * @param length
	 */
	protected CLocation(CText text, int bias, int length) {
		this.text = text;
		this.set_location(bias, length);
	}

	/**
	 * get the source of this location
	 * 
	 * @return
	 */
	public CText get_source() {
		return text;
	}

	/**
	 * get the bias to first character
	 * 
	 * @return
	 */
	public int get_bias() {
		return bias;
	}

	/**
	 * get number of characters within
	 * 
	 * @return
	 */
	public int get_length() {
		return length;
	}

	/**
	 * get the text to this location represents
	 * 
	 * @return
	 */
	public String read() {
		return text.text.substring(bias, bias + length);
	}
	
	/** used to print the trim-code **/
	private static final StringBuilder buffer = new StringBuilder();
	/**
	 * get the trim code text in which \t and \n are
	 * replaced as space
	 * @param max_length
	 * @return
	 */
	public String trim_code(int max_length) {
		buffer.setLength(0);
		
		String code = this.read();
		for(int k = 0; k < code.length() && 
				buffer.length() < max_length; k++) {
			char ch = code.charAt(k);
			if(ch == '\t' || ch == '\n') continue;
			else buffer.append(ch);
		}
		
		return buffer.toString();
	}
	/**
	 * get the trim code text without \t and \n
	 * @return
	 */
	public String trim_code() {
		return trim_code(this.read());
	}
	/**
	 * get the standard form of code string
	 * @param code
	 * @return
	 * @throws Exception
	 */
	public static String trim_code(String code) {
		buffer.setLength(0);
		for(int k = 0; k < code.length(); k++) {
			char ch = code.charAt(k);
			if(Character.isWhitespace(ch)) {
				for(int j = k; j < code.length(); j++) {
					ch = code.charAt(j);
					if(!Character.isWhitespace(ch)) {
						k = j - 1; break;
					}
				}
				buffer.append(' ');
			}
			else {
				buffer.append(ch);
			}
		}
		return buffer.toString();
	}
	
	/**
	 * get the line where the location belonging to.
	 * @return
	 * @throws Exception
	 */
	public int line_of() throws Exception {
		return this.text.line_of(this.bias);
	}
	
	public void set_location(int bias, int length) {
		if (bias < 0 || bias >= text.text.length())
			throw new IllegalArgumentException("Invalid bias: " + bias);
		else if (length <= 0 || bias + length > text.text.length())
			throw new IllegalArgumentException("Invalid length: " + length);
		else {
			this.bias = bias;
			this.length = length;
		}
	}

	@Override
	public String toString() {
		return "(" + bias + ", " + length + ")";
	}
}
