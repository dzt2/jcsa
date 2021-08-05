package com.jcsa.jcmutest.mutant.cir2mutant.base;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.parse.symbol.process.SymbolProcess;

public class CirBlockError extends CirAttribute {

	protected CirBlockError(CirExecution execution, SymbolExpression parameter) throws IllegalArgumentException {
		super(CirAttributeType.blk_error, execution, execution.get_statement(), parameter);
	}

	/**
	 * @return the statement to be mutated in execution
	 */
	public CirStatement get_statement() { return (CirStatement) this.get_location(); }
	/**
	 * @return whether the error incorrectly executes the statement when it should not be
	 */
	public boolean is_executed() { return ((SymbolConstant) this.get_parameter()).get_bool(); }
	/**
	 * @return whether the error incorrectly cancals the execution, when it should have been
	 */
	public boolean is_canceled() { return !((SymbolConstant) this.get_parameter()).get_bool(); }

	@Override
	public CirAttribute optimize(SymbolProcess context) throws Exception {
		return this;
	}

	@Override
	public Boolean evaluate(SymbolProcess context) throws Exception {
		return true;
	}

}
