package com.jcsa.jcmutest.mutant.sym2mutant.base;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.parse.symbol.SymbolEvaluator;
import com.jcsa.jcparse.parse.symbol.process.SymbolProcess;

/**
 * Symbolic constraint is a symbolic expression (boolean) evaluated at some point of the program.
 *
 * @author yukimula
 *
 */
public class SymConstraint extends SymInstance {

	/** the symbolic condition being evaluated at the statement point **/
	private SymbolExpression condition;

	/**
	 * @param execution
	 * @param condition
	 * @throws IllegalArgumentException
	 */
	protected SymConstraint(CirExecution execution,
			SymbolExpression condition) throws IllegalArgumentException {
		super(SymInstanceType.constraint, execution);
		if(condition == null)
			throw new IllegalArgumentException("Invalid condition: null");
		this.condition = condition;
	}

	/**
	 * @return the symbolic condition being evaluated at the statement point
	 */
	public SymbolExpression get_condition() { return this.condition; }
	@Override
	protected String generate_code() throws Exception {
		return this.get_type() + ":" + this.get_execution() + "(" + this.condition.generate_code(true) + ")";
	}

	@Override
	public Boolean validate(SymbolProcess contexts) throws Exception {
		SymbolExpression expression = SymbolEvaluator.
				evaluate_on(this.condition, contexts);
		if(expression instanceof SymbolConstant) {
			return ((SymbolConstant) expression).get_bool();
		}
		else {
			return null;	/* Undecidable */
		}
	}

}
