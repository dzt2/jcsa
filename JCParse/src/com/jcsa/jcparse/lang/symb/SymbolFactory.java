package com.jcsa.jcparse.lang.symb;

import com.jcsa.jcparse.lang.CRunTemplate;
import com.jcsa.jcparse.lang.ctype.impl.CTypeFactory;

/**
 * It implements the construction of SymbolNode and its parsing methods.
 * 
 * @author yukimula
 *
 */
public class SymbolFactory {
	
	/* definitions */
	/** it is used to support the inference of data type in C language **/
	private static final CTypeFactory 	type_factory = new CTypeFactory();
	/** used to implement the computation for sizeof **/
	private CRunTemplate 				ast_template;
	/** true to parse CirDefaultValue into constants **/
	private boolean						cir_optimize;
	/**
	 * create a default factory for constructing symbolic node with ast_template as
	 * null and C-intermediate representative optimization as closed configuration.
	 */
	public SymbolFactory() { this.ast_template = null; this.cir_optimize = false; }
	
	/* instance parameter getters and setter */
	/**
	 * @return used to implement the computation for sizeof
	 */
	public CRunTemplate get_ast_template() { return this.ast_template; }
	/**
	 * @return true to parse CirDefaultValue into constants
	 */
	public boolean		get_cir_optimize() { return this.cir_optimize; }
	/**
	 * @param ast_template used to implement the computation for sizeof
	 * @param cir_optimize true to parse CirDefaultValue into constants
	 */
	public void			set_configuration(CRunTemplate ast_template, boolean cir_optimize) {
		this.ast_template = ast_template;
		this.cir_optimize = cir_optimize;
	}
	
	
	
	
	

}
