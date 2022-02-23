package com.jcsa.jcparse.lang.symb;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator.DeclaratorProduction;
import com.jcsa.jcparse.lang.astree.stmt.AstCaseStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstExpressionStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstIfStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstReturnStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstSwitchStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirDefaultValue;
import com.jcsa.jcparse.lang.irlang.expr.CirReturnPoint;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.unit.CirFunctionDefinition;
import com.jcsa.jcparse.lang.scope.CName;

/**
 * 	SymbolIdentifier		[get_name(): String]		<br>
 * 	Naming-Regulation									<br>
 * 	<code>
 * 		name#scope			|--	normal variable			<br>
 * 		do#exec_id			|--	{CirExecution}			<br>
 * 		return#func_name	|--	{CirFunctionDefinition
 * 								|AstFunctionDefinition
 * 								|AstReturnStatement}	<br>
 * 		default#node_id		|--	{CirDefaultValue}		<br>
 * 		if#node_id			|--	{CirIfStatement
 * 								|AstIfStatement
 * 								|AstSwitchStatement}	<br>
 * 		case#node_id		|--	{CirCaseStatement
 * 								|AstCaseStatement}		<br>
 * 		while#node_id		|--	{AstForStatement
 * 								|AstWhileStatement
 * 								|AstDoWhileStatement}	<br>
 * 		default#			|--	{AstExpressionStatement}<br>
 * 	</code>
 * 	
 * 	@author yukimula
 *
 */
public class SymbolIdentifier extends SymbolBasicExpression {
	
	/** the name of the identifier expression **/
	private String name;
	
	/**
	 * @param type	the data type of the expression
	 * @param name	the name of the identifier
	 * @throws Exception
	 */
	private SymbolIdentifier(CType type, String name) throws Exception {
		super(SymbolClass.identifier, type);
		if(name == null || name.strip().isEmpty()) {
			throw new IllegalArgumentException("Invalid name: null");
		}
		else {
			this.name = name.strip();
		}
	}
	
	/**
	 * @return the name of the identifier expression
	 */
	public String get_identifier() { return this.name; }
	
	/**
	 * @return [name, scope] divided by #
	 */
	public String[] get_name_scope() { 
		int index = this.name.indexOf('#');
		String title = "", scope = "";
		if(index > 0) {
			title = this.name.substring(0, index).strip();
		}
		if(index < this.name.length() - 1) {
			scope = this.name.substring(index + 1).strip();
		}
		return new String[] { title, scope };
	}
	
	@Override
	protected SymbolNode construct_copy() throws Exception {
		return new SymbolIdentifier(this.get_data_type(), this.name);
	}

	@Override
	protected String get_code(boolean simplified) throws Exception {
		if(simplified) {
			String[] name_scope = this.get_name_scope();
			String name = name_scope[0];
			if(name.isEmpty()) {
				return this.name;
			}
			else {
				return name;
			}
		}
		else {
			return this.name;
		}
	}

	@Override
	protected boolean is_refer_type() { return true; }

	@Override
	protected boolean is_side_affected() { return false; }
	
	/**
	 * @param type
	 * @param name
	 * @param scope
	 * @return (name#scope: type)
	 * @throws Exception
	 */
	protected static SymbolIdentifier create(CType type, String name, Object scope) throws Exception {
		if(type == null) {
			throw new IllegalArgumentException("Invalid type: null");
		}
		else if(name == null) {
			throw new IllegalArgumentException("Invalid name: null");
		}
		else {
			return new SymbolIdentifier(type, name + "#" + scope);
		}
	}
	
	/**
	 * Variable Name by Normal
	 * @param type
	 * @param cname
	 * @return	(name#scope: type)	|--> {CName}
	 * @throws Exception
	 */
	protected static SymbolIdentifier create_name(CType type, CName cname) throws Exception {
		if(type == null) {
			throw new IllegalArgumentException("Invalid type: null");
		}
		else if(cname == null) {
			throw new IllegalArgumentException("Invalid cname: null");
		}
		else {
			SymbolIdentifier identifier = SymbolIdentifier.create(type, 
						cname.get_name(), cname.get_scope().hashCode());
			identifier.set_source(cname); return identifier;
		}
	}
	
	/**
	 * Execution Flag Variable
	 * @param execution
	 * @return	(do#exec_id: int)
	 * @throws Exception
	 */
	protected static SymbolIdentifier create_exec(CirExecution execution) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else {
			SymbolIdentifier identifier = SymbolIdentifier.create(
						CBasicTypeImpl.int_type, "do", execution);
			identifier.set_source(execution); return identifier;
		}
	}
	
	/**
	 * Variable originated from abstract syntactic node
	 * @param source
	 * @return	AstFunctionDefinition		|--	return#func_name	<br>
	 * 			AstReturnStatement			|--	return#func_name	<br>
	 * 			AstIfStatement				|--	if#ast_key			<br>
	 * 			AstSwitchStatement			|--	switch#ast_key		<br>
	 * 			AstCaseStatement			|--	case#ast_key		<br>
	 * 			AstForStatement				|--	for#ast_key			<br>
	 * 			AstWhileStatement			|--	while#ast_key		<br>
	 * 			AstDoWhileStatement			|--	while#ast_key		<br>
	 * 			AstExpressionStatement		|--	default#ast_key		<br>
	 * 			otherwise					|--	ast#ast_key			<br>
	 * @throws Exception
	 */
	protected static SymbolIdentifier create_astn(CType type, AstNode source) throws Exception {
		if(type == null) {
			throw new IllegalArgumentException("Invalid type: null");
		}
		else if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof AstFunctionDefinition) {
			AstDeclarator declarator = ((AstFunctionDefinition) source).get_declarator();
			while(declarator.get_production() != DeclaratorProduction.identifier) {
				declarator = declarator.get_declarator();
			}
			String func_name = declarator.get_identifier().get_name();
			SymbolIdentifier identifier = 
					SymbolIdentifier.create(type, "return", func_name);
			identifier.set_source(source); return identifier;
		}
		else if(source instanceof AstReturnStatement) {
			AstFunctionDefinition def = null;
			AstNode node = source;
			while(node != null) {
				if(node instanceof AstFunctionDefinition) {
					def = (AstFunctionDefinition) node;
					break;
				}
				else {
					node = node.get_parent();
				}
			}
			SymbolIdentifier identifier = SymbolIdentifier.create_astn(type, def);
			identifier.set_source(source); return identifier;
		}
		else if(source instanceof AstIfStatement) {
			SymbolIdentifier identifier = SymbolIdentifier.
						create(type, "if", source.get_key());
			identifier.set_source(source); return identifier;
		}
		else if(source instanceof AstSwitchStatement) {
			SymbolIdentifier identifier = SymbolIdentifier.
					create(type, "switch", source.get_key());
			identifier.set_source(source); return identifier;
		}
		else if(source instanceof AstCaseStatement) {
			SymbolIdentifier identifier = SymbolIdentifier.
					create(type, "case", source.get_key());
			identifier.set_source(source); return identifier;
		}
		else if(source instanceof AstForStatement) {
			SymbolIdentifier identifier = SymbolIdentifier.
					create(type, "for", source.get_key());
			identifier.set_source(source); return identifier;
		}
		else if(source instanceof AstWhileStatement) {
			SymbolIdentifier identifier = SymbolIdentifier.
					create(type, "while", source.get_key());
			identifier.set_source(source); return identifier;
		}
		else if(source instanceof AstDoWhileStatement) {
			SymbolIdentifier identifier = SymbolIdentifier.
					create(type, "while", source.get_key());
			identifier.set_source(source); return identifier;
		}
		else if(source instanceof AstExpressionStatement) {
			SymbolIdentifier identifier = SymbolIdentifier.
					create(type, "", "");	// #
			identifier.set_source(source); return identifier;
		}
		else {
			SymbolIdentifier identifier = SymbolIdentifier.
					create(type, "ast", source.get_key());
			identifier.set_source(source); return identifier;
		}
	}
	
	/**
	 * Variable originated from C-intermediate representative code
	 * @param type
	 * @param source
	 * @return	CirDefaultValue			|--	default#cir_key
	 * 			CirIfStatement			|--	if#cir_key
	 * 			CirCaseStatement		|--	case#cir_key
	 * 			CirFunctionDefinition	|--	return#func_name
	 * 			CirReturnPoint			|--	return#func_name
	 * 			Otherwise				|--	cir#cir_key
	 * @throws Exception
	 */
	protected static SymbolIdentifier create_cirn(CType type, CirNode source) throws Exception {
		if(type == null) {
			throw new IllegalArgumentException("Invalid type: null");
		}
		else if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof CirDefaultValue) {
			SymbolIdentifier identifier = SymbolIdentifier.
					create(type, "default", source.get_node_id());
			identifier.set_source(source); return identifier;
		}
		else if(source instanceof CirIfStatement) {
			SymbolIdentifier identifier = SymbolIdentifier.
					create(type, "if", source.get_node_id());
			identifier.set_source(source); return identifier;
		}
		else if(source instanceof CirCaseStatement) {
			SymbolIdentifier identifier = SymbolIdentifier.
					create(type, "case", source.get_node_id());
			identifier.set_source(source); return identifier;
		}
		else if(source instanceof CirFunctionDefinition) {
			CirFunctionDefinition def = (CirFunctionDefinition) source;
			SymbolIdentifier identifier = SymbolIdentifier.
					create(type, "return", def.get_declarator().get_name());
			identifier.set_source(source); return identifier;
		}
		else if(source instanceof CirReturnPoint) {
			SymbolIdentifier identifier = SymbolIdentifier.
						create_cirn(type, source.function_of());
			identifier.set_source(source); return identifier;
		}
		else {
			SymbolIdentifier identifier = SymbolIdentifier.
					create(type, "cir", source.get_node_id());
			identifier.set_source(source); return identifier;
		}
	}
	
}
