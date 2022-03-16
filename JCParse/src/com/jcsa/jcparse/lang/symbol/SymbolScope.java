package com.jcsa.jcparse.lang.symbol;

import java.util.HashMap;
import java.util.Map;

/**
 * It specifies the map from reference to value hold in dynamic execution.
 * 
 * @author yukimula
 *
 */
public class SymbolScope {
	
	/* attributes */
	private SymbolScope parent_scope;
	private Object		scope_identifier;
	private	Map<SymbolExpression, SymbolExpression> state_map;
	protected SymbolScope() {
		this.parent_scope = null;
		this.scope_identifier = null;
		this.state_map = new HashMap<SymbolExpression, SymbolExpression>();
	}
	
	/* getters */
	/**
	 * @return whether this scope is the root
	 */
	public boolean 			is_root_scope() 	{ return this.parent_scope == null; }
	/**
	 * @return the parent scope enclosing this one
	 */
	public SymbolScope 		get_parent_scope() 	{ return this.parent_scope; }
	/**
	 * @return the identifier to tag this scope uniquely
	 */
	public Object			get_identifier()	{ return this.scope_identifier; }
	/**
	 * @param source
	 * @return the value hold for the source or null if not defined
	 */
	public boolean 			has_value(SymbolExpression source) {
		if(source == null) {
			return false;
		}
		else if(this.state_map.containsKey(source)) {
			return true;
		}
		else if(this.parent_scope != null) {
			return this.parent_scope.has_value(source);
		}
		else {
			return false;
		}
	}
	/**
	 * @param source
	 * @return the value hold for the source or null if not defined
	 */
	public SymbolExpression	get_value(SymbolExpression source) {
		if(source == null) {
			return null;
		}
		else if(this.state_map.containsKey(source)) {
			return this.state_map.get(source);
		}
		else if(this.parent_scope != null) {
			return this.parent_scope.get_value(source);
		}
		else {
			return null;
		}
	}
	
	/* setters */
	/**
	 * It creates a child scope under this one using identifier tag
	 * @param identifier
	 * @return
	 */
	protected SymbolScope	new_child(Object identifier) {
		SymbolScope child = new SymbolScope();
		child.parent_scope = this;
		child.scope_identifier = identifier;
		return child;
	}
	/**
	 * It updates the source-target pair in state-map
	 * @param source
	 * @param target
	 * @throws Exception
	 */
	protected void			put_value(SymbolExpression source, SymbolExpression target) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(target == null) {
			throw new IllegalArgumentException("Invalid target: null");
		}
		else {
			this.state_map.put(source, target);
		}
	}
	
}
