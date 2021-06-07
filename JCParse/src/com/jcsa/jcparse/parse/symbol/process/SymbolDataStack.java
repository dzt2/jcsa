package com.jcsa.jcparse.parse.symbol.process;

import com.jcsa.jcparse.lang.symbol.SymbolExpression;

public class SymbolDataStack {
	
	/* definitions */
	/** the process to which the stack serves **/
	private SymbolProcess process;
	/** the top block of the data stack in the local space **/
	private SymbolDataBlock top;
	/**
	 * create an empty data stack in the process memory
	 * @param process
	 * @throws Exception
	 */
	protected SymbolDataStack(SymbolProcess process) throws Exception {
		if(process == null)
			throw new IllegalArgumentException("Invalid process: null");
		else {
			this.process = process;
			this.top = new SymbolDataBlock(this);
		}
	}
	
	/* getters */
	/**
	 * @return the process to which the stack serves
	 */
	public SymbolProcess get_process() { return this.process; }
	/**
	 * @return the top block of the data stack in the local space
	 */
	public SymbolDataBlock peek_block() { return this.top; }
	/**
	 * @param key
	 * @return create a new block under the top block and update local top
	 * @throws Exception
	 */
	public SymbolDataBlock push_block(Object key) throws Exception {
		this.top = this.top.new_child(key);
		return this.top;
	}
	/**
	 * @param key
	 * @return pop the old block and update local top
	 * @throws Exception
	 */
	public SymbolDataBlock pop_block(Object key) throws Exception {
		if(this.top.is_root()) {
			throw new IllegalArgumentException("Top block cannot be removed");
		}
		else if(this.top.get_block_key() == key) {
			SymbolDataBlock ret_block = this.top;
			this.top = this.top.get_parent();
			return ret_block;
		}
		else {
			throw new IllegalArgumentException("Unmatcked key: " + key);
		}
	}
	/**
	 * @param key
	 * @param value
	 * @return save the value to the reference of key at top block
	 * @throws Exception
	 */
	public boolean save(Object key, Object value) throws Exception {
		SymbolExpression sym_value = this.process.get_symbol_factory().parse_to_expression(value);
		return this.top.save(key, sym_value);
	}
	/**
	 * @param key
	 * @return the symbolic expression w.r.t. reference of the key or null if undefined
	 * @throws Exception
	 */
	public SymbolExpression load(Object key) throws Exception {
		return this.top.load(key);
	}
	
}
