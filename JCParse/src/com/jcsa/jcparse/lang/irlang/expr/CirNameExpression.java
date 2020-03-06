package com.jcsa.jcparse.lang.irlang.expr;

/**
 * name_expression	|-- identifier
 * 					|-- implicator
 * 					|-- return_pnt
 * 					|-- declarator
 * @author yukimula
 *
 */
public interface CirNameExpression extends CirReferExpression {
	/**
	 * get the simple name of the expression (without scope information)
	 * @return
	 */
	public String get_name();
	/**
	 * get the unique name of the expression based on following rules:<br>
	 * 	1. identifier 	|-- name#scope.hashCode()<br>
	 * 	2. declarator	|--	name#scope.hashCode()<br>
	 * 	3. implicator	|-- name<br>
	 * 	4. return_ptr	|-- return#function.hashCode()<br>
	 * @return
	 */
	public String get_unique_name();
}
