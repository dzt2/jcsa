package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class CirBlockErrorState extends CirPathErrorState {

	protected CirBlockErrorState(CirAbstractStore location, boolean orig_exec, boolean muta_exec) throws Exception {
		super(CirAbstractClass.mut_stmt, location, 
				SymbolFactory.sym_constant(Boolean.valueOf(orig_exec)), 
				SymbolFactory.sym_constant(Boolean.valueOf(muta_exec)));
	}
	
	/**
	 * @return whether the statement is executed in the original program
	 */
	public boolean is_original_executed() { return ((SymbolConstant) this.get_loperand()).get_bool(); }
	
	/**
	 * @return whether the statement is executed in the mutation program
	 */
	public boolean is_mutation_executed() { return ((SymbolConstant) this.get_roperand()).get_bool(); }
	
}
