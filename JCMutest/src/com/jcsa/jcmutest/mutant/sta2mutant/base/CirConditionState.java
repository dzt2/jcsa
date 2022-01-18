package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.parse.symbol.process.SymbolProcess;

/**
 * <code>
 * 	|--	CirConditionState		(execution, [stmt], (bool, condition))			<br>
 * 	|--	|--	CirLimitTimesState	(execution, [stmt], (true, int_times))			<br>
 * 	|--	|--	CirReachTimesState	(execution, [stmt], (false, int_times))			<br>
 * 	|--	|--	CirTConstrainState	(execution, [stmt], (true, condition))			<br>
 * 	|--	|--	CirFConstrainState	(execution, [stmt], (false, condition))			<br>
 * </code>
 * 
 * @author yukimula
 *
 */
public abstract class CirConditionState extends CirAbstractState {

	protected CirConditionState(CirExecution point, CirStateValue value) throws Exception {
		super(point, CirStateStore.new_unit(point.get_statement()), value);
	}
	
	/* common getters */
	/**
	 * @return the execution point where the condition should be evaluated
	 */
	public CirExecution get_evaluated_point() { return this.get_clocation().execution_of(); }
	/**
	 * @return the first operand as the boolean of the conditioned states
	 */
	public boolean		get_option() { return ((SymbolConstant) this.get_loperand()).get_bool(); }
	
	/**
	 * It translates the abstract error state to a normalized form
	 * @param context
	 * @return the normalized form of the conditioned state
	 * @throws Exception
	 */
	public abstract CirConditionState normalize(SymbolProcess context) throws Exception;
	
	/**
	 * @return the normalized form of the conditioned state
	 * @throws Exception
	 */
	public CirConditionState normalize() throws Exception { return this.normalize(null); }
	
}
