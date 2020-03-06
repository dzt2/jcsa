package com.jcsa.jcparse.lopt.ingraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlowType;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionType;
import com.jcsa.jcparse.lopt.CirInstance;

public class CirInstanceGraph {
	
	private static final List<CirInstance> empty_instances = new ArrayList<CirInstance>();
	private static final List<Object> empty_instance_sources = new ArrayList<Object>();
	
	private CirTree cir_tree;
	private Map<Object, Map<Object, CirInstance>> context_instances;
	private Set<CirInstanceNode> heads;
	private Set<CirInstanceNode> tails;
	protected CirInstanceGraph(CirTree cir_tree) throws Exception {
		if(cir_tree == null)
			throw new IllegalArgumentException("invalid cir-tree: null");
		else {
			this.cir_tree = cir_tree;
			this.context_instances = new HashMap<
					Object, Map<Object, CirInstance>>();
			this.heads = new HashSet<CirInstanceNode>();
			this.tails = new HashSet<CirInstanceNode>();
		}
	}
	
	/* getters */
	/**
	 * get the tree of C-like intermediate representation program
	 * @return
	 */
	public CirTree get_cir_tree() { return this.cir_tree; }
	/**
	 * get the number of instances created (not include the virtual one)
	 * @return
	 */
	public int size() {
		int size = 0;
		for(Map<Object, CirInstance> instances : context_instances.values()) {
			size = size + instances.size();
		}
		return size;
	}
	/**
	 * get the set of contexts referring to the instance in the graph
	 * @return
	 */
	public Iterable<Object> get_contexts() { return this.context_instances.keySet(); }
	/**
	 * whether there are instances referring in the given context
	 * @param context
	 * @return
	 */
	public boolean has_instances(Object context) {
		return this.context_instances.containsKey(context);
	}
	/**
	 * get the set of instances (nodes and edges) in the context
	 * @param context
	 * @return
	 */
	public Iterable<CirInstance> get_instances(Object context) {
		if(this.context_instances.containsKey(context))
			return context_instances.get(context).values();
		else return empty_instances;
	}
	/**
	 * get the set of statements and flows to which the instances in the context refer
	 * @param context
	 * @return
	 */
	public Iterable<Object> get_keys_of_instances(Object context) {
		if(this.context_instances.containsKey(context))
			return context_instances.get(context).keySet();
		else return empty_instance_sources;
	}
	/**
	 * whether there is an instance (of flow or statement) in the context
	 * @param context
	 * @param element
	 * @return
	 */
	public boolean has_instance(Object context, Object element) {
		if(this.context_instances.containsKey(context)) {
			Map<Object, CirInstance> instances = this.context_instances.get(context);
			return instances.containsKey(element);
		}
		else return false;
	}
	/**
	 * get the node of statement instance in the graph
	 * @param context
	 * @param statement
	 * @return
	 * @throws Exception
	 */
	public CirInstanceNode get_instance(Object context, CirExecution statement) throws Exception {
		if(this.context_instances.containsKey(context)) {
			Map<Object, CirInstance> instances = this.context_instances.get(context);
			if(instances.containsKey(statement)) {
				return (CirInstanceNode) instances.get(statement);
			}
			else throw new IllegalArgumentException("Invalid element: " + statement);
		}
		else throw new IllegalArgumentException("Invalid context: " + context);
	}
	/**
	 * get the edge of the flow instance in the graph
	 * @param context
	 * @param flow
	 * @return
	 * @throws Exception
	 */
	public CirInstanceEdge get_instance(Object context, CirExecutionFlow flow) throws Exception {
		if(this.context_instances.containsKey(context)) {
			Map<Object, CirInstance> instances = this.context_instances.get(context);
			if(instances.containsKey(flow)) {
				return (CirInstanceEdge) instances.get(flow);
			}
			else throw new IllegalArgumentException("Invalid element: " + flow);
		}
		else throw new IllegalArgumentException("Invalid context: " + context);
	}
	/**
	 * get the set of instance nodes in this graph such that all the other nodes and edges can be 
	 * reached from these nodes in the graph.
	 * @return
	 */
	public Iterable<CirInstanceNode> get_heads() { return this.heads; }
	/**
	 * get the set of instance nodes in this graph such that all the nodes and edges can reach all
	 * of these nodes in this graph.
	 * @return
	 */
	public Iterable<CirInstanceNode> get_tails() { return this.tails; }
	/**
	 * whether there are some instances of the specified statement in graph
	 * @param statement
	 * @return
	 */
	public boolean has_instances_of(CirExecution statement) {
		for(Map<Object, CirInstance> instances : this.context_instances.values()) {
			if(instances.containsKey(statement))
				return true;
		}
		return false;
	}
	/**
	 * whether there are some instances of the specified flow in the program.
	 * @param flow
	 * @return
	 */
	public boolean has_instances_of(CirExecutionFlow flow) {
		for(Map<Object, CirInstance> instances : this.context_instances.values()) {
			if(instances.containsKey(flow))
				return true;
		}
		return false;
	}
	/**
	 * get the instances of the statement in the program flow graph
	 * @param statement
	 * @return
	 * @throws Exception
	 */
	public Iterable<CirInstanceNode> get_instances_of(CirExecution statement) {
		List<CirInstanceNode> nodes = new ArrayList<CirInstanceNode>();
		if(statement != null) {
			for(Map<Object, CirInstance> instances : this.context_instances.values()) {
				if(instances.containsKey(statement)) {
					nodes.add((CirInstanceNode) instances.get(statement));
				}
			}
		}
		return nodes;
	}
	/**
	 * get the instances of the execution flow within the program.
	 * @param flow
	 * @return
	 */
	public Iterable<CirInstanceEdge> get_instances_of(CirExecutionFlow flow) {
		List<CirInstanceEdge> nodes = new ArrayList<CirInstanceEdge>();
		if(flow != null) {
			for(Map<Object, CirInstance> instances : this.context_instances.values()) {
				if(instances.containsKey(flow)) {
					nodes.add((CirInstanceEdge) instances.get(flow));
				}
			}
		}
		return nodes;
	}
	
	/* setters */
	/**
	 * create a node representing the statement being executed in specified context
	 * @param context
	 * @param statement
	 * @return
	 * @throws Exception
	 */
	protected CirInstanceNode new_node(Object context, CirExecution statement) throws Exception {
		if(context == null)
			throw new IllegalArgumentException("invalid context: null");
		else if(statement == null)
			throw new IllegalArgumentException("invalid element: null");
		else {
			if(!this.context_instances.containsKey(context)) 
				this.context_instances.put(context, new HashMap<Object, CirInstance>());
			Map<Object, CirInstance> instances = this.context_instances.get(context);
			if(!instances.containsKey(statement)) 
				instances.put(statement, new CirInstanceNode(this, context, statement));
			else throw new RuntimeException("Duplicated: " + context + ":" + statement);
			return (CirInstanceNode) instances.get(statement);
		}
	}
	/**
	 * create a virtual node representing an imagined statement being executed
	 * which will not be recorded in the graph
	 * @param context
	 * @param type
	 * @return
	 * @throws Exception
	 */
	protected CirInstanceNode new_node(Object context, CirExecutionType type) throws Exception {
		return new CirInstanceNode(this, context, type);
	}
	/**
	 * create an edge from source to the target with respect to the context and flow as given.
	 * @param source
	 * @param target
	 * @param context
	 * @param flow
	 * @return
	 * @throws Exception
	 */
	protected CirInstanceEdge new_edge(CirInstanceNode source, CirInstanceNode target,
			Object context, CirExecutionFlow flow) throws Exception {
		if(source == null || source.get_graph() != this)
			throw new IllegalArgumentException("invalid source: " + source);
		else if(target == null || target.get_graph() != this)
			throw new IllegalArgumentException("invalid target: " + target);
		else if(context == null)
			throw new IllegalArgumentException("invalid context: null");
		else if(flow == null)
			throw new IllegalArgumentException("invalid flow as null");
		else {
			if(!this.context_instances.containsKey(context))
				this.context_instances.put(context, new HashMap<Object, CirInstance>());
			Map<Object, CirInstance> instances = this.context_instances.get(context);
			if(!instances.containsKey(flow)) 
				instances.put(flow, source.link_to(target, context, flow));
			else throw new RuntimeException("Duplicated: " + context + ":" + flow);
			return (CirInstanceEdge) instances.get(flow);
		}
	}
	/**
	 * create a virtual edge from source to the target with respect to the context and the type
	 * which will not be referred with the graph.
	 * @param source
	 * @param target
	 * @param context
	 * @param flow_type
	 * @return
	 * @throws Exception
	 */
	protected CirInstanceEdge new_edge(CirInstanceNode source, CirInstanceNode target,
			Object context, CirExecutionFlowType flow_type) throws Exception {
		if(source == null || source.get_graph() != this)
			throw new IllegalArgumentException("invalid source: " + source);
		else if(target == null || target.get_graph() != this)
			throw new IllegalArgumentException("invalid target: " + target);
		else if(context == null)
			throw new IllegalArgumentException("invalid context: null");
		else if(flow_type == null)
			throw new IllegalArgumentException("invalid flow as null");
		else { return source.link_to(target, context, flow_type); }
	}
	/**
	 * update the set of heads and tails in the graph such that all the others can reach or be reached
	 * from the heads or the tails.
	 * @throws Exception
	 */
	protected void update_heads_and_tails() throws Exception {
		this.update_heads();	this.update_tails();
	}
	/**
	 * Update the set of heads such that all the nodes in the graph can be reached from those in heads;
	 * meanwhile, removing any node from the heads, makes the above assertion not satisfied.
	 * @throws Exception
	 */
	private void update_heads() throws Exception {
		/* 1. declarations */
		Set<CirInstanceNode> visited = new HashSet<CirInstanceNode>();
		Queue<CirInstanceNode> queue = new LinkedList<CirInstanceNode>();
		
		/* 2. initialize the heads set by adding all the nodes */
		this.heads.clear();
		for(Map<Object, CirInstance> instances : this.context_instances.values()) {
			for(CirInstance instance : instances.values()) {
				if(instance instanceof CirInstanceNode)
					this.heads.add((CirInstanceNode) instance);
			}
		}
		
		/* 3. simplify the nodes in heads until minimization */
		while(true) {
			/* (1) Find the next non-visited node in heads */
			CirInstanceNode head = null;
			for(CirInstanceNode node : this.heads) {
				if(!visited.contains(node)) {
					head = node; break;
				}
			}
			if(head == null) { break; }	// no more needed
			
			/* (2) Traverse all the nodes (not-visited) from head 
			 * 	   and remove those nodes reached from the head */
			queue.clear(); queue.add(head); visited.add(head);
			
			/* (3) Brand-first traversal algorithm */
			while(!queue.isEmpty()) {
				// a. get the next node in BFS traversal
				CirInstanceNode node = queue.poll();
				
				for(CirInstanceEdge edge : node.get_ou_edges()) {
					// b. for non-visited node, add into queue
					if(!visited.contains(edge.target)) {
						visited.add(edge.target);
						queue.add(edge.target);
					}
					
					// c. whatever, remove the linked node from
					this.heads.remove(edge.target);
				}
			}	/* end of BFS traversal */
			
		}	/* end of all */
	}
	/**
	 * Update the set of tails such that all the nodes in the graph can reach the nodes within the tails;
	 * meanwhile, removing any one node from the tails can break the above assertion.
	 * @throws Exception
	 */
	private void update_tails() throws Exception {
		/* 1. declarations */
		Set<CirInstanceNode> visited = new HashSet<CirInstanceNode>();
		Queue<CirInstanceNode> queue = new LinkedList<CirInstanceNode>();
		
		/* 2. initialize the tails set by adding all the nodes */
		this.tails.clear();
		for(Map<Object, CirInstance> instances : this.context_instances.values()) {
			for(CirInstance instance : instances.values()) {
				if(instance instanceof CirInstanceNode)
					this.tails.add((CirInstanceNode) instance);
			}
		}
		
		/* 3. simplify the nodes in heads until minimization */
		while(true) {
			/* (1) Find the next non-visited node in tails */
			CirInstanceNode tail = null;
			for(CirInstanceNode node : this.tails) {
				if(!visited.contains(node)) {
					tail = node; break;
				}
			}
			if(tail == null) { break; }	// no more needed
			
			/* (2) Traverse all the nodes (not-visited) from tail 
			 * 	   and remove those nodes reached from the tail */
			queue.clear(); queue.add(tail); visited.add(tail);
			
			/* (3) Brand-first traversal algorithm */
			while(!queue.isEmpty()) {
				// a. get the next node in BFS traversal
				CirInstanceNode node = queue.poll();
				
				for(CirInstanceEdge edge : node.get_in_edges()) {
					// b. for non-visited node, add into queue
					if(!visited.contains(edge.source)) {
						visited.add(edge.source);
						queue.add(edge.source);
					}
					
					// c. whatever, remove the linked node from
					this.tails.remove(edge.source);
				}
			}	/* end of BFS traversal */
			
		}	/* end of all */
	}
	
	/* iterator */
	/**
	 * Forward traversal starting from the node
	 * @param node
	 * @return
	 * @throws Exception
	 */
	public Iterator<CirInstanceNode> forward_node_traversal(CirInstanceNode node) throws Exception {
		if(node == null || node.get_graph() != this)
			throw new IllegalArgumentException("Invalid node: null");
		else return new CirBFSNodeIterator(node, true);
	}
	/**
	 * Backward traversal starting from the node
	 * @param node
	 * @return
	 * @throws Exception
	 */
	public Iterator<CirInstanceNode> backward_node_traversal(CirInstanceNode node) throws Exception {
		if(node == null || node.get_graph() != this)
			throw new IllegalArgumentException("Invalid node: null");
		else return new CirBFSNodeIterator(node, false);
	}
	/**
	 * Forward traversal starting from the edge
	 * @param edge
	 * @return
	 * @throws Exception
	 */
	public Iterator<CirInstanceNode> forward_node_traversal(CirInstanceEdge edge) throws Exception {
		if(edge == null || edge.get_graph() != this)
			throw new IllegalArgumentException("Invalid edge: null");
		else return new CirBFSNodeIterator(edge, true);
	}
	/**
	 * Backward traversal starting from the edge
	 * @param edge
	 * @return
	 * @throws Exception
	 */
	public Iterator<CirInstanceNode> backward_node_traversal(CirInstanceEdge edge) throws Exception {
		if(edge == null || edge.get_graph() != this)
			throw new IllegalArgumentException("Invalid edge: null");
		else return new CirBFSNodeIterator(edge, false);
	}
	/**
	 * Forward traversal starting from the heads of this graph
	 * @return
	 */
	public Iterator<CirInstanceNode> forward_node_traversal() {
		return new CirBFSNodeIterator(this, true);
	}
	/**
	 * Backward traversal starting from the tails of this graph
	 * @return
	 */
	public Iterator<CirInstanceNode> backward_node_traversal() {
		return new CirBFSNodeIterator(this, false);
	}
	/**
	 * Forward traversal starting from the node
	 * @param node
	 * @return
	 * @throws Exception
	 */
	public Iterator<CirInstanceEdge> forward_edge_traversal(CirInstanceNode node) throws Exception {
		if(node == null || node.get_graph() != this)
			throw new IllegalArgumentException("Invalid node: null");
		else return new CirBFSEdgeIterator(node, true);
	}
	/**
	 * Backward traversal starting from the node
	 * @param node
	 * @return
	 * @throws Exception
	 */
	public Iterator<CirInstanceEdge> backward_edge_traversal(CirInstanceNode node) throws Exception {
		if(node == null || node.get_graph() != this)
			throw new IllegalArgumentException("Invalid node: null");
		else return new CirBFSEdgeIterator(node, false);
	}
	/**
	 * Forward traversal starting from the edge
	 * @param edge
	 * @return
	 * @throws Exception
	 */
	public Iterator<CirInstanceEdge> forward_edge_traversal(CirInstanceEdge edge) throws Exception {
		if(edge == null || edge.get_graph() != this)
			throw new IllegalArgumentException("Invalid edge: null");
		else return new CirBFSEdgeIterator(edge, true);
	}
	/**
	 * Backward traversal starting from the edge
	 * @param edge
	 * @return
	 * @throws Exception
	 */
	public Iterator<CirInstanceEdge> backward_edge_traversal(CirInstanceEdge edge) throws Exception {
		if(edge == null || edge.get_graph() != this)
			throw new IllegalArgumentException("Invalid edge: null");
		else return new CirBFSEdgeIterator(edge, false);
	}
	/**
	 * Forward traversal starting from the heads of this graph
	 * @return
	 */
	public Iterator<CirInstanceEdge> forward_edge_traversal() {
		return new CirBFSEdgeIterator(this, true);
	}
	/**
	 * Backward traversal starting from the tails of this graph
	 * @return
	 */
	public Iterator<CirInstanceEdge> backward_edge_traversal() {
		return new CirBFSEdgeIterator(this, false);
	}
	/**
	 * Forward traversal starting from the node with depth-first traversal
	 * @param node
	 * @return
	 * @throws Exception
	 */
	public Iterator<CirInstanceNode> forward_node_traversal_depth(CirInstanceNode node) throws Exception {
		if(node == null || node.get_graph() != this)
			throw new IllegalArgumentException("Invalid node: null");
		else return new CirDFSNodeIterator(node, true);
	}
	/**
	 * Backward traversal starting from the node with depth-first traversal
	 * @param node
	 * @return
	 * @throws Exception
	 */
	public Iterator<CirInstanceNode> backward_node_traversal_depth(CirInstanceNode node) throws Exception {
		if(node == null || node.get_graph() != this)
			throw new IllegalArgumentException("Invalid node: null");
		else return new CirDFSNodeIterator(node, false);
	}
	/**
	 * Forward traversal starting from the edge with depth-first traversal
	 * @param edge
	 * @return
	 * @throws Exception
	 */
	public Iterator<CirInstanceNode> forward_node_traversal_depth(CirInstanceEdge edge) throws Exception {
		if(edge == null || edge.get_graph() != this)
			throw new IllegalArgumentException("Invalid edge: null");
		else return new CirDFSNodeIterator(edge, true);
	}
	/**
	 * Backward traversal starting from the edge with depth-first traversal
	 * @param edge
	 * @return
	 * @throws Exception
	 */
	public Iterator<CirInstanceNode> backward_node_traversal_depth(CirInstanceEdge edge) throws Exception {
		if(edge == null || edge.get_graph() != this)
			throw new IllegalArgumentException("Invalid edge: null");
		else return new CirDFSNodeIterator(edge, false);
	}
	/**
	 * Forward traversal starting from the edge with depth-first traversal
	 * @param edge
	 * @return
	 * @throws Exception
	 */
	public Iterator<CirInstanceEdge> forward_edge_traversal_depth(CirInstanceEdge edge) throws Exception {
		if(edge == null || edge.get_graph() != this)
			throw new IllegalArgumentException("Invalid edge: null");
		else return new CirDFSEdgeIterator(edge, true);
	}
	/**
	 * Backward traversal starting from the edge with depth-first traversal
	 * @param edge
	 * @return
	 * @throws Exception
	 */
	public Iterator<CirInstanceEdge> backward_edge_traversal_depth(CirInstanceEdge edge) throws Exception {
		if(edge == null || edge.get_graph() != this)
			throw new IllegalArgumentException("Invalid edge: null");
		else return new CirDFSEdgeIterator(edge, false);
	}
	
}
