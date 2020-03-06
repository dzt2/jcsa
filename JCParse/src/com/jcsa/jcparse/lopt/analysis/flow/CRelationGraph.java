package com.jcsa.jcparse.lopt.analysis.flow;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lopt.ingraph.CirInstanceGraph;
import com.jcsa.jcparse.lopt.ingraph.CirInstanceNode;

/**
 * The relational graph of C program represents the relationships between the program elements, which can be:<br>
 * <br>
 * -----------------------------------------------------------------------------------------------------<br>
 * 	(1)	<code>condition</code>: the source node is a conditional statement (e.g. <code>CirIfStatement</code>
 * 		or <code>CirCaseStatement</code>) while the target is the expression of its condition.<br>
 * 	(2) <code>left_value</code>: the source node is an assignment statement and the target node is taken as
 * 		the left-reference of the assignment directly within the statement of the source node.<br>
 * 	(3) <code>right_value</code> the source node is an assignment statement and the target node is taken as
 * 		the right-expression of the assignment directly within the statement of the source node.<br>
 * 	(4) <code>used_in</code>: the source node is an expression while the target node is a reference that is
 * 		defined within the syntactic tree of the expression node in CIR program.<br>
 * -----------------------------------------------------------------------------------------------------<br>
 * <br>
 * -----------------------------------------------------------------------------------------------------<br>
 * 	(1)	<code>define_use</code>: both the source and the target node refer to the reference expression in
 * 		different statement such that the former is a definition point, which defines the value hold by the
 * 		reference of latter in another statement.<br>
 * 	(2)	<code>use_define</code>: the source node is an expression while the target node is a reference, such
 * 		that the former and the latter refer to the left and right value in the same assignment statement.<br>
 * 	(3)	<code>pass_in</code>: the source node refers to an expression in the calling statement, while the 
 * 		target node refers to the right-value of the assignment to initialize the parameter in the callee
 * 		function.<br>
 * 	(4)	<code>pass_ou</code>: the source node refers to the reference of returning assignment while target
 * 		node refers to the <code>CirWaitExpression</code> used to assign in waiting statement.<br>
 * -----------------------------------------------------------------------------------------------------<br>
 * <br>
 * -----------------------------------------------------------------------------------------------------<br>
 * 	(1) <code>transit_true</code>: the source node is the condition of a if-statement while the target node
 * 		refers to the statement(s) that are executed iff. the condition is evaluated as true.<br>
 * 	(2) <code>transit_false</code>: the source node is the condition of a if-statement while the target node
 * 		refers to the statement(s) that are executed iff. the condition is evaluated as false.<br>
 * -----------------------------------------------------------------------------------------------------<br>
 * <br>
 * -----------------------------------------------------------------------------------------------------<br>
 * 	(1)	<code>wait_point</code>: the source node refers to the calling statement while the target node is
 * 		the waiting assignment statement such that the calling ends at the point of waiting.<br>
 * 	(2)	<code>retr_point</code>: the source node refers to the returning assignment statement and the target
 * 		refers to a waiting assign statement such that the return-statement flowing to the waiting point.<br>
 * 	(3)	<code>function</code>: the source node is the wait-expression while the target node is its operand.<br>
 * 	(4) <code>argument</code>: the source node is the operand of the waiting expression, and the target node
 * 		refers to the expression in the calling statement that flows to the waiting point as specified.<br>
 * -----------------------------------------------------------------------------------------------------<br>
 * <br>
 * 
 * @author yukimula
 *
 */
public class CRelationGraph {
	
	/* constructor */
	/** the mapping from the instance of statement where the nodes refer to **/
	private Map<CirInstanceNode, List<CRelationNode>> instances_nodes;
	/** create an empty relational graph for the C program **/
	private CRelationGraph() { 
		instances_nodes = new HashMap<CirInstanceNode, List<CRelationNode>>(); 
	}
	/**
	 * generate the relational graph based on the given program in C-like intermediate representation.
	 * @param cir_tree
	 * @return
	 * @throws Exception
	 */
	public static CRelationGraph graph(CirInstanceGraph cir_tree) throws Exception {
		CRelationGraph graph = new CRelationGraph();
		CRelationBuilder.build(cir_tree, graph);
		return graph;
	}
	
	/* getters */
	/**
	 * get the number of nodes created in this graph
	 * @return
	 */
	public int size() {
		int size = 0;
		for(List<CRelationNode> nodes : instances_nodes.values()) {
			size = size + nodes.size();
		}
		return size;
	}
	/**
	 * get the set of instances of statements referring to the nodes in this graph
	 * @return
	 */
	public Iterable<CirInstanceNode> get_instances() { return instances_nodes.keySet(); }
	/**
	 * whether there are nodes created in this graph referring to the instance of the statement as provided
	 * @param instance
	 * @return
	 */
	public boolean has_nodes(CirInstanceNode instance) { return instances_nodes.containsKey(instance); }
	/**
	 * get the set of nodes in this graph referring to the given instance of specified statement.
	 * @param instance
	 * @return
	 * @throws Exception
	 */
	public Iterable<CRelationNode> get_nodes(CirInstanceNode instance) throws Exception {
		if(instances_nodes.containsKey(instance)) return instances_nodes.get(instance);
		else { throw new IllegalArgumentException("Invalid instance as: " + instance); }
	}
	/**
	 * whether there is the node with respect to the CIR source node of specified instance
	 * @param instance
	 * @param cir_source
	 * @return
	 */
	public boolean has_node(CirInstanceNode instance, CirNode cir_source) {
		if(this.instances_nodes.containsKey(instance)) {
			for(CRelationNode node : instances_nodes.get(instance)) {
				if(node.get_cir_source() == cir_source) return true;
			}
			return false;
		}
		else {
			return false;
		}
	}
	/**
	 * get the node with respect to the CIR source node of specified instance
	 * @param instance
	 * @param cir_source
	 * @return
	 */
	public CRelationNode get_node(CirInstanceNode instance, CirNode cir_source) {
		if(this.instances_nodes.containsKey(instance)) {
			for(CRelationNode node : instances_nodes.get(instance)) {
				if(node.get_cir_source() == cir_source) return node;
			}
			throw new IllegalArgumentException("Undefined cir_source: " + cir_source);
		}
		else {
			throw new IllegalArgumentException("Undefined instance: " + instance);
		}
	}
	
	/* setters */
	/**
	 * create a new node with respect to the source node in the instance of the statement.
	 * @param instance
	 * @param cir_source
	 * @return
	 * @throws Exception
	 */
	protected CRelationNode new_node(CirInstanceNode instance, CirNode cir_source) throws Exception {
		if(instance == null)
			throw new IllegalArgumentException("Invalid instance: null");
		else if(cir_source == null)
			throw new IllegalArgumentException("Invalid cir_source: null");
		else {
			if(!this.instances_nodes.containsKey(instance))
				this.instances_nodes.put(instance, new LinkedList<CRelationNode>());
			List<CRelationNode> nodes = this.instances_nodes.get(instance);
			
			for(CRelationNode node : nodes) {
				if(node.get_cir_source() == cir_source) return node;
			}
			
			CRelationNode node = new CRelationNode(this, instance, cir_source);
			nodes.add(node);
			return node;
		}
	}
	/**
	 * create an edge from source to the target with respect to the given type
	 * @param type
	 * @param source
	 * @param target
	 * @return
	 * @throws Exception
	 */
	protected boolean connect(CRelationEdgeType type, CRelationNode source, CRelationNode target) throws Exception {
		if(type == null)
			throw new IllegalArgumentException("Invalid type as null");
		else if(source == null || source.get_graph() != this)
			throw new IllegalArgumentException("Invalid source: null");
		else if(target == null || target.get_graph() != this)
			throw new IllegalArgumentException("Invalid target: null");
		else { 
			CRelationEdge edge = source.link_to(type, target); 
			return edge.get_type() == type; 
		}
	}
	
}
