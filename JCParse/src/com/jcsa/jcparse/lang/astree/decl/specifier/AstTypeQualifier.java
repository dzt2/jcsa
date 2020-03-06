package com.jcsa.jcparse.lang.astree.decl.specifier;

import com.jcsa.jcparse.lang.astree.base.AstKeyword;

/**
 * <code>TypeQualifier |--> <b>const | volatile | restrict</b>(gnu|c99)</code>
 * 
 * @author yukimula
 */
public interface AstTypeQualifier extends AstSpecifier {
	/**
	 * get keyword of the type qualifier
	 * 
	 * @return
	 */
	public AstKeyword get_keyword();
}
