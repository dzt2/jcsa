package com.jcsa.jcmutest.mutant.cir2mutant.struct;

import java.util.LinkedList;
import java.util.List;

import com.jcsa.jcmutest.mutant.cir2mutant.model.CirConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.model.CirExpressionError;
import com.jcsa.jcmutest.mutant.cir2mutant.model.CirMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.model.CirReferenceError;
import com.jcsa.jcmutest.mutant.cir2mutant.model.CirStateError;
import com.jcsa.jcmutest.mutant.cir2mutant.model.CirStateValueError;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.test.state.CStateContexts;

/**
 * Each node in mutation graph refers to a mutation in C-intermediate code,
 * which defines the constraint as well as the state error that shall be
 * caused during testing. They are used to describe the structural relation
 * between cir-mutation during testing.
 * 
 * @author yukimula
 *
 */
public class CirMutationNode {
	
	/* definitions */
	/** the graph in which the node is created **/
	private CirMutationGraph graph;
	/** the mutation that the node is defined **/
	private CirMutation mutation;
	/** the edges that point to this node from the others in the graph **/
	private List<CirMutationEdge> in;
	/** the edges that point from this node to the others in the graph **/
	private List<CirMutationEdge> ou;
	
	/* constructor */
	/**
	 * create an isolated node within the graph with corresond to the mutation as given
	 * @param graph
	 * @param mutation
	 * @throws IllegalArgumentException
	 */
	protected CirMutationNode(CirMutationGraph graph, CirMutation mutation) throws IllegalArgumentException {
		if(graph == null)
			throw new IllegalArgumentException("Invalid graph: null");
		else if(mutation == null)
			throw new IllegalArgumentException("Invalid mutation: null");
		else {
			this.graph = graph;
			this.mutation = mutation;
			this.in = new LinkedList<CirMutationEdge>();
			this.ou = new LinkedList<CirMutationEdge>();
		}
	}
	
	/* getters */
	/**
	 * @return the graph where the node created
	 */
	public CirMutationGraph get_graph() { return this.graph; }
	/**
	 * @return the mutation that defines this node in the graph
	 */
	public CirMutation get_mutation() { return this.mutation; }
	/**
	 * @return the statement where the node's mutation occurs
	 */
	public CirStatement get_statement() { return this.mutation.get_statement(); }
	/**
	 * @return the constraint required for the mutation in this node to infect
	 */
	public CirConstraint get_constraint() { return this.mutation.get_constraint(); }
	/**
	 * @return the state error required for the mutation in this node to influence
	 */
	public CirStateError get_state_error() { return this.mutation.get_state_error(); }
	/**
	 * @return the location where the state error of the node occurs.
	 */
	public CirNode get_location() {
		CirStateError error = this.get_state_error();
		if(error instanceof CirExpressionError) {
			return ((CirExpressionError) error).get_expression();
		}
		else if(error instanceof CirReferenceError) {
			return ((CirReferenceError) error).get_reference();
		}
		else if(error instanceof CirStateValueError) {
			return ((CirStateValueError) error).get_reference();
		}
		else {
			return error.get_statement();
		}
	}
	/**
	 * @return edges that point to this node from the others in the graph
	 */
	public Iterable<CirMutationEdge> get_in_edges() { return this.in; }
	/**
	 * @return edges that point from this node to the others in the graph
	 */
	public Iterable<CirMutationEdge> get_ou_edges() { return this.ou; }
	/**
	 * @param type
	 * @param target
	 * @return create an edge that points from this node to the specified node of target
	 * @throws Exception
	 */
	public CirMutationEdge link_with(CirMutationFlow type, CirMutationNode target) throws Exception {
		CirMutationEdge edge = new CirMutationEdge(type, this, target);
		this.ou.add(edge);
		target.in.add(edge);
		return edge;
	}
	/**
	 * @param contexts null if a context-insensitive approach is applied to this node
	 * @return validate whether the constraint is satisfiable under the given contexts
	 * @throws Exception
	 */
	public boolean validate_constraint(CStateContexts contexts) throws Exception {
		return this.graph.get_cir_mutations().optimize(this.get_constraint(), contexts).satisfiable();
	}
	/**
	 * @param contexts
	 * @return whether the state error is influence-able under the given state context
	 * @throws Exception
	 */
	public boolean validate_state_error(CStateContexts contexts) throws Exception {
		return this.graph.get_cir_mutations().optimize(this.get_state_error(), contexts).influencable();
	}
	
}
