package com.jcsa.jcmutest.mutant.cir2mutant.path;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutations;
import com.jcsa.jcparse.flwa.symbol.CStateContexts;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.test.state.CStateNode;
import com.jcsa.jcparse.test.state.CStatePath;


/**
 * It implements the algorithms to evaluate the status in symbolic instance graph.
 * 
 * @author yukimula
 *
 */
class SymInstanceEvaluator {
	
	/* used to manage the nodes and edges in local error propagation */
	/**
	 * Local graph in symbolic instance structure, defined as a unique input edge and a group of output edges
	 * (as the input edge of the following local graph)
	 * @author yukimula
	 *
	 */
	protected static class SymInstanceBlock {
		
		/* definitions */
		private SymInstanceEdge in_edge;
		private Collection<SymInstanceEdge> ou_edges;
		private SymInstanceBlock parent;
		private Collection<SymInstanceBlock> children;
		
		/* constructor */
		/**
		 * create the local block of the symbolic instance graph starting from the input edge
		 * @param in_edge
		 * @throws Exception
		 */
		protected SymInstanceBlock(SymInstanceBlock parent, SymInstanceEdge in_edge) throws Exception {
			if(in_edge.get_type() == SymInstanceEdgeType.infc_flow 
				|| in_edge.get_type() == SymInstanceEdgeType.oupa_flow) {
				this.in_edge = in_edge;
				this.ou_edges = new ArrayList<SymInstanceEdge>();
				Queue<SymInstanceNode> queue = new LinkedList<SymInstanceNode>();
				queue.add(in_edge.get_target());
				while(!queue.isEmpty()) {
					SymInstanceNode node = queue.poll();
					for(SymInstanceEdge edge : node.get_ou_edges()) {
						if(edge.get_type() == SymInstanceEdgeType.inpa_flow
							|| edge.get_type() == SymInstanceEdgeType.cont_flow) {
							queue.add(edge.get_target());
						}
						else {
							this.ou_edges.add(edge);
						}
					}
				}
			}
			else {
				throw new IllegalArgumentException("Invalid in_edge: " + in_edge.get_type());
			}
			
			this.parent = parent;
			this.children = new LinkedList<SymInstanceBlock>();
			for(SymInstanceEdge ou_edge : this.ou_edges) {
				this.children.add(new SymInstanceBlock(this, ou_edge));
			}
		}
		
		/* getters */
		/**
		 * @return the edge that starts the evaluation of the nodes and edges in local block
		 */
		public SymInstanceEdge get_root_edge() { return this.in_edge; }
		/**
		 * @return the target of the root edge
		 */
		public SymInstanceNode get_root_node() { return this.in_edge.get_target(); }
		/**
		 * @return the execution in which the status of the nodes and edges in the block will be evaluated
		 */
		public CirExecution get_execution() { return this.in_edge.get_constraint().get_execution(); }
		/**
		 * @return the collection of edges out from the local block of the symbolic instance subgraph
		 */
		public Iterable<SymInstanceEdge> get_leaf_edges() { return this.ou_edges; }
		/**
		 * @return the parent of the local block or null if it is the root
		 */
		public SymInstanceBlock get_parent() { return this.parent; }
		/**
		 * @return the set of children blocks extended from the output edges from this block
		 */
		public Iterable<SymInstanceBlock> get_children() { return this.children; }
		/**
		 * @param ou_edge
		 * @return the child w.r.t. the output edge from this parent
		 */
		public SymInstanceBlock get_child(SymInstanceEdge ou_edge) {
			for(SymInstanceBlock child : this.children) {
				if(child.in_edge == ou_edge) 
					return child;
			}
			return null;
		}
		
	}
	
	/* singleton mode */
	/** private constructor for creating evaluator for evaluating status of symbolic instance **/
	private SymInstanceEvaluator() { }
	/** the singleton of evaluator used for evaluating the status of symbolic instances in graph **/
	protected static final SymInstanceEvaluator evaluator = new SymInstanceEvaluator();
	
	/* basic evaluation method */
	/**
	 * reset the status for the nodes and edges in the graph
	 * @param graph
	 * @throws Exception
	 */
	private void reset_status(SymInstanceGraph graph) {
		for(SymInstanceNode node : graph.get_nodes()) {
			node.get_status().reset();
			for(SymInstanceEdge edge : node.get_ou_edges()) {
				edge.get_status().reset();
			}
		}
	}
	/**
	 * perform evaluation on the instance of the given status
	 * @param status
	 * @param contexts
	 * @return true if the validation result is either null or true
	 * @throws Exception
	 */
	private boolean evaluate_status(SymInstanceStatus status, CirMutations cir_mutations, CStateContexts contexts) throws Exception {
		Boolean validation = status.evaluate(cir_mutations, contexts);
		return validation == null || validation.booleanValue();
	}
	/**
	 * perform evaluation on the status of the node 
	 * @param node
	 * @param contexts
	 * @return the set of output edges from the node if it is evaluated as non-false
	 * @throws Exception
	 */
	private Iterable<SymInstanceEdge> evaluate_status(SymInstanceNode node, CStateContexts contexts) throws Exception {
		CirMutations cir_mutations = node.get_graph().get_cir_mutations();
		List<SymInstanceEdge> next_edges = new ArrayList<SymInstanceEdge>();
		if(this.evaluate_status(node.get_status(), cir_mutations, contexts)) {
			for(SymInstanceEdge next_edge : node.get_ou_edges()) {
				next_edges.add(next_edge);
			}
		}
		return next_edges;
	}
	/**
	 * perform evaluation on the status of the edge and its target
	 * @param edge
	 * @param contexts
	 * @return the set of next edges from the target of the edge if it passes to the target and evaluated as true or null
	 * @throws Exception
	 */
	private Iterable<SymInstanceEdge> evaluate_status(SymInstanceEdge edge, CStateContexts contexts) throws Exception {
		List<SymInstanceEdge> next_edges = new ArrayList<SymInstanceEdge>();
		CirMutations cir_mutations = edge.get_source().get_graph().get_cir_mutations();
		if(this.evaluate_status(edge.get_status(), cir_mutations, contexts)) {
			if(this.evaluate_status(edge.get_target().get_status(), cir_mutations, contexts)) {
				for(SymInstanceEdge next_edge : edge.get_target().get_ou_edges()) {
					next_edges.add(next_edge);
				}
			}
		}
		return next_edges;
	}
	
	/* static status evaluation for graph */
	/**
	 * Perform static evaluation on the edge and its target, and recursively solve its next edges
	 * using the same way recursively
	 * @param edge
	 * @throws Exception
	 */
	private void static_evaluate_from_edge(SymInstanceEdge edge) throws Exception {
		Iterable<SymInstanceEdge> next_edges = this.evaluate_status(edge, null);
		for(SymInstanceEdge next_edge : next_edges) {
			this.static_evaluate_from_edge(next_edge);
		}
	}
	/**
	 * Perform static evaluation on status of the nodes and edges in the graph
	 * @param graph
	 * @throws Exception
	 */
	protected void static_evaluate(SymInstanceGraph graph) throws Exception {
		this.reset_status(graph);
		Iterable<SymInstanceEdge> init_edges = this.evaluate_status(graph.get_root(), null);
		for(SymInstanceEdge init_edge : init_edges) this.static_evaluate_from_edge(init_edge);
	}
	
	/* dynamic status evaluation for graph */
	/**
	 * Perform dynamic evaluation in the status of the nodes and edges of the graph with information in state path
	 * @param graph
	 * @param state_path
	 * @throws Exception
	 */
	protected void dynamic_evaluate(SymInstanceGraph graph, CStatePath state_path) throws Exception {
		if(state_path == null)
			throw new IllegalArgumentException("Invalid state_path as null");
		else {
			this.reset_status(graph);
			this.dynamic_prev_evaluate(graph, state_path);
			this.dynamic_post_evaluate(graph, state_path);
		}
	}
	/**
	 * @param graph
	 * @return mapping from execution to the status of nodes and edges in the corresponding execution 
	 * 		   (only allow the reaching part, including exec_node, muta_node and their outputs edges)
	 * @throws Exception
	 */
	private Map<CirExecution, Collection<SymInstanceStatus>> get_reaching_status(SymInstanceGraph graph) throws Exception {
		/* declarations */
		Queue<SymInstanceNode> queue = new LinkedList<SymInstanceNode>();
		Map<CirExecution, Collection<SymInstanceStatus>> results = 
				new HashMap<CirExecution, Collection<SymInstanceStatus>>();
		
		queue.add(graph.get_root());
		while(!queue.isEmpty()) {
			SymInstanceNode node = queue.poll();
			
			/* record the status of node in reaching part */
			CirExecution node_execution = node.get_execution();
			if(!results.containsKey(node_execution)) 
				results.put(node_execution, new ArrayList<SymInstanceStatus>());
			results.get(node_execution).add(node.get_status());
			
			/* record the status of edge in reaching part */
			for(SymInstanceEdge edge : node.get_ou_edges()) {
				CirExecution edge_execution = edge.get_constraint().get_execution();
				if(!results.containsKey(edge_execution))
					results.put(edge_execution, new ArrayList<SymInstanceStatus>());
				results.get(edge_execution).add(edge.get_status());
			}
			
			/* append the next nodes from reaching node to queue */
			if(node.get_type() == SymInstanceNodeType.path_node) {
				for(SymInstanceEdge edge : node.get_ou_edges()) {
					queue.add(edge.get_target());
				}
			}
		}
		
		return results;
	}
	/**
	 * Perform the dynamic evaluation on the nodes and edges in the reaching part of the graph using the dynamic information
	 * in the state path
	 * @param graph
	 * @param state_path
	 * @throws Exception
	 */
	private void dynamic_prev_evaluate(SymInstanceGraph graph, CStatePath state_path) throws Exception {
		/* get the status of nodes and edges in the reaching part (including mutated nodes and infection edges) */
		Map<CirExecution, Collection<SymInstanceStatus>> reaching_map = this.get_reaching_status(graph);
		CStateContexts contexts = new CStateContexts();
		CirMutations cir_mutations = graph.get_cir_mutations();
		
		/* perform dynamic evaluation on the status of nodes and edges in the reaching part of the graph */
		for(CStateNode state_node : state_path.get_nodes()) {
			contexts.accumulate(state_node);
			CirExecution execution = state_node.get_execution();
			if(reaching_map.containsKey(execution)) {
				for(SymInstanceStatus status : reaching_map.get(execution)) {
					this.evaluate_status(status, cir_mutations, contexts);
				}
			}
		}
	}
	/**
	 * Perform dynamic evaluation on the status of nodes and edges in the block
	 * @param parent
	 * @param contexts
	 * @return the set of child blocks from the parent that can be reached during the propagation
	 * @throws Exception
	 */
	private Iterable<SymInstanceBlock> evaluate_local_block(SymInstanceBlock parent, CStateContexts contexts) throws Exception {
		List<SymInstanceBlock> valid_children = new ArrayList<SymInstanceBlock>();
		Queue<SymInstanceEdge> queue = new LinkedList<SymInstanceEdge>();
		
		queue.add(parent.get_root_edge());
		while(!queue.isEmpty()) {
			SymInstanceEdge edge = queue.poll();
			if(parent.ou_edges.contains(edge)) {
				SymInstanceBlock child = parent.get_child(edge);
				if(child != null) valid_children.add(child);
				else
					throw new RuntimeException("Unable to find child from " + edge);
			}
			else {
				Iterable<SymInstanceEdge> next_edges = this.evaluate_status(edge, contexts);
				for(SymInstanceEdge next_edge : next_edges) { queue.add(next_edge); } 
			}
		}
		
		return valid_children;
	}
	/**
	 * Perform dynamic evaluation based on the execution path for the post propagation tree.
	 * @param post_tree
	 * @param state_path
	 * @throws Exception
	 */
	private void dynamic_post_evaluate(SymInstanceBlock tree, CStatePath state_path) throws Exception {
		/* declarations */
		CStateContexts contexts = new CStateContexts();
		Collection<SymInstanceBlock> candidates = new HashSet<SymInstanceBlock>();
		Collection<SymInstanceBlock> remove_set = new HashSet<SymInstanceBlock>();
		
		for(CStateNode state_node : state_path.get_nodes()) {
			/* accumulate contexts */ contexts.accumulate(state_node);
			
			/* restart from the root of the propagation tree in testing */
			if(tree.get_execution() == state_node.get_execution()) {
				candidates.clear();
				candidates.add(tree);
			}
			
			/* remove the matched tree and add into remove_set */
			remove_set.clear();
			for(SymInstanceBlock candidate : candidates) {
				if(candidate.get_execution() == state_node.get_execution()) {
					remove_set.add(candidate);
				}
			}
			candidates.removeAll(remove_set);
			
			/* perform dynamic evaluation on selected candidate and update candidates more */
			for(SymInstanceBlock selected_candidate : remove_set) {
				Iterable<SymInstanceBlock> children = this.evaluate_local_block(selected_candidate, contexts);
				for(SymInstanceBlock child : children) { candidates.add(child); }
			}
		}
	}
	/**
	 * Perform dynamic evaluation on error propagation part and update its status
	 * @param graph
	 * @param state_path
	 * @throws Exception
	 */
	private void dynamic_post_evaluate(SymInstanceGraph graph, CStatePath state_path) throws Exception {
		for(SymInstanceNode mutated_node : graph.get_mutated_nodes()) {
			for(SymInstanceEdge infect_edge : mutated_node.get_ou_edges()) {
				if(infect_edge.get_status().is_executed() && infect_edge.get_status().is_acceptable()) {
					SymInstanceBlock tree = new SymInstanceBlock(null, infect_edge);
					this.dynamic_post_evaluate(tree, state_path);	/* iterations */
				}
			}
		}
	}
	
	/* feature selection algorithms */
	/**
	 * traverse the symbolic instance graph from the edge and collect the reachable path into the paths
	 * @param edge
	 * @param path
	 * @param paths
	 * @throws Exception
	 */
	private void select_reachable_paths(SymInstanceEdge edge, Stack<SymInstanceEdge> 
					path, Collection<List<SymInstanceEdge>> paths) throws Exception {
		if(edge.get_status().is_acceptable()) {
			path.push(edge);
			
			if(edge.get_target().get_ou_degree() == 0) {
				List<SymInstanceEdge> copy = new ArrayList<SymInstanceEdge>();
				for(SymInstanceEdge path_edge : path) { copy.add(path_edge); }
				paths.add(copy);
			}
			else {
				for(SymInstanceEdge next_edge : edge.get_target().get_ou_edges()) {
					this.select_reachable_paths(next_edge, path, paths);
				}
			}
			
			path.pop();
		}
		else {
			List<SymInstanceEdge> copy = new ArrayList<SymInstanceEdge>();
			for(SymInstanceEdge path_edge : path) { copy.add(path_edge); }
			copy.add(edge); paths.add(copy);
		}
	}
	/**
	 * @param graph
	 * @return the set of paths from program entry to mutated node until a state error node that can be reached from
	 * 		   the evaluation status.
	 * @throws Exception
	 */
	protected Collection<List<SymInstanceEdge>> select_reachable_paths(SymInstanceGraph graph) throws Exception {
		Collection<List<SymInstanceEdge>> paths = new HashSet<List<SymInstanceEdge>>();
		for(SymInstanceEdge init_edge : graph.get_root().get_ou_edges()) 
			this.select_reachable_paths(init_edge, new Stack<SymInstanceEdge>(), paths);
		return paths;
	}
	
}
