package com.jcsa.jcparse.lang.irlang;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * The pair of AstNode and CirNode describes the code range of C-like intermediate representation
 * with the Abstract syntactic tree node in source program as following information provided:<br>
 * (1) <code>ast_source</code>: the abstract syntactic tree node which refers to a set of CirNode.<br>
 * (2) <code>beg_statement</code>: the CIR-statement as the first being executed when AST source node 
 * 		is traversed in the testing.<br>
 * (3) <code>end_statement</code>: the CIR-statement that must be gone through when the computation
 * 		or execution within the AST source node has been completed.<br>
 * (4) <code>result</code>: the computational node refers an expression node in IR program which can 
 * 	   represent the final result of the expression and its usage in the IR code.<br>
 * @author yukimula
 *
 */
public interface AstCirPair {
	
	/**
	 * get the AST source node which refers to a set of statements in the CIR code.
	 * @return
	 */
	public AstNode get_ast_source();
	/**
	 * get the first statement in the CIR code range to be referred from AST source
	 * as the entry of the referred code range.
	 * @return null when it cannot be reached
	 */
	public CirStatement get_beg_statement();
	/**
	 * get the final statement in the CIR code range to be referred from AST source
	 * as the exit from the referred code range
	 * @return null when it cannot be reached
	 */
	public CirStatement get_end_statement();
	/**
	 * get the expression representing the final result of the AST source as its usage in CIR code
	 * @return null when it is not a computational part
	 */
	public CirExpression get_result();
	
	/**
	 * whether the AST source node refers to any statements being executed in the program.
	 * @return
	 */
	public boolean executional();
	/**
	 * whether the AST source node refers to any expressions being evaluated in program.
	 * @return
	 */
	public boolean computational();
	
}
