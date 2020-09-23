package com.jcsa.jcparse.test.state;

import java.util.HashMap;
import java.util.Map;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.sym.SymNode;

/**
 * It records the value holds by expressions evaluated during and by the statement
 * being executed currently.
 * @author yukimula
 *
 */
public class CStateContext {
	
	/* definitions */
	/** the parent context or null **/
	private CStateContext parent;
	/** the key for pushing and pop the context **/
	private Object context_key;
	/** mapping from string code of expressions to their values **/
	private Map<String, Object> local_values;
	
	/* constructor */
	/**
	 * create a root context scope 
	 * @param context_key
	 */
	protected CStateContext(Object context_key) {
		this.parent = null;
		this.context_key = context_key;
		this.local_values = new HashMap<String, Object>();
	}
	/**
	 * create a child context under the parent
	 * @param parent
	 * @param context_key
	 * @throws IllegalArgumentException
	 */
	private CStateContext(CStateContext parent, 
			Object context_key) throws IllegalArgumentException {
		if(parent == null)
			throw new IllegalArgumentException("Invalid parent: null");
		else {
			this.parent = parent;
			this.context_key = context_key;
			this.local_values = new HashMap<String, Object>();
		}
	}
	
	/* getters */
	/**
	 * @return whether the context is a root
	 */
	public boolean is_root() { return this.parent == null; }
	/**
	 * @return the parent of the context or null
	 */
	public CStateContext get_parent() { return this.parent; }
	/**
	 * @return the key of the context used for push and pop
	 */
	public Object get_context_key() { return this.context_key; }
	/**
	 * @param key {AstNode|CirNode|SymNode|Any.toString()}
	 * @return the value w.r.t. the key in the context
	 * @throws Exception
	 */
	public Object get_value(Object key) throws Exception {
		String skey;
		if(key instanceof AstNode)
			skey = ((AstNode) key).generate_code();
		else if(key instanceof CirNode)
			skey = ((CirNode) key).generate_code(false);
		else if(key instanceof SymNode)
			skey = ((SymNode) key).generate_code();
		else
			skey = key.toString();
		
		CStateContext context = this;
		while(context != null) {
			if(context.local_values.containsKey(skey)) {
				return context.local_values.get(skey);
			}
			else {
				context = context.parent;
			}
		}
		return null;
	}
	/**
	 * set the value w.r.t. the key
	 * @param key {AstNode|CirNode|SymNode|Any.toString()}
	 * @param value
	 * @throws Exception
	 */
	public void put_value(Object key, Object value) throws Exception {
		String skey;
		if(key instanceof AstNode)
			skey = ((AstNode) key).generate_code();
		else if(key instanceof CirNode)
			skey = ((CirNode) key).generate_code(false);
		else if(key instanceof SymNode)
			skey = ((SymNode) key).generate_code();
		else
			skey = key.toString();
		this.local_values.put(skey, value);
	}
	
	/* setters */
	protected CStateContext new_child(Object context_key) throws Exception {
		return new CStateContext(this, context_key);
	}
}
