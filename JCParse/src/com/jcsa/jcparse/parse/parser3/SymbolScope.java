package com.jcsa.jcparse.parse.parser3;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * 	It defines the local scope of name-value in symbolic evaluation for context.
 * 	
 * 	@author yukimula
 *
 */
final class SymbolScope {
	
	/* definitions */
	/** the parent scope enclosing this scope or null if it is root **/
	private	SymbolScope parent_scope;
	/** the key identifier to push this scope from its parent scope **/
	private Object		identifier;
	/** mapping from symbolic expression to its corresponding value **/
	private Map<SymbolExpression, SymbolExpression> data_table;
	/**
	 * It creates an isolated scope with respect to the identifier
	 * @param identifier
	 * @throws IllegalArgumentException
	 */
	protected SymbolScope(Object identifier) throws IllegalArgumentException {
		if(identifier == null) {
			throw new IllegalArgumentException("Invalid identifier: null");
		}
		else {
			this.parent_scope = null;
			this.identifier = identifier;
			this.data_table = new HashMap<SymbolExpression, SymbolExpression>();
		}
	}
	
	/* getters */
	/**
	 * @return whether this scope is a root scope without parent
	 */
	protected boolean is_root() { return this.parent_scope == null; }
	/**
	 * @return the parent scope enclosing this scope or null if it is root
	 */
	protected SymbolScope get_parent() { return this.parent_scope; }
	/**
	 * @return the key identifier to push this scope from its parent scope
	 */
	protected Object get_identifier() { return this.identifier; }
	/**
	 * @param identifier
	 * @return it creates a child-scope under this one and returns it
	 * @throws Exception
	 */
	protected SymbolScope new_child(Object identifier) throws Exception {
		if(identifier == null) {
			throw new IllegalArgumentException("Invalid identifier: null");
		}
		else {
			SymbolScope child = new SymbolScope(identifier);
			child.parent_scope = this; 
			return child;
		}
	}
	
	/* values */
	/**
	 * @param source
	 * @return whether the source refers to any value in the scope or its parent
	 */
	protected boolean has_value(SymbolExpression source) {
		if(source == null) {
			return false;
		}
		else if(this.data_table.containsKey(source)) {
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
	 * @return 	the symbolic value derived from the source in the local scope
	 * 			or the parent scope, or null if the source is undefined
	 */
	protected SymbolExpression get_value(SymbolExpression source) {
		if(source == null) {
			return null;
		}
		else if(this.data_table.containsKey(source)) {
			return this.data_table.get(source);
		}
		else if(this.parent_scope != null) {
			return this.parent_scope.get_value(source);
		}
		else {
			return null;
		}
	}
	/**
	 * It derives the set of source expressions from this scope and its parents
	 * @param sources
	 * @throws IllegalArgumentException
	 */
	private void derive_keys(Collection<SymbolExpression> sources) throws IllegalArgumentException {
		if(sources == null) {
			throw new IllegalArgumentException("Invalid sources as null");
		}
		else {
			if(this.parent_scope != null) {
				this.parent_scope.derive_keys(sources);
			}
			for(SymbolExpression source : this.data_table.keySet()) {
				sources.add(source);
			}
		}
	}
	/**
	 * @return the set of source expressions derived from this scope and parents
	 */
	protected Iterable<SymbolExpression> get_keys() {
		Set<SymbolExpression> sources = new HashSet<SymbolExpression>();
		this.derive_keys(sources); return sources;
	}
	/**
	 * It puts the key-value pairs in the scope to the given tables
	 * @param table
	 * @throws IllegalArgumentException
	 */
	private	void put_kvalues(Map<SymbolExpression, SymbolExpression> table) throws IllegalArgumentException {
		if(table == null) {
			throw new IllegalArgumentException("Invalid table: null");
		}
		else {
			if(this.parent_scope != null) {
				this.parent_scope.put_kvalues(table);
			}
			for(SymbolExpression source : this.data_table.keySet()) {
				SymbolExpression target = this.data_table.get(source);
				table.put(source, target);
			}
		}
	}
	/**
	 * @return the mapping from source expression to target value
	 * @throws Exception
	 */
	protected Map<SymbolExpression, SymbolExpression> get_kvalues() {
		Map<SymbolExpression, SymbolExpression> table = new
				HashMap<SymbolExpression, SymbolExpression>();
		this.put_kvalues(table); return table;
	}
	/**
	 * It puts the source-target value-pair to the maps
	 * @param source
	 * @param target
	 * @throws Exception
	 */
	protected void put_value(SymbolExpression source, SymbolExpression target) throws IllegalArgumentException {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(target == null) {
			throw new IllegalArgumentException("Invalid target: null");
		}
		else {
			this.data_table.put(source, target);
		}
	}
	/**
	 * It clears the local table values
	 */
	protected void clear_values() { this.data_table.clear(); }
	
}
