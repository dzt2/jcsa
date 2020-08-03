package com.jcsa.jcparse.lang.ir.expr.refer;

import com.jcsa.jcparse.lang.astree.AstNode;

/**
 * The temporal variable generated from a AstNode
 * @author yukimula
 *
 */
public interface CirTemporalExpression extends CirNameExpression {
	
	/**
	 * @return the AST source that the expression represents
	 */
	public AstNode get_ast_source();
	
	/**
	 * @return the integer key of the ast-source
	 */
	public int get_temporal_key();
	
}
