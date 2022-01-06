package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * cov_time(execution, {stmt:statement}, {int_times});
 * 
 * @author yukimula
 *
 */
public class CirCoverTimesState extends CirConditionState {

	protected CirCoverTimesState(CirExecution execution, int times) throws Exception {
		super(CirStateClass.cov_time, 
				CirStateStore.new_unit(execution.get_statement()), 
				SymbolFactory.sym_constant(Integer.valueOf(times)));
	}
	
	/**
	 * @return the number of execution times that the statement needs be covered
	 */
	public int get_exec_times() {
		return ((SymbolConstant) this.get_uvalue()).get_int();
	}
	
}
