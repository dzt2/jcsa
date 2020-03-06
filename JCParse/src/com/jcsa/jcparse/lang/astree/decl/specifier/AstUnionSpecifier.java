package com.jcsa.jcparse.lang.astree.decl.specifier;

import com.jcsa.jcparse.lang.astree.base.AstKeyword;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstName;

/**
 * <code>StructSpec --> <b>union</b> Name | (Name)? StructUnionBody</code>
 * 
 * @author yukimula
 *
 */
public interface AstUnionSpecifier extends AstSpecifier {
	/**
	 * get <b>union</b>
	 * 
	 * @return
	 */
	public AstKeyword get_union();

	/**
	 * whether there is name in specifier
	 * 
	 * @return
	 */
	public boolean has_name();

	/**
	 * get the name of specifier
	 * 
	 * @return : null when no name is defined
	 */
	public AstName get_name();

	/**
	 * whether there is body to define the union type
	 * 
	 * @return
	 */
	public boolean has_body();

	/**
	 * get the body
	 * 
	 * @return : null when no body is defined
	 */
	public AstStructUnionBody get_body();
}
