package com.jcsa.jcmutest.mutant.cir2mutant.path;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.parse.symbol.process.SymbolProcess;

/**
 * It maintains the data, statement and condition store-value pairs at an 
 * execution point during testing.
 * 
 * @author yukimula
 *
 */
public class CirStateData {
	
	/* definitions */
	/** the execution point where the state is defined **/
	private CirExecution				execution;
	/** the set of logical formulas of pred_conditions **/
	private Set<CirStoreValue>			conditions;
	/** the mapping from expression to the data values **/
	private Map<CirNode, CirStoreValue>	data_table;
	/** the mapping from statement to the point values **/
	private Map<CirNode, CirStoreValue> stmt_table;
	
	/* constructor */
	/**
	 * It creates an empty table of store state at some execution point.
	 * @param execution
	 * @throws IllegalArgumentException
	 */
	protected CirStateData(CirExecution execution) throws IllegalArgumentException {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else {
			this.execution 	= execution;
			this.conditions = new HashSet<CirStoreValue>();
			this.data_table = new HashMap<CirNode, CirStoreValue>();
			this.stmt_table = new HashMap<CirNode, CirStoreValue>();
		}
	}
	
	/* getters */
	/**
	 * @return the execution point where the state is defined
	 */
	public CirExecution 			get_execution() 	{ return this.execution; }
	/**
	 * @return the set of logical formulas of pred_conditions
	 */
	public Iterable<CirStoreValue> 	get_conditions() 	{ return this.conditions; }
	/**
	 * @return the set of store-value pairs preserving the state of data store units
	 */
	public Iterable<CirStoreValue> 	get_data_values() 	{ return this.data_table.values(); }
	/**
	 * @return the set of store-value pairs preserving the state of statement points
	 */
	public Iterable<CirStoreValue>	get_stmt_values()	{ return this.stmt_table.values(); }
	/**
	 * @return the store units (locations) preserving the states of data store units
	 */
	public Iterable<CirNode>		get_data_units() 	{ return this.data_table.keySet(); }
	/**
	 * @return the store units (locations) preserving the states of statement points
	 */
	public Iterable<CirNode>		get_stmt_units()	{ return this.stmt_table.keySet(); }
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		
		/* [C1 AND C2 AND ... AND Cn] */
		boolean first = true;
		buffer.append("[");
		for(CirStoreValue condition : this.conditions) {
			if(first) {
				first = false;
			}
			else {
				buffer.append(" AND ");
			}
			buffer.append(condition.get_val_string());
		}
		buffer.append("]");
		
		/* DATA{ S1; S2; ... Sn; } */
		buffer.append("\tDATA{ ");
		for(CirStoreValue value : this.data_table.values()) {
			buffer.append(value.toString());
			buffer.append("; ");
		}
		buffer.append("}");
		
		/* STMT{ S1; S2; ... Sn; } */
		buffer.append("\tSTMT{ ");
		for(CirStoreValue value : this.stmt_table.values()) {
			buffer.append(value.toString());
			buffer.append("; ");
		}
		buffer.append("}");
		
		return buffer.toString();
	}
	
	/* setters */
	/**
	 * It adds the store value pair into the data state table.
	 * @param store_value
	 * @throws Exception
	 */
	protected void add(CirStoreValue store_value) throws Exception {
		if(store_value == null) {
			throw new IllegalArgumentException("Invalid store_value: null");
		}
		else if(store_value.get_store_type() == CirStoreClass.cond) {
			this.conditions.add(store_value);
		}
		else if(store_value.get_store_type() == CirStoreClass.stmt) {
			this.stmt_table.put(store_value.get_store_unit(), store_value);
		}
		else {
			this.data_table.put(store_value.get_store_unit(), store_value);
		}
	}
	/**
	 * It clears all the pred_conditions, data values and statement pointers defined in the state
	 */
	protected void clear() {
		this.conditions.clear();
		this.data_table.clear();
		this.stmt_table.clear();
	}
	/**
	 * It clears the state or value hold by each store-unit in the state table.
	 */
	protected void reset() {
		for(CirStoreValue value : this.conditions) value.clc();
		for(CirStoreValue value : this.data_table.values()) value.clc();
		for(CirStoreValue value : this.stmt_table.values()) value.clc();
	}
	/**
	 * It evaluates the value of each store unit in the state table concretely
	 * @param context
	 * @throws Exception
	 */
	protected void evaluate(SymbolProcess context) throws Exception {
		for(CirStoreValue value : this.conditions) { value.add(context); }
		for(CirStoreValue value : this.data_table.values()) { value.add(context); }
		for(CirStoreValue value : this.stmt_table.values()) { value.add(context); }
	}
	
}
