package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class UniBlockErrorState extends UniPathErrorState {

	protected UniBlockErrorState(CirStatement location, boolean muta_exec) throws Exception {
		super(UniAbstractClass.set_stmt, location, 
				SymbolFactory.sym_constant(Boolean.valueOf(!muta_exec)), 
				SymbolFactory.sym_constant(Boolean.valueOf(muta_exec)));
	}
	
	/**
	 * @return whether the original version will execute
	 */
	public boolean is_original_executed() {
		return ((SymbolConstant) this.get_loperand()).get_bool();
	}
	
	/**
	 * @return whether the mutation version will execute
	 */
	public boolean is_mutation_executed() {
		return ((SymbolConstant) this.get_roperand()).get_bool();
	}
	
}
