package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * <code>
 * 	|--	CirPathErrorState		execution[stmt]		:=	{set_stmt|flow|trap}	<br>
 * 	|--	|--	CirBlockErrorState	[stmt:statement]	:=	{set_stmt:bool:bool}	<br>
 * 	|--	|--	CirFlowsErrorState	[stmt:statement]	:=	{set_flow:exec:exec}	<br>
 * 	|--	|--	CirTrapsErrorState	[stmt:statement]	:=	{set_trap:exec:expt}	<br>
 * </code>
 * 
 * @author yukimula
 *
 */
public abstract class CirPathErrorState extends CirAbstractState {

	protected CirPathErrorState(CirExecution execution, CirStateValue value) throws Exception {
		super(execution, CirStateStore.new_unit(execution.get_statement()), value);
	}
	
	/* universal getters */
	/**
	 * @return the original symbolic value to represent the path error state
	 */
	public SymbolExpression get_ovalue() { return this.get_value().get_lvalue(); }
	/**
	 * @return the mutation symbolic value to represent the path error state
	 */
	public SymbolExpression get_mvalue() { return this.get_value().get_rvalue(); }
	
}
