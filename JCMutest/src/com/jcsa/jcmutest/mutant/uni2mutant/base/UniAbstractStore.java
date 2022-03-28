package com.jcsa.jcmutest.mutant.uni2mutant.base;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;

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
	protected UniAbstractStore(UniAbstractLType _store_class, 
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
	
	/* classifier */
	/**
	 * @return whether the store-location represents an expression
	 */
	public boolean is_expression() {
		switch(this._store_class) {
		case bool_expr:
		case cdef_expr:
		case argv_expr:
		case used_expr:	return true;
		default:		return false;
		}
	}
	/**
	 * @return whether the store-location represents a statement
	 */
	public boolean is_statement() {
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
	/**
	 * @return whether the store-location represents an elemental
	 */
	public boolean is_element() {
		switch(this._store_class) {
		case fiel_elem:
		case labl_elem:
		case type_elem:
		case args_elem:	return true;
		default:		return false;
		}
	}
	
}