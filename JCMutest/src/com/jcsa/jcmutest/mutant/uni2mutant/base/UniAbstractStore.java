package com.jcsa.jcmutest.mutant.uni2mutant.base;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;

/**
 * 	It specifies a triple-location to localize the state where it is evaluated.
 * 	<br>
 * 	<code>
 * 	+---------------+-------------------+-------------------+-------------------+	<br>
 * 	|	_class		|	AstNode			|	CirNode			|	CirExecution	|	<br>
 * 	+---------------+-------------------+-------------------+-------------------+	<br>
 * 	|  args_list	|  AstArgumentList	| CirArgumentList	| CirCallStatement	|	<br>
 * 	|  bool_cond	|  AstLogicExpr		| CirExpression		| CirIfStatement	|	<br>
 * 	+---------------+-----------------------------------------------------------+	<br>
 * 	
 * 	TODO append more store class and structural description here...
 * 	</code>
 * 	
 * 	@author yukimula
 *
 */
public class UniAbstractStore {
	
	/* definitions */
	/** the type of the stored location of state **/
	private	UniAbstractSType	_store_class;
	/** the abstract syntactic location of state **/
	private	AstNode				ast_location;
	/** a C-intermediate representation of state **/
	private	CirNode				cir_location;
	/** the CFG-node as execution point of state **/
	private	CirExecution		exe_location;
	/**
	 * It creates a store location of the abstract state
	 * @param _store_class	the type of the stored location of state
	 * @param ast_location	the abstract syntactic location of state
	 * @param cir_location	the C-intermediate representive of state
	 * @param exe_location	the CFG-node as execution point of state
	 * @throws IllegalArgumentException
	 */
	protected	UniAbstractStore(UniAbstractSType _class,
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
	 * @return the type of the stored location of state
	 */
	public	UniAbstractSType	get_store_class( ) { return this._store_class; }
	/**
	 * @return the abstract syntactic location of state
	 */
	public	AstNode				get_ast_location() { return this.ast_location; }
	/**
	 * @return the C-intermediate representive of state
	 */
	public	CirNode				get_cir_location() { return this.cir_location; }
	/**
	 * @return the CFG-node as execution point of state
	 */
	public	CirExecution		get_exe_location() { return this.exe_location; }
	
	/* factory */
	/**
	 * It translates the C-intermediate location to a best-matching store location with given AstNode
	 * @param cir_location
	 * @return
	 * @throws Exception
	 */
	public static UniAbstractStore	cir_node(CirNode cir_location) throws Exception {
		return UniStoreLocalizer.loc(cir_location);
	}
	/**
	 * It generates a virtual definition point w.r.t. ast and cir nodes
	 * @param ast_location
	 * @param cir_location
	 * @return
	 * @throws Exception
	 */
	public static UniAbstractStore	cir_vdef(AstNode ast_location, CirNode cir_location) throws Exception {
		return new UniAbstractStore(UniAbstractSType.vdef_expr, ast_location, cir_location, cir_location.execution_of());
	}
	
}
