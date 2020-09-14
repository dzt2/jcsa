package com.jcsa.jcmutest.mutant.sec2mutant;

/**
 * It defines the keywords for symbolic error and constraint language (SEC).
 * 
 * @author yukimula
 *
 */
public enum SecKeywords {
	
	/* constraint keywords */
	/** execute(stmt, int) **/					execute,
	/** asserts(stmt, expr) **/					asserts,
	/** conjunct(constraint+) **/				conjunct,
	/** disjunct(constraint+) **/				disjunct,
	
	/* statement error keywords */
	/** add_statement(stmt) **/					add_statement,
	/** del_statement(stmt) **/					del_statement,
	/** set_statement(stmt, stmt) **/			set_statement,
	
	/* expression error keywords */
	/** set_expression(expr, expr) **/			set_expression,
	/** add_expression(expr, oprt, expr) **/	add_expression,
	/** ins_expression(expr, oprt, expr) **/	ins_expression,
	/** uny_expression(expr, oprt) **/			uny_expression,
	
	/* reference error keywords */
	/** set_expression(expr, expr) **/			set_reference,
	/** add_expression(expr, oprt, expr) **/	add_reference,
	/** ins_expression(expr, oprt, expr) **/	ins_reference,
	/** uny_expression(expr, oprt) **/			uny_reference,
	
	/* unique error keywords */
	/** trap() **/								trap,
	/** none() **/								none,
	
}
