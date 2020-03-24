package com.jcsa.jcparse.lopt.models.relation;

/**
 * The edge in relational graph of the C-like intermediate representation describes the relationship
 * between the program elements, which can be one of the following types:<br>
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
 * @author yukimula
 *
 */
public class CRelationEdge {
	
	/* attributes */
	/** the type of the edge in relational graph **/
	private CRelationEdgeType type;
	/** the source node from which the edge points to the target **/
	private CRelationNode source;
	/** the target node to which this edge points from the source **/
	private CRelationNode target;
	
	/* constructor */
	/**
	 * create an edge from the source node to the target node in relational graph 
	 * with respect to the specified type
	 * @param type
	 * @param source
	 * @param target
	 */
	protected CRelationEdge(CRelationEdgeType type, CRelationNode source, CRelationNode target) {
		this.type = type; this.source = source; this.target = target;
	}
	
	/* getters */
	/**
	 * get the type of the edge
	 * @return
	 */
	public CRelationEdgeType get_type() { return type; }
	/**
	 * get the source node from which the edge points to another
	 * @return
	 */
	public CRelationNode get_source() { return source; }
	/**
	 * get the target node to which the edge points from source
	 * @return
	 */
	public CRelationNode get_target() { return target; }
	
}
