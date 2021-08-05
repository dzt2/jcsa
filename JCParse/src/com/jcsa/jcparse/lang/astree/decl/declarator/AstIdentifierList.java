package com.jcsa.jcparse.lang.astree.decl.declarator;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.base.AstPunctuator;

/**
 * <code>id_list -> id (, id)*</code>
 *
 * @author yukimula
 */
public interface AstIdentifierList extends AstNode {
	public int number_of_identifiers();

	public AstName get_identifier(int k);

	public AstPunctuator get_comma(int k);

	public void append_identifier(AstPunctuator comma, AstName name) throws Exception;
}
