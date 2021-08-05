package com.jcsa.jcparse.parse.symbol.process;

import java.util.HashMap;
import java.util.Map;

import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolInitializerList;
import com.jcsa.jcparse.lang.symbol.SymbolLiteral;

public class SymbolDataBlock {

	/* definitions */
	/** the data stack to which this block belongs to **/
	private SymbolDataStack stack;
	/** the parent of this block or null if it is root **/
	private SymbolDataBlock parent;
	/** the key uniquely defines this block in parent **/
	private Object block_key;
	/** local data table from unique reference name to its symbolic value **/
	private Map<String, SymbolExpression> table;

	/* constructor */
	/**
	 * create a root block in the stack
	 * @param stack
	 * @throws Exception
	 */
	protected SymbolDataBlock(SymbolDataStack stack) throws Exception {
		if(stack == null)
			throw new IllegalArgumentException("Invalid stack: null");
		else {
			this.stack = stack;
			this.parent = null;
			this.block_key = null;
			this.table = new HashMap<>();
		}
	}
	/**
	 * create a child block when calling function
	 * @param parent
	 * @param key
	 * @throws Exception
	 */
	private SymbolDataBlock(SymbolDataBlock parent, Object key) throws Exception {
		if(parent == null)
			throw new IllegalArgumentException("Invalid parent: null");
		else {
			this.stack = parent.stack;
			this.parent = parent;
			this.block_key = key;
			this.table = new HashMap<>();
		}
	}

	/* getters */
	/**
	 * @return the data stack to which this block belongs to
	 */
	public SymbolDataStack get_stack() { return this.stack; }
	/**
	 * @return the parent of this block or null if it is root
	 */
	public SymbolDataBlock get_parent() { return this.parent; }
	/**
	 * @return the key uniquely defines this block in parent
	 */
	public Object get_block_key() { return this.block_key; }
	/**
	 * @return true if the block is root without parent
	 */
	public boolean is_root() { return this.parent == null; }
	/**
	 * create a new child block as the new context for evaluation
	 * @param key
	 * @return
	 * @throws Exception
	 */
	protected SymbolDataBlock new_child(Object key) throws Exception {
		return new SymbolDataBlock(this, key);
	}

	/* memory operations */
	/**
	 * clear all the data values in local table
	 */
	public void clear() { this.table.clear(); }
	/**
	 * @param reference
	 * @return
	 */
	private boolean is_storage_class(SymbolExpression reference) {
		if(reference instanceof SymbolConstant || reference instanceof SymbolLiteral || reference instanceof SymbolInitializerList) {
			return false;
		}
		else {
			return true;
		}
	}
	/**
	 * save the symbolic value to the reference specified by key
	 * @param key
	 * @param value
	 * @return true if the value is updated into the table
	 * @throws Exception if key is not a reference
	 */
	public boolean save(Object key, SymbolExpression value) throws Exception {
		SymbolExpression reference = this.stack.
				get_process().get_symbol_factory().parse_to_expression(key);
		if(this.is_storage_class(reference)) {
			this.table.put(reference.generate_code(false), value);
			return true;
		}
		else {
			return false;
		}
	}
	/**
	 * load the symbolic value w.r.t. the key
	 * @param key
	 * @return null if value is not defined for key
	 * @throws Exception if key is not reference
	 */
	public SymbolExpression load(Object key) throws Exception {
		SymbolExpression reference = this.stack.
				get_process().get_symbol_factory().parse_to_expression(key);
		if(this.is_storage_class(reference)) {
			String reference_key = reference.generate_code(false);
			SymbolDataBlock block = this;
			while(block != null) {
				if(block.table.containsKey(reference_key)) {
					return block.table.get(reference_key);
				}
				else {
					block = block.get_parent();
				}
			}
			return null;
		}
		else {
			return null;
		}
	}

}
