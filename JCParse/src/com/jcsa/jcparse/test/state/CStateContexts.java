package com.jcsa.jcparse.test.state;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.impl.CirLocalizer;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirBegStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirEndStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.unit.CirFunctionDefinition;
import com.jcsa.jcparse.lang.sym.SymCallExpression;
import com.jcsa.jcparse.lang.sym.SymConstant;
import com.jcsa.jcparse.lang.sym.SymEvaluator;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;
import com.jcsa.jcparse.lang.sym.SymInvocate;

public class CStateContexts {
	
	/* definition */
	private CStateContext context;
	private Set<SymInvocate> invocate_set;
	public CStateContexts() {
		this.context = new CStateContext(null);
		this.invocate_set = new HashSet<SymInvocate>();
	}
	
	/* context operations */
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
	 * @return get the backup of the current contexts
	 * @throws Exception
	 */
	public CStateContexts copy() throws Exception {
		CStateContexts contexts = new CStateContexts();
		
		/* set invocation list and obtain the stack of contexts */
		Stack<CStateContext> stack = new Stack<CStateContext>();
		CStateContext old_context = this.context, new_context;
		while(old_context != null) { 
			stack.push(old_context); 
			old_context = old_context.get_parent();
		}
		contexts.invocate_set.addAll(this.invocate_set);
		
		boolean first = true;
		while(!stack.isEmpty()) {
			/* generate new-context for copy */
			old_context = stack.pop();
			if(first) {
				first = false;
			}
			else {
				contexts.push(old_context.get_context_key());
			}
			new_context = contexts.context;
			
			/* clone the key-value pairs from old to new */
			for(String key : old_context.local_values.keySet()) {
				SymExpression value = old_context.local_values.get(key);
				new_context.local_values.put(key, value);
			}
		}
		
		return contexts;
	}
	
	/* data getters */
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
	/**
	 * @param source
	 * @return evaluate the input symbolic expression using the current context
	 * @throws Exception
	 */
	public SymExpression evaluate(SymExpression source) throws Exception {
		return SymEvaluator.evaluate_on(source, this);
	}
	
	/* state update function */
	/**
	 * accumulate the state on flow from node.prev_node --> node,
	 * including assigning value of right-value in node.prev_node
	 * and obtaining the input value of the node.
	 * @param node for flow[node.prev_node --> node]
	 * @throws Exception
	 */
	public void accumulate(CStateNode node) throws Exception {
		if(node == null)
			throw new IllegalArgumentException("Invalid node");
		else {
			/* 1. update the left-reference in prev-assignment */
			if(node.get_prev_node() != null) {
				CirStatement prev_statement = node.get_prev_node().get_statement();
				if(prev_statement instanceof CirAssignStatement) {
					CirExpression lvalue = ((CirAssignStatement) prev_statement).get_lvalue();
					CirExpression rvalue = ((CirAssignStatement) prev_statement).get_rvalue();
					if(node.get_prev_node().has_unit(rvalue)) {
						this.context.put_value(lvalue, node.get_prev_node().get_unit(rvalue).get_value());
					}
				}
			}
			//System.out.println("\t\t==> STEP-1");
			
			/* 2. update the scope at the border of function */
			CirStatement statement = node.get_statement();
			CirFunctionDefinition def = statement.function_of();
			if(statement instanceof CirBegStatement) {
				this.push(def);
			}
			else if(statement instanceof CirEndStatement) {
				this.pop(def);
			}
			//System.out.println("\t\t==> STEP-2");
			
			/* 3. update the local state in current scope */
			for(CStateUnit unit : node.get_units()) {
				SymExpression source = unit.get_value();
				SymExpression target = this.evaluate(source);
				this.put(unit.get_expression(), target);
			}
			//System.out.println("\t\t==> STEP-3");
			
			/* 4. accumulate the statement as being executed */
			SymExpression sexpr = SymFactory.sym_statement(statement);
			SymExpression value = this.context.get_value(sexpr);
			int counter = 0;
			if(value != null) {
				counter = ((SymConstant) value).get_int();
			}
			counter++;
			this.context.put_value(sexpr, Integer.valueOf(counter));
			//System.out.println("\t\t==> STEP-4");
		}
	}
	/**
	 * @param execution
	 * @return the state node after executing the statement as given
	 * @throws Exception
	 */
	public CStateNode generate(CirExecution execution) throws Exception {
		if(execution == null)
			throw new IllegalArgumentException("Invalid execution: null");
		else {
			/* 1. declarations */
			CStateNode node = new CStateNode(execution);
			Set<CirExpression> expressions = CirLocalizer.
					expressions_in(execution.get_statement());
			
			/* 2. perform symbolic evaluation to create node */
			for(CirExpression expression : expressions) {
				SymExpression sym_value = SymFactory.parse(expression);
				node.set_unit(expression, this.evaluate(sym_value));
			}
			
			/* 3. return the state node */	return node;
		}
	}
	
}
