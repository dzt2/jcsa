package com.jcsa.jcparse.lang.irlang.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.jcsa.jcparse.flwa.depend.CDependEdge;
import com.jcsa.jcparse.flwa.depend.CDependGraph;
import com.jcsa.jcparse.flwa.depend.CDependNode;
import com.jcsa.jcparse.flwa.depend.CDependPredicate;
import com.jcsa.jcparse.flwa.depend.CDependType;
import com.jcsa.jcparse.flwa.graph.CirInstanceGraph;
import com.jcsa.jcparse.flwa.graph.CirInstanceNode;
import com.jcsa.jcparse.lang.irlang.stmt.CirEndStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.test.state.CStateNode;
import com.jcsa.jcparse.test.state.CStatePath;

/**
 * It implements the path selection/finding algorithm on execution flow graph.
 * 
 * @author yukimula
 *
 */
public class CirExecutionPathFinder {
	
	/* singleton mode for path finder */
	private static final Random random = new Random(System.currentTimeMillis());
	/** private constructor for path finder **/
	private CirExecutionPathFinder() { }
	/** the single instance of the path finder **/
	public static final CirExecutionPathFinder finder = new CirExecutionPathFinder();
	
	/* basic methods */
	/**
	 * find the set of flows that are available for being searched in next stage.
	 * @param path
	 * @param next_flows
	 */
	private void collect_next_flows(CirExecutionPath path, Collection<CirExecutionFlow> next_flows) {
		CirExecution execution = path.get_target(); next_flows.clear();
		if(execution.get_ou_degree() == 0) { /* no more flows available */ }
		if(execution.get_ou_degree() == 1) { /* unique flow available */
			next_flows.add(execution.get_ou_flow(0)); 
		}
		else if(execution.get_statement() instanceof CirEndStatement) {
			/* return the unique return flow matching with current context */
			CirExecutionFlow call_flow = path.last_call_edge().get_flow();
			if(call_flow != null) {
				for(CirExecutionFlow retr_flow : execution.get_ou_flows()) {
					if(CirExecutionFlow.match_call_retr_flow(call_flow, retr_flow)) {
						next_flows.add(retr_flow); break;
					}
				}
			}
			else { /* avoid to select across the procedural borders */ }
		}
		else {
			for(CirExecutionFlow flow : execution.get_ou_flows()) next_flows.add(flow);
		}
	}
	
	/* decidable path finding */
	/**
	 * extend the path to the target using decidable path finding algorithm
	 * @param path
	 * @param target
	 * @return true if a decidable path is extended reaching the target, or false in case that
	 * 		   the conditional branches exist between the path.target to the execution target.
	 * @throws Exception
	 */
	public boolean decidable_extend(CirExecutionPath path, CirExecution target) throws Exception {
		if(path == null)
			throw new IllegalArgumentException("Invalid path as null");
		else if(target == null)
			throw new IllegalArgumentException("Invalid target: null");
		else {
			List<CirExecutionFlow> next_flows = new ArrayList<CirExecutionFlow>();
			while(path.get_target() != target) {
				this.collect_next_flows(path, next_flows);
				if(next_flows.size() == 0) break;
				else if(next_flows.size() == 1) {
					path.add(next_flows.get(0));
				}
				else break;
			}
			return path.get_target() == target;
		}
	}
	/**
	 * extend the path until the target flow is reached using decidable finding algorithm
	 * @param path
	 * @param flow
	 * @return
	 * @throws Exception
	 */
	public boolean decidable_extend(CirExecutionPath path, CirExecutionFlow flow) throws Exception {
		if(path == null)
			throw new IllegalArgumentException("Invalid path as null");
		else if(flow == null)
			throw new IllegalArgumentException("Invalid flow as null");
		else {
			if(path.length() > 0 && path.get_edge(path.length() - 1).get_flow().equals(flow)) {
				return true;
			}
			else if(this.decidable_extend(path, flow.get_source())) {
				path.add(flow); 
				return true;
			}
			else {
				return false;
			}
		}
	}
	/**
	 * extend the path until the undecidable node is reached uding decidable finding method
	 * @param path
	 * @throws Exception
	 */
	public void decidable_extend(CirExecutionPath path) throws Exception {
		if(path == null)
			throw new IllegalArgumentException("Invalid path as null");
		else {
			List<CirExecutionFlow> next_flows = new ArrayList<CirExecutionFlow>();
			while(true) {
				this.collect_next_flows(path, next_flows);
				if(next_flows.size() == 1) {
					path.add(next_flows.get(0));
				}
			}
		}
	}
	/**
	 * @param source
	 * @return the decidable path starting from the source
	 * @throws Exception
	 */
	public CirExecutionPath decidable_path(CirExecution source) throws Exception {
		CirExecutionPath path = new CirExecutionPath(source);
		this.decidable_extend(path);
		return path;
	}
	
	/* simple paths finding */
	/**
	 * recursively find the simple paths from source to specified target
	 * @param path the path currently being built up
	 * @param flow the next flow being added in path
	 * @param target the statement being reached as target
	 * @param paths the set of execution paths constructed
	 * @throws Exception
	 */
	private void find_simple_path_from(CirExecutionPath path, CirExecutionFlow flow, 
			CirExecution target, Collection<CirExecutionPath> paths) throws Exception {
		if(!path.has_edge_of(flow)) {
			path.add(flow);					/* update path <-- path + flow */
			if(flow.get_target() == target) 
				paths.add(path.clone());	/* find the simple path to target */
			else {							/* recursively find on next flows */
				List<CirExecutionFlow> next_flows = new ArrayList<CirExecutionFlow>();
				this.find_simple_path_from(path, flow, target, paths);
				for(CirExecutionFlow next_flow : next_flows) {
					this.find_simple_path_from(path, next_flow, target, paths);
				}
			}
			path.pop();						/* recover path <-- path - flow */
		}
		else { /* to avoid non-simple path */ }
	}
	/**
	 * @param path
	 * @param target
	 * @return the set of simple paths that can reach the target actually (those not reaching target will be ignored)
	 * @throws Exception
	 */
	public Set<CirExecutionPath> simple_extend(CirExecutionPath path, CirExecution target) throws Exception {
		if(path == null)
			throw new IllegalArgumentException("Invalid path as null");
		else if(target == null)
			throw new IllegalArgumentException("Invalid target: null");
		else {
			Set<CirExecutionPath> paths = new HashSet<CirExecutionPath>();
			CirExecution source = path.get_target();
			if(source == target) {
				paths.add(new CirExecutionPath(target));
			}
			else {
				List<CirExecutionFlow> next_flows = new ArrayList<CirExecutionFlow>();
				this.collect_next_flows(path, next_flows);
				for(CirExecutionFlow flow : next_flows) {
					this.find_simple_path_from(path, flow, target, paths);
				}
			}
			return paths;
		}
	}
	/**
	 * extend the path until the flow is reached using simple path finding algorithm
	 * @param path
	 * @param flow
	 * @return
	 * @throws Exception
	 */
	public Set<CirExecutionPath> simple_extend(CirExecutionPath path, CirExecutionFlow flow) throws Exception {
		Set<CirExecutionPath> paths = this.simple_extend(path, flow.get_source());
		for(CirExecutionPath simple_path : paths) simple_path.add(flow); return paths;
	}
	/**
	 * @param source
	 * @param target
	 * @return the minimal simple path linking from source to target.
	 * @throws Exception
	 */
	public Set<CirExecutionPath> simple_paths(CirExecution source, CirExecution target) throws Exception {
		return this.simple_extend(new CirExecutionPath(source), target);
	}
	/**
	 * @param paths
	 * @return the path with the smallest length
	 * @throws Exception
	 */
	public CirExecutionPath select_min_path(Iterable<CirExecutionPath> paths) throws Exception {
		if(paths == null)
			throw new IllegalArgumentException("Invalid paths as null");
		else {
			int min_length = Integer.MAX_VALUE;
			CirExecutionPath min_path = null;
			for(CirExecutionPath path : paths) {
				if(path.length() <= min_length) {
					min_path = path;
					min_length = path.length();
				}
			}
			return min_path;
		}
	}
	/**
	 * @param paths
	 * @return the path with the largest length
	 * @throws Exception
	 */
	public CirExecutionPath select_max_path(Iterable<CirExecutionPath> paths) throws Exception {
		if(paths == null)
			throw new IllegalArgumentException("Invalid paths as null");
		else {
			int max_length = 0;
			CirExecutionPath max_path = null;
			for(CirExecutionPath path : paths) {
				if(path.length() >= max_length) {
					max_path = path;
					max_length = path.length();
				}
			}
			return max_path;
		}
	}
	/**
	 * @param paths
	 * @return a random execution path in set
	 * @throws Exception
	 */
	public CirExecutionPath select_rad_path(Collection<CirExecutionPath> paths) throws Exception {
		if(paths == null)
			return null;
		else if(paths.isEmpty())
			return null;
		else {
			int counter = Math.abs((random.nextInt())) % paths.size();
			CirExecutionPath path = null;
			for(CirExecutionPath simple_path : paths) {
				path = simple_path;
				if(counter-- <= 0) {
					break;
				}
			}
			return path;
		}
	}
	/**
	 * @param source
	 * @param target
	 * @return the minimal simple path connecting from source to target (if they exist)
	 * @throws Exception
	 */
	public CirExecutionPath min_simple_path(CirExecution source, CirExecution target) throws Exception {
		return this.select_min_path(this.simple_paths(source, target));
	}
	/**
	 * @param source
	 * @param target
	 * @return the maximal simple path connecting from source to target (if they exist)
	 * @throws Exception
	 */
	public CirExecutionPath max_simple_path(CirExecution source, CirExecution target) throws Exception {
		return this.select_max_path(this.simple_paths(source, target));
	}
	/**
	 * @param path
	 * @param target
	 * @return the minimal simple path from the prefix to the target
	 * @throws Exception
	 */
	public CirExecutionPath min_simple_path(CirExecutionPath prefix, CirExecution target) throws Exception {
		return this.select_min_path(this.simple_extend(prefix, target));
	}
	/**
	 * @param path
	 * @param target
	 * @return the maximal simple path from the prefix to the target
	 * @throws Exception
	 */
	public CirExecutionPath max_simple_path(CirExecutionPath prefix, CirExecution target) throws Exception {
		return this.select_max_path(this.simple_extend(prefix, target));
	}
	
	/* dependence path parser */
	/**
	 * @param source
	 * @param target
	 * @param flows
	 * @return generate a virtual path from source to target that pass via the given flows.
	 * @throws Exception
	 */
	public CirExecutionPath virtual_bridge(CirExecution source, CirExecution target, Iterable<CirExecutionFlow> flows) throws Exception {
		if(source == null)
			throw new IllegalArgumentException("Invalid source: null");
		else if(target == null)
			throw new IllegalArgumentException("Invalid target: null");
		else if(flows == null)
			throw new IllegalArgumentException("Invalid flows: null");
		else {
			CirExecutionPath path = new CirExecutionPath(source);
			for(CirExecutionFlow flow : flows) {
				if(!this.decidable_extend(path, flow)) {
					path.add(CirExecutionFlow.virtual_flow(CirExecutionFlowType.next_flow, path.get_target(), flow.get_source()));
					path.add(flow);
				}
			}
			if(!this.decidable_extend(path, target)) {
				path.add(CirExecutionFlow.virtual_flow(CirExecutionFlowType.next_flow, path.get_target(), target));
			}
			return path;
		}
	}
	/**
	 * @param dependence_graph
	 * @param instance
	 * @return the execution path from the program entry to the statement of instance target via control dependence edges
	 * @throws Exception
	 */
	private CirExecutionPath dependence_path(CDependGraph dependence_graph, CirInstanceNode instance) throws Exception {
		if(dependence_graph == null)
			throw new IllegalArgumentException("Invalid dependence_graph: null");
		else if(instance == null || !dependence_graph.has_node(instance))
			throw new IllegalArgumentException("Invalid instance as " + instance);
		else {
			CirExecution source = dependence_graph.get_program_graph().get_cir_tree().
					get_function_call_graph().get_main_function().get_flow_graph().get_entry();
			CDependNode dependence_node = dependence_graph.get_node(instance), next_node;
			List<CirExecutionFlow> dependence_flows = new ArrayList<CirExecutionFlow>();
			
			while(dependence_node != null) {
				next_node = null;
				for(CDependEdge dependence_edge : dependence_node.get_ou_edges()) {
					if(dependence_edge.get_type() == CDependType.predicate_depend) {
						CDependPredicate element = (CDependPredicate) dependence_edge.get_element();
						CirStatement if_statement = dependence_edge.get_target().get_statement();
						CirExecution if_execution = 
								if_statement.get_tree().get_localizer().get_execution(if_statement);
						for(CirExecutionFlow flow : if_execution.get_ou_flows()) {
							if(element.get_predicate_value() && flow.get_type() == CirExecutionFlowType.true_flow) {
								dependence_flows.add(flow); break;
							}
							else if(!element.get_predicate_value() && flow.get_type() == CirExecutionFlowType.fals_flow) {
								dependence_flows.add(flow); break;
							}
						}
						next_node = dependence_edge.get_target();
						break;
					}
					else if(dependence_edge.get_type() == CDependType.stmt_call_depend) {
						next_node = dependence_edge.get_target();
						CirExecution call_execution = next_node.get_execution();
						dependence_flows.add(call_execution.get_ou_flow(0));
						break;
					}
					else if(dependence_edge.get_type() == CDependType.stmt_exit_depend) {
						CirInstanceNode wait_instance = dependence_edge.get_target().get_instance();
						next_node = dependence_graph.get_node(wait_instance.get_in_edge(0).get_source());
						CirExecutionFlow retr_flow = wait_instance.get_in_edge(0).get_flow();
						if(retr_flow.get_type() == CirExecutionFlowType.retr_flow) 
							dependence_flows.add(retr_flow);
						break;
					}
				}
				dependence_node = next_node;
			}
			
			for(int k = 0; k < dependence_flows.size() / 2; k++) {
				CirExecutionFlow x = dependence_flows.get(k);
				CirExecutionFlow y = dependence_flows.get(dependence_flows.size() - 1 - k);
				dependence_flows.set(k, y); 
				dependence_flows.set(dependence_flows.size() - 1 - k, x);
			}
			return this.virtual_bridge(source, instance.get_execution(), dependence_flows);
		}
	}
	/**
	 * @param dependence_graph
	 * @param target
	 * @return
	 * @throws Exception
	 */
	public Set<CirExecutionPath> dependence_paths(CDependGraph dependence_graph, CirExecution target) throws Exception {
		if(dependence_graph == null)
			throw new IllegalArgumentException("Invalid dependence_graph: null");
		else if(target == null)
			throw new IllegalArgumentException("Invalid target as " + target);
		else {
			CirInstanceGraph instance_graph = dependence_graph.get_program_graph();
			if(instance_graph.has_instances_of(target)) {
				Set<CirExecutionPath> dependence_paths = new HashSet<CirExecutionPath>();
				for(CirInstanceNode instance : instance_graph.get_instances_of(target)) {
					if(dependence_graph.has_node(instance)) {
						dependence_paths.add(this.dependence_path(dependence_graph, instance));
					}
				}
				return dependence_paths;
			}
			else {
				return this.simple_paths(target.get_graph().get_entry(), target);
			}
			
		}
	}
	
	/* instrumental path creation */
	/**
	 * @param state_path
	 * @return the execution path connecting statements in testing
	 * @throws Exception
	 */
	public CirExecutionPath instrumental_path(CStatePath state_path) throws Exception {
		if(state_path == null || state_path.size() == 0)
			throw new IllegalArgumentException("Invalid state_path: null");
		else {
			CirExecutionPath path = new CirExecutionPath(state_path.get_node(0).get_execution());
			int prev_index = 0;
			for(int k = 1; k < state_path.size(); k++) {
				CStateNode prev_node = state_path.get_node(k - 1);
				CStateNode next_node = state_path.get_node(k);
				
				if(!this.decidable_extend(path, next_node.get_execution())) {
					path.add(CirExecutionFlow.virtual_flow(CirExecutionFlowType.next_flow, path.get_target(), next_node.get_execution()));
				}
				
				CirExecutionEdge prev_edge = path.get_edge(prev_index);
				CirExecutionEdge post_edge = path.peek();
				prev_edge.set_annotation(prev_node.get_units());
				prev_index = post_edge.get_index();
			}
			path.peek().set_annotation(state_path.get_last_node().get_units());
			return path;
		}
	}
	
}
