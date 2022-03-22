package com.jcsa.jcmutest.mutant.uni2mutant.base.impl;

import com.jcsa.jcmutest.mutant.uni2mutant.base.UniAbstractClass;
import com.jcsa.jcmutest.mutant.uni2mutant.base.UniAbstractStore;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * 	mut_stmt(statement, orig_exec, muta_exec)
 * 	
 *	@author yukimula
 *
 */
public class UniBlockErrorState extends UniPathErrorState {

	protected UniBlockErrorState(UniAbstractStore state_store, boolean muta_exec) throws Exception {
		super(UniAbstractClass.mut_stmt, state_store, 
				SymbolFactory.sym_constant(Boolean.valueOf(!muta_exec)),
				SymbolFactory.sym_constant(Boolean.valueOf(muta_exec)));
	}
	
	/**
	 * @return 	whether the statement is executed in original version
	 */
	public boolean is_orig_exec() { return ((SymbolConstant) this.get_lvalue()).get_bool(); }
	
	/**
	 * @return	whether the statement is executed in mutation version
	 */
	public boolean is_muta_exec() { return ((SymbolConstant) this.get_rvalue()).get_bool(); }
	
}
