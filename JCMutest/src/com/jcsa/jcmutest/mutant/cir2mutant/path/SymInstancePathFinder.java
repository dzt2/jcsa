package com.jcsa.jcmutest.mutant.cir2mutant.path;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.cir2mutant.CirMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymInstanceUtils;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymStateError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymStateValueError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymValueError;
import com.jcsa.jcparse.flwa.depend.CDependGraph;
import com.jcsa.jcparse.flwa.graph.CirInstanceGraph;
import com.jcsa.jcparse.flwa.graph.CirInstanceNode;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionEdge;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlowType;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionPath;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionPathFinder;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.parse.symbol.SymbolEvaluator;
import com.jcsa.jcparse.parse.symbol.SymbolStateContexts;
import com.jcsa.jcparse.test.state.CStateNode;


/**
 * It is used to generate the paths that pass through mutated statement and generate the symbolic
 * instances and their state evaluated at given point during testing.
 * 
 * @author yukimula
 *
 */
public class SymInstancePathFinder {
	
	/* singleton mode */
	private SymInstancePathFinder() { }
	public static final SymInstancePathFinder finder = new SymInstancePathFinder();
	
	/* path finding analysis */
	/**
	 * @param execution
	 * @return the collection of execution paths passing through given location
	 * @throws Exception
	 */
	private Collection<CirExecutionPath> prev_paths(CirExecution execution,
			CDependGraph dependence_graph) throws Exception {
		Collection<CirExecutionPath> paths = new ArrayList<CirExecutionPath>();
		
		if(dependence_graph != null) {
			CirInstanceGraph instance_graph = dependence_graph.get_program_graph();
			if(instance_graph.has_instances_of(execution)) {
				for(CirInstanceNode instance : instance_graph.get_instances_of(execution)) {
					if(dependence_graph.has_node(instance)) {
						CirExecutionPath path = CirExecutionPathFinder.finder.dependence_path(dependence_graph, instance);
						paths.add(path);
					}
				}
			}
		}
		
		if(paths.isEmpty()) {
			CirExecution source = execution.get_graph().get_entry();
			CirExecutionPath path = new CirExecutionPath(source);
			CirExecutionPathFinder.finder.vf_extend(path, execution);
			CirExecutionPathFinder.finder.db_extend(path);
			paths.add(path);
		}
		
		return paths;
	}
	/**
	 * extend the path using decidable forward extension for times and recursively extend it to multiple
	 * branch when undecidable point is reached, until the layer is reduced to zero. All the results are
	 * recorded in the output collection of paths.
	 * @param prev_path
	 * @param layer
	 * @param paths
	 * @throws Exception
	 */
	private void post_extend(CirExecutionPath prev_path, int layer, Collection<CirExecutionPath> paths) throws Exception {
		prev_path = prev_path.clone();
		CirExecutionPathFinder.finder.df_extend(prev_path);
		
		if(layer > 0) {
			CirExecution target = prev_path.get_target();
			if(target.get_ou_degree() > 0) {
				for(CirExecutionFlow flow : target.get_ou_flows()) {
					prev_path.add_final(flow);
					this.post_extend(prev_path, layer - 1, paths);
					prev_path.del_final();
				}
			}
			else {
				paths.add(prev_path);
			}
		}
		else {
			paths.add(prev_path);
		}
	}
	/**
	 * @param execution
	 * @param max_layers
	 * @return the paths of error propagation from the specified location with maximal undecidable extension layers.
	 * @throws Exception
	 */
	private Collection<CirExecutionPath> post_paths(CirExecution execution, int max_layers) throws Exception {
		Collection<CirExecutionPath> paths = new ArrayList<CirExecutionPath>();
		this.post_extend(new CirExecutionPath(execution), max_layers, paths);
		return paths;
	}
	/**
	 * @param execution
	 * @param dependence_graph
	 * @param max_propagation_layers
	 * @return the collection of all possible paths that pass through given execution point
	 * @throws Exception
	 */
	private Collection<CirExecutionPath> pass_paths(CirExecution execution, 
			CDependGraph dependence_graph, int max_layers) throws Exception {
		Collection<CirExecutionPath> prev_paths = this.prev_paths(execution, dependence_graph);
		if(!prev_paths.isEmpty()) {
			Collection<CirExecutionPath> post_paths = this.post_paths(execution, max_layers);
			if(!post_paths.isEmpty()) {
				Collection<CirExecutionPath> all_paths = new ArrayList<CirExecutionPath>();
				for(CirExecutionPath prev_path : prev_paths) {
					for(CirExecutionPath post_path : post_paths) {
						CirExecutionPath path = prev_path.clone();
						for(CirExecutionEdge edge : post_path.get_edges()) {
							path.add_final(edge.get_flow());
						}
						all_paths.add(path);
					}
				}
				return all_paths;
			}
		}
		return prev_paths;
	}
	/**
	 * @param path
	 * @param execution
	 * @return the sequence of indexs of execution edges in path that pass through target execution
	 * @throws Exception
	 */
	private List<Integer> indexs_of_pass_edges(CirExecutionPath path, CirExecution execution) throws Exception {
		List<Integer> indexs = new ArrayList<Integer>();
		int index = 0;
		for(CirExecutionEdge edge : path.get_edges()) {
			if(edge.get_target() == execution) {
				indexs.add(index);
			}
			index++;
		}
		return indexs;
	}
	
	/* symbolic instance news */
	/**
	 * generate the path constraints for each branch and call-return flow in the path
	 * @param cir_mutations
	 * @param path
	 * @throws Exception
	 */
	private void generate_R_conditions(SymInstancePath path) throws Exception {
		CirMutations cir_mutations = path.get_cir_mutations();
		int index = 0;
		
		for(CirExecutionEdge edge : path.get_execution_edges()) {
			SymConstraint constraint; 
			CirExecution execution;
			CirStatement statement;
			CirExpression condition;
			
			switch(edge.get_type()) {
			case true_flow:
			{
				execution = edge.get_source();
				statement = execution.get_statement();
				if(statement instanceof CirIfStatement) {
					condition = ((CirIfStatement) statement).get_condition();
				}
				else {
					condition = ((CirCaseStatement) statement).get_condition();
				}
				constraint = cir_mutations.expression_constraint(statement, condition, true);
				if(index > 0) { edge = path.get_execution_edge(index - 1); }
				break;
			}
			case fals_flow:
			{
				execution = edge.get_source();
				statement = execution.get_statement();
				if(statement instanceof CirIfStatement) {
					condition = ((CirIfStatement) statement).get_condition();
				}
				else {
					condition = ((CirCaseStatement) statement).get_condition();
				}
				constraint = cir_mutations.expression_constraint(statement, condition, false);
				if(index > 0) { edge = path.get_execution_edge(index - 1); }
				break;
			}
			case call_flow:
			{
				constraint = cir_mutations.expression_constraint(
						edge.get_source().get_statement(), Boolean.TRUE, true);
				break;
			}
			case retr_flow:
			{
				constraint = cir_mutations.expression_constraint(
						edge.get_target().get_statement(), Boolean.TRUE, true);
				break;
			}
			default: 
			{
				constraint = null;
				break;
			}
			}
			
			index++;
			if(constraint != null) {
				path.get_state(edge, constraint);
			}
		}
	}
	/**
	 * generate the infection constraints along with state errors required for killing
	 * @param path
	 * @param execution
	 * @param infections
	 * @throws Exception
	 */
	private void generate_I_conditions(SymInstancePath path, CirExecution execution, Iterable<CirMutation> infections) throws Exception {
		for(int index : this.indexs_of_pass_edges(path.get_execution_path(), execution)) {
			CirExecutionEdge infection_edge = path.get_execution_edge(index);
			for(CirMutation infection : infections) {
				SymConstraint constraint = infection.get_constraint();
				SymStateError state_error = infection.get_state_error();
				path.get_state(infection_edge, constraint);
				path.get_state(infection_edge, state_error);
			}
		}
	}
	
	/* propagation analysis */
	/**
	 * generate the state errors along with their constraints in given edge and update the states
	 * in local execution
	 * @param path
	 * @param edge_states
	 * @param source_error
	 * @param leaf_mutations to preserve the leaf state errors as root of next statement for propagation
	 * @throws Exception
	 */
	private void generate_P_conditions(SymInstancePath path, CirExecutionEdge execution_edge, 
			SymStateError root_error, Collection<SymStateValueError> propagations) throws Exception {
		Collection<CirMutation> next_mutations = SymInstanceUtils.
				propagate(path.get_cir_mutations(), root_error);
		
		if(next_mutations.isEmpty()) {
			if(root_error instanceof SymValueError) {
				CirNode statement = ((SymValueError) root_error).get_expression().get_parent();
				if(statement instanceof CirIfStatement || statement instanceof CirCaseStatement) {
					this.generate_S_conditions(path, execution_edge, (SymValueError) root_error);
				}
			}
			else if(root_error instanceof SymStateValueError) {
				/* only state-value error can propagate out */
				propagations.add((SymStateValueError) root_error);
			}
		}
		else {
			for(CirMutation next_mutation : next_mutations) {
				path.get_state(execution_edge, next_mutation.get_constraint());
				path.get_state(execution_edge, next_mutation.get_state_error());
				this.generate_P_conditions(path, execution_edge, next_mutation.get_state_error(), propagations);
			}
		}
	}
	/**
	 * generate the statement flow error caused by error in conditional statement's predicate
	 * @param path
	 * @param execution_edge
	 * @param condition_error
	 * @throws Exception
	 */
	private void generate_S_conditions(SymInstancePath path, CirExecutionEdge execution_edge, 
			SymValueError condition_error) throws Exception {
		CirExpression location = ((SymValueError) condition_error).get_expression();
		SymbolExpression muta_value = ((SymValueError) condition_error).get_mutation_value();
		CirMutations cir_mutations = path.get_cir_mutations();
		SymConstraint constraint; SymStateError next_error; 
		muta_value = SymbolEvaluator.evaluate_on(muta_value);
		
		if(muta_value instanceof SymbolConstant) {
			if(((SymbolConstant) muta_value).get_bool()) {
				CirStatement if_statement = location.statement_of();
				CirExecution if_execution = if_statement.get_tree().get_localizer().get_execution(if_statement);
				CirExecutionFlow true_flow = null, fals_flow = null;
				for(CirExecutionFlow flow : if_execution.get_ou_flows()) {
					if(flow.get_type() == CirExecutionFlowType.true_flow) {
						true_flow = flow;
					}
					else if(flow.get_type() == CirExecutionFlowType.fals_flow) {
						fals_flow = flow;
					}
				}
				constraint = cir_mutations.expression_constraint(if_statement, location, false);
				next_error = cir_mutations.flow_error(fals_flow, true_flow);
				path.get_state(execution_edge, constraint);
				path.get_state(execution_edge, next_error);
			}
			else {
				CirStatement if_statement = location.statement_of();
				CirExecution if_execution = if_statement.get_tree().get_localizer().get_execution(if_statement);
				CirExecutionFlow true_flow = null, fals_flow = null;
				for(CirExecutionFlow flow : if_execution.get_ou_flows()) {
					if(flow.get_type() == CirExecutionFlowType.true_flow) {
						true_flow = flow;
					}
					else if(flow.get_type() == CirExecutionFlowType.fals_flow) {
						fals_flow = flow;
					}
				}
				constraint = cir_mutations.expression_constraint(if_statement, location, true);
				next_error = cir_mutations.flow_error(true_flow, fals_flow);
				path.get_state(execution_edge, constraint);
				path.get_state(execution_edge, next_error);
			}
		}
		else {
			CirStatement if_statement = location.statement_of();
			CirExecution if_execution = if_statement.get_tree().get_localizer().get_execution(if_statement);
			CirExecutionFlow true_flow = null, fals_flow = null;
			for(CirExecutionFlow flow : if_execution.get_ou_flows()) {
				if(flow.get_type() == CirExecutionFlowType.true_flow) {
					true_flow = flow;
				}
				else if(flow.get_type() == CirExecutionFlowType.fals_flow) {
					fals_flow = flow;
				}
			}
			
			SymbolExpression condition1 = SymbolFactory.sym_condition(location, true);
			SymbolExpression condition2 = SymbolFactory.sym_condition(muta_value, false);
			SymbolExpression condition = SymbolFactory.logic_and(condition1, condition2);
			constraint = cir_mutations.expression_constraint(if_statement, condition, true);
			next_error = cir_mutations.flow_error(true_flow, fals_flow);
			path.get_state(execution_edge, constraint);
			path.get_state(execution_edge, next_error);
			
			condition1 = SymbolFactory.sym_condition(location, false);
			condition2 = SymbolFactory.sym_condition(muta_value, true);
			condition = SymbolFactory.logic_and(condition1, condition2);
			constraint = cir_mutations.expression_constraint(if_statement, condition, true);
			next_error = cir_mutations.flow_error(fals_flow, true_flow);
			path.get_state(execution_edge, constraint);
			path.get_state(execution_edge, next_error);
		}
	}
	/**
	 * @param statement
	 * @param expression
	 * @return whether expression is used in the statement
	 */
	private void get_use_expressions_in(CirNode location, String expression_key, Collection<CirExpression> use_expressions) throws Exception {
		if(location instanceof CirExpression) {
			if(location.generate_code(true).equals(expression_key)) {
				CirNode parent = location.get_parent();
				if(parent instanceof CirAssignStatement) {
					if(((CirAssignStatement) parent).get_lvalue() != location) {
						use_expressions.add((CirExpression) location);
					}
				}
				else {
					use_expressions.add((CirExpression) location);
				}
			}
		}
		for(CirNode child : location.get_children()) {
			this.get_use_expressions_in(child, expression_key, use_expressions);
		}
	}
	/**
	 * @param path
	 * @param beg_index
	 * @param end_index
	 * @throws Exception
	 */
	private void generate_P_conditions(SymInstancePath path, int beg_index, int end_index) throws Exception {
		Collection<SymStateValueError> propagations = new HashSet<SymStateValueError>();
		Collection<SymStateValueError> remove_error = new HashSet<SymStateValueError>();
		Collection<CirExpression> use_expressions = new HashSet<CirExpression>();
		for(int index = beg_index; index < end_index; index++) {
			CirExecutionEdge execution_edge = path.get_execution_edge(index);
			
			/* update the defined variables from current source of edge */
			if(execution_edge.get_source().get_statement() instanceof CirAssignStatement) {
				CirAssignStatement statement = (CirAssignStatement) 
						execution_edge.get_source().get_statement();
				remove_error.clear();
				String removed_key = statement.get_lvalue().generate_code(true);
				for(SymStateValueError propagation : propagations) {
					String test_key = propagation.get_expression().generate_code(true);
					if(removed_key.equals(test_key)) {
						remove_error.add(propagation);
					}
				}
				propagations.removeAll(remove_error);
			}
			
			/* update new generated infection from the target of edge */
			if(index == beg_index) {
				for(SymInstanceState state : path.get_state(execution_edge).get_states_copy()) {
					if(state.get_instance() instanceof SymStateError) {
						this.generate_P_conditions(path, 
								execution_edge, (SymStateError) state.get_instance(), propagations);
					}
				}
			}
			else {
				for(SymStateValueError propagation : propagations) {
					String use_key = propagation.get_expression().generate_code(true);
					use_expressions.clear();
					this.get_use_expressions_in(execution_edge.get_target().get_statement(), use_key, use_expressions);
					for(CirExpression use_expression : use_expressions) {
						this.generate_P_conditions(path, execution_edge, 
								path.get_cir_mutations().expr_error(use_expression, propagation.get_mutation_value()), 
								propagations);
					}
				}
			}
		}
	}
	/**
	 * generate propagation conditions for killing mutant in the path
	 * @param path
	 * @param execution
	 * @throws Exception
	 */
	private void generate_P_conditions(SymInstancePath path, CirExecution execution) throws Exception {
		int beg_index = -1, end_index = 0;
		for(CirExecutionEdge edge : path.get_execution_edges()) {
			if(edge.get_target() == execution) {
				if(beg_index >= 0) {
					this.generate_P_conditions(path, beg_index, end_index);
				}
				beg_index = end_index;
			}
			end_index++;
		}
		if(beg_index >= 0) {
			this.generate_P_conditions(path, beg_index, path.get_execution_length());
		}
	}
	
	/* static path generation */
	/**
	 * @param mutant
	 * @param dependence_graph
	 * @param max_propagation_layers
	 * @return
	 * @throws Exception
	 */
	public Collection<SymInstancePath> find(Mutant mutant, CirMutations cir_mutations,
			CDependGraph dependence_graph, int max_propagation_layers) throws Exception {
		Map<CirExecution, Collection<CirMutation>> init_mutations = 
				new HashMap<CirExecution, Collection<CirMutation>>();
		Collection<SymInstancePath> sym_paths = new ArrayList<SymInstancePath>();
		
		if(mutant.has_cir_mutations()) {
			for(CirMutation cir_mutation : mutant.get_cir_mutations()) {
				if(!init_mutations.containsKey(cir_mutation.get_execution())) {
					init_mutations.put(cir_mutation.get_execution(), new ArrayList<CirMutation>());
				}
				init_mutations.get(cir_mutation.get_execution()).add(cir_mutation);
			}
			
			for(CirExecution execution : init_mutations.keySet()) {
				Collection<CirExecutionPath> pass_paths = this.pass_paths(
						execution, dependence_graph, max_propagation_layers);
				for(CirExecutionPath pass_path : pass_paths) {
					SymInstancePath sym_path = new SymInstancePath(pass_path, cir_mutations);
					
					this.generate_R_conditions(sym_path);
					this.generate_I_conditions(sym_path, execution, init_mutations.get(execution));
					this.generate_P_conditions(sym_path, execution);
					for(CirExecutionEdge edge : sym_path.get_execution_edges()) {
						sym_path.evaluate(edge, null);
					}
					
					sym_paths.add(sym_path);
				}
			}
		}
		
		return sym_paths;
	}
	
	/* dynamic path generation */
	/**
	 * @param mutant
	 * @param test_path
	 * @return the set of executions reached for infecting mutant in the given path
	 * @throws Exception 
	 */
	private Collection<CirExecution> get_reached_executions(Mutant mutant, CirExecutionPath test_path) throws Exception {
		Collection<CirExecution> executions = new HashSet<CirExecution>();
		if(mutant.has_cir_mutations()) {
			for(CirMutation cir_mutation : mutant.get_cir_mutations()) {
				executions.add(cir_mutation.get_execution());
			}
		}
		
		Collection<CirExecution> reachings = new HashSet<CirExecution>();
		for(CirExecutionEdge edge : test_path.get_edges()) {
			if(reachings.contains(edge.get_source())) {
				reachings.add(edge.get_source());
			}
		}
		if(reachings.contains(test_path.get_target())) {
			reachings.add(test_path.get_target());
		}
		
		return reachings;
	}
	/**
	 * @param mutant
	 * @param test_path
	 * @param cir_mutations
	 * @return dynamic symbolic path for killing mutant
	 * @throws Exception
	 */
	public SymInstancePath find(Mutant mutant, CirExecutionPath test_path, CirMutations cir_mutations) throws Exception {
		Map<CirExecution, Collection<CirMutation>> init_mutations = 
				new HashMap<CirExecution, Collection<CirMutation>>();
		if(mutant.has_cir_mutations()) {
			for(CirMutation cir_mutation : mutant.get_cir_mutations()) {
				if(!init_mutations.containsKey(cir_mutation.get_execution())) {
					init_mutations.put(cir_mutation.get_execution(), new ArrayList<CirMutation>());
				}
				init_mutations.get(cir_mutation.get_execution()).add(cir_mutation);
			}
		}
		
		Collection<CirExecution> reachings = this.get_reached_executions(mutant, test_path);
		SymInstancePath sym_path = new SymInstancePath(test_path, cir_mutations);
		
		this.generate_R_conditions(sym_path);
		for(CirExecution execution : reachings) {
			this.generate_I_conditions(sym_path, execution, init_mutations.get(execution));
			this.generate_P_conditions(sym_path, execution);
		}
		
		SymbolStateContexts contexts = new SymbolStateContexts();
		for(CirExecutionEdge edge : sym_path.get_execution_edges()) {
			if(edge.get_annotation() instanceof CStateNode) {
				CStateNode state_node = (CStateNode) edge.get_annotation();
				contexts.accumulate(state_node);
			}
			sym_path.evaluate(edge, contexts);
		}
		
		return sym_path;
	}
	
}
