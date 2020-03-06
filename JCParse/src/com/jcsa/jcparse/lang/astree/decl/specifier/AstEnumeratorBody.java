package com.jcsa.jcparse.lang.astree.decl.specifier;

import com.jcsa.jcparse.lang.astree.AstScopeNode;
import com.jcsa.jcparse.lang.astree.base.AstPunctuator;

/**
 * <code>EnumeratorBody -> { EnumeratorList (,)? }</code>
 * 
 * @author yukimula
 */
public interface AstEnumeratorBody extends AstScopeNode {
	/**
	 * {
	 * 
	 * @return
	 */
	public AstPunctuator get_lbrace();

	/**
	 * get the enumerator list
	 * 
	 * @return
	 */
	public AstEnumeratorList get_enumerator_list();

	/**
	 * whether there is tail comma
	 * 
	 * @return
	 */
	public boolean has_comma();

	/**
	 * get the tail comma
	 * 
	 * @return : null when no such comma defined
	 */
	public AstPunctuator get_comma();

	/**
	 * get }
	 * 
	 * @return
	 */
	public AstPunctuator get_rbrace();
}
