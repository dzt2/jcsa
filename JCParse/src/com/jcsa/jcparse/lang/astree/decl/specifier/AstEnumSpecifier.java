package com.jcsa.jcparse.lang.astree.decl.specifier;

import com.jcsa.jcparse.lang.astree.base.AstKeyword;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstName;

/**
 * <code>EnumSpec --> <b>enum</b> Name | (Name)? EnumeratorBody</code>
 * 
 * @author yukimula
 *
 */
public interface AstEnumSpecifier extends AstSpecifier {
	/**
	 * get <b>enum</b>
	 * 
	 * @return
	 */
	public AstKeyword get_enum();

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
	 * whether there is body to define the enum type
	 * 
	 * @return
	 */
	public boolean has_body();

	/**
	 * get the body
	 * 
	 * @return : null when no body is defined
	 */
	public AstEnumeratorBody get_body();
}
