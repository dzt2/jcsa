package com.jcsa.jcparse.lang.astree.decl;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstAbsDeclarator;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstSpecifierQualifierList;
import com.jcsa.jcparse.lang.ctype.CType;

/**
 * <code>spec_qualifier_list (abs_declarator)?</code>
 *
 * @author yukimula
 */
public interface AstTypeName extends AstNode {
	public AstSpecifierQualifierList get_specifiers();

	public AstAbsDeclarator get_declarator();

	public boolean has_declarator();

	public CType get_type();

	public void set_type(CType type);
}
