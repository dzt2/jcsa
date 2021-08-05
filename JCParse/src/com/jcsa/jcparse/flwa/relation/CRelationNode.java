package com.jcsa.jcparse.flwa.relation;

import java.util.LinkedList;
import java.util.List;

import com.jcsa.jcparse.flwa.graph.CirInstanceNode;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirReferExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirValueExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirTagStatement;

/**
 * Each node in relational graph represents a program element in testing, including:<br>
 * 	(1)	<code>Statement</code>: a statement being executed in C-like intermediate representation, except
 * 		the useless goto and tag statement.<br>
 * 	(2)	<code>Reference</code>: a reference expression used or defined in C-like intermediate representation.<br>
 * 	(3)	<code>Expression</code>: a non-reference expression used in C-like intermediate representation.<br>
 * @author yukimula
 *
 */
public class CRelationNode {

	/* attributes */
	/** the graph where this node is created **/
	private CRelationGraph graph;
	/** the instance of the statement being executed where
	 * 	the source node that the node represent is defined **/
	private CirInstanceNode instance;
	/** the source node of C-like intermediate representation
	 * 	code that this node refers to. **/
	private CirNode cir_source;
	/** the type of this node in relational graph **/
	private CRelationNodeType type;
	/** the edges point to this node from others **/
	private List<CRelationEdge> in;
	/** the edges point from this ndoe to others **/
	private List<CRelationEdge> ou;

	/* constructor */
	/**
	 * Create a node in the specified relational graph with respect to the specified node of C-like intermediate
	 * representation code in the given instance of statement being executed.
	 * @param graph
	 * @param instance
	 * @param cir_source
	 * @throws Exception
	 */
	protected CRelationNode(CRelationGraph graph, CirInstanceNode instance, CirNode cir_source) throws Exception {
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
			this.in = new LinkedList<>();
			this.ou = new LinkedList<>();
		}
	}
	/**
	 * determine the type of the node based on CIR syntactic node in program code.
	 * @param cir_source
	 * @return
	 * @throws Exception
	 */
	private CRelationNodeType get_type(CirNode cir_source) throws Exception {
		if(cir_source instanceof CirStatement) {
			if(cir_source instanceof CirTagStatement) {
				throw new IllegalArgumentException(
						"Invalid cir_source: " +
						cir_source.getClass().getSimpleName());
			}
			else { return CRelationNodeType.Statement; }
		}
		else if(cir_source instanceof CirReferExpression) {
			return CRelationNodeType.Reference;
		}
		else if(cir_source instanceof CirValueExpression) {
			return CRelationNodeType.Expression;
		}
		else {
			throw new IllegalArgumentException(
					"Invalid cir_source: " +
					cir_source.getClass().getSimpleName());
		}
	}

	/* getters */
	/**
	 * get the graph where this node is created
	 * @return
	 */
	public CRelationGraph get_graph() { return this.graph; }
	/**
	 * get the instance of the statement being executed where the source node of C-like
	 * intermediate representation code that this node refers to is defined in.
	 * @return
	 */
	public CirInstanceNode get_instance() { return instance; }
	/**
	 * get the execution of the statement being executed where the source node of C-like
	 * intermediate representation code that this node refers to is defined in.
	 * @return
	 */
	public CirExecution get_execution() { return instance.get_execution(); }
	/**
	 * get the statement being executed where the source node of C-like intermediate
	 * representation code that this node refers to is defined in.
	 * @return
	 */
	public CirStatement get_statement() { return instance.get_execution().get_statement(); }
	/**
	 * get the source node of the C-like intermediate representation code that this node refers to
	 * @return
	 */
	public CirNode get_cir_source() { return this.cir_source; }
	/**
	 * get the type of the node
	 * @return
	 */
	public CRelationNodeType get_type() { return this.type; }
	/**
	 * get the edges that point to this node from the others
	 * @return
	 */
	public Iterable<CRelationEdge> get_in_edges() { return in; }
	/**
	 * get the edges that point from this node to the others
	 * @return
	 */
	public Iterable<CRelationEdge> get_ou_edges() { return ou; }
	/**
	 * get the number of edges pointng to this node from others
	 * @return
	 */
	public int get_in_degree() { return in.size(); }
	/**
	 * get the number of edges pointing from this node to others
	 * @return
	 */
	public int get_ou_degree() { return ou.size(); }
	/**
	 * get the kth edge that points to this node from the others
	 * @param k
	 * @return
	 * @throws IndexOutOfBoundsException
	 */
	public CRelationEdge get_in_edge(int k) throws IndexOutOfBoundsException { return in.get(k); }
	/**
	 * get the kth edge that points from this node to the others
	 * @param k
	 * @return
	 * @throws IndexOutOfBoundsException
	 */
	public CRelationEdge get_ou_edge(int k) throws IndexOutOfBoundsException { return ou.get(k); }
	@Override
	public String toString() {
		try {
			switch(type) {
			case Statement:
			{
				return "[stmt]: " + this.get_statement().generate_code(true);
			}
			case Reference:
			{
				return "[refr]: {" + this.cir_source.generate_code(true) +
						"} in {" + this.get_statement().generate_code(true) + "}";
			}
			case Expression:
			{
				return "[expr]: {" + this.cir_source.generate_code(true) +
						"} in {" + this.get_statement().generate_code(true) + "}";
			}
			default: throw new IllegalArgumentException("Invalid type: " + type);
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/* setters */
	/**
	 * link this node to the target with an edge of specified type.
	 * @param type
	 * @param target
	 * @return
	 */
	protected CRelationEdge link_to(CRelationEdgeType type, CRelationNode target) {
		/** NOTE: to avoid duplicated relation **/
		for(CRelationEdge edge : this.ou) {
			if(edge.get_target() == target)
				return edge;
		}

		/** create new relation between the two nodes in graph **/
		CRelationEdge edge = new CRelationEdge(type, this, target);
		this.ou.add(edge); target.in.add(edge); return edge;
	}

}
