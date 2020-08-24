package com.jcsa.jcmutest.mutant.sad2mutant.eval;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.lang.irlang.graph.CirFunctionCall;

/**
 * The context that defines the value used for evaluating the value of 
 * SadExpression in static analysis.
 * 
 * @author yukimula
 *
 */
public class SadEvalContext {
	
	/* definition */
	/** the context as its parent **/
	private SadEvalContext parent;
	/** the function in which the context is defined **/
	private CirFunction curr_function;
	/** the function calling relationship **/
	private CirFunctionCall func_call;
	/** mapping from cir-code to the value described in symbolic format **/
	private Map<String, SadExpression> table;
	/** the function invocation interfaces **/
	private List<SadInvocate> invocates;
	/**
	 * create a root context w.r.t. the function
	 * @param curr_function
	 * @throws Exception
	 */
	protected SadEvalContext(CirFunction curr_function) throws Exception {
		if(curr_function == null)
			throw new IllegalArgumentException("Invalid function: null");
		else {
			this.parent = null;
			this.curr_function = curr_function;
			this.func_call = null;
			this.table = new HashMap<String, SadExpression>();
			this.invocates = new ArrayList<SadInvocate>();
		}
	}
	/**
	 * create a child context w.r.t. the calling extension
	 * @param parent
	 * @param func_call
	 * @throws Exception
	 */
	private SadEvalContext(SadEvalContext parent, CirFunctionCall func_call) throws Exception {
		if(parent == null)
			throw new IllegalArgumentException("Invalid parent: null");
		else if(func_call == null)
			throw new IllegalArgumentException("Invalid func_call: null");
		else if(func_call.get_caller() != parent.curr_function)
			throw new IllegalArgumentException("Unmatched function call");
		else {
			this.parent = parent;
			this.func_call = func_call;
			this.curr_function = func_call.get_callee();
			this.table = new HashMap<String, SadExpression>();
			this.invocates = new ArrayList<SadInvocate>();
		}
	}
	
	/* getters */
	/**
	 * @return whether the context is root
	 */
	public boolean is_root() { return this.parent == null; }
	/**
	 * @return the parent of this context or null
	 */
	public SadEvalContext get_parent() { return this.parent; }
	/**
	 * @return the function in which the context is created
	 */
	public CirFunction get_function() { return this.curr_function; }
	/**
	 * @return the calling relation that creates this context
	 */
	public CirFunctionCall get_in_call() { return this.func_call; }
	/**
	 * @param name
	 * @return whether there is a value w.r.t. the name
	 */
	public boolean has(String name) {
		if(this.table.containsKey(name)) {
			return true;
		}
		else if(this.parent == null) {
			return false;
		}
		else {
			return this.parent.has(name);
		}
	}
	/**
	 * @param name
	 * @return the value w.r.t. the name
	 */
	public SadExpression get(String name) {
		if(this.table.containsKey(name)) {
			return this.table.get(name);
		}
		else if(this.parent == null) {
			return null;
		}
		else {
			return this.parent.get(name);
		}
	}
	/**
	 * save the value w.r.t. the name in the context
	 * @param name
	 * @param value
	 * @throws Exception
	 */
	public void put(String name, SadExpression value) throws Exception {
		if(name == null)
			throw new IllegalArgumentException("Invalid name: null");
		else if(value == null)
			throw new IllegalArgumentException("Invalid value null");
		else {
			this.table.put(name, value);
		}
	}
	/**
	 * @param func_call
	 * @return
	 * @throws Exception
	 */
	public SadEvalContext extend(CirFunctionCall func_call) throws Exception {
		return new SadEvalContext(this, func_call);
	}
	@Override
	public String toString() {
		if(this.parent == null) {
			return this.curr_function.get_name();
		}
		else {
			return this.parent.toString() + "::[" + 
					this.func_call.get_call_execution().toString()
					+ " --> " + this.curr_function.get_name() + "]";
		}
	}
	/**
	 * add the invocate machine to the context
	 * @param invocate
	 * @throws Exception
	 */
	public void add_invocate(SadInvocate invocate) throws Exception {
		if(invocate == null)
			throw new IllegalArgumentException("Invalid invocate: null");
		else if(!this.invocates.contains(invocate)) {
			this.invocates.add(invocate);
		}
	}
	/**
	 * @param function
	 * @param arguments
	 * @return calling the invocation-expression or null if none of invocation machines are available.
	 * @throws Exception
	 */
	public SadExpression invocate(SadExpression function, Iterable<SadExpression> arguments) throws Exception {
		SadExpression result = null;
		for(SadInvocate invocate : this.invocates) {
			result = invocate.invocate(function, arguments);
			if(result != null) {
				return result;
			}
		}
		
		if(result == null) {
			if(parent != null) {
				result = parent.invocate(function, arguments);
			}
		}
		
		return result;
	}
	
}
