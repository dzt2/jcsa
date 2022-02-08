package com.jcsa.jcmutest.mutant.cir2mutant.base;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.lang.symbol.SymbolLiteral;

/**
 * ast_muta([statement, stmt_key]; [muta_id, operator]);
 * 
 * @author yukimula
 *
 */
public class CirSyMutationState extends CirConditionState {

	protected CirSyMutationState(CirExecution execution, int mid, String operator) throws Exception {
		super(CirAbstractClass.ast_muta, execution, 
				SymbolFactory.sym_constant(Integer.valueOf(mid)), 
				SymbolFactory.sym_expression(operator));
	}
	
	/* special getter */
	/**
	 * @return the integer ID of Mutant that the state refers to
	 */
	public int get_mutant_id() {
		SymbolConstant loperand = (SymbolConstant) this.get_loperand();
		return loperand.get_int();
	}
	/**
	 * @return the mutation operator to generate the mutant object
	 */
	public String get_operator() {
		SymbolLiteral roperand = (SymbolLiteral) this.get_roperand();
		return roperand.get_literal();
	}
	
}
