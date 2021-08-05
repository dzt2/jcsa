package com.jcsa.jcparse.lang.astree.decl.specifier;

import com.jcsa.jcparse.lang.astree.AstScopeNode;
import com.jcsa.jcparse.lang.astree.base.AstPunctuator;

/**
 * <code>{ struct_decl_list? }</code>
 *
 * @author yukimula
 */
public interface AstStructUnionBody extends AstScopeNode {
	/**
	 * {
	 *
	 * @return
	 */
	public AstPunctuator get_lbrace();

	/**
	 * whether there is struct-declaration in the body
	 *
	 * @return
	 */
	public boolean has_declaration_list();

	/**
	 * get the struct-declaration-list
	 *
	 * @return : null when no declaration exists in the body
	 */
	public AstStructDeclarationList get_declaration_list();

	/**
	 * }
	 *
	 * @return
	 */
	public AstPunctuator get_rbrace();
}
