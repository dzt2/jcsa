package com.jcsa.jcmutest.mutant.cir2mutant.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
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
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirBitwsRsvPropagator;
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
import com.jcsa.jcparse.flwa.depend.CDependReference;
import com.jcsa.jcparse.flwa.depend.CDependType;
import com.jcsa.jcparse.flwa.graph.CirInstanceGraph;
import com.jcsa.jcparse.flwa.graph.CirInstanceNode;
import com.jcsa.jcparse.flwa.symbol.CStateContexts;
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
import com.jcsa.jcparse.test.state.CStateNode;
import com.jcsa.jcparse.test.state.CStatePath;

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
		
		propagators.put(COperator.bit_not, new CirBitwsRsvPropagator());
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
	/**
	 * @param source
	 * @param target
	 * @return the execution flows that must occur between any path from source to target
	 * @throws Exception
	 */
	public List<CirExecutionFlow> find_must_paths_between(CirExecution source, CirExecution target) throws Exception {
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
		
		return common_path;
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
	/**
	 * @param cir_mutations
	 * @param source
	 * @param target
	 * @return path constraints required from source to target
	 * @throws Exception
	 */
	public List<CirConstraint> get_path_constraints(CirMutations cir_mutations, CirExecution source, CirExecution target) throws Exception {
		List<CirExecutionFlow> common_path = this.find_must_paths_between(source, target);
		
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
	
	/* local propagation */
	/**
	 * @param cir_mutations
	 * @param source_error
	 * @return the set of cir-mutations directly generated from the source error
	 *         in the same statement as given.
	 * @throws Exception
	 */
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
	
	/* implement use-define analysis here... */
	/**
	 * @param dependence_graph
	 * @param def_expression
	 * @return the set of use expressions used after the definition point
	 * @throws Exception 
	 */
	public Collection<CirExpression> find_use_expressions(
			CDependGraph dependence_graph, CirExpression def_expression) throws Exception {
		Set<CirExpression> use_expressions = new HashSet<CirExpression>();
		
		CirStatement statement = def_expression.statement_of();
		CirExecution execution = statement.get_tree().get_localizer().get_execution(statement);
		if(dependence_graph != null) {
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
								if(element.get_def() == def_expression) {
									use_expressions.add(element.get_use());
								}
							}
							default: break;
							}
						}
					}
				}
			}
		}
		
		return use_expressions;
	}
	
	/* concrete (dynamic) analysis methods */
	/**
	 * @param target the node being reached from the others
	 * @param nodes to preserve the nodes that can reach the target in the graph
	 * @param edges to preserve the edges that can reach the target in the graph
	 * @throws Exception
	 */
	private void collect_prefix_nodes_and_edges(CirMutationNode target, 
			Set<CirMutationNode> nodes, Set<CirMutationEdge> edges) throws Exception {
		if(!nodes.contains(target)) {
			nodes.add(target);
			for(CirMutationEdge edge : target.get_in_edges()) {
				edges.add(edge);
				this.collect_prefix_nodes_and_edges(edge.get_source(), nodes, edges);
			}
		}
	}
	/**
	 * perform the dynamic analysis on given execution path to determine of which
	 * nodes before the faulty statement have been executed (excluding the 
	 * @param mutation_graph
	 * @param state_path
	 * @throws Exception
	 */
	private void conc_prev_evaluate(CirMutationGraph mutation_graph, 
			CStatePath state_path) throws Exception {
		if(mutation_graph == null)
			throw new IllegalArgumentException("Invalid mutation_graph: null");
		else if(state_path == null)
			throw new IllegalArgumentException("Invalid state_path as null");
		else {
			/* 1. collect the nodes and edges reaching the faulty statement */
			Set<CirMutationNode> prefix_nodes = new HashSet<CirMutationNode>();
			Set<CirMutationEdge> prefix_edges = new HashSet<CirMutationEdge>();
			for(CirMutationNode reaching_node : mutation_graph.get_reaching_nodes()) {
				this.collect_prefix_nodes_and_edges(reaching_node, prefix_nodes, prefix_edges);
			}
			
			/* 2. collect the nodes and edges in execution-maps */
			Map<CirExecution, List<Object>> nodes_and_edges = new HashMap<CirExecution, List<Object>>();
			for(CirMutationNode prefix_node : prefix_nodes) {
				CirExecution execution = prefix_node.get_execution();
				if(!nodes_and_edges.containsKey(execution)) {
					nodes_and_edges.put(execution, new LinkedList<Object>());
				}
				nodes_and_edges.get(execution).add(prefix_node);
			}
			for(CirMutationEdge prefix_edge : prefix_edges) {
				CirExecution execution = prefix_edge.get_constraint().get_execution();
				if(!nodes_and_edges.containsKey(execution)) {
					nodes_and_edges.put(execution, new LinkedList<Object>());
				}
				nodes_and_edges.get(execution).add(prefix_edge);
			}
			
			/* 3. dynamic execution to solve the reaching counters */
			CStateContexts contexts = new CStateContexts();
			for(CStateNode state_node : state_path.get_nodes()) {
				contexts.accumulate(state_node);
				if(nodes_and_edges.containsKey(state_node.get_execution())) {
					for(Object subject : nodes_and_edges.get(state_node.get_execution())) {
						if(subject instanceof CirMutationNode) {
							((CirMutationNode) subject).append_status(contexts);
						}
						else {
							((CirMutationEdge) subject).append_status(contexts);
						}
					}
				}
			}
		}
	}
	/**
	 * perform the dynamic analysis from the given root (node or edge) in error
	 * propagation until all the reachable nodes in current execution evaluated.
	 * @param root
	 * @param execution
	 * @param contexts
	 * @param next_generations to preserve the reachable nodes or edges as next being evaluated
	 * @throws Exception
	 */
	private void conc_loca_evaluate(Object root, CirExecution execution, 
			CStateContexts contexts, Set<Object> next_generations) throws Exception {
		/* declarations */
		CirMutationNode head, node; Boolean result;
		Queue<CirMutationNode> queue = new LinkedList<CirMutationNode>();
		
		/* get the head node and initialize queue */
		if(root instanceof CirMutationEdge) {
			result = ((CirMutationEdge) root).append_status(contexts);
			head = ((CirMutationEdge) root).get_target();
			if(result == null || result.booleanValue()) {
				queue.add(head);
			}
		}
		else {
			head = (CirMutationNode) root;
			queue.add(head);
		}
		
		/* BFS-algorithm to search local nodes & edges */
		while(!queue.isEmpty()) {
			/* get next node from tree */	node = queue.poll();
			
			/* when matched with the context */
			if(node.get_execution() == execution) {			
				
				/* perform evaluation on the node and avoid edges when rejection */
				result = node.append_status(contexts);
				if(result != null && !result.booleanValue()) 
					continue;	/* cut off at invalid node */
				
				/* continue to evaluate edges when accepted or acceptable */
				for(CirMutationEdge edge : node.get_ou_edges()) {
					if(edge.get_constraint().get_execution() == execution) {
						result = edge.append_status(contexts);
						if(result != null && !result.booleanValue()) 
							continue;	/* cut off at invalid edge */
						else {
							queue.add(edge.get_target());
						}
					}
					else {
						next_generations.add(edge);	/* edge out of the range */
					}
				}
			}
			else {
				next_generations.add(node);		/* node out of the range */
			}
		}
	}
	/**
	 * perform the dynamic analysis on execution path to determine of which nodes
	 * and edges in the error propagation process are reached and satisfied.
	 * @param reaching_node
	 * @param state_path
	 * @throws Exception
	 */
	private void conc_post_evaluate(CirMutationNode reaching_node, CStatePath state_path) throws Exception {
		/* declarations */
		Set<Object> current_nodes_edges = new HashSet<Object>();
		Set<Object> removed_nodes_edges = new HashSet<Object>();
		Set<Object> next_generations = new HashSet<Object>();
		CStateContexts contexts = new CStateContexts();
		
		for(CStateNode state_node : state_path.get_nodes()) {
			/* 1. accumulate program state */	
			contexts.accumulate(state_node);
			
			/* 2. restart the counting on reaching node */
			if(reaching_node.get_execution() == state_node.get_execution()) {
				current_nodes_edges.clear();
				for(CirMutationEdge edge : reaching_node.get_ou_edges()) {
					current_nodes_edges.add(edge);
				}
			}
			
			/* 3. perform the analysis on local nodes & edges and update */
			removed_nodes_edges.clear(); next_generations.clear();
			for(Object root : current_nodes_edges) {
				removed_nodes_edges.add(root);
				this.conc_loca_evaluate(root, state_node.get_execution(), contexts, next_generations);
			}
			current_nodes_edges.removeAll(removed_nodes_edges);
			current_nodes_edges.addAll(next_generations);
		}
	}
	/**
	 * perform dynamic analysis on execution path after reaching the faulty statement.
	 * @param mutation_graph
	 * @param state_path
	 * @throws Exception
	 */
	private void conc_post_evaluate(CirMutationGraph mutation_graph, CStatePath state_path) throws Exception {
		if(mutation_graph == null)
			throw new IllegalArgumentException("Invalid mutation_graph: null");
		else if(state_path == null)
			throw new IllegalArgumentException("Invalid state_path as null");
		else {
			for(CirMutationNode reaching_node : mutation_graph.get_reaching_nodes()) {
				if(reaching_node.get_status().is_executed()) {
					this.conc_post_evaluate(reaching_node, state_path);
				}
			}
		}
	}
	/**
	 * perform dynamic analysis to count the reachability and propagation state in testing
	 * @param mutation_graph
	 * @param state_path
	 * @throws Exception
	 */
	public void conc_evaluate(CirMutationGraph mutation_graph, CStatePath state_path) throws Exception {
		if(mutation_graph == null)
			throw new IllegalArgumentException("Invalid mutation_graph: null");
		else if(state_path == null)
			throw new IllegalArgumentException("Invalid state_path as null");
		else {
			mutation_graph.reset_status();
			this.conc_prev_evaluate(mutation_graph, state_path);
			this.conc_post_evaluate(mutation_graph, state_path);
		}
	}
	
	/* abstract (static) analysis methods */
	/**
	 * Perform a context-insensitive static analysis on mutation branch tree
	 * @param mutation_graph
	 * @throws Exception
	 */
	public void abst_evaluate(CirMutationGraph mutation_graph) throws Exception {
		if(mutation_graph == null)
			throw new IllegalArgumentException("Invalid mutation_graph: null");
		else {
			Queue<CirMutationNode> queue = new LinkedList<CirMutationNode>();
			queue.add(mutation_graph.get_start_node()); CirMutationNode node;
			mutation_graph.reset_status(); Boolean result;
			while(!queue.isEmpty()) {
				node = queue.poll();
				result = node.append_status(null);
				if(result == null || result.booleanValue()) {
					for(CirMutationEdge edge : node.get_ou_edges()) {
						result = edge.append_status(null);
						if(result == null || result.booleanValue()) {
							queue.add(edge.get_target());
						}
					}
				}
			}
		}
	}
	
	/* acceptable border analysis */
	/**
	 * Find the edges and nodes as the border of the acceptable range, i.e. nodes that 
	 * cannot be accepted yet their preconditions can, or terminal leafs of the graph.
	 * @param mutation_graph
	 * @return
	 * @throws Exception
	 */
	public Set<Object> find_acceptable_border(CirMutationGraph mutation_graph) throws Exception {
		if(mutation_graph == null)
			throw new IllegalArgumentException("Invalid mutation_graph: null");
		else {
			Set<Object> border = new HashSet<Object>();
			
			Queue<CirMutationNode> queue = new LinkedList<CirMutationNode>();
			queue.add(mutation_graph.get_start_node()); CirMutationNode node;
			while(!queue.isEmpty()) {
				node = queue.poll();
				if(node.get_status().is_acceptable()) {
					if(node.get_ou_degree() > 0) {
						for(CirMutationEdge edge : node.get_ou_edges()) {
							if(edge.get_status().is_acceptable()) {
								queue.add(edge.get_target());
							}
							else {
								border.add(edge);
							}
						}
					}
					else {
						border.add(node);	/* leaf is on the border of acceptance. */
					}
				}
				else {
					border.add(node);
				}
			}
			
			return border;
		}
	}
	
}
