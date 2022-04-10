package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirReferExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirArgumentList;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirBegStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCallStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirDefaultStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirEndStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirGotoStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirLabelStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirWaitAssignStatement;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * 	The location to preserve the value of a state.
 * 	@author yukimula
 *
 */
public final class AbsExecutionStore {
	
	/* constructor */
	/** the category of this state location **/
	private	AbsExecutionLType	_store_class;
	/** the syntactic location of the state **/
	private	AstNode				ast_location;
	/** the intermediate point of the state **/
	private	CirNode				cir_location;
	/** the execution location of the state **/
	private	CirExecution		exe_location;
	
	/* getters */
	/** 
	 * @return the category of this state location
	 */
	public	AbsExecutionLType	get_store_class( )	{ return this._store_class; }
	/**
	 * @return the syntactic location of the state
	 */
	public	AstNode				get_ast_location()	{ return this.ast_location; }
	/**
	 * @return the intermediate point of the state
	 */
	public	CirNode 			get_cir_location()	{ return this.cir_location; }
	/**
	 * @return the execution location of the state
	 */
	public	CirExecution		get_exe_location()	{ return this.exe_location; }
	
	/* classify */
	/**
	 * @return whether the location is an execution-able statement
	 */
	public boolean is_statement() {
		switch(this._store_class) {
		case assg:
		case ifte:
		case call:
		case wait:
		case gots:
		case bend:
		case labl:
		case tags:	return true;
		default:	return false;
		}
	}
	/**
	 * @return whether the location is an evaluate-able expression
	 */
	public boolean is_expression() {
		switch(this._store_class) {
		case bool:
		case argv:
		case expr:
		case refr:	return true;
		default:	return false;
		}
	}
	
	/* general */
	@Override
	public String toString() {
		return this._store_class + "#" + this.ast_location.get_key() + "#" + this.cir_location.get_node_id();
	}
	@Override
	public int hashCode() { return this.toString().hashCode(); }
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof AbsExecutionStore) {
			return this.toString().equals(obj.toString());
		}
		else {
			return false;
		}
	}
	
	/* localize */
	/**
	 * It creates a store-unit based on its arguments
	 * @param _store_class	the category of this state location
	 * @param ast_location	the syntactic location of the state
	 * @param cir_location	the intermediate point of the state
	 * @param exe_location	the execution location of the state
	 * @throws Exception
	 */
	private AbsExecutionStore(AbsExecutionLType _store_class,
			AstNode ast_location, CirNode cir_location,
			CirExecution exe_location) throws IllegalArgumentException {
		if(_store_class == null) {
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
			this._store_class = _store_class;
			this.ast_location = ast_location;
			this.cir_location = cir_location;
			this.exe_location = exe_location;
		}
	}
	/**
	 * It infers the type of execution-location
	 * @param cir_location
	 * @return
	 * @throws IllegalArgumentException
	 */
	private		static AbsExecutionLType new_type(CirNode cir_location) throws IllegalArgumentException {
		if(cir_location == null) {
			throw new IllegalArgumentException("Invalid cir_location: null");
		}
		else if(cir_location instanceof CirExpression) {
			CType type = ((CirExpression) cir_location).get_data_type();
			if(cir_location.get_parent() instanceof CirIfStatement
				|| cir_location.get_parent() instanceof CirCaseStatement) {
				return AbsExecutionLType.bool;
			}
			else if(cir_location.get_parent() instanceof CirArgumentList) {
				return AbsExecutionLType.argv;
			}
			else if(cir_location.get_parent() instanceof CirAssignStatement) {
				CirAssignStatement statement = (CirAssignStatement) cir_location.get_parent();
				if(statement.get_lvalue() == cir_location) {
					return AbsExecutionLType.refr;
				}
				else {
					return SymbolFactory.is_bool(type) ? AbsExecutionLType.bool : AbsExecutionLType.expr;
				}
			}
			else {
				return SymbolFactory.is_bool(type) ? AbsExecutionLType.bool : AbsExecutionLType.expr;
			}
		}
		else if(cir_location instanceof CirStatement) {
			if(cir_location instanceof CirCallStatement) {
				return AbsExecutionLType.call;
			}
			else if(cir_location instanceof CirWaitAssignStatement) {
				return AbsExecutionLType.wait;
			}
			else if(cir_location instanceof CirIfStatement ||
					cir_location instanceof CirCaseStatement) {
				return AbsExecutionLType.ifte;
			}
			else if(cir_location instanceof CirAssignStatement) {
				return AbsExecutionLType.assg;
			}
			else if(cir_location instanceof CirGotoStatement) {
				return AbsExecutionLType.gots;
			}
			else if(cir_location instanceof CirBegStatement ||
					cir_location instanceof CirEndStatement) {
				return AbsExecutionLType.bend;
			}
			else if(cir_location instanceof CirLabelStatement ||
					cir_location instanceof CirDefaultStatement) {
				return AbsExecutionLType.labl;
			}
			else {
				return AbsExecutionLType.tags;
			}
		}
		else {
			throw new IllegalArgumentException(cir_location.getClass().getSimpleName());
		}
	}
	/**
	 * It creates a location with cir-node best-match the ast-location
	 * @param cir_tree		to derive the C-intermediate representative code
	 * @param ast_location	the abstract syntactic tree location in analysis
	 * @return				
	 * @throws Exception
	 */
	public 		static AbsExecutionStore ast_store(CirTree cir_tree, AstNode ast_location) throws Exception {
		if(cir_tree == null) {
			throw new IllegalArgumentException("Invalid cir_tree as: null");
		}
		else if(ast_location == null) {
			throw new IllegalArgumentException("Invalid ast_location: null");
		}
		else {
			CirNode cir_location = AstCirLocalizer.localize(cir_tree, ast_location);
			return new AbsExecutionStore(new_type(cir_location), 
					ast_location, cir_location, cir_location.execution_of());
		}
	}
	/**
	 * It creates a location with ast-node best-match the cir-location
	 * @param cir_location
	 * @return
	 * @throws Exception
	 */
	public 		static AbsExecutionStore cir_store(CirNode cir_location) throws Exception {
		if(cir_location == null) {
			throw new IllegalArgumentException("Invalid cir_location: null");
		}
		else if(cir_location.execution_of() == null) {
			throw new IllegalArgumentException("Invalid exe_location: null");
		}
		else {
			return new AbsExecutionStore(new_type(cir_location), AstCirLocalizer.
					localize(cir_location), cir_location, cir_location.execution_of());
		}
	}
	/**
	 * It creates a location as the definition point
	 * @param ast_location	the abstract syntactic location
	 * @param cir_location	the reference-expression to define 
	 * @return				
	 * @throws Exception
	 */
	public 		static AbsExecutionStore def_store(AstNode ast_location, CirReferExpression cir_location) throws Exception {
		if(ast_location == null) {
			throw new IllegalArgumentException("Invalid ast_location: null");
		}
		else if(cir_location == null) {
			throw new IllegalArgumentException("Invalid cir_location: null");
		}
		else if(cir_location.execution_of() == null) {
			throw new IllegalArgumentException("Invalid exe_location: null");
		}
		else {
			return new AbsExecutionStore(AbsExecutionLType.refr, ast_location, cir_location, cir_location.execution_of());
		}
	}
	/**
	 * It creates a location to the first statement of the location
	 * @param cir_tree
	 * @param ast_location
	 * @return
	 * @throws Exception
	 */
	public 		static AbsExecutionStore beg_store(CirTree cir_tree, AstNode ast_location) throws Exception {
		if(cir_tree == null) {
			throw new IllegalArgumentException("Invalid cir_tree as: null");
		}
		else if(ast_location == null) {
			throw new IllegalArgumentException("Invalid ast_location: null");
		}
		else {
			CirNode cir_location = AstCirLocalizer.localize_beg(cir_tree, ast_location);
			ast_location = AstCirLocalizer.localize(cir_location);
			return new AbsExecutionStore(new_type(cir_location), 
					ast_location, cir_location, cir_location.execution_of());
		}
	}
	/**
	 * It creates a location to the first statement of the location
	 * @param cir_tree
	 * @param ast_location
	 * @return
	 * @throws Exception
	 */
	public 		static AbsExecutionStore end_store(CirTree cir_tree, AstNode ast_location) throws Exception {
		if(cir_tree == null) {
			throw new IllegalArgumentException("Invalid cir_tree as: null");
		}
		else if(ast_location == null) {
			throw new IllegalArgumentException("Invalid ast_location: null");
		}
		else {
			CirNode cir_location = AstCirLocalizer.localize_end(cir_tree, ast_location);
			ast_location = AstCirLocalizer.localize(cir_location);
			return new AbsExecutionStore(new_type(cir_location), 
					ast_location, cir_location, cir_location.execution_of());
		}
	}
	
}
