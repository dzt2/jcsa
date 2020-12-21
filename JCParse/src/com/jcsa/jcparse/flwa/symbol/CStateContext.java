package com.jcsa.jcparse.flwa.symbol;

import java.util.HashMap;
import java.util.Map;

import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializer;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.sym.SymConstant;
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
	 * @param context_key
	 * @return create the child context under this one as parent w.r.t. the given key
	 * @throws Exception
	 */
	protected CStateContext new_child(Object context_key) throws Exception {
		return new CStateContext(this, context_key);
	}
	
	/* data table access */
	/**
	 * @param key AstExpression | AstInitializer | CirExpression | CirStatement | CirExecution | SymExpression
	 * @return
	 * @throws Exception
	 */
	protected boolean has(Object key) throws Exception {
		if(key == null)
			return false;
		else if(key instanceof AstExpression || key instanceof AstInitializer || key instanceof CirExpression
				|| key instanceof CirStatement || key instanceof CirExecution || key instanceof SymExpression) {
			SymExpression sym_key = SymFactory.sym_expression(key);
			String string_code = sym_key.generate_code(false);
			CStateContext context = this;
			while(context != null) {
				if(context.local_values.containsKey(string_code)) {
					return true;
				}
				else {
					context = context.parent;
				}
			}
			return false;
		}
		else
			return false;
	}
	/**
	 * 
	 * @param key AstExpression | AstInitializer | CirExpression | CirStatement | CirExecution | SymExpression
	 * @return null if no solution corresponds to the symbolic representation that is parsed from the key
	 * @throws Exception
	 */
	protected SymExpression get(Object key) throws Exception {
		if(key == null)
			throw new IllegalArgumentException("Invalid key: null");
		else if(key instanceof AstExpression || key instanceof AstInitializer || key instanceof CirExpression
				|| key instanceof CirStatement || key instanceof CirExecution || key instanceof SymExpression) {
			SymExpression sym_key = SymFactory.sym_expression(key);
			String string_code = sym_key.generate_code(false);
			CStateContext context = this;
			while(context != null) {
				if(context.local_values.containsKey(string_code)) {
					return context.local_values.get(string_code);
				}
				else {
					context = context.parent;
				}
			}
			return null;
		}
		else
			throw new IllegalArgumentException(key.getClass().getSimpleName());
	}
	/**
	 * @param key AstExpression | AstInitializer | CirExpression | CirStatement | CirExecution | SymExpression
	 * @param value used to generate value corresponding to the context
	 * @throws Exception
	 */
	protected void put(Object key, Object value) throws Exception {
		if(key == null)
			throw new IllegalArgumentException("Invalid key: null");
		else if(key instanceof AstExpression || key instanceof AstInitializer || key instanceof CirExpression
				|| key instanceof CirStatement || key instanceof CirExecution || key instanceof SymExpression) {
			SymExpression sym_key = SymFactory.sym_expression(key);
			if(sym_key instanceof SymConstant) { /* ignored list */ }
			else {
				this.local_values.put(sym_key.generate_code(false), 
							SymFactory.sym_expression(value));
			}
		}
		else
			throw new IllegalArgumentException(key.getClass().getSimpleName());
	}
	
}
