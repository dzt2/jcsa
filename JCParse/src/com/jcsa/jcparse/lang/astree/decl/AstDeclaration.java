package com.jcsa.jcparse.lang.astree.decl;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstInitDeclaratorList;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstDeclarationSpecifiers;

/**
 * <code>declaration --> specifiers (init_declarator_list)?</code>
 * 
 * @author yukimula
 *
 */
public interface AstDeclaration extends AstNode {
	public AstDeclarationSpecifiers get_specifiers();

	public boolean has_declarator_list();

	public AstInitDeclaratorList get_declarator_list();
}
