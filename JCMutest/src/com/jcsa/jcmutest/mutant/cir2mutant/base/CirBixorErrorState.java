package com.jcsa.jcmutest.mutant.cir2mutant.base;

import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * use_point: xor_expr([expression, expr_key]; [orig_value, difference])
 * 
 * def_point: xor_expr([statement, refer_key]; [orig_value, difference])
 * 
 * @author yukimula
 *
 */
public class CirBixorErrorState extends CirDataErrorState {

	protected CirBixorErrorState(CirExecution execution, CirNode location,
			SymbolExpression identifier, SymbolExpression orig_value, SymbolExpression difference) throws Exception {
		super(CirAbstractClass.xor_expr, execution, location, identifier, orig_value, difference);
	}
	
	public SymbolExpression get_difference() { return this.get_roperand(); } 
	
}
