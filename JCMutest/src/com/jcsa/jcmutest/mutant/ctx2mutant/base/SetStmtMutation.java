package com.jcsa.jcmutest.mutant.ctx2mutant.base;

import com.jcsa.jcparse.lang.program.AstCirNode;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class SetStmtMutation extends ContextMutation {

	protected SetStmtMutation(AstCirNode location, 
			boolean orig_exec, boolean muta_exec) throws Exception {
		super(ContextMutaClass.set_stmt, location, 
				SymbolFactory.sym_constant(orig_exec), 
				SymbolFactory.sym_constant(muta_exec));
	}
	
	/**
	 * @return whether the statement is executed in original program
	 */
	public boolean is_original_executed() { return ((SymbolConstant) this.get_loperand()).get_bool(); }
	
	/**
	 * @return whether the statement is executed in mutation program
	 */
	public boolean is_mutation_executed() { return ((SymbolConstant) this.get_roperand()).get_bool(); }
	
}
