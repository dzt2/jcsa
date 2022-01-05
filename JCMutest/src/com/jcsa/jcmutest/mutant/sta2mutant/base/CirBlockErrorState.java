package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * mut_stmt(execution, statement, [bool, bool]);
 * 
 * @author yukimula
 *
 */
public class CirBlockErrorState extends CirPathErrorState {

	protected CirBlockErrorState(CirExecution execution, SymbolExpression orig_value,
			SymbolExpression muta_value) throws Exception {
		super(CirStateCategory.mut_stmt, execution, orig_value, muta_value);
	}
	
	/**
	 * @return whether this statement is originally executed
	 */
	public boolean is_original_executed() {
		return ((SymbolConstant) this.get_orig_value()).get_bool();
	}
	/**
	 * @return whether this statement is executed by mutant
	 */
	public boolean is_mutation_executed() {
		return ((SymbolConstant) this.get_muta_value()).get_bool();
	}
	
}
