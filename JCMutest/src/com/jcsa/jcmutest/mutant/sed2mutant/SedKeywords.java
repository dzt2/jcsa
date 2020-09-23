package com.jcsa.jcmutest.mutant.sed2mutant;

/**
 * Keywords in state execution description language.
 * 
 * @author yukimula
 *
 */
public enum SedKeywords {
	
	/* constraint keywords */
	/** execute(stmt, int) **/					execute,
	/** asserts(stmt, expr) **/					asserts,
	/** conjunct(constraint+) **/				conjunct,
	/** disjunct(constraint+) **/				disjunct,
	
	/* state transition */
	/** goto_statement(stmt) **/				goto_statement,
	/** set_value(expr, expr) **/				set_expression,
	/** set_state(expr, expr) **/				set_reference,
	
}
