package com.jcsa.jcparse.lang.astree.stmt;

import com.jcsa.jcparse.lang.astree.AstScopeNode;
import com.jcsa.jcparse.lang.astree.base.AstPunctuator;

/**
 * <code>comp_stmt --> { stmt_list? }</code>
 * 
 * @author yukimula
 *
 */
public interface AstCompoundStatement extends AstStatement, AstScopeNode {
	public AstPunctuator get_lbrace();

	public boolean has_statement_list();

	public AstStatementList get_statement_list();

	public AstPunctuator get_rbrace();
}
