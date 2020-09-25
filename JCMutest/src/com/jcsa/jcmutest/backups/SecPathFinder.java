package com.jcsa.jcmutest.backups;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jcsa.jcparse.flwa.depend.CDependEdge;
import com.jcsa.jcparse.flwa.depend.CDependGraph;
import com.jcsa.jcparse.flwa.depend.CDependNode;
import com.jcsa.jcparse.flwa.depend.CDependPredicate;
import com.jcsa.jcparse.flwa.graph.CirInstanceNode;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlowType;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * It is used to build up the dominance path that reach the faulty statement
 * from the entry to it.
 * 
 * @author yukimula
 *
 */
public class SecPathFinder {
	
	/* definition */
	/** the singleton of the path finder **/
	private static final SecPathFinder finder = new SecPathFinder();
	/** private constructor **/
	private SecPathFinder() { }
	
	/* prev-dependence-path */
	/**
	 * @param target
	 * @param dependence_graph
	 * @return the sequence of execution flows from entry to the target
	 *         that must be passed through during executing the program.
	 * @throws Exception
	 */
	private List<CirExecutionFlow> get_control_dependence_path(CirInstanceNode 
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
	private Map<CirInstanceNode, List<CirExecutionFlow>> get_control_dependence_paths(
			CirStatement statement, CDependGraph dependence_graph) throws Exception {
		CirExecution execution = statement.get_tree().get_localizer().get_execution(statement);
		Iterable<CirInstanceNode> instances = dependence_graph.get_program_graph().get_instances_of(execution);		
		Map<CirInstanceNode, List<CirExecutionFlow>> paths = new HashMap<CirInstanceNode, List<CirExecutionFlow>>();
		for(CirInstanceNode instance : instances) {
			paths.put(instance, this.get_control_dependence_path(instance, dependence_graph));
		}
		return paths;
	}
	/**
	 * @param statement
	 * @param dependence_graph
	 * @return map from instance of the statement to the prev-dominance path built from 
	 * 		   control dependence in each specific context
	 * @throws Exception
	 */
	public static Map<CirInstanceNode, List<CirExecutionFlow>> control_dependence_paths(
			CirStatement statement, CDependGraph dependence_graph) throws Exception {
		return finder.get_control_dependence_paths(statement, dependence_graph);
	}
	
	/* dominance-path-between */
	private void simple_paths_between(CirExecution target, CirExecutionFlow flow,
			Set<CirExecutionFlow> path, Set<Set<CirExecutionFlow>> paths) throws Exception {
		/* when flow reaches the target, record it */
		if(flow.get_target() == target) {
			paths.add(new HashSet<CirExecutionFlow>(path));
		}
		/* deep-traverse from the flow furthermore */
		else {
			CirExecution next_execution = flow.get_target();
			for(CirExecutionFlow next_flow : next_execution.get_ou_flows()) {
				if(!path.contains(next_flow)) {
					switch(next_flow.get_type()) {
					case call_flow:
					{
						/* append(call_flow, retr_flow) */
						CirExecutionFlow call_flow = next_flow;
						CirExecution call_execution = next_flow.get_source();
						CirExecution wait_execution = call_execution.get_graph().
									get_execution(call_execution.get_id() + 1);
						CirExecutionFlow retr_flow = wait_execution.get_in_flow(0);
						
						path.add(call_flow); 
						path.add(retr_flow);
						this.simple_paths_between(target, retr_flow, path, paths);
						path.remove(call_flow);
						path.remove(retr_flow);
						
						break;
					}
					case retr_flow:
					{
						break;	/* do not traverse more when it reaches end */
					}
					default: 
					{
						path.add(next_flow);
						this.simple_paths_between(target, next_flow, path, paths);
						path.remove(next_flow);
						break;
					}
					}
				}
			}
		}
	}
	/**
	 * @param source
	 * @param target
	 * @return simple paths from source to target using intra-procedural analysis
	 * @throws Exception
	 */
	private Set<Set<CirExecutionFlow>> simple_paths_between(
			CirExecution source, CirExecution target) throws Exception {
		Set<Set<CirExecutionFlow>> paths = new HashSet<Set<CirExecutionFlow>>();
		if(source != target) {
			for(CirExecutionFlow flow : source.get_ou_flows()) {
				this.simple_paths_between(target, flow, new HashSet<CirExecutionFlow>(), paths);
			}
		}
		return paths;
	}
	/**
	 * @param source
	 * @param target
	 * @return simple paths from source to target using intra-procedural analysis
	 * @throws Exception
	 */
	public static Set<Set<CirExecutionFlow>> intra_simple_paths(
			CirExecution source, CirExecution target) throws Exception {
		return finder.simple_paths_between(source, target);
	}
	
}
