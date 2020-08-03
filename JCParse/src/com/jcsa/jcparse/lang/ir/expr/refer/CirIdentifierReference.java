package com.jcsa.jcparse.lang.ir.expr.refer;

/**
 * {user_name}@{scope_identifier}
 * @author yukimula
 *
 */
public interface CirIdentifierReference extends CirNameExpression {
	
	/**
	 * @return the user name that the declarator defines
	 */
	public String get_user_name();
	
	/**
	 * @return the integer that the declarator refers to
	 */
	public int get_scope_id();
	
}
