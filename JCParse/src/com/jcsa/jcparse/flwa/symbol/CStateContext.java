package com.jcsa.jcparse.flwa.symbol;

import java.util.HashMap;
import java.util.Map;

import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;


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
	protected Map<String, SymExpression> local_values;
	
	/* constructor */
	/**
	 * create a root context scope 
	 * @param context_key
	 */
	protected CStateContext(Object context_key) {
		this.parent = null;
		this.context_key = context_key;
		this.local_values = new HashMap<String, SymExpression>();
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
			this.local_values = new HashMap<String, SymExpression>();
		}
	}
	
	/* key-value methods */
	/**
	 * @param key {AstExpression|CirExpression|SymExpression|
	 * 			   CirStatement|others are not allowed.}
	 * @return 
	 * @throws Exception
	 */
	public static String get_string_key(Object key) throws Exception {
		if(key == null)
			throw new IllegalArgumentException("Invalid key: null");
		else if(key instanceof AstExpression)
			return ((AstExpression) key).generate_code();
		else if(key instanceof CirExpression)
			return SymFactory.parse(key).generate_code();
		else if(key instanceof SymExpression)
			return ((SymExpression) key).generate_code();
		else if(key instanceof CirStatement)
			return SymFactory.sym_statement((CirStatement) key).generate_code();
		else if(key instanceof CirExecution)
			return SymFactory.sym_statement(((CirExecution) key).get_statement()).generate_code();
		else
			throw new IllegalArgumentException("Unsupported: " + key.getClass().getSimpleName());
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
	 * @param key {AstExpression|CirExpression|SymExpression|
	 * 			   CirStatement|others are not allowed.}
	 * @return true if the key corresponds to some value in 
	 * 		   the context or its parent
	 * @throws Exception
	 */
	public boolean has_value(Object key) throws Exception {
		String string_key = get_string_key(key);
		CStateContext context = this;
		while(context != null) {
			if(context.local_values.containsKey(string_key)) {
				return true;
			}
			else {
				context = context.parent;
			}
		}
		return false;
	}
	/**
	 * @param key {AstNode|CirNode|SymNode|Any.toString()}
	 * @return the value w.r.t. the key in the context
	 * @throws Exception
	 */
	public SymExpression get_value(Object key) throws Exception {
		String string_key = get_string_key(key);
		CStateContext context = this;
		while(context != null) {
			if(context.local_values.containsKey(string_key)) {
				return context.local_values.get(string_key);
			}
			else {
				context = context.parent;
			}
		}
		return null;
	}
	/**
	 * set the value w.r.t. the key
	 * @param key {AstExpression|CirExpression|SymExpression|
	 * 			   CirStatement|others are not allowed.}
	 * @param value {Boolean|Character|Short|Integer|Long|Float
	 * 				|Double|AstExpression|CirExpression|
	 * 				|SymExpression|CirStatement|CirExecution
	 * 				|CConstant}
	 * @throws Exception
	 */
	public void put_value(Object key, Object value) throws Exception {
		String string_key = get_string_key(key);
		SymExpression sym_value = SymFactory.parse(value);
		this.local_values.put(string_key, sym_value);
	}
	
	/* setters */
	protected CStateContext new_child(Object context_key) throws Exception {
		return new CStateContext(this, context_key);
	}
	
}
