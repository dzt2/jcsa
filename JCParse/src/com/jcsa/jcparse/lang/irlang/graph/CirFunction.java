package com.jcsa.jcparse.lang.irlang.graph;

import java.util.LinkedList;
import java.util.List;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.expr.CirDeclarator;
import com.jcsa.jcparse.lang.irlang.expr.CirNameExpression;
import com.jcsa.jcparse.lang.irlang.unit.CirFunctionDefinition;

public class CirFunction {

	private CirFunctionCallGraph graph;
	private CirFunctionDefinition definition;
	private CirExecutionFlowGraph flow_graph;
	protected List<CirFunctionCall> in, ou;
	protected CirFunction(CirFunctionCallGraph graph, CirFunctionDefinition definition) throws Exception {
		if(graph == null)
			throw new IllegalArgumentException("invalid graph: null");
		else if(definition == null)
			throw new IllegalArgumentException("invalid definition.");
		else {
			this.graph = graph;
			this.definition = definition;
			this.in = new LinkedList<>();
			this.ou = new LinkedList<>();
			this.flow_graph = new CirExecutionFlowGraph(this);
		}
	}

	/* getters */
	/**
	 * get the graph where the function node is created
	 * @return
	 */
	public CirFunctionCallGraph get_graph() { return this.graph; }
	/**
	 * get the function name (without scoping information)
	 * @return
	 */
	public String get_name() {
		CirNameExpression declarator = definition.get_declarator();
		if(declarator instanceof CirDeclarator) {
			return ((CirDeclarator) declarator).get_cname().get_name();
		}
		else {
			return declarator.get_name();
		}
	}
	/**
	 * get the type of the function
	 * @return
	 */
	public CType get_type() { return definition.get_declarator().get_data_type(); }
	/**
	 * get the definition of the function node in graph
	 * @return
	 */
	public CirFunctionDefinition get_definition() { return definition; }
	/**
	 * get the execution flow graph of the function
	 * @return
	 */
	public CirExecutionFlowGraph get_flow_graph() { return flow_graph; }
	/**
	 * get the calling relations that call this method
	 * @return
	 */
	public Iterable<CirFunctionCall> get_in_calls() { return this.in; }
	/**
	 * get the calling relations that this method calls (another defined function)
	 * @return
	 */
	public Iterable<CirFunctionCall> get_ou_calls() { return this.ou; }

}
