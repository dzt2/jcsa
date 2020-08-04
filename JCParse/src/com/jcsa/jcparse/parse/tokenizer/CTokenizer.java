package com.jcsa.jcparse.parse.tokenizer;

import com.jcsa.jcparse.lang.ctoken.CToken;
import com.jcsa.jcparse.lang.text.CText;

/**
 * To produce CToken based on characters from CStream
 * 
 * @author yukimula
 */
public interface CTokenizer {
	/**
	 * open the stream for text for tokenization
	 * 
	 * @param stream
	 */
	public void open(CText text);

	/**
	 * close the tokenizer, then <code>tokenize</code> is no more accessible
	 */
	public void close();

	/**
	 * whether the tokenizer is openned
	 * 
	 * @return
	 */
	public boolean is_openned();

	/**
	 * get the text openned by tokenizer
	 * 
	 * @return
	 */
	public CText get_source();

	/**
	 * To parse the string in text to produce the next CToken
	 * 
	 * @return : null when no more character is remained in stream
	 * @throws Exception
	 *             : tokenizer is not openned, or parsing fails
	 */
	public CToken tokenize() throws Exception;

	/**
	 * get stream to access tokens from the tokenizer
	 * 
	 * @return
	 */
	public CTokenStream get_stream() throws Exception;
}
