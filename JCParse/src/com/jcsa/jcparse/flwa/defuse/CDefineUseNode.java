package com.jcsa.jcparse.flwa.defuse;

import java.util.LinkedList;
import java.util.List;

import com.jcsa.jcparse.flwa.graph.CirInstanceNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class CDefineUseNode {

	/* attributes */
	/** the graph where this node is created **/
	private CDefineUseGraph graph;
	/** the instance of the statement where the use or define node is created **/
	private CirInstanceNode instance;
	/** whether this node is a definition point or the usage point in graph **/
	private boolean define;
	/** the expression that the node refers to in the program **/
	private CirExpression expression;
	/** the set of define or use nodes linked to this use or define node **/
	private List<CDefineUseEdge> in;
	/** the set of define or used nodes that this use or define node links to **/
	private List<CDefineUseEdge> ou;

	/* constructor */
	/**
	 * create a node within the graph with respect to the instance and expression
	 * @param graph
	 * @param define
	 * @param instance
	 * @param expression
	 * @throws Exception
	 */
	protected CDefineUseNode(CDefineUseGraph graph, boolean define,
			CirInstanceNode instance, CirExpression expression) throws Exception {
		if(graph == null)
			throw new IllegalArgumentException("Invalid graph: null");
		else if(instance == null)
			throw new IllegalArgumentException("Invalid instance: null");
		else if(expression == null)
			throw new IllegalArgumentException("Invalid expression: null");
		else {
			this.graph = graph;
			this.define = define;
			this.instance = instance;
			this.expression = expression;
			this.in = new LinkedList<>();
			this.ou = new LinkedList<>();
		}
	}

	/* getters */
	/**
	 * get the definition-usage graph
	 * @return
	 */
	public CDefineUseGraph get_graph() { return this.graph; }
	/**
	 * get the instance that the node's expression's statement is executed
	 * @return
	 */
	public CirInstanceNode get_instance() { return this.instance; }
	/**
	 * get the execution of the statement where the expression of the node is used.
	 * @return
	 */
	public CirExecution get_execution() { return instance.get_execution(); }
	/**
	 * get the statement of the expression that this node represents
	 * @return
	 */
	public CirStatement get_statement() { return instance.get_execution().get_statement(); }
	/**
	 * whether this node is a definition node
	 * @return
	 */
	public boolean is_define() { return this.define; }
	/**
	 * whether this is a usage node
	 * @return
	 */
	public boolean is_usage() { return !this.define; }
	/**
	 * get the expression that this node represents.
	 * @return
	 */
	public CirExpression get_expression() { return this.expression; }
	/**
	 * get the set of edges that linked to this node
	 * @return
	 */
	public Iterable<CDefineUseEdge> get_in_edges() { return this.in; }
	/**
	 * get the set of edges that liked from this node
	 * @return
	 */
	public Iterable<CDefineUseEdge> get_ou_edges() { return this.ou; }
	/**
	 * get the number of edges that linked to this node
	 * @return
	 */
	public int get_in_degree() { return this.in.size(); }
	/**
	 * get the number of edges that liked from this node
	 * @return
	 */
	public int get_ou_degree() { return this.ou.size(); }
	/**
	 * get the kth edge that linked to this node
	 * @param k
	 * @return
	 * @throws IndexOutOfBoundsException
	 */
	public CDefineUseEdge get_in_edge(int k) throws IndexOutOfBoundsException { return in.get(k); }
	/**
	 * get the kth edge that liked from this node
	 * @param k
	 * @return
	 * @throws IndexOutOfBoundsException
	 */
	public CDefineUseEdge get_ou_edge(int k) throws IndexOutOfBoundsException { return ou.get(k); }
	/**
	 * get the reference of the node
	 * @return
	 * @throws Exception
	 */
	public String get_reference() throws Exception { return this.expression.generate_code(false); }

	/* setters */
	/**
	 * link this node to the target, of which pairs need to be:<br>
	 * (1) [define_use]: the definition node is further used in another point;<br>
	 * (2) [use_define]: the usage node is further used to define another point.<br>
	 * @param target
	 * @return
	 * @throws Exception
	 */
	protected CDefineUseEdge link_to(CDefineUseNode target) throws Exception {
		if(target == null || target.graph != this.graph)
			throw new IllegalArgumentException("Invalid target: " + target);
		else if(this.define && !target.define) {
			CDefineUseEdge edge = new
					CDefineUseEdge(this, target, this.instance);
			this.ou.add(edge); target.in.add(edge); return edge;
		}
		else if(!this.define && target.define) {
			CDefineUseEdge edge = new
					CDefineUseEdge(this, target, target.instance);
			this.ou.add(edge); target.in.add(edge); return edge;
		}
		else throw new IllegalArgumentException("Invalid target: " + this.define + " to " + target.define);
	}

}
