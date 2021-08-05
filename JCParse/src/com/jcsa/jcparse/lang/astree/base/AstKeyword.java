package com.jcsa.jcparse.lang.astree.base;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.lexical.CKeyword;

/**
 * Keyword node
 *
 * @author yukimula
 *
 */
public interface AstKeyword extends AstNode {
	/**
	 * get the keyword tag
	 *
	 * @return
	 */
	public CKeyword get_keyword();
}
