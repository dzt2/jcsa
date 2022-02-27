package com.jcsa.jcparse.lang.symb;

import com.jcsa.jcparse.lang.ctype.CType;

/**
 * 	It represents an identifier expression that is taken as variable by name, in
 * 	which the identifier is defined as "name#scope", where: 'name' is the simple
 * 	name of the id-expression, and 'scope' is the appendix name for being unique.
 * 	<br>
 * 	<code>
 * 	+-------------------+-------------------+-------------------+-------------------+	<br>
 * 	| Input				| Name				| Scope				| Source			|	<br>
 * 	+-------------------+-------------------+-------------------+-------------------+	<br>
 * 	| CInstanceName		| cname.name		| cname.scope.hash	| [cname]			|	<br>
 * 	| CParameterName	| cname.name		| cname.scope.hash	| [cname]			|	<br>
 * 	| CirExecution		| @exec				| [exec_id]			| [cir_execution]	|	<br>
 * 	+-------------------+-------------------+-------------------+-------------------+	<br>
 * 	| AstName			| name.get_name()	| scope.hash|null	| [ast_name]		|	<br>
 * 	| AstIdExpression	| id.get_name()		| scope.hash|null	| [id_expr]			|	<br>
 * 	| AstExprStatement	| @null				| ast_node.key		| [expr_stmt]		|	<br>
 * 	| AstExpression		| @ast_expr			| ast_node.key		| [ast_expr]		|	<br>
 * 	| AstStatement		| @ast_stmt			| ast_stmt.key		| [ast_stmt]		|	<br>
 * 	| AstNode			| @ast_node			| ast_node.key		| [ast_node]		|	<br>
 * 	+-------------------+-------------------+-------------------+-------------------+	<br>
 * 	| CirDeclarator		| cname.get_name()	| cname.scope.hash	| [cname]			|	<br>
 * 	| CirIdentifier		| cname.get_name()	| cname.scope.hash	| [cname]			|	<br>
 * 	| CirImplicator		| ast				| [ast_node.key]	| [ast_source]		|	<br>
 * 	| CirReturnPoint	| return			| [func_name]		| [retr_point]		|	<br>
 * 	| CirExpression		| @cir_expr			| cir_expr.id		| [cir_expr]		|	<br>
 * 	| CirStatement		| @exec				| [exec_id]			| [cir_execution]	|	<br>
 * 	+-------------------+-------------------+-------------------+-------------------+	<br>
 * 	</code>
 * 	
 * 	@author yukimula
 *
 */
public class SymbolIdentifier extends SymbolBasicExpression {
	
	/* attributes */
	/** the simple name of the identifier **/
	private String name;
	/** the scope where the name is defined **/
	private Object scope;
	
	/* constructor */
	private SymbolIdentifier(CType type, String name, Object scope) throws Exception {
		super(SymbolClass.identifier, type);
		if(name == null || name.strip().isEmpty()) {
			throw new IllegalArgumentException("Invalid name: null");
		}
		else { this.name = name.strip(); this.scope = scope; }
	}
	
	/* getters */
	/**
	 * @return the simple name of the identifier
	 */
	public String get_name() { return this.name; }
	/**
	 * @return the scope where the name is defined
	 */
	public Object get_scope() { return this.scope; }
	/**
	 * @return name#scope as unique identifier
	 */
	public String get_identifier() { return this.name + "#" + this.scope; }
	
	@Override
	protected SymbolNode construct_copy() throws Exception {
		return new SymbolIdentifier(this.get_data_type(), this.name, this.scope);
	}
	
	@Override
	protected String get_code(boolean simplified) throws Exception {
		if(simplified) {
			return this.name;
		}
		else {
			return this.get_identifier();
		}
	}
	
	@Override
	protected boolean is_refer_type() { return true; }

	@Override
	protected boolean is_side_affected() { return false; }
	
	/**
	 * @param type	the data type of the identifier expression
	 * @param name	the simple name of the identifier expression
	 * @param scope	the scope (null) where the unique identifier is defined
	 * @return		name#scope: type
	 * @throws Exception
	 */
	protected static SymbolIdentifier create(CType type, String name, Object scope) throws Exception {
		if(type == null) {
			throw new IllegalArgumentException("Invalid type: null");
		}
		else if(name == null || name.strip().isEmpty()) {
			throw new IllegalArgumentException("Invalid name: null");
		}
		else { return new SymbolIdentifier(type, name, scope); }
	}
	
}
