package com.jcsa.jcmutest.mutant.cir2mutant.base;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * <code>
 * 	|--	CirPathErrorState	[class, execution] [stmt, stm_key]; [xxxxxx, xxxxxx]<br>
 * 	|--	|--	CirBlockErrorState 	[mut_stmt];	[stmt, skey]; [orig_exec, muta_exec]<br>
 * 	|--	|--	CirFlowsErrorState	[mut_flow]; [stmt, skey]; [orig_trgt, muta_trgt]<br>
 * 	|--	|--	CirTrapsErrorState	[trp_stmt]; [stmt, skey]; [exception, exception]<br>
 * </code>
 * 
 * @author yukimula
 *
 */
public abstract class CirPathErrorState extends CirAbstErrorState {

	protected CirPathErrorState(CirAbstractClass category, CirExecution execution, 
			SymbolExpression loperand, SymbolExpression roperand) throws Exception {
		super(category, execution, execution.get_statement(), 
				SymbolFactory.sym_expression(execution), loperand, roperand);
	}

}
