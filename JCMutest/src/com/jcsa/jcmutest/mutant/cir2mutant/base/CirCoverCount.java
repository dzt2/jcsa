package com.jcsa.jcmutest.mutant.cir2mutant.base;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.parse.symbol.process.SymbolProcess;

public class CirCoverCount extends CirAttribute {

	protected CirCoverCount(CirExecution execution, SymbolExpression parameter) throws IllegalArgumentException {
		super(CirAttributeType.cov_count, execution, execution.get_statement(), parameter);
	}

	@Override
	public CirAttribute optimize(SymbolProcess context) throws Exception {
		return this;
	}

	@Override
	public Boolean evaluate(SymbolProcess context) throws Exception {
		SymbolExpression source = SymbolFactory.sym_expression(this.get_execution());
		if(context != null) {
			SymbolExpression times = context.get_data_stack().load(source);
			if(times instanceof SymbolConstant) {
				return ((SymbolConstant) times).get_int() >= this.get_coverage_count();
			}
			else {
				return false;
			}
		}
		else {
			return null;
		}
	}
	
	/**
	 * @return the statement being counted in the coverage counter
	 */
	public CirStatement get_statement() { return this.get_execution().get_statement(); }
	/**
	 * @return the times to execute the statement and count its coverage
	 */
	public int get_coverage_count() { return ((SymbolConstant) this.get_parameter()).get_int(); }
	
}
