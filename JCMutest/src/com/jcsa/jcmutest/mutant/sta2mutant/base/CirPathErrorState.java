package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * 	<code>
 * 	CirAbstractState			st_class(storage_l; l_operand, r_operand)		<br>
 * 	|--	CirPathErrorState		st_class(statement; l_operand, r_operand)		<br>
 * 	|--	|--	CirBlockErrorState	mut_stmt(statement; orig_exec, muta_exec)		<br>
 * 	|--	|--	CirFlowsErrorState	mut_flow(statement; orig_next, muta_next)		<br>
 * 	|--	|--	CirTrapsErrorState	mut_trap(statement; exception, exception)		<br>
 * 	</code>
 * 	
 * 	@author yukimula
 *
 */
public abstract class CirPathErrorState extends CirAbstErrorState {

	protected CirPathErrorState(CirAbstractClass category, CirAbstractStore location, SymbolExpression loperand,
			SymbolExpression roperand) throws Exception {
		super(category, location, loperand, roperand);
		if(!location.is_statement()) {
			throw new IllegalArgumentException("Invalid location: " + location);
		}
	}

}
