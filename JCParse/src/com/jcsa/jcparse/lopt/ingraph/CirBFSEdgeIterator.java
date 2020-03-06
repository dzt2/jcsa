package com.jcsa.jcparse.lopt.ingraph;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * Using brand-first traversal algorithm to traverse the edges in the graph , in which 
 * each edge can be visited once.<br>
 * (1) The iterator accesses all the edges in graph dynamically rather than preserving
 * 	   a sequence of edges in the graph.<br>
 * (2) The iterator specifies the direction to traverse the edges in graph, either the
 * 	   forward or backward traversal anyway.<br>
 * (3) It can starts the traversal from an edge, node or the heads of graph provided.<br>
 * @author yukimula
 *
 */
class CirBFSEdgeIterator implements Iterator<CirInstanceEdge> {
	
	/* attributes */
	/** the direction in which the edges are traversed **/
	private boolean direction;
	/** the queue preserves the order of edges in traversal **/
	private Queue<CirInstanceEdge> queue;
	/** the set of edges that have been visited before **/
	private Set<CirInstanceEdge> visited;
	
	/* constructor */
	/**
	 * create a brand-first traversal for edges starting from specified edge
	 * @param edge
	 * @param direction
	 */
	protected CirBFSEdgeIterator(CirInstanceEdge edge, boolean direction) {
		this.init_buffers(direction);
		this.add_queue(edge);
	}
	/**
	 * create a brand-first traversal for edges starting from specified node
	 * @param node
	 * @param direction
	 */
	protected CirBFSEdgeIterator(CirInstanceNode node, boolean direction) {
		this.init_buffers(direction);
		if(direction) {
			for(CirInstanceEdge edge : node.get_ou_edges()) {
				this.add_queue(edge);
			}
		}
		else {
			for(CirInstanceEdge edge : node.get_in_edges()) {
				this.add_queue(edge);
			}
		}
	}
	/**
	 * create a brand-first traversal for edges starting from specified graph
	 * @param edge
	 * @param direction
	 */
	protected CirBFSEdgeIterator(CirInstanceGraph graph, boolean direction) {
		this.init_buffers(direction);
		if(direction) {
			for(CirInstanceNode head : graph.get_heads()) {
				for(CirInstanceEdge edge : head.get_ou_edges()) {
					this.add_queue(edge);
				}
			}
		}
		else {
			for(CirInstanceNode tail : graph.get_tails()) {
				for(CirInstanceEdge edge : tail.get_in_edges()) {
					this.add_queue(edge);
				}
			}
		}
	}
	
	/* basic methods */
	/**
	 * initialize the buffers for brand-first traversal
	 */
	private void init_buffers(boolean direction) {
		this.direction = direction;
		this.queue = new LinkedList<CirInstanceEdge>();
		this.visited = new HashSet<CirInstanceEdge>();
	}
	/**
	 * add the edge into the queue or not if it has been visited
	 * @param edge
	 * @return false if the edge is not added into the queue
	 */
	private boolean add_queue(CirInstanceEdge edge) {
		if(!this.visited.contains(edge)) {
			this.visited.add(edge);
			this.queue.add(edge);
			return true;
		}
		else return false;
	}
	
	/* implementation methods */
	@Override
	public boolean hasNext() { return !this.queue.isEmpty(); }
	@Override
	public CirInstanceEdge next() {
		if(this.queue.isEmpty()) return null;
		else {
			CirInstanceEdge edge = this.queue.poll();
			if(this.direction)
				this.forward_traversal(edge);
			else this.bacward_traversal(edge);
			return edge;
		}
	}
	/**
	 * update the queue by pushing its target edges in forward traversal
	 * @param source_edge
	 */
	private void forward_traversal(CirInstanceEdge source_edge) {
		for(CirInstanceEdge target_edge : source_edge.target.get_ou_edges()) {
			this.add_queue(target_edge);
		}
	}
	/**
	 * update the queue by pushing its source edges in backward traversal
	 * @param target_edge
	 */
	private void bacward_traversal(CirInstanceEdge target_edge) {
		for(CirInstanceEdge source_edge : target_edge.source.get_in_edges()) {
			this.add_queue(source_edge);
		}
	}

}
