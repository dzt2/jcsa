package com.jcsa.jcparse.lang.astree.base;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.lexical.CPunctuator;

/**
 * Node to represent punctuator in code
 * 
 * @author yukimula
 *
 */
public interface AstPunctuator extends AstNode {
	/**
	 * get the punctuator it represents
	 * 
	 * @return
	 */
	public CPunctuator get_punctuator();
}
