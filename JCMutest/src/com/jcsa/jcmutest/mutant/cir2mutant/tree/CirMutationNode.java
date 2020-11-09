package com.jcsa.jcmutest.mutant.cir2mutant.tree;

import java.util.LinkedList;
import java.util.List;

import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirStateError;
import com.jcsa.jcparse.flwa.symbol.CStateContexts;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * Each node in mutation gragh refers to a statement with one or zero state error.
 * 
 * @author yukimula
 *
 */
public class CirMutationNode {
	
	/* definitions */
	private CirMutationGraph graph;
	private CirExecution execution;
	private CirMutationNodeType type;
	private CirStateError state_error;
	private CirMutationStatus status;
	private List<CirMutationEdge> in_edges;
	private List<CirMutationEdge> ou_edges;
	
	/* constructor */
	/**
	 * create an isolated node in the graph w.r.t. the execution node and the state error
	 * @param graph
	 * @param type
	 * @param execution
	 * @param state_error
	 * @throws Exception
	 */
	protected CirMutationNode(CirMutationGraph graph, CirMutationNodeType type, 
			CirExecution execution, CirStateError state_error) throws Exception {
		if(graph == null)
			throw new IllegalArgumentException("Invalid graph: null");
		else if(type == null)
			throw new IllegalArgumentException("Invalid type as null");
		else if(execution == null)
			throw new IllegalArgumentException("Invalid execution: null");
		else {
			this.graph = graph;
			this.type = type;
			this.execution = execution;
			this.state_error = state_error;
			this.in_edges = new LinkedList<CirMutationEdge>();
			this.ou_edges = new LinkedList<CirMutationEdge>();
		}
	}
	
	/* getters */
	/**
	 * @return the graph where the node is created
	 */
	public CirMutationGraph get_graph() { return this.graph; }
	/**
	 * @return the type of the node in the graph
	 */
	public CirMutationNodeType get_type() { return this.type; }
	/**
	 * @return the execution of the statement where the node occurs
	 */
	public CirExecution get_execution() { return this.execution; }
	/**
	 * @return the statement where the node occurs
	 */
	public CirStatement get_statement() { return this.execution.get_statement(); }
	/**
	 * @return whether there is state error in the node
	 */
	public boolean has_state_error() { return this.state_error != null; }
	/**
	 * @return the state error occurs in the node
	 */
	public CirStateError get_state_error() { return this.state_error; }
	/**
	 * @return the status to describe the state of the error and execution of the node
	 */
	public CirMutationStatus get_status() { return this.status; }
	/**
	 * @return edges pointing to this node from the others
	 */
	public Iterable<CirMutationEdge> get_in_edges() { return this.in_edges; }
	/**
	 * @return edges pointing from this node to the others
	 */ 
	public Iterable<CirMutationEdge> get_ou_edges() { return this.ou_edges; }
	
	/* setters */
	/**
	 * delete this node from the graph
	 */
	protected void delete() {
		if(this.graph != null) {
			this.graph = null;
			this.type = null;
			this.execution = null;
			this.state_error = null;
			this.in_edges.clear();
			this.ou_edges.clear();
			this.status = null;
			this.in_edges = null;
			this.ou_edges = null;
		}
	}
	/**
	 * clear the records in status of the error in this node
	 */
	protected void reset_status() { this.status.clear(); }
	/**
	 * append the record in the status of this node
	 * @param contexts
	 * @throws Exception
	 */
	protected void append_status(CStateContexts contexts) throws Exception {
		if(this.state_error != null)
			this.status.append(state_error, contexts);
		else
			this.status.append();
	}
	/**
	 * link this node to the target w.r.t. the constraint and return the new linking edge
	 * @param target
	 * @param constraint
	 * @return
	 * @throws Exception
	 */
	protected CirMutationEdge link_to(CirMutationEdgeType type, 
			CirMutationNode target, CirConstraint constraint) throws Exception {
		CirMutationEdge edge = new CirMutationEdge(type, this, target, constraint);
		this.ou_edges.add(edge);
		target.in_edges.add(edge);
		return edge;
	}
	
}
