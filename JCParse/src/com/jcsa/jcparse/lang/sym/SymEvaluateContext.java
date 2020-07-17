package com.jcsa.jcparse.lang.sym;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * It provides the contextual data used for symbolic evaluation.
 * 
 * @author yukimula
 *
 */
public class SymEvaluateContext {
	
	/** mapping from the unique code of input expressions 
	 *  to their specified values in symbolic forms. **/
	private Map<String, SymNode> states;
	/** set of invoke machine for interpreting symbolic
	 *  invoking (SymFunCallExpression). **/
	private List<SymInvoke> invoke_list;
	/** mapping from the node to its evaluated result 
	 * 	during symbolic evaluation **/
	protected Map<SymNode, SymNode> debug_table;
	
	/**
	 * initialize the context data for evaluation
	 */
	protected SymEvaluateContext() {
		this.states = new HashMap<String, SymNode>();
		this.invoke_list = new ArrayList<SymInvoke>();
		this.debug_table = new HashMap<SymNode, SymNode>();
	}
	
	/* states */
	/**
	 * @param input
	 * @return true if there is the value specified for input expression as given
	 */
	public boolean has_state(SymNode input) {
		if(input == null)
			return false;
		else 
			return this.states.containsKey(input.toString());
	}
	/**
	 * @param input
	 * @return the symbolic value specified for input or null if it is not defined
	 */
	public SymNode get_state(SymNode input) {
		if(input == null)
			return null;
		else {
			String key = input.toString();
			if(this.states.containsKey(key))
				return this.states.get(key);
			else
				return null;
		}
	}
	/**
	 * clear all the old values for some symbolic variables in program
	 */
	public void clear_states() {
		this.states.clear();
	}
	/**
	 * specify the value for symbolic input node
	 * @param input
	 * @param value
	 * @throws IllegalArgumentException
	 */
	public void set_state(SymNode input, SymNode value) throws IllegalArgumentException {
		if(input == null)
			throw new IllegalArgumentException("Invalid input: null");
		else if(value == null)
			throw new IllegalArgumentException("Invalid value: null");
		else
			this.states.put(input.toString(), value);
	}
	
	/* invoke_list */
	/**
	 * remove all the invoke machines in context.
	 */
	public void clear_invoke_set() {
		this.invoke_list.clear();
	}
	/**
	 * add a new invoke machine in context
	 * @param invoke
	 * @throws IllegalArgumentException
	 */
	public void add_invoke(SymInvoke invoke) throws IllegalArgumentException {
		if(invoke == null)
			throw new IllegalArgumentException("Invalid invoke: null");
		else if(!this.invoke_list.contains(invoke))
			this.invoke_list.add(invoke);
	}
	/**
	 * @param input
	 * @return the result interpreted from the invocation method or itself if 
	 * the method being invoked is not supported by any machines in the context.
	 * @throws Exception
	 */
	public SymExpression invoke(SymFunCallExpression input) throws Exception {
		for(SymInvoke invoke : this.invoke_list) {
			SymExpression result = invoke.invoke(input);
			if(result != null)
				return result;
		}
		return input;
	}
	
	/* debug_table */
	/**
	 * @return print the information during debuging.
	 */
	public String debug_information() {
		StringBuilder buffer = new StringBuilder();
		
		buffer.append("{\n");
		for(SymNode input : this.debug_table.keySet()) {
			SymNode output = this.debug_table.get(input);
			buffer.append("\t");
			buffer.append(input.toString());
			buffer.append("\t|--> ");
			if(output != null) {
				buffer.append(output.toString());
			}
			buffer.append("\n");
		}
		buffer.append("}\n");
		
		return buffer.toString();
	}
	/**
	 * @return mapping from the node to its evaluated result during symbolic evaluation
	 */
	public Map<SymNode, SymNode> get_debug_table() {
		return this.debug_table;
	}
	
}
