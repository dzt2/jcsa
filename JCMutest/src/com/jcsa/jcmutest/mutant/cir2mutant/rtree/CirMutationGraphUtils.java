package com.jcsa.jcmutest.mutant.cir2mutant.rtree;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirExpressionError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirReferenceError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirStateError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirStateValueError;
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
import com.jcsa.jcmutest.mutant.cir2mutant.ptree.CirMutationFlowType;
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
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.sym.SymExpression;

/**
 * It is used to build the cir-mutation trees.
 * 
 * @author yukimula
 *
 */
public class CirMutationGraphUtils {
	
	/* path constraints */
	/**
	 * @param dependence_graph
	 * @param instance
	 * @param cir_mutations
	 * @return the path constraints from entry to the specified statement
	 * @throws Exception
	 */
	private static Iterable<CirConstraint> path_constraints(CDependGraph dependence_graph, 
			CirInstanceNode instance, CirMutations cir_mutations) throws Exception {
		if(dependence_graph == null)
			throw new IllegalArgumentException("Invalid dependence_graph: null");
		else if(cir_mutations == null)
			throw new IllegalArgumentException("Invalid cir_mutations as null");
		else {
			Set<CirConstraint> constraints = new HashSet<CirConstraint>();
			if(dependence_graph.has_node(instance)) {
				CDependNode dependence_node = dependence_graph.get_node(instance);
				while(dependence_node != null) {
					CDependNode next_node;
					for(CDependEdge dependence_edge : dependence_node.get_ou_edges()) {
						if(dependence_edge.get_type() == CDependType.predicate_depend) {
							CDependPredicate predicate = (CDependPredicate) dependence_edge.get_element();
							CirExpression condition = predicate.get_condition();
							CirStatement condition_statement = predicate.get_statement();
							if(predicate.get_predicate_value()) {
								constraints.add(cir_mutations.expression_constraint(condition_statement, condition, true));
							}
							else {
								constraints.add(cir_mutations.expression_constraint(condition_statement, condition, false));
							}
							next_node = dependence_edge.get_target();
						}
						else if(dependence_edge.get_type() == CDependType.stmt_exit_depend) {
							CirStatement statement = dependence_edge.get_target().get_statement();
							constraints.add(cir_mutations.expression_constraint(statement, Boolean.TRUE, true));
							next_node = dependence_edge.get_target();
						}
						else if(dependence_edge.get_type() == CDependType.stmt_call_depend) {
							CirStatement statement = dependence_edge.get_target().get_statement();
							constraints.add(cir_mutations.expression_constraint(statement, Boolean.TRUE, true));
							next_node = dependence_edge.get_target();
						}
						else {
							next_node = null;
						}
						dependence_node = next_node;
					}
				}
			}
			else {
				/* unreachable point */
				constraints.add(cir_mutations.expression_constraint(
						instance.get_execution().get_statement(), Boolean.FALSE, true));
			}
			return constraints;
		}
	}
	/**
	 * @param dependence_graph
	 * @param statement
	 * @param cir_mutations
	 * @return the common path constraints that lead to the target statement
	 * @throws Exception
	 */
	public static Iterable<CirConstraint> common_path_constraints(CDependGraph dependence_graph,
			CirStatement statement, CirMutations cir_mutations) throws Exception {
		if(statement == null)
			throw new IllegalArgumentException("Invalid statement: null");
		else if(dependence_graph == null)
			throw new IllegalArgumentException("Invalid dependence_graph: null");
		else {
			CirInstanceGraph instance_graph = dependence_graph.get_program_graph();
			CirExecution execution = statement.get_tree().get_localizer().get_execution(statement);
			Set<CirConstraint> common_constraints = new HashSet<CirConstraint>(); 
			Set<CirConstraint> buffer = new HashSet<CirConstraint>(); boolean first = true;
			if(instance_graph.has_instances_of(execution)) {
				for(CirInstanceNode instance : instance_graph.get_instances_of(execution)) {
					Iterable<CirConstraint> constraints = path_constraints(dependence_graph, instance, cir_mutations);
					if(first) {
						first = false;
						for(CirConstraint constraint : constraints) common_constraints.add(constraint);
					}
					else {
						buffer.clear();
						for(CirConstraint constraint : constraints) {
							if(common_constraints.contains(constraint)) {
								buffer.add(constraint);
							}
						}
						common_constraints.clear();
						common_constraints.addAll(buffer);
					}
				}
			}
			common_constraints.add(cir_mutations.expression_constraint(statement, Boolean.TRUE, true));
			return common_constraints;
		}
	}
	
	/* error propagation module */
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
	 * @return the set of cir-mutations propagated from the source mutation
	 * @throws Exception
	 */
	protected static Map<CirMutation, CirMutationFlowType> propagate_one(CirMutations 
			cir_mutations, CirMutation source_mutation) throws Exception {
		if(cir_mutations == null)
			throw new IllegalArgumentException("Invalid cir_mutations: null");
		else if(source_mutation == null)
			throw new IllegalArgumentException("Invalid source_mutation: null");
		else {
			/* declarations */
			Map<CirMutation, CirMutationFlowType> results = new HashMap<CirMutation, CirMutationFlowType>();
			CirStateError state_error = source_mutation.get_state_error();
			CirNode source_location, target_location; 
			CirErrorPropagator propagator; CirMutationFlowType flow_type;
			
			/* determine the next location for propagation to occur */
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
			
			/* obtain the error propagator algorithm for analysis */
			if(target_location instanceof CirDeferExpression) {
				propagator = propagators.get(COperator.dereference);
				flow_type = CirMutationFlowType.operand_parent;
			}
			else if(target_location instanceof CirFieldExpression) {
				propagator = propagators.get(COperator.arith_mul_assign);
				flow_type = CirMutationFlowType.operand_parent;
			}
			else if(target_location instanceof CirAddressExpression) {
				propagator = propagators.get(COperator.address_of);
				flow_type = CirMutationFlowType.operand_parent;
			}
			else if(target_location instanceof CirCastExpression) {
				propagator = propagators.get(COperator.arith_add_assign);
				flow_type = CirMutationFlowType.operand_parent;
			}
			else if(target_location instanceof CirComputeExpression) {
				propagator = propagators.get(
						((CirComputeExpression) target_location).get_operator());
				flow_type = CirMutationFlowType.operand_parent;
			}
			else if(target_location instanceof CirInitializerBody) {
				propagator = propagators.get(COperator.arith_sub_assign);
				flow_type = CirMutationFlowType.operand_parent;
			}
			else if(target_location instanceof CirWaitExpression) {
				propagator = propagators.get(COperator.arith_div_assign);
				flow_type = CirMutationFlowType.operand_parent;
			}
			else if(target_location instanceof CirArgumentList) {
				/*
				propagator = propagators.get(COperator.arith_mod_assign);
				flow_type = CirMutationFlowType.argument_retr;
				*/
				/* ignore the propagation */ 
				propagator = null; flow_type = null;
			}
			else if(target_location instanceof CirAssignStatement) {
				propagator = propagators.get(COperator.assign);
				if(((CirAssignStatement) target_location).get_lvalue() == source_location)
					flow_type = CirMutationFlowType.lvalue_lstate;
				else
					flow_type = CirMutationFlowType.rvalue_lstate;
			}
			else { /* ignore the propagation */ propagator = null; flow_type = null; }
			
			/* apply the algorithm to generate error propagation */
			propagations.clear();
			if(propagator != null) {
				propagator.propagate(cir_mutations, 
						state_error, source_location, target_location, propagations);
			}
			
			/* generate the next generation of state errors */
			for(CirStateError target_error : propagations.keySet()) {
				CirConstraint constraint = propagations.get(target_error);
				results.put(cir_mutations.new_mutation(constraint, target_error), flow_type);
			}
			return results;
		}
	}
	
	/* mutation graph building */
	private static Iterable<CDependEdge> find_data_dependence(CDependGraph dependence_graph, 
			CirStatement statement, CirExpression reference) throws Exception {
		CirExecution execution = statement.get_tree().get_localizer().get_execution(statement);
		Set<CDependEdge> data_dependence_edges = new HashSet<CDependEdge>();
		CirInstanceGraph instance_graph = dependence_graph.get_program_graph();
		if(instance_graph.has_instances_of(execution)) {
			for(CirInstanceNode instance : instance_graph.get_instances_of(execution)) {
				if(dependence_graph.has_node(instance)) {
					CDependNode source = dependence_graph.get_node(instance);
					for(CDependEdge edge : source.get_in_edges()) {
						switch(edge.get_type()) {
						case use_defin_depend:
						case param_arg_depend:
						case wait_retr_depend:
						{
							CDependReference element = (CDependReference) edge.get_element();
							if(element.get_def() == reference) {
								data_dependence_edges.add(edge);
							}
						}
						default: break;
						}
					}
				}
			}
		}
		return data_dependence_edges;
	}
	/**
	 * @param graph
	 * @param mutation
	 * @param dependence_graph
	 * @param distance
	 * @throws Exception
	 */
	private static void build_mutation_tree_in(CirMutationGraph graph, 
			CirMutationNode root_node, CDependGraph dependence_graph, int distance) throws Exception {
		root_node.set_path_constraints(dependence_graph);
		Iterable<CirMutationTreeNode> leafs = root_node.build_inner_tree();
		CirMutations cir_mutations = graph.get_cir_mutations();
		
		if(distance > 0) {
			for(CirMutationTreeNode leaf : leafs) {
				CirStateError leaf_error = leaf.get_mutation().get_state_error();
				CirExpression definition_reference; SymExpression mutation_expression;
				if(leaf_error instanceof CirStateValueError) {
					definition_reference = ((CirStateValueError) leaf_error).get_reference();
					mutation_expression = ((CirStateValueError) leaf_error).get_mutation_value();
				}
				else if(leaf_error instanceof CirReferenceError) {
					definition_reference = ((CirReferenceError) leaf_error).get_reference();
					mutation_expression = ((CirReferenceError) leaf_error).get_mutation_value();
				}
				else if(leaf_error instanceof CirExpressionError) {
					definition_reference = ((CirExpressionError) leaf_error).get_expression();
					mutation_expression = ((CirExpressionError) leaf_error).get_mutation_value();
				}
				else {
					continue;
				}
				
				Iterable<CDependEdge> edges = find_data_dependence(
						dependence_graph, leaf_error.get_statement(), definition_reference);
				for(CDependEdge edge : edges) {
					CDependReference element = (CDependReference) edge.get_element();
					CirStateError next_error = cir_mutations.expr_error(element.get_use(), mutation_expression);
					CirConstraint constraint = cir_mutations.expression_constraint(next_error.get_statement(), Boolean.TRUE, true);
					CirMutation next_mutation = cir_mutations.new_mutation(constraint, next_error);
					
					Set<CirConstraint> path_constraints = new HashSet<CirConstraint>();
					for(CirExecutionFlow flow : element.get_flow_path()) {
						switch(flow.get_type()) {
						case true_flow:
						{
							CirStatement source_stmt = flow.get_source().get_statement();
							CirExpression condition;
							if(source_stmt instanceof CirIfStatement) {
								condition = ((CirIfStatement) source_stmt).get_condition();
							}
							else {
								condition = ((CirCaseStatement) source_stmt).get_condition();
							}
							path_constraints.add(cir_mutations.expression_constraint(source_stmt, condition, true));
							break;
						}
						case fals_flow:
						{
							CirStatement source_stmt = flow.get_source().get_statement();
							CirExpression condition;
							if(source_stmt instanceof CirIfStatement) {
								condition = ((CirIfStatement) source_stmt).get_condition();
							}
							else {
								condition = ((CirCaseStatement) source_stmt).get_condition();
							}
							path_constraints.add(cir_mutations.expression_constraint(source_stmt, condition, false));
							break;
						}
						case call_flow:
						{
							path_constraints.add(cir_mutations.expression_constraint(
									flow.get_source().get_statement(), Boolean.TRUE, true));
							break;
						}
						case retr_flow:
						{
							path_constraints.add(cir_mutations.expression_constraint(
									flow.get_target().get_statement(), Boolean.TRUE, true));
							break;
						}
						default: break;
						}
					}
					
					CirMutationNode next_node = graph.new_node(next_mutation);
					graph.connect(path_constraints, leaf, next_node.get_mutation_tree().get_root());
					build_mutation_tree_in(graph, next_node, dependence_graph, distance - 1);
				}
			}
		}
	}
	protected static void build_mutation_graph(CirMutationGraph graph, CDependGraph dependence_graph, int max_distance) throws Exception {
		if(graph == null)
			throw new IllegalArgumentException("Invalid graph: null");
		else if(dependence_graph == null)
			throw new IllegalArgumentException("Invalid dependence_graph");
		else if(graph.get_mutant().has_cir_mutations()) {
			for(CirMutation cir_mutation : graph.get_mutant().get_cir_mutations()) {
				CirMutationNode root_node = graph.new_node(cir_mutation);
				build_mutation_tree_in(graph, root_node, dependence_graph, max_distance);
			}
		}
	}
	
}
