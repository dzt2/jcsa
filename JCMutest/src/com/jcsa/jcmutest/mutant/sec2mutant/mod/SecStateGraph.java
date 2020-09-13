package com.jcsa.jcmutest.mutant.sec2mutant.mod;

import java.util.HashMap;
import java.util.Map;

import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecDescription;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.stmt.SecPasStatementError;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.SecInfection;
import com.jcsa.jcparse.flwa.depend.CDependGraph;

public class SecStateGraph {
	
	/* definitions */
	private CDependGraph depend_graph;
	private SecInfection infection;
	private SecStateNode reach_node;
	/** mapping from code to unique state unit **/
	private Map<String, SecStateUnit> units;
	/** mapping from unit to its state node in graph **/
	private Map<SecStateUnit, SecStateNode> nodes;
	/**
	 * create an empty state graph for error analysis
	 * @throws Exception 
	 */
	public SecStateGraph(CDependGraph graph, SecInfection infection) throws Exception {
		if(graph == null)
			throw new IllegalArgumentException("Invalid graph: null");
		else if(infection == null)
			throw new IllegalArgumentException("Invalid infection.");
		else {
			this.depend_graph = graph;
			this.infection = infection;
			this.units = new HashMap<String, SecStateUnit>();
			this.nodes = new HashMap<SecStateUnit, SecStateNode>();
			this.initialize();
		}
	}
	
	/* getters */
	/**
	 * @return the infection module of the state error graph
	 */
	public SecInfection get_infection() { return this.infection; }
	/**
	 * @return the unique instances of state units in the graph
	 */
	public Iterable<SecStateUnit> get_units() {
		return this.units.values();
	}
	/**
	 * @param desc
	 * @return the unit w.r.t. the description as given
	 * @throws Exception
	 */
	public SecStateUnit get_unit(SecDescription desc) throws Exception {
		if(desc == null)
			throw new IllegalArgumentException("Invalid description");
		else {
			String key = desc.generate_code();
			if(!units.containsKey(key)) {
				units.put(key, new SecStateUnit(this, desc));
			}
			return units.get(key);
		}
	}
	/**
	 * @return the node that describes the execution of faulty statement
	 */
	public SecStateNode get_reach_node() { return this.reach_node; }
	/**
	 * @return the set of state nodes in the graph
	 */
	public Iterable<SecStateNode> get_nodes() { return this.nodes.values(); }
	/**
	 * @param desc
	 * @return the node w.r.t. the description of unique instance in space
	 * @throws Exception
	 */
	public SecStateNode get_node(SecDescription desc) throws Exception {
		if(desc == null)
			throw new IllegalArgumentException("Invalid description");
		else {
			SecStateUnit unit = this.get_unit(desc);
			if(!this.nodes.containsKey(unit)) {
				this.nodes.put(unit, new SecStateNode(this, unit));
			}
			return this.nodes.get(unit);
		}
	}
	protected void clear() {
		for(SecStateNode node : this.nodes.values()) {
			node.delete();
		}
		this.nodes.clear(); this.units.clear();
	}
	
	/* initializer */
	private void initialize() throws Exception {
		this.clear();
		this.reach_node = SecPathBuilder.build_path(depend_graph, infection, this);
		for(int k = 0; k < this.infection.number_of_infection_pairs(); k++) {
			SecDescription[] infection_pair = this.infection.get_infection_pair(k);
			SecDescription constraint = infection_pair[0];
			SecDescription state_error = infection_pair[1];
			if(!(state_error instanceof SecPasStatementError)) {
				SecStateNode target = this.get_node(state_error);
				this.reach_node.connect(target, constraint);
			}
		}
	}
	
}
