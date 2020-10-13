package com.jcsa.jcmutest.mutant.cir2mutant.ptree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirExpressionError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirReferenceError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirStateError;
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
import com.jcsa.jcparse.flwa.dominate.CDominanceGraph;
import com.jcsa.jcparse.flwa.dominate.CDominanceNode;
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

/**
 * Used to implement the construction of mutation propagation tree.
 * 
 * @author yukimula
 *
 */
public class CirMutationTreeUtils {
	
	/** the graph being built upon **/
	private CirMutationTree tree;
	/** private constructor for singleton **/
	private CirMutationTreeUtils() { }
	/** singleton instance **/
	protected static final CirMutationTreeUtils utils = new CirMutationTreeUtils();
	
	/* input methods */
	/**
	 * set the propagation tree for constructed
	 * @param tree
	 */
	public void set_tree(CirMutationTree tree) {
		this.tree = tree;
	}
	
	/* path constraint generation */
	/**
	 * @param dominance_graph
	 * @param instance
	 * @return the constraints for reaching the instance of target statement
	 * @throws Exception
	 */
	public static List<CirConstraint> path_constraints(
			CDominanceGraph dominance_graph, CirInstanceNode instance,
			CirMutations cir_mutations) throws Exception {
		List<CirConstraint> constraints = new ArrayList<CirConstraint>();
		if(dominance_graph.has_node(instance)) {
			CDominanceNode dominance_node = dominance_graph.get_node(instance);
			List<CirExecutionFlow> flows = dominance_node.get_dominance_path();
			for(CirExecutionFlow flow : flows) {
				switch(flow.get_type()) {
				case true_flow:
				{
					CirStatement if_statement = flow.get_source().get_statement();
					CirExpression condition;
					if(if_statement instanceof CirIfStatement) {
						condition = ((CirIfStatement) if_statement).get_condition();
					}
					else if(if_statement instanceof CirCaseStatement) {
						condition = ((CirCaseStatement) if_statement).get_condition();
					}
					else {
						throw new IllegalArgumentException(if_statement.generate_code(true));
					}
					constraints.add(cir_mutations.expression_constraint(if_statement, condition, true));
					break;
				}
				case fals_flow:
				{
					CirStatement if_statement = flow.get_source().get_statement();
					CirExpression condition;
					if(if_statement instanceof CirIfStatement) {
						condition = ((CirIfStatement) if_statement).get_condition();
					}
					else if(if_statement instanceof CirCaseStatement) {
						condition = ((CirCaseStatement) if_statement).get_condition();
					}
					else {
						throw new IllegalArgumentException(if_statement.generate_code(true));
					}
					constraints.add(cir_mutations.expression_constraint(if_statement, condition, false));
					break;
				}
				case call_flow:
				{
					CirStatement call_statement = flow.get_source().get_statement();
					constraints.add(cir_mutations.expression_constraint(call_statement, Boolean.TRUE, true));
					break;
				}
				case retr_flow:
				{
					CirStatement wait_statement = flow.get_target().get_statement();
					constraints.add(cir_mutations.expression_constraint(wait_statement, Boolean.TRUE, true));
					break;
				}
				default: break;
				}
			}
		}
		return constraints;
	}
	/**
	 * @param dominance_graph
	 * @param statement
	 * @param cir_mutations
	 * @return common constraints shared between different paths leading to the statement
	 * @throws Exception
	 */
	public static Set<CirConstraint> common_path_constraints(CDominanceGraph dominance_graph,
			CirStatement statement, CirMutations cir_mutations) throws Exception {
		Set<CirConstraint> common_constraints = new HashSet<CirConstraint>(); boolean first = true;
		CirExecution execution = statement.get_tree().get_localizer().get_execution(statement);
		Set<CirConstraint> removed_constraints = new HashSet<CirConstraint>();
		
		if(dominance_graph.get_instance_graph().has_instances_of(execution)) {
			for(CirInstanceNode instance : dominance_graph.get_instance_graph().get_instances_of(execution)) {
				List<CirConstraint> constraints = path_constraints(dominance_graph, instance, cir_mutations);
				if(first) {
					first = false;
					common_constraints.addAll(constraints);
				}
				else {
					removed_constraints.clear();
					for(CirConstraint constraint : common_constraints) {
						if(!constraints.contains(constraint)) {
							removed_constraints.add(constraint);
						}
					}
					common_constraints.removeAll(removed_constraints);
				}
			}
		}
		return common_constraints;
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
	public static Map<CirMutation, CirMutationFlowType> propagate_one(CirMutations 
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
				propagator = propagators.get(COperator.arith_mod_assign);
				flow_type = CirMutationFlowType.argument_retr;
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
	/**
	 * @param cir_mutations
	 * @param source_mutation
	 * @return It generates a complete set of state errors propagated from the source
	 * 		   mutation in the local statement.
	 * @throws Exception
	 */
	public static Set<CirMutation> local_propagate(CirMutations
			cir_mutations, CirMutation source_mutation) throws Exception {
		Queue<CirMutation> queue = new LinkedList<CirMutation>();
		Set<CirMutation> results = new HashSet<CirMutation>();
		queue.add(source_mutation); CirMutation cir_mutation;
		while(!queue.isEmpty()) {
			/* get the next mutation for analysis */
			cir_mutation = queue.poll(); results.add(cir_mutation);
			
			/* append the next generation into the queue */
			Map<CirMutation, CirMutationFlowType> next_mutations = propagate_one(cir_mutations, cir_mutation);
			for(CirMutation next_mutation : next_mutations.keySet()) { queue.add(next_mutation); }
		}
		return results;
	}
	/**
	 * build the error propagation trees
	 * @throws Exception
	 */
	public void build_trees() throws Exception {
		Queue<CirMutationTreeNode> queue = new LinkedList<CirMutationTreeNode>();
		queue.add(this.tree.get_root());
		while(!queue.isEmpty()) {
			CirMutationTreeNode source = queue.poll();
			Map<CirMutation, CirMutationFlowType> target_mutations = propagate_one(
					this.tree.get_trees().get_cir_mutations(), source.get_cir_mutation());
			for(CirMutation target_mutation : target_mutations.keySet()) {
				CirMutationFlowType flow_type = target_mutations.get(target_mutation);
				CirMutationTreeNode target = source.new_child(flow_type, target_mutation);
				queue.add(target);
			}
		}
	}
	
}
