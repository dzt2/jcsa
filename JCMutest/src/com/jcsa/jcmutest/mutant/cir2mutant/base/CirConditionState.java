package com.jcsa.jcmutest.mutant.cir2mutant.base;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * 	Conditional State
 * 	<br>
 * 	<code>
 * 	|--	CirConditionState	[class, execution] [stmt, stm_key]; [xxxxxx, xxxxxx]<br>
 * 	|--	|--	CirCoverTimesState	[cov_stmt]; [stmt, skey]; [must_need, int_times]<br>
 * 	|--	|--	CirConstraintState	[eva_expr]; [stmt, skey]; [condition, condition]<br>
 * 	|--	|--	CirSyMutationState	[ast_muta]; [stmt, skey]; [muta_id, mu_operator]<br>
 * 	</code>
 * 
 * @author dzt2
 *
 */
public abstract class CirConditionState extends CirAbstractState {

	protected CirConditionState(CirAbstractClass category, CirExecution execution, 
			SymbolExpression loperand, SymbolExpression roperand) throws Exception {
		super(category, execution, execution.get_statement(), 
				SymbolFactory.sym_expression(execution), loperand, roperand);
	}
	
}
