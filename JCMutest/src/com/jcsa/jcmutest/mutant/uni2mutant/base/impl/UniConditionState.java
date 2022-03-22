package com.jcsa.jcmutest.mutant.uni2mutant.base.impl;

import com.jcsa.jcmutest.mutant.uni2mutant.base.UniAbstractClass;
import com.jcsa.jcmutest.mutant.uni2mutant.base.UniAbstractState;
import com.jcsa.jcmutest.mutant.uni2mutant.base.UniAbstractStore;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * 	It represents the state for evaluating logical condition.
 * 	<br>
 * 	<code>
 * 	UniConditionalState			class(statement; lvalue, rvalue)			<br>
 * 	|--	UniCoverTimesState		cov_time(statement; min_times, max_times)	<br>
 * 	|--	UniConstraintState		eva_bool(statement; condition, must_need)	<br>
 * 	|--	UniSeedMutantState		sed_muta(statement; mutant_ID, clas_oprt)	<br>
 * 	</code>
 * 	
 * 	@author yukimula
 *
 */
public abstract class UniConditionState extends UniAbstractState {

	protected UniConditionState(UniAbstractClass state_class, UniAbstractStore state_store, 
			SymbolExpression loperand, SymbolExpression roperand) throws Exception {
		super(state_class, state_store, loperand, roperand);
		if(state_store.is_statement() || state_store.is_goto_label()) { /* valid */ }
		else {
			throw new IllegalArgumentException("Invalid state_store: " + state_store);
		}
	}
	
	/**
	 * @return the statement where the condition is evaluated in the state location
	 */
	public CirStatement get_statement() { 
		return (CirStatement) this.get_store().get_cir_location(); 
	}
	
}
