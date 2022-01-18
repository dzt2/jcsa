package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcmutest.mutant.sta2mutant.StateMutations;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.parse.symbol.process.SymbolProcess;

public class CirNConstrainState extends CirConditionState {

	protected CirNConstrainState(CirExecution point, SymbolExpression condition) throws Exception {
		super(point, CirStateValue.cov_cond(true, condition));
	}
	
	/**
	 * @return the constraint being statisfied at this point
	 */
	public SymbolExpression get_condition() { return this.get_roperand(); }

	@Override
	public CirConditionState normalize(SymbolProcess context) throws Exception {
		SymbolExpression condition = this.get_condition();
		condition = StateMutations.evaluate(condition, context);
		if(StateMutations.is_trap_value(condition)) {
			condition = SymbolFactory.sym_constant(Boolean.TRUE);
		}
		CirExecution execution = this.find_previous_checkpoint(condition);
		return CirAbstractState.eva_cond(execution, condition, true);
	}

	@Override
	public Boolean validate(SymbolProcess context) throws Exception {
		SymbolExpression condition = this.get_condition();
		condition = StateMutations.evaluate(condition, context);
		if(StateMutations.is_trap_value(condition)) {
			return Boolean.TRUE;
		}
		else if(condition instanceof SymbolConstant) {
			return ((SymbolConstant) condition).get_bool();
		}
		else {
			return null;
		}
	}
	
}
