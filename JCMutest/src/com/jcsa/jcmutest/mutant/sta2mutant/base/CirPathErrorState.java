package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;


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
public abstract class CirPathErrorState extends CirAbstErrorState {

	protected CirPathErrorState(CirExecution execution, CirStateValue value) throws Exception {
		super(execution, CirStateStore.new_unit(execution.get_statement()), value);
	}
	
}
