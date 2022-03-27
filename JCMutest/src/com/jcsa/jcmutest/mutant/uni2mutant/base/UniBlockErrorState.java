package com.jcsa.jcmutest.mutant.uni2mutant.base;

import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * 	set_stmt(statement; orig_exec, muta_exec)
 * 	
 * 	@author yukimula
 *
 */
public class UniBlockErrorState extends UniPathErrorState {
	
	/**
	 * @param _store
	 * @param muta_exec	whether the statement is incorrectly executed or not
	 * @throws Exception
	 */
	protected UniBlockErrorState(UniAbstractStore _store, boolean muta_exec) throws Exception {
		super(UniAbstractClass.mut_stmt, _store, 
				SymbolFactory.sym_constant(Boolean.valueOf(!muta_exec)), 
				SymbolFactory.sym_constant(Boolean.valueOf(muta_exec)));
	}
	
	/**
	 * @return whether the statement is executed in original version
	 */
	public boolean is_orig_executed() {
		return ((SymbolConstant) this.get_loperand()).get_bool();
	}
	
	/**
	 * @return whether the statement is executed in mutation version
	 */
	public boolean is_muta_executed() {
		return ((SymbolConstant) this.get_roperand()).get_bool();
	}
	
}
