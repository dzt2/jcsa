package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;

/**
 * execution [stmt:statement] <== {set_stmt:bool:bool}
 * 
 * @author yukimula
 *
 */
public class CirBlockErrorState extends CirPathErrorState {

	protected CirBlockErrorState(CirExecution execution, boolean execute) throws Exception {
		super(execution, CirStateValue.set_stmt(execute));
	}
	
	/**
	 * @return whether the statement is executed in original program testing
	 */
	public boolean is_orig_executed() {
		return ((SymbolConstant) this.get_ovalue()).get_bool();
	}
	/**
	 * @return whether the statement is executed in mutation program testing
	 */
	public boolean is_muta_executed() {
		return ((SymbolConstant) this.get_mvalue()).get_bool();
	}
	
}
