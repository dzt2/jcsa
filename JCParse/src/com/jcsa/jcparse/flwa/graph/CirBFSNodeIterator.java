package com.jcsa.jcparse.flwa.graph;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * Using brand-first traversal algorithm to traverse all the nodes in instance graph 
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
class CirBFSNodeIterator implements Iterator<CirInstanceNode> {
	
	/* attributes */
	/** the direction of traversal, true --> forward, false --> backward **/
	private boolean direction;
	/** the queue to preserve the sequence of nodes being traversed **/
	private Queue<CirInstanceNode> queue;
	/** the set of nodes that have been visited before in traversal **/
	private Set<CirInstanceNode> visited;
	
	/* constructors */
	/**
	 * create a brand-first traversal iterator starting from the edge.
	 * @param edge
	 * @param direction
	 */
	protected CirBFSNodeIterator(CirInstanceEdge edge, boolean direction) {
		this.init_buffers(direction); 
		if(direction) 
			this.add_queue(edge.target);
		else this.add_queue(edge.source);
	}
	/**
	 * create a brand-first traversal iterator starting from the node.
	 * @param edge
	 * @param direction
	 */
	protected CirBFSNodeIterator(CirInstanceNode node, boolean direction) {
		this.init_buffers(direction);
		this.add_queue(node);
	}
	/**
	 * create a brand-first traversal iterator starting from the edge.
	 * @param edge
	 * @param direction
	 */
	protected CirBFSNodeIterator(CirInstanceGraph graph, boolean direction) {
		this.init_buffers(direction);
		if(direction) {
			for(CirInstanceNode head : graph.get_heads())
				this.add_queue(head);
		}
		else {
			for(CirInstanceNode tail : graph.get_tails())
				this.add_queue(tail);
		}
	}
	
	/* basic methods */
	/**
	 * create the buffers used for brand-first traversal
	 */
	private void init_buffers(boolean direction) {
		this.direction = direction;
		this.queue = new LinkedList<CirInstanceNode>();
		this.visited = new HashSet<CirInstanceNode>();
	}
	/**
	 * add the node to the queue if it is not visited
	 * @param node
	 * @return false if the node is not added in queue
	 */
	private boolean add_queue(CirInstanceNode node) {
		if(this.visited.contains(node)) return false;
		else {
			this.visited.add(node); this.queue.add(node);
			return true;
		}
	}
	
	/* implementation methods */
	@Override
	public boolean hasNext() { return !this.queue.isEmpty(); }
	@Override
	public CirInstanceNode next() {
		if(this.queue.isEmpty()) return null;
		else {
			CirInstanceNode node = this.queue.poll();
			if(this.direction)
				this.forward_traversal(node);
			else this.bacward_traversal(node);
			return node;
		}
	}
	/**
	 * put the nodes that are directly reached from source into queue
	 * @param source
	 */
	private void forward_traversal(CirInstanceNode source) {
		for(CirInstanceEdge edge : source.get_ou_edges()) {
			this.add_queue(edge.target);
		}
	}
	/**
	 * put the nodes that directly reaches the target into this queue
	 * @param target
	 */
	private void bacward_traversal(CirInstanceNode target) {
		for(CirInstanceEdge edge : target.get_in_edges()) {
			this.add_queue(edge.source);
		}
	}
	
}
