package com.jcsa.jcmutest.mutant.cir2mutant.base;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.parse.symbol.SymbolEvaluator;
import com.jcsa.jcparse.parse.symbol.process.SymbolProcess;

public class CirConstraint extends CirAttribute {

	protected CirConstraint(CirExecution execution, SymbolExpression parameter) throws IllegalArgumentException {
		super(CirAttributeType.condition, execution, execution.get_statement(), parameter);
	}

	@Override
	public CirAttribute optimize(SymbolProcess context) throws Exception {
		SymbolExpression condition = this.get_parameter();
		try {
			condition = SymbolEvaluator.evaluate_on(condition, context);
		}
		catch(Exception ex) {
			return CirAttribute.new_constraint(get_execution(), Boolean.TRUE, true);
		}
		if(condition instanceof SymbolConstant) {
			if(((SymbolConstant) condition).get_bool()) {
				return CirAttribute.new_cover_count(get_execution(), 1);
			}
			else {
				return CirAttribute.new_constraint(this.get_execution(), Boolean.FALSE, true);
			}
		}
		else {
			return CirAttribute.new_constraint(this.get_execution(), condition, true);
		}
	}

	@Override
	public Boolean evaluate(SymbolProcess context) throws Exception {
		SymbolExpression condition = this.get_parameter();
		try {
			condition = SymbolEvaluator.evaluate_on(condition, context);
		}
		catch(Exception ex) {
			return Boolean.TRUE;
		}
		if(condition instanceof SymbolConstant) {
			return ((SymbolConstant) condition).get_bool();
		}
		else {
			return null;
		}
	}

	/**
	 * @return the statement in which the constraint will be evaluated
	 */
	public CirStatement get_statement() { return (CirStatement) this.get_location(); }

	/**
	 * @return the symbolic condition being evaluated in the constraint
	 */
	public SymbolExpression get_condition() { return this.get_parameter(); }

}
