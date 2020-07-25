package com.jcsa.jcparse.flwa.defuse;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import com.jcsa.jcparse.flwa.graph.CirInstanceGraph;
import com.jcsa.jcparse.flwa.graph.CirInstanceNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;

/**
 * The definition-usage graph describes the define-usage and usage-define relationships between
 * the variables within the program under analysis.
 * 
 * @author yukimula
 *
 */
public class CDefineUseGraph {
	
	/* constructor */
	private Map<CirInstanceNode, Collection<CDefineUseNode>> instance_nodes;
	private CDefineUseGraph() {
		this.instance_nodes = new HashMap<
				CirInstanceNode, Collection<CDefineUseNode>>();
	}
	
	/* getters */
	/**
	 * get the number of nodes in the graph
	 * @return
	 */
	public int size() { 
		int size = 0;
		for(Collection<CDefineUseNode> instance_nodes : instance_nodes.values()) 
			size = size + instance_nodes.size();
		return  size;
	}
	/**
	 * get the set of nodes created in this graph
	 * @return
	 */
	public Iterable<CDefineUseNode> get_nodes() { 
		return new CDefineUseNodeIterable(this); 
	}
	/**
	 * whether there are nodes within the instance of statement
	 * @param instance
	 * @return
	 */
	public boolean has_nodes(CirInstanceNode instance) {
		return this.instance_nodes.containsKey(instance);
	}
	/**
	 * get the nodes with respect to the instance of the given statement
	 * @param instance
	 * @return
	 * @throws Exception
	 */
	public Iterable<CDefineUseNode> get_nodes(CirInstanceNode instance) throws Exception {
		if(instance_nodes.containsKey(instance)) return instance_nodes.get(instance);
		else throw new IllegalArgumentException("Undefined instance: " + instance);
	}
	/**
	 * whether there is a node with respect to the instance where the expression is used or defined
	 * @param instance
	 * @param expression
	 * @return 
	 */
	public boolean has_node(CirInstanceNode instance, CirExpression expression) {
		if(this.instance_nodes.containsKey(instance)) {
			for(CDefineUseNode node : instance_nodes.get(instance)) {
				if(node.get_expression() == expression) return true;
			}
			return false;
		}
		else return false;
	}
	/**
	 * get the node with respect to the instance where the expression is used or defined
	 * @param instance
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public CDefineUseNode get_node(CirInstanceNode instance, CirExpression expression) throws Exception {
		if(this.instance_nodes.containsKey(instance)) {
			for(CDefineUseNode node : instance_nodes.get(instance)) {
				if(node.get_expression() == expression) return node;
			}
			throw new IllegalArgumentException("Undefined expression");
		}
		else throw new IllegalArgumentException("Undefined instance");
	}
	
	/* setter */
	/**
	 * create a new node (define or usage) in the graph with respect to the expression
	 * in the statement being executed within the instance as provided.
	 * @param define
	 * @param instance
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	protected CDefineUseNode new_node(boolean define, CirInstanceNode 
			instance, CirExpression expression) throws Exception {
		if(expression == null)
			throw new IllegalArgumentException("Invalid expression: null");
		else if(instance == null)
			throw new IllegalArgumentException("Invalid statement: null");
		else {
			if(!this.instance_nodes.containsKey(instance)) 
				this.instance_nodes.put(instance, new LinkedList<CDefineUseNode>());
			Collection<CDefineUseNode> nodes = this.instance_nodes.get(instance);
			for(CDefineUseNode node : nodes) {
				if(node.get_expression() == expression) return node;
			}
			CDefineUseNode node = new CDefineUseNode(this, define, instance, expression);
			nodes.add(node); return node;
		}
	}
	
	/* iterator class */
	protected static class CDefineUseNodeIterator implements Iterator<CDefineUseNode> {
		
		private Iterator<Collection<CDefineUseNode>> iter1;
		private Iterator<CDefineUseNode> iter2;
		private CDefineUseNode node;
		
		protected CDefineUseNodeIterator(CDefineUseGraph graph) {
			iter1 = graph.instance_nodes.values().iterator();
			if(iter1.hasNext()) {
				iter2 = iter1.next().iterator();
			}
			else iter2 = null;
			this.update();
		}
		private void update() {
			if(iter2 == null) node = null;
			else {
				do {
					if(iter2.hasNext()) {
						node = iter2.next();
						return;
					}
					if(iter1.hasNext())
						iter2 = iter1.next().iterator();
					else iter2 = null;
				} while(iter2 != null);
				
				node = null;
			}
		}
		@Override
		public boolean hasNext() { return node != null; }
		@Override
		public CDefineUseNode next() {
			CDefineUseNode next = this.node;
			this.update(); return next;
		}
	}
	protected static class CDefineUseNodeIterable implements Iterable<CDefineUseNode> {
		private CDefineUseGraph graph;
		private CDefineUseNodeIterable(CDefineUseGraph graph) { this.graph = graph; }
		@Override
		public Iterator<CDefineUseNode> iterator() {
			return new CDefineUseNodeIterator(this.graph);
		}
	}
	
	/* factory methods */
	/**
	 * create the definition-usage graph over the entire program flow graph
	 * @param program_graph
	 * @return
	 * @throws Exception
	 */
	public static CDefineUseGraph define_use_graph(CirInstanceGraph program_graph) throws Exception {
		CDefineUseGraph output = new CDefineUseGraph();
		CDefineUseBuilder.builder.build(program_graph, output);
		return output;
	}
	
}
