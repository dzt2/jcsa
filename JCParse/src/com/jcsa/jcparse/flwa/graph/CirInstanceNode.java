package com.jcsa.jcparse.flwa.graph;

import java.util.LinkedList;
import java.util.List;

import com.jcsa.jcparse.flwa.CirInstance;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlowType;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionType;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class CirInstanceNode extends CirInstance {
	
	/** the set of edges to and from this node in the graph **/
	private List<CirInstanceEdge> in, ou;
	/**
	 * create a node of instance in execution graph representing the statement being executed
	 * @param graph
	 * @param context
	 * @param element
	 * @throws Exception
	 */
	protected CirInstanceNode(CirInstanceGraph graph, Object context, CirExecution element) throws Exception {
		super(graph, context, element);
		this.in = new LinkedList<CirInstanceEdge>();
		this.ou = new LinkedList<CirInstanceEdge>();
	}
	/**
	 * create a node of instance in execution graph with respect to the virtual statement of specified type
	 * @param graph
	 * @param context
	 * @param element
	 * @throws Exception
	 */
	protected CirInstanceNode(CirInstanceGraph graph, Object context, CirExecutionType element) throws Exception {
		super(graph, context, element);
		this.in = new LinkedList<CirInstanceEdge>();
		this.ou = new LinkedList<CirInstanceEdge>();
	}
	
	/* getters */
	@Override
	public boolean is_virtual() { 
		return this.get_element() instanceof CirExecutionType; 
	}
	/**
	 * get the statement being executed
	 * @return null if the node is virtual
	 */
	public CirExecution get_execution() { 
		Object element = this.get_element();
		if(element instanceof CirExecution)
			return (CirExecution) element;
		else return null;
	}
	/**
	 * get the type of the statement it represents
	 * @return
	 */
	public CirExecutionType get_type() {
		Object element = this.get_element();
		if(element instanceof CirExecution)
			return ((CirExecution) element).get_type();
		else return (CirExecutionType) element;
	}
	/**
	 * get the set of edges to this node in the graph
	 * @return
	 */
	public Iterable<CirInstanceEdge> get_in_edges() { return in; }
	/**
	 * get the set of edges from this node in the graph
	 * @return
	 */
	public Iterable<CirInstanceEdge> get_ou_edges() { return ou; }
	/**
	 * get the number of edges to this node
	 * @return
	 */
	public int get_in_degree() { return this.in.size(); }
	/**
	 * get the number of edges from this node
	 * @return
	 */
	public int get_ou_degree() { return this.ou.size(); }
	/**
	 * get the kth edge to this node
	 * @param k
	 * @return
	 * @throws IndexOutOfBoundsException
	 */
	public CirInstanceEdge get_in_edge(int k) throws IndexOutOfBoundsException {
		return this.in.get(k);
	}
	/**
	 * get the kth edge from this node
	 * @param k
	 * @return
	 * @throws IndexOutOfBoundsException
	 */
	public CirInstanceEdge get_ou_edge(int k) throws IndexOutOfBoundsException {
		return this.ou.get(k);
	}
	
	/* code generator */
	/**
	 * whether the syntax node of C-like intermediate representation
	 * belongs to the range of the instance node refers to.
	 * @param node
	 * @return
	 */
	public boolean has_cir_node(CirNode node) {
		CirExecution execution = this.get_execution();
		if(execution == null) { return false; }
		else {
			CirStatement statement = execution.get_statement();
			while(node != null) {
				if(node == statement) { return true; }
				else { node = node.get_parent(); }
			}
			return false;
		}
	}
	/**
	 * generate the unique code of the node within the context of the instance
	 * which requires the node belongs to the instance range of statement.
	 * @param node
	 * @return
	 * @throws Exception
	 */
	public String generate_code(CirNode node) throws Exception {
		if(!this.has_cir_node(node))
			throw new IllegalArgumentException("Undefined expression: " + node);
		else {
			return node.generate_code(false) + "#" + this.get_context().hashCode();
		}
	}
	
	/* setters */
	/**
	 * link this node with the target with an edge with respect to the program flow
	 * in the program.
	 * @param target
	 * @param context
	 * @param flow
	 * @return
	 * @throws Exception
	 */
	protected CirInstanceEdge link_to(CirInstanceNode target, 
			Object context, CirExecutionFlow flow) throws Exception {
		if(target == null || target.get_graph() != this.get_graph())
			throw new IllegalArgumentException("Undefined target: " + target);
		else if(flow.get_source() != this.get_execution())
			throw new IllegalArgumentException("Unable to match the source");
		else if(flow.get_target() != target.get_execution())
			throw new IllegalArgumentException("Unable to match the target");
		else {
			CirInstanceEdge edge = new CirInstanceEdge(this, target, context, flow);
			this.ou.add(edge); target.in.add(edge); return edge;
		}
	}
	/**
	 * create a virtual edge to link this node with the specified target.
	 * @param target
	 * @param context
	 * @param flow_type
	 * @return
	 * @throws Exception
	 */
	protected CirInstanceEdge link_to(CirInstanceNode target, 
			Object context, CirExecutionFlowType flow_type) throws Exception {
		if(target == null || target.get_graph() != this.get_graph())
			throw new IllegalArgumentException("Undefined target: " + target);
		else {
			CirInstanceEdge edge = new CirInstanceEdge(this, target, context, flow_type);
			this.ou.add(edge); target.in.add(edge); return edge;
		}
	}
	
}
