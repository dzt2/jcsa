package com.jcsa.jcmutest.mutant.cir2mutant.cerr;

import com.jcsa.jcparse.flwa.symbol.CStateContexts;
import com.jcsa.jcparse.flwa.symbol.SymEvaluator;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.sym.SymConstant;
import com.jcsa.jcparse.lang.sym.SymExpression;

/**
 * Symbolic constraint is a symbolic expression (boolean) evaluated at some point of the program.
 * 
 * @author yukimula
 *
 */
public class SymConstraint extends SymInstance {
	
	/** the symbolic condition being evaluated at the statement point **/
	private SymExpression condition;
	
	/**
	 * @param execution
	 * @param condition
	 * @throws IllegalArgumentException
	 */
	protected SymConstraint(CirExecution execution, SymExpression condition) throws IllegalArgumentException {
		super(SymInstanceType.constraint, execution);
		if(condition == null)
			throw new IllegalArgumentException("Invalid condition: null");
		this.condition = condition;
	}
	
	/**
	 * @return the symbolic condition being evaluated at the statement point
	 */
	public SymExpression get_condition() { return this.condition; }
	@Override
	protected String generate_code() throws Exception {
		return this.get_type() + ":" + this.get_execution() + "(" + this.condition.generate_code() + ")";
	}

	@Override
	public Boolean validate(CStateContexts contexts) throws Exception {
		SymExpression expression = SymEvaluator.
				evaluate_on(this.condition, contexts);
		if(expression instanceof SymConstant) {
			return ((SymConstant) expression).get_bool();
		}
		else {
			return null;	/* Undecidable */
		}
	}
	
}
