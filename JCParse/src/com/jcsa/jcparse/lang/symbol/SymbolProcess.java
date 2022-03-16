package com.jcsa.jcparse.lang.symbol;

import java.util.Map;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.test.state.CStateNode;
import com.jcsa.jcparse.test.state.CStateUnit;

/**
 * It manages the data context in dynamic execution point.
 * 
 * @author yukimula
 *
 */
public class SymbolProcess {
	
	private SymbolScope curr_scope;
	public SymbolProcess() {
		this.curr_scope = new SymbolScope();
	}
	
	/* getters */
	/**
	 * @return the top scope currently
	 */
	public SymbolScope get_top_scope() { return this.curr_scope; }
	/**
	 * @param key
	 * @return create a child scope and return it
	 */
	public SymbolScope push_scope(Object key) {
		this.curr_scope = this.curr_scope.new_child(key);
		return this.curr_scope;
	}
	/**
	 * @param key
	 * @return remove current and pop to parent (return the child scope)
	 * @throws Exception
	 */
	public SymbolScope pop_scope(Object key) throws Exception {
		if(this.curr_scope.is_root_scope()) {
			throw new IllegalArgumentException("Empty stack");
		}
		else if(this.curr_scope.get_identifier() != key) {
			throw new IllegalArgumentException("Unmatched: " + key);
		}
		else {
			SymbolScope scope = this.curr_scope;
			this.curr_scope = this.curr_scope.get_parent_scope();
			return scope;
		}
	}
	/**
	 * @param source
	 * @return whether the source contains any value
	 */
	public boolean has_value(SymbolExpression source) {
		return this.curr_scope.has_value(source);
	}
	/**
	 * @param source
	 * @return the value contained by the source  or null
	 */
	public SymbolExpression get_value(SymbolExpression source) {
		return this.curr_scope.get_value(source);
	}
	/**
	 * @param source
	 * @param target
	 * @throws Exception
	 */
	public void set_value(SymbolExpression source, SymbolExpression target) throws Exception {
		this.curr_scope.put_value(source, target);
	}
	/**
	 * It derives the names of the input-output values
	 * @param table
	 * @throws Exception
	 */
	public void derive_maps(Map<SymbolExpression, SymbolExpression> table) throws Exception {
		
	}
	/**
	 * It accumulates the previous state to this process
	 * @param state_node
	 * @throws Exception
	 */
	public void accumulate(CStateNode state_node) throws Exception {
		if(state_node == null) {
			throw new IllegalArgumentException("Invalid state_node: null");
		}
		else {
			CirExecution execution = state_node.get_execution();
			SymbolExpression exec_key = SymbolFactory.sym_expression(execution);
			if(!this.curr_scope.has_value(exec_key)) {
				this.curr_scope.put_value(exec_key, SymbolFactory.sym_constant(Integer.valueOf(0)));
			}
			SymbolConstant exec_num = (SymbolConstant) this.get_value(exec_key);
			this.set_value(exec_key, SymbolFactory.sym_constant(Integer.valueOf(exec_num.get_int() + 1)));
			
			for(CStateUnit unit : state_node.get_units()) {
				if(unit.get_expression() != null && unit.has_value()) {
					SymbolExpression source = SymbolFactory.sym_expression(unit.get_expression());
					SymbolExpression target = SymbolFactory.sym_expression(unit.get_value());
					this.set_value(source, target);
				}
			}
		}
	}
	
}
