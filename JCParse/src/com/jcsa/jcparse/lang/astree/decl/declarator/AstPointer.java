package com.jcsa.jcparse.lang.astree.decl.declarator;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.base.AstKeyword;
import com.jcsa.jcparse.lang.astree.base.AstPunctuator;

/**
 * <code>Pointer --> (<b>* | const | volatile | restrict (gnu|c99)</b>)+</code>
 *
 * @author yukimula
 */
public interface AstPointer extends AstNode {
	/**
	 * get the number of specifiers
	 *
	 * @return
	 */
	public int number_of_keywords();

	/**
	 * get the kth specifier in pointer
	 *
	 * @param k
	 * @return
	 */
	public AstNode get_specifier(int k);

	/**
	 * append <b>const | volatile | restrict</b>
	 *
	 * @param keyword
	 * @throws Exception
	 *             : invalid keywor
	 */
	public void append_keyword(AstKeyword keyword) throws Exception;

	/**
	 * append * in the tail of pointer
	 *
	 * @param punc
	 * @throws Exception
	 */
	public void append_punctuator(AstPunctuator punc) throws Exception;
}
