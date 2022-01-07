package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;

/**
 * <code>
 * 	|--	CirDataErrorState		execution[expr]		:=	{set|inc|xor_expr}		<br>
 * 	|--	|--	CirValueErrorState	[usep|defp|vdef]	:=	{set_expr:orig:muta}	<br>
 * 	|--	|--	CirIncreErrorState	[usep|defp|vdef]	:=	{inc_expr:base:diff}	<br>
 * 	|--	|--	CirBixorErrorState	[usep|defp|vdef]	:=	{xor_expr:base:diff}	<br>
 * </code>
 * 
 * @author yukimula
 *
 */
public abstract class CirDataErrorState extends CirAbstErrorState {
	
	protected CirDataErrorState(CirExecution execution, CirStateStore store, CirStateValue value) throws Exception {
		super(execution, store, value);
	}
	
	/**
	 * @return the expression where this data state error is introduced
	 */
	public CirExpression get_expression() { return (CirExpression) this.get_store().get_unit(); }
}
