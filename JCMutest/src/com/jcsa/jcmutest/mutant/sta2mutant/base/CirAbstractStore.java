package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirArgumentList;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirBegStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCallStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseEndStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirDefaultStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirEndStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfEndStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirLabelStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirWaitAssignStatement;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * 	It describes the storage location to preserve value in the abstract state.	<br>
 * 	
 * 	@author yukimula
 *
 */
public class CirAbstractStore {
	
	/* definitions */
	private	CirAbstractLType	_store_class;
	private	AstNode				ast_location;
	private	CirNode				cir_location;
	private	CirExecution		exe_location;
	private CirAbstractStore(CirAbstractLType _store_class,
			AstNode ast_location, CirNode cir_location,
			CirExecution exe_location) throws Exception {
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
	 * @return the type of the storage location in the program state
	 */
	public 	CirAbstractLType 	get_store_class()	{ return this._store_class; }
	/**
	 * @return the abstract syntactic location to preserve the state
	 */
	public	AstNode				get_ast_location()	{ return this.ast_location; }
	/**
	 * @return the C-intermediate representation to preserve state
	 */
	public	CirNode				get_cir_location()	{ return this.cir_location; }
	/**
	 * @return the CFG-execution point to preserve the abstract state
	 */
	public	CirExecution		get_exe_location()	{ return this.exe_location; }
	@Override
	public String toString() { 
		return this._store_class + "#" + this.ast_location.get_key() + "#" + this.cir_location.get_node_id(); 
	}
	@Override
	public int hashCode() { return this.toString().hashCode(); }
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof CirAbstractStore) {
			return obj.toString().equals(this.toString());
		}
		else {
			return false;
		}
	}
	/**
	 * @return whether the storage location is a statement
	 */
	public boolean is_statement() {
		switch(this._store_class) {
		case assg:
		case call:
		case wait:
		case ifte:
		case bend:
		case labl:
		case conv:
		case gotw:	return true;
		default:	return false;
		}
	}
	/**
	 * @return whether the storage location is an expression
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
	
	/* factory */
	/**
	 * @param cir_location
	 * @return	the type of the storage location based on the input C-intermediate code
	 * @throws Exception
	 */
	private static CirAbstractLType new_type(CirNode cir_location) throws Exception {
		if(cir_location == null) {
			throw new IllegalArgumentException("Invalid cir_location: null");
		}
		else if(cir_location instanceof CirExpression) {
			CirExpression expression = (CirExpression) cir_location;
			if(SymbolFactory.is_bool(expression.get_data_type())
				|| expression.get_parent() instanceof CirIfStatement
				|| expression.get_parent() instanceof CirCaseStatement) {
				return CirAbstractLType.bool;
			}
			else if(expression.get_parent() instanceof CirArgumentList) {
				return CirAbstractLType.argv;
			}
			else if(expression.get_parent() instanceof CirAssignStatement) {
				CirAssignStatement statement = (CirAssignStatement) expression.get_parent();
				if(statement.get_lvalue() == expression) {
					return CirAbstractLType.refr;
				}
				else {
					return CirAbstractLType.expr;
				}
			}
			else {
				return CirAbstractLType.expr;
			}
		}
		else if(cir_location instanceof CirStatement) {
			CirStatement statement = (CirStatement) cir_location;
			if(statement instanceof CirCallStatement) {
				return CirAbstractLType.call;
			}
			else if(statement instanceof CirWaitAssignStatement) {
				return CirAbstractLType.wait;
			}
			else if(statement instanceof CirIfStatement || statement instanceof CirCaseStatement) {
				return CirAbstractLType.ifte;
			}
			else if(statement instanceof CirAssignStatement) {
				return CirAbstractLType.assg;
			}
			else if(statement instanceof CirBegStatement || statement instanceof CirEndStatement) {
				return CirAbstractLType.bend;
			}
			else if(statement instanceof CirIfEndStatement || statement instanceof CirCaseEndStatement) {
				return CirAbstractLType.conv;
			}
			else if(statement instanceof CirDefaultStatement || statement instanceof CirLabelStatement) {
				return CirAbstractLType.labl;
			}
			else {
				return CirAbstractLType.gotw;
			}
		}
		else {
			throw new IllegalArgumentException("Invalid: " + cir_location.getClass());
		}
	}
	/**
	 * @param cir_location
	 * @return the storage location w.r.t. the cir-based location as center
	 * @throws Exception
	 */
	protected static CirAbstractStore new_store(AstNode ast_location, CirNode cir_location) throws Exception {
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
			CirAbstractLType type = new_type(cir_location);
			return new CirAbstractStore(type, ast_location, cir_location, cir_location.execution_of());
		}
	}
	
}
