package com.jcsa.jcparse.lang.symb;

import com.jcsa.jcparse.lang.CRunTemplate;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.lexical.CConstant;

/**
 * It provides interface to construct SymbolNode.
 * 
 * @author yukimula
 *
 */
public class SymbolFactory {
	
	/* singleton mode */
	/** the parser used to parse Java-Instance to SymbolNode **/
	private SymbolParser parser;
	/**
	 * create a default factory for constructing symbolic node with ast_template as
	 * null and C-intermediate representative optimization as closed configuration.
	 */
	private SymbolFactory() { this.parser = new SymbolParser(); }
	/** the singleton instance of the symbolic node factory to construct **/
	private static final SymbolFactory factory = new SymbolFactory();
	/**
	 * It establishes the parameters used in factory of symbolic node
	 * @param ast_template
	 * @param cir_optimize
	 */
	public static void set_config(CRunTemplate ast_template, boolean cir_optimize) {
		factory.parser.set(ast_template, cir_optimize);
	}
	
	/* parse methods */
	/**
	 * @param value	{Boolean|Character|Short|Integer|Long|Float|Double|CConstant}
	 * @return		
	 * @throws Exception
	 */
	public static SymbolConstant sym_constant(Object value) throws Exception {
		return factory.parser.parse_cons(value);
	}
	/**
	 * @param source	{Boolean|Character|Short|Integer|Long|Float|Double|CConstant
	 * 					|AstNode|CirNode|CirExecution|SymbolExpression}
	 * @return
	 * @throws Exception
	 */
	public static SymbolExpression sym_expression(Object source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof Boolean || source instanceof Character || source instanceof Short ||
				source instanceof Integer || source instanceof Long || source instanceof Float ||
				source instanceof Double || source instanceof CConstant) {
			return factory.parser.parse_cons(source);
		}
		else if(source instanceof CirExecution) {
			return factory.parser.parse_exec((CirExecution) source);
		}
		else if(source instanceof AstNode) {
			return factory.parser.parse_astn((AstNode) source);
		}
		else if(source instanceof CirNode) {
			return factory.parser.parse_cirn((CirNode) source);
		}
		else if(source instanceof SymbolExpression) {
			return (SymbolExpression) source;
		}
		else if(source instanceof String) {
			return SymbolLiteral.create((String) source);
		}
		else {
			throw new IllegalArgumentException("Invalid source: " + source.getClass().getSimpleName());
		}
	}
	/**
	 * @param source	{Boolean|Character|Short|Integer|Long|Float|Double|CConstant
	 * 					|AstNode|CirNode|CirExecution|SymbolExpression}
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public static SymbolExpression sym_condition(Object source, boolean value) throws Exception {
		SymbolExpression expression = sym_expression(source);
		return factory.parser.parse_bool(expression, value);
	}
	
	/* node creators */
	
	
	
	
	
	
	
	
}
