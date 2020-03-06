package com.jcsa.jcparse.lang.astree.decl.specifier;

import com.jcsa.jcparse.lang.astree.AstNode;

/**
 * <code>StructDeclList --> (StructDecl)+</code>
 * 
 * @author yukimula
 *
 */
public interface AstStructDeclarationList extends AstNode {
	public int number_of_declarations();

	public AstStructDeclaration get_declaration(int k);

	public void append_declaration(AstStructDeclaration decl) throws Exception;
}
