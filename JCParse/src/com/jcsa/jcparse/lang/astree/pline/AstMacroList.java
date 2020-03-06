package com.jcsa.jcparse.lang.astree.pline;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstIdentifierList;

/**
 * <code>( identifier_list? | ... )</code>
 * 
 * @author yukimula
 *
 */
public interface AstMacroList extends AstNode {
	public boolean has_identifiers();

	public boolean has_ellipsis();

	public AstPunctuator get_lparanth();

	public AstIdentifierList get_identifiers();

	public AstPunctuator get_ellipsis();

	public AstPunctuator get_rparanth();
}
