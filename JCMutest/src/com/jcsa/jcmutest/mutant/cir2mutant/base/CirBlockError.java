package com.jcsa.jcmutest.mutant.cir2mutant.base;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

public class CirBlockError extends CirAttribute {

	protected CirBlockError(CirExecution execution, SymbolExpression parameter) throws IllegalArgumentException {
		super(CirAttributeType.blk_error, execution, execution.get_statement(), parameter);
	}
	
	/* specialized */
	/**
	 * @return the statement being traped to exit the program as failure immediately
	 */
	public CirStatement get_statement() { return this.get_execution().get_statement(); }
	/**
	 * @return whether to execute the statement 
	 */
	public boolean is_executed() { return ((SymbolConstant) this.get_parameter()).get_bool(); }
	/**
	 * @return whether to cancal the execution
	 */
	public boolean is_canceled() { return !((SymbolConstant) this.get_parameter()).get_bool(); }
	
}
