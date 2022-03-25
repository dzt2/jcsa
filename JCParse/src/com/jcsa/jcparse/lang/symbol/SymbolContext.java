package com.jcsa.jcparse.lang.symbol;

import java.util.Map;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.test.state.CStateNode;
import com.jcsa.jcparse.test.state.CStateUnit;

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
	
	/**
	 * It accumulates the instrumental path state to the context
	 * @param state_node
	 * @throws Exception
	 */
	public void accumulate(CStateNode state_node) throws Exception {
		if(state_node == null) {
			throw new IllegalArgumentException("Invalid state_node: null");
		}
		else {
			/* 1. set the data-states before executing */
			for(CStateUnit unit : state_node.get_units()) {
				if(unit.has_value() && unit.get_expression() != null) {
					SymbolExpression source = SymbolFactory.sym_expression(unit.get_expression());
					SymbolExpression target = SymbolFactory.sym_expression(unit.get_value());
					this.put_value(source, target);
				}
			}
			
			/* 2. account the prior executed statement */
			if(state_node.get_prev_node() != null) {
				CirExecution execution = state_node.get_prev_node().get_execution();
				SymbolExpression source = SymbolFactory.sym_expression(execution);
				if(!this.has_value(source)) {
					this.put_value(source, SymbolFactory.sym_constant(Integer.valueOf(0)));
				}
				
				SymbolConstant target = (SymbolConstant) this.get_value(source);
				target = SymbolFactory.sym_constant(Integer.valueOf(target.get_int() + 1));
				this.put_value(source, target);
			}
		}
	}
	
}
