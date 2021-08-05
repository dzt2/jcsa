package com.jcsa.jcparse.lang.astree;

import com.jcsa.jcparse.lang.scope.CScope;

/**
 * AST-node that refers to some scope, could be:<br>
 * <code>
 * 	AstTranslationUnit, AstEnumeratorBody, AstParameterBody, AstStructUnionBody,
 * </code>
 *
 * @author yukimula
 */
public interface AstScopeNode extends AstNode {
	/**
	 * get the scope of this node
	 *
	 * @return
	 */
	public CScope get_scope();

	/**
	 * set the scope for this node
	 *
	 * @param scope
	 */
	public void set_scope(CScope scope);
}
