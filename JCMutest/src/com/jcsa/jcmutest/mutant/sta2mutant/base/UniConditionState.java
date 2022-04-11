package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * 	<code>
 * 		UniAbstractState				[category, location,  l_operand, r_operand]	<br>
 * 		|--	UniConditionState			[category, statement, l_operand, r_operand]	<br>
 * 		|--	|--	UniCoverTimesState		[cov_time, statement, min_times, max_times]	<br>
 * 		|--	|--	UniConstraintState		[eva_cond, statement, condition, must_need]	<br>
 * 		|--	|--	UniSeedMutantState		[sed_muta, statement, mutant_ID, clas_oprt]	<br>
 * 	</code>
 * 	
 * 	@author yukimula
 *
 */
public abstract class UniConditionState extends UniAbstractState {
	
	protected UniConditionState(UniAbstractClass category, 
			CirStatement location, SymbolExpression loperand,
			SymbolExpression roperand) throws Exception {
		super(category, location, loperand, roperand);
	}
	
}
