package com.jcsa.jcparse.lang.irlang.graph;

import java.util.HashMap;
import java.util.Map;

import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.unit.CirFunctionDefinition;
import com.jcsa.jcparse.lang.irlang.unit.CirTransitionUnit;

public class CirFunctionCallGraph {

	private CirTree cir_tree;
	private Map<CirFunctionDefinition, CirFunction> functions;
	private Map<String, CirFunction> name_function_index;
	private Map<CirExecutionFlow, CirFunctionCall> calls;
	/**
	 * create the function graph and creating the function definition's flow graph
	 * @param cir_tree
	 * @throws Exception
	 */
	protected CirFunctionCallGraph(CirTree cir_tree) throws Exception {
		if(cir_tree == null)
			throw new IllegalArgumentException("invalid cir-tree");
		else {
			this.cir_tree = cir_tree;
			this.functions = new HashMap<>();
			this.name_function_index = new HashMap<>();
			this.calls = new HashMap<>();

			this.init_functions();
		}
	}
	/**
	 * creating all the function and flow graph for the definitions in the program.
	 * @throws Exception
	 */
	private void init_functions() throws Exception {
		CirTransitionUnit cir_root = cir_tree.get_root();
		for(int k = 0; k < cir_root.number_of_units(); k++) {
			CirNode child = cir_root.get_unit(k);
			if(child instanceof CirFunctionDefinition) {
				CirFunction function = new CirFunction(this, (CirFunctionDefinition) child);
				this.functions.put(function.get_definition(), function);
				this.name_function_index.put(function.get_name(), function);
			}
		}
	}

	/* getters */
	/**
	 * get the C-like intermediate representation code tree
	 * @return
	 */
	public CirTree get_cir_tree() { return this.cir_tree; }
	/**
	 * get all the functions defined in the program
	 * @return
	 */
	public Iterable<CirFunction> get_functions() { return functions.values(); }
	/**
	 * whether there is a function defined in the source file with respect to the
	 * provided name (each function in C program has a unique name).
	 * @param name
	 * @return
	 */
	public boolean has_function(String name) { return this.name_function_index.containsKey(name); }
	/**
	 * get the function with respect to its name without scoping information.
	 * @param name
	 * @return
	 * @throws IllegalArgumentException
	 */
	public CirFunction get_function(String name) throws IllegalArgumentException {
		if(name_function_index.containsKey(name)) return name_function_index.get(name);
		else throw new IllegalArgumentException("undefined function name as " + name);
	}
	/**
	 * get the function where the statement or expression belongs to
	 * @param node
	 * @return
	 * @throws IllegalArgumentException
	 */
	public CirFunction get_function(CirNode node) throws IllegalArgumentException {
		CirFunctionDefinition definition = node.function_of();
		if(!this.functions.containsKey(definition))
			throw new IllegalArgumentException("Undefined node: " + node);
		else return this.functions.get(definition);
	}
	/**
	 * @return main function in CIR or null
	 */
	public CirFunction get_main_function() {
		if(name_function_index.containsKey("main"))
			return name_function_index.get("main");
		else
			return null;
	}
	/**
	 * whether the execution flow corresponds to any calling relations in the graph
	 * @param flow
	 * @return
	 */
	public boolean has_calling(CirExecutionFlow flow) { return calls.containsKey(flow); }
	/**
	 * get the calling relations with respect to the call_flow|retr_flow
	 * @param flow
	 * @return
	 * @throws IllegalArgumentException
	 */
	public CirFunctionCall get_calling(CirExecutionFlow flow) throws IllegalArgumentException {
		if(this.calls.containsKey(flow)) return this.calls.get(flow);
		else throw new IllegalArgumentException("undefined " + flow.get_type());
	}
	/**
	 * get the calling relation with respect to the given call and return flow in program.
	 * @param call_flow
	 * @param retr_flow
	 * @return
	 * @throws Exception
	 */
	protected CirFunctionCall call(CirExecutionFlow call_flow, CirExecutionFlow retr_flow) throws Exception {
		if(this.calls.containsKey(call_flow) && this.calls.containsKey(retr_flow))
			return this.calls.get(call_flow);
		else if(this.calls.containsKey(call_flow) && this.calls.containsKey(retr_flow))
			throw new IllegalArgumentException("Impossible case for calling!");
		else {
			CirFunctionCall calling = new CirFunctionCall(call_flow, retr_flow);
			this.calls.put(call_flow, calling); this.calls.put(retr_flow, calling);
			calling.get_caller().ou.add(calling); calling.get_callee().in.add(calling);
			return calling;
		}
	}

	/* parsing method */
	/**
	 * construct the execution flow graph and function calling graph based
	 * on the C-like intermediate representation program.
	 * @param cir_tree
	 * @return
	 * @throws Exception
	 */
	public static CirFunctionCallGraph graph(CirTree cir_tree) throws Exception {
		return CirFunctionBuilder.build(cir_tree);
	}

}
