package com.jcsa.jcparse.lang.symbol;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * It specifies the map from reference to value hold in dynamic execution.
 * 
 * @author yukimula
 *
 */
final class SymbolScope {
	
	/* attributes */
	private SymbolScope parent_scope;
	private Object		scope_identifier;
	private	Map<SymbolExpression, SymbolExpression> state_map;
	protected SymbolScope(Object identifier) throws IllegalArgumentException {
		if(identifier == null) {
			throw new IllegalArgumentException("Invalid identifier: null");
		}
		else {
			this.parent_scope = null;
			this.scope_identifier = identifier;
			this.state_map = new HashMap<SymbolExpression, SymbolExpression>();
		}
	}
	
	/* getters */
	/**
	 * @return whether this scope is the root
	 */
	protected boolean 			is_root_scope() 	{ return this.parent_scope == null; }
	/**
	 * @return the parent scope enclosing this one
	 */
	protected SymbolScope 		get_parent_scope() 	{ return this.parent_scope; }
	/**
	 * @return the identifier to tag this scope uniquely
	 */
	protected Object			get_identifier()	{ return this.scope_identifier; }
	/**
	 * @param source
	 * @return the value hold for the source or null if not defined
	 */
	protected boolean 			has_value(SymbolExpression source) {
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
	protected SymbolExpression	get_value(SymbolExpression source) {
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
	protected SymbolScope		new_child(Object identifier) throws IllegalArgumentException {
		SymbolScope child = new SymbolScope(identifier);
		child.parent_scope = this; return child;
	}
	/**
	 * It updates the source-target pair in state-map
	 * @param source
	 * @param target
	 * @throws Exception
	 */
	protected void				put_value(SymbolExpression source, SymbolExpression target) throws IllegalArgumentException {
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
	/**
	 * It derives the source-target pairs into the input table
	 * @param table
	 * @throws Exception
	 */
	protected void				derive_loc_values(Map<SymbolExpression, SymbolExpression> table) throws IllegalArgumentException {
		if(table == null) {
			throw new IllegalArgumentException("Invalid table: null");
		}
		else {
			for(SymbolExpression source : this.state_map.keySet()) {
				SymbolExpression target = this.state_map.get(source);
				table.put(source, target);
			}
		}
	}
	/**
	 * It derives all the source-target pairs in the scope until its root
	 * @param table
	 * @throws Exception
	 */
	protected void				derive_all_values(Map<SymbolExpression, SymbolExpression> table) throws IllegalArgumentException {
		if(table == null) {
			throw new IllegalArgumentException("Invalid table: null");
		}
		else {
			if(this.parent_scope != null) {
				this.parent_scope.derive_all_values(table);
			}
			this.derive_loc_values(table);
		}
	}
	/**
	 * It derives the set of expressions as sources to derive in local scope
	 * @param keys
	 * @throws Exception
	 */
	protected void				derive_loc_keys(Collection<SymbolExpression> keys) throws IllegalArgumentException {
		if(keys == null) {
			throw new IllegalArgumentException("Invalid keys as null");
		}
		else {
			for(SymbolExpression source : this.state_map.keySet()) {
				keys.add(source);
			}
		}
	}
	/**
	 * It derives the set of expressions as sources to derive in all scopes
	 * @param keys
	 * @throws Exception
	 */
	protected void				derive_all_keys(Collection<SymbolExpression> keys) throws IllegalArgumentException {
		if(keys == null) {
			throw new IllegalArgumentException("Invalid keys as null");
		}
		else {
			if(this.parent_scope != null) {
				this.parent_scope.derive_all_keys(keys);
			}
			this.derive_loc_keys(keys);
		}
	}
	/**
	 * It clears the state-map data
	 */
	protected void				clear() { this.state_map.clear(); }
	
}
