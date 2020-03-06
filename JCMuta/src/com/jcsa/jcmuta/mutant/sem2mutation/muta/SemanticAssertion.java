package com.jcsa.jcmuta.mutant.sem2mutation.muta;

import java.util.LinkedList;
import java.util.List;

import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * Semantic assertion describes either a constraint that is required to hold on program elements, or the
 * state that is expected to hold at some point of the program, which relies on the keyword it contains:<br>
 * 	1. function: the semantic function describes the assertion over the program element(s);<br>
 * 	2. operands: the sequence of operands on which the semantic assertion is taken over.<br>
 * @author yukimula
 *
 */
public class SemanticAssertion {
	
	/* attributes */
	/** the parent instance that create the semantic assertion **/
	private SemanticAssertions assertions;
	/** the semantic function defines the assertion taken over the program elements **/
	private Object function;
	/** the sequence of program elements on which the semantic assertions are taken **/
	private List<Object> operands;
	/** the inference that infer to or from this asserton **/
	protected List<SemanticInference> in, ou;
	
	/* constructor */
	/**
	 * create a semantic assertion with specified function on none operands
	 * @param function
	 * @throws Exception
	 */
	protected SemanticAssertion(SemanticAssertions assertions, Object function) throws Exception {
		if(function == null)
			throw new IllegalArgumentException("Invalid function: null");
		else if(assertions == null)
			throw new IllegalArgumentException("Invalid assertion parent");
		else {
			this.assertions = assertions;
			this.function = function;
			this.operands = new LinkedList<Object>();
			this.in = new LinkedList<SemanticInference>();
			this.ou = new LinkedList<SemanticInference>();
		}
	}
	
	/* getters */
	/**
	 * get the parent instance that creates the semantic assertion.
	 * @return
	 */
	public SemanticAssertions get_assertions() { return this.assertions; }
	/**
	 * whether the assertion describes the semantic constraints on program elements
	 * @return
	 */
	public boolean is_constraint() { return this.function instanceof ConstraintFunction; }
	/**
	 * get the constraint function if the assertion describes constraint on some point;
	 * @return
	 */
	public ConstraintFunction get_constraint_function() { return (ConstraintFunction) this.function; }
	/**
	 * whether the assertion describes the semantic state error on program elements
	 * @return
	 */
	public boolean is_state_error() { return this.function instanceof StateErrorFunction; }
	/**
	 * get the state error function if the assertion describes error at some point of the proram.
	 * @return
	 */
	public StateErrorFunction get_state_error_function() { return (StateErrorFunction) this.function; }
	/**
	 * get the semantic function on which the assertion is defined
	 * @return
	 */
	public Object get_function() { return this.function; }
	/**
	 * get the program elements or parameters on which the semantic assertion is taken
	 * @return
	 */
	public Iterable<Object> get_operands() { return this.operands; }
	/**
	 * get the number of operands on which the semantic assertion is taken on
	 * @return
	 */
	public int number_of_operands() { return this.operands.size(); }
	/**
	 * get the kth operand on which the semantic assertion is taken on.
	 * @param k
	 * @return
	 * @throws Exception
	 */
	public Object get_operand(int k) throws Exception { return this.operands.get(k); }
	private static final StringBuilder buffer = new StringBuilder();
	@Override
	public String toString() {
		buffer.setLength(0);
		
		buffer.append(this.function.toString());
		buffer.append("(");
		boolean first = true;
		for(Object operand : this.operands) {
			if(first) {
				first = false;
			}
			else {
				buffer.append(", ");
			}
			buffer.append(operand.toString());
		}
		buffer.append(")");
		
		return buffer.toString();
	}
	protected void add_operand(Object operand) throws Exception {
		if(operand == null)
			throw new IllegalArgumentException("Invalid operad: null");
		else { this.operands.add(operand); }
	}
	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof SemanticAssertion) {
			return this.toString().equals(obj.toString());
		}
		else return false;
	}
	/**
	 * get the inferences that infer to this assertion from the others
	 * @return
	 */
	public Iterable<SemanticInference> get_in_inferences() { return this.in; }
	/**
	 * get the inference from this assertion so to generate the others
	 * @return
	 */
	public Iterable<SemanticInference> get_ou_inferences() { return this.ou; }
	/**
	 * get the number of inferences that cause this assertion in analysis
	 * @return
	 */
	public int get_in_degree() { return this.in.size(); }
	/**
	 * get the number of inferences that caused by the other assertions
	 * @return
	 */
	public int get_ou_degree() { return this.ou.size(); }
	public CirNode get_location() {
		for(Object operand : this.operands) {
			if(operand instanceof CirNode)
				return (CirNode) operand;
		}
		return null;
	}
	public Object get_parameter() {
		for(Object operand : this.operands) {
			if(operand instanceof CirNode) { 
				
			}
			else if(operand != null) {
				return operand;
			}
		}
		return null;
	}
	
}
