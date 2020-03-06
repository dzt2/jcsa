package com.jcsa.jcparse.lang.astree.decl.declarator;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConstExpression;

/**
 * <code>dimension --> [ (array_qualifier_list)? (const_expr)? ]</code>
 * 
 * @author yukimula
 */
public interface AstDimension extends AstNode {
	public boolean has_expression();

	public boolean has_array_qualifier_list();

	public AstPunctuator get_lbracket();

	public AstArrayQualifierList get_array_qualifier_list();

	public AstConstExpression get_expression();

	public AstPunctuator get_rbracket();

}
