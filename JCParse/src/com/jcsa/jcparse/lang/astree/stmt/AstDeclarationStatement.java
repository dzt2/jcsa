package com.jcsa.jcparse.lang.astree.stmt;

import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.decl.AstDeclaration;
import com.jcsa.jcparse.lang.astree.unit.AstExternalUnit;

/**
 * <code>decl_stmt |--> declaration ;</code>
 *
 * @author yukimula
 *
 */
public interface AstDeclarationStatement extends AstStatement, AstExternalUnit {
	public AstDeclaration get_declaration();

	public AstPunctuator get_semicolon();
}
