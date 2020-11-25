package com.jcsa.jcmutest.mutant.cir2mutant.paths;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jcsa.jcmutest.mutant.cir2mutant.CirMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymConstraint;
import com.jcsa.jcparse.flwa.depend.CDependGraph;
import com.jcsa.jcparse.flwa.graph.CirInstanceGraph;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionEdge;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlowType;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionPath;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionPathFinder;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * It implements the construction of symbolic instance nodes and edges.
 * @author yukimula
 *
 */
class SymInstanceGraphBuilder {
	
	/* singleton mode */
	private SymInstanceGraphBuilder() { }
	protected static final SymInstanceGraphBuilder builder = new SymInstanceGraphBuilder();
	
	/* execution part */
	/**
	 * @param dependence_graph
	 * @param instance
	 * @return the sequence of flows that reach from program entry to the instance of target via the control dependence flow.
	 * @throws Exception
	 */
	private List<CirExecutionFlow> get_control_dependence_flows(CDependGraph dependence_graph, CirExecution execution) throws Exception {
		List<CirExecutionFlow> dependence_flows = new ArrayList<CirExecutionFlow>();
		CirExecutionPath dependence_path = CirExecutionPathFinder.finder.dependence_path(dependence_graph, execution);
		for(CirExecutionEdge dependence_edge : dependence_path.get_edges()) {
			switch(dependence_edge.get_type()) {
			case true_flow:
			case fals_flow:
			case call_flow:
			case retr_flow:	dependence_flows.add(dependence_edge.get_flow());	break;
			default: 		break;
			}
		}
		return dependence_flows;
	}
	/**
	 * @param execution
	 * @return the local dependence flows from function entry to the target execution
	 * @throws Exception
	 */
	private List<CirExecutionFlow> get_local_dependence_flows(CirExecution execution) throws Exception {
		CirExecution source = execution.get_graph().get_entry();
		CirExecutionPath dependence_path = new CirExecutionPath(source);
		CirExecutionPathFinder.finder.vf_extend(dependence_path, execution);
		List<CirExecutionFlow> dependence_flows = new ArrayList<CirExecutionFlow>();
		for(CirExecutionEdge dependence_edge : dependence_path.get_edges()) {
			switch(dependence_edge.get_type()) {
			case true_flow:
			case fals_flow:
			case call_flow:
			case retr_flow:	dependence_flows.add(dependence_edge.get_flow());	break;
			default: 		break;
			}
		}
		return dependence_flows;
	}
	/**
	 * @param dependence_graph
	 * @param execution
	 * @return the sequence of flows that reach from program entry to the instance of target via the control dependence flow.
	 * @throws Exception
	 */
	private List<CirExecutionFlow> get_dependence_flows(CDependGraph dependence_graph, CirExecution execution) throws Exception {
		if(dependence_graph != null) {
			CirInstanceGraph instance_graph = dependence_graph.get_program_graph();
			if(instance_graph.has_instances_of(execution)) {
				return this.get_control_dependence_flows(dependence_graph, execution);
			}
			else {
				return this.get_local_dependence_flows(execution);
			}
		}
		else {
			return this.get_local_dependence_flows(execution);
		}
	}
	/**
	 * @param dependence_graph used to construct dependence flows path
	 * @param source the node w.r.t. the program entry
	 * @param target the execution of statement being reached from the entry
	 * @return the node w.r.t. the given execution as target generated from the symbolic instance graph as given
	 * @throws Exception
	 */
	private SymInstanceNode construct_reaching_path(CDependGraph dependence_graph, SymInstanceNode source, CirExecution target) throws Exception {
		/* declarations */
		SymInstanceNode prev_node = source, next_node;
		SymInstanceGraph sym_graph = source.get_graph();
		CirMutations cir_mutations = sym_graph.get_cir_mutations();
		SymConstraint constraint; SymInstanceEdge sym_edge;
		
		List<CirExecutionFlow> dependence_flows = this.get_dependence_flows(dependence_graph, target);
		for(CirExecutionFlow dependence_flow : dependence_flows) {
			/* link the previous node to the source of the current flow */
			if(prev_node.get_execution() != dependence_flow.get_source()) {
				constraint = cir_mutations.expression_constraint(dependence_flow.get_source().get_statement(), Boolean.TRUE, true);
				next_node = sym_graph.new_node(dependence_flow.get_source());
				sym_edge = prev_node.link_to(next_node); 
				sym_edge.add_constraint(constraint);
				prev_node = next_node;
			}
			
			/* generate the constraint for reaching target of flow from its source */
			if(dependence_flow.get_type() == CirExecutionFlowType.true_flow) {
				CirStatement if_statement = dependence_flow.get_source().get_statement();
				CirExpression condition;
				if(if_statement instanceof CirIfStatement) {
					condition = ((CirIfStatement) if_statement).get_condition();
				}
				else {
					condition = ((CirCaseStatement) if_statement).get_condition();
				}
				constraint = cir_mutations.expression_constraint(if_statement, condition, true);
			}
			else if(dependence_flow.get_type() == CirExecutionFlowType.fals_flow) {
				CirStatement if_statement = dependence_flow.get_source().get_statement();
				CirExpression condition;
				if(if_statement instanceof CirIfStatement) {
					condition = ((CirIfStatement) if_statement).get_condition();
				}
				else {
					condition = ((CirCaseStatement) if_statement).get_condition();
				}
				constraint = cir_mutations.expression_constraint(if_statement, condition, false);
			}
			else {
				constraint = cir_mutations.expression_constraint(dependence_flow.get_target().get_statement(), Boolean.TRUE, true);
			}
			
			/* link from the source of the flow to the target of flow */
			next_node = sym_graph.new_node(dependence_flow.get_target());
			sym_edge = prev_node.link_to(next_node);
			sym_edge.add_constraint(constraint);
			prev_node = next_node;
		}
		
		if(prev_node.get_execution() != target) {
			next_node = sym_graph.new_node(target);
			sym_edge = prev_node.link_to(next_node);
			constraint = cir_mutations.expression_constraint(target.get_statement(), Boolean.TRUE, true);
			sym_edge.add_constraint(constraint);
			prev_node = next_node;
		}
		
		return prev_node;
	}
	/**
	 * generate the paths reaching the symbolic instance graph's reaching nodes
	 * @param dependence_graph
	 * @param sym_graph
	 * @throws Exception
	 */
	protected void generate_reaching_paths(CDependGraph dependence_graph, SymInstanceGraph sym_graph) throws Exception {
		if(sym_graph == null)
			throw new IllegalArgumentException("Invalid sym_graph as null");
		else {
			/* declarations */
			CirExecution entry = sym_graph.get_mutant().get_space().get_cir_tree().
					get_function_call_graph().get_main_function().get_flow_graph().get_entry();
			sym_graph.clear(); SymInstanceNode source = sym_graph.new_node(entry), target;
			sym_graph.reaching_ndoes = new ArrayList<SymInstanceNode>();
			CirStatement muta_statement; CirExecution muta_execution; SymInstanceEdge sym_edge;
			
			/* collect the set of cir-mutations w.r.t. the execution where it is seeded */
			Map<CirExecution, List<CirMutation>> init_set = new HashMap<CirExecution, List<CirMutation>>();
			if(sym_graph.get_mutant().has_cir_mutations()) {
				for(CirMutation cir_mutation : sym_graph.get_mutant().get_cir_mutations()) {
					muta_statement = cir_mutation.get_statement();
					muta_execution = muta_statement.get_tree().get_localizer().get_execution(muta_statement);
					if(!init_set.containsKey(muta_execution)) {
						init_set.put(muta_execution, new ArrayList<CirMutation>());
					}
					init_set.get(muta_execution).add(cir_mutation);
				}
			}
			
			/* solve the reaching and infection part of each reaching node */
			sym_graph.reaching_ndoes = new ArrayList<SymInstanceNode>();
			for(CirExecution target_execution : init_set.keySet()) {
				target = this.construct_reaching_path(dependence_graph, source, target_execution);
				for(CirMutation cir_mutation : init_set.get(target_execution)) {
					muta_statement = cir_mutation.get_statement();
					muta_execution = muta_statement.get_tree().get_localizer().get_execution(muta_statement);
					SymInstanceNode init_error_node = sym_graph.new_node(muta_execution);
					init_error_node.add_state_error(cir_mutation.get_state_error());
					sym_edge = target.link_to(init_error_node);
					sym_edge.add_constraint(cir_mutation.get_constraint());
				}
				sym_graph.reaching_ndoes.add(target);
			}
		}
	}
	

}
