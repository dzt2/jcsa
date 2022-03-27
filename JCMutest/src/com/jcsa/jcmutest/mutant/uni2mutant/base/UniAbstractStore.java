package com.jcsa.jcmutest.mutant.uni2mutant.base;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirField;
import com.jcsa.jcparse.lang.irlang.expr.CirReferExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirType;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirArgumentList;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirBegStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCallStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseEndStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirEndStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirGotoStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfEndStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirLabel;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * 	It specifies the state-location where UniAbstractState is evaluated using a
 * 	triple-tuple as [store_class, ast_location, cir_location, exe_location].	<br>
 * 	<br>
 * 	<code>
 * 		_store_class: 	the class of the store location to evaluate this state;	<br>
 * 		ast_location:	the abstract syntactic location to preserve this state;	<br>
 * 		cir_location:	a C-intermediate representative to preserve this state;	<br>
 * 		exe_location:	the CFG-node where this state is located and evaluated;	<br>
 * 	</code>
 * 	
 * 	@author yukimula
 *
 */
public class UniAbstractStore {
	
	/* definitions */
	/** the class of the store location to evaluate this state **/
	private UniAbstractLType 	_store_class;
	/** the abstract syntactic location to preserve this state **/
	private	AstNode				ast_location;
	/** a C-intermediate representative to preserve this state **/
	private	CirNode				cir_location;
	/** the CFG-node where this state is located and evaluated **/
	private	CirExecution		exe_location;
	
	/* constructor */
	/**
	 * It creates a store-location to preserve abstract states
	 * @param _store_class	the class of the store location to evaluate this state
	 * @param ast_location	the abstract syntactic location to preserve this state
	 * @param cir_location	a C-intermediate representative to preserve this state
	 * @param exe_location	the CFG-node where this state is located and evaluated
	 * @throws IllegalArgumentException
	 */
	private UniAbstractStore(UniAbstractLType _store_class, 
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
	
	/* getters */
	/**
	 * @return	the class of the store location to evaluate this state
	 */
	public 	UniAbstractLType	get_store_class() 	{ return this._store_class; }
	/**
	 * @return	the abstract syntactic location to preserve this state
	 */
	public	AstNode				get_ast_location()	{ return this.ast_location; }
	/**
	 * @return	a C-intermediate representative to preserve this state
	 */
	public 	CirNode				get_cir_location()	{ return this.cir_location; }
	/**
	 * @return	the CFG-node where this state is located and evaluated
	 */
	public	CirExecution		get_exe_location()	{ return this.exe_location; }
	
	/* general */
	@Override
	public	String	toString() { 
		return this._store_class + "[cir#" + this.cir_location.get_node_id() + ", " + this.exe_location.toString() + "]";
	}
	@Override
	public	int	hashCode()	{ return this.toString().hashCode(); }
	@Override
	public	boolean equals(Object obj) {
		if(obj instanceof UniAbstractStore) {
			return this.toString().equals(obj.toString());
		}
		else {
			return false;
		}
	}
	@Override
	public	UniAbstractStore clone() {
		return new UniAbstractStore(this._store_class, this.ast_location, this.cir_location, this.exe_location);
	}
	
	/* factory */
	/**
	 * @param ast_location the abstract syntactic location to preserve this state
	 * @param cir_location a C-intermediate representative to preserve this state
	 * @return				
	 * @throws Exception
	 */
	protected static UniAbstractStore new_node(AstNode ast_location, CirNode cir_location) throws Exception {
		if(ast_location == null) {
			throw new IllegalArgumentException("Invalid ast_location: null");
		}
		else if(cir_location == null || cir_location.execution_of() == null) {
			throw new IllegalArgumentException("Invalid cir_location: null");
		}
		else {
			UniAbstractLType _store_class;
			if(cir_location instanceof CirExpression) {
				CirExpression expression = (CirExpression) cir_location;
				CirStatement statement = expression.statement_of();
				if(expression.get_parent() instanceof CirAssignStatement) {
					if(((CirAssignStatement) statement).get_lvalue() == expression) {
						_store_class = UniAbstractLType.cdef_expr;
					}
					else if(SymbolFactory.is_bool(expression.get_data_type())) {
						_store_class = UniAbstractLType.bool_expr;
					}
					else {
						_store_class = UniAbstractLType.used_expr;
					}
				}
				else if(SymbolFactory.is_bool(expression.get_data_type())
						|| expression.get_parent() instanceof CirIfStatement
						|| expression.get_parent() instanceof CirCaseStatement) {
					_store_class = UniAbstractLType.bool_expr;
				}
				else {
					_store_class = UniAbstractLType.used_expr;
				}
			}
			else if(cir_location instanceof CirStatement) {
				CirStatement statement = (CirStatement) cir_location;
				if(statement instanceof CirAssignStatement) {
					_store_class = UniAbstractLType.assg_stmt;
				}
				else if(statement instanceof CirIfStatement) {
					_store_class = UniAbstractLType.ifte_stmt;
				}
				else if(statement instanceof CirCaseStatement) {
					_store_class = UniAbstractLType.case_stmt;
				}
				else if(statement instanceof CirCallStatement) {
					_store_class = UniAbstractLType.call_stmt;
				}
				else if(statement instanceof CirGotoStatement) {
					_store_class = UniAbstractLType.goto_stmt;
				}
				else if(statement instanceof CirBegStatement
						|| statement instanceof CirEndStatement) {
					_store_class = UniAbstractLType.bend_stmt;
				}
				else if(statement instanceof CirIfEndStatement
						|| statement instanceof CirCaseEndStatement) {
					_store_class = UniAbstractLType.conv_stmt;
				}
				else {
					_store_class = UniAbstractLType.labl_stmt;
				}
			}
			else {
				if(cir_location instanceof CirArgumentList) {
					_store_class = UniAbstractLType.argls_elm;
				}
				else if(cir_location instanceof CirField) {
					_store_class = UniAbstractLType.field_elm;
				}
				else if(cir_location instanceof CirType) {
					_store_class = UniAbstractLType.ctype_elm;
				}
				else if(cir_location instanceof CirLabel) {
					_store_class = UniAbstractLType.label_elm;
				}
				else {
					throw new IllegalArgumentException(cir_location.getClass().getSimpleName());
				}
			}
			return new UniAbstractStore(_store_class, ast_location, cir_location, cir_location.execution_of());
		}
	}
	/**
	 * @param ast_location
	 * @param cir_location
	 * @return
	 * @throws Exception
	 */
	protected static UniAbstractStore new_vdef(AstNode ast_location, CirReferExpression cir_location) throws Exception {
		if(ast_location == null) {
			throw new IllegalArgumentException("Invalid ast_location: null");
		}
		else if(cir_location == null || cir_location.execution_of() == null) {
			throw new IllegalArgumentException("Invalid cir_location: null");
		}
		else {
			UniAbstractLType _store_class;
			if(cir_location.get_parent() instanceof CirAssignStatement) {
				CirAssignStatement statement = (CirAssignStatement) cir_location.get_parent();
				if(statement.get_lvalue() == cir_location) {
					_store_class = UniAbstractLType.cdef_expr;
				}
				else {
					_store_class = UniAbstractLType.vdef_expr;
				}
			}
			else {
				_store_class = UniAbstractLType.vdef_expr;
			}
			return new UniAbstractStore(_store_class, ast_location, cir_location, cir_location.execution_of());
		}
	}
	
	/* classify */
	/**
	 * @return	whether this location is elementary
	 */
	public boolean is_elem() {
		switch(this._store_class) {
		case argls_elm:
		case field_elm:
		case label_elm:
		case ctype_elm:	return true;
		default:		return false;
		}
	}
	/**
	 * @return whether this location is expression
	 */
	public boolean is_expr() {
		switch(this._store_class) {
		case bool_expr:
		case cdef_expr:
		case used_expr:
		case vdef_expr:	return true;
		default:		return false;
		}
	}
	/**
	 * @return whether the location is a statement
	 */
	public boolean is_stmt() {
		switch(this._store_class) {
		case assg_stmt:
		case ifte_stmt:
		case case_stmt:
		case call_stmt:
		case goto_stmt:
		case bend_stmt:
		case conv_stmt:
		case labl_stmt:	return true;
		default:		return false;
		}
	}
	
}
