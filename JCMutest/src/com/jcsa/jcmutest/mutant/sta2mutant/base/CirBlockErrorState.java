package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;

/**
 * [stmt:statement] <== set_stmt(boolean, boolean)
 * 
 * @author yukimula
 *
 */
public class CirBlockErrorState extends CirPathErrorState {

	protected CirBlockErrorState(CirExecution point, boolean muta_execute) throws Exception {
		super(point, CirStateValue.set_stmt(!muta_execute, muta_execute));
	}
	
	/** 
	 * @return whether the statement is executed in original version
	 */
	public boolean is_original_executed() {
		return ((SymbolConstant) this.get_loperand()).get_bool();
	}
	
	/**
	 * @return whether the statement is executed in mutated version
	 */
	public boolean is_mutation_executed() {
		return ((SymbolConstant) this.get_roperand()).get_bool();
	}

}
