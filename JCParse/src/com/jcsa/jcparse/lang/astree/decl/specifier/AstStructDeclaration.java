package com.jcsa.jcparse.lang.astree.decl.specifier;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.base.AstPunctuator;

/**
 * <code>StructDecl --> SpecifierQualifierList StructDeclaratorList ;</code>
 *
 * @author yukimula
 */
public interface AstStructDeclaration extends AstNode {
	public AstSpecifierQualifierList get_specifiers();

	public AstStructDeclaratorList get_declarators();

	public AstPunctuator get_semicolon();
}
