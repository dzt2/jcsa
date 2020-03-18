package com.jcsa.jcmuta.mutant.sem2mutation.error;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jcsa.jcmuta.mutant.sem2mutation.error.process.ADD_LProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.error.process.ADD_RProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.error.process.ADR_EProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.error.process.BAN_LProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.error.process.BAN_RProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.error.process.BOR_LProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.error.process.BOR_RProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.error.process.BXR_LProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.error.process.BXR_RProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.error.process.CAL_AProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.error.process.CAS_EProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.error.process.CON_FProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.error.process.CON_TProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.error.process.DEF_EProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.error.process.DIV_LProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.error.process.DIV_RProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.error.process.EQV_LProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.error.process.EQV_RProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.error.process.FID_EProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.error.process.FID_FProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.error.process.GRE_LProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.error.process.GRE_RProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.error.process.GRT_LProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.error.process.GRT_RProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.error.process.INI_EProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.error.process.LSH_LProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.error.process.LSH_RProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.error.process.MOD_LProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.error.process.MOD_RProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.error.process.MUL_LProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.error.process.MUL_RProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.error.process.NEG_EProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.error.process.NEQ_LProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.error.process.NEQ_RProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.error.process.NOT_EProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.error.process.RSH_LProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.error.process.RSH_RProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.error.process.RSV_EProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.error.process.SME_LProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.error.process.SME_RProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.error.process.SMT_LProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.error.process.SMT_RProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.error.process.SUB_LProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.error.process.SUB_RProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.error.process.WAT_FProcess;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticAssertion;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticMutation;
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
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
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

public class StateErrorProcesses {
	
	private CirTree cir_tree;
	private CirInstanceGraph program_graph;
	private CInfluenceGraph influence_graph;
	private Map<String, StateErrorProcess> process_map;
	private StateErrorProcesses() { 
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
	public static final StateErrorProcesses processes = new StateErrorProcesses();
	
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
	public StateErrorGraph process(SemanticMutation mutation, boolean extend, int layer) throws Exception {
		StateErrorGraph graph = new StateErrorGraph(mutation);
		for(StateInfection infection : graph.get_infections()) {
			this.propagate(infection.get_state_error(), layer);
		}
		this.extend_constraints(graph, extend);
		return graph;
	}
	private void extend_constraints(StateErrorGraph graph, boolean extend) throws Exception {
		if(extend) {
			graph.reach_constraints = ConstraintExtension.get_constraint(cir_tree, 
					program_graph, influence_graph, graph.mutation.get_reachability());
			
			for(StateInfection infection : graph.infections) {
				ConstraintSet constraints = ConstraintExtension.get_constraint(cir_tree, 
						program_graph, influence_graph, infection.get_assertions());
				infection.constraint_set = constraints;
			}
			
			for(StateError error_node : graph.get_errors()) {
				for(StateErrorFlow flow : error_node.get_in_flows()) {
					ConstraintSet constraints = ConstraintExtension.get_constraint(cir_tree, 
							program_graph, influence_graph, flow.get_assertions());
					flow.constraint_set = constraints;
				}
			}
		}
		else {
			List<SemanticAssertion> reach_assertion = new ArrayList<SemanticAssertion>();
			reach_assertion.add(graph.mutation.get_reachability());
			graph.reach_constraints = new ConstraintSet(reach_assertion);
			
			for(StateInfection infection : graph.infections) {
				ConstraintSet constraints = new ConstraintSet(infection.get_assertions());
				infection.constraint_set = constraints;
			}
			
			for(StateError error_node : graph.get_errors()) {
				for(StateErrorFlow flow : error_node.get_in_flows()) {
					ConstraintSet constraints = new ConstraintSet(flow.get_assertions());
					flow.constraint_set = constraints;
				}
			}
		}
	}
	public void close() { this.influence_graph = null; }
	
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
	
	private List<StateError> process(StateError source_error) throws Exception {
		CirNode location = source_error.get_location();
		List<StateError> errors = new ArrayList<StateError>();
		
		if(location != null) {
			if(location instanceof CirExpression) {
				CirNode parent = location.get_parent();
				
				if(parent instanceof CirDeferExpression) {
					if(((CirDeferExpression) parent).get_address() == location) {
						StateErrorProcess process = this.get_processor(COperator.dereference, 0);
						List<StateError> next_errors = process.process(source_error, parent);
						if(next_errors != null) errors.addAll(next_errors);
					}
				}
				else if(parent instanceof CirFieldExpression) {
					if(((CirFieldExpression) parent).get_body() == location) {
						StateErrorProcess process = this.get_processor(COperator.address_of, 1);
						List<StateError> next_errors = process.process(source_error, parent);
						if(next_errors != null) errors.addAll(next_errors);
					}
					else if(((CirFieldExpression) parent).get_field() == location) {
						StateErrorProcess process = this.get_processor(COperator.address_of, 2);
						List<StateError> next_errors = process.process(source_error, parent);
						if(next_errors != null) errors.addAll(next_errors);
					}
				}
				else if(parent instanceof CirAddressExpression) {
					if(((CirAddressExpression) parent).get_operand() == location) {
						StateErrorProcess process = this.get_processor(COperator.address_of, 0);
						List<StateError> next_errors = process.process(source_error, parent);
						if(next_errors != null) errors.addAll(next_errors);
					}
				}
				else if(parent instanceof CirCastExpression) {
					if(((CirCastExpression) parent).get_operand() == parent) {
						StateErrorProcess process = this.get_processor(COperator.address_of, 6);
						List<StateError> next_errors = process.process(source_error, parent);
						if(next_errors != null) errors.addAll(next_errors);
					}
				}
				else if(parent instanceof CirWaitExpression) {
					if(((CirWaitExpression) parent).get_function() == location) {
						StateErrorProcess process = this.get_processor(COperator.address_of, 3);
						List<StateError> next_errors = process.process(source_error, parent);
						if(next_errors != null) errors.addAll(next_errors);
					}
				}
				else if(parent instanceof CirInitializerBody) {
					StateErrorProcess process = this.get_processor(COperator.address_of, 4);
					List<StateError> next_errors = process.process(source_error, parent);
					if(next_errors != null) errors.addAll(next_errors);
				}
				else if(parent instanceof CirComputeExpression) {
					int index = location.get_child_index();
					StateErrorProcess process = this.get_processor(
							((CirComputeExpression) parent).get_operator(), index);
					List<StateError> next_errors = process.process(source_error, parent);
					if(next_errors != null) errors.addAll(next_errors);
				}
				else if(parent instanceof CirIfStatement) {
					if(((CirIfStatement) parent).get_condition() == location) {
						List<Set<CirStatement>> depend_sets = this.get_control_depend_set((CirExpression) location);
						Set<CirStatement> true_set = depend_sets.get(0), false_set = depend_sets.get(1);
						
						for(CirStatement statement : true_set) {
							if(!(statement instanceof CirTagStatement)) {
								StateErrorProcess process = this.get_processor(COperator.address_of, 5);
								List<StateError> next_errors = process.process(source_error, parent);
								if(next_errors != null) errors.addAll(next_errors);
							}
						}
						
						for(CirStatement statement : false_set) {
							if(!(statement instanceof CirTagStatement)) {
								StateErrorProcess process = this.get_processor(COperator.address_of, 8);
								List<StateError> next_errors = process.process(source_error, parent);
								if(next_errors != null) errors.addAll(next_errors);
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
								List<StateError> next_errors = process.process(source_error, parent);
								if(next_errors != null) errors.addAll(next_errors);
							}
						}
						
						for(CirStatement statement : false_set) {
							if(!(statement instanceof CirTagStatement)) {
								StateErrorProcess process = this.get_processor(COperator.address_of, 8);
								List<StateError> next_errors = process.process(source_error, parent);
								if(next_errors != null) errors.addAll(next_errors);
							}
						}
					}
				}
				else if(parent instanceof CirCallStatement) {
					if(((CirCallStatement) parent).get_function() == location) {
						CirWaitAssignStatement wait = this.get_wait_point((CirCallStatement) parent);
						StateErrorProcess process = this.get_processor(COperator.address_of, 7);
						List<StateError> next_errors = process.process(source_error, wait);
						if(next_errors != null) errors.addAll(next_errors);
					}
				}
				else if(parent instanceof CirArgumentList) {
					parent = parent.get_parent();
					CirWaitAssignStatement wait = this.get_wait_point((CirCallStatement) parent);
					StateErrorProcess process = this.get_processor(COperator.address_of, 7);
					List<StateError> next_errors = process.process(source_error, wait.get_rvalue());
					if(next_errors != null) errors.addAll(next_errors);
					
					/** argument to function **/
					Set<CirExpression> usage_points = this.get_usage_points((CirExpression) location);
					process =  this.get_processor(COperator.address_of, 6);
					for(CirExpression usage_point : usage_points) {
						next_errors = process.process(source_error, usage_point);
						if(next_errors != null) errors.addAll(next_errors);
					}
				}
				else if(parent instanceof CirAssignStatement) {
					if(((CirAssignStatement) parent).get_rvalue() == location) {
						StateErrorProcess process = this.get_processor(COperator.address_of, 6);
						List<StateError> next_errors = process.
								process(source_error, ((CirAssignStatement) parent).get_lvalue());
						if(next_errors != null) errors.addAll(next_errors);
					}
					else if(((CirAssignStatement) parent).get_lvalue() == location) {
						Set<CirExpression> usage_points = this.
								get_usage_points(((CirAssignStatement) parent).get_lvalue());
						StateErrorProcess process = this.get_processor(COperator.address_of, 6);
						for(CirExpression usage_point : usage_points) {
							List<StateError> next_errors = process.process(source_error, usage_point);
							if(next_errors != null) errors.addAll(next_errors);
						}
					}
				}
				else {
					/** ignore the otherwise cases **/
				}
			}
		}
		
		return errors;
	}
	
	private void propagate(StateError source_error, int layer) throws Exception {
		if(layer > 0 && source_error.number_of_assertions() > 0) {
			List<StateError> next_errors = this.process(source_error);
			
			for(StateError next_error : next_errors) {
				this.propagate(next_error, layer - 1);
			}
		}
	}
	
}
