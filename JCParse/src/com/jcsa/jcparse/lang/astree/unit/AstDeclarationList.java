package com.jcsa.jcparse.lang.astree.unit;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstDeclarationStatement;

/**
 * <code>decl_list --> decl_stmt+</code>
 * 
 * @author yukimula
 *
 */
public interface AstDeclarationList extends AstNode {
	public int number_of_declarations();

	public AstDeclarationStatement get_declaration(int k);

	public void append_declaration(AstDeclarationStatement decl) throws Exception;
}
