package com.jcsa.jcparse.lang.astree.decl.specifier;

import com.jcsa.jcparse.lang.astree.base.AstKeyword;

/**
 * <code>FuncSpec (c99|gnu) --> <b>inline</b></code>
 *
 * @author yukimula
 */
public interface AstFunctionQualifier extends AstSpecifier {
	/**
	 * get keyword of the function qualifier
	 *
	 * @return
	 */
	public AstKeyword get_keyword();
}
