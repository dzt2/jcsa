package com.jcsa.jcmuta.mutant.sem2mutation.error;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticAssertion;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * It constains a set of assertions describing the errors in program testing.
 * @author yukimula
 *
 */
public class StateError {
	
	/** the integer ID tag this node in the graph **/
	private int id;
	/** the graph where the state errors are created **/
	private StateErrorGraph graph;
	/** the location where the state error occus in **/
	private CirNode location;
	/** the set of assertions to be satisfied **/
	private List<SemanticAssertion> assertions;
	/** the flows to or from this error to the others **/
	private List<StateErrorFlow> in, ou;
	/***
	 * create an empty constraint instance in the graph
	 */
	protected StateError(StateErrorGraph graph, int id) {
		this.graph = graph; this.id = id;
		this.assertions = new ArrayList<SemanticAssertion>();
		this.in = new LinkedList<StateErrorFlow>();
		this.ou = new LinkedList<StateErrorFlow>();
	}
	
	/* getters */
	public int get_id() { return this.id; }
	/**
	 * get the graph where the constraint is created
	 * @return
	 */
	public StateErrorGraph get_graph() { return this.graph; }
	/**
	 * get the set of assertions to caused as errors
	 * @return
	 */
	public Iterable<SemanticAssertion> get_assertions() { return this.assertions; }
	/**
	 * get the propagation flows to generate this error
	 * @return
	 */
	public Iterable<StateErrorFlow> get_in_flows() { return this.in; }
	/**
	 * get the propagation flows from this error to generate the others
	 * @return
	 */
	public Iterable<StateErrorFlow> get_ou_flows() { return this.ou; }
	/**
	 * get the number of the propagation flows to generate this error
	 * @return
	 */
	public int get_in_degree() { return this.in.size(); }
	/**
	 * get the number of the propagation flows from this error to generate the others
	 * @return
	 */
	public int get_ou_degree() { return this.ou.size(); }
	/**
	 * get the location where the state error occurs
	 * @return
	 */
	public CirNode get_location() { return this.location; }
	/**
	 * get the number of assertions in the state error
	 * @return
	 */
	public int number_of_assertions() { return this.assertions.size(); }
	
	/* setters */
	/**
	 * set the semantic assertions describing the semantic errors.
	 * @param assertions
	 * @throws Exception
	 */
	protected void set_assertions(Iterable<SemanticAssertion> assertions) throws Exception {
		if(assertions == null)
			throw new IllegalArgumentException("Invalid assertions: null");
		else {
			this.assertions.clear(); this.location = null;
			for(SemanticAssertion assertion : assertions) {
				if(assertion.is_state_error()) {
					if(!this.assertions.contains(assertion))
						this.assertions.add(assertion);
					
					if(assertion.number_of_operands() > 0) {
						if(this.location == null)
							this.location = assertion.get_location();
						else if(this.location != assertion.get_location()) 
							throw new IllegalArgumentException("Invalid location");
					}
				}
				else {
					throw new IllegalArgumentException("Not constraint assertion!");
				}
			}
		}
	}
	/**
	 * generate a new propagation flow from this source to the target in the constraint.
	 * @param constraint
	 * @param target
	 * @return
	 * @throws Exception
	 */
	protected StateErrorFlow propagate(Iterable<SemanticAssertion> constraint, StateError target) throws Exception {
		for(StateErrorFlow flow : this.ou) {
			if(flow.get_target() == target) return flow;
		}
		
		StateErrorFlow flow = new StateErrorFlow(constraint, this, target);
		this.ou.add(flow); target.in.add(flow); return flow;
	}
	
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		
		buffer.append("{ ");
		for(SemanticAssertion assertion : this.assertions) {
			buffer.append(assertion.get_state_error_function().toString());
			if(assertion.get_parameter() != null)
				buffer.append("@" + assertion.get_parameter().toString());
			buffer.append("; ");
		}
		buffer.append("}( ");
		
		CirNode location = this.location;
		if(location != null) {
			if(location.get_ast_source() != null) {
				String code = location.get_ast_source().get_location().trim_code();
				buffer.append("\"" + code + "\"");
			}
			else {
				buffer.append("CIR[").append(location.get_node_id()).append("]");
			}
		}
		
		buffer.append(" )");
		
		return buffer.toString();
		//return this.assertions.toString();
	}
	
}
