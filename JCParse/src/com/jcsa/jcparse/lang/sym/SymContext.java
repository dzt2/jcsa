package com.jcsa.jcparse.lang.sym;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The context provides value to the symbolic expressions.
 * 
 * @author yukimula
 *
 */
public class SymContext {
	
	/* definitions */
	/** the parent context or null **/
	private SymContext parent;
	/** the key that extends this context from its parent **/
	private Object key;
	/** mapping from key to the value it holds in states **/
	private Map<Object, SymExpression> ltable;
	/** the set of invocation interfaces to compute call **/
	private Set<SymInvocate> invocate_set;
	
	/* constructors */
	/**
	 * create a root context for evaluatint symbolic expression
	 */
	protected SymContext() {
		this.parent = null;
		this.key = null;
		this.ltable = new HashMap<Object, SymExpression>();
		this.invocate_set = new HashSet<SymInvocate>();
	}
	/**
	 * create a child context from the parent with specified key
	 * @param parent
	 * @param key
	 */
	private SymContext(SymContext parent, Object key) {
		this.parent = parent;
		this.key = key;
		this.ltable = new HashMap<Object, SymExpression>();
		this.invocate_set = new HashSet<SymInvocate>();
	}
	
	/* getters */
	/**
	 * @return the parent context or null
	 */
	protected SymContext get_parent() { return this.parent; }
	/**
	 * @return the key that extends this context from its parent
	 */
	protected Object get_key() { return this.key; }
	/**
	 * @param key
	 * @return whether there is value in the context w.r.t. the key
	 */
	public boolean has(Object key) {
		if(key == null)
			return false;
		else if(this.ltable.containsKey(key)) 
			return true;
		else if(this.parent != null)
			return this.parent.has(key);
		else
			return false;
	}
	/**
	 * @param key
	 * @return the expression w.r.t. the specified key
	 */
	public SymExpression get(Object key) {
		if(this.ltable.containsKey(key))
			return this.ltable.get(key);
		else {
			if(this.parent != null) {
				return this.parent.get(key);
			}
			else {
				return null;
			}
		}
	}
	/**
	 * @param source
	 * @return the result of the call-expression or itself
	 * @throws Exception
	 */
	public SymExpression invocate(SymCallExpression source) throws Exception {
		for(SymInvocate invocate : this.invocate_set) {
			SymExpression result = invocate.invocate(source);
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
	protected SymContext get_child(Object key) {
		return new SymContext(this, key);
	}
	/**
	 * remove all the local state in the context
	 */
	public void clear() {
		this.ltable.clear();
	}
	/**
	 * add an invocate interface to compute call-expr
	 * @param invocate
	 * @throws Exception
	 */
	public void add(SymInvocate invocate) throws Exception {
		if(invocate == null)
			throw new IllegalArgumentException("invalid invocate");
		else 
			this.invocate_set.add(invocate);
	}
	/**
	 * set the key-value in local-state-table
	 * @param key
	 * @param value
	 * @throws Exception
	 */
	public void put(Object key, SymExpression value) throws Exception {
		if(key == null)
			throw new IllegalArgumentException("Invalid key");
		else if(value == null)
			throw new IllegalArgumentException("invalid value");
		else {
			this.ltable.put(key, value);
		}
	}
	
}
