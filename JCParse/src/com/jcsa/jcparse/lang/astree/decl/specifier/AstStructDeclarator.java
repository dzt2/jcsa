package com.jcsa.jcparse.lang.astree.decl.specifier;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConstExpression;

/**
 * <code>StructDeclarator --> declarator | (declarator)? : const_expr</code>
 * 
 * @author yukimula
 */
public interface AstStructDeclarator extends AstNode {
	/**
	 * whether declarator defined in struct-declarator
	 * 
	 * @return
	 */
	public boolean has_declarator();

	/**
	 * whether expression defined in struct-declarator
	 * 
	 * @return
	 */
	public boolean has_expression();

	/**
	 * get declarator
	 * 
	 * @return
	 */
	public AstDeclarator get_declarator();

	/**
	 * get :
	 * 
	 * @return
	 */
	public AstPunctuator get_colon();

	/**
	 * get the const-expression
	 * 
	 * @return
	 */
	public AstConstExpression get_expression();
}
