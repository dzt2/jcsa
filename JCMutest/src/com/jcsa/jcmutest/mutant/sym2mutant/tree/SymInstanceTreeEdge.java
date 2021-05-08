package com.jcsa.jcmutest.mutant.sym2mutant.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.jcsa.jcmutest.mutant.sym2mutant.base.SymInstance;

/**
 * The edge linking the tree node in symbolic instance tree for killing a mutant.
 * 
 * @author yukimula
 *
 */
public class SymInstanceTreeEdge extends SymInstanceContent {
	
	/* definitions */
	private SymInstanceTreeNode source, target;
	protected SymInstanceTreeEdge(SymInstanceTreeNode source, SymInstanceTreeNode target, SymInstance edge_instance) throws Exception {
		super(edge_instance);
		if(source == null)
			throw new IllegalArgumentException("Invalid source: null");
		else if(target == null)
			throw new IllegalArgumentException("Invalid target: null");
		else { this.source = source; this.target = target; }
	}
	
	/* getters */
	/**
	 * @return the parent of the edge
	 */
	public SymInstanceTreeNode get_source() { return this.source; }
	/**
	 * @return the child of this edge
	 */
	public SymInstanceTreeNode get_target() { return this.target; }
	
	/* inference */
	/**
	 * @return whether the edge referring to an infection edge (constraint-error pair)
	 */
	public boolean is_infection_edge() {
		return this.source.is_constraint() && this.target.is_state_error();
	}
	@Override
	public List<SymInstanceTreeEdge> get_prev_path() {
		List<SymInstanceTreeEdge> path = new ArrayList<SymInstanceTreeEdge>();
		SymInstanceTreeEdge edge = this;
		while(edge != null) {
			path.add(edge);
			edge = edge.source.get_in_edge();
		}
		for(int k = 0; k < path.size() / 2; k++) {
			int i = k, j = path.size() - 1 - k;
			SymInstanceTreeEdge ei = path.get(i);
			SymInstanceTreeEdge ej = path.get(j);
			path.set(i, ej); path.set(j, ei);
		}
		return path;
	}
	private void get_post_paths(List<SymInstanceTreeEdge> prev_path, Collection<List<SymInstanceTreeEdge>> post_paths) {
		prev_path.add(this);
		if(this.target.is_leaf()) {
			List<SymInstanceTreeEdge> copy_path = new ArrayList<SymInstanceTreeEdge>();
			copy_path.addAll(prev_path); 
			post_paths.add(copy_path);
		}
		else {
			for(SymInstanceTreeEdge next_edge : this.target.get_ou_edges()) {
				next_edge.get_post_paths(prev_path, post_paths);
			}
		}
		prev_path.remove(prev_path.size() - 1);
	}
	@Override
	public Collection<List<SymInstanceTreeEdge>> get_post_paths() {
		Collection<List<SymInstanceTreeEdge>> post_paths = new ArrayList<List<SymInstanceTreeEdge>>();
		this.get_post_paths(new ArrayList<SymInstanceTreeEdge>(), post_paths);
		return post_paths;
	}
	
}
