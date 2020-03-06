package com.jcsa.jcparse.lang.astree.pline;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.lexical.CDirective;

/**
 * Node to represent directive in preprocessing line, including:<br>
 * <code>
 * <b> #if, #ifdef, #ifndef, #elif, #else, #endif, #define, #undef, #include, #line, #error, #pragma </b>
 * </code>
 * 
 * @author yukimula
 *
 */
public interface AstDirective extends AstNode {
	/**
	 * get the directive
	 * 
	 * @return
	 */
	public CDirective get_directive();
}
