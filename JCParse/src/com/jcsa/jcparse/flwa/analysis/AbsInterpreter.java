package com.jcsa.jcparse.flwa.analysis;

import java.util.Iterator;

import com.jcsa.jcparse.flwa.graph.CirInstanceEdge;
import com.jcsa.jcparse.flwa.graph.CirInstanceGraph;
import com.jcsa.jcparse.flwa.graph.CirInstanceNode;

/**
 * Abstract interpreter runs over the abstract value defined in system.
 *
 * @author yukimula
 *
 */
public class AbsInterpreter {

	/* attributes */
	/** it defines on which direction the analysis is performed **/
	private boolean direction;
	/** the operator used to simulate the computation on abstract values **/
	private AbsOperator operator;
	/** the instance graph of program flow used within data flow analysis **/
	private CirInstanceGraph graph;

	/* constructor */
	/**
	 * create an abstract interpreter with specified direction and operator.
	 * @param direction
	 * @param operator
	 * @throws Exception
	 */
	private AbsInterpreter(boolean direction, AbsOperator operator) throws Exception {
		if(operator == null)
			throw new IllegalArgumentException("No operators are defined to support abstract computation");
		else { this.direction = direction; this.operator = operator; this.graph = null; }
	}

	/* analysis methods */
	/**
	 * Load the program flow graph on which the abstract interpretation is performed
	 * @param graph
	 * @throws Exception
	 */
	private void load_graph(CirInstanceGraph graph) throws Exception {
		if(graph == null)
			throw new IllegalArgumentException("invalid graph: null");
		else { this.graph = graph; this.init_graph(); }
	}
	/**
	 * Initialize the abstract state hold by each statement instance
	 * or its flow instance.
	 * @throws Exception
	 */
	private void init_graph() throws Exception {
		Iterator<CirInstanceNode> nodes =
				this.graph.forward_node_traversal();
		while(nodes.hasNext()) {
			CirInstanceNode node = nodes.next();
			node.set_state(this.operator.initial_value(node));
			for(CirInstanceEdge edge : node.get_ou_edges())
				edge.set_state(this.operator.initial_value(edge));
		}
	}
	/**
	 * Interpret the abstract state hold by statements or flows within the program
	 * graph until the fix point is reached.
	 * @throws Exception
	 */
	private void interpret() throws Exception {
		int counter = 0;
		while(this.update_nodes_and_edges()) {
			/* fix-point algorithm */
			System.out.println("\t\t#Iteration[" + (++counter) + "]");
		}
	}
	/**
	 * Interpret the abstract state hold by statements or flows within the program
	 * flow graph
	 * @param graph
	 * @throws Exception
	 */
	public void interpret(CirInstanceGraph graph) throws Exception {
		this.load_graph(graph);
		this.interpret();
		return;
	}

	/* updating methods */
	/**
	 * Update the state hold by the statement node in the program flow graph
	 * @param node
	 * @return true if the state changes after the node's new value is decided
	 * @throws Exception
	 */
	private boolean update_node(CirInstanceNode node) throws Exception {
		AbsValue result = this.operator.update_value(node, direction);
		boolean change = ((AbsValue) node.get_state()).set(result);
		return change;
	}
	/**
	 * Update the output flows (or input flows) of the statement node
	 * @param node
	 * @return
	 * @throws Exception
	 */
	private boolean update_edges(CirInstanceNode node) throws Exception {
		/* get the edges that need to be updated */
		Iterable<CirInstanceEdge> edges;
		if(this.direction)
			edges = node.get_ou_edges();
		else edges =node.get_in_edges();

		/* update all the edges' states in program */
		boolean change = false;
		for(CirInstanceEdge edge : edges) {
			AbsValue result = this.operator.update_value(edge, direction);
			if(((AbsValue) edge.get_state()).set(result)) {change = true;}
		}

		return change;
	}
	/**
	 * Update the nodes and edges' states within the entire program graph
	 * @return
	 * @throws Exception
	 */
	private boolean update_nodes_and_edges() throws Exception {
		/* 1. declarations */
		boolean change; Iterator<CirInstanceNode> nodes;
		if(direction)
			nodes = this.graph.forward_node_traversal();
		else nodes = this.graph.backward_node_traversal();

		/* 2. update the state of each node and edge */
		change = false;
		while(nodes.hasNext()) {
			CirInstanceNode node = nodes.next();
			if(this.update_node(node)) 	change = true;
			if(this.update_edges(node)) change = true;
		}

		/* 3. whether program state changes */ return change;
	}

	/* factory methods */
	/**
	 * forward abstract interpreter
	 * @param operator
	 * @return
	 * @throws Exception
	 */
	public static AbsInterpreter forward_interpreter(AbsOperator operator) throws Exception {
		return new AbsInterpreter(true, operator);
	}
	/**
	 * backward abstract interpreter
	 * @param operator
	 * @return
	 * @throws Exception
	 */
	public static AbsInterpreter backward_interpreter(AbsOperator operator) throws Exception {
		return new AbsInterpreter(false, operator);
	}

}
