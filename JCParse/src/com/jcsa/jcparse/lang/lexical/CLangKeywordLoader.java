package com.jcsa.jcparse.lang.lexical;

/**
 * to load the keyword and binds them with specific name in specified C language
 * standard
 * 
 * @author yukimula
 */
public interface CLangKeywordLoader {
	/**
	 * bind the names defined in this standard with specific keyword
	 * 
	 * @param lib
	 * @throws Exception
	 */
	public void load(CLangKeywordLib lib) throws Exception;
}
