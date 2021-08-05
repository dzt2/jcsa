package com.jcsa.jcparse.lang.irlang;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.unit.CirFunctionDefinition;

/**
 * C-like intermediate representation language is defined based on following syntactic:<br>
 * <br>
 * <code>
 * 	+---------------------------------------------------------------------------------------+
 * 	CirTransitionUnit		|-- (CirFunction | CirInitAssignStatement)*
 * 	CirFunction				|-- CirDeclarator CirFunctionBody
 * 	CirFunctionBody			|-- CirBegStatement CirParameterList CirStatementList CirEndStatement
 * 	CirParameterList		|-- (CirInitAssignStatement)*
 * 	CirStatementList		|-- (CirStatement)*
 * 	+---------------------------------------------------------------------------------------+
 *
 * 	+---------------------------------------------------------------------------------------+
 * 	CirStatement [index]	|-- CirAssignStatement
 * 							|-- CirGotoStatement | CirCaseStatement
 * 							|-- CirIfStatement | CirCallStatement
 * 							|-- CirTagStatement
 *
 * 	CirAssignStatement		|-- CirBinAssignStatement | CirInitAssignStatement
 * 							|-- CirReturnAssignStatement | CirWaitAssignStatement
 * 							|-- CirIncreAssignStatement
 * 	CirGotoStatement		|-- goto CirLabel
 * 	CirCaseStatement		|-- case CirExpression then goto CirLabel
 * 	CirIfStatement			|-- if CirExpression then CirLabel else CirLabel
 * 	CirCallStatement		|-- call CirExpression CirArgumentList
 * 	CirArgumentList			|-- (CirExpression)*
 * 	CirTagStatement			|-- CirLabelStatement | CirDefaultStatement
 * 							|-- CirBegStatement | CirEndStatement
 * 							|-- CirConjunctStatement
 * 	CirBegStatement			|-- begin :
 * 	CirEndStatement			|-- end :
 * 	CirDefaultStatement		|-- default :
 * 	CirLabelStatement		|-- {string}:
 * 	CirConjunctStatement	|-- end
 * 	CirLabel				|-- <i>integer</i>
 * 	+---------------------------------------------------------------------------------------+
 *
 * 	+---------------------------------------------------------------------------------------+
 * 	CirExpression [CType]	|-- CirReferExpression | CirValueExpression
 * 	CirReferExpression		|-- CirNameExpression | CirDeferExpression | CirFieldExpression
 * 	CirNameExpression 		|-- CirIdentifier | CirDeclarator | CirReturnPoint | CirImplictor
 * 	CirValueExpression		|-- CirConstExpression | CirStringLiteral
 * 							|-- CirArithExpression | CirBitwsExpression
 * 							|-- CirLogicExpression | CirRelationExpression
 * 							|-- CirAddressExpression | CirCastExpression
 * 							|-- CirInitializerBody | CirWaitExpression
 * 							|-- CirDefaultValue
 *
 * 	CirIdExpression			|-- {string}
 * 	CirDeferExpression		|-- (defer CirExpression)
 * 	CirFieldExpression		|-- (get_field CirExpression CirField)
 * 	CirField				|-- {string}
 *
 * 	CirConstExpression		|-- {constant}
 * 	CirStringLiteral		|-- {string}
 * 	CirArithExpression		|-- (+,-,*,/,% CirExpression+)
 * 	CirBitwsExpression		|-- (~,&,|,^,<<,>> CirExpression+)
 * 	CirLogicExpression		|-- (!, &&, || CirExpression+)
 * 	CirRelationExpression	|-- (... CirExpression+)
 * 	CirAddressExpression	|-- (& CirReferExpression)
 * 	CirCastExpression		|-- (cast_to CirType CirExpression)
 * 	CirInitializerBody		|-- { (CirExpression)* }
 * 	+---------------------------------------------------------------------------------------+
 *
 * </code><br>
 * <br>
 * In the node of C-like intermediate representation, it is identified based on an integer ID in
 * its tree within the representation programs.
 *
 * @author yukimula
 *
 */
public interface CirNode {

	/* local properties */
	/**
	 * get the node in syntactic tree that the IR node refers to.
	 * @return
	 */
	public AstNode get_ast_source();
	/**
	 * set the node in syntactic tree that the IR node refers to.
	 * @param source
	 */
	public void set_ast_source(AstNode source) throws IllegalArgumentException;

	/* tree-node relation */
	/**
	 * get the tree in which this node is created
	 * @return
	 */
	public CirTree get_tree();
	/**
	 * get the integer ID of this node within the tree it belongs to
	 * @return
	 */
	public int get_node_id();

	/* parent-child relation */
	/**
	 * get the parent where this node belongs to
	 * @return
	 */
	public CirNode get_parent();
	/**
	 * get the index of this node as the child of its parent
	 * @return -1 when the node is the root of the syntax tree
	 */
	public int get_child_index();
	/**
	 * get the children of this node
	 * @return
	 */
	public Iterable<CirNode> get_children();
	/**
	 * get the number of children in which this node is created
	 * @return
	 */
	public int number_of_children();
	/**
	 * get the kth child in the intermediate representation node
	 * @param k
	 * @return
	 * @throws IndexOutOfBoundsException
	 */
	public CirNode get_child(int k) throws IndexOutOfBoundsException;

	/**
	 * @param simplified
	 * @return generate the code (simplified) that describes the C-intermediate
	 * 			representation language based on the structure of this node.
	 * @throws Exception
	 */
	public String generate_code(boolean simplified) throws Exception;

	/**
	 * get the function to which the node belongs to
	 * @return null when the node is transition unit.
	 */
	public CirFunctionDefinition function_of();

	/**
	 * @return the execution where the node is defined or null if it is not
	 * 			in any CFG-execution node in the graph
	 */
	public CirExecution execution_of();

}
