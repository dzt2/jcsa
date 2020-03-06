package com.jcsa.jcparse.lang.astree.decl.declarator;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializer;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstOperator;

/**
 * <code>init_declarator --> declarator (= initializer)?</code>
 * 
 * @author yukimula
 */
public interface AstInitDeclarator extends AstNode {
	public AstDeclarator get_declarator();

	public AstOperator get_assign();

	public AstInitializer get_initializer();

	public boolean has_initializer();
}
