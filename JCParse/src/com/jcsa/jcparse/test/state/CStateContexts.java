package com.jcsa.jcparse.test.state;

import java.util.Set;

import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirBegStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirEndStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.unit.CirFunctionDefinition;
import com.jcsa.jcparse.lang.sym.SymCallExpression;
import com.jcsa.jcparse.lang.sym.SymConstant;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;
import com.jcsa.jcparse.lang.sym.SymInvocate;

public class CStateContexts {
	
	/* definition */
	private CStateContext context;
	private Set<SymInvocate> invocate_set;
	public CStateContexts() {
		this.context = new CStateContext(null);
	}
	
	/* getters */
	/**
	 * @return the root context in the scope
	 */
	public CStateContext get_root_context() {
		CStateContext context = this.context;
		while(!context.is_root()) {
			context = context.get_parent();
		}
		return context;
	}
	/**
	 * @return the current context being evaluated
	 */
	public CStateContext get_context() { return this.context; }
	/**
	 * push the new child context w.r.t. the key
	 * @param context_key
	 * @throws Exception
	 */
	public void push(Object context_key) throws Exception {
		this.context = this.context.new_child(context_key);
	}
	/**
	 * remove the current context from scope using the key as given
	 * @param context_key
	 * @throws Exception
	 */
	public void pop(Object context_key) throws Exception {
		if(this.context.is_root())
			throw new IllegalArgumentException("Empty stack");
		else if(this.context.get_context_key() == context_key)
			this.context = this.context.get_parent();
		else
			throw new IllegalArgumentException("Unable to match");
	}
	/**
	 * @param key
	 * @return whether there is value w.r.t. the key in current context 
	 * @throws Exception
	 */
	public boolean has(Object key) throws Exception {
		return this.context.has_value(key);
	}
	/**
	 * @param key {AstNode|CirNode|SymNode}
	 * @return the value w.r.t. the key in the context
	 * @throws Exception
	 */
	public SymExpression get(Object key) throws Exception {
		return this.context.get_value(key);
	}
	/**
	 * save the value w.r.t. the key in current context
	 * @param key {AstNode|CirNode|SymNode}
	 * @param value
	 * @throws Exception
	 */
	public void put(Object key, Object value) throws Exception {
		this.context.put_value(key, value);
	}
	/**
	 * add the invocation machine to the contexts.
	 * @param invocate
	 * @throws Exception
	 */
	public void add(SymInvocate invocate) throws Exception {
		if(invocate != null)
			this.invocate_set.add(invocate);
		else
			throw new IllegalArgumentException("Invalid invocate: null");
	}
	/**
	 * @param source
	 * @return symbolic result computed from the source using invocators
	 * 		   or the source itself if it is impossible to interpret.
	 * @throws Exception
	 */
	public SymExpression invocate(SymCallExpression source) throws Exception {
		SymExpression result;
		for(SymInvocate invocate : this.invocate_set) {
			result = invocate.invocate(source);
			if(result != null) return result;
		}
		return source;
	}
	
	/* state update function */
	/**
	 * update the state contexts when the statement in the node is executed
	 * @param node
	 * @throws Exception
	 */
	public void update(CStateNode node) throws Exception {
		if(node == null)
			throw new IllegalArgumentException("Invalid node: null");
		else {
			/* update the state transition context */
			CirStatement statement = node.get_statement();
			CirFunctionDefinition fun = statement.function_of();
			if(statement instanceof CirBegStatement) {
				this.push(fun);
			}
			else if(statement instanceof CirEndStatement) {
				this.pop(fun);
			}
			
			/* record the counter of the execution */
			SymExpression key = SymFactory.parse(node.get_execution());
			if(!this.has(key)) {
				this.get_root_context().put_value(key, Integer.valueOf(1));
			}
			Integer counter = ((SymConstant) this.get(key)).get_int();
			counter = Integer.valueOf(counter.intValue() + 1);
			this.get_root_context().put_value(key, counter);
			
			/* record the values hold by expressions */
			for(CStateUnit unit : node.get_units()) {
				CirExpression expression = unit.get_expression();
				SymExpression sym_expr = SymFactory.parse(expression);
				this.put(sym_expr, unit.get_value());
			}
		}
	}
	
}
