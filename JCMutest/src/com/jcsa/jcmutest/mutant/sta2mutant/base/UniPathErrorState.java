package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * 	<code>
 * 		|--	UniPathErrorState			[category, statement, l_operand, r_operand]	<br>
 * 		|--	|--	UniBlockErrorState		[set_stmt, statement, orig_exec, muta_exec]	<br>
 * 		|--	|--	UniFlowsErrorState		[set_flow, statement, orig_next, muta_next]	<br>
 * 		|--	|--	UniTrapsErrorState		[trp_stmt, statement, orig_exec, exception]	<br>
 * 	</code>
 * 	
 * 	@author yukimula
 *
 */
public abstract class UniPathErrorState extends UniAbstErrorState {

	protected UniPathErrorState(UniAbstractClass category, 
			CirStatement location, SymbolExpression loperand,
			SymbolExpression roperand) throws Exception {
		super(category, location, loperand, roperand);
	}

}
