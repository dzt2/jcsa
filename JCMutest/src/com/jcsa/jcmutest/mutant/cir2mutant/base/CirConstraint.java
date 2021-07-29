package com.jcsa.jcmutest.mutant.cir2mutant.base;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * {condition; execution; statement; expression}
 * @author yukimula
 *
 */
public class CirConstraint extends CirAttribute {

	protected CirConstraint(CirExecution execution, SymbolExpression parameter)
			throws IllegalArgumentException {
		super(CirAttributeType.condition, execution, execution.get_statement(), parameter);
	}
	
	/* specialized */
	/**
	 * @return the statement where the constraint is evaluated
	 */
	public CirStatement get_statement() { return this.get_execution().get_statement(); }
	/**
	 * @return symbolic condition being evaluated at particular point
	 */
	public SymbolExpression get_condition() { return this.get_parameter(); }
	
}
