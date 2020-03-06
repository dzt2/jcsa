package com.jcsa.jcparse.lang.astree.stmt;

import com.jcsa.jcparse.lang.astree.AstNode;

/**
 * <code>stmt_list --> stmt+</code>
 * 
 * @author yukimula
 *
 */
public interface AstStatementList extends AstNode {
	public int number_of_statements();

	public AstStatement get_statement(int k);

	public void append_statement(AstStatement stmt) throws Exception;
}
