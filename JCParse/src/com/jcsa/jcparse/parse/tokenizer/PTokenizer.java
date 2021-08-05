package com.jcsa.jcparse.parse.tokenizer;

import com.jcsa.jcparse.lang.ptoken.PToken;
import com.jcsa.jcparse.lang.text.CStream;

/**
 * This is an automatic machine that consumes characters from CStream and
 * produce correct PToken as outputs
 *
 * @author yukimula
 */
public interface PTokenizer {
	/**
	 * open a next stream for reading characters of source code
	 *
	 * @param stream
	 */
	public void open(CStream stream);

	/**
	 * is this tokenizer openned?
	 *
	 * @return
	 */
	public boolean is_openned();

	/**
	 * get the next PToken from code characters;
	 *
	 * @return <b>null</b> when no more characters
	 * @throws Exception
	 *             when invalid character occurs or tokenizer is not openned
	 */
	public PToken tokenize() throws Exception;

	/**
	 * close the tokenizer
	 */
	public void close();
}
