package com.jcsa.jcmutest.sedlang.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.jcsa.jcmutest.sedlang.lang.expr.SedCallExpression;
import com.jcsa.jcmutest.sedlang.lang.expr.SedExpression;

public class SedEvalScope {
	
	/* definitions */
	/** the parent scope of this one **/
	private SedEvalScope parent;
	/** it defines the relation between the scope and its parent **/
	private Object scope_key;
	/** it records the value hold by each string key **/
	private Map<String, SedExpression> stable;
	/** the set of invocation interpreters **/
	private Set<SedCallInvocate> invocate_list;
	
	/* constructor */
	/**
	 * create a root scope
	 */
	protected SedEvalScope() {
		this.parent = null;
		this.scope_key = null;
		this.stable = new HashMap<String, SedExpression>();
		this.invocate_list = new HashSet<SedCallInvocate>();
	}
	/**
	 * create the child of the parent
	 * @param parent
	 * @param key
	 */
	private SedEvalScope(SedEvalScope parent, Object key) {
		this.parent = parent;
		this.scope_key = key;
		this.stable = new HashMap<String, SedExpression>();
		this.invocate_list = new HashSet<SedCallInvocate>();
	}
	/**
	 * @param key
	 * @return get the child of the scope w.r.t. the key
	 * @throws Exception
	 */
	protected SedEvalScope get_child(Object key) throws Exception {
		if(key == null)
			throw new IllegalArgumentException("Invalid key: null");
		else 
			return new SedEvalScope(this, key);
	}
	
	/* getters */
	/**
	 * @return whether the scope is root
	 */
	public boolean is_root() {
		return this.parent == null;
	}
	/**
	 * @return the parent of the scope or null
	 */
	public SedEvalScope get_parent() {
		return this.parent;
	}
	/**
	 * @return key that uniquely defines this scope in this parent
	 */
	public Object get_scope_key() {
		return this.scope_key;
	}
	/**
	 * @param key
	 * @return whether there is value w.r.t. the name 
	 */
	public boolean has(String key) {
		if(this.stable.containsKey(key)) {
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
	 * @return the value w.r.t. the name in the scope or its parent or null
	 */
	public SedExpression get(String key) {
		if(this.stable.containsKey(key)) {
			return this.stable.get(key);
		}
		else if(this.parent != null) {
			return this.parent.get(key);
		}
		else {
			return null;
		}
	}
	/**
	 * add the {key; value} in the local scope
	 * @param key
	 * @param value
	 * @throws IllegalArgumentException
	 */
	public void put(String key, SedExpression value) throws IllegalArgumentException {
		if(key == null)
			throw new IllegalArgumentException("Invalid key: null");
		else if(value == null)
			throw new IllegalArgumentException("Invalid value: null");
		else {
			this.stable.put(key, value);
		}
	}
	/**
	 * @param source
	 * @return the result of the call-expression or itself
	 * @throws Exception
	 */
	public SedExpression invocate(SedCallExpression source) throws Exception {
		for(SedCallInvocate invocate : this.invocate_list) {
			SedExpression result = invocate.invocate(source);
			if(result != null) return result;
		}
		if(this.parent != null) {
			return this.parent.invocate(source);
		}
		else {
			return source;
		}
	}
	
	/* setters */
	/**
	 * add the call-invocate to the scope
	 * @param invocate
	 * @throws IllegalArgumentException
	 */
	public void add(SedCallInvocate invocate) throws IllegalArgumentException {
		if(invocate == null)
			throw new IllegalArgumentException("Invalid invocate");
		else
			this.invocate_list.add(invocate);
	}
	/**
	 * clear the local value table
	 */
	public void clear() {
		this.stable.clear();
	}
	
}
