package com.jcsa.jcmuta.mutant.sem2mutation.sem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticAssertion;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticAssertions;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticMutation;
import com.jcsa.jcmuta.mutant.sem2mutation.sem.process.ADD_LProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.sem.process.ADD_RProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.sem.process.ADR_EProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.sem.process.BAN_LProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.sem.process.BAN_RProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.sem.process.BOR_LProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.sem.process.BOR_RProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.sem.process.BXR_LProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.sem.process.BXR_RProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.sem.process.CAL_AProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.sem.process.CAS_EProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.sem.process.CON_FProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.sem.process.CON_TProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.sem.process.DEF_EProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.sem.process.DIV_LProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.sem.process.DIV_RProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.sem.process.EQV_LProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.sem.process.EQV_RProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.sem.process.FID_EProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.sem.process.FID_FProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.sem.process.GRE_LProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.sem.process.GRE_RProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.sem.process.GRT_LProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.sem.process.GRT_RProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.sem.process.INI_EProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.sem.process.LSH_LProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.sem.process.LSH_RProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.sem.process.MOD_LProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.sem.process.MOD_RProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.sem.process.MUL_LProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.sem.process.MUL_RProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.sem.process.NEG_EProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.sem.process.NEQ_LProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.sem.process.NEQ_RProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.sem.process.NOT_EProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.sem.process.RSH_LProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.sem.process.RSH_RProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.sem.process.RSV_EProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.sem.process.SME_LProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.sem.process.SME_RProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.sem.process.SMT_LProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.sem.process.SMT_RProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.sem.process.SUB_LProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.sem.process.SUB_RProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.sem.process.WAT_FProcess;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
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
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlowType;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.lang.irlang.graph.CirFunctionCall;
import com.jcsa.jcparse.lang.irlang.stmt.CirArgumentList;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCallStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirTagStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirWaitAssignStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lopt.analysis.flow.CInfluenceEdge;
import com.jcsa.jcparse.lopt.analysis.flow.CInfluenceGraph;
import com.jcsa.jcparse.lopt.analysis.flow.CInfluenceNode;
import com.jcsa.jcparse.lopt.context.CirCallContextInstanceGraph;
import com.jcsa.jcparse.lopt.context.CirFunctionCallPathType;
import com.jcsa.jcparse.lopt.ingraph.CirInstanceGraph;
import com.jcsa.jcparse.lopt.ingraph.CirInstanceNode;

/**
 * To build up the semantic error graph
 * @author yukimula
 *
 */
public class SemanticErrorBuilder {
	
	private CirTree cir_tree;
	private CirInstanceGraph program_graph;
	private CInfluenceGraph influence_graph;
	private Map<String, StateErrorProcess> process_map;
	
	private SemanticErrorBuilder() { 
		this.process_map = new HashMap<String, StateErrorProcess>();
		
		/** INITIALIZATION **/
		this.process_map.put(this.get_key(COperator.negative, 0), 		new NEG_EProcess());
		this.process_map.put(this.get_key(COperator.bit_not, 0), 		new RSV_EProcess());
		this.process_map.put(this.get_key(COperator.logic_not, 0), 		new NOT_EProcess());
		
		this.process_map.put(this.get_key(COperator.arith_add, 0), 		new ADD_LProcess());
		this.process_map.put(this.get_key(COperator.arith_add, 1), 		new ADD_RProcess());
		
		this.process_map.put(this.get_key(COperator.arith_sub, 0), 		new SUB_LProcess());
		this.process_map.put(this.get_key(COperator.arith_sub, 1), 		new SUB_RProcess());
		
		this.process_map.put(this.get_key(COperator.arith_mul, 0), 		new MUL_LProcess());
		this.process_map.put(this.get_key(COperator.arith_mul, 1), 		new MUL_RProcess());
		
		this.process_map.put(this.get_key(COperator.arith_div, 0), 		new DIV_LProcess());
		this.process_map.put(this.get_key(COperator.arith_div, 1), 		new DIV_RProcess());
		
		this.process_map.put(this.get_key(COperator.arith_mod, 0), 		new MOD_LProcess());
		this.process_map.put(this.get_key(COperator.arith_mod, 1), 		new MOD_RProcess());
		
		this.process_map.put(this.get_key(COperator.bit_and, 0), 		new BAN_LProcess());
		this.process_map.put(this.get_key(COperator.bit_and, 1), 		new BAN_RProcess());
		
		this.process_map.put(this.get_key(COperator.bit_or, 0), 		new BOR_LProcess());
		this.process_map.put(this.get_key(COperator.bit_or, 1), 		new BOR_RProcess());
		
		this.process_map.put(this.get_key(COperator.bit_xor, 0), 		new BXR_LProcess());
		this.process_map.put(this.get_key(COperator.bit_xor, 1), 		new BXR_RProcess());
		
		this.process_map.put(this.get_key(COperator.left_shift, 0), 	new LSH_LProcess());
		this.process_map.put(this.get_key(COperator.left_shift, 1), 	new LSH_RProcess());
		
		this.process_map.put(this.get_key(COperator.righ_shift, 0), 	new RSH_LProcess());
		this.process_map.put(this.get_key(COperator.righ_shift, 1), 	new RSH_RProcess());
		
		this.process_map.put(this.get_key(COperator.address_of, 0), 	new ADR_EProcess());
		this.process_map.put(this.get_key(COperator.dereference, 0), 	new DEF_EProcess());
		
		this.process_map.put(this.get_key(COperator.address_of, 1), 	new FID_EProcess());
		this.process_map.put(this.get_key(COperator.address_of, 2), 	new FID_FProcess());
		this.process_map.put(this.get_key(COperator.address_of, 3), 	new WAT_FProcess());
		this.process_map.put(this.get_key(COperator.address_of, 4), 	new INI_EProcess());
		this.process_map.put(this.get_key(COperator.address_of, 5), 	new CON_TProcess());
		this.process_map.put(this.get_key(COperator.address_of, 6), 	new CAS_EProcess());
		this.process_map.put(this.get_key(COperator.address_of, 7), 	new CAL_AProcess());
		this.process_map.put(this.get_key(COperator.address_of, 8), 	new CON_FProcess());
		
		this.process_map.put(this.get_key(COperator.greater_tn, 0), 	new GRT_LProcess());
		this.process_map.put(this.get_key(COperator.greater_tn, 1), 	new GRT_RProcess());
		
		this.process_map.put(this.get_key(COperator.greater_eq, 0), 	new GRE_LProcess());
		this.process_map.put(this.get_key(COperator.greater_eq, 1), 	new GRE_RProcess());
		
		this.process_map.put(this.get_key(COperator.smaller_tn, 0), 	new SMT_LProcess());
		this.process_map.put(this.get_key(COperator.smaller_tn, 1), 	new SMT_RProcess());
		
		this.process_map.put(this.get_key(COperator.smaller_eq, 0), 	new SME_LProcess());
		this.process_map.put(this.get_key(COperator.smaller_eq, 1), 	new SME_RProcess());
		
		this.process_map.put(this.get_key(COperator.equal_with, 0), 	new EQV_LProcess());
		this.process_map.put(this.get_key(COperator.equal_with, 1), 	new EQV_RProcess());
		
		this.process_map.put(this.get_key(COperator.not_equals, 0), 	new NEQ_LProcess());
		this.process_map.put(this.get_key(COperator.not_equals, 1), 	new NEQ_RProcess());
	}
	public static final SemanticErrorBuilder builder = new SemanticErrorBuilder();
	
	private String get_key(COperator operator, int index) {
		return operator + "#" + index;
	}
	private StateErrorProcess get_processor(COperator operator, int index) throws Exception {
		String key = this.get_key(operator, index);
		if(!this.process_map.containsKey(key))
			throw new IllegalArgumentException("Invalid: " + key);
		else return this.process_map.get(key);
	}
	
	private CirWaitAssignStatement get_wait_point(CirCallStatement statement) throws Exception {
		CirFunction function = this.cir_tree.get_function_call_graph().get_function(statement);
		CirExecution call_execution = function.get_flow_graph().get_execution(statement);
		CirExecution wait_execution = function.get_flow_graph().get_execution(call_execution.get_id() + 1);
		return (CirWaitAssignStatement) wait_execution.get_statement();
	}
	private List<Set<CirStatement>> get_control_depend_set(CirExpression condition) throws Exception {
		CirStatement statement = condition.statement_of();
		CirExecution execution = cir_tree.get_function_call_graph().
				get_function(statement).get_flow_graph().get_execution(statement);
		Set<CirStatement> true_statements = new HashSet<CirStatement>();
		Set<CirStatement> false_statements = new HashSet<CirStatement>();
		
		for(Object context : this.program_graph.get_contexts()) {
			if(this.program_graph.has_instance(context, execution)) {
				CirInstanceNode instance = this.program_graph.get_instance(context, execution);
				if(this.influence_graph.has_node(instance, condition)) {
					CInfluenceNode source = this.influence_graph.get_node(instance, condition);
					for(CInfluenceEdge edge : source.get_ou_edges()) {
						CInfluenceNode target = edge.get_target();
						CirStatement target_stmt = target.get_statement();
						switch(edge.get_type()) {
						case execute_when_true:
							true_statements.add(target_stmt); 	break;
						case execute_when_false:
							false_statements.add(target_stmt); 	break;
						default: break;
						}
					}
				}
			}
		}
		
		List<Set<CirStatement>> depend_sets = new ArrayList<Set<CirStatement>>();
		depend_sets.add(true_statements); depend_sets.add(false_statements); 
		return depend_sets;
	}
	private Set<CirExpression> get_usage_points(CirExpression definition) throws Exception {
		CirStatement statement = definition.statement_of();
		CirExecution execution = cir_tree.get_function_call_graph().
				get_function(statement).get_flow_graph().get_execution(statement);
		Set<CirExpression> usage_points = new HashSet<CirExpression>();
		
		for(Object context : this.program_graph.get_contexts()) {
			if(this.program_graph.has_instance(context, execution)) {
				CirInstanceNode instance = this.program_graph.get_instance(context, execution);
				if(this.influence_graph.has_node(instance, definition)) {
					CInfluenceNode source = this.influence_graph.get_node(instance, definition);
					for(CInfluenceEdge edge : source.get_ou_edges()) {
						CInfluenceNode target = edge.get_target();
						switch(edge.get_type()) {
						case def_use_assign:
						case arg_param_assign:
						case retr_wait_assign:
							usage_points.add((CirExpression) target.get_cir_source());
						default: break;
						}
					}
				}
			}
		}
		
		return usage_points;
	}
	
	private boolean traverse_control_path(CirExecution target, CirExecutionFlow flow, 
			Map<CirExecutionFlow, Boolean> visit_set, Set<CirExecutionFlow> control_flows) throws Exception {
		if(!visit_set.containsKey(flow)) {
			boolean reach = false;
			visit_set.put(flow, false);		// initialize as unreachable
			
			if(flow.get_target() != target) {
				CirExecution next_node;
				switch(flow.get_type()) {
				case call_flow:	
				{
					CirFunctionCall call = target.get_graph().get_function().get_graph().get_calling(flow);
					next_node = call.get_wait_execution();
				}
				break;
				case retr_flow:	next_node = null;			   	break;
				default: 		next_node = flow.get_target(); 	break;
				}
				
				if(next_node != null) {
					for(CirExecutionFlow next_flow : next_node.get_ou_flows()) {
						if(this.traverse_control_path(target, next_flow, visit_set, control_flows))
							reach = true;
					}
					visit_set.put(flow, reach);
				}
			}
			else {
				visit_set.put(flow, reach);
			}
		}
		
		if(visit_set.get(flow)) {
			if(flow.get_type() == CirExecutionFlowType.true_flow ||
				flow.get_type() == CirExecutionFlowType.fals_flow) {
				control_flows.add(flow);
			}
		}
		
		return visit_set.get(flow);
	}
	private List<SemanticAssertion> get_path_constraints(CirStatement source, 
			CirStatement target, SemanticAssertions assertions) throws Exception {
		CirExecution source_execution = cir_tree.get_function_call_graph().
				get_function(source).get_flow_graph().get_execution(source);
		CirExecution target_execution = cir_tree.get_function_call_graph().
				get_function(target).get_flow_graph().get_execution(target);
		
		Map<CirExecutionFlow, Boolean> visit_set = new HashMap<CirExecutionFlow, Boolean>();
		Set<CirExecutionFlow> control_flows = new HashSet<CirExecutionFlow>();
		for(CirExecutionFlow flow : source_execution.get_ou_flows())
			this.traverse_control_path(target_execution, flow, visit_set, control_flows);
		
		Set<SemanticAssertion> path_constraints = new HashSet<SemanticAssertion>();
		for(CirExecutionFlow control_flow : control_flows) {
			CirExecution condition_execution = control_flow.get_source();
			if(!(control_flows.contains(condition_execution.get_ou_flow(0)) && 
					control_flows.contains(condition_execution.get_ou_flow(1)))) {
				boolean predicate = (control_flow.get_type() == CirExecutionFlowType.true_flow);
				
				CirExpression condition; 
				CirStatement condition_statement = condition_execution.get_statement();
				if(condition_statement instanceof CirIfStatement) {
					condition = ((CirIfStatement) condition_statement).get_condition();
				}
				else {
					condition = ((CirCaseStatement) condition_statement).get_condition();
				}
				
				path_constraints.add(assertions.equal_with(condition, predicate));
			}
		}
		
		List<SemanticAssertion> constraints = new ArrayList<SemanticAssertion>();
		for(SemanticAssertion constraint : path_constraints) constraints.add(constraint);
		return constraints;
	}
	
	private List<SemanticErrorNode> get_propagation(SemanticErrorNode source,
			List<List<SemanticAssertion>> constraint_error) throws Exception {
		List<SemanticAssertion> constraint_assertions = constraint_error.get(0);
		List<SemanticAssertion> state_error_assertions = constraint_error.get(1);
		
		List<SemanticErrorNode> targets = source.get_graph().new_nodes(state_error_assertions);
		for(SemanticErrorNode target : targets) source.link_to(target, constraint_assertions);
		
		return targets;
	}
	/**
	 * process within expression 
	 * @param source_error
	 * @return
	 * @throws Exception
	 */
	private Set<SemanticErrorNode> process(SemanticErrorNode source_error) throws Exception {
		CirNode location = source_error.get_location();
		Set<SemanticErrorNode> target_errors = new HashSet<SemanticErrorNode>();
		
		if(location != null) {
			if(location instanceof CirExpression) {
				CirNode parent = location.get_parent();
				
				if(parent instanceof CirDeferExpression) {
					if(((CirDeferExpression) parent).get_address() == location) {
						StateErrorProcess process = this.get_processor(COperator.dereference, 0);
						List<List<SemanticAssertion>> next_errors = 
								process.process(source_error.get_assertions(), parent);
						target_errors.addAll(this.get_propagation(source_error, next_errors));
					}
				}
				else if(parent instanceof CirFieldExpression) {
					if(((CirFieldExpression) parent).get_body() == location) {
						StateErrorProcess process = this.get_processor(COperator.address_of, 1);
						List<List<SemanticAssertion>> next_errors = 
								process.process(source_error.get_assertions(), parent);
						target_errors.addAll(this.get_propagation(source_error, next_errors));
					}
					else if(((CirFieldExpression) parent).get_field() == location) {
						StateErrorProcess process = this.get_processor(COperator.address_of, 2);
						List<List<SemanticAssertion>> next_errors = 
								process.process(source_error.get_assertions(), parent);
						target_errors.addAll(this.get_propagation(source_error, next_errors));
					}
				}
				else if(parent instanceof CirAddressExpression) {
					if(((CirAddressExpression) parent).get_operand() == location) {
						StateErrorProcess process = this.get_processor(COperator.address_of, 0);
						List<List<SemanticAssertion>> next_errors = 
								process.process(source_error.get_assertions(), parent);
						target_errors.addAll(this.get_propagation(source_error, next_errors));
					}
				}
				else if(parent instanceof CirCastExpression) {
					if(((CirCastExpression) parent).get_operand() == parent) {
						StateErrorProcess process = this.get_processor(COperator.address_of, 6);
						List<List<SemanticAssertion>> next_errors = 
								process.process(source_error.get_assertions(), parent);
						target_errors.addAll(this.get_propagation(source_error, next_errors));
					}
				}
				else if(parent instanceof CirWaitExpression) {
					if(((CirWaitExpression) parent).get_function() == location) {
						StateErrorProcess process = this.get_processor(COperator.address_of, 3);
						List<List<SemanticAssertion>> next_errors = 
								process.process(source_error.get_assertions(), parent);
						target_errors.addAll(this.get_propagation(source_error, next_errors));
					}
				}
				else if(parent instanceof CirInitializerBody) {
					StateErrorProcess process = this.get_processor(COperator.address_of, 4);
					List<List<SemanticAssertion>> next_errors = 
							process.process(source_error.get_assertions(), parent);
					target_errors.addAll(this.get_propagation(source_error, next_errors));
				}
				else if(parent instanceof CirComputeExpression) {
					int index = location.get_child_index();
					StateErrorProcess process = this.get_processor(
							((CirComputeExpression) parent).get_operator(), index);
					List<List<SemanticAssertion>> next_errors = 
							process.process(source_error.get_assertions(), parent);
					target_errors.addAll(this.get_propagation(source_error, next_errors));
				}
				else if(parent instanceof CirIfStatement) {
					if(((CirIfStatement) parent).get_condition() == location) {
						List<Set<CirStatement>> depend_sets = this.get_control_depend_set((CirExpression) location);
						Set<CirStatement> true_set = depend_sets.get(0), false_set = depend_sets.get(1);
						
						for(CirStatement statement : true_set) {
							if(!(statement instanceof CirTagStatement)) {
								StateErrorProcess process = this.get_processor(COperator.address_of, 5);
								List<List<SemanticAssertion>> next_errors = 
										process.process(source_error.get_assertions(), statement);
								target_errors.addAll(this.get_propagation(source_error, next_errors));
							}
						}
						
						for(CirStatement statement : false_set) {
							if(!(statement instanceof CirTagStatement)) {
								StateErrorProcess process = this.get_processor(COperator.address_of, 8);
								List<List<SemanticAssertion>> next_errors = 
										process.process(source_error.get_assertions(), statement);
								target_errors.addAll(this.get_propagation(source_error, next_errors));
							}
						}
					}
				}
				else if(parent instanceof CirCaseStatement) {
					if(((CirCaseStatement) parent).get_condition() == location) {
						List<Set<CirStatement>> depend_sets = this.get_control_depend_set((CirExpression) location);
						Set<CirStatement> true_set = depend_sets.get(0), false_set = depend_sets.get(1);
						
						for(CirStatement statement : true_set) {
							if(!(statement instanceof CirTagStatement)) {
								StateErrorProcess process = this.get_processor(COperator.address_of, 5);
								List<List<SemanticAssertion>> next_errors = 
										process.process(source_error.get_assertions(), statement);
								target_errors.addAll(this.get_propagation(source_error, next_errors));
							}
						}
						
						for(CirStatement statement : false_set) {
							if(!(statement instanceof CirTagStatement)) {
								StateErrorProcess process = this.get_processor(COperator.address_of, 8);
								List<List<SemanticAssertion>> next_errors = 
										process.process(source_error.get_assertions(), statement);
								target_errors.addAll(this.get_propagation(source_error, next_errors));
							}
						}
					}
				}
				else if(parent instanceof CirCallStatement) {
					if(((CirCallStatement) parent).get_function() == location) {
						CirWaitAssignStatement wait = this.get_wait_point((CirCallStatement) parent);
						StateErrorProcess process = this.get_processor(COperator.address_of, 7);
						List<List<SemanticAssertion>> next_errors = 
								process.process(source_error.get_assertions(), wait);
						target_errors.addAll(this.get_propagation(source_error, next_errors));
					}
				}
				else if(parent instanceof CirArgumentList) {
					parent = parent.get_parent();
					CirWaitAssignStatement wait = this.get_wait_point((CirCallStatement) parent);
					StateErrorProcess process = this.get_processor(COperator.address_of, 7);
					List<List<SemanticAssertion>> next_errors = 
							process.process(source_error.get_assertions(), wait.get_rvalue());
					target_errors.addAll(this.get_propagation(source_error, next_errors));
					
					/** argument to function **/
					Set<CirExpression> usage_points = this.get_usage_points((CirExpression) location);
					process =  this.get_processor(COperator.address_of, 6);
					for(CirExpression usage_point : usage_points) {
						next_errors = 
								process.process(source_error.get_assertions(), usage_point);
						target_errors.addAll(this.get_propagation(source_error, next_errors));
					}
				}
				else if(parent instanceof CirAssignStatement) {
					if(((CirAssignStatement) parent).get_rvalue() == location) {
						StateErrorProcess process = this.get_processor(COperator.address_of, 6);
						List<List<SemanticAssertion>> next_errors = process.process(
								source_error.get_assertions(), ((CirAssignStatement) parent).get_lvalue());
						target_errors.addAll(this.get_propagation(source_error, next_errors));
					}
					else if(((CirAssignStatement) parent).get_lvalue() == location) {
						Set<CirExpression> usage_points = this.
								get_usage_points(((CirAssignStatement) parent).get_lvalue());
						StateErrorProcess process = this.get_processor(COperator.address_of, 6);
						for(CirExpression usage_point : usage_points) {
							List<List<SemanticAssertion>> next_errors = process.process(
									source_error.get_assertions(), ((CirAssignStatement) parent).get_lvalue());
							
							/* NOTE: fix the path constraints in define-use propagation of error edge */
							next_errors.set(0, this.get_path_constraints(
									((CirExpression) location).statement_of(), usage_point.statement_of(), 
									source_error.get_graph().get_mutation().get_assertions()));
							target_errors.addAll(this.get_propagation(source_error, next_errors));
						}
					}
				}
				else {
					/** ignore the otherwise cases **/
				}
			}
		}
		
		return target_errors;
	}
	
	public void open(CirTree cir_tree, String main_function) throws Exception {
		if(cir_tree == null)
			throw new IllegalArgumentException("Invalid cir_tree: null");
		else {
			this.cir_tree = cir_tree;
			CirFunction root_function = cir_tree.get_function_call_graph().get_function(main_function);
			this.program_graph = CirCallContextInstanceGraph.graph(root_function, 
					CirFunctionCallPathType.unique_path, -1);
			this.influence_graph = CInfluenceGraph.graph(program_graph);
		}
	}
	public void close() { this.influence_graph = null; this.program_graph = null; }
	private void propagate(SemanticErrorNode source, boolean extend_error, 
			int layer, boolean extend_constraint) throws Exception {
		if(layer <= 0 || source.get_location() == null) { /* stop the error propagation */ }
		else {
			/** 1. update the layer when propagation between statements occurs **/
			CirNode location = source.get_location();
			CirNode parent = location.get_parent();
			if(parent instanceof CirStatement) {
				if(parent instanceof CirAssignStatement) {
					if(((CirAssignStatement) parent).get_lvalue() == location) {
						layer--;
					}
				}
				else {
					layer--;
				}
			}
			else if(parent instanceof CirArgumentList) {
				layer--;
			}
			
			/** 2. recursively traversal on the targets **/
			Set<SemanticErrorNode> targets = this.process(source);
			for(SemanticErrorNode target : targets) {
				if(extend_error) target.extend();
				if(extend_constraint) {
					SemanticErrorEdge edge = target.get_in_edges().iterator().next();
					ConstraintExtension.get_constraint(cir_tree, 
							this.program_graph, this.influence_graph, 
							edge.constraint.get_assertions());
				}
				this.propagate(target, extend_error, layer, extend_constraint);
			}
		}
	}
	public SemanticErrorGraph build(SemanticMutation mutation, boolean 
			extend_error, int layer, boolean extend_constraint) throws Exception {
		if(mutation == null)
			throw new IllegalArgumentException("Invalid mutation: null");
		else {
			SemanticErrorGraph graph = new SemanticErrorGraph(mutation);
			for(SemanticErrorEdge edge : graph.get_infection_edges()) {
				if(extend_constraint) {
					edge.constraint = ConstraintExtension.get_constraint(
							cir_tree, this.program_graph, this.influence_graph, 
							edge.constraint.get_assertions());
				}
				this.propagate(edge.get_target(), extend_error, layer, extend_constraint);
			}
			return graph;
		}
	}
	
}
