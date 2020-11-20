package com.jcsa.jcmutest.mutant.cir2mutant.cerr;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.jcsa.jcmutest.mutant.cir2mutant.CirMutation;
import com.jcsa.jcparse.flwa.symbol.CStateContexts;
import com.jcsa.jcparse.flwa.symbol.SymEvaluator;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirReferExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.sym.SymBinaryExpression;
import com.jcsa.jcparse.lang.sym.SymConstant;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;

/**
 * Used to create unique instance of SymInstance or CirMutation
 * @author yukimula
 *
 */
public class CirMutations {
	
	/* definitions */
	/** C-intermediate code on which mutation is seeded **/
	private CirTree cir_tree;
	/** set of unique instance for symbolic constraints or state errors **/
	private Map<String, SymInstance> sym_instances;
	/** set of mutations injected in C-intermediate representation code **/
	private Map<String, CirMutation> cir_mutations;
	
	/* constructor */
	/**
	 * @param cir_tree C-intermediate code on which mutation is injected
	 * @throws IllegalArgumentException
	 */
	public CirMutations(CirTree cir_tree) throws IllegalArgumentException {
		if(cir_tree == null)
			throw new IllegalArgumentException("Invalid cir_tree: null");
		else {
			this.cir_tree = cir_tree;
			this.sym_instances = new HashMap<String, SymInstance>();
			this.cir_mutations = new HashMap<String, CirMutation>();
		}
	}
	
	/* getters */
	/**
	 * @return C-intermediate code on which mutation is seeded
	 */
	public CirTree get_cir_tree() { return this.cir_tree; }
	/**
	 * @param instance
	 * @return the unique symbolic instance in the program
	 * @throws Exception
	 */
	private SymInstance get_unique_instance(SymInstance instance) throws Exception {
		String key = instance.toString();
		if(!this.sym_instances.containsKey(key)) {
			this.sym_instances.put(key, instance);
		}
		return this.sym_instances.get(key);
	}
	/**
	 * @param mutation
	 * @return the unique cir-mutation defined in the program
	 * @throws Exception
	 */
	private CirMutation get_unique_mutation(CirMutation mutation) throws Exception {
		String key = mutation.toString();
		if(!this.cir_mutations.containsKey(key)) {
			this.cir_mutations.put(key, mutation);
		}
		return this.cir_mutations.get(key);
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
	 * @return unique instance of (execution, condition)
	 * @throws Exception
	 */
	public SymConstraint expression_constraint(CirStatement statement,
			Object expression, boolean value) throws Exception {
		this.verify_location(statement);
		SymExpression condition = SymFactory.sym_condition(expression, value);
		CirExecution execution = statement.get_tree().get_localizer().get_execution(statement);
		return (SymConstraint) this.get_unique_instance(new SymConstraint(execution, condition));
	}
	/**
	 * @param statement
	 * @param times
	 * @return constraint is used to assert the times of which the statement is
	 * 		   executed during testing is in the specified range.
	 * @throws Exception
	 */
	public SymConstraint statement_constraint(CirStatement statement, int times) throws Exception {
		this.verify_location(statement);
		SymExpression stmt_id = SymFactory.sym_expression(statement);
		SymExpression condition = SymFactory.greater_eq(stmt_id, Integer.valueOf(times));
		CirExecution execution = statement.get_tree().get_localizer().get_execution(statement);
		return (SymConstraint) this.get_unique_instance(new SymConstraint(execution, condition));
	}
	/**
	 * @param statement where the exception will be thrown
	 * @return trap_error(statement)
	 * @throws Exception
	 */
	public SymTrapError trap_error(CirStatement statement) throws Exception {
		this.verify_location(statement);
		CirExecution execution = statement.get_tree().get_localizer().get_execution(statement);
		return (SymTrapError) this.get_unique_instance(new SymTrapError(execution));
	}
	/**
	 * @param orig_flow the original flow being replaced
	 * @param muta_flow the mutation flow to replace 
	 * @return flow_error(orig_flow, muta_flow)
	 * @throws Exception
	 */
	public SymFlowError flow_error(CirExecutionFlow orig_flow, 
			CirExecutionFlow muta_flow) throws Exception {
		this.verify_location(orig_flow.get_source().get_statement());
		return (SymFlowError) this.get_unique_instance(new SymFlowError(orig_flow.get_source(), orig_flow, muta_flow));
	}
	/**
	 * @param expression
	 * @param muta_value
	 * @return expr_error(expression, orig_value, muta_value)
	 * @throws Exception
	 */
	public SymExpressionError expr_error(CirExpression expression,
			SymExpression muta_value) throws Exception {
		this.verify_location(expression);
		CirExecution execution = expression.get_tree().get_localizer().get_execution(expression.statement_of());
		return (SymExpressionError) this.get_unique_instance(new SymExpressionError(execution, expression, SymFactory.sym_expression(expression), muta_value));
	}
	/**
	 * @param reference
	 * @param muta_value
	 * @return refer_error(reference, orig_value, muta_value)
	 * @throws Exception
	 */
	public SymReferenceError refer_error(CirReferExpression expression,
			SymExpression muta_value) throws Exception {
		this.verify_location(expression);
		CirExecution execution = expression.get_tree().get_localizer().get_execution(expression.statement_of());
		return (SymReferenceError) this.get_unique_instance(new SymReferenceError(execution, expression, SymFactory.sym_expression(expression), muta_value));
	}
	/**
	 * @param reference
	 * @param muta_value
	 * @return stat_error(reference, muta_value)
	 * @throws Exception
	 */
	public SymStateValueError state_error(CirReferExpression expression,
			SymExpression muta_value) throws Exception {
		this.verify_location(expression);
		CirExecution execution = expression.get_tree().get_localizer().get_execution(expression.statement_of());
		return (SymStateValueError) this.get_unique_instance(new SymStateValueError(execution, expression, SymFactory.sym_expression(expression), muta_value));
	}
	/**
	 * @param constraint
	 * @param state_error
	 * @return the mutation as constraint-error pair in testing
	 * @throws Exception
	 */
	public CirMutation new_mutation(SymConstraint constraint, 
			SymStateError state_error) throws Exception {
		return this.get_unique_mutation(new CirMutation(constraint, state_error));
	}
	/**
	 * @return the set of mutations being created under the library.
	 */
	public Iterable<CirMutation> get_mutations() {
		return this.cir_mutations.values();
	}
	
	/**
	 * @param source
	 * @param contexts
	 * @return get the symbolic result of the expression evaluated under contexts
	 * @throws Exception
	 */
	private SymExpression evaluate(SymExpression source, CStateContexts contexts) throws Exception {
		return SymEvaluator.evaluate_on(source, contexts);
	}
	/**
	 * @param constraint
	 * @param contexts
	 * @return optimize the constraint to a concrete version w.r.t. the contexts as given
	 * @throws Exception
	 */
	public SymConstraint optimize(SymConstraint constraint, CStateContexts contexts) throws Exception {
		if(constraint == null)
			throw new IllegalArgumentException("Invalid constraint: null");
		else {
			CirStatement statement = constraint.get_statement();
			SymExpression condition = constraint.get_condition();
			condition = this.evaluate(condition, contexts);
			return this.expression_constraint(statement, condition, true);
		}
	}
	/**
	 * @param state_error
	 * @param contexts
	 * @return optimize the state error to a concrete version w.r.t. the contexts as given
	 * @throws Exception
	 */
	public SymStateError optimize(SymStateError state_error, CStateContexts contexts) throws Exception {
		if(state_error == null)
			throw new IllegalArgumentException("Invalid state_error: null");
		else if(state_error instanceof SymTrapError) {
			return this.trap_error(state_error.get_statement());
		}
		else if(state_error instanceof SymFlowError) {
			return this.flow_error(((SymFlowError) state_error).get_original_flow(), 
								  ((SymFlowError) state_error).get_mutation_flow());
		}
		else if(state_error instanceof SymExpressionError) {
			CirExpression expression = ((SymExpressionError) state_error).get_expression();
			SymExpression orig_value = ((SymExpressionError) state_error).get_original_value();
			SymExpression muta_value = ((SymExpressionError) state_error).get_mutation_value();
			try {
				orig_value = this.evaluate(orig_value, contexts); 
				muta_value = this.evaluate(muta_value, contexts);
			}
			catch(ArithmeticException ex) {
				return (SymStateError) get_unique_instance(new SymTrapError(state_error.get_execution()));
			}
			return (SymStateError) get_unique_instance(new SymExpressionError(state_error.get_execution(), expression, orig_value, muta_value));
		}
		else if(state_error instanceof SymReferenceError) {
			CirReferExpression reference = (CirReferExpression) ((SymReferenceError) state_error).get_expression();
			SymExpression orig_value = ((SymReferenceError) state_error).get_original_value();
			SymExpression muta_value = ((SymReferenceError) state_error).get_mutation_value();
			try {
				orig_value = this.evaluate(orig_value, contexts); 
				muta_value = this.evaluate(muta_value, contexts);
			}
			catch(ArithmeticException ex) {
				return (SymStateError) get_unique_instance(new SymTrapError(state_error.get_execution()));
			}
			return (SymStateError) get_unique_instance(new SymReferenceError(state_error.get_execution(), reference, orig_value, muta_value));
		}
		else if(state_error instanceof SymStateValueError) {
			CirReferExpression reference = (CirReferExpression) ((SymStateValueError) state_error).get_expression();
			SymExpression orig_value = ((SymStateValueError) state_error).get_original_value();
			SymExpression muta_value = ((SymStateValueError) state_error).get_mutation_value();
			try {
				orig_value = this.evaluate(orig_value, contexts); 
				muta_value = this.evaluate(muta_value, contexts);
			}
			catch(ArithmeticException ex) {
				return (SymStateError) get_unique_instance(new SymTrapError(state_error.get_execution()));
			}
			return (SymStateError) get_unique_instance(new SymStateValueError(state_error.get_execution(), reference, orig_value, muta_value));
		}
		else {
			throw new IllegalArgumentException(state_error.toString());
		}
	}
	/**
	 * @param mutation
	 * @param contexts
	 * @return the mutation of which constraint and state error are optimized w.r.t. the contexts
	 * @throws Exception
	 */
	public CirMutation optimize(CirMutation mutation, CStateContexts contexts) throws Exception {
		if(mutation == null)
			throw new IllegalArgumentException("Invalid mutation: null");
		else {
			SymConstraint constraint = this.optimize(mutation.get_constraint(), contexts);
			SymStateError state_error = this.optimize(mutation.get_state_error(), contexts);
			return this.new_mutation(constraint, state_error);
		}
	}
	
	/* constraint reduction and optimization */
	/**
	 * divide the conditions into automated and normalized format and preserve into the buffer of constraints collection
	 * @param execution
	 * @param condition
	 * @param constraints
	 * @throws Exception
	 */
	private boolean divide_conditions(CirExecution execution, SymExpression condition, Collection<SymConstraint> constraints) throws Exception {
		if(condition instanceof SymConstant) {
			if(((SymConstant) condition).get_bool()) {
				return true;
			}
			else {
				constraints.clear();
				constraints.add((SymConstraint) this.get_unique_instance(new SymConstraint(execution, SymFactory.sym_constant(Boolean.FALSE))));
				return false;
			}
		}
		else if(condition instanceof SymBinaryExpression) {
			if(((SymBinaryExpression) condition).get_operator().get_operator() == COperator.logic_and) {
				if(this.divide_conditions(execution, ((SymBinaryExpression) condition).get_loperand(), constraints)) {
					if(this.divide_conditions(execution, ((SymBinaryExpression) condition).get_roperand(), constraints)) {
						return true;
					}
					else {
						return false;
					}
				}
				else {
					return false;
				}
			}
			else {
				constraints.add((SymConstraint) this.get_unique_instance(new SymConstraint(execution, condition)));
				return true;
			}
		}
		else {
			constraints.add((SymConstraint) this.get_unique_instance(new SymConstraint(execution, condition)));
			return true;
		}
	}
	/**
	 * @param constraint
	 * @return divide the conditions in the constraint into automated format and preserve into buffer
	 * @throws Exception
	 */
	private Collection<SymConstraint> divide_constraint(SymConstraint constraint) throws Exception {
		Set<SymConstraint> constraints = new HashSet<SymConstraint>();
		constraints.add((SymConstraint) this.get_unique_instance(this.expression_constraint(constraint.get_statement(), Boolean.TRUE, true)));
		this.divide_conditions(constraint.get_execution(), constraint.get_condition(), constraints);
		return constraints;
	}
	
	
}
