package com.jcsa.jcparse.lang.irlang.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import com.jcsa.jcparse.flwa.depend.CDependEdge;
import com.jcsa.jcparse.flwa.depend.CDependGraph;
import com.jcsa.jcparse.flwa.depend.CDependNode;
import com.jcsa.jcparse.flwa.depend.CDependPredicate;
import com.jcsa.jcparse.flwa.graph.CirInstanceGraph;
import com.jcsa.jcparse.flwa.graph.CirInstanceNode;
import com.jcsa.jcparse.lang.irlang.stmt.CirEndStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.test.state.CStatePath;

/**
 * 	It provides the algorithms to find path from a source to a target.
 * 	
 * 	@author yukimula
 *	
 */
public class CirExecutionPathFinder {
	
	/* singleton mode */
	private CirExecutionPathFinder() { }
	/** the singleton of the path finder **/
	public static final CirExecutionPathFinder finder = new CirExecutionPathFinder();
	
	/* automatic extension algorithms */
	/**
	 * @param path
	 * @return find the last calling flow that create the context in which the final
	 * 		   target in the path is created and applied in testing or null if such
	 * 		   calling flow does not exist.
	 */
	private CirExecutionFlow call_flow_of_context(CirExecutionPath path) {
		Stack<CirExecutionFlow> call_stack = new Stack<CirExecutionFlow>();
		for(int k = path.length() - 1; k >= 0; k--) {
			CirExecutionFlow flow = path.get_flow(k);
			if(flow.get_type() == CirExecutionFlowType.retr_flow) {
				call_stack.push(flow);
			}
			else if(flow.get_type() == CirExecutionFlowType.call_flow) {
				if(call_stack.isEmpty()) {
					return flow;	/* find the calling flow of the context  */
				}
				else {
					call_stack.pop();
				}
			}
		}
		return null;	/* no calling flow to the context of the path target */
	}
	/**
	 * @param call_flow
	 * @param retr_flow
	 * @return whether the call-flow matches with the retr-flow in the program
	 */
	private boolean match_call_retr_flow_pair(CirExecutionFlow call_flow, CirExecutionFlow retr_flow) {
		if(call_flow == null || retr_flow == null)
			return false;
		else if(call_flow.get_type() == CirExecutionFlowType.call_flow
				&& retr_flow.get_type() == CirExecutionFlowType.retr_flow) {
			CirExecution call_execution = call_flow.get_source();
			CirExecution wait_execution = retr_flow.get_target();
			return call_execution.get_graph() == wait_execution.get_graph()
					&& call_execution.get_id() + 1 == wait_execution.get_id();
		}
		else 
			return false;
	}
	/**
	 * @param path
	 * @return extend the flows in the path from its target automatically until
	 * 		   the extension becomes undecidable,
	 * @throws Exception
	 */
	private void automatic_extend_path(CirExecutionPath path) throws Exception {
		/* declarations */
		CirExecution node; CirStatement statement;
		CirExecutionFlow call_flow; boolean has_ret;
		
		while(true) {
			/* get the next statement for being reached */
			node = path.get_target();
			statement = node.get_statement();
			
			/* reach the exit of the program */
			if(node.get_ou_degree() == 0) { return; }
			/* extend the unique next flow from the target */
			else if(node.get_ou_degree() == 1) {
				path.addFinal(node.get_ou_flow(0));
			}
			/* select the returning flow to the next node correctly */
			else if(statement instanceof CirEndStatement) {
				call_flow = this.call_flow_of_context(path);
				if(call_flow != null) {
					has_ret = false;
					for(CirExecutionFlow retr_flow : node.get_ou_flows()) {
						if(this.match_call_retr_flow_pair(call_flow, retr_flow)) {
							path.addFinal(retr_flow); has_ret = true; break;
						}
					}
					if(!has_ret)
						throw new RuntimeException("Unmatched: " + node.toString());
				}
				else {
					return;	/* unable to decide return flow in calling contexts */
				}
			}
			else { return;	/* unable to decide next flow */ }
		}
	}
	/**
	 * extend the path from its target
	 * @param path
	 * @throws Exception
	 */
	public void extend_path(CirExecutionPath path) throws Exception {
		if(path == null)
			throw new IllegalArgumentException("Invalid path: null");
		else
			this.automatic_extend_path(path);
	}
	/**
	 * generate the path extended from the source automatically
	 * @param source
	 * @return decidable path extended from the source automatically
	 * @throws Exception
	 */
	public CirExecutionPath extend_from(CirExecution source) throws Exception {
		CirExecutionPath path = new CirExecutionPath(source);
		this.automatic_extend_path(path);
		return path;
	}
	
	/* path construction methods */
	/**
	 * Extend the path to the flow, if not decidable, creating a virtual flow to the
	 * source of the flow from the target of the path.
	 * @param path
	 * @param flow
	 * @return true if virtual path is created
	 * @throws Exception
	 */
	private boolean extend_to(CirExecutionPath path, CirExecutionFlow flow) throws Exception {
		/* automatic path extension */
		while(path.get_target() != flow.get_source()) {
			CirExecution source = path.get_target();
			if(source.get_ou_degree() == 0) {
				return false;	/* reaching the exit and stop extension */
			}
			else if(source.get_ou_degree() == 1) {
				path.addFinal(source.get_ou_flow(0));
			}
			else if(source.get_statement() instanceof CirEndStatement) {
				CirExecutionFlow call_flow = this.call_flow_of_context(path);
				boolean has_ret = false;
				if(call_flow != null) {
					for(CirExecutionFlow retr_flow : source.get_ou_flows()) {
						if(this.match_call_retr_flow_pair(call_flow, retr_flow)) {
							has_ret = true;
							path.addFinal(retr_flow);
							break;
						}
					}
				}
				if(!has_ret) break;	/* unable to extend the path */
			}
			else {
				break;	/* unable to extend the path */
			}
		}
		
		/* create the virtual flow to the source of flow */
		if(path.get_target() != flow.get_source()) {
			path.addFinal(CirExecutionFlow.virtual_flow(
					CirExecutionFlowType.next_flow, 
					path.get_target(), flow.get_source()));
		}
		
		/* extend the flow in the tail of the path */
		path.addFinal(flow); return true;
	}
	/**
	 * Extend the path to the target using decidable way, and build virtual flow if not matched
	 * @param path
	 * @param target
	 * @throws Exception
	 */
	private void extend_to(CirExecutionPath path, CirExecution target) throws Exception {
		while(path.get_target() != target) {
			CirExecution source = path.get_target();
			if(source.get_ou_degree() == 0) {
				break;
			}
			else if(source.get_ou_degree() == 1) {
				path.addFinal(source.get_ou_flow(0));
			}
			else if(source.get_statement() instanceof CirEndStatement) {
				CirExecutionFlow call_flow = this.call_flow_of_context(path);
				boolean has_ret = false;
				if(call_flow != null) {
					for(CirExecutionFlow retr_flow : source.get_ou_flows()) {
						if(this.match_call_retr_flow_pair(call_flow, retr_flow)) {
							has_ret = true;
							path.addFinal(retr_flow);
							break;
						}
					}
				}
				if(!has_ret) break;	/* unable to extend the path */
			}
			else {
				break;	/* unable to extend the path */
			}
		}
		
		if(path.get_target() != target) {
			path.addFinal(CirExecutionFlow.virtual_flow(
					CirExecutionFlowType.next_flow, path.get_target(), target));
		}
	}
	/**
	 * @param source
	 * @param flows
	 * @param target
	 * @return create the virtual path from source to target via the given flows.
	 * @throws Exception
	 */
	private CirExecutionPath new_vpath(CirExecution source, Iterable<CirExecutionFlow> flows, CirExecution target) throws Exception {
		/* connect the path to the flows */
		CirExecutionPath path = new CirExecutionPath(source);
		for(CirExecutionFlow flow : flows) {
			this.extend_to(path, flow);
		}
		this.extend_to(path, target);
		return path;
	}
	
	/* path finder from source to target */
	/**
	 * find the paths from the target of the path to the target in the candidates
	 * @param candidates
	 * @param target
	 * @param paths
	 * @throws Exception
	 */
	private void find_paths_from(CirExecutionPath path, CirExecution target, Set<CirExecutionPath> candidates) throws Exception {
		CirExecutionFlow next_flow, call_flow;
		if(path.get_target() == target) {
			candidates.add(path.clone());
		}
		else if(path.get_target().get_ou_degree() == 0) {
			return;	/* reaching the final exit of program */
		}
		/* automatically find the next node in decidable way */
		else if(path.get_target().get_ou_degree() == 1) {
			next_flow = path.get_target().get_ou_flow(0);
			if(!path.has_flow(next_flow)) {
				path.addFinal(path.get_target().get_ou_flow(0));
				this.find_paths_from(path, target, candidates);
				path.delFinal();
			}
		}
		/* decide the return flow in context sensitive way */
		else if(path.get_target().get_statement() instanceof CirEndStatement) {
			call_flow = this.call_flow_of_context(path);	/* find last call context */
			
			/* context-sensitive path finding */
			if(call_flow != null) {
				for(CirExecutionFlow retr_flow : path.get_target().get_ou_flows()) {
					if(this.match_call_retr_flow_pair(call_flow, retr_flow)) {
						if(!path.has_flow(retr_flow)) {
							path.addFinal(retr_flow);
							this.find_paths_from(path, target, candidates);
							path.delFinal();
						}
						return;
					}
				}
				throw new RuntimeException("Unmatched: " + path.get_target() + " from " + call_flow.get_source());
			}
			/* context-insensitive path finding */
			else {
				for(CirExecutionFlow flow : path.get_target().get_ou_flows()) {
					if(!path.has_flow(flow)) {
						path.addFinal(flow);
						this.find_paths_from(path, target, candidates);
						path.delFinal();
					}
				}
			}
		}
		/* true | false branch */
		else {
			for(CirExecutionFlow flow : path.get_target().get_ou_flows()) {
				if(!path.has_flow(flow)) {
					path.addFinal(flow);
					this.find_paths_from(path, target, candidates);
					path.delFinal();
				}
			}
		}
	}
	/**
	 * @param path
	 * @param target
	 * @return find the paths from the end of path to the target
	 * @throws Exception
	 */
	public Set<CirExecutionPath> find_paths_from(CirExecutionPath path, CirExecution target) throws Exception {
		if(path == null)
			throw new IllegalArgumentException("Invalid path as null");
		else if(target == null)
			throw new IllegalArgumentException("Invalid target as null");
		else {
			Set<CirExecutionPath> candidates = new HashSet<CirExecutionPath>();
			this.find_paths_from(path, target, candidates);
			return candidates;
		}
	}
	/**
	 * Find the possible simple paths from the source to the target
	 * @param source
	 * @param target
	 * @return
	 * @throws Exception
	 */
	public Set<CirExecutionPath> find_paths_from(CirExecution source, CirExecution target) throws Exception {
		if(source == null)
			throw new IllegalArgumentException("Invalid source as null");
		else if(target == null)
			throw new IllegalArgumentException("Invalid target as null");
		else {
			Set<CirExecutionPath> candidates = new HashSet<CirExecutionPath>();
			this.find_paths_from(new CirExecutionPath(source), target, candidates);
			return candidates;
		}
	}
	
	/* path selection algorithms */
	/**
	 * @param paths
	 * @return the path with shortest length or null
	 */
	public CirExecutionPath find_shortest_path(Iterable<CirExecutionPath> paths) {
		CirExecutionPath solution = null; int length = Integer.MAX_VALUE;
		for(CirExecutionPath path : paths) {
			if(path.length() < length) {
				length = path.length();
				solution = path;
			}
		}
		return solution;
	}
	/**
	 * @param paths
	 * @return the path with largest length or null
	 */
	public CirExecutionPath find_largest_path(Iterable<CirExecutionPath> paths) {
		CirExecutionPath solution = null; int length = 0;
		for(CirExecutionPath path : paths) {
			if(path.length() > length) {
				length = path.length();
				solution = path;
			}
		}
		return solution;
	}
	/**
	 * @param source
	 * @param paths possible paths from source to target
	 * @param target
	 * @return the sequence of flows commonly occur in the paths
	 * @throws Exception
	 */
	public CirExecutionPath find_common_vpath(CirExecution source, Iterable<CirExecutionPath> paths, CirExecution target) throws Exception {
		/* generate the common execution flows from paths */
		List<CirExecutionFlow> common_flows = new ArrayList<CirExecutionFlow>();
		Set<CirExecutionFlow> removed_flows = new HashSet<CirExecutionFlow>();
		boolean first = true;
		for(CirExecutionPath path : paths) {
			if(first) {
				for(CirExecutionFlow flow : path.get_flows()) {
					common_flows.add(flow);
				}
				first = false;
			}
			else {
				removed_flows.clear();
				for(CirExecutionFlow flow : common_flows) {
					if(!path.has_flow(flow)) {
						removed_flows.add(flow);
					}
				}
				common_flows.removeAll(removed_flows);
			}
		}
		
		/* generate the path using common flows and virtual next_flow */
		return this.new_vpath(source, common_flows, target);
	}
	
	/* dependence-based path finder */
	/**
	 * @param dependence_graph
	 * @param instance
	 * @return generate the path using dependence graph to the instance node
	 * @throws Exception
	 */
	private CirExecutionPath find_dependence_path(CDependGraph dependence_graph, CirInstanceNode instance) throws Exception {
		if(dependence_graph == null)
			throw new IllegalArgumentException("Invalid dependence_graph: null");
		else if(instance == null)
			throw new IllegalArgumentException("Invalid instance: null");
		else {
			List<CirExecutionFlow> flows = new ArrayList<CirExecutionFlow>();
			CirExecution source = dependence_graph.get_program_graph().get_cir_tree().
					get_function_call_graph().get_main_function().get_flow_graph().get_entry();
			
			CDependNode dependence_node = dependence_graph.get_node(instance);
			while(dependence_node != null) {
				CDependNode next_node = null;
				for(CDependEdge dependence_edge : dependence_node.get_ou_edges()) {
					switch(dependence_edge.get_type()) {
					case predicate_depend:
					{
						CDependPredicate element = (CDependPredicate) dependence_edge.get_element();
						CirStatement if_statement = element.get_statement();
						CirExecution if_execution = if_statement.get_tree().get_localizer().get_execution(if_statement);
						for(CirExecutionFlow flow : if_execution.get_ou_flows()) {
							if(element.get_predicate_value() && flow.get_type() == CirExecutionFlowType.true_flow) {
								flows.add(flow);
								break;
							}
							else if(!element.get_predicate_value() && flow.get_type() == CirExecutionFlowType.fals_flow) {
								flows.add(flow);
								break;
							}
						}
						next_node = dependence_edge.get_target();
						break;
					}
					case stmt_exit_depend:
					{
						CirInstanceNode wait_instance = dependence_edge.get_target().get_instance();
						flows.add(wait_instance.get_in_edge(0).get_flow());
						CirInstanceNode exit_instance = wait_instance.get_in_edge(0).get_source();
						next_node = dependence_graph.get_node(exit_instance);
						break;
					}
					case stmt_call_depend:
					{
						next_node = dependence_edge.get_target();
						flows.add(next_node.get_instance().get_ou_edge(0).get_flow());
						break;
					}
					default: 
					{
						break;
					}
					}
				}
				dependence_node = next_node;
			}
			
			for(int k = 0; k < flows.size() / 2; k++) {
				CirExecutionFlow x = flows.get(k);
				CirExecutionFlow y = flows.get(flows.size() - 1 - k);
				flows.set(k, y);
				flows.set(flows.size() - 1 - k, x);
			}
			
			return this.new_vpath(source, flows, instance.get_execution());
		}
	}
	/**
	 * @param dependence_graph
	 * @param execution
	 * @return the set of paths from program entry to the target using dependence graph
	 * @throws Exception
	 */
	public Set<CirExecutionPath> find_dependence_paths(CDependGraph dependence_graph, CirExecution execution) throws Exception {
		if(dependence_graph == null)
			throw new IllegalArgumentException("Invalid dependence_graph: null");
		else if(execution == null)
			throw new IllegalArgumentException("Invalid execution as null");
		else {
			Set<CirExecutionPath> paths = new HashSet<CirExecutionPath>();
			CirInstanceGraph instance_graph = dependence_graph.get_program_graph();
			if(instance_graph.has_instances_of(execution)) {
				for(CirInstanceNode instance : instance_graph.get_instances_of(execution)) {
					if(dependence_graph.has_node(instance)) {
						paths.add(this.find_dependence_path(dependence_graph, instance));
					}
				}
			}
			return paths;
		}
	}
	
	/* instrumental path */
	/**
	 * @param state_path
	 * @return generate the execution path from the instrumental path
	 * @throws Exception
	 */
	public CirExecutionPath find_path(CStatePath state_path) throws Exception {
		if(state_path == null || state_path.size() == 0)
			throw new IllegalArgumentException("Invalid state_path as null");
		else {
			CirExecutionPath path = new CirExecutionPath(state_path.get_node(0).get_execution());
			for(int k = 1; k < state_path.size(); k++) 
				this.extend_to(path, state_path.get_node(k).get_execution());
			return path;
		}
	}
	
}
