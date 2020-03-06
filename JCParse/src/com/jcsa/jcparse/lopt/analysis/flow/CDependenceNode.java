package com.jcsa.jcparse.lopt.analysis.flow;

import java.util.LinkedList;
import java.util.List;

import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lopt.ingraph.CirInstanceNode;

/**
 * Each node in program dependence graph refers to an instance of statement execution in program.
 * 
 * @author yukimula
 *
 */
public class CDependenceNode {
	
	/* attributes */
	/** the dependence graph where this node is created **/
	private CDependenceGraph graph;
	/** the instance of statement being executed the node represents **/
	private CirInstanceNode instance;
	/** the set of edges that the others directly depend on this node **/
	private List<CDependenceEdge> in;
	/** the set of edges that the node directly depends on other nodes **/
	private List<CDependenceEdge> ou;
	
	/* constructor */
	/**
	 * create a dependece node within the graph with respect to the instance of the 
	 * statement being executed.
	 * @param graph
	 * @param instance
	 * @throws Exception
	 */
	protected CDependenceNode(CDependenceGraph graph, CirInstanceNode instance) throws Exception {
		if(graph == null)
			throw new IllegalArgumentException("Invalid graph: null");
		else if(instance == null)
			throw new IllegalArgumentException("Invalid instance: null");
		else {
			this.graph = graph; this.instance = instance;
			this.in = new LinkedList<CDependenceEdge>();
			this.ou = new LinkedList<CDependenceEdge>();
		}
	}
	
	/* getters */
	/**
	 * get the dependence graph where this node belongs to
	 * @return
	 */
	public CDependenceGraph get_graph() { return this.graph; }
	/**
	 * get the instance of statement being executed that the node represents
	 * @return
	 */
	public CirInstanceNode get_instance() { return instance; }
	/**
	 * get the execution of the statement that the node represents
	 * @return
	 */
	public CirExecution get_execution() { return instance.get_execution(); }
	/**
	 * get the statement that the node represents
	 * @return
	 */
	public CirStatement get_statement() { return instance.get_execution().get_statement(); }
	/**
	 * get the set of edges that the others directly depend on this node
	 * @return
	 */
	public Iterable<CDependenceEdge> get_in_edges() { return in; }
	/**
	 * get the set of edges that this node directly depends on others
	 * @return
	 */
	public Iterable<CDependenceEdge> get_ou_edges() { return ou; }
	/**
	 * get the number of edges that the others directly depend on this node
	 * @return
	 */
	public int get_in_degree() { return this.in.size(); }
	/**
	 * get the number of edges that this node directly depends on others
	 * @return
	 */
	public int get_ou_degree() { return this.ou.size(); }
	/**
	 * get the kth edge that the others directly depend on this node
	 * @param k
	 * @return
	 * @throws IndexOutOfBoundsException
	 */
	public CDependenceEdge get_in_edge(int k) throws IndexOutOfBoundsException { return in.get(k); }
	/**
	 * get the kth edge that this node directly depends on other node
	 * @param k
	 * @return
	 * @throws IndexOutOfBoundsException
	 */
	public CDependenceEdge get_ou_edge(int k) throws IndexOutOfBoundsException { return ou.get(k); }
	
	/* setters */
	/**
	 * set the source as control depends on the target with specified predicate value
	 * @param target
	 * @param predicate_value
	 * @return
	 * @throws Exception
	 */
	protected CDependenceControlEdge control_depend(CDependenceNode 
			target, boolean predicate_value) throws Exception {
		if(target == null)
			throw new IllegalArgumentException("Invalid target: null");
		else {
			CDependenceControlEdge edge = new 
					CDependenceControlEdge(this, target, predicate_value);
			this.ou.add(edge); target.in.add(edge); return edge;
		}
	}
	/**
	 * set the source as data depends on the target with specified variable
	 * @param target
	 * @param reference
	 * @return
	 * @throws Exception
	 */
	protected CDependenceDataEdge data_depend(CDependenceNode target, boolean is_define_use,
			CirExpression source_reference, CirExpression target_reference) throws Exception {
		if(target == null)
			throw new IllegalArgumentException("Invalid target: null");
		else {
			CDependenceDataEdge edge = new CDependenceDataEdge(this, target, 
					is_define_use, source_reference, target_reference);
			this.ou.add(edge); target.in.add(edge); return edge;
		}
	}
	
}
