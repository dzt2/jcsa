package com.jcsa.jcmutest.mutant.cir2mutant.base;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * {cov_count; execution; statement; integer;}
 * @author yukimula
 *
 */
public class CirCoverCount extends CirAttribute {

	protected CirCoverCount(CirExecution execution, SymbolExpression parameter)
			throws IllegalArgumentException {
		super(CirAttributeType.cov_count, execution, execution.get_statement(), parameter);
	}
	
	/* specialized */
	/**
	 * @return the statement where the counter is executed within
	 */
	public CirStatement get_statement() { return this.get_execution().get_statement(); }
	/**
	 * @return the maximal times to achieve the coverage counter
	 */
	public int get_count_limit() { return ((SymbolConstant) this.get_parameter()).get_int(); }
	
}
