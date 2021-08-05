package com.jcsa.jcparse.lang.astree.decl.specifier;

import com.jcsa.jcparse.lang.astree.AstNode;

/**
 * <code>DeclSpecs --> (specifier)+</code>
 *
 * @author yukimula
 *
 */
public interface AstDeclarationSpecifiers extends AstNode {
	/**
	 * get the number of specifiers in the list
	 *
	 * @return
	 */
	public int number_of_specifiers();

	/**
	 * get the kth specifier in the list
	 *
	 * @param k
	 * @return : null when out of index
	 */
	public AstSpecifier get_specifier(int k);

	/**
	 * add another specifier in the list
	 *
	 * @param spec
	 * @throws Exception
	 *             : null or invalid specifier
	 */
	public void append_specifier(AstSpecifier spec) throws Exception;
}
