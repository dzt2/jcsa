package com.jcsa.jcmutest.mutant.cir2mutant.error;

import java.util.HashMap;
import java.util.Map;

import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirReferExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.sym.SymExpression;

/**
 * It is used to create the unique state error in the program specified.
 * 
 * @author yukimula
 *
 */
public class CirMutations {
	
	/* definitions */
	/** C-intermediate code in which error seeded **/
	private CirTree cir_tree;
	/** mapping from unique code to constraint **/
	private Map<String, CirConstraint> constraints;
	/** mapping from unique code to state error **/
	private Map<String, CirStateError> state_errors;
	/**
	 * create a state error library w.r.t. the program as specified in CIR
	 * @param cir_tree
	 * @throws IllegalArgumentException
	 */
	public CirMutations(CirTree cir_tree) throws IllegalArgumentException {
		if(cir_tree == null)
			throw new IllegalArgumentException("Invalid cir_tree: null");
		else {
			this.cir_tree = cir_tree;
			this.constraints = new HashMap<String, CirConstraint>();
			this.state_errors = new HashMap<String, CirStateError>();
		}
	}
	
	/* constraint getters */
	/**
	 * @return C-intermediate code in which the errors are seeded
	 */
	public CirTree get_cir_tree() { return this.cir_tree; }
	/**
	 * @param constraint
	 * @return the unique instance of constraint 
	 * @throws Exception
	 */
	private CirConstraint get_unique_constraint(
			CirConstraint constraint) throws Exception {
		String key = constraint.generate_code();
		if(!this.constraints.containsKey(key)) {
			this.constraints.put(key, constraint);
		}
		return this.constraints.get(key);
	}
	/**
	 * @param statement
	 * @param expression
	 * @param value
	 * @return constraint as expression == value at statement
	 * @throws Exception
	 */
	public CirConstraint get_constraint(CirStatement statement, 
			Object expression, boolean value) throws Exception {
		return this.get_unique_constraint(CirConstraint.
				new_constraint(statement, expression, value));
	}
	/**
	 * @param statement
	 * @param expression
	 * @return constraint as expression == true at statement
	 * @throws Exception
	 */
	public CirConstraint get_constraint(CirStatement statement, 
			Object expression) throws Exception {
		return this.get_unique_constraint(CirConstraint.
				new_constraint(statement, expression, true));
	}
	
	/* state error getters */
	/**
	 * @param error
	 * @return the unique state error as specified
	 * @throws Exception
	 */
	private CirStateError get_unique_error(CirStateError error) throws Exception {
		String key = error.generate_code();
		if(!this.state_errors.containsKey(key))
			this.state_errors.put(key, error);
		return this.state_errors.get(key);
	}
	/**
	 * @param statement
	 * @return trap_error(statement) throws exception and terminate program at statement
	 * @throws Exception
	 */
	public CirTrapError trap_error(CirStatement statement) throws Exception {
		return (CirTrapError) this.get_unique_error(new CirTrapError(statement));
	}
	/**
	 * @param original_flow
	 * @param mutation_flow
	 * @return flow_error(orig_error, muta_error)
	 * @throws Exception
	 */
	public CirFlowError flow_error( 
			CirExecutionFlow original_flow,
			CirExecutionFlow mutation_flow) throws Exception {
		return (CirFlowError) this.get_unique_error(new 
				CirFlowError(original_flow.get_source().get_statement(), 
						original_flow, mutation_flow));
	}
	/**
	 * @param expression
	 * @param muta_value
	 * @return expr_error replaces the expression with specified value
	 * @throws Exception
	 */
	public CirExpressionError expr_error(CirExpression expression, SymExpression muta_value) throws Exception {
		return (CirExpressionError) this.get_unique_error(new 
				CirExpressionError(expression.statement_of(), expression, muta_value));
	}
	/**
	 * @param reference
	 * @param muta_value
	 * @return 
	 * @throws Exception
	 */
	public CirReferenceError refr_error(CirReferExpression reference, SymExpression muta_value) throws Exception {
		return (CirReferenceError) this.get_unique_error(new 
				CirReferenceError(reference.statement_of(), reference, muta_value));
	}
	
}
