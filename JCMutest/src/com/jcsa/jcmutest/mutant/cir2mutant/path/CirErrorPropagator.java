package com.jcsa.jcmutest.mutant.cir2mutant.path;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jcsa.jcmutest.mutant.cir2mutant.model.CirConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.model.CirExpressionError;
import com.jcsa.jcmutest.mutant.cir2mutant.model.CirMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.model.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.model.CirReferenceError;
import com.jcsa.jcmutest.mutant.cir2mutant.model.CirStateError;
import com.jcsa.jcmutest.mutant.cir2mutant.model.CirStateValueError;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirAddressExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirCastExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirComputeExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirDeferExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirFieldExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirInitializerBody;
import com.jcsa.jcparse.lang.irlang.expr.CirWaitExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirArgumentList;
import com.jcsa.jcparse.lang.irlang.stmt.CirCallStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;
import com.jcsa.jcparse.test.state.CStateContexts;

/**
 * It implements the propagation via errors in the same statement.
 * 
 * @author yukimula
 *
 */
public class CirErrorPropagator {
	
	/* definitions */
	/** the library to generate cir-mutations **/
	private CirMutations cir_mutations;
	/** statement in which propagation occurs **/
	private CirStatement statement;
	/** the state error that causes the other **/
	private CirStateError source_error;
	/** mapping from new error to its constraint **/
	private Map<CirStateError, CirConstraint> propagations;
	
	/* constructor */
	private CirErrorPropagator() {
		this.propagations = new HashMap<CirStateError, CirConstraint>();
	}
	private CirErrorPropagator propagator = new CirErrorPropagator();
	
	/* propagation methods */
	/**
	 * @param cir_mutations
	 * @param mutation
	 * @param contexts
	 * @return generate the next errors from source error
	 * @throws Exception
	 */
	private Iterable<CirMutation> propagate(CirMutations cir_mutations,
			CirMutation mutation, CStateContexts contexts) throws Exception {
		if(cir_mutations == null)
			throw new IllegalArgumentException("Invalid cir_mutations: null");
		else if(mutation == null)
			throw new IllegalArgumentException("Invalid mutation: null");
		else {
			List<CirMutation> mutations = new ArrayList<CirMutation>();
			mutation = cir_mutations.optimize(mutation, contexts);
			if(mutation != null) {
				/* 1. initialization */
				this.cir_mutations = cir_mutations;
				this.source_error = mutation.get_state_error();
				this.statement = this.source_error.get_statement();
				
				/* 2. determine the next location to propagate */
				CirExpression source_location; 
				CirNode target_location;
				SymExpression muta_operand;
				if(source_error instanceof CirExpressionError) {
					source_location = ((CirExpressionError) source_error).get_expression();
					target_location = ((CirExpressionError) source_error).get_expression().get_parent();
					muta_operand = ((CirExpressionError) source_error).get_mutation_value();
				}
				else if(source_error instanceof CirReferenceError) {
					source_location = ((CirReferenceError) source_error).get_reference();
					target_location = ((CirReferenceError) source_error).get_reference().get_parent();
					muta_operand = ((CirReferenceError) source_error).get_mutation_value();
				}
				else {
					source_location = null; target_location = null; muta_operand = null;
				}
				
				/* 3. perform the propagation algorithms */
				this.propagations.clear();
				if(target_location != null) {
					/* TODO implement syntax-directed translation */
				}
				
				/* 4. construct the error being generated */
				for(CirStateError state_error : this.propagations.keySet()) {
					CirConstraint constraint = this.propagations.get(state_error);
					CirMutation cir_mutation = this.cir_mutations.
							new_mutation(this.statement, constraint, state_error);
					cir_mutation = cir_mutations.optimize(cir_mutation, contexts);
					if(cir_mutation != null) { mutations.add(cir_mutation); }
				}
			}
			return mutations;
		}
	}
	
	/* syntax-directed translation */
	private void propagate_defer_expression(CirExpression source_location,
			CirDeferExpression target_location, SymExpression muta_operand) throws Exception {
		SymExpression muta_value = SymFactory.
				dereference(target_location.get_data_type(), muta_operand);
		CirConstraint constraint = this.cir_mutations.
					expression_constraint(statement, Boolean.TRUE, true);
		CirStateError state_error = this.cir_mutations.refer_error(target_location, muta_value);
		this.propagations.put(state_error, constraint);
	}
	private void propagate_field_expression(CirExpression source_location,
			CirFieldExpression target_location, SymExpression muta_operand) throws Exception {
		SymExpression muta_value = SymFactory.field_expression(target_location.
				get_data_type(), muta_operand, target_location.get_field().get_name());
		CirConstraint constraint = this.cir_mutations.
				expression_constraint(statement, Boolean.TRUE, true);
		
		CirStateError state_error;
		if(this.source_error instanceof CirExpressionError) {
			state_error = this.cir_mutations.expr_error(target_location, muta_value);
		}
		else {
			state_error = this.cir_mutations.refer_error(target_location, muta_value);
		}
		this.propagations.put(state_error, constraint);
	}
	private void propagate_address_expression(CirExpression source_location,
			CirAddressExpression target_location, SymExpression muta_operand) throws Exception {
		if(this.source_error instanceof CirReferenceError) {
			SymExpression muta_value = SymFactory.
					address_of(target_location.get_data_type(), muta_operand);
			CirConstraint constraint = this.cir_mutations.
						expression_constraint(statement, Boolean.TRUE, true);
			CirStateError state_error = this.
						cir_mutations.expr_error(target_location, muta_value);
			this.propagations.put(state_error, constraint);
		}
	}
	private void propagate_cast_expression(CirExpression source_location,
			CirCastExpression target_location, SymExpression muta_operand) throws Exception {
		SymExpression muta_value = SymFactory.
				type_cast(target_location.get_data_type(), muta_operand);
		CirConstraint constraint = this.cir_mutations.
					expression_constraint(statement, Boolean.TRUE, true);
		CirStateError state_error = this.
					cir_mutations.expr_error(target_location, muta_value);
		this.propagations.put(state_error, constraint);
	}
	private void propagate_initializer_body(CirExpression source_location,
			CirInitializerBody target_location, SymExpression muta_operand) throws Exception {
		List<Object> operands = new ArrayList<Object>();
		for(int k = 0; k < target_location.number_of_elements(); k++) {
			if(target_location.get_element(k) == source_location) {
				operands.add(muta_operand);
			}
			else {
				operands.add(target_location.get_element(k));
			}
		}
		SymExpression muta_value = SymFactory.
				initializer_list(target_location.get_data_type(), operands);
		CirConstraint constraint = this.cir_mutations.
				expression_constraint(statement, Boolean.TRUE, true);
		CirStateError state_error = this.
				cir_mutations.expr_error(target_location, muta_value);
		this.propagations.put(state_error, constraint);
	}
	private void propagate_wait_expression(CirExpression source_location,
			CirWaitExpression target_location, SymExpression muta_operand) throws Exception {
		/* obtain call-statement */
		CirStatement wait_statement = target_location.statement_of();
		CirExecution wait_execution = 
				cir_mutations.get_cir_tree().get_localizer().get_execution(wait_statement);
		CirExecution call_execution = 
					wait_execution.get_graph().get_execution(wait_execution.get_id() - 1);
		CirCallStatement call_statement = (CirCallStatement) call_execution.get_statement();
		
		/* Generate mutation value */
		List<Object> arguments = new ArrayList<Object>();
		CirArgumentList alist = call_statement.get_arguments();
		for(int k = 0; k < alist.number_of_arguments(); k++) 
			arguments.add(alist.get_argument(k));
		SymExpression muta_value = SymFactory.call_expression(
				target_location.get_data_type(), muta_operand, arguments);
		
		/* construct the constraint-error propagation */
		CirConstraint constraint = this.cir_mutations.
				expression_constraint(statement, Boolean.TRUE, true);
		CirStateError state_error = this.
				cir_mutations.expr_error(target_location, muta_value);
		this.propagations.put(state_error, constraint);
	}
	private void propagate_arith_neg(CirExpression source_location,
			CirComputeExpression target_location, 
			SymExpression muta_operand) throws Exception {
		SymExpression muta_value = SymFactory.arith_neg(
				target_location.get_data_type(), muta_operand);
		CirConstraint constraint = this.cir_mutations.
				expression_constraint(statement, Boolean.TRUE, true);
		CirStateError state_error = this.
				cir_mutations.expr_error(target_location, muta_value);
		this.propagations.put(state_error, constraint);
	}
	private void propagate_bitws_rsv(CirExpression source_location,
			CirComputeExpression target_location, 
			SymExpression muta_operand) throws Exception {
		SymExpression muta_value = SymFactory.bitws_rsv(
				target_location.get_data_type(), muta_operand);
		CirConstraint constraint = this.cir_mutations.
				expression_constraint(statement, Boolean.TRUE, true);
		CirStateError state_error = this.
				cir_mutations.expr_error(target_location, muta_value);
		this.propagations.put(state_error, constraint);
	}
	private void propagate_logic_not(CirExpression source_location,
			CirComputeExpression target_location, 
			SymExpression muta_operand) throws Exception {
		SymExpression muta_value = SymFactory.logic_not(muta_operand);
		CirConstraint constraint = this.cir_mutations.
				expression_constraint(statement, Boolean.TRUE, true);
		CirStateError state_error = this.
				cir_mutations.expr_error(target_location, muta_value);
		this.propagations.put(state_error, constraint);
	}
	private void propagate_arith_add(CirExpression source_location,
			CirComputeExpression target_location, 
			SymExpression muta_operand) throws Exception {
		SymExpression muta_value;
		if(source_location == target_location.get_operand(0)) {
			muta_value = SymFactory.arith_add(target_location.get_data_type(), 
					muta_operand, target_location.get_operand(1));
		}
		else {
			muta_value = SymFactory.arith_add(target_location.get_data_type(), 
					target_location.get_operand(0), muta_operand);
		}
		CirConstraint constraint = this.cir_mutations.
				expression_constraint(statement, Boolean.TRUE, true);
		CirStateError state_error = this.
				cir_mutations.expr_error(target_location, muta_value);
		this.propagations.put(state_error, constraint);
	}
	private void propagate_arith_sub(CirExpression source_location,
			CirComputeExpression target_location, 
			SymExpression muta_operand) throws Exception {
		SymExpression muta_value;
		if(source_location == target_location.get_operand(0)) {
			muta_value = SymFactory.arith_sub(target_location.get_data_type(), 
					muta_operand, target_location.get_operand(1));
		}
		else {
			muta_value = SymFactory.arith_sub(target_location.get_data_type(), 
					target_location.get_operand(0), muta_operand);
		}
		CirConstraint constraint = this.cir_mutations.
				expression_constraint(statement, Boolean.TRUE, true);
		CirStateError state_error = this.
				cir_mutations.expr_error(target_location, muta_value);
		this.propagations.put(state_error, constraint);
	}
	private void propagate_arith_mul(CirExpression source_location,
			CirComputeExpression target_location, 
			SymExpression muta_operand) throws Exception {
		SymExpression muta_value; CirConstraint constraint;
		if(source_location == target_location.get_operand(0)) {
			constraint = this.cir_mutations.expression_constraint(statement, SymFactory.
					not_equals(target_location.get_operand(1), Integer.valueOf(0)), true);
			muta_value = SymFactory.arith_mul(target_location.get_data_type(), 
					muta_operand, target_location.get_operand(1));
		}
		else {
			constraint = this.cir_mutations.expression_constraint(statement, SymFactory.
					not_equals(target_location.get_operand(0), Integer.valueOf(0)), true);
			muta_value = SymFactory.arith_mul(target_location.get_data_type(), 
					muta_operand, target_location.get_operand(0));
		}
		CirStateError state_error = this.
				cir_mutations.expr_error(target_location, muta_value);
		this.propagations.put(state_error, constraint);
	}
	private void propagate_arith_div(CirExpression source_location,
			CirComputeExpression target_location, 
			SymExpression muta_operand) throws Exception {
		SymExpression muta_value; CirConstraint constraint; CirStateError state_error;
		if(source_location == target_location.get_operand(0)) {
			muta_value = SymFactory.arith_div(target_location.get_data_type(), 
					muta_operand, target_location.get_operand(1));
			constraint = this.cir_mutations.
					expression_constraint(statement, Boolean.TRUE, true);
			state_error = this.
					cir_mutations.expr_error(target_location, muta_value);
			this.propagations.put(state_error, constraint);
		}
		else {
			constraint = this.cir_mutations.expression_constraint(statement, 
					SymFactory.not_equals(target_location.get_data_type(), 
							target_location.get_operand(0)), true);
			muta_value = SymFactory.arith_div(target_location.get_data_type(), 
					target_location.get_operand(1), muta_operand);
			state_error = this.
					cir_mutations.expr_error(target_location, muta_value);
			this.propagations.put(state_error, constraint);
		}
	}
	
	
	
	
}
