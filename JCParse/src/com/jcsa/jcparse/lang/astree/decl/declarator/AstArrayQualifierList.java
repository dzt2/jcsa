package com.jcsa.jcparse.lang.astree.decl.declarator;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.base.AstKeyword;

/**
 * <code>array_qualifier_list --> (<b>static | const | volatile | restrict</b>)+</code>
 *
 * @author yukimula
 */
public interface AstArrayQualifierList extends AstNode {
	public int number_of_keywords();

	public AstKeyword get_keyword(int k);

	public void append_keyword(AstKeyword keyword) throws Exception;
}
