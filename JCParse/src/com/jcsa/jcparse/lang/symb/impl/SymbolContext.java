package com.jcsa.jcparse.lang.symb.impl;

import java.util.Map;

import com.jcsa.jcparse.lang.symb.SymbolExpression;

/**
 * 	It provides the value-read-write operations to preserve states used in
 * 	symbolic evaluation.
 * 	@author yukimula
 *
 */
public class SymbolContext {
	
	/* definitions */
	/** the scope at the top of the context-stack **/
	private	SymbolScope top_scope;
	/**
	 * It creates an empty context with only one single root scope
	 */
	public SymbolContext() {
		this.top_scope = new SymbolScope(this);
	}
	
	/* getters */
	/**
	 * @return whether the top scope is root-context
	 */
	public boolean is_root_scope() { return this.top_scope.is_root(); }
	/**
	 * @return the identifier of the top-scope in the context
	 */
	public Object top_identifier() { return this.top_scope.get_identifier(); }
	/**
	 * @param source
	 * @return whether there exists value w.r.t. the source
	 */
	public boolean has_value(SymbolExpression source) {
		return this.top_scope.has_value(source);
	}
	/**
	 * @param source
	 * @return the value w.r.t. the source expression
	 */
	public SymbolExpression get_value(SymbolExpression source) {
		return this.top_scope.get_value(source);
	}
	/**
	 * @return the set of symbolic expressions as keys to derive the values
	 */
	public Iterable<SymbolExpression> get_keys() { return this.top_scope.get_keys(); }
	/**
	 * @return the mapping from source expression to the target values
	 */
	public Map<SymbolExpression, SymbolExpression> get_kvalues() { return this.top_scope.get_kvalues(); }
	
	/* setters */
	/**
	 * It puts the source-target value-pair to the maps
	 * @param source
	 * @param target
	 * @throws Exception
	 */
	public void put_value(SymbolExpression source, SymbolExpression target) throws Exception {
		this.top_scope.put_value(source, target);
	}
	/**
	 * It clears all the key-values pairs and remain only the root scope
	 */
	public void clear() {
		while(!this.top_scope.is_root()) {
			this.top_scope = this.top_scope.get_parent();
		}
		this.top_scope.clear_values();
	}
	/**
	 * It creates a new scope under the top-scope currently and updates context.
	 * @param identifier
	 * @throws Exception
	 */
	public void push(Object identifier) throws Exception { this.top_scope = this.top_scope.new_child(identifier); }
	/**
	 * It removes the scope at top using the given identifier
	 * @param identifier
	 * @throws Exception
	 */
	public void pop(Object identifier) throws Exception {
		if(this.top_scope.is_root()) {
			throw new IllegalArgumentException("Invalid access: root-scope is reached");
		}
		else if(this.top_scope.get_identifier() == identifier) {
			this.top_scope = this.top_scope.get_parent();
		}
		else {
			throw new IllegalArgumentException(identifier + " != " + this.top_scope.get_identifier());
		}
	}
	/**
	 * It removes the scope at top by forcely
	 * @throws Exception
	 */
	public void pop() {
		if(this.top_scope.is_root()) {
			throw new IllegalArgumentException("Invalid access: root-scope is reached");
		}
		else {
			this.top_scope = this.top_scope.get_parent();
		}
	}
	
}
