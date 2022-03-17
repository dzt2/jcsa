package com.jcsa.jcmutest.mutant.uni2mutant.base;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * 	It denotes a location where the abstract state is evaluated.
 * 	
 * 	@author yukimula
 *
 */
public class CirAbstractStore {
	
	/* attributes */
	/** the category of the store unit from state **/
	private	CirAbstractStoreClass	_store_class;
	/** the abstract syntactic location to denote **/
	private	AstNode					ast_location;
	/** the C-intermediate representive to denote **/
	private	CirNode					cir_location;
	/** the CFG-point as the execution statements **/
	private	CirExecution			exe_location;
	
	/* constructor */
	/**
	 * 
	 * @param _store_class
	 * @param ast_location
	 * @param cir_location
	 * @param exe_location
	 * @throws IllegalArgumentException
	 */
	private CirAbstractStore(CirAbstractStoreClass _class,
			AstNode ast_location, CirNode cir_location, 
			CirExecution exe_location) throws Exception {
		if(_class == null) {
			throw new IllegalArgumentException("Invalid _store_class: null");
		}
		else if(ast_location == null) {
			throw new IllegalArgumentException("Invalid ast_location: null");
		}
		else if(cir_location == null) {
			throw new IllegalArgumentException("Invalid cir_location: null");
		}
		else if(exe_location == null) {
			throw new IllegalArgumentException("Invalid exe_location: null");
		}
		else {
			this._store_class = _class;
			this.ast_location = ast_location;
			this.cir_location = cir_location;
			this.exe_location = exe_location;
		}
	}
	
	/* getters */
	/**
	 * @return	the category of the store unit from state
	 */
	public CirAbstractStoreClass	get_store_class() 	{ return this._store_class; }
	/**
	 * @return	the abstract syntactic location to denote
	 */
	public AstNode					get_ast_location()	{ return this.ast_location; }
	/**
	 * @return	the C-intermediate representive to denote
	 */
	public CirNode					get_cir_location()	{ return this.cir_location; }
	/**
	 * @return	the CFG-point as the execution statements
	 */
	public CirExecution				get_exe_location()	{ return this.exe_location; }
	
	/* factory methods */
	/**
	 * It creates a used expression location to evaluate the l-states
	 * @param ast_location
	 * @param cir_location
	 * @return
	 * @throws Exception
	 */
	protected static CirAbstractStore cir_expr(AstNode ast_location, CirExpression cir_location) throws Exception {
		if(ast_location == null) {
			throw new IllegalArgumentException("Invalid ast_location: null");
		}
		else if(cir_location == null || cir_location.statement_of() == null) {
			throw new IllegalArgumentException("Invalid cir_location: null");
		}
		else if(cir_location.get_parent() instanceof CirIfStatement
				|| cir_location.get_parent() instanceof CirCaseStatement) {
			return new CirAbstractStore(CirAbstractStoreClass.cond, ast_location, cir_location, cir_location.execution_of());
		}
		else if(cir_location.get_parent() instanceof CirAssignStatement) {
			CirAssignStatement stmt = (CirAssignStatement) cir_location.get_parent();
			if(stmt.get_lvalue() == cir_location) {
				return new CirAbstractStore(CirAbstractStoreClass.refr, ast_location, cir_location, stmt.execution_of());
			}
			else {
				return new CirAbstractStore(CirAbstractStoreClass.expr, ast_location, cir_location, cir_location.execution_of());
			}
		}
		else {
			return new CirAbstractStore(CirAbstractStoreClass.expr, ast_location, cir_location, cir_location.execution_of());
		}
	}
	/**
	 * It creates a statement location for evaluating the l-states
	 * @param ast_location
	 * @param cir_location
	 * @return
	 * @throws Exception
	 */
	protected static CirAbstractStore cir_stmt(CirAbstractStoreClass _class, AstNode ast_location, CirStatement cir_location) throws Exception {
		if(ast_location == null) {
			throw new IllegalArgumentException("Invalid ast_location: null");
		}
		else if(cir_location == null || cir_location.execution_of() == null) {
			throw new IllegalArgumentException("Invalid cir_location: null");
		}
		else {
			return new CirAbstractStore(_class, ast_location, cir_location, cir_location.execution_of());
		}
	}
	
	
	
	
	
}
