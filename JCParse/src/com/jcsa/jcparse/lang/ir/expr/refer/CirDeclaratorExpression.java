package com.jcsa.jcparse.lang.ir.expr.refer;

/**
 * declarator |-- {user_name}@{scope_id}
 * 
 * @author yukimula
 *
 */
public interface CirDeclaratorExpression extends CirNameExpression {
	
	/**
	 * @return name declared by the source code users
	 */
	public String get_user_name();
	
	/**
	 * @return id of the scope where the name is declared
	 */
	public int get_scope_id();
	
}
