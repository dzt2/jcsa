package com.jcsa.jcparse.lang.irlang.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jcsa.jcparse.flwa.depend.CDependEdge;
import com.jcsa.jcparse.flwa.depend.CDependGraph;
import com.jcsa.jcparse.flwa.depend.CDependNode;
import com.jcsa.jcparse.flwa.depend.CDependPredicate;
import com.jcsa.jcparse.flwa.depend.CDependType;
import com.jcsa.jcparse.flwa.graph.CirInstanceGraph;
import com.jcsa.jcparse.flwa.graph.CirInstanceNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.test.state.CStateNode;
import com.jcsa.jcparse.test.state.CStatePath;

/**
 * It implements context-sensitive path extension.
 * 
 * @author yukimula
 *
 */
public class CirExecutionPathFinder {
	
	/* singleton mode */	/** constructor **/	 private CirExecutionPathFinder() { }
	/** whether to include cross-function paths **/	  private boolean cross_function;
	public static final CirExecutionPathFinder finder = new CirExecutionPathFinder();
	/**
	 * set the flag to determine whether to search cross function when it is undecidable
	 * @param cross_function
	 */
	public void set_cross_function(boolean cross_function) { this.cross_function = cross_function; }
	
	/* forward decidable path extensions */
	/**
	 * decidable extend the path forward, until its undecidable point in testing
	 * @param path
	 * @return the number of edges appended at the tail of the execution path
	 * @throws Exception
	 */
	public int 		df_extend(CirExecutionPath path) throws Exception {
		if(path == null) {
			throw new IllegalArgumentException("Invalid path: null");
		}
		else {
			int counter = 0;	/* to account the edges appended */
			while(true) {
				Collection<CirExecutionFlow> ou_flows = path.
						get_ou_flows_of_target(cross_function);
				if(ou_flows.isEmpty()) { break;	/* none forward */ }
				else if(ou_flows.size() == 1) {	/* unique flows */
					path.append(ou_flows.iterator().next());
					counter++;
				}
				else { break;	/* undecidable point is reached */ }
			}
			return counter;		/* the number of edges appended */
		}
	}
	/**
	 * decidable extend the path forward, until its undecidable point or target.
	 * @param path
	 * @param target
	 * @return whether the target is reached in forward traversal from the path
	 * @throws Exception
	 */
	public boolean	df_extend(CirExecutionPath path, CirExecution node) throws Exception {
		if(path == null) {
			throw new IllegalArgumentException("Invalid path: null");
		}
		else if(node == null) {
			throw new IllegalArgumentException("Invalid target: null");
		}
		else {
			while(path.get_target() != node) {
				Collection<CirExecutionFlow> ou_flows = path.
						get_ou_flows_of_target(cross_function);
				if(ou_flows.isEmpty()) { break;	/* none forward */ }
				else if(ou_flows.size() == 1) {	/* unique flows */
					path.append(ou_flows.iterator().next());
				}
				else {	/* try to link the target in final edge */
					for(CirExecutionFlow ou_flow : ou_flows) {
						if(ou_flow.get_target() == node) {
							path.append(ou_flow); break;
						}
					}
					break;	/* terminate search on undecidable */
				}
			}
			return path.get_target() == node;	
		}
	}
	/**
	 * decidable extend the path forward, until undecidable point or given flow.
	 * @param path
	 * @param flow
	 * @return whether the path includes the given flow in the tail of the path.
	 * @throws Exception
	 */
	public boolean	df_extend(CirExecutionPath path, CirExecutionFlow flow) throws Exception {
		if(path == null) {
			throw new IllegalArgumentException("Invalid path: null");
		}
		else if(flow == null) {
			throw new IllegalArgumentException("Invalid flow: null");
		}
		else {
			while(path.get_final_flow() != flow) {
				Collection<CirExecutionFlow> ou_flows = path.
						get_ou_flows_of_target(cross_function);
				if(ou_flows.isEmpty()) { break;	/* none forward */ }
				else if(ou_flows.size() == 1) {	/* unique flows */
					path.append(ou_flows.iterator().next());
				}
				else {	/* try to incorporate that flow in path */
					for(CirExecutionFlow ou_flow : ou_flows) {
						if(ou_flow == flow) {
							path.append(ou_flow); break;
						}
					}
					break;	/* terminate search on undecidable */
				}
			}
			return path.get_final_flow() == flow;
		}
	}
	/**
	 * decidable extend the path forward until undecidable point from the source
	 * @param source
	 * @return decidable path from the source until the first undecidable point.
	 * @throws Exception
	 */
	public CirExecutionPath df_extend(CirExecution source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else {
			CirExecutionPath path = new CirExecutionPath(source);
			this.df_extend(path);
			return path;
		}
	}
	
	/* backward decidable path extension */
	/**
	 * decidable extend the path backward until its undecidable point in testing
	 * @param path
	 * @return the number of edges inserted at the head of the execution path
	 * @throws Exception
	 */
	public int 		db_extend(CirExecutionPath path) throws Exception {
		if(path == null) {
			throw new IllegalArgumentException("Invalid path: null");
		}
		else {
			int counter = 0;	/* to account the edges inserted */
			while(true) {
				Collection<CirExecutionFlow> in_flows = path.
						get_in_flows_of_source(cross_function);
				if(in_flows.isEmpty()) { break;	/* none forward */ }
				else if(in_flows.size() == 1) {	/* unique flows */
					path.insert(in_flows.iterator().next());
					counter++;
				}
				else { break;	/* undecidable point is reached */ }
			}
			return counter;		/* the number of edges inserted. */
		}
	}
	/**
	 * decidable extend the path backward until its undecidable point or target.
	 * @param path
	 * @param node
	 * @return whether the target is reached in backward traversal to the path
	 * @throws Exception
	 */
	public boolean	db_extend(CirExecutionPath path, CirExecution node) throws Exception {
		if(path == null) {
			throw new IllegalArgumentException("Invalid path: null");
		}
		else if(node == null) {
			throw new IllegalArgumentException("Invalid target: null");
		}
		else {
			while(path.get_source() != node) {
				Collection<CirExecutionFlow> in_flows = path.
						get_in_flows_of_source(cross_function);
				if(in_flows.isEmpty()) { break;	/* none forward */ }
				else if(in_flows.size() == 1) {	/* unique flows */
					path.insert(in_flows.iterator().next());
				}
				else {	/* try to incorporate node in this path */
					for(CirExecutionFlow in_flow : in_flows) {
						if(in_flow.get_source() == node) {
							path.insert(in_flow); break;
						}
					}
					break;	/* terminate search on undecidable */
				}
			}
			return path.get_source() == node;
		}
	}
	/**
	 * decidable extend the path backward until undecidable point or given flow.
	 * @param path
	 * @param flow
	 * @return whether the path includes the given flow in the head of the path.
	 * @throws Exception
	 */
	public boolean	db_extend(CirExecutionPath path, CirExecutionFlow flow) throws Exception {
		if(path == null) {
			throw new IllegalArgumentException("Invalid path: null");
		}
		else if(flow == null) {
			throw new IllegalArgumentException("Invalid flow: null");
		}
		else {
			while(path.get_first_flow() != flow) {
				Collection<CirExecutionFlow> in_flows = path.
						get_in_flows_of_source(cross_function);
				if(in_flows.isEmpty()) { break;	/* none forward */ }
				else if(in_flows.size() == 1) {	/* unique flows */
					path.insert(in_flows.iterator().next());
				}
				else {	/* try to incorporate node in this path */
					for(CirExecutionFlow in_flow : in_flows) {
						if(in_flow == flow) {
							path.insert(in_flow);	break;
						}
					}
					break;	/* terminate search on undecidable */
				}
			}
			return path.get_first_flow() == flow;
		}
	}
	/**
	 * decidable extend the path from the target backward until undecidable node
	 * @param target
	 * @return
	 * @throws Exception
	 */
	public CirExecutionPath db_extend(CirExecution target) throws Exception {
		if(target == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else {
			CirExecutionPath path = new CirExecutionPath(target);
			this.db_extend(path);
			return path;
		}
	}
	
	/* forward virtual path extension */
	/**
	 * virtually extend the path forward, until it is linked to the target node.
	 * @param path
	 * @param node
	 * @throws Exception
	 */
	public void 	vf_extend(CirExecutionPath path, CirExecution node) throws Exception {
		if(path == null) {
			throw new IllegalArgumentException("Invalid path: null");
		}
		else if(node == null) {
			throw new IllegalArgumentException("Invalid target: null");
		}
		else {
			if(!this.df_extend(path, node)) {
				CirExecutionPath next_path = new CirExecutionPath(node);
				if(!this.db_extend(next_path, path.get_target())) {
					next_path.insert(CirExecutionFlow.virtual_flow(
							path.get_target(), next_path.get_source()));
				}
				path.r_connect(next_path);
			}
		}
	}
	/**
	 * virtually extend the path forward, until it is linked to include the flow
	 * @param path
	 * @param flow
	 * @throws Exception
	 */
	public void 	vf_extend(CirExecutionPath path, CirExecutionFlow flow) throws Exception {
		if(path == null) {
			throw new IllegalArgumentException("Invalid path: null");
		}
		else if(flow == null) {
			throw new IllegalArgumentException("Invalid flow: null");
		}
		else {
			if(!this.df_extend(path, flow)) {
				CirExecutionPath next_path = new CirExecutionPath(flow.get_source());
				next_path.append(flow);
				if(!this.db_extend(next_path, path.get_target())) {
					next_path.insert(CirExecutionFlow.virtual_flow(
							path.get_target(), next_path.get_source()));
				}
				path.r_connect(next_path);
			}
		}
	}
	/**
	 * virtually extend the path backward until it is linked to the target node.
	 * @param path
	 * @param node
	 * @throws Exception
	 */
	public void		vb_extend(CirExecutionPath path, CirExecution node) throws Exception {
		if(path == null) {
			throw new IllegalArgumentException("Invalid path: null");
		}
		else if(node == null) {
			throw new IllegalArgumentException("Invalid node: null");
		}
		else {
			if(!this.db_extend(path, node)) {
				CirExecutionPath prev_path = new CirExecutionPath(node);
				if(!this.df_extend(prev_path, path.get_source())) {
					prev_path.append(CirExecutionFlow.virtual_flow(
							prev_path.get_target(), path.get_source()));
				}
				path.l_connect(prev_path);
			}
		}
	}
	/**
	 * virtually extend the path backward until it is linked to the target flow.
	 * @param path
	 * @param flow
	 * @throws Exception
	 */
	public void 	vb_extend(CirExecutionPath path, CirExecutionFlow flow) throws Exception {
		if(path == null) {
			throw new IllegalArgumentException("Invalid path: null");
		}
		else if(flow == null) {
			throw new IllegalArgumentException("Invalid flow: null");
		}
		else {
			if(!this.db_extend(path, flow)) {
				CirExecutionPath prev_path = new CirExecutionPath(flow.get_source());
				prev_path.append(flow);
				if(!this.df_extend(prev_path, path.get_source())) {
					prev_path.append(CirExecutionFlow.virtual_flow(
							prev_path.get_target(), path.get_source()));
				}
				path.l_connect(prev_path);
			}
		}
	}
	
	/* dependence or instrumentation */
	/**
	 * @param dependence_graph
	 * @param instance
	 * @return the sequence of execution flows dominating the execution of target instance
	 * @throws Exception
	 */
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
	 * @return the execution path linking from program entry to the target instance in CFG graph via dependence points
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
			this.vf_extend(path, target);
			return path;
		}
		else {
			List<CirExecutionFlow> dependence_flows = this.dependence_flows(dependence_graph, instance);
			CirExecutionPath path = new CirExecutionPath(dependence_graph.get_program_graph().get_cir_tree().
					get_function_call_graph().get_main_function().get_flow_graph().get_entry());
			for(CirExecutionFlow dependence_flow : dependence_flows) this.vf_extend(path, dependence_flow);
			this.vf_extend(path, instance.get_execution());
			return path;
		}
	}
	/**
	 * @param dependence_graph
	 * @param execution
	 * @return the execution path linking from program entry to the target instance in CFG graph via dependence points
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
	/**
	 * @param state_path
	 * @return
	 * @throws Exception
	 */
	public CirExecutionPath instrument_path(CStatePath state_path) throws Exception {
		if(state_path == null || state_path.size() == 0)
			throw new IllegalArgumentException("Empty state-path is invalid");
		else {
			CirTree cir_tree = state_path.get_cir_tree();
			CirExecutionPath path = new CirExecutionPath(cir_tree.get_function_call_graph().
					get_main_function().get_flow_graph().get_entry());
			for(CStateNode state_node : state_path.get_nodes()) {
				this.vf_extend(path, state_node.get_execution());
				CirExecutionEdge edge = path.get_final_edge();
				if(edge != null) {
					edge.set_annotation(state_node);
				}
			}
			return path;
		}
	}
	
}
