package com.jcsa.jcparse.lang.symbol;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.jcsa.jcparse.test.state.CStateNode;
import com.jcsa.jcparse.test.state.CStateUnit;

/**
 * It manages the data context in dynamic execution point.
 * 
 * @author yukimula
 *
 */
public class SymbolProcess {
	
	/* definitions */
	/** the scope as stack-top **/
	private SymbolScope top_scope;
	/** private constructor for factory mode **/
	public SymbolProcess() { this.top_scope = new SymbolScope(this); }
	
	/* stack getters */
	/**
	 * @return the length of the scopes in the stack
	 */
	public int length() {
		int size = 0;
		SymbolScope scope = this.top_scope;
		while(scope != null) {
			scope = scope.get_parent_scope();
			size++;
		}
		return size;
	}
	/**
	 * @param source
	 * @return whether there exists 
	 */
	public boolean	has_value(SymbolExpression source) {
		if(source == null) {
			return false;
		}
		else {
			return this.top_scope.has_value(source);
		}
	}
	/**
	 * @param source
	 * @return the value to which the source preserves or null if undefined
	 */
	public SymbolExpression	get_value(SymbolExpression source) {
		if(source == null) {
			return null;
		}
		else {
			return this.top_scope.get_value(source);
		}
	}
	/**
	 * It updates the source-target value-pair to the top-scope
	 * @param source
	 * @param target
	 * @throws Exception
	 */
	public void	set_value(SymbolExpression source, SymbolExpression target) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(target == null) {
			throw new IllegalArgumentException("Invalid target: null");
		}
		else {
			this.top_scope.put_value(source, target);
		}
	}
	/**
	 * @return the set of symbolic expressions enclosed by the scopes in stack
	 * @throws Exception
	 */
	public Iterable<SymbolExpression> get_keys() throws Exception {
		Set<SymbolExpression> keys = new HashSet<SymbolExpression>();
		this.top_scope.derive_all_keys(keys); return keys;
	}
	/**
	 * It derives the source-target value-pairs in the entire scope-stack to the table
	 * @param table
	 * @throws Exception
	 */
	public void	derive_value_table(Map<SymbolExpression, SymbolExpression> table) throws Exception {
		if(table == null) {
			throw new IllegalArgumentException("Invalid table: null");
		}
		else {
			table.clear();
			this.top_scope.derive_all_values(table);
			return;
		}
	}
	/**
	 * @return the maps from source to target in scope-context
	 */
	public Map<SymbolExpression, SymbolExpression> get_value_table() {
		Map<SymbolExpression, SymbolExpression> table = 
				new HashMap<SymbolExpression, SymbolExpression>();
		this.top_scope.derive_all_values(table); return table;
	}
	
	/* stack setters */
	/**
	 * It pushes a new scope under the top using given identifier
	 * @param identifier
	 * @throws Exception
	 */
	public void push(Object identifier) throws Exception {
		this.top_scope = top_scope.new_child(identifier);
	}
	/**
	 * @return the identifier of top scope or null if the stack is empty
	 */
	public Object get_top_identifier() {
		return this.top_scope.get_identifier();
	}
	/**
	 * It pops the top scope in the stack by matching the input identifier
	 * @param identifier
	 * @throws Exception
	 */
	public void pop(Object identifier) throws Exception {
		if(this.top_scope.is_root_scope()) {
			throw new IllegalArgumentException("Invalid access: top reached");
		}
		else if(this.top_scope.get_identifier() == identifier) {
			this.top_scope = this.top_scope.get_parent_scope();
		}
		else {
			throw new IllegalArgumentException("Unmatched between " +
					identifier + "\t<--> " + this.top_scope.get_identifier());
		}
	}
	/**
	 * It pops the top scope any way
	 */
	public void pop() throws Exception {
		if(this.top_scope.is_root_scope()) {
			throw new IllegalArgumentException("Invalid access: top reached");
		}
		else {
			this.top_scope = this.top_scope.get_parent_scope();
		}
	}
	/**
	 * It accumulates the state in dynamic execution node.
	 * @param state_node
	 * @throws Exception
	 */
	public void accumulate(CStateNode state_node) throws Exception {
		if(state_node == null) {
			throw new IllegalArgumentException("Invalid state_node: null");
		}
		else {
			/* 1. accumulate the execution counter */
			SymbolExpression execution = SymbolFactory.
					sym_expression(state_node.get_execution());
			if(!this.top_scope.has_value(execution)) {
				this.top_scope.put_value(execution, SymbolFactory.sym_constant(Integer.valueOf(0)));
			}
			SymbolConstant counter = (SymbolConstant) this.top_scope.get_value(execution);
			counter = SymbolFactory.sym_constant(counter.get_int() + 1);
			this.top_scope.put_value(execution, counter);
			
			/* 2. accumulate the current expression units */
			for(CStateUnit unit : state_node.get_units()) {
				if(unit.get_expression() != null && unit.has_value()) {
					SymbolExpression source = SymbolFactory.sym_expression(unit.get_expression());
					SymbolExpression target = SymbolFactory.sym_expression(unit.get_value());
					this.top_scope.put_value(source, target);
				}
			}
		}
	}
	
}
