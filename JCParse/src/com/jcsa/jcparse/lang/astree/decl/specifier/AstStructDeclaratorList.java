package com.jcsa.jcparse.lang.astree.decl.specifier;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.base.AstPunctuator;

/**
 * <code>StructDeclaratorList --> struct_declarator (, struct_declarator)*</code>
 *
 * @author yukimula
 */
public interface AstStructDeclaratorList extends AstNode {
	/**
	 * get the number of struct-declarators in the list
	 *
	 * @return
	 */
	public int number_of_declarators();

	/**
	 * get the kth struct declarator
	 *
	 * @param i
	 * @return
	 */
	public AstStructDeclarator get_declarator(int i);

	/**
	 * get the kth comma
	 *
	 * @param k
	 * @return
	 */
	public AstPunctuator get_comma(int k);

	/**
	 * add new struct-declarator in the tail of the list
	 *
	 * @param comma
	 * @param declarator
	 * @throws Exception
	 */
	public void append_declarator(AstPunctuator comma, AstStructDeclarator declarator) throws Exception;
}
