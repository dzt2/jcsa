package com.jcsa.jcmutest.mutant.sec2mutant.util.apis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecFactory;
import com.jcsa.jcmutest.mutant.sec2mutant.util.SecStateEdgeType;
import com.jcsa.jcmutest.mutant.sec2mutant.util.SecStateGraph;
import com.jcsa.jcmutest.mutant.sec2mutant.util.SecStateNode;
import com.jcsa.jcparse.flwa.depend.CDependEdge;
import com.jcsa.jcparse.flwa.depend.CDependGraph;
import com.jcsa.jcparse.flwa.depend.CDependNode;
import com.jcsa.jcparse.flwa.depend.CDependPredicate;
import com.jcsa.jcparse.flwa.graph.CirInstanceNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlowType;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * It is used to build up the dominance path that reach the faulty statement
 * from the entry to it.
 * 
 * @author yukimula
 *
 */
public class SecPathFinder {
	
	/** the singleton of the path finder **/
	public static final SecPathFinder finder = new SecPathFinder();
	
	/** private constructor **/
	private SecPathFinder() { }
	
	/**
	 * @param target
	 * @param dependence_graph
	 * @return the sequence of execution flows from entry to the target
	 *         that must be passed through during executing the program.
	 * @throws Exception
	 */
	private List<CirExecutionFlow> get_dominance_path(CirInstanceNode 
			target, CDependGraph dependence_graph) throws Exception {
		List<CirExecutionFlow> path = new ArrayList<CirExecutionFlow>();
		if(dependence_graph.has_node(target)) {
			CDependNode prev = dependence_graph.get_node(target), next;
			while(prev != null) {
				next = null;
				/* find the next dominance node for being reached */
				for(CDependEdge edge : prev.get_ou_edges()) {
					if(edge.get_target() != prev) {
						switch(edge.get_type()) {
						case predicate_depend:
						{
							CDependPredicate predicate = (CDependPredicate) edge.get_element();
							CirExecution if_execution = edge.get_target().get_execution();
							if(predicate.get_predicate_value()) {
								for(CirExecutionFlow flow : if_execution.get_ou_flows()) {
									if(flow.get_type() == CirExecutionFlowType.true_flow) {
										path.add(flow);
										next = edge.get_target();
										break;
									}
								}
							}
							else {
								for(CirExecutionFlow flow : if_execution.get_ou_flows()) {
									if(flow.get_type() == CirExecutionFlowType.fals_flow) {
										path.add(flow);
										next = edge.get_target();
										break;
									}
								}
							}
							break;
						}
						case stmt_call_depend:
						{
							CirExecution call_execution = edge.get_target().get_execution();
							path.add(call_execution.get_ou_flow(0));
							next = edge.get_target();
							break;
						}
						case stmt_exit_depend:
						{
							CirExecution wait_execution = edge.get_target().get_execution();
							path.add(wait_execution.get_in_flow(0));
							next = edge.get_target();
							break;
						}
						default: break;
						}
					}
					if(next != null) break;
				}
				prev = next;
			}
		}
		return path;
	}
	
	/**
	 * @param statement
	 * @param dependence_graph
	 * @return mapping from the instance of the statement to the flows that must be reached before it is satisfied.
	 * @throws Exception
	 */
	private Map<CirInstanceNode, List<CirExecutionFlow>> get_dominance_paths(
			CirStatement statement, CDependGraph dependence_graph) throws Exception {
		CirExecution execution = statement.get_tree().get_localizer().get_execution(statement);
		Iterable<CirInstanceNode> instances = dependence_graph.get_program_graph().get_instances_of(execution);		
		Map<CirInstanceNode, List<CirExecutionFlow>> paths = new HashMap<CirInstanceNode, List<CirExecutionFlow>>();
		for(CirInstanceNode instance : instances) {
			paths.put(instance, this.get_dominance_path(instance, dependence_graph));
		}
		return paths;
	}
	
	/**
	 * @param graph
	 * @param path
	 * @throws Exception
	 */
	private void find_path(SecStateGraph graph, Iterable<CirExecutionFlow> path) throws Exception {
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
	 * @param graph
	 * @param dependence_graph
	 * @throws Exception
	 */
	public void find_path(SecStateGraph graph, CDependGraph dependence_graph) throws Exception {
		if(graph == null)
			throw new IllegalArgumentException("Invalid graph: null");
		else if(dependence_graph == null)
			throw new IllegalArgumentException("Invalid dependence_graph");
		else if(graph.has_reach_node()) {
			CirStatement statement = graph.get_reach_node().get_constraint().get_statement().get_statement();
			Map<CirInstanceNode, List<CirExecutionFlow>> paths = get_dominance_paths(statement, dependence_graph);
			for(List<CirExecutionFlow> path : paths.values()) this.find_path(graph, path);
		}
	}
	
}
