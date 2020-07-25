package com.jcsa.jcparse.flwa.graph;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

/**
 * Using depth-first traversal algorithm to traverse all the edges in instance graph
 * based on edges such that each edge is visited only once.<br>
 * (1) The iterator accesses all the edges in graph dynamically rather than preserving
 * 	   a sequence of edges in the graph.<br>
 * (2) The iterator specifies the direction to traverse the edges in graph, either the
 * 	   forward or backward traversal anyway.<br>
 * (3) It can starts the traversal from an edge, node or the heads of graph provided.<br>
 * @author yukimula
 *
 */
class CirDFSEdgeIterator implements Iterator<CirInstanceEdge> {
	
	/* element class */
	/**
	 * The element used in depth-first traversal.
	 * 
	 * @author yukimula
	 *
	 */
	protected static class CirDFSEdgeElement {
		protected CirInstanceEdge edge;
		protected Iterator<CirInstanceEdge> edges;
		protected CirDFSEdgeElement(CirInstanceEdge edge, boolean direction) {
			this.edge = edge;
			if(direction)
				this.edges = edge.target.get_ou_edges().iterator();
			else this.edges = edge.source.get_in_edges().iterator();
		}
	}
	
	/* attributes */
	/** the direction of traversal can be forward (true) or backward (false) **/
	private boolean direction;
	/** the stack is used to simulate the depth-first traversal in graph **/
	private Stack<CirDFSEdgeElement> stack;
	/** the set of the edges that have been visited before in traversal **/
	private Set<CirInstanceEdge> visited;
	
	/* basic methods */
	/**
	 * create the buffers for depth-first traversal
	 * @param direction
	 */
	private void init_buffers(boolean direction) {
		this.direction = direction;
		this.stack = new Stack<CirDFSEdgeElement>();
		this.visited = new HashSet<CirInstanceEdge>();
	}
	/**
	 * push the node into the stack and update visited set if it has not been visited before.
	 * @param node
	 * @return
	 */
	private boolean push_stack(CirInstanceEdge edge) {
		if(this.visited.contains(edge)) return false;
		else {
			this.stack.push(new CirDFSEdgeElement(edge, direction));
			this.visited.add(edge); return true;
		}
	}
	
	/* constructors */
	/**
	 * Depth-first traversal starting from the edge specified
	 * @param edge
	 * @param direction
	 */
	protected CirDFSEdgeIterator(CirInstanceEdge edge, boolean direction) {
		this.init_buffers(direction);
		this.push_stack(edge);
	}
	
	@Override
	public boolean hasNext() { return !this.stack.isEmpty(); }
	@Override
	public CirInstanceEdge next() {
		if(this.stack.isEmpty()) return null;
		else {
			CirInstanceEdge edge = this.stack.peek().edge;
			this.update_traversal(); return edge;
		}
	}
	private void update_traversal() {
		while(!this.stack.isEmpty()) {
			// (1) get the current top in the stack
			CirDFSEdgeElement element = this.stack.peek();
			
			// (2) try to find the next edge that can be visited
			while(element.edges.hasNext()) {
				CirInstanceEdge edge = element.edges.next();
				if(this.push_stack(edge)) return;
			}
			
			// (3) if no more node non-visited from this one, pop
			this.stack.pop();
		}
	}

}
