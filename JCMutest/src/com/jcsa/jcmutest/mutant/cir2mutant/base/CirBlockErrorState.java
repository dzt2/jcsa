package com.jcsa.jcmutest.mutant.cir2mutant.base;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * mut_stmt([statement, stmt_key]; [orig_exec, muta_exec])
 * 
 * @author yukimula
 *
 */
public class CirBlockErrorState extends CirPathErrorState {

	protected CirBlockErrorState(CirExecution execution, boolean muta_execute) throws Exception {
		super(CirAbstractClass.mut_stmt, execution, 
				SymbolFactory.sym_constant(Boolean.valueOf(!muta_execute)), 
				SymbolFactory.sym_constant(Boolean.valueOf(muta_execute)));
	}
	
	/* getters */
	/**
	 * @return whether the statement is correctly executed in original program
	 */
	public boolean is_original_executed() {
		SymbolConstant loperand = (SymbolConstant) this.get_loperand();
		return loperand.get_bool();
	}
	/**
	 * @return whether the statement is incorrectly executed in mutated location
	 */
	public boolean is_mutated_executed() {
		SymbolConstant roperand = (SymbolConstant) this.get_roperand();
		return roperand.get_bool();
	}
	
}
