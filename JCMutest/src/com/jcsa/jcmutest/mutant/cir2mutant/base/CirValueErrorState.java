package com.jcsa.jcmutest.mutant.cir2mutant.base;

import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * use_point: set_expr([expression, expr_key]; [orig_value, muta_value])
 * 
 * def_point: set_expr([statement, refer_key]; [orig_value, muta_value])
 * 
 * @author yukimula
 *
 */
public class CirValueErrorState extends CirDataErrorState {

	protected CirValueErrorState(CirExecution execution, CirNode location, SymbolExpression 
			identifier, SymbolExpression orig_value, SymbolExpression muta_value) throws Exception {
		super(CirAbstractClass.set_expr, execution, location, identifier, orig_value, muta_value);
	}
	
	/* special getters */
	/**
	 * @return the mutated value to replace the original one
	 */
	public SymbolExpression get_muta_value() { return this.get_roperand(); } 
	
}
