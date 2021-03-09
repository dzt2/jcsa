package com.jcsa.jcparse.lang.irlang.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
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
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.stmt.CirBegStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirEndStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.test.state.CStateNode;
import com.jcsa.jcparse.test.state.CStatePath;

/**
 * 	It implements the path finding algorithms and provide following methods for users to apply.
 * 	
 * 	@author yukimula
 *
 */
public class CirExecutionPathFinder {
	
	/* singleton method */
	private Random random = new Random(System.currentTimeMillis());
	/** private constructor for creating path finder **/
	private CirExecutionPathFinder() { }
	/** the instance of single path finder used to perform path finding algorithm **/
	public static final CirExecutionPathFinder finder = new CirExecutionPathFinder();
	
	/* basic methods */
	/**
	 * collect the set of execution flows available being traversed from the path.target
	 * @param path
	 * @param flows to preserve the available execution flows performed from path.target
	 * @throws Exception
	 */
	private void collect_next_flows(CirExecutionPath path, Collection<CirExecutionFlow> flows) throws Exception {
		if(path == null)
			throw new IllegalArgumentException("Invalid path: null");
		else if(flows == null)
			throw new IllegalArgumentException("Invalid next_flows");
		else {
			/* declarations */
			CirExecution execution = path.get_target();
			CirExecutionFlow call_flow; flows.clear();
			
			if(execution.get_ou_degree() == 0) return;	/* no available flow exists */
			else if(execution.get_ou_degree() == 1) {	/* decidable unique next flow */
				flows.add(execution.get_ou_flow(0));
			}
			else if(execution.get_statement() instanceof CirEndStatement) {
				call_flow = path.final_call_flow();
				if(call_flow != null) {					/* find available return flow */
					for(CirExecutionFlow retr_flow : execution.get_ou_flows()) {
						if(CirExecutionFlow.match_call_retr_flow(call_flow, retr_flow)) {
							flows.add(retr_flow); break;
						}
					}
				}
				else return;							/* to avoid across the function */
			}
			else {										/* condition branches available */
				for(CirExecutionFlow flow : execution.get_ou_flows()) flows.add(flow);
			}
		}
	}
	/**
	 * collect the set of execution flows available reaching to the path.source
	 * @param path
	 * @param flows
	 * @throws Exception
	 */
	private void collect_prev_flows(CirExecutionPath path, Collection<CirExecutionFlow> flows) throws Exception {
		if(path == null)
			throw new IllegalArgumentException("Invalid path: null");
		else if(flows == null)
			throw new IllegalArgumentException("Invalid next_flows");
		else {
			/* declarations */
			CirExecution execution = path.get_source();
			CirExecutionFlow retr_flow; flows.clear();
			
			if(execution.get_in_degree() == 0) return;	/* no available flow exists */
			else if(execution.get_in_degree() == 1) {	/* decidable unique previous flow */
				flows.add(execution.get_in_flow(0));
			}
			else if(execution.get_statement() instanceof CirBegStatement) {
				retr_flow = path.first_retr_flow();
				if(retr_flow != null) {					/* find available call flow */
					for(CirExecutionFlow call_flow : execution.get_in_flows()) {
						if(CirExecutionFlow.match_call_retr_flow(call_flow, retr_flow)) {
							flows.add(call_flow); break;
						}
					}
				}
				else return;							/* to avoid across the function */
			}
			else {										/* multiple input flows available */
				for(CirExecutionFlow flow : execution.get_in_flows()) flows.add(flow);
			}
		}
	}
	/**
	 * using virtual edge to connect the prev_path.target to the post_path.source, of which edges will be
	 * appended in the tail of the prev_path while the state of post_path remains unchanged.
	 * @param prev_path
	 * @param post_path
	 * @throws Exception
	 */
	private void virtual_l_bridge(CirExecutionPath prev_path, CirExecutionPath post_path) throws Exception {
		if(prev_path == null)
			throw new IllegalArgumentException("Invalid prev_path: null");
		else if(post_path == null)
			throw new IllegalArgumentException("Invalid post_path: null");
		else {
			/* build a virtual edge from prev_path to post_path */
			if(prev_path.get_target() != post_path.get_source()) {
				/* using actual flow (try to manage to do so) */
				CirExecution source = prev_path.get_target();
				CirExecution target = post_path.get_source();
				boolean is_linked = false;
				for(CirExecutionFlow flow : source.get_ou_flows()) {
					if(flow.get_target() == target) {
						prev_path.add_final(flow);
						is_linked = true; break;
					}
				}
				/* using virtual flow to link otherwise */
				if(!is_linked) {
					prev_path.add_final(CirExecutionFlow.virtual_flow(source, target));
				}
			}
			/* append the edges in post_path to the prev_path */
			for(CirExecutionEdge post_edge : post_path.get_edges()) {
				prev_path.add_final(post_edge.get_flow());
			}
		}
	}
	/**
	 * using virtual edge to connect the prev_path.target to the post_path.source, of which edges will be
	 * appended in the head of the post_path while the state of prev_path remains unchanged.
	 * @param prev_path
	 * @param post_path
	 * @throws Exception
	 */
	private void virtual_r_bridge(CirExecutionPath prev_path, CirExecutionPath post_path) throws Exception {
		if(prev_path == null)
			throw new IllegalArgumentException("Invalid prev_path: null");
		else if(post_path == null)
			throw new IllegalArgumentException("Invalid post_path: null");
		else {
			/* build a virtual edge from prev_path to post_path */
			if(prev_path.get_target() != post_path.get_source()) {
				/* using actual flow (try to manage to do so) */
				CirExecution source = prev_path.get_target();
				CirExecution target = post_path.get_source();
				boolean is_linked = false;
				for(CirExecutionFlow flow : source.get_ou_flows()) {
					if(flow.get_target() == target) {
						post_path.add_first(flow);
						is_linked = true; break;
					}
				}
				/* using virtual flow to link otherwise */
				if(!is_linked) {
					post_path.add_first(CirExecutionFlow.virtual_flow(source, target));
				}
			}
			/* append the edges in post_path to the prev_path */
			Iterator<CirExecutionEdge> iterator = prev_path.get_reverse_edges();
			while(iterator.hasNext()) {
				post_path.add_first(iterator.next().get_flow());
			}
		}
	}
	
	/* decidable path extension */
	/**
	 * Decidable Forward Path Extension:<br>
	 * extend the prefix path using decidable extension algorithm until the point of which output flows are undecidable.
	 * @param prev_path the prefix path being extended from its original target to the undecidable point in following.
	 * @return the number of execution flows appended in the tail of the previous path as given
	 * @throws Exception
	 */
	public int df_extend(CirExecutionPath prev_path) throws Exception {
		if(prev_path == null)
			throw new IllegalArgumentException("Invalid prev_path: null");
		else {
			List<CirExecutionFlow> flows = new ArrayList<CirExecutionFlow>();
			int counter = 0;
			while(true) {
				this.collect_next_flows(prev_path, flows);
				if(flows.size() == 0) break;	/* none of available output flows */
				else if(flows.size() == 1) {	/* decidable unique next flow */
					prev_path.add_final(flows.get(0));
				}
				else break;						/* reaching undecidable point */
			}
			return counter;
		}
	}
	/**
	 * Decidable Backward Path Extension.<br>
	 * extend the following path backward using decidable extension algorithm until the point of which input flows are not
	 * decidable.
	 * @param post_path the following path being extended backward from its source to the undecidable point reaching it.
	 * @return the number of execution edges appended before the following path's source.
	 * @throws Exception
	 */
	public int db_extend(CirExecutionPath post_path) throws Exception {
		if(post_path == null)
			throw new IllegalArgumentException("Invalid post_path: null");
		else {
			List<CirExecutionFlow> flows = new ArrayList<CirExecutionFlow>();
			int counter = 0;
			while(true) {
				this.collect_prev_flows(post_path, flows);
				if(flows.size() == 0) break;	/* none of available output flows */
				else if(flows.size() == 1) {	/* decidable unique next flow */
					post_path.add_first(flows.get(0));
				}
				else break;						/* reaching undecidable point */
			}
			return counter;
		}
	}
	/**
	 * Decidable Forward Path Extension.<br>
	 * To extend the prefix path until reaching the target or the undecidable point.
	 * @param prev_path the prefix path being extended from its original target
	 * @param target the statement that the prefix path tries to extend to
	 * @return whether the prefix path is extended to reach the target finally.
	 * @throws Exception
	 */
	public boolean df_extend(CirExecutionPath prev_path, CirExecution target) throws Exception {
		if(prev_path == null)
			throw new IllegalArgumentException("Invalid prev_path: null");
		else if(target == null)
			throw new IllegalArgumentException("Invalid target as null");
		else {
			List<CirExecutionFlow> flows = new ArrayList<CirExecutionFlow>();
			while(prev_path.get_target() != target) {
				this.collect_next_flows(prev_path, flows);
				if(flows.size() == 0) break;	/* none of available output flows */
				else if(flows.size() == 1) {	/* decidable unique next flow */
					prev_path.add_final(flows.get(0));
				}
				else {							/* last step connection being tried */
					for(CirExecutionFlow flow : flows) {
						if(flow.get_target() == target) {
							prev_path.add_final(flow);
							break;
						}
					}
					break;						/* undecidable point being reached */
				}
			}
			return prev_path.get_target() == target;	/* return true if reaching target */
		}
	}
	/**
	 * Decidable Backward Path Extension.<br>
	 * To extend the following path backward until the source or the undecidable point.
	 * @param post_path the following path being extended from its original source
	 * @param source the statement that the following path tries to extend backward to.
	 * @return whether the following path is extended to reach the source finally.
	 * @throws Exception
	 */
	public boolean db_extend(CirExecutionPath post_path, CirExecution source) throws Exception {
		if(post_path == null)
			throw new IllegalArgumentException("Invalid post_path: null");
		else if(source == null)
			throw new IllegalArgumentException("Invalid source as null");
		else {
			List<CirExecutionFlow> flows = new ArrayList<CirExecutionFlow>();
			while(post_path.get_source() != source) {
				this.collect_prev_flows(post_path, flows);
				if(flows.size() == 0) break;	/* none of available input flows */
				else if(flows.size() == 1) {	/* decidable unique input flow */
					post_path.add_first(flows.get(0));
				}
				else {							/* last step connection being tried */
					for(CirExecutionFlow flow : flows) {
						if(flow.get_source() == source) {
							post_path.add_first(flow);
							break;
						}
					}
					break;						/* undecidable point being reached */
				}
			}
			return post_path.get_source() == source;	/* return true if reaching source */
		}
	}
	/**
	 * Decidable Forward Path Extension.<br>
	 * To extend the prefix path until the target flow or the point of which output flows are undecidable.
	 * @param prev_path the prefix path being extended to the target flow or undecidable node
	 * @param flow the target flow being reached from the original target of the prefix path
	 * @return true if the prefix path is extended to reach the target flow
	 * @throws Exception
	 */
	public boolean df_extend(CirExecutionPath prev_path, CirExecutionFlow flow) throws Exception {
		if(prev_path == null)
			throw new IllegalArgumentException("Invalid prev_path: null");
		else if(flow == null)
			throw new IllegalArgumentException("Invalid flow as null");
		else if(flow.equals(prev_path.get_final_flow())) return true;		/* reaching yet */
		else {
			if(this.df_extend(prev_path, flow.get_source())) {	/* decidable extend to its source first */
				prev_path.add_final(flow); 						/* and then connect with the flow further */
			}
			return flow.equals(prev_path.get_final_flow());		/* return true if reaching flow */
		}
	}
	/**
	 * Decidable Backward Path Extension.<br>
	 * To extend the following path backward from its source until the flow or the undecidable point.
	 * @param post_path the following path being extended backward from its source until the flow
	 * @param flow the flow tried to be reached from the source of the following path
	 * @return true if the following path manages to reach the flow backward.
	 * @throws Exception
	 */
	public boolean db_extend(CirExecutionPath post_path, CirExecutionFlow flow) throws Exception {
		if(post_path == null)
			throw new IllegalArgumentException("Invalid post_path: null");
		else if(flow == null)
			throw new IllegalArgumentException("Invalid flow as null");
		else if(flow.equals(post_path.get_first_flow())) return true;		/* reach yet */
		else {
			if(this.db_extend(post_path, flow.get_target())) {	/* try to reach the flow.source backwardly */
				post_path.add_first(flow);						/* then back-connect the path to the flow. */
			}
			return flow.equals(post_path.get_first_flow());
		}
	}
	/**
	 * @param source
	 * @return the decidable path starting from the source until the undecidable point.
	 * @throws Exception
	 */
	public CirExecutionPath df_extend(CirExecution source) throws Exception {
		CirExecutionPath path = new CirExecutionPath(source);
		this.df_extend(path);
		return path;
	}
	/**
	 * @param target
	 * @return the decidable path terminating at target from the undecidable source.
	 * @throws Exception
	 */
	public CirExecutionPath db_extend(CirExecution target) throws Exception {
		CirExecutionPath path = new CirExecutionPath(target);
		this.db_extend(path);
		return path;
	}
	
	/* virtual bridge algorithms */
	/**
	 * Virtual Forward Path Extension.<br>
	 * 	1. Use decidable forward extension to extend prefix path to target.
	 * 	2. If failed, use decidable backward extension to target, trying to link the prev_path.target.
	 * 	3. If failed, use virtual bridge to connect the prev_path and newly created post_path.
	 * @param prev_path
	 * @param target
	 * @return
	 * @throws Exception
	 */
	public void vf_extend(CirExecutionPath prev_path, CirExecution target) throws Exception {
		if(prev_path == null)
			throw new IllegalArgumentException("Invalid prev_path: null");
		else if(target == null)
			throw new IllegalArgumentException("Invalid target as null");
		else {
			if(!this.df_extend(prev_path, target)) {
				CirExecutionPath post_path = new CirExecutionPath(target);
				this.db_extend(post_path, prev_path.get_target());
				this.virtual_l_bridge(prev_path, post_path);
			}
		}
	}
	/**
	 * Virtual Backward Path Extension.<br>
	 * 	1. Use decidable backward extension to extend the following path to source.
	 * 	2. If failed, use decidable forward extension to build a path from source to post_path.source.
	 * 	3. If failed, use virtual bridge to connect from the new prev_path to the post_path.
	 * @param post_path
	 * @param source
	 * @throws Exception
	 */
	public void vb_extend(CirExecutionPath post_path, CirExecution source) throws Exception {
		if(post_path == null)
			throw new IllegalArgumentException("Invalid post_path: null");
		else if(source == null)
			throw new IllegalArgumentException("Invalid source as null");
		else {
			if(!this.db_extend(post_path, source)) {
				CirExecutionPath prev_path = new CirExecutionPath(source);
				this.df_extend(prev_path, post_path.get_source());
				this.virtual_r_bridge(prev_path, post_path);
			}
		}
	}
	/**
	 * Virtual Forward Path Extension.<br>
	 * 	1. Use decidable forward extension to extend prefix path to flow.
	 * 	2. If failed, use decidable backward extension to flow.source, trying to link to prev_path.target.
	 * 	3. If failed, use virtual bridge to connect the prev_path and newly created post_path.
	 * @param prev_path the prefix path being extended to the target flow using virtual extension.
	 * @param flow the flow being reached from the prefix path
	 * @throws Exception
	 */
	public void vf_extend(CirExecutionPath prev_path, CirExecutionFlow flow) throws Exception {
		if(prev_path == null)
			throw new IllegalArgumentException("Invalid prev_path: null");
		else if(flow == null)
			throw new IllegalArgumentException("Invalid flow as null");
		else {
			if(!this.df_extend(prev_path, flow)) {
				CirExecutionPath post_path = new CirExecutionPath(flow.get_source());
				post_path.add_final(flow);
				this.db_extend(post_path, prev_path.get_target());
				this.virtual_l_bridge(prev_path, post_path);
			}
		}
	}
	/**
	 * Virtual Backward Path Extension.<br>
	 * 	1. Use decidable backward extension for linking from post_path.source to flow.target.
	 * 	2. If failed, use decidable forward extension to create a new prev_path from flow.target, including flow, until the post_path.source.
	 * 	3. If failed, use virtual bridge to connect the newly prev_path to the post_path being updated.
	 * @param post_path
	 * @param flow
	 * @throws Exception
	 */
	public void vb_extend(CirExecutionPath post_path, CirExecutionFlow flow) throws Exception {
		if(post_path == null)
			throw new IllegalArgumentException("Invalid post_path: null");
		else if(flow == null)
			throw new IllegalArgumentException("Invalid flow as null");
		else {
			if(!this.db_extend(post_path, flow)) {
				CirExecutionPath prev_path = new CirExecutionPath(flow.get_target());
				prev_path.add_first(flow);
				this.df_extend(prev_path, post_path.get_source());
				this.virtual_r_bridge(prev_path, post_path);
			}
		}
	}
	
	/* simple path finding methods */
	/**
	 * Find the simple paths from the prefix path using the input flow until the target
	 * @param prev_path
	 * @param flow
	 * @param target
	 * @param paths to preserve the path solutions from prefix until the target
	 * @throws Exception
	 */
	private void find_simple_forward_paths(CirExecutionPath prev_path, CirExecutionFlow flow, 
			CirExecution target, Collection<CirExecutionPath> paths) throws Exception {
		if(!prev_path.has_edge_of(flow)) {	/* to avoid non-simple path */
			prev_path.add_final(flow);		/* push the flow into prev_path */
			
			if(flow.get_target() == target) {
				paths.add(prev_path.clone());	/* find a solution to target */
			}
			else {								/* recursively search on next flows */
				List<CirExecutionFlow> next_flows = new ArrayList<CirExecutionFlow>();
				this.collect_next_flows(prev_path, next_flows);
				for(CirExecutionFlow next_flow : next_flows) {
					this.find_simple_forward_paths(prev_path, next_flow, target, paths);
				}
			}
			
			prev_path.del_final();			/* pop the flow from prev_path */
		}
	}
	/**
	 * Find the simple paths from the source to the source of the following path.
	 * @param post_path
	 * @param flow
	 * @param source
	 * @param paths
	 * @throws Exception
	 */
	private void find_simple_bakward_paths(CirExecutionPath post_path, CirExecutionFlow flow, 
			CirExecution source, Collection<CirExecutionPath> paths) throws Exception {
		if(!post_path.has_edge_of(flow)) {	/* to avoid non-simple path */
			post_path.add_first(flow);		/* push the flow in the head of the path */
			
			if(flow.get_source() == flow.get_source()) {
				paths.add(post_path.clone());	/* find a solution to source */
			}
			else {								/* recursively search on prev flows */
				List<CirExecutionFlow> prev_flows = new ArrayList<CirExecutionFlow>();
				this.collect_prev_flows(post_path, prev_flows);
				for(CirExecutionFlow prev_flow : prev_flows) {
					this.find_simple_bakward_paths(post_path, prev_flow, source, paths);
				}
			}
			
			post_path.del_first();			/* pop the first flow from the head of path */
		}
	}
	/**
	 * @param prev_path the prefix path of which state might be changed
	 * @param target 
	 * @return the set of simple paths that can reach the target from its original target in the prefix path
	 * @throws Exception
	 */
	public Collection<CirExecutionPath> sf_extend(CirExecutionPath prev_path, CirExecution target) throws Exception {
		if(prev_path == null)
			throw new IllegalArgumentException("Invalid prev_path: null");
		else if(target == null)
			throw new IllegalArgumentException("Invalid target as null");
		else {
			Set<CirExecutionPath> paths = new HashSet<CirExecutionPath>();
			if(this.df_extend(prev_path, target)) {
				paths.add(prev_path.clone());
			}
			else {
				List<CirExecutionFlow> next_flows = new ArrayList<CirExecutionFlow>();
				this.collect_next_flows(prev_path, next_flows);
				for(CirExecutionFlow next_flow : next_flows) {
					this.find_simple_forward_paths(prev_path, next_flow, target, paths);
				}
			}
			return paths;
		}
	}
	/**
	 * @param prev_path the prefix path of which state might be changed
	 * @param flow the execution flow being reached from the original target of prefix path
	 * @return the set of simple paths that can reach the source of target flow and include the flow itself.
	 * @throws Exception
	 */
	public Collection<CirExecutionPath> sf_extend(CirExecutionPath prev_path, CirExecutionFlow flow) throws Exception {
		if(prev_path == null)
			throw new IllegalArgumentException("Invalid prev_path: null");
		else if(flow == null)
			throw new IllegalArgumentException("Invalid flow as null");
		else {
			Set<CirExecutionPath> paths = new HashSet<CirExecutionPath>();
			if(this.df_extend(prev_path, flow)) {
				paths.add(prev_path.clone());
			}
			else {
				List<CirExecutionFlow> next_flows = new ArrayList<CirExecutionFlow>();
				this.collect_next_flows(prev_path, next_flows);
				for(CirExecutionFlow next_flow : next_flows) {
					this.find_simple_forward_paths(prev_path, next_flow, flow.get_source(), paths);
				}
				for(CirExecutionPath path : paths) path.add_final(flow);
			}
			return paths;
		}
	}
	/**
	 * @param post_path the following path of which state might be changed
	 * @param source the statement being reached from original source of the post_path backwardly
	 * @return the set of simple paths from source to the original source of post_path
	 * @throws Exception
	 */
	public Collection<CirExecutionPath> sb_extend(CirExecutionPath post_path, CirExecution source) throws Exception {
		if(post_path == null)
			throw new IllegalArgumentException("Invalid post_path: null");
		else if(source == null)
			throw new IllegalArgumentException("Invalid source as null");
		else {
			Set<CirExecutionPath> paths = new HashSet<CirExecutionPath>();
			if(this.db_extend(post_path, source)) {
				paths.add(post_path);
			}
			else {
				List<CirExecutionFlow> prev_flows = new ArrayList<CirExecutionFlow>();
				this.collect_prev_flows(post_path, prev_flows);
				for(CirExecutionFlow prev_flow : prev_flows) {
					this.find_simple_bakward_paths(post_path, prev_flow, source, paths);
				}
			}
			return paths;
		}
	}
	/**
	 * @param post_path the following path of which state might be changed
	 * @param flow the flow that is linked to the original source of the post_path
	 * @return the set of simple paths connecting from the source 
	 * @throws Exception
	 */
	public Collection<CirExecutionPath> sb_extend(CirExecutionPath post_path, CirExecutionFlow flow) throws Exception {
		if(post_path == null)
			throw new IllegalArgumentException("Invalid post_path: null");
		else if(flow == null)
			throw new IllegalArgumentException("Invalid flow as null");
		else {
			Set<CirExecutionPath> paths = new HashSet<CirExecutionPath>();
			if(this.db_extend(post_path, flow)) {
				paths.add(post_path);
			}
			else {
				List<CirExecutionFlow> prev_flows = new ArrayList<CirExecutionFlow>();
				this.collect_prev_flows(post_path, prev_flows);
				for(CirExecutionFlow prev_flow : prev_flows) {
					this.find_simple_bakward_paths(post_path, prev_flow, flow.get_target(), paths);
				}
				for(CirExecutionPath path : paths) path.add_first(flow);
			}
			return paths;
		}
	}
	
	/* path selection methods */
	/**
	 * @param paths
	 * @return the path with maximal length in the set
	 */
	public CirExecutionPath max_path(Collection<CirExecutionPath> paths) {
		CirExecutionPath max_path = null;
		int max_length = 0;
		if(paths != null && !paths.isEmpty()) {
			for(CirExecutionPath path : paths) {
				if(path.length() >= max_length) {
					max_path = path;
					max_length = path.length();
				}
			}
		}
		return max_path;
	}
	/**
	 * @param paths
	 * @return the path with minimal length in the set
	 */
	public CirExecutionPath min_path(Collection<CirExecutionPath> paths) {
		CirExecutionPath min_path = null;
		int min_length = Integer.MAX_VALUE;
		if(paths != null && !paths.isEmpty()) {
			for(CirExecutionPath path : paths) {
				if(path.length() <= min_length) {
					min_path = path;
					min_length = path.length();
				}
			}
		}
		return min_path;
	}
	/**
	 * @param paths
	 * @return a random path extracted from the set
	 */
	public CirExecutionPath rad_path(Collection<CirExecutionPath> paths) {
		CirExecutionPath rand_path = null;
		if(paths != null && !paths.isEmpty()) {
			int length = paths.size();
			int counter = Math.abs(random.nextInt()) % length;
			for(CirExecutionPath path : paths) {
				rand_path = path;
				if(counter-- <= 0) break;
			}
		}
		return rand_path;
	}
	
	/* user-level path generation */
	/**
	 * @param state_path
	 * @return the execution path generated from the state-path of instrumentation
	 * 		   of which flow is correlated with annotation as List[CStateUnit] of
	 * 		   the evaluated values of each expression before the edge.target is
	 * 		   executed in testing.
	 * @throws Exception
	 */
	public CirExecutionPath instrumental_path(CStatePath state_path) throws Exception {
		if(state_path == null || state_path.size() == 0)
			throw new IllegalArgumentException("Empty state-path is invalid");
		else {
			CirTree cir_tree = state_path.get_cir_tree();
			CirExecutionPath path = new CirExecutionPath(
					cir_tree.get_function_call_graph().
					get_main_function().get_flow_graph().get_entry());
			for(CStateNode state_node : state_path.get_nodes()) {
				this.vf_extend(path, state_node.get_execution());
				CirExecutionEdge edge = path.get_final();
				if(edge != null) {
					edge.set_annotation(state_node);
				}
			}
			return path;
		}
	}
	private List<CirExecutionFlow> dependence_flows(CDependGraph dependence_graph, CirInstanceNode instance) throws Exception {
		CDependNode dependence_node = dependence_graph.get_node(instance), next_node;
		List<CirExecutionFlow> dependence_flows = new ArrayList<CirExecutionFlow>();
		
		while(dependence_node != null) {
			/* traverse the control dependence path until none */
			next_node = null;
			
			/* traverse the control dependence edges from the node */
			for(CDependEdge dependence_edge : dependence_node.get_ou_edges()) {
				next_node = null;
				if(dependence_edge.get_type() == CDependType.predicate_depend) {
					CDependPredicate element = (CDependPredicate) dependence_edge.get_element();
					CirStatement if_statement = element.get_statement();
					CirExecution if_execution = if_statement.get_tree().get_localizer().get_execution(if_statement);
					for(CirExecutionFlow flow : if_execution.get_ou_flows()) {
						if(flow.get_type() == CirExecutionFlowType.true_flow && element.get_predicate_value()) {
							dependence_flows.add(flow); break;
						}
						else if(flow.get_type() == CirExecutionFlowType.fals_flow && !element.get_predicate_value()) {
							dependence_flows.add(flow); break;
						}
					}
					next_node = dependence_edge.get_target();
				}
				else if(dependence_edge.get_type() == CDependType.stmt_exit_depend) {
					CirInstanceNode wait_instance = dependence_edge.get_target().get_instance();
					CirInstanceNode exit_instance = wait_instance.get_in_edge(0).get_source();
					dependence_flows.add(wait_instance.get_in_edge(0).get_flow());
					next_node = dependence_graph.get_node(exit_instance);
				}
				else if(dependence_edge.get_type() == CDependType.stmt_call_depend) {
					CirInstanceNode call_instance = dependence_edge.get_target().get_instance();
					CirExecutionFlow flow = call_instance.get_ou_edge(0).get_flow();
					if(flow.get_type() == CirExecutionFlowType.call_flow)
						dependence_flows.add(flow);
					next_node = dependence_edge.get_target();
				}
				if(next_node != null) break;	/* when find control dependence */
			}
			
			dependence_node = next_node;
		}
		
		for(int k = 0; k < dependence_flows.size() / 2; k++) {
			CirExecutionFlow x = dependence_flows.get(k);
			CirExecutionFlow y = dependence_flows.get(dependence_flows.size() - 1 - k);
			dependence_flows.set(k, y); dependence_flows.set(dependence_flows.size() - 1 - k, x);
		}
		
		return dependence_flows;
	}
	/**
	 * @param dependence_graph
	 * @param instance
	 * @return the dependence path from program entry to the node of instance execution.
	 * @throws Exception 
	 */
	public CirExecutionPath dependence_path(CDependGraph dependence_graph, CirInstanceNode instance) throws Exception {
		if(dependence_graph == null)
			throw new IllegalArgumentException("Invalid dependence_graph: null");
		else if(instance == null) 
			throw new IllegalArgumentException("Invalid instance: null");
		else if(!dependence_graph.has_node(instance)) {	/* virtual path from function entry */
			CirExecution source = instance.get_execution().get_graph().get_entry();
			CirExecution target = instance.get_execution();
			CirExecutionPath path = new CirExecutionPath(source);
			this.vf_extend(path, target); return path;
		}
		else {
			List<CirExecutionFlow> dependence_flows = this.dependence_flows(dependence_graph, instance);
			CirExecutionPath path = new CirExecutionPath(dependence_graph.get_program_graph().get_cir_tree().
					get_function_call_graph().get_main_function().get_flow_graph().get_entry());
			for(CirExecutionFlow dependence_flow : dependence_flows) this.vf_extend(path, dependence_flow);
			this.vf_extend(path, instance.get_execution()); return path;
		}
	}
	/**
	 * 
	 * @param dependence_graph
	 * @param execution
	 * @return
	 * @throws Exception
	 */
	public CirExecutionPath dependence_path(CDependGraph dependence_graph, CirExecution execution) throws Exception {
		if(dependence_graph == null)
			throw new IllegalArgumentException("Invalid dependence_graph: null");
		else if(execution == null) 
			throw new IllegalArgumentException("Invalid execution as null");
		else {
			CirInstanceGraph instance_graph = dependence_graph.get_program_graph();
			if(instance_graph.has_instances_of(execution)) {
				List<CirExecutionFlow> common_flows = new ArrayList<CirExecutionFlow>();
				Set<CirExecutionFlow> removed_flows = new HashSet<CirExecutionFlow>();
				List<CirExecutionFlow> dependence_flows;
				
				boolean first = true;
				for(CirInstanceNode instance : instance_graph.get_instances_of(execution)) {
					dependence_flows = this.dependence_flows(dependence_graph, instance);
					if(first) {
						first = false;
						common_flows.addAll(dependence_flows);
					}
					else {
						removed_flows.clear();
						for(CirExecutionFlow flow : dependence_flows) {
							if(!common_flows.contains(flow)) {
								removed_flows.add(flow);
							}
						}
						common_flows.removeAll(removed_flows);
					}
				}
				
				CirExecutionPath path = new CirExecutionPath(dependence_graph.get_program_graph().get_cir_tree().
						get_function_call_graph().get_main_function().get_flow_graph().get_entry());
				for(CirExecutionFlow dependence_flow : common_flows) this.vf_extend(path, dependence_flow);
				this.vf_extend(path, execution); return path;
			}
			else {
				CirExecution source = execution.get_graph().get_entry();
				CirExecution target = execution;
				CirExecutionPath path = new CirExecutionPath(source);
				this.vf_extend(path, target); return path;
			}
		}
	}
	
}
