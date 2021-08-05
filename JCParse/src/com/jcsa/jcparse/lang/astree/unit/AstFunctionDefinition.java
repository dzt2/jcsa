package com.jcsa.jcparse.lang.astree.unit;

import com.jcsa.jcparse.lang.astree.AstScopeNode;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstDeclarationSpecifiers;
import com.jcsa.jcparse.lang.astree.stmt.AstCompoundStatement;

/**
 * <code>func_def --> decl_specifiers declarator (decl_list)? comp_stmt</code>
 *
 * @author yukimula
 */
public interface AstFunctionDefinition extends AstExternalUnit, AstScopeNode {
	public AstDeclarationSpecifiers get_specifiers();

	public AstDeclarator get_declarator();

	public boolean has_declaration_list();

	public AstDeclarationList get_declaration_list();

	public AstCompoundStatement get_body();
}
