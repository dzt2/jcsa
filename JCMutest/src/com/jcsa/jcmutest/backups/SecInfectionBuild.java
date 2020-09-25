package com.jcsa.jcmutest.backups;

import java.util.List;
import java.util.Map;

import com.jcsa.jcparse.flwa.depend.CDependGraph;
import com.jcsa.jcparse.flwa.graph.CirInstanceNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * It is used to initialize the state-graph using the sec-infection module,
 * including the reaching statement as well as the initial state errors.
 * 
 * @author yukimula
 *
 */
public class SecInfectionBuild {
	
	/** singleton of the builder **/
	private static final SecInfectionBuild builder = new SecInfectionBuild(); 
	
	/** private constructor **/
	private SecInfectionBuild() { }
	
	/**
	 * build up the infection structure from reaching point to the initial
	 * state errors linked with the constraints as required for infection.
	 * @param graph
	 * @throws Exception
	 */
	private void build_infection(SecStateGraph graph) throws Exception {
		try {
			SecInfection infection = SecInfection.parse(
					graph.get_cir_tree(), graph.get_mutant());
			if(infection.has_statement()) {
				SecStateNode reach_node = graph.new_node(SecFactory.
						execution_constraint(infection.get_statement(), 1));
				for(SecInfectPair pair : infection.get_infection_pairs()) {
					reach_node.link_to(SecStateEdgeType.infect, 
							graph.new_node(pair.get_init_error()), 
							pair.get_constraint());
				}
			}
		}
		catch(UnsupportedOperationException ex) {
			return;
		}
	}
	
	/**
	 * build up the path from entry to the reach-node as given 
	 * @param graph
	 * @param path
	 * @throws Exception
	 */
	private void build_reach_path(SecStateGraph graph, Iterable<CirExecutionFlow> path) throws Exception {
		SecStateNode next = graph.get_reach_node(), prev;
		for(CirExecutionFlow flow : path) {
			switch(flow.get_type()) {
			case true_flow:
			{
				CirExecution if_execution = flow.get_source();
				CirStatement if_statement = if_execution.get_statement();
				prev = graph.new_node(SecFactory.execution_constraint(if_statement, 1));
				CirExpression condition;
				if(if_statement instanceof CirIfStatement) {
					condition = ((CirIfStatement) if_statement).get_condition();
				}
				else {
					condition = ((CirCaseStatement) if_statement).get_condition();
				}
				prev.link_to(SecStateEdgeType.lead_to, next, SecFactory.condition_constraint(if_statement, condition, true));
				break;
			}
			case fals_flow:
			{
				CirExecution if_execution = flow.get_source();
				CirStatement if_statement = if_execution.get_statement();
				prev = graph.new_node(SecFactory.execution_constraint(if_statement, 1));
				CirExpression condition;
				if(if_statement instanceof CirIfStatement) {
					condition = ((CirIfStatement) if_statement).get_condition();
				}
				else {
					condition = ((CirCaseStatement) if_statement).get_condition();
				}
				prev.link_to(SecStateEdgeType.lead_to, next, SecFactory.condition_constraint(if_statement, condition, false));
				break;
			}
			case call_flow:
			{
				CirExecution call_execution = flow.get_source();
				CirStatement call_statement = call_execution.get_statement();
				prev = graph.new_node(SecFactory.execution_constraint(call_statement, 1));
				prev.link_to(SecStateEdgeType.lead_to, next, SecFactory.condition_constraint(call_statement, Boolean.TRUE, true));
				break;
			}
			case retr_flow:
			{
				CirExecution wait_execution = flow.get_target();
				CirStatement wait_statement = wait_execution.get_statement();
				prev = graph.new_node(SecFactory.execution_constraint(wait_statement, 1));
				prev.link_to(SecStateEdgeType.lead_to, next, SecFactory.condition_constraint(wait_statement, Boolean.TRUE, true));
				break;
			}
			default: throw new IllegalArgumentException("Invalid type: " + flow.get_type());
			}
			next = prev;
		}
	}
	
	/**
	 * build up the paths from entry to the reach-node using SecPathFinder.control_dependence_paths()
	 * @param graph
	 * @param dependence_graph
	 * @throws Exception
	 */
	private void build_reach_paths(SecStateGraph graph, CDependGraph dependence_graph) throws Exception {
		if(graph == null)
			throw new IllegalArgumentException("Invalid graph: null");
		else if(dependence_graph == null)
			throw new IllegalArgumentException("Invalid dependence_graph");
		else if(graph.has_reach_node()) {
			/* 1. get the statement where the fault is reached */
			CirStatement statement = graph.
					get_reach_node().get_constraint().get_statement().get_statement();
			
			/* 2. generate the control-dependence-paths for the faulty statement */
			Map<CirInstanceNode, List<CirExecutionFlow>> paths = 
					SecPathFinder.control_dependence_paths(statement, dependence_graph);
			
			/* 3. translate dependence-path into the reaching-path in state graph */
			for(List<CirExecutionFlow> path : paths.values()) {
				this.build_reach_path(graph, path);
			}
		}
	}
	
	/**
	 * build up the infection structure from reaching point to the initial
	 * state errors linked with the constraints as required for infection.
	 * @param graph
	 * @throws Exception
	 */
	public static void build(SecStateGraph graph, CDependGraph dependence_graph) throws Exception {
		builder.build_infection(graph);
		builder.build_reach_paths(graph, dependence_graph);
	}
	
}
