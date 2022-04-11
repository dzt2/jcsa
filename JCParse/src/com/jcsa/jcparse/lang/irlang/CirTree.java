package com.jcsa.jcparse.lang.irlang;

import java.util.List;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.irlang.graph.CirFunctionCallGraph;
import com.jcsa.jcparse.lang.irlang.impl.CirLocalizer;
import com.jcsa.jcparse.lang.irlang.unit.CirTransitionUnit;

/**
 * The syntax tree of the C-like intermediate representation as following:
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
 * 	CirGotoStatement		|-- goto CirLabel
 * 	CirCaseStatement		|-- case CirExpression then goto CirLabel
 * 	CirIfStatement			|-- if CirExpression then CirLabel else CirLabel
 * 	CirCallStatement		|-- call CirExpression CirArgumentList
 * 	CirArgumentList			|-- (CirExpression)*
 * 	CirTagStatement			|-- CirLabelStatement | CirDefaultStatement
 * 							|-- CirBegStatement | CirEndStatement
 * 	CirBegStatement			|-- begin :
 * 	CirEndStatement			|-- end :
 * 	CirDefaultStatement		|-- default :
 * 	CirLabelStatement		|-- {string}:
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
 * 							|-- CirInitializerBody
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
 * @author yukimula
 *
 */
public interface CirTree {

	/* tree node access */
	/**
	 * get the root node of the entire tree (as CirTransitionUnit)
	 * @return
	 */
	public CirTransitionUnit get_root();
	/**
	 * get the number of tree nodes
	 * @return
	 */
	public int size();
	/**
	 * get the tree node in the tree with respect to the specified address ID
	 * @param id
	 * @return
	 * @throws IndexOutOfBoundsException
	 */
	public CirNode get_node(int id) throws IndexOutOfBoundsException;
	/**
	 * get all the nodes created in the given tree
	 * @return
	 */
	public Iterable<CirNode> get_nodes();

	/* mapping from AST source nodes to their CIR nodes */
	/**
	 * get all the CIR nodes in the tree to which the AST node refers
	 * @param ast_source
	 * @return empty
	 */
	public List<CirNode> get_cir_nodes(AstNode ast_source);
	/**
	 * get the CIR nodes in the tree the AST node refers to with specified type
	 * @param ast_source
	 * @param cir_type
	 * @return
	 */
	public List<CirNode> get_cir_nodes(AstNode ast_source, Class<?> cir_type);
	/**
	 * whether there is code range to be referred from the AST source node
	 * @param ast_source
	 * @return
	 */
	public boolean has_cir_range(AstNode ast_source);
	/**
	 * get the CIR code range to be referred from the specified AST source node
	 * @param ast_source
	 * @return
	 */
	public AstCirPair get_cir_range(AstNode ast_source) throws IllegalArgumentException;
	/**
	 * @return the localization algorithm machine
	 */
	public CirLocalizer get_localizer();
	
	/* factory methods */
	/**
	 * generate the copy of the specified node in this tree
	 * @param node
	 * @return
	 * @throws Exception
	 */
	public CirNode copy(CirNode node) throws Exception;

	/* flow graph getter */
	/**
	 * get the function calling graph that describes the execution flow
	 * for statements defined in the given program in form of C-IR code.
	 * @return
	 */
	public CirFunctionCallGraph get_function_call_graph();

}
