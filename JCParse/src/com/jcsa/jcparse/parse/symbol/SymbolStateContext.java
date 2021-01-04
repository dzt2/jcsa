package com.jcsa.jcparse.parse.symbol;

import java.util.HashMap;
import java.util.Map;

import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializer;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * It records the value holds by expressions evaluated during and by the statement
 * being executed currently.
 * 
 * @author yukimula
 *
 */
public class SymbolStateContext {
	
	/* definitions */
	/** the parent context or null **/
	private SymbolStateContext parent;
	/** the key for pushing and pop the context **/
	private Object context_key;
	/** mapping from string code of expressions to their values **/
	protected Map<String, SymbolExpression> local_values;
	
	/* constructor */
	/**
	 * create a root context scope 
	 * @param context_key
	 */
	protected SymbolStateContext(Object context_key) {
		this.parent = null;
		this.context_key = context_key;
		this.local_values = new HashMap<String, SymbolExpression>();
	}
	/**
	 * create a child context under the parent
	 * @param parent
	 * @param context_key
	 * @throws IllegalArgumentException
	 */
	private SymbolStateContext(SymbolStateContext parent, 
			Object context_key) throws IllegalArgumentException {
		if(parent == null)
			throw new IllegalArgumentException("Invalid parent: null");
		else {
			this.parent = parent;
			this.context_key = context_key;
			this.local_values = new HashMap<String, SymbolExpression>();
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
	public SymbolStateContext get_parent() { return this.parent; }
	/**
	 * @return the key of the context used for push and pop
	 */
	public Object get_context_key() { return this.context_key; }
	/**
	 * @param context_key
	 * @return create the child context under this one as parent w.r.t. the given key
	 * @throws Exception
	 */
	protected SymbolStateContext new_child(Object context_key) throws Exception {
		return new SymbolStateContext(this, context_key);
	}
	
	/* data table access */
	/**
	 * @param key AstExpression | AstInitializer | CirExpression | CirStatement | CirExecution | SymbolExpression
	 * @return
	 * @throws Exception
	 */
	protected boolean has(Object key) throws Exception {
		if(key == null)
			return false;
		else if(key instanceof AstExpression || key instanceof AstInitializer || key instanceof CirExpression
				|| key instanceof CirStatement || key instanceof CirExecution || key instanceof SymbolExpression) {
			SymbolExpression sym_key = SymbolFactory.sym_expression(key);
			String string_code = sym_key.generate_code(false);
			SymbolStateContext context = this;
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
	 * @param key AstExpression | AstInitializer | CirExpression | CirStatement | CirExecution | SymbolExpression
	 * @return null if no solution corresponds to the symbolic representation that is parsed from the key
	 * @throws Exception
	 */
	protected SymbolExpression get(Object key) throws Exception {
		if(key == null)
			throw new IllegalArgumentException("Invalid key: null");
		else if(key instanceof AstExpression || key instanceof AstInitializer || key instanceof CirExpression
				|| key instanceof CirStatement || key instanceof CirExecution || key instanceof SymbolExpression) {
			SymbolExpression sym_key = SymbolFactory.sym_expression(key);
			String string_code = sym_key.generate_code(false);
			SymbolStateContext context = this;
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
	 * @param key AstExpression | AstInitializer | CirExpression | CirStatement | CirExecution | SymbolExpression
	 * @param value used to generate value corresponding to the context
	 * @throws Exception
	 */
	protected void put(Object key, Object value) throws Exception {
		if(key == null)
			throw new IllegalArgumentException("Invalid key: null");
		else if(key instanceof AstExpression || key instanceof AstInitializer || key instanceof CirExpression
				|| key instanceof CirStatement || key instanceof CirExecution || key instanceof SymbolExpression) {
			SymbolExpression sym_key = SymbolFactory.sym_expression(key);
			if(sym_key instanceof SymbolConstant) { /* ignored list */ }
			else {
				this.local_values.put(sym_key.generate_code(false), 
							SymbolFactory.sym_expression(value));
			}
		}
		else
			throw new IllegalArgumentException(key.getClass().getSimpleName());
	}
	
	
}
