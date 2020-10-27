package com.jcsa.jcmutest.mutant.cir2mutant;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirExpressionError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirReferenceError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirStateError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirStateValueError;
import com.jcsa.jcmutest.mutant.cir2mutant.graph.CirMutationFlow;
import com.jcsa.jcmutest.mutant.cir2mutant.graph.CirMutationResult;
import com.jcsa.jcmutest.mutant.cir2mutant.graph.CirMutationTreeNode;
import com.jcsa.jcmutest.mutant.cir2mutant.pgate.CirAddressOfPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.pgate.CirArgumentListPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.pgate.CirArithAddPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.pgate.CirArithDivPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.pgate.CirArithModPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.pgate.CirArithMulPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.pgate.CirArithNegPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.pgate.CirArithSubPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.pgate.CirAssignPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.pgate.CirBitwsAndPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.pgate.CirBitwsIorPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.pgate.CirBitwsLshPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.pgate.CirBitwsRshPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.pgate.CirBitwsRsvPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.pgate.CirBitwsXorPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.pgate.CirDereferencePropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.pgate.CirEqualWithPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.pgate.CirErrorPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.pgate.CirFieldOfPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.pgate.CirGreaterEqPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.pgate.CirGreaterTnPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.pgate.CirInitializerPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.pgate.CirLogicAndPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.pgate.CirLogicIorPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.pgate.CirLogicNotPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.pgate.CirNotEqualsPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.pgate.CirSmallerEqPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.pgate.CirSmallerTnPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.pgate.CirTypeCastPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.pgate.CirWaitValuePropagator;
import com.jcsa.jcparse.flwa.depend.CDependEdge;
import com.jcsa.jcparse.flwa.depend.CDependGraph;
import com.jcsa.jcparse.flwa.depend.CDependNode;
import com.jcsa.jcparse.flwa.depend.CDependPredicate;
import com.jcsa.jcparse.flwa.depend.CDependReference;
import com.jcsa.jcparse.flwa.depend.CDependType;
import com.jcsa.jcparse.flwa.graph.CirInstanceGraph;
import com.jcsa.jcparse.flwa.graph.CirInstanceNode;
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
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.stmt.CirArgumentList;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCallStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirReturnAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.sym.SymExpression;

/**
 * It implements the algorithms to build up the error propagation tree and graph.
 * 
 * @author yukimula
 *
 */
public class CirMutationUtils {
	
	/* path constraints for reaching the statement node */
	/**
	 * @param instance
	 * @param dependence_graph
	 * @param cir_mutations
	 * @return the path constraints required for reaching the instance
	 * 	       of target statement during testing.
	 * @throws Exception
	 */
	private static Set<CirConstraint> get_path_constraint(
			CirInstanceNode instance, CDependGraph dependence_graph,
			CirMutations cir_mutations) throws Exception {
		if(instance == null)
			throw new IllegalArgumentException("Invalid instance: null");
		else {
			Set<CirConstraint> constraints = new HashSet<CirConstraint>();
			if(dependence_graph.has_node(instance)) {
				CDependNode prev = dependence_graph.get_node(instance), next;
				while(prev != null) {
					next = null;
					
					for(CDependEdge edge : prev.get_ou_edges()) {
						/* (conditional_statement, condition, true|false) */
						if(edge.get_type() == CDependType.predicate_depend) {
							CDependPredicate element = (CDependPredicate) edge.get_element();
							next = edge.get_target();
							CirStatement condition_statement = next.get_statement();
							CirExpression condition = element.get_condition();
							if(element.get_predicate_value()) {
								constraints.add(cir_mutations.expression_constraint(condition_statement, condition, true));
							}
							else {
								constraints.add(cir_mutations.expression_constraint(condition_statement, condition, false));
							}
						}
						/* (wait_statement, True, true) --> as reaching statement */
						else if(edge.get_type() == CDependType.stmt_exit_depend) {
							if(prev != edge.get_target()) {
								next = edge.get_target();
								CirStatement statement = next.get_statement();
								constraints.add(cir_mutations.expression_constraint(statement, Boolean.TRUE, true));
							}
						}
						/* (call_statement, True, true) --> as reaching statement */
						else if(edge.get_type() == CDependType.stmt_call_depend) {
							next = edge.get_target();
							CirStatement statement = next.get_statement();
							constraints.add(cir_mutations.expression_constraint(statement, Boolean.TRUE, true));
						}
						
						if(next != null) break;	/* find the next dependence node */
					}
					
					prev = next;
				}
			}
			return constraints;
		}
	}
	/**
	 * @param statement
	 * @param dependence_graph
	 * @param cir_mutations
	 * @return the constraints that are commonly required for reaching the statement in all-possible paths.
	 * @throws Exception
	 */
	public static Set<CirConstraint> get_common_path_constraints(CirStatement statement, 
			CDependGraph dependence_graph, CirMutations cir_mutations) throws Exception {
		if(statement == null)
			throw new IllegalArgumentException("Invalid statement: null");
		else if(dependence_graph == null)
			throw new IllegalArgumentException("Invalid dependence_graph: null");
		else if(cir_mutations == null)
			throw new IllegalArgumentException("Invalid cir_mutations: null");
		else {
			Set<CirConstraint> common_constraints = new HashSet<CirConstraint>(), local_constraints;
			CirExecution execution = statement.get_tree().get_localizer().get_execution(statement);
			CirInstanceGraph instance_graph = dependence_graph.get_program_graph(); boolean first = true;
			Set<CirConstraint> removed_constraints = new HashSet<CirConstraint>();
			
			if(instance_graph.has_instances_of(execution)) {
				for(CirInstanceNode instance : instance_graph.get_instances_of(execution)) {
					local_constraints = get_path_constraint(instance, dependence_graph, cir_mutations);
					
					if(first) {
						first = false;
						common_constraints.addAll(local_constraints);
					}
					else {
						removed_constraints.clear();
						for(CirConstraint constraint : common_constraints) {
							if(!local_constraints.contains(constraint)) {
								removed_constraints.add(constraint);
							}
						}
						common_constraints.removeAll(removed_constraints);
					}
				}
			}
			
			return common_constraints;
		}
	}
	
	/* error propagation generation in local statement */
	private static final Map<COperator, CirErrorPropagator> propagators = new HashMap<COperator, CirErrorPropagator>();
	private static final Map<CirStateError, CirConstraint> propagations = new HashMap<CirStateError, CirConstraint>();	
	static {
		propagators.put(COperator.negative, 	new CirArithNegPropagator());
		propagators.put(COperator.bit_not, 		new CirBitwsRsvPropagator());
		propagators.put(COperator.logic_not, 	new CirLogicNotPropagator());
		propagators.put(COperator.address_of, 	new CirAddressOfPropagator());
		propagators.put(COperator.dereference, 	new CirDereferencePropagator());
		
		propagators.put(COperator.arith_add, 	new CirArithAddPropagator());
		propagators.put(COperator.arith_sub, 	new CirArithSubPropagator());
		propagators.put(COperator.arith_mul, 	new CirArithMulPropagator());
		propagators.put(COperator.arith_div, 	new CirArithDivPropagator());
		propagators.put(COperator.arith_mod, 	new CirArithModPropagator());
		
		propagators.put(COperator.bit_and, 		new CirBitwsAndPropagator());
		propagators.put(COperator.bit_or, 		new CirBitwsIorPropagator());
		propagators.put(COperator.bit_xor, 		new CirBitwsXorPropagator());
		propagators.put(COperator.left_shift, 	new CirBitwsLshPropagator());
		propagators.put(COperator.righ_shift, 	new CirBitwsRshPropagator());
		
		propagators.put(COperator.logic_and, 	new CirLogicAndPropagator());
		propagators.put(COperator.logic_or, 	new CirLogicIorPropagator());
		
		propagators.put(COperator.greater_tn, 	new CirGreaterTnPropagator());
		propagators.put(COperator.greater_eq, 	new CirGreaterEqPropagator());
		propagators.put(COperator.smaller_tn, 	new CirSmallerTnPropagator());
		propagators.put(COperator.smaller_eq, 	new CirSmallerEqPropagator());
		propagators.put(COperator.equal_with, 	new CirEqualWithPropagator());
		propagators.put(COperator.not_equals, 	new CirNotEqualsPropagator());
		
		/* special case */
		propagators.put(COperator.assign, 		new CirAssignPropagator());
		propagators.put(COperator.arith_add_assign, new CirTypeCastPropagator());
		propagators.put(COperator.arith_sub_assign, new CirInitializerPropagator());
		propagators.put(COperator.arith_mul_assign, new CirFieldOfPropagator());
		propagators.put(COperator.arith_div_assign, new CirWaitValuePropagator());
		propagators.put(COperator.arith_mod_assign, new CirArgumentListPropagator());
	}
	/**
	 * @param cir_mutations
	 * @param source_mutation
	 * @return the set of state errors propagete from the source mutation in one iteration within local statement
	 * @throws Exception
	 */
	public static Iterable<CirMutation> local_propagate(CirMutations 
			cir_mutations, CirMutation source_mutation) throws Exception {
		if(cir_mutations == null)
			throw new IllegalArgumentException("Invalid cir_mutations: null");
		else if(source_mutation == null)
			throw new IllegalArgumentException("Invalid source_mutation: null");
		else {
			/* 1. declarations */
			CirStateError state_error = source_mutation.get_state_error();
			CirNode source_location, target_location; 
			CirErrorPropagator propagator;
			List<CirMutation> results = new LinkedList<CirMutation>();
			
			/* 2. determine the next location for propagation to occur */
			if(state_error instanceof CirExpressionError) {
				source_location = ((CirExpressionError) state_error).get_expression();
			}
			else if(state_error instanceof CirReferenceError) {
				source_location = ((CirReferenceError) state_error).get_reference();
			}
			else {
				source_location = null; target_location = null; return results;
			}
			target_location = source_location.get_parent();
			
			/* 3. obtain the error propagator algorithm for analysis */
			if(target_location instanceof CirDeferExpression) {
				propagator = propagators.get(COperator.dereference);
			}
			else if(target_location instanceof CirFieldExpression) {
				propagator = propagators.get(COperator.arith_mul_assign);
			}
			else if(target_location instanceof CirAddressExpression) {
				propagator = propagators.get(COperator.address_of);
			}
			else if(target_location instanceof CirCastExpression) {
				propagator = propagators.get(COperator.arith_add_assign);
			}
			else if(target_location instanceof CirComputeExpression) {
				propagator = propagators.get(
						((CirComputeExpression) target_location).get_operator());
			}
			else if(target_location instanceof CirInitializerBody) {
				propagator = propagators.get(COperator.arith_sub_assign);
			}
			else if(target_location instanceof CirWaitExpression) {
				propagator = propagators.get(COperator.arith_div_assign);
			}
			else if(target_location instanceof CirArgumentList) {
				propagator = propagators.get(COperator.arith_mod_assign);
			}
			else if(target_location instanceof CirAssignStatement) {
				propagator = propagators.get(COperator.assign);
			}
			else { /* ignore the propagation */ propagator = null; }
			
			/* 4. generate the next iteration from the source error */
			propagations.clear();
			if(propagator != null) {
				propagator.propagate(cir_mutations, 
						state_error, source_location, target_location, propagations);
			}
			
			/* 5. generate the next generation of state errors */
			for(CirStateError target_error : propagations.keySet()) {
				CirConstraint constraint = propagations.get(target_error);
				results.add(cir_mutations.new_mutation(constraint, target_error));
			}
			return results;
		}
	}
	
	/* global error propagation algorithms */
	/**
	 * @param source_error
	 * @return whether it is the conditional error
	 * @throws Exception
	 */
	private static boolean is_condition_error(CirStateError source_error) throws Exception {
		CirExpression condition;
		CirStatement source_statement = source_error.get_statement();
		if(source_error instanceof CirExpressionError) {
			condition = ((CirExpressionError) source_error).get_expression();
			if(source_statement instanceof CirIfStatement) {
				return ((CirIfStatement) source_statement).get_condition() == condition;
			}
			else if(source_statement instanceof CirCaseStatement) {
				return ((CirCaseStatement) source_statement).get_condition() == condition;
			}
			else {
				return false;
			}
		}
		else if(source_error instanceof CirReferenceError) {
			condition = ((CirReferenceError) source_error).get_reference();
			if(source_statement instanceof CirIfStatement) {
				return ((CirIfStatement) source_statement).get_condition() == condition;
			}
			else if(source_statement instanceof CirCaseStatement) {
				return ((CirCaseStatement) source_statement).get_condition() == condition;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}
	/**
	 * perform the error propagation from condition to its statement
	 * @param source_error
	 * @param dependence_graph
	 * @param cir_mutations
	 * @throws Exception
	 */
	private static void condition_propagate(CirStateError source_error,
			CDependGraph dependence_graph, CirMutations cir_mutations,
			Map<CirMutation, CirMutationFlow> results) throws Exception {
		/* 1. get the source condition and its statement */
		SymExpression mutation_value; 
		CirStatement source_statement = source_error.get_statement();
		if(source_error instanceof CirExpressionError) {
			mutation_value = ((CirExpressionError) source_error).get_mutation_value();
		}
		else if(source_error instanceof CirReferenceError) {
			mutation_value = ((CirReferenceError) source_error).get_mutation_value();
		}
		else {
			throw new IllegalArgumentException(source_error.toString());
		}
		
		/* 3. construct the constraint for reaching two branches */
		CirConstraint true_constraint, false_constraint;
		true_constraint = cir_mutations.expression_constraint(
						source_statement, mutation_value, true);
		false_constraint = cir_mutations.expression_constraint(
						source_statement, mutation_value, false);
		
		/* 4. collect the statements in true and false branch */
		CirExecution condition_execution = source_statement.get_tree().
						get_localizer().get_execution(source_statement);
		CirExecutionFlow true_flow = null, false_flow = null;
		for(CirExecutionFlow flow : condition_execution.get_ou_flows()) {
			switch(flow.get_type()) {
			case true_flow:	true_flow = flow; 	break;
			case fals_flow: false_flow = flow;	break;
			default: break;
			}
		}
		
		/* 5. generate the flow errors in next stop */
		results.put(cir_mutations.new_mutation(true_constraint, cir_mutations.
				flow_error(false_flow, true_flow)), CirMutationFlow.control_influence);
		results.put(cir_mutations.new_mutation(false_constraint, cir_mutations.
				flow_error(true_flow, false_flow)), CirMutationFlow.control_influence);
	}
	/**
	 * @param source_error
	 * @return whether it is the expression error in an argument.
	 * @throws Exception
	 */
	private static boolean is_argument_error(CirStateError source_error) throws Exception {
		CirStatement statement = source_error.get_statement();
		if(source_error instanceof CirExpressionError) {
			CirExpression expression = ((CirExpressionError) source_error).get_expression();
			if(statement instanceof CirCallStatement) {
				CirNode parent = expression.get_parent();
				return parent instanceof CirArgumentList;
			}
			else {
				return false;
			}
		}
		else if(source_error instanceof CirReferenceError) {
			CirExpression expression = ((CirReferenceError) source_error).get_reference();
			if(statement instanceof CirCallStatement) {
				CirNode parent = expression.get_parent();
				return parent instanceof CirArgumentList;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}
	/**
	 * @param source_error
	 * @throws Exception
	 */
	private static void argument_propagate(CirStateError source_error,
			CDependGraph dependence_graph, CirMutations cir_mutations,
			Map<CirMutation, CirMutationFlow> results) throws Exception {
		/* 1. get the argument and its  */
		SymExpression mutation_value; CirExpression expression; 
		CirStatement statement = source_error.get_statement();
		if(source_error instanceof CirExpressionError) {
			expression = ((CirExpressionError) source_error).get_expression();
			mutation_value = ((CirExpressionError) source_error).get_mutation_value();
		}
		else if(source_error instanceof CirReferenceError) {
			expression = ((CirReferenceError) source_error).get_reference();
			mutation_value = ((CirReferenceError) source_error).get_mutation_value();
		}
		else {
			throw new IllegalArgumentException(source_error.toString());
		}
		
		/* 2. find the usage expression in callee function */
		CirExecution execution = statement.get_tree().get_localizer().get_execution(statement);
		Set<CirExpression> parameters = new HashSet<CirExpression>();
		if(dependence_graph.get_program_graph().has_instances_of(execution)) {
			for(CirInstanceNode instance : dependence_graph.get_program_graph().get_instances_of(execution)) {
				if(dependence_graph.has_node(instance)) {
					CDependNode dependence_node = dependence_graph.get_node(instance);
					for(CDependEdge dependence_edge : dependence_node.get_in_edges()) {
						if(dependence_edge.get_type() == CDependType.param_arg_depend) {
							CDependReference element = (CDependReference) dependence_edge.get_element();
							if(element.get_def() == expression) {
								parameters.add(element.get_use());
							}
						}
					}
				}
			}
		}
		
		/* 3. generate the argument-parameter propagation */
		for(CirExpression parameter : parameters) {
			CirConstraint constraint = cir_mutations.expression_constraint(statement, Boolean.TRUE, true);
			CirStateError state_error = cir_mutations.expr_error(parameter, mutation_value);
			results.put(cir_mutations.new_mutation(constraint, state_error), CirMutationFlow.data_pass_influence);
		}
	}
	/**
	 * @param source_error
	 * @return whether the error is in return-point
	 * @throws Exception
	 */
	private static boolean is_returning_error(CirStateError source_error) throws Exception {
		if(source_error instanceof CirStateValueError) {
			CirStatement statement = source_error.get_statement();
			CirExpression expression = ((CirStateValueError) source_error).get_reference();
			if(statement instanceof CirReturnAssignStatement) {
				return ((CirReturnAssignStatement) statement).get_lvalue() == expression;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}
	/**
	 * @param source_error
	 * @param dependence_graph
	 * @param cir_mutations
	 * @param results
	 * @throws Exception
	 */
	private static void returning_propagate(CirStateError source_error,
			CDependGraph dependence_graph, CirMutations cir_mutations,
			Map<CirMutation, CirMutationFlow> results) throws Exception {
		/* 1. get the return-definition and its statement */
		SymExpression mutation_value; CirExpression expression; 
		CirStatement statement = source_error.get_statement();
		if(source_error instanceof CirStateValueError) {
			expression = ((CirStateValueError) source_error).get_reference();
			mutation_value = ((CirStateValueError) source_error).get_mutation_value();
		}
		else {
			throw new IllegalArgumentException(source_error.toString());
		}
		
		/* 2. collect the wait-expression using return-value */
		Set<CirExpression> use_set = new HashSet<CirExpression>();
		CirExecution execution = statement.get_tree().get_localizer().get_execution(statement);
		if(dependence_graph.get_program_graph().has_instances_of(execution)) {
			for(CirInstanceNode instance : dependence_graph.get_program_graph().get_instances_of(execution)) {
				if(dependence_graph.has_node(instance)) {
					CDependNode dependence_node = dependence_graph.get_node(instance);
					for(CDependEdge dependence_edge : dependence_node.get_in_edges()) {
						if(dependence_edge.get_type() == CDependType.wait_retr_depend) {
							CDependReference element = (CDependReference) dependence_edge.get_element();
							if(element.get_def() == expression) {
								use_set.add(element.get_use());
							}
						}
					}
				}
			}
		}
		
		/* 3. generate the return-wait propagation */
		for(CirExpression use_expression : use_set) {
			CirConstraint constraint = cir_mutations.expression_constraint(use_expression.statement_of(), Boolean.TRUE, true);
			CirStateError state_error = cir_mutations.expr_error(use_expression, mutation_value);
			results.put(cir_mutations.new_mutation(constraint, state_error), CirMutationFlow.use_define_influence);
		}
	}
	/**
	 * @param source_error
	 */
	private static boolean is_definition_error(CirStateError source_error) {
		return source_error instanceof CirStateValueError;
	}
	/**
	 * @param source_error
	 * @param dependence_graph
	 * @param cir_mutations
	 * @param results
	 * @throws Exception
	 */
	private static void definition_propagate(CirStateError source_error,
			CDependGraph dependence_graph, CirMutations cir_mutations,
			Map<CirMutation, CirMutationFlow> results) throws Exception {
		/* 1. get the return-definition and its statement */
		SymExpression mutation_value; CirExpression expression; 
		CirStatement statement = source_error.get_statement();
		if(source_error instanceof CirStateValueError) {
			expression = ((CirStateValueError) source_error).get_reference();
			mutation_value = ((CirStateValueError) source_error).get_mutation_value();
		}
		else {
			throw new IllegalArgumentException(source_error.toString());
		}
		
		/* 2. collect the usage_definition set */
		Set<CirExpression> use_set = new HashSet<CirExpression>();
		CirExecution execution = statement.get_tree().get_localizer().get_execution(statement);
		if(dependence_graph.get_program_graph().has_instances_of(execution)) {
			for(CirInstanceNode instance : dependence_graph.get_program_graph().get_instances_of(execution)) {
				if(dependence_graph.has_node(instance)) {
					CDependNode dependence_node = dependence_graph.get_node(instance);
					for(CDependEdge dependence_edge : dependence_node.get_in_edges()) {
						if(dependence_edge.get_type() == CDependType.use_defin_depend) {
							CDependReference element = (CDependReference) dependence_edge.get_element();
							if(element.get_def() == expression) {
								use_set.add(element.get_use());
							}
						}
					}
				}
			}
		}
		
		/* 3. generate the return-wait propagation */
		for(CirExpression use_expression : use_set) {
			CirConstraint constraint = cir_mutations.expression_constraint(use_expression.statement_of(), Boolean.TRUE, true);
			CirStateError state_error = cir_mutations.expr_error(use_expression, mutation_value);
			results.put(cir_mutations.new_mutation(constraint, state_error), CirMutationFlow.use_define_influence);
		}
	}
	/**
	 * build up the connection between errors in different statement
	 * @return mapping from target mutation to the edge type that connects it from source mutation
	 * @throws Exception
	 */
	public static Map<CirMutation, CirMutationFlow> global_propagate(
			CirMutation source_mutation, CDependGraph
			dependence_graph, CirMutations cir_mutations) throws Exception {
		if(cir_mutations == null)
			throw new IllegalArgumentException("Invalid cir_mutations: null");
		else if(source_mutation == null)
			throw new IllegalArgumentException("Invalid source_mutation: null");
		else if(dependence_graph == null)
			throw new IllegalArgumentException("Invalid dependence_graph as null");
		else {
			Map<CirMutation, CirMutationFlow> results = new HashMap<CirMutation, CirMutationFlow>();
			
			if(is_condition_error(source_mutation.get_state_error())) {
				condition_propagate(source_mutation.get_state_error(), dependence_graph, cir_mutations, results);
			}
			else if(is_argument_error(source_mutation.get_state_error())) {
				argument_propagate(source_mutation.get_state_error(), dependence_graph, cir_mutations, results);
			}
			else if(is_returning_error(source_mutation.get_state_error())) {
				returning_propagate(source_mutation.get_state_error(), dependence_graph, cir_mutations, results);
			}
			else if(is_definition_error(source_mutation.get_state_error())) {
				definition_propagate(source_mutation.get_state_error(), dependence_graph, cir_mutations, results);
			}
			
			return results;
		}
	}
	
	/* feature selection algorithms */
	/**
	 * @param mutation_result_map
	 * @return mapping from constraint or state error to whether they are satisfied (null as unknown)
	 * @throws Exception
	 */
	public static Map<Object, Boolean> select_features(Map<CirMutationTreeNode, CirMutationResult> mutation_result_map) throws Exception {
		Map<Object, Boolean> results = new HashMap<Object, Boolean>();
		
		for(CirMutationTreeNode tree_node : mutation_result_map.keySet()) {
			/* 1. get the test result summary of the tree node */
			CirMutationResult tree_node_result = mutation_result_map.get(tree_node);
			
			/* 2. determine the path constraints for reaching the statement */
			if(tree_node.is_root()) {
				boolean reached = tree_node_result.is_executed();
				for(CirConstraint constraint : tree_node.get_tree().
						get_statement_node().get_path_constraints()) {
					results.put(constraint, Boolean.valueOf(reached));
				}
			}
			
			/* 3. determine the satisfiability of constraint & state-error */
			if(tree_node_result.is_executed()) {
				CirConstraint constraint = tree_node.get_cir_mutation().get_constraint();
				if(tree_node_result.get_constraint_acceptions() > 0) {
					results.put(constraint, Boolean.TRUE);
				}
				else if(tree_node_result.get_constraint_rejections() > 0) {
					results.put(constraint, Boolean.FALSE);
				}
				else {
					results.put(constraint, null);
				}
				
				CirStateError state_error = tree_node.get_cir_mutation().get_state_error();
				if(tree_node_result.get_state_error_acceptions() > 0) {
					results.put(state_error, Boolean.TRUE);
				}
				else if(tree_node_result.get_state_error_ignorances() > 0) {
					results.put(state_error, Boolean.FALSE);
				}
				else {
					results.put(state_error, null);
				}
			}
		}
		
		return results;
	}
	
}
