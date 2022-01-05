package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * mut_brac(execution, statement, [orig_condition, muta_condition]);
 * 
 * @author yukimula
 *
 */
public class CirBrachErrorState extends CirPathErrorState {

	protected CirBrachErrorState(CirExecution execution, SymbolExpression orig_value,
			SymbolExpression muta_value) throws Exception {
		super(CirStateCategory.mut_brac, execution, orig_value, muta_value);
	}
	
	/**
	 * @return the original condition value to choose the branch on the statement
	 */
	public SymbolExpression get_original_condition() { return this.get_orig_value(); }
	/**
	 * @return the mutation condition value to choose the branch on the statement
	 */
	public SymbolExpression get_mutation_condition() { return this.get_muta_value(); }
	
}
