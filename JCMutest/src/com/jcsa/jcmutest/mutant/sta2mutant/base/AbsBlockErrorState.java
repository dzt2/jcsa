package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * 	set_stmt(statement; orig_exec, muta_exec)
 * 	
 * 	@author yukimula
 *
 */
public class AbsBlockErrorState extends AbsPathErrorState {

	protected AbsBlockErrorState(AbsExecutionStore _store, boolean muta_exec) throws Exception {
		super(AbsExecutionClass.set_stmt, _store, 
				SymbolFactory.sym_constant(Boolean.valueOf(!muta_exec)), 
				SymbolFactory.sym_constant(Boolean.valueOf(muta_exec)));
	}
	
	/**
	 * @return whether the original location is executed
	 */
	public boolean is_original_executed() {
		return ((SymbolConstant) this.get_loperand()).get_bool();
	}
	
	/**
	 * @return whether the mutation location is executed
	 */
	public boolean is_mutation_executed() {
		return ((SymbolConstant) this.get_roperand()).get_bool();
	}
	
}
