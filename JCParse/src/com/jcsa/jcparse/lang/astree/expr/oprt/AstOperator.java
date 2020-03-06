package com.jcsa.jcparse.lang.astree.expr.oprt;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * <code>operator |--> {+, -, *, /, %, +=, -=, *=, /=, %=, >>, >>=, <<, <<=, >=
 * , >, <, <=, ==, !=,
 * 						++, --, &, |, ^, &&, ||, !, ~}</code>
 * 
 * @author yukimula
 */
public interface AstOperator extends AstNode {
	/**
	 * get the operator in expression
	 * 
	 * @return
	 */
	public COperator get_operator();
}
