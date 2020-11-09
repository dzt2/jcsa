package com.jcsa.jcmutest.mutant.cir2mutant.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirExpressionError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirReferenceError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirStateError;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirAddressOfPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirArgumentListPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirArithAddPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirArithDivPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirArithModPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirArithMulPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirArithNegPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirArithSubPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirAssignPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirBitwsAndPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirBitwsIorPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirBitwsLshPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirBitwsRshPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirBitwsXorPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirDereferencePropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirEqualWithPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirErrorPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirFieldOfPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirGreaterEqPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirGreaterTnPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirInitializerPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirLogicAndPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirLogicIorPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirLogicNotPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirNotEqualsPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirSmallerEqPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirSmallerTnPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirTypeCastPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirWaitValuePropagator;
import com.jcsa.jcparse.flwa.depend.CDependEdge;
import com.jcsa.jcparse.flwa.depend.CDependGraph;
import com.jcsa.jcparse.flwa.depend.CDependNode;
import com.jcsa.jcparse.flwa.depend.CDependPredicate;
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

/**
 * Used to build the path and propagation flows in graph.
 * 
 * @author yukimula
 *
 */
public class CirMutationUtils {
	
	/* singleton mode */
	private Map<COperator, CirErrorPropagator> propagators;
	private CirMutationUtils() {
		propagators = new HashMap<COperator, CirErrorPropagator>();
		
		propagators.put(COperator.arith_add, new CirArithAddPropagator());
		propagators.put(COperator.arith_sub, new CirArithSubPropagator());
		propagators.put(COperator.arith_mul, new CirArithMulPropagator());
		propagators.put(COperator.arith_div, new CirArithDivPropagator());
		propagators.put(COperator.arith_mod, new CirArithModPropagator());
		propagators.put(COperator.negative, new CirArithNegPropagator());
		
		propagators.put(COperator.bit_and, new CirBitwsAndPropagator());
		propagators.put(COperator.bit_or, new CirBitwsIorPropagator());
		propagators.put(COperator.bit_xor, new CirBitwsXorPropagator());
		propagators.put(COperator.left_shift, new CirBitwsLshPropagator());
		propagators.put(COperator.righ_shift, new CirBitwsRshPropagator());
		
		propagators.put(COperator.assign, new CirAssignPropagator());
		propagators.put(COperator.address_of, new CirAddressOfPropagator());
		propagators.put(COperator.dereference, new CirDereferencePropagator());
		
		propagators.put(COperator.greater_eq, new CirGreaterEqPropagator());
		propagators.put(COperator.greater_tn, new CirGreaterTnPropagator());
		propagators.put(COperator.smaller_eq, new CirSmallerEqPropagator());
		propagators.put(COperator.smaller_tn, new CirSmallerTnPropagator());
		propagators.put(COperator.equal_with, new CirEqualWithPropagator());
		propagators.put(COperator.not_equals, new CirNotEqualsPropagator());
		
		propagators.put(COperator.logic_and, new CirLogicAndPropagator());
		propagators.put(COperator.logic_or, new CirLogicIorPropagator());
		propagators.put(COperator.logic_not, new CirLogicNotPropagator());
		
		propagators.put(COperator.arith_add_assign, new CirFieldOfPropagator());
		propagators.put(COperator.arith_sub_assign, new CirTypeCastPropagator());
		propagators.put(COperator.arith_mul_assign, new CirInitializerPropagator());
		propagators.put(COperator.arith_div_assign, new CirArgumentListPropagator());
		propagators.put(COperator.arith_mod_assign, new CirWaitValuePropagator());
	}
	public static final CirMutationUtils utils = new CirMutationUtils();
	
	/* path finder between two execution nodes */
	private void find_path_between(CirExecution target, CirExecutionFlow flow, 
			Stack<CirExecutionFlow> path, Collection<List<CirExecutionFlow>> accumulate_paths) throws Exception {
		if(!path.contains(flow)) {
			path.push(flow);
			
			if(flow.get_target() == target) {
				List<CirExecutionFlow> new_path = new ArrayList<CirExecutionFlow>();
				for(CirExecutionFlow path_flow : path) {
					new_path.add(path_flow);
				}
				accumulate_paths.add(new_path);
			}
			else {
				for(CirExecutionFlow next_flow : flow.get_target().get_ou_flows()) {
					switch(next_flow.get_type()) {
					case call_flow:
					{
						CirExecution call_execution = flow.get_target();
						CirExecution wait_execution = call_execution.get_graph().get_execution(call_execution.get_id() + 1);
						next_flow = wait_execution.get_in_flow(0);
						break;
					}
					case retr_flow:
					{
						next_flow = null;
						break;
					}
					default: break;
					}
					
					if(next_flow != null)
						this.find_path_between(target, next_flow, path, accumulate_paths);
				}
			}
			
			path.pop();
		}
	}
	/**
	 * @param source
	 * @param target
	 * @return find all the simple paths from source to target
	 * @throws Exception
	 */
	public Collection<List<CirExecutionFlow>> find_paths_between(CirExecution source, CirExecution target) throws Exception {
		Set<List<CirExecutionFlow>> accumulate_paths = new HashSet<List<CirExecutionFlow>>();
		for(CirExecutionFlow flow : source.get_ou_flows()) {
			this.find_path_between(target, flow, new Stack<CirExecutionFlow>(), accumulate_paths);
		}
		return accumulate_paths;
	}
	
	/* path generation by dependence analysis */
	/**
	 * @param cir_mutations
	 * @param dependence_graph
	 * @param instance
	 * @return generate the constraints on path that can reach the instance of the target node
	 * @throws Exception
	 */
	private List<CirConstraint> generate_path_constraints(CirMutations cir_mutations,
			CDependGraph dependence_graph, CirInstanceNode instance) throws Exception {
		if(dependence_graph == null)
			throw new IllegalArgumentException("Invalid dependence_graph: null");
		else if(cir_mutations == null)
			throw new IllegalArgumentException("Invalid cir_mutations as null");
		else if(instance == null)
			throw new IllegalArgumentException("Invalid instance as null");
		else {
			List<CirConstraint> constraints = new ArrayList<CirConstraint>();
			
			if(dependence_graph.has_node(instance)) {
				CDependNode prev_node = dependence_graph.get_node(instance);
				while(prev_node != null) {
					CDependNode next_node = null;
					for(CDependEdge edge : prev_node.get_ou_edges()) {
						if(edge.get_type() == CDependType.predicate_depend) {
							CDependPredicate element = (CDependPredicate) edge.get_element();
							CirStatement statement = element.get_condition().statement_of();
							CirExpression expression = element.get_condition();
							boolean value = element.get_predicate_value();
							constraints.add(cir_mutations.expression_constraint(statement, expression, value));
							next_node = edge.get_target();
							break;
						}
						else if(edge.get_type() == CDependType.stmt_exit_depend) {
							CirInstanceNode wait_instance = edge.get_target().get_instance();
							CirInstanceNode exit_instance = wait_instance.get_in_edge(0).get_source();
							next_node = dependence_graph.get_node(exit_instance);
							constraints.add(cir_mutations.expression_constraint(wait_instance.
									get_execution().get_statement(), Boolean.TRUE, true));
							break;
						}
						else if(edge.get_type() == CDependType.stmt_call_depend) {
							next_node = edge.get_target();
							constraints.add(cir_mutations.expression_constraint(next_node.get_statement(), Boolean.TRUE, true));
							break;
						}
					}
					prev_node = next_node;
				}
			}
			
			for(int k = 0; k < constraints.size() / 2; k++) {
				CirConstraint x = constraints.get(k);
				CirConstraint y = constraints.get(constraints.size() - k - 1);
				constraints.set(k, y);
				constraints.set(constraints.size() - k - 1, x);
			}
			
			return constraints;
		}
	}
	/**
	 * @param cir_mutations
	 * @param dependence_graph
	 * @param execution
	 * @return the path constraints for reaching the execution on dominance relationship
	 * @throws Exception
	 */
	private List<CirConstraint> generate_path_constraints(CirMutations cir_mutations,
			CDependGraph dependence_graph, CirExecution execution) throws Exception {
		if(dependence_graph == null)
			throw new IllegalArgumentException("Invalid dependence_graph: null");
		else if(cir_mutations == null)
			throw new IllegalArgumentException("Invalid cir_mutations as null");
		else if(execution == null)
			throw new IllegalArgumentException("Invalid execution as null");
		else {
			List<CirConstraint> common_constraints = new ArrayList<CirConstraint>();
			Set<CirConstraint> removed_constraints = new HashSet<CirConstraint>();
			CirInstanceGraph instance_graph = dependence_graph.get_program_graph();
			
			if(instance_graph.has_instances_of(execution)) {
				boolean first = true;
				for(CirInstanceNode instance : instance_graph.get_instances_of(execution)) {
					List<CirConstraint> constraints = this.generate_path_constraints(cir_mutations, dependence_graph, instance);
					if(first) {
						first = false;
						common_constraints.addAll(constraints);
					}
					else {
						removed_constraints.clear();
						for(CirConstraint constraint : common_constraints) {
							if(constraints.contains(constraint)) {
								removed_constraints.add(constraint);
							}
						}
						for(CirConstraint constraint : removed_constraints) {
							common_constraints.remove(constraint);
						}
					}
				}
			}
			
			common_constraints.add(cir_mutations.expression_constraint(execution.get_statement(), Boolean.TRUE, true));
			return common_constraints;
		}
	}
	/**
	 * @param cir_mutations
	 * @param execution
	 * @return the path constraints for reaching the execution without dependence relationship
	 * @throws Exception
	 */
	private List<CirConstraint> generate_path_constraints(CirMutations cir_mutations,
			CirExecution target) throws Exception {
		CirExecution source = target.get_graph().get_entry();
		Collection<List<CirExecutionFlow>> paths = this.find_paths_between(source, target);
		Set<CirExecutionFlow> removed_flows = new HashSet<CirExecutionFlow>();
		
		List<CirExecutionFlow> common_path = new ArrayList<CirExecutionFlow>();
		boolean first = true;
		for(List<CirExecutionFlow> path : paths) {
			if(first) {
				first = false;
				common_path.addAll(path);
			}
			else {
				removed_flows.clear();
				for(CirExecutionFlow flow : common_path) {
					if(!path.contains(flow)) {
						removed_flows.add(flow);
					}
				}
				for(CirExecutionFlow flow : removed_flows) {
					common_path.remove(flow);
				}
			}
		}
		
		List<CirConstraint> constraints = new ArrayList<CirConstraint>();
		constraints.add(cir_mutations.expression_constraint(source.get_statement(), Boolean.TRUE, true));
		for(CirExecutionFlow flow : common_path) {
			switch(flow.get_type()) {
			case true_flow:
			{
				CirStatement if_statement = flow.get_source().get_statement();
				CirExpression condition;
				if(if_statement instanceof CirIfStatement) {
					condition = ((CirIfStatement) if_statement).get_condition();
				}
				else {
					condition = ((CirCaseStatement) if_statement).get_condition();
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
				else {
					condition = ((CirCaseStatement) if_statement).get_condition();
				}
				constraints.add(cir_mutations.expression_constraint(if_statement, condition, false));
				break;
			}
			case call_flow:
			{
				constraints.add(cir_mutations.expression_constraint(flow.get_source().get_statement(), Boolean.TRUE, true));
				break;
			}
			case retr_flow:
			{
				constraints.add(cir_mutations.expression_constraint(flow.get_target().get_statement(), Boolean.TRUE, true));
				break;
			}
			case skip_flow:
			{
				constraints.add(cir_mutations.expression_constraint(flow.get_source().get_statement(), Boolean.TRUE, true));
				constraints.add(cir_mutations.expression_constraint(flow.get_target().get_statement(), Boolean.TRUE, true));
				break;
			}
			default: break;
			}
		}
		constraints.add(cir_mutations.expression_constraint(target.get_statement(), Boolean.TRUE, true));
		
		return constraints;
	}
	/**
	 * @param cir_mutations
	 * @param dependence_graph can be null
	 * @param execution
	 * @return path constraints for reaching the execution node
	 * @throws Exception
	 */
	public List<CirConstraint> get_path_constraints(CirMutations cir_mutations, CDependGraph dependence_graph, CirExecution execution) throws Exception {
		if(cir_mutations == null)
			throw new IllegalArgumentException("Invalid cir_mutations: null");
		else if(execution == null)
			throw new IllegalArgumentException("Invalid execution as null");
		else if(dependence_graph == null) {
			return this.generate_path_constraints(cir_mutations, execution);
		}
		else {
			CirInstanceGraph instance_graph = dependence_graph.get_program_graph();
			if(instance_graph.has_instances_of(execution)) {
				return this.generate_path_constraints(cir_mutations, dependence_graph, execution);
			}
			else {
				return this.generate_path_constraints(cir_mutations, execution);
			}
		}
	}
	
	/* local propagation */
	public Collection<CirMutation> local_propagate(CirMutations cir_mutations, 
			CirStateError source_error) throws Exception {
		List<CirMutation> next_mutations = new ArrayList<CirMutation>();
		
		CirExpression location;
		if(source_error instanceof CirExpressionError) {
			location = ((CirExpressionError) source_error).get_expression();
		}
		else if(source_error instanceof CirReferenceError) {
			location = ((CirReferenceError) source_error).get_reference();
		}
		else {
			location = null;
		}
		
		if(location != null) {
			CirNode parent = location.get_parent();
			Map<CirStateError, CirConstraint> propagations = new HashMap<CirStateError, CirConstraint>();
			
			if(parent instanceof CirDeferExpression) {
				this.propagators.get(COperator.dereference).propagate(cir_mutations, source_error, location, parent, propagations);
			}
			else if(parent instanceof CirFieldExpression) {
				this.propagators.get(COperator.arith_add_assign).propagate(cir_mutations, source_error, location, parent, propagations);
			}
			else if(parent instanceof CirAddressExpression) {
				this.propagators.get(COperator.address_of).propagate(cir_mutations, source_error, location, parent, propagations);
			}
			else if(parent instanceof CirCastExpression) {
				this.propagators.get(COperator.arith_sub_assign).propagate(cir_mutations, source_error, location, parent, propagations);
			}
			else if(parent instanceof CirInitializerBody) {
				this.propagators.get(COperator.arith_mul_assign).propagate(cir_mutations, source_error, location, parent, propagations);
			}
			else if(parent instanceof CirWaitExpression) {
				this.propagators.get(COperator.arith_mod_assign).propagate(cir_mutations, source_error, location, parent, propagations);
			}
			else if(parent instanceof CirComputeExpression) {
				this.propagators.get(((CirComputeExpression) parent).get_operator()).propagate(cir_mutations, source_error, location, parent, propagations);
			}
			else if(parent instanceof CirArgumentList) {
				this.propagators.get(COperator.arith_div_assign).propagate(cir_mutations, source_error, location, parent, propagations);
			}
			else if(parent instanceof CirIfStatement
					|| parent instanceof CirCaseStatement) {
				CirStatement statement = (CirStatement) parent;
				CirExecution execution = statement.get_tree().get_localizer().get_execution(statement);
				CirExecutionFlow true_flow = execution.get_ou_flow(0);
				CirExecutionFlow fals_flow = execution.get_ou_flow(1);
				
				CirExpression condition;
				if(statement instanceof CirIfStatement) {
					condition = ((CirIfStatement) statement).get_condition();
				}
				else {
					condition = ((CirCaseStatement) statement).get_condition();
				}
				
				propagations.put(cir_mutations.flow_error(true_flow, fals_flow), 
						cir_mutations.expression_constraint(statement, condition, true));
				propagations.put(cir_mutations.flow_error(fals_flow, true_flow), 
						cir_mutations.expression_constraint(statement, condition, false));
			}
			else if(parent instanceof CirAssignStatement) {
				propagators.get(COperator.assign).propagate(cir_mutations, source_error, location, parent, propagations);
			}
			
			for(CirStateError next_error : propagations.keySet()) {
				CirConstraint constraint = propagations.get(next_error);
				next_mutations.add(cir_mutations.new_mutation(constraint, next_error));
			}
		}
		
		return next_mutations;
	}
	
	
	
	
}
