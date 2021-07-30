package com.jcsa.jcmutest.mutant.cir2mutant.base;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.parse.symbol.process.SymbolProcess;

public class CirTrapsError extends CirAttribute {

	protected CirTrapsError(CirExecution execution, SymbolExpression parameter) throws IllegalArgumentException {
		super(CirAttributeType.trp_error, execution, execution.get_statement(), parameter);
	}
	
	/**
	 * @return the statement in which the trapping will be introduced
	 */
	public CirStatement get_statement() { return (CirStatement) this.get_location(); }

	@Override
	public CirAttribute optimize(SymbolProcess context) throws Exception {
		return this;
	}

	@Override
	public Boolean evaluate(SymbolProcess context) throws Exception {
		return true;
	}
	
}
