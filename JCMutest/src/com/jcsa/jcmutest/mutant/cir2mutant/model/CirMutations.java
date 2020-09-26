package com.jcsa.jcmutest.mutant.cir2mutant.model;

import java.util.HashMap;
import java.util.Map;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirReferExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;

/**
 * It provides the interfaces to manage the mutations created in C-intermediate 
 * representation.
 * 
 * @author yukimula
 *
 */
public class CirMutations {
	
	/* definitions */
	/** C-intermediate code on which mutation is seeded **/
	private CirTree cir_tree;
	/** mapping from unique code to constraint **/
	private Map<String, CirConstraint> constraints;
	/** mapping from unique code to state-error **/
	private Map<String, CirStateError> state_errors;
	/** mapping from unique code to cir-mutation **/
	private Map<String, CirMutation> mutations;
	/**
	 * @param cir_tree C-intermediate code on which mutation is injected
	 * @throws IllegalArgumentException
	 */
	public CirMutations(CirTree cir_tree) throws IllegalArgumentException {
		if(cir_tree == null)
			throw new IllegalArgumentException("Invalid cir_tree: null");
		else {
			this.cir_tree = cir_tree;
			this.constraints = new HashMap<String, CirConstraint>();
			this.state_errors = new HashMap<String, CirStateError>();
			this.mutations = new HashMap<String, CirMutation>();
		}
	}
	
	/* getters */
	/**
	 * @return C-intermediate code on which mutation is seeded
	 */
	public CirTree get_cir_tree() { return this.cir_tree; }
	/**
	 * @param constraint
	 * @return the unique constraint defined in the program
	 * @throws Exception
	 */
	private CirConstraint get_unique_constraint(CirConstraint constraint) throws Exception {
		String key = constraint.generate_code();
		if(!this.constraints.containsKey(key)) {
			this.constraints.put(key, constraint);
		}
		return this.constraints.get(key);
	}
	/**
	 * @param state_error
	 * @return the unique state-error defined in the program
	 * @throws Exception
	 */
	private CirStateError get_unique_state_error(CirStateError state_error) throws Exception {
		String key = state_error.generate_code();
		if(!this.state_errors.containsKey(key)) {
			this.state_errors.put(key, state_error);
		}
		return this.state_errors.get(key);
	}
	/**
	 * @param mutation
	 * @return the unique cir-mutation defined in the program
	 * @throws Exception
	 */
	private CirMutation get_unique_mutation(CirMutation mutation) throws Exception {
		String key = mutation.generate_code();
		if(!this.mutations.containsKey(key)) {
			this.mutations.put(key, mutation);
		}
		return this.mutations.get(key);
	}
	
	/* creators */
	/**
	 * verify whether the location belongs to the program under test
	 * @param location
	 * @throws Exception
	 */
	private void verify_location(CirNode location) throws Exception {
		if(this.cir_tree != location.get_tree())
			throw new RuntimeException("Unable to match the program");
	}
	/**
	 * @param statement
	 * @param expression
	 * @param value
	 * @return constraint is used to assert whether expression is true or false
	 * @throws Exception
	 */
	public CirConstraint expression_constraint(CirStatement statement,
			Object expression, boolean value) throws Exception {
		this.verify_location(statement);
		SymExpression condition = SymFactory.parse(expression);
		CType type = CTypeAnalyzer.get_value_type(condition.get_data_type());
		if(CTypeAnalyzer.is_boolean(type)) {
			if(value) { }
			else {
				condition = SymFactory.logic_not(condition);
			}
		}
		else if(CTypeAnalyzer.is_integer(type) 
				|| CTypeAnalyzer.is_real(type)
				|| CTypeAnalyzer.is_pointer(type)) {
			if(value) {
				condition = SymFactory.not_equals(condition, Integer.valueOf(0));
			}
			else {
				condition = SymFactory.equal_with(condition, Integer.valueOf(0));
			}
		}
		else {
			throw new IllegalArgumentException(type.generate_code());
		}
		return this.get_unique_constraint(new CirConstraint(statement, condition));
	}
	/**
	 * @param statement
	 * @param minimal_times
	 * @param maximal_times
	 * @return constraint is used to assert the times of which the statement is
	 * 		   executed during testing is in the specified range.
	 * @throws Exception
	 */
	public CirConstraint statement_constraint(CirStatement statement, 
			int minimal_times, int maximal_times) throws Exception {
		this.verify_location(statement);
		SymExpression stmt_id = SymFactory.sym_statement(statement);
		SymExpression lcondition = SymFactory.
				greater_eq(stmt_id, Integer.valueOf(minimal_times));
		SymExpression rcondition = SymFactory.
				smaller_eq(stmt_id, Integer.valueOf(maximal_times));
		SymExpression condition = SymFactory.logic_and(lcondition, rcondition);
		return this.get_unique_constraint(new CirConstraint(statement, condition));
	}
	/**
	 * @param statement where the exception will be thrown
	 * @return trap_error(statement)
	 * @throws Exception
	 */
	public CirStateError trap_error(CirStatement statement) throws Exception {
		this.verify_location(statement);
		return this.get_unique_state_error(new CirTrapError(statement));
	}
	/**
	 * @param orig_flow the original flow being replaced
	 * @param muta_flow the mutation flow to replace 
	 * @return flow_error(orig_flow, muta_flow)
	 * @throws Exception
	 */
	public CirStateError flow_error(CirExecutionFlow orig_flow, 
			CirExecutionFlow muta_flow) throws Exception {
		this.verify_location(orig_flow.get_source().get_statement());
		return this.get_unique_state_error(new CirFlowError(orig_flow, muta_flow));
	}
	/**
	 * @param expression
	 * @param muta_value
	 * @return expr_error(expression, orig_value, muta_value)
	 * @throws Exception
	 */
	public CirStateError expr_error(CirExpression expression,
			SymExpression muta_value) throws Exception {
		this.verify_location(expression);
		return this.get_unique_state_error(new 
				CirExpressionError(expression, muta_value));
	}
	/**
	 * @param reference
	 * @param muta_value
	 * @return refer_error(reference, orig_value, muta_value)
	 * @throws Exception
	 */
	public CirStateError refer_error(CirReferExpression reference,
			SymExpression muta_value) throws Exception {
		this.verify_location(reference);
		return this.get_unique_state_error(new 
				CirReferenceError(reference, muta_value));
	}
	/**
	 * @param constraint
	 * @param state_error
	 * @return the mutation as constraint-error pair in testing
	 * @throws Exception
	 */
	public CirMutation new_mutation(CirConstraint constraint, 
			CirStateError state_error) throws Exception {
		return this.get_unique_mutation(new CirMutation(constraint, state_error));
	}
	
}
