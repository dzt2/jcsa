package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;

/**
 * <code>
 * 	|--	CirPathErrorState		(execution, [stmt], (loperand, roperand))		<br>
 * 	|--	|--	CirBlockErrorState	(execution, [stmt], (orig_exec, muta_exec))		<br>
 * 	|--	|--	CirFlowsErrorState	(execution, [stmt], (orig_stmt, muta_stmt))		<br>
 * 	|--	|--	CirTrapsErrorState	(execution, [stmt], (execution, exception))		<br>
 * </code>
 * 
 * @author yukimula
 *
 */
public abstract class CirPathErrorState extends CirAbstErrorState {

	protected CirPathErrorState(CirExecution point, CirStateValue value) throws Exception {
		super(point, CirStateStore.new_unit(point.get_statement()), value);
	}
	
	/**
	 * @return the source execution point where the path error occurs
	 */
	public CirExecution get_source_execution() { return this.get_clocation().execution_of(); }
	
}
