package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * cov_stmt(execution, {stmt:statement}, {TRUE});
 * 
 * @author yukimula
 *
 */
public class CirCheckPointState extends CirConditionState {
	
	protected CirCheckPointState(CirExecution execution) throws Exception {
		super(CirStateClass.cov_stmt, 
				CirStateStore.new_unit(execution.get_statement()), 
				SymbolFactory.sym_constant(Boolean.TRUE));
	}
	
}
