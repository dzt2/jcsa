package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcmutest.mutant.sta2mutant.StateMutations;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.parse.symbol.process.SymbolProcess;

/**
 * [cond|expr|dvar|vdef]	<==		set_expr(ovalue, mvalue)
 * 
 * @author yukimula
 *
 */
public class CirValueErrorState extends CirDataErrorState {

	protected CirValueErrorState(CirExecution point, CirStateStore store, 
			SymbolExpression orig_value, SymbolExpression muta_value) throws Exception {
		super(point, store, CirStateValue.set_expr(orig_value, muta_value));
	}
	
	/**
	 * @return the original value before the expression is mutated
	 */
	public SymbolExpression get_orig_value() { return this.get_loperand(); }
	
	/**
	 * @return the mutation value after this expression is mutated
	 */
	public SymbolExpression get_muta_value() { return this.get_roperand(); }

	@Override
	public CirAbstErrorState normalize(SymbolProcess context) throws Exception {
		SymbolExpression muta_value = this.get_muta_value();
		muta_value = StateMutations.evaluate(muta_value, context);
		if(StateMutations.is_trap_value(muta_value)) {
			return CirAbstractState.set_trap(this.get_execution());
		}
		else if(this.get_store_type() == CirStoreClass.vdef) {
			return CirAbstractState.set_vdef(this.get_expression(), this.get_store_key(), muta_value);
		}
		else {
			return CirAbstractState.set_expr(this.get_expression(), muta_value);
		}
	}

	@Override
	public Boolean validate(SymbolProcess context) throws Exception {
		SymbolExpression orig_value = this.get_orig_value();
		SymbolExpression muta_value = this.get_muta_value();
		orig_value = StateMutations.evaluate(orig_value, context);
		muta_value = StateMutations.evaluate(muta_value, context);
		
		if(StateMutations.is_trap_value(muta_value)) {
			return Boolean.TRUE;
		}
		else if(muta_value.equals(orig_value)) {
			return Boolean.FALSE;
		}
		else {
			return null;
		}
	}
	
}
