package com.jcsa.jcmutest.mutant.sec2mutant.mod;

import java.util.HashMap;
import java.util.Map;

import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecDescription;
import com.jcsa.jcparse.lang.irlang.CirTree;

public class SecStateGraph {
	
	/* definitions */
	/** C code on which the graph is defined **/
	private CirTree cir_tree;
	/** set of unique state units in graph **/
	private Map<String, SecStateUnit> units;
	/** set of unique state nodes in graph **/
	private Map<SecStateUnit, SecStateNode> nodes;
	/**
	 * create an empty state graph on the specified C program
	 * @param cir_tree
	 * @throws Exception
	 */
	public SecStateGraph(CirTree cir_tree) throws Exception {
		if(cir_tree == null)
			throw new IllegalArgumentException("Invalid cir_tree: null");
		else {
			this.cir_tree = cir_tree;
			this.units = new HashMap<String, SecStateUnit>();
			this.nodes = new HashMap<SecStateUnit, SecStateNode>();
		}
	}
	
	/* getters */
	/**
	 * @return the C-intermediate code
	 */
	public CirTree get_cir_tree() { return this.cir_tree; }
	/**
	 * @param key
	 * @return the unit w.r.t. the description as given
	 * @throws Exception
	 */
	public SecStateUnit get_unit(SecDescription key) throws Exception {
		if(key == null)
			throw new IllegalArgumentException("Invalid key: null");
		else {
			String skey = key.generate_code();
			if(!this.units.containsKey(skey)) {
				this.units.put(skey, new SecStateUnit(key));
			}
			return this.units.get(skey);
		}
	}
	/**
	 * @return the set of state units with descriptions created in the space.
	 */
	public Iterable<SecStateUnit> get_units() {
		return this.units.values();
	}
	/**
	 * @return the number of nodes in the graph
	 */
	public int size() { return this.nodes.size(); }
	/**
	 * @return the set of nodes created in this graph
	 */
	public Iterable<SecStateNode> get_nodes() { return this.nodes.values(); }
	/**
	 * @param unit
	 * @return whether there is a node w.r.t. the unit
	 */
	public boolean has_node(SecStateUnit unit) {
		return this.nodes.containsKey(unit);
	}
	/**
	 * @param unit
	 * @return the node w.r.t. the unit in the graph
	 * @throws Exception
	 */
	public SecStateNode get_node(SecStateUnit unit) throws Exception {
		if(unit == null)
			throw new IllegalArgumentException("Invalid unit: null");
		else {
			if(!this.nodes.containsKey(unit)) 
				this.nodes.put(unit, new SecStateNode(this, unit));
			return this.nodes.get(unit);
		}
	}
	/**
	 * clear all the nodes and units within the graph.
	 */
	public void clear() {
		for(SecStateNode node : this.nodes.values()) {
			node.delete();
		}
		this.nodes.clear();
		this.units.clear();
	}
	/**
	 * @param key
	 * @return the node w.r.t. the unit w.r.t. the description
	 * @throws Exception
	 */
	public SecStateNode get_node(SecDescription key) throws Exception {
		return this.get_node(this.get_unit(key));
	}
	/**
	 * connect the source to the target with specified constraint
	 * @param type
	 * @param source
	 * @param target
	 * @param constraint
	 * @return
	 * @throws Exception
	 */
	public SecStateEdge connect(SecStateEdgeType type, SecStateNode source, 
			SecStateNode target, SecDescription constraint) throws Exception {
		SecStateUnit unit = this.get_unit(constraint);
		SecStateEdge edge = new SecStateEdge(type, source, target, unit);
		source.ou.add(edge);
		target.in.add(edge);
		return edge;
	}
	
}
