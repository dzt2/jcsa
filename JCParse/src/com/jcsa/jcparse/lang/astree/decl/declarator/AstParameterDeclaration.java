package com.jcsa.jcparse.lang.astree.decl.declarator;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstDeclarationSpecifiers;

/**
 * <code>param_decl --> decl_specifiers (declarator|abs_declarator)?</code>
 * 
 * @author yukimula
 */
public interface AstParameterDeclaration extends AstNode {
	public AstDeclarationSpecifiers get_specifiers();

	public AstDeclarator get_declarator();

	public AstAbsDeclarator get_abs_declarator();

	public boolean has_declarator();

	public boolean has_abs_declarator();
}
