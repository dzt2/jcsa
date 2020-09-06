package com.jcsa.jcparse.lang.symb;

/**
 * It is used to evaluate the value of symbolic expression in a given context.
 * 
 * @author yukimula
 *
 */
public class SymEvaluator {
	
	/* definition */
	private SymContext context;
	public SymEvaluator() {
		this.context = new SymContext();
	}
	
	/* context operations */
	/**
	 * @return get the current context for evaluation
	 */
	public SymContext get_context() { return this.context; }
	/**
	 * create a child context when calling a new function
	 * @param key
	 */
	public void push_context(Object key) {
		this.context = this.context.get_child(key);
	}
	/**
	 * recover to the parent context when return to caller
	 * @param key
	 * @throws Exception
	 */
	public void pop_context(Object key) throws Exception {
		if(this.context.get_parent() == null)
			throw new IllegalArgumentException("Root reached");
		else if(this.context.get_key() != key)
			throw new IllegalArgumentException("Not matched");
		else {
			this.context = this.context.get_parent();
		}
	}
	
	/* evaluation methods */
	private SymExpression get_solution(SymExpression source) throws Exception {
		if(this.context.has(source)) {
			return this.context.get(source);
		}
		else if(this.context.has(source.get_source())) {
			return this.context.get(source.get_source());
		}
		else if(this.context.has(source.generate_code())) {
			return this.context.get(source.generate_code());
		}
		else {
			return null;
		}
	}
	/**
	 * evaluate the result of the symbolic expression
	 * @param source
	 * @return
	 * @throws Exception
	 */
	public SymExpression evaluate(SymExpression source) throws Exception {
		if(source == null)
			throw new IllegalArgumentException("Invalid source: null");
		else {
			SymExpression result = this.get_solution(source);
			if(result != null) {
				return result;
			}
			else {
				// TODO implement the evaluation algorithms
				return null;
			}
		}
	}
	
	
	
	
}
