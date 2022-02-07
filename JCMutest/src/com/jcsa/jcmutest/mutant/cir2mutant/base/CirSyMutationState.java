package com.jcsa.jcmutest.mutant.cir2mutant.base;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * ast_muta([statement, stmt_key]; [muta_id, operator]);
 * 
 * @author yukimula
 *
 */
public class CirSyMutationState extends CirConditionState {

	protected CirSyMutationState(CirExecution execution, Mutant mutant) throws Exception {
		super(CirAbstractClass.ast_muta, execution, 
				SymbolFactory.sym_constant(Integer.valueOf(mutant.get_id())), 
				SymbolFactory.sym_expression(mutant.get_mutation().get_operator()));
	}
	
	/* special getter */
	/**
	 * @return the integer ID of Mutant that the state refers to
	 */
	public int get_mutant_id() {
		SymbolConstant loperand = (SymbolConstant) this.get_loperand();
		return loperand.get_int();
	}
	
}
