package com.jcsa.jcparse.lang.sym;

/**
 * It preserves all the call-stack in contexts for evaluation
 * @author yukimula
 *
 */
public class SymContexts {
	
	private SymContext context;
	public SymContexts() {
		this.context = new SymContext();
	}
	
	/* stack operations */
	public SymContext peek() { return context; }
	public SymContext get_root() {
		SymContext context = this.context;
		while(context.get_parent() != null) {
			context = context.get_parent();
		}
		return context;
	}
	public SymContext push(Object key) throws Exception {
		if(key == null)
			throw new IllegalArgumentException("Invalid key: null");
		else {
			this.context = this.context.get_child(key);
			return this.context;
		}
	}
	public SymContext pop(Object key) throws Exception {
		if(this.context.get_parent() == null)
			throw new IllegalArgumentException("Out-of-bound in stack");
		else if(key != this.context.get_key()) 
			throw new IllegalArgumentException("Unable to match " + key);
		else {
			SymContext old_context = this.context;
			this.context = this.context.get_parent();
			return old_context;
		}
	}
	
	/* getters */
	public boolean has(Object key) {
		return this.context.has(key);
	}
	public SymExpression get(Object key) {
		return this.context.get(key);
	}
	public void put(Object key, SymExpression value) throws Exception {
		this.context.put(key, value);;
	}
	public SymExpression invocate(SymCallExpression source) throws Exception {
		return this.context.invocate(source);
	}
	
	
}
