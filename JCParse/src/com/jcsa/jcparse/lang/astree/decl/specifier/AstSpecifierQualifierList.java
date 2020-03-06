package com.jcsa.jcparse.lang.astree.decl.specifier;

import com.jcsa.jcparse.lang.astree.AstNode;

/**
 * <code>SpecQualifierList --> (type_qualifier | type_keyword | struct_specifier | union_specifier | enum_specifier | typedef_name)+</code>
 * 
 * @author yukimula
 */
public interface AstSpecifierQualifierList extends AstNode {
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
	 *             : null or invalid qualifier-specifier
	 */
	public void append_specifier(AstSpecifier spec) throws Exception;
}
