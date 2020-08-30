package com.jcsa.jcmutest.mutant.sed2mutant.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedCallExpression;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.lang.irlang.graph.CirFunctionCall;

/**
 * The scope defines the values that might be used in symbolic evaluation.
 * 
 * @author yukimula
 *
 */
public class SedEvalScope {
	
	/* definitions */
	/** the parent scope of this one or null if it's root **/
	private SedEvalScope parent;
	/** the function in which the scope is created **/
	private CirFunction function;
	/** the calling relation that extends this scope or null if it's root **/
	private CirFunctionCall call;
	/** mapping from the cir-code key to the value it contains in evaluation **/
	private Map<String, SedExpression> local_table;
	/** the set of invocations to interpret the calling-expression **/
	private Set<SedInvocate> invocates;
	
	/* constructors */
	/**
	 * create the root evaluation scope w.r.t. the function specified
	 * @param function
	 * @throws Exception
	 */
	public SedEvalScope(CirFunction function) throws Exception {
		if(function == null)
			throw new IllegalArgumentException("Invalid function: null");
		else {
			this.parent = null;
			this.function = function;
			this.call = null;
			this.local_table = new HashMap<String, SedExpression>();
			this.invocates = new HashSet<SedInvocate>();
		}
	}
	/**
	 * create a child scope of parent w.r.t. the calling extension
	 * @param parent
	 * @param call
	 * @throws Exception
	 */
	private SedEvalScope(SedEvalScope parent, CirFunctionCall call) throws Exception {
		if(parent == null)
			throw new IllegalArgumentException("Invalid parent: null");
		else if(call == null)
			throw new IllegalArgumentException("Invalid call: null");
		else if(call.get_caller() != parent.function) 
			throw new IllegalArgumentException("Not match the parent");
		else {
			this.parent = parent;
			this.function = call.get_callee();
			this.call = call;
			this.local_table = new HashMap<String, SedExpression>();
			this.invocates = new HashSet<SedInvocate>();
		}
	}
	
	/* getters */
	/**
	 * @return whether the scope is of root
	 */
	public boolean is_root() { return this.parent == null; }
	/**
	 * @return the parent scope of this one or null if it's root 
	 */
	public SedEvalScope get_parent() { return this.parent; }
	/**
	 * @return the function in which the scope is created
	 */
	public CirFunction get_function() { return this.function; }
	/**
	 * @return the calling-relation that extends this scope
	 */
	public CirFunctionCall get_call() { return this.call; }
	/**
	 * @param key
	 * @return whether there is value w.r.t. this key
	 */
	public boolean has(String key) { return this.local_table.containsKey(key); }
	/**
	 * @param key
	 * @return the value w.r.t. the key or null if there is not defined
	 */
	public SedExpression get(String key) {
		if(this.local_table.containsKey(key)) {
			return this.local_table.get(key);
		}
		else {
			return null;
		}
	}
	/**
	 * @param key
	 * @param value value w.r.t. the key or null if it is not defined
	 * @throws Exception
	 */
	public void put(String key, SedExpression value) throws Exception {
		if(key == null || key.isBlank())
			throw new IllegalArgumentException("Invalid key: null");
		else if(value == null)
			throw new IllegalArgumentException("Invalid value: null");
		else {
			this.local_table.put(key, value);
		}
	}
	/**
	 * add the calling invocation machine
	 * @param invocate
	 * @throws Exception
	 */
	public void add(SedInvocate invocate) throws Exception {
		if(invocate == null)
			throw new IllegalArgumentException("Invalid invocate");
		else
			this.invocates.add(invocate);
	}
	/**
	 * @param source
	 * @return the value explained by the calling source
	 * @throws Exception
	 */
	public SedExpression invocate(SedCallExpression source) throws Exception {
		SedExpression result = null;
		for(SedInvocate invocate : this.invocates) {
			result = invocate.invocate(source);
			if(result != null) return result;
		}
		if(parent != null) {
			return this.parent.invocate(source);
		}
		else {
			return source;
		}
	}
	/**
	 * @param call
	 * @return the child scope from this one w.r.t. the calling relation
	 * @throws Exception
	 */
	public SedEvalScope extend(CirFunctionCall call) throws Exception {
		return new SedEvalScope(this, call);
	}
	
}
