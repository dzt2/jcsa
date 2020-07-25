package com.jcsa.jcparse.flwa.influence;

import java.util.LinkedList;
import java.util.List;

import com.jcsa.jcparse.flwa.graph.CirInstanceNode;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirField;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirLabel;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * The node represents a program element in C-like intermediate representation.
 * @author yukimula
 *
 */
public class CInfluenceNode {
	
	/* attributes */
	/** the graph where the node is created **/
	private CInfluenceGraph graph;
	/** the type of the program element that the node represents **/
	private CInfluenceNodeType type;
	/** the instance of statement where the syntax source node that this node refers to
	 *  in the C-like intermediate representation **/
	private CirInstanceNode instance;
	/** the C-like intermediate representation syntax node that this node represents **/
	private CirNode cir_source;
	/** the edges pointing to this node from the others **/
	protected List<CInfluenceEdge> in;
	/** the edges pointing from this node to the others **/
	protected List<CInfluenceEdge> ou;
	
	/* constructor */
	/**
	 * create an isolated node within the influence node with respect to the program element (of cir-source)
	 * under the execution of the specified statement (of instance node)
	 * @param graph
	 * @param instance
	 * @param cir_source
	 * @throws Exception
	 */
	protected CInfluenceNode(CInfluenceGraph graph, 
			CirInstanceNode instance, CirNode cir_source) throws Exception {
		if(graph == null)
			throw new IllegalArgumentException("Invalid graph: null");
		else if(instance == null)
			throw new IllegalArgumentException("Invalid instance: null");
		else if(cir_source == null)
			throw new IllegalArgumentException("Invalid cir_source: null");
		else {
			this.graph = graph;
			this.instance = instance;
			this.cir_source = cir_source;
			this.type = this.get_type(cir_source);
			this.in = new LinkedList<CInfluenceEdge>();
			this.ou = new LinkedList<CInfluenceEdge>();
		}
	}
	/**
	 * determine the type of the node based on the CIR source node
	 * @param cir_source
	 * @return
	 * @throws Exception
	 */
	private CInfluenceNodeType get_type(CirNode cir_source) throws Exception {
		if(cir_source instanceof CirStatement)
			return CInfluenceNodeType.statement;
		else if(cir_source instanceof CirExpression)
			return CInfluenceNodeType.expression;
		else if(cir_source instanceof CirField)
			return CInfluenceNodeType.field;
		else if(cir_source instanceof CirLabel)
			return CInfluenceNodeType.label;
		else throw new IllegalArgumentException(
				"Unsupport element: " + cir_source.getClass().getSimpleName());
		
	}
	
	/* getters */
	/**
	 * get the graph where this node is created
	 * @return
	 */
	public CInfluenceGraph get_graph() { return this.graph; }
	/**
	 * get the instance of the execution of the statement where the program element 
	 * that this node represents is defined.
	 * @return
	 */
	public CirInstanceNode get_instance() { return this.instance; }
	/**
	 * get the context of the execution instance of the statement where the program
	 * element that this node represents is defined.
	 * @return
	 */
	public Object get_instance_context() { return this.instance.get_context(); }
	/**
	 * get the execution of the statement where the program element that this node 
	 * represents is defined
	 * @return
	 */
	public CirExecution get_execution() { return this.instance.get_execution(); }
	/**
	 * get the statement where the program element that this node represents is defined
	 * @return
	 */
	public CirStatement get_statement() { return this.instance.get_execution().get_statement(); }
	/**
	 * get the node of C-like intermediate code that represents the program element this node refers to
	 * @return
	 */
	public CirNode get_cir_source() { return this.cir_source; }
	/**
	 * get the type of the node, either statement, expression, field or label.
	 * @return
	 */
	public CInfluenceNodeType get_node_type() { return this.type; }
	/**
	 * get the edges to this node from the others, which influence on this one.
	 * @return
	 */
	public Iterable<CInfluenceEdge> get_in_edges() { return in; }
	/**
	 * get the edges from this node to the others, which are influenced by this.
	 * @return
	 */
	public Iterable<CInfluenceEdge> get_ou_edges() { return ou; }
	/**
	 * get the number of edges to this node from the others, which influence on this one.
	 * @return
	 */
	public int get_in_degree() { return in.size(); }
	/**
	 * get the number of edges from this node to the others, which are influenced by this.
	 * @return
	 */
	public int get_ou_degree() { return ou.size(); }
	/**
	 * get the kth edge to this node from the others, which influence on this one.
	 * @param k
	 * @return
	 * @throws IndexOutOfBoundsException
	 */
	public CInfluenceEdge get_in_edge(int k) throws IndexOutOfBoundsException { return in.get(k); }
	/**
	 * get the kth edge from this node to the others, which are influenced by this.
	 * @param k
	 * @return
	 * @throws IndexOutOfBoundsException
	 */
	public CInfluenceEdge get_ou_edge(int k) throws IndexOutOfBoundsException { return ou.get(k); }
	
	/* setter */
	/**
	 * link this node to the target via the influence edge of specified type
	 * @param edge_type
	 * @param target
	 * @return the edge that connects from this node to the target
	 * @throws Exception
	 */
	protected CInfluenceEdge link_with(CInfluenceEdgeType edge_type, CInfluenceNode target) throws Exception {
		CInfluenceEdge edge = new CInfluenceEdge(edge_type, this, target);
		this.ou.add(edge); target.in.add(edge); return edge;
	}
	
}
