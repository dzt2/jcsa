package com.jcsa.jcmutest.mutant.cir2mutant.base;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;


public class CirTrapsError extends CirAttribute {

	protected CirTrapsError(CirExecution execution, SymbolExpression parameter) throws IllegalArgumentException {
		super(CirAttributeType.trp_error, execution, execution.get_statement(), parameter);
	}
	
	/* specialized */
	/**
	 * @return the statement being traped to exit the program as failure immediately
	 */
	public CirStatement get_statement() { return this.get_execution().get_statement(); }
	
}
