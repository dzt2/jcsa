package com.jcsa.jcmutest.mutant.uni2mutant.base;

import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * 	<code>
 * 	UniAbstractState				[st_class, c_loct; lsh_value, rsh_value]	<br>
 * 	|--	UniConditionState			[st_class, c_stmt; lsh_value, rsh_value]	<br>
 * 	|--	|--	UniCoverTimesState		[cov_time, c_stmt; min_times, max_times]	<br>
 * 	|--	|--	UniConstraintState		[eva_cond, c_stmt; condition, must_need]	<br>
 * 	|--	|--	UniSeedMutantState		[sed_muta, c_stmt; mutant_ID, clas_oprt]	<br>
 * 	</code>
 * 	
 * 	@author yukimula
 *
 */
public abstract class UniConditionState extends UniAbstractState {

	protected UniConditionState(UniAbstractClass _class, UniAbstractStore _store, 
			SymbolExpression lvalue, SymbolExpression rvalue) throws Exception {
		super(_class, _store, lvalue, rvalue);
		if(!_store.is_stmt()) {
			throw new IllegalArgumentException("Statement-Store required.");
		}
	}
	
	/**
	 * @return the statement where the condition is evaluated as a state
	 */
	public CirStatement get_statement() { return (CirStatement) this.get_state_store().get_cir_location(); }

}
