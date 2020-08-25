package com.jcsa.jcmutest.mutant.sad2mutant.eval;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadCallExpression;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.lang.irlang.graph.CirFunctionCall;

/**
 * The scope defines the data used for evaluating SadExpression.
 * 
 * @author yukimula
 *
 */
public class SadCallScope {
	
	/* definitions */
	/** the scope in which this scope is enclosed **/
	private SadCallScope parent;
	/** the function in which the scope of local data is defined **/
	private CirFunction function;
	/** the calling relation that extends this scope or null if parent is null **/
	private CirFunctionCall call;
	/** the mapping from cir-code to the symbolic expression to replace. **/
	private Map<String, SadExpression> local_table;
	/** the invocation machine for interpreting the symbolic expression. **/
	private List<SadInvocate> invocate_list;
	
	/* constructors */
	/**
	 * create the root scope
	 * @param function
	 * @throws Exception
	 */
	protected SadCallScope(CirFunction function) throws Exception {
		if(function == null)
			throw new IllegalArgumentException("Invalid function: null");
		else {
			this.parent = null;
			this.function = function;
			this.call = null;
			this.local_table = new HashMap<String, SadExpression>();
			this.invocate_list = new ArrayList<SadInvocate>();
		}
	}
	/**
	 * create the child scope under the given parent
	 * @param parent
	 * @param call
	 * @throws Exception
	 */
	private SadCallScope(SadCallScope parent, CirFunctionCall call) throws Exception {
		if(parent == null)
			throw new IllegalArgumentException("Invalid parent: null");
		else if(call == null)
			throw new IllegalArgumentException("Invalid call as null");
		else if(call.get_caller() != parent.function)
			throw new IllegalArgumentException("Not matched relation");
		else {
			this.parent = parent;
			this.call = call;
			this.function = call.get_callee();
			this.local_table = new HashMap<String, SadExpression>();
			this.invocate_list = new ArrayList<SadInvocate>();
		}
	}
	
	/* getters */
	/**
	 * @return the parent scope where this scope is created
	 */
	public SadCallScope get_parent() { return this.parent; }
	/**
	 * @return whether the scope is the root scope
	 */
	public boolean is_root() { return this.parent == null; }
	/**
	 * @return the function that the scope refers to
	 */
	public CirFunction get_function() { return this.function; }
	/**
	 * @return the calling relation that extends on this scope from its parent
	 * 		   or null if the scope is a root.
	 */
	public CirFunctionCall get_call() { return this.call; }
	/**
	 * @param key
	 * @return whether there is value to the key in the scope or its parent
	 */
	public boolean has(String key) {
		if(this.local_table.containsKey(key)) {
			return true;
		}
		else if(this.parent != null) {
			return this.parent.has(key);
		}
		else {
			return false;
		}
	}
	/**
	 * @param key
	 * @return the symbolic value set for the given key
	 */
	public SadExpression get(String key) {
		if(this.local_table.containsKey(key)) {
			return this.local_table.get(key);
		}
		else if(this.parent != null) {
			return this.parent.get(key);
		}
		else {
			return null;
		}
	}
	/**
	 * set the key-value in local-table of the scope
	 * @param key
	 * @param value
	 * @throws Exception
	 */
	public void set(String key, SadExpression value) throws Exception {
		if(key == null || key.isBlank())
			throw new IllegalArgumentException("Invalid key: null");
		else if(value == null)
			throw new IllegalArgumentException("Invalid value: null");
		else {
			this.local_table.put(key, value);
		}
	}
	/**
	 * add the invocation machine to interpret the symbolic expression
	 * @param invocate
	 * @throws Exception
	 */
	public void add_invocate(SadInvocate invocate) throws Exception {
		if(invocate == null) {
			throw new IllegalArgumentException("Invalid invocate: null");
		}
		else if(!this.invocate_list.contains(invocate)) {
			this.invocate_list.add(invocate);
		}
	}
	/**
	 * @param expression
	 * @return evaluate the call-expression using invocation machine or itself
	 * 		   if none of invocation machines are provided to the scope
	 * @throws Exception
	 */
	public SadExpression invocate(SadCallExpression expression) throws Exception {
		/* initialization */
		SadExpression result = null;
		
		/* local interpretation */
		for(SadInvocate invocate : this.invocate_list) {
			result = invocate.invocate(expression);
			if(result != null) {
				break;
			}
		}
		
		/* calling interpretation */
		if(result == null && this.parent != null) {
			result = this.parent.invocate(expression);
		}
		
		/* unable to interpret the expression */
		if(result == null) {
			result = expression;
		}
		
		return result;
	}
	/**
	 * @param call
	 * @return the child scope extended from this scope as its parent
	 * @throws Exception
	 */
	public SadCallScope extend(CirFunctionCall call) throws Exception {
		return new SadCallScope(this, call);
	}
	@Override
	public String toString() {
		if(this.parent == null) {
			return this.function.get_name();
		}
		else {
			return parent.toString() + "::[" + 
					call.get_call_execution().toString() + 
					"-->" + this.function.get_name() + "]";
		}
	}
	
}
