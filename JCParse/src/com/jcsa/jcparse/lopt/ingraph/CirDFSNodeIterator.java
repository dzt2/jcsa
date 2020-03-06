package com.jcsa.jcparse.lopt.ingraph;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

/**
 * Using depth-first traversal algorithm to traverse all the nodes in instance graph
 * based on nodes such that each node is visited only once.<br>
 * (1) The iterator accesses all the nodes in graph dynamically rather than preserving
 * 	   a sequence of nodes in the graph.<br>
 * (2) The iterator specifies the direction to traverse the nodes in graph, either the
 * 	   forward or backward traversal anyway.<br>
 * (3) It can starts the traversal from an edge, node or the heads of graph provided.<br>
 * 
 * @author yukimula
 *
 */
class CirDFSNodeIterator implements Iterator<CirInstanceNode> {
	
	/* element class */
	/**
	 * The element used in depth-first traversal.
	 * 
	 * @author yukimula
	 *
	 */
	protected static class CirDFSNodeElement {
		protected CirInstanceNode node;
		protected Iterator<CirInstanceEdge> edges;
		protected CirDFSNodeElement(CirInstanceNode node, boolean direction) {
			this.node = node;
			if(direction)
				this.edges = node.get_ou_edges().iterator();
			else this.edges = node.get_in_edges().iterator();
		}
	}
	
	/* attributes */
	/** the direction of traversal can be forward (true) or backward (false) **/
	private boolean direction;
	/** the stack is used to simulate the depth-first traversal in graph **/
	private Stack<CirDFSNodeElement> stack;
	/** the set of the nodes that have been visited before in traversal **/
	private Set<CirInstanceNode> visited;
	
	/* constructor */
	/**
	 * Depth-first traversal starting from the node specified
	 * @param node
	 * @param direction
	 */
	protected CirDFSNodeIterator(CirInstanceNode node, boolean direction) {
		this.init_buffers(direction);
		this.push_stack(node);
	}
	/**
	 * Depth-first traversal starting from the edge specified
	 * @param edge
	 * @param direction
	 */
	protected CirDFSNodeIterator(CirInstanceEdge edge, boolean direction) {
		this.init_buffers(direction);
		if(direction)
			this.push_stack(edge.target);
		else this.push_stack(edge.source);
	}
	
	/* basic methods */
	/**
	 * create the buffers for depth-first traversal
	 * @param direction
	 */
	private void init_buffers(boolean direction) {
		this.direction = direction;
		this.stack = new Stack<CirDFSNodeElement>();
		this.visited = new HashSet<CirInstanceNode>();
	}
	/**
	 * push the node into the stack and update visited set if it has not been visited before.
	 * @param node
	 * @return
	 */
	private boolean push_stack(CirInstanceNode node) {
		if(this.visited.contains(node)) return false;
		else {
			this.stack.push(new CirDFSNodeElement(node, direction));
			this.visited.add(node); return true;
		}
	}
	
	/* implementation methods */
	@Override
 	public boolean hasNext() { return !this.stack.isEmpty(); }
	@Override
	public CirInstanceNode next() {
		if(this.stack.isEmpty()) return null;
		else {
			CirInstanceNode node = this.stack.peek().node;
			if(this.direction)
				this.forward_traversal();
			else this.backward_traversal();
			return node;
		}
	}
	private void forward_traversal() {
		while(!this.stack.isEmpty()) {
			// (1) get the current top in the stack
			CirDFSNodeElement element = this.stack.peek();
			
			// (2) try to find the next node that can be visited
			while(element.edges.hasNext()) {
				CirInstanceEdge edge = element.edges.next();
				if(this.push_stack(edge.target)) return;
			}
			
			// (3) if no more node non-visited from this one, pop
			this.stack.pop();
		}
	}
	private void backward_traversal() {
		while(!this.stack.isEmpty()) {
			// (1) get the current top in the stack
			CirDFSNodeElement element = this.stack.peek();
			
			// (2) try to find the next node that can be visited
			while(element.edges.hasNext()) {
				CirInstanceEdge edge = element.edges.next();
				if(this.push_stack(edge.source)) return;
			}
			
			// (3) if no more node non-visited from this one, pop
			this.stack.pop();
		}
	}
	
}
