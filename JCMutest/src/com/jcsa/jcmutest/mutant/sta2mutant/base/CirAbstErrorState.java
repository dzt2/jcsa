package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcmutest.mutant.sta2mutant.utils.StateMutationUtils;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.parse.symbol.process.SymbolProcess;

/**
 * <code>
 * 	|--	CirPathErrorState		(execution, [stmt], (loperand, roperand))		<br>
 * 	|--	|--	CirBlockErrorState	(execution, [stmt], (orig_exec, muta_exec))		<br>
 * 	|--	|--	CirFlowsErrorState	(execution, [stmt], (orig_stmt, muta_stmt))		<br>
 * 	|--	|--	CirTrapsErrorState	(execution, [stmt], (execution, exception))		<br>
 * 	<br>
 * 	|--	CirDataErrorState		(execution, [expr|cond|dvar|vdef], (val1, val2))<br>
 * 	|--	|--	CirValueErrorState	(execution, [expr|cond|dvar|vdef], (val1, val2))<br>
 * 	|--	|--	CirIncreErrorState	(execution, [expr|dvar|vdef],	(base, differ))	<br>
 * 	|--	|--	CirBixorErrorState	(execution, [expr|dvar|vdef],	(base, differ))	<br>
 * </code>
 * 
 * @author yukimula
 *
 */
public abstract class CirAbstErrorState extends CirAbstractState {

	protected CirAbstErrorState(CirExecution point, CirStateStore store, CirStateValue value) throws Exception {
		super(point, store, value);
	}
	
	/**
	 * It normalizes this error-state under a given symbolic context
	 * @param context
	 * @return the normalized form of the error state under the context
	 * @throws Exception
	 */
	public CirAbstErrorState normalize(SymbolProcess context) throws Exception {
		return (CirAbstErrorState) StateMutationUtils.normalize(this, context);
	}
	/**
	 * It normalizes this error-state to a formal way
	 * @return the normalized form of the error state under the context
	 * @throws Exception 
	 */
	public CirAbstErrorState normalize() throws Exception {
		return (CirAbstErrorState) StateMutationUtils.normalize(this, null);
	}
	
}
