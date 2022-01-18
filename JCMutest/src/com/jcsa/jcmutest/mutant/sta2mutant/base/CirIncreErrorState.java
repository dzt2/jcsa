package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcmutest.mutant.sta2mutant.StateMutations;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.parse.symbol.process.SymbolProcess;

/**
 * [expr|dvar|vdef]	<== inc_expr(base, difference)
 * 
 * @author yukimula
 *
 */
public class CirIncreErrorState extends CirDataErrorState {

	protected CirIncreErrorState(CirExecution point, CirStateStore store, 
			SymbolExpression base_value, SymbolExpression difference) throws Exception {
		super(point, store, CirStateValue.inc_expr(base_value, difference));
	}
	
	/**
	 * @return the original value being incremented
	 */
	public SymbolExpression get_base_value() { return this.get_loperand(); }
	/**
	 * @return the value incremented to original one
	 */
	public SymbolExpression get_difference() { return this.get_roperand(); }

	@Override
	public CirAbstErrorState normalize(SymbolProcess context) throws Exception {
		SymbolExpression difference = this.get_difference();
		difference = StateMutations.evaluate(difference, context);
		if(StateMutations.is_trap_value(difference)) {
			return CirAbstractState.set_trap(this.get_execution());
		}
		else if(this.get_store_type() == CirStoreClass.vdef) {
			return CirAbstractState.inc_vdef(this.get_expression(), this.get_store_key(), difference);
		}
		else {
			return CirAbstractState.inc_expr(this.get_expression(), difference);
		}
	}

	@Override
	public Boolean validate(SymbolProcess context) throws Exception {
		SymbolExpression difference = this.get_difference();
		difference = StateMutations.evaluate(difference, context);
		if(StateMutations.is_trap_value(difference)) {
			return Boolean.TRUE;
		}
		else if(difference instanceof SymbolConstant) {
			return ((SymbolConstant) difference).get_double() != 0;
		}
		else {
			return null;
		}
	}
	
}
