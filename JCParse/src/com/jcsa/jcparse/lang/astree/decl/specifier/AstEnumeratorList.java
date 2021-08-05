package com.jcsa.jcparse.lang.astree.decl.specifier;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.base.AstPunctuator;

/**
 * <code>EnumList --> Enumerator (, Enumerator)*</code>
 *
 * @author yukimula
 */
public interface AstEnumeratorList extends AstNode {
	/**
	 * get the number of enumerators in the list
	 *
	 * @return
	 */
	public int number_of_enumerators();

	/**
	 * get the kth enumerator
	 *
	 * @param k
	 * @return
	 */
	public AstEnumerator get_enumerator(int k);

	/**
	 * get the kth comma punctuator
	 *
	 * @param k
	 * @return
	 */
	public AstPunctuator get_comma(int k);

	/**
	 * add new enumerator in the tail of this list
	 *
	 * @param comma
	 * @param enumerator
	 * @throws Exception
	 */
	public void append_enumerator(AstPunctuator comma, AstEnumerator enumerator) throws Exception;
}
