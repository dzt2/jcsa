package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * 	<code>
 * 		UniAbstractState				[category, location,  l_operand, r_operand]	<br>
 * 		|--	UniPathErrorState			[category, statement, l_operand, r_operand]	<br>
 * 		|--	|--	UniBlockErrorState		[set_stmt, statement, orig_exec, muta_exec]	<br>
 * 		|--	|--	UniFlowsErrorState		[set_flow, statement, orig_next, muta_next]	<br>
 * 		|--	|--	UniTrapsErrorState		[trp_stmt, statement, orig_exec, exception]	<br>
 * 		|--	UniDataErrorState			[category, location,  l_operand, r_operand]	<br>
 * 		|--	|--	UniValueErrorState		[set_expr, expr|stmt, orig_expr, muta_expr]	<br>
 * 		|--	|--	UniIncreErrorState		[inc_expr, expr|stmt, orig_expr, different]	<br>
 * 		|--	|--	UniBixorErrorState		[xor_expr, expr|stmt, orig_expr, different]	<br>
 * 	</code>
 * 	
 *	@author yukimula
 *
 */
public abstract class UniAbstErrorState extends UniAbstractState {

	protected UniAbstErrorState(UniAbstractClass category, 
			CirNode location, SymbolExpression loperand,
			SymbolExpression roperand) throws Exception {
		super(category, location, loperand, roperand);
	}

}
