package com.jcsa.jcmutest.mutant.sec2mutant.mod;

import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecDescription;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.refs.SecReferenceError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.token.SecExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.sym.SymContexts;

/**
 * Each unit in symbolic state space corresponds to an assertion defined
 * in symbolic error & constraint description.
 * 
 * @author yukimula
 *
 */
public class SecStateUnit {
	
	/* definitions */
	private SecStateGraph graph;
	private SecDescription description;
	protected SecStateUnit(SecStateGraph graph, SecDescription description) throws Exception {
		if(description == null || !description.is_consistent())
			throw new IllegalArgumentException("Invalid description");
		else {
			this.graph = graph;
			this.description = description;
		}
	}
	
	/* getters */
	/**
	 * @return the graph where the unit is created
	 */
	public SecStateGraph get_graph() { return this.graph; }
	/**
	 * @return the description that is controlled by this unit
	 */
	public SecDescription get_description() { return this.description; }
	/**
	 * @return the statement where the unit is defined on
	 */
	public CirStatement get_statement() { 
		return description.get_location().get_statement(); 
	}
	/**
	 * @return the expression on which the error influences
	 */
	public SecExpression get_orig_expression() {
		if(this.description instanceof SecExpressionError) {
			return ((SecExpressionError) this.description).get_orig_expression();
		}
		else if(this.description instanceof SecReferenceError) {
			return ((SecReferenceError) this.description).get_orig_reference();
		}
		else {
			return null;
		}
	}
	/**
	 * @return whether the unit refers to a constraint
	 */
	public boolean is_constraint() { return this.description.is_constraint(); }
	/**
	 * @return whether the unit refers to a state error
	 */
	public boolean is_state_error() { return this.description.is_state_error(); }
	
	/**
	 * @param contexts
	 * @return get the unit with description optimized under the contexts
	 * @throws Exception
	 */
	public SecStateUnit optimize(SymContexts contexts) throws Exception {
		return this.graph.get_unit(this.description.optimize(contexts));
	}
	
	@Override
	public String toString() { return this.description.toString(); }
	@Override
	public boolean equals(Object obj) {
		if(obj == this)
			return true;
		else if(obj instanceof SecStateUnit)
			return ((SecStateUnit) obj).description.equals(this.description);
		else
			return false;
	}
	@Override
	public int hashCode() { return this.description.hashCode(); }
	
}
