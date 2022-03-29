package com.jcsa.jcmutest.mutant.uni2mutant.base;

import java.util.List;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.decl.AstDeclaration;
import com.jcsa.jcparse.lang.astree.decl.AstTypeName;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator.DeclaratorProduction;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstInitDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstInitDeclaratorList;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstName;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstFieldInitializer;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializer;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerBody;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerList;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstBasicExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstConstant;
import com.jcsa.jcparse.lang.astree.expr.base.AstIdExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstLiteral;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncrePostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstPointUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstPostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstArgumentList;
import com.jcsa.jcparse.lang.astree.expr.othr.AstArrayExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstCastExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstCommaExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConditionalExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConstExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstField;
import com.jcsa.jcparse.lang.astree.expr.othr.AstFieldExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstFunCallExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstParanthExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstSizeofExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstBreakStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstCaseStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstCompoundStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstContinueStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstDeclarationStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstDefaultStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstExpressionStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstGotoStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstIfStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstLabeledStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstReturnStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstSwitchStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirAddressExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirArithExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirBitwsExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirCastExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirComputeExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirConstExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirDeclarator;
import com.jcsa.jcparse.lang.irlang.expr.CirDefaultValue;
import com.jcsa.jcparse.lang.irlang.expr.CirDeferExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirField;
import com.jcsa.jcparse.lang.irlang.expr.CirFieldExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirIdentifier;
import com.jcsa.jcparse.lang.irlang.expr.CirImplicator;
import com.jcsa.jcparse.lang.irlang.expr.CirInitializerBody;
import com.jcsa.jcparse.lang.irlang.expr.CirLogicExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirNameExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirRelationExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirReturnPoint;
import com.jcsa.jcparse.lang.irlang.expr.CirStringLiteral;
import com.jcsa.jcparse.lang.irlang.expr.CirType;
import com.jcsa.jcparse.lang.irlang.expr.CirWaitExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirArgumentList;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirBegStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirBinAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCallStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseEndStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirDefaultStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirEndStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirGotoStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfEndStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIncreAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirInitAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirLabel;
import com.jcsa.jcparse.lang.irlang.stmt.CirLabelStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirReturnAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirSaveAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirTagStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirWaitAssignStatement;
import com.jcsa.jcparse.lang.irlang.unit.CirFunctionBody;
import com.jcsa.jcparse.lang.irlang.unit.CirFunctionDefinition;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * 	It implements the localization of state-storage used in UniAbstractState.	<br>
 * 	<br>
 * 	<code>
 * 	</code>
 * 	
 * 	@author yukimula
 *
 */
public final class UniStateLocalizer {
	
	/* singleton pattern */ /** constructor **/ private UniStateLocalizer() { }
	private static final UniStateLocalizer localizer = new UniStateLocalizer();
	
	/* storage-construction */
	/**
	 * 	It infers the class of the UniAbstractStore based on the cir_location.	<br>
	 * 	<br>
	 * 	<code>
	 * 		CirFunctionDefinition			-->	func_elem						<br>
	 * 		CirFunctionBody					-->	func_elem						<br>
	 * 		CirArgumentList					-->	args_elem						<br>
	 * 		CirLabel						-->	labl_elem						<br>
	 * 		CirField						-->	fiel_elem						<br>
	 * 		CirType							-->	type_elem						<br>
	 * 		CirExpression (assg.lvalue)		-->	cdef_expr						<br>
	 * 		CirExpression (assg.rvalue)		-->	used_expr						<br>
	 * 		CirExpression (if|case|bool)	-->	bool_expr						<br>
	 * 		CirExpression (arg_list.arg)	-->	argv_expr						<br>
	 * 		CirExpression (otherwise)		-->	used_expr						<br>
	 * 		CirCallStatement				-->	call_stmt						<br>
	 * 		CirWaitAssignStatement			-->	wait_stmt						<br>
	 * 		CirIfStatement					-->	ifte_stmt						<br>
	 * 		CirCaseStatement				-->	case_stmt						<br>
	 * 		CirReturnAssignStatement		-->	retr_stmt						<br>
	 * 		CirGotoStatement (return)		-->	retr_stmt						<br>
	 * 		CirGotoStatement				-->	goto_stmt						<br>
	 * 		CirAssignStatement				-->	assg_stmt						<br>
	 * 		CirBeg|EndStatement				-->	bend_stmt						<br>
	 * 		CirLabel|DefaultStatement		-->	labl_stmt						<br>
	 * 		CirIfEnd|CaseEndStatement		-->	conv_stmt						<br>
	 * 	</code>
	 * 	
	 * 	@param cir_location
	 * 	@return it infers the UniAbstractLType that the location corresponds to
	 */
	private	UniAbstractLType	infer_store_class(CirNode cir_location) {
		if(cir_location == null) {
			return null;
		}
		else if(cir_location instanceof CirExpression) {
			if(cir_location.get_parent() instanceof CirAssignStatement) {
				CirAssignStatement statement = (CirAssignStatement) cir_location.get_parent();
				if(statement.get_lvalue() == cir_location) {
					return UniAbstractLType.cdef_expr;
				}
				else {
					return UniAbstractLType.used_expr;
				}
			}
			else if(cir_location.get_parent() instanceof CirIfStatement ||
					cir_location.get_parent() instanceof CirCaseStatement || 
					SymbolFactory.is_bool(((CirExpression) cir_location).get_data_type())) {
				return UniAbstractLType.bool_expr;
			}
			else if(cir_location.get_parent() instanceof CirArgumentList) {
				return UniAbstractLType.args_elem;
			}
			else {
				return UniAbstractLType.used_expr;
			}
		}
		else if(cir_location instanceof CirStatement) {
			if(cir_location instanceof CirWaitAssignStatement) {
				return UniAbstractLType.wait_stmt;
			}
			else if(cir_location instanceof CirAssignStatement) {
				return UniAbstractLType.assg_stmt;
			}
			else if(cir_location instanceof CirReturnAssignStatement) {
				return UniAbstractLType.retr_stmt;
			}
			else if(cir_location instanceof CirIfStatement) {
				return UniAbstractLType.ifte_stmt;
			}
			else if(cir_location instanceof CirCaseStatement) {
				return UniAbstractLType.case_stmt;
			}
			else if(cir_location instanceof CirCallStatement) {
				return UniAbstractLType.call_stmt;
			}
			else if(cir_location instanceof CirGotoStatement) {
				if(cir_location.get_ast_source() instanceof AstReturnStatement) {
					return UniAbstractLType.retr_stmt;
				}
				else {
					return UniAbstractLType.goto_stmt;
				}
			}
			else if(cir_location instanceof CirBegStatement
					|| cir_location instanceof CirEndStatement) {
				return UniAbstractLType.bend_stmt;
			}
			else if(cir_location instanceof CirIfEndStatement
					|| cir_location instanceof CirCaseEndStatement) {
				return UniAbstractLType.conv_stmt;
			}
			else if(cir_location instanceof CirLabelStatement
					|| cir_location instanceof CirDefaultStatement) {
				return UniAbstractLType.labl_stmt;
			}
			else {
				return null;
			}
		}
		else {
			if(cir_location instanceof CirArgumentList) {
				return UniAbstractLType.args_elem;
			}
			else if(cir_location instanceof CirField) {
				return UniAbstractLType.fiel_elem;
			}
			else if(cir_location instanceof CirLabel) {
				return UniAbstractLType.labl_elem;
			}
			else if(cir_location instanceof CirType) {
				return UniAbstractLType.type_elem;
			}
			else if(cir_location instanceof CirFunctionDefinition
					|| cir_location instanceof CirFunctionBody) {
				return UniAbstractLType.func_elem;
			}
			else {
				return null;
			}
		}
	}
	/**
	 * @param ast_location	the abstract syntax location that the storage refers
	 * @param cir_location	the C-intermediate location for which storage refers
	 * @return				_store_class[ast_location:cir_location]
	 * @throws Exception
	 */
	private	UniAbstractStore	construct_storage(AstNode ast_location, CirNode cir_location) throws Exception {
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
			return new UniAbstractStore(this.infer_store_class(cir_location), 
					ast_location, cir_location, cir_location.execution_of());
		}
	}
	
	/* localization-methods */
	/**
	 * It localizes the real expression under the ast_location that it represents
	 * @param ast_location
	 * @return
	 */
	private	AstExpression		loc_ast_expression(AstNode ast_location) {
		if(ast_location == null) {
			return null;
		}
		else if(ast_location instanceof AstParanthExpression) {
			ast_location = ((AstParanthExpression) ast_location).get_sub_expression();
			return this.loc_ast_expression(ast_location);
		}
		else if(ast_location instanceof AstConstExpression) {
			ast_location = ((AstConstExpression) ast_location).get_expression();
			return this.loc_ast_expression(ast_location);
		}
		else if(ast_location instanceof AstInitDeclarator) {
			if(((AstInitDeclarator) ast_location).has_initializer()) {
				ast_location = ((AstInitDeclarator) ast_location).get_initializer();
			}
			else {
				ast_location = ((AstInitDeclarator) ast_location).get_declarator();
			}
			return this.loc_ast_expression(ast_location);
		}
		else if(ast_location instanceof AstInitializer) {
			if(((AstInitializer) ast_location).is_body()) {
				ast_location = ((AstInitializer) ast_location).get_body();
			}
			else {
				ast_location = ((AstInitializer) ast_location).get_expression();
			}
			return this.loc_ast_expression(ast_location);
		}
		else if(ast_location instanceof AstInitializerList) {
			ast_location = ast_location.get_parent();
			return this.loc_ast_expression(ast_location);
		}
		else if(ast_location instanceof AstDeclarator) {
			if(((AstDeclarator) ast_location).get_production() == DeclaratorProduction.identifier) {
				ast_location = ((AstDeclarator) ast_location).get_identifier();
			}
			else {
				ast_location = ((AstDeclarator) ast_location).get_declarator();
			}
			return this.loc_ast_expression(ast_location);
		}
		else if(ast_location instanceof AstName) {
			return null;
		}
		else if(ast_location instanceof AstExpression) {
			return (AstExpression) ast_location;
		}
		else {
			return null;
		}
	}
	/**
	 * It derives the list of CirNode(s) w.r.t. the given class under the abstract syntax location
	 * @param cir_tree
	 * @param ast_location
	 * @param cir_class
	 * @return
	 */
	private	List<CirNode>		loc_cir_locations(CirTree cir_tree, AstNode ast_location, Class<?> cir_class) {
		return cir_tree.get_localizer().get_cir_nodes(ast_location, cir_class);
	}
	/**
	 * @param cir_tree
	 * @param ast_location
	 * @param cir_class
	 * @param index
	 * @return the kth cir-node w.r.t. the abstract syntax location or null if it is out of range
	 */
	private	CirNode				loc_cir_location(CirTree cir_tree, AstNode ast_location, Class<?> cir_class, int index) {
		List<CirNode> cir_nodes = this.loc_cir_locations(cir_tree, ast_location, cir_class);
		if(index < 0 || index >= cir_nodes.size()) {
			return cir_nodes.get(index);
		}
		else {
			return null;
		}
	}
	/**
	 * @param cir_tree
	 * @param ast_location
	 * @param cir_class
	 * @param index
	 * @return the first cir-node w.r.t. the abstract syntax location or null if it is out of range
	 */
	private	CirNode				loc_cir_location(CirTree cir_tree, AstNode ast_location, Class<?> cir_class) {
		List<CirNode> cir_nodes = this.loc_cir_locations(cir_tree, ast_location, cir_class);
		for(CirNode cir_node : cir_nodes) {
			if(cir_node.execution_of() != null) {
				return cir_node;
			}
		}
		return null;
	}
	/**
	 * @param cir_tree		the C-intermediate representation tree to derive the locations
	 * @param ast_location	the abstract syntactic location to which the beg-statement is derived
	 * @return				the first statement to which node is in or null if unable to localize
	 * @throws Exception
	 */
	private	CirStatement		loc_beg_statement(CirTree cir_tree, AstNode ast_location) throws Exception {
		return cir_tree.get_localizer().beg_statement(ast_location);
	}
	/**
	 * @param cir_tree		the C-intermediate representation tree to derive the locations
	 * @param ast_location	the abstract syntactic location to which the beg-statement is derived
	 * @return				the first statement to which node is in or null if unable to localize
	 * @throws Exception
	 */
	private	CirStatement		loc_end_statement(CirTree cir_tree, AstNode ast_location) throws Exception {
		return cir_tree.get_localizer().end_statement(ast_location);
	}
	/**
	 * @param cir_tree		the C-intermediate representation tree to derive the locations
	 * @param ast_location	the abstract syntactic location to which the expression is derived
	 * @param cir_class		the class of CirExpression's sub-classes to which the expression is
	 * @return				the expression that represent the ast_location w.r.t. the cir_class
	 */
	private	CirExpression		loc_use_expression(CirTree cir_tree, AstNode ast_location, Class<?> cir_class) {
		List<CirNode> cir_nodes = this.loc_cir_locations(cir_tree, ast_location, cir_class);
		CirExpression expression = null;
		for(CirNode cir_node : cir_nodes) {
			if(cir_node instanceof CirExpression) {
				expression = (CirExpression) cir_node;
				if(expression.execution_of() != null) {
					break;
				}
			}
		}
		return expression;
	}
	
	/* CIR-based Localization */
	/**
	 * It automatically localizes the store-location of the Cir-based source
	 * @param source	the location where this store will be localized with
	 * @return			the store-location as best-matching with given input
	 * @throws Exception
	 */
	private	UniAbstractStore	parse_cir(CirNode source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source as null");
		}
		else if(source.execution_of() == null) {
			throw new IllegalArgumentException("No execution localized");
		}
		else if(source instanceof CirExpression) {
			return this.parse_cir_expression((CirExpression) source);
		}
		else if(source instanceof CirStatement) {
			return this.parse_cir_statement((CirStatement) source);
		}
		else {
			return this.parse_cir_element(source);
		}
	}
	/* CIR-Expression */
	private	UniAbstractStore	parse_cir_identifier(CirIdentifier source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		return this.construct_storage(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_declarator(CirDeclarator source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		return this.construct_storage(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_implicator(CirImplicator source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		if(ast_location == null) {
			throw new IllegalArgumentException("Invalid ast_location: null");
		}
		else if(ast_location instanceof AstSwitchStatement) {
			ast_location = ((AstSwitchStatement) ast_location).get_condition();
			ast_location = this.loc_ast_expression(ast_location);
		}
		else if(ast_location instanceof AstConditionalExpression) {
			if(source.get_parent() instanceof CirSaveAssignStatement) {
				CirSaveAssignStatement statement = (CirSaveAssignStatement) source.get_parent();
				if(statement.get_ast_source() == ast_location) {
					ast_location = statement.get_rvalue().get_ast_source();
				}
			}
		}
		else if(ast_location instanceof AstFunCallExpression) { }
		else if(ast_location instanceof AstIncrePostfixExpression) { } 
		else if(ast_location instanceof AstLogicBinaryExpression) {
			AstNode loperand = ((AstLogicBinaryExpression) ast_location).get_loperand();
			loperand = this.loc_ast_expression(loperand); 
			
			if(source.get_parent() instanceof CirSaveAssignStatement) {
				CirSaveAssignStatement statement = (CirSaveAssignStatement) source.get_parent();
				if(statement.get_ast_source() == ast_location) {
					ast_location = statement.get_rvalue().get_ast_source();
				}
			}
			else if(source.statement_of() instanceof CirIfStatement) {
				CirStatement statement = source.statement_of();
				if(statement.get_ast_source() == ast_location) {
					ast_location = loperand;
				}
			}
		}
		else {
			throw new IllegalArgumentException(ast_location.getClass().getSimpleName());
		}
		return this.construct_storage(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_retr_point(CirReturnPoint source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		return this.construct_storage(ast_location, source);
	}
	private UniAbstractStore	parse_cir_name_expr(CirNameExpression source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof CirDeclarator) {
			return this.parse_cir_declarator((CirDeclarator) source);
		}
		else if(source instanceof CirIdentifier) {
			return this.parse_cir_identifier((CirIdentifier) source);
		}
		else if(source instanceof CirImplicator) {
			return this.parse_cir_implicator((CirImplicator) source);
		}
		else if(source instanceof CirReturnPoint) {
			return this.parse_cir_retr_point((CirReturnPoint) source);
		}
		else {
			throw new IllegalArgumentException("Unsupported: " + source);
		}
	}
	private	UniAbstractStore	parse_cir_defer_expr(CirDeferExpression source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		if(ast_location == null) {
			ast_location = source.get_address().get_ast_source();
		}
		else if(ast_location instanceof AstArrayExpression) {}
		else if(ast_location instanceof AstPointUnaryExpression) {}
		else {
			throw new IllegalArgumentException(ast_location.getClass().getSimpleName());
		}
		return this.construct_storage(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_field_expr(CirFieldExpression source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		return this.construct_storage(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_const_expr(CirConstExpression source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		if(ast_location == null) {
			CirStatement statement = source.statement_of();
			ast_location = statement.get_ast_source();
		}
		else if(ast_location instanceof AstIdExpression) { }
		else if(ast_location instanceof AstConstant) { }
		else if(ast_location instanceof AstSizeofExpression) { }
		else if(ast_location instanceof AstExpressionStatement) {}
		else {
			throw new IllegalArgumentException(ast_location.getClass().getSimpleName());
		}
		return this.construct_storage(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_st_literal(CirStringLiteral source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		return this.construct_storage(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_defa_value(CirDefaultValue source) throws Exception {
		CirAssignStatement statement = (CirAssignStatement) source.statement_of();
		AstNode ast_location = statement.get_lvalue().get_ast_source();
		return this.construct_storage(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_addr_expr(CirAddressExpression source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		return this.construct_storage(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_cast_expr(CirCastExpression source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		return this.construct_storage(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_init_body(CirInitializerBody source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		return this.construct_storage(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_wait_expr(CirWaitExpression source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		return this.construct_storage(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_arith_expr(CirArithExpression source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		if(ast_location == null) {
			ast_location = source.get_parent().get_ast_source();
		}
		return this.construct_storage(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_bitws_expr(CirBitwsExpression source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		return this.construct_storage(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_logic_expr(CirLogicExpression source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		return this.construct_storage(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_relation_expr(CirRelationExpression source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		return this.construct_storage(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_compt_expr(CirComputeExpression source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source as null");
		}
		else if(source instanceof CirArithExpression) {
			return this.parse_cir_arith_expr((CirArithExpression) source);
		}
		else if(source instanceof CirBitwsExpression) {
			return this.parse_cir_bitws_expr((CirBitwsExpression) source);
		}
		else if(source instanceof CirLogicExpression) {
			return this.parse_cir_logic_expr((CirLogicExpression) source);
		}
		else if(source instanceof CirRelationExpression) {
			return this.parse_cir_relation_expr((CirRelationExpression) source);
		}
		else {
			throw new IllegalArgumentException("Unsupported: " + source);
		}
	}
	private	UniAbstractStore	parse_cir_expression(CirExpression source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source as null");
		}
		else if(source instanceof CirNameExpression) {
			return this.parse_cir_name_expr((CirNameExpression) source);
		}
		else if(source instanceof CirConstExpression) {
			return this.parse_cir_const_expr((CirConstExpression) source);
		}
		else if(source instanceof CirStringLiteral) {
			return this.parse_cir_st_literal((CirStringLiteral) source);
		}
		else if(source instanceof CirDefaultValue) {
			return this.parse_cir_defa_value((CirDefaultValue) source);
		}
		else if(source instanceof CirDeferExpression) {
			return this.parse_cir_defer_expr((CirDeferExpression) source);
		}
		else if(source instanceof CirFieldExpression) {
			return this.parse_cir_field_expr((CirFieldExpression) source);
		}
		else if(source instanceof CirAddressExpression) {
			return this.parse_cir_addr_expr((CirAddressExpression) source);
		}
		else if(source instanceof CirCastExpression) {
			return this.parse_cir_cast_expr((CirCastExpression) source);
		}
		else if(source instanceof CirInitializerBody) {
			return this.parse_cir_init_body((CirInitializerBody) source);
		}
		else if(source instanceof CirWaitExpression) {
			return this.parse_cir_wait_expr((CirWaitExpression) source);
		}
		else if(source instanceof CirComputeExpression) {
			return this.parse_cir_compt_expr((CirComputeExpression) source);
		}
		else {
			throw new IllegalArgumentException("Unsupported: " + source);
		}
	}
	/* CIR-Statements */
	private	UniAbstractStore	parse_cir_bin_assgn(CirBinAssignStatement source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		return this.construct_storage(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_inc_assgn(CirIncreAssignStatement source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		return this.construct_storage(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_ini_assgn(CirInitAssignStatement source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		return this.construct_storage(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_ret_assgn(CirReturnAssignStatement source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		return this.construct_storage(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_wat_assgn(CirWaitAssignStatement source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		return this.construct_storage(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_sav_assgn(CirSaveAssignStatement source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		if(ast_location == null) {
			throw new IllegalArgumentException("Invalid ast_location: null");
		}
		else if(ast_location instanceof AstConditionalExpression) {
			ast_location = source.get_rvalue().get_ast_source();
		}
		else if(ast_location instanceof AstLogicBinaryExpression) {
			ast_location = source.get_rvalue().get_ast_source();
		}
		else if(ast_location instanceof AstIncrePostfixExpression) { }
		else if(ast_location instanceof AstSwitchStatement) { }
		else {
			throw new IllegalArgumentException(ast_location.getClass().getSimpleName());
		}
		return this.construct_storage(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_assg_stmt(CirAssignStatement source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source as null");
		}
		else if(source instanceof CirBinAssignStatement) {
			return this.parse_cir_bin_assgn((CirBinAssignStatement) source);
		}
		else if(source instanceof CirIncreAssignStatement) {
			return this.parse_cir_inc_assgn((CirIncreAssignStatement) source);
		}
		else if(source instanceof CirInitAssignStatement) {
			return this.parse_cir_ini_assgn((CirInitAssignStatement) source);
		}
		else if(source instanceof CirReturnAssignStatement) {
			return this.parse_cir_ret_assgn((CirReturnAssignStatement) source);
		}
		else if(source instanceof CirWaitAssignStatement) {
			return this.parse_cir_wat_assgn((CirWaitAssignStatement) source);
		}
		else if(source instanceof CirSaveAssignStatement) {
			return this.parse_cir_sav_assgn((CirSaveAssignStatement) source);
		}
		else {
			throw new IllegalArgumentException("Unsupported: " + source);
		}
	}
	private	UniAbstractStore	parse_cir_ifte_stmt(CirIfStatement source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		if(ast_location == null) {
			throw new IllegalArgumentException("Invalid ast_location as null");
		}
		else if(ast_location instanceof AstConditionalExpression || 
				ast_location instanceof AstLogicBinaryExpression ||
				ast_location instanceof AstIfStatement) { }
		else if(ast_location instanceof AstWhileStatement || 
				ast_location instanceof AstDoWhileStatement || 
				ast_location instanceof AstForStatement) { }
		else {
			throw new IllegalArgumentException(ast_location.getClass().getSimpleName());
		}
		return this.construct_storage(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_case_stmt(CirCaseStatement source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		return this.construct_storage(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_call_stmt(CirCallStatement source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		return this.construct_storage(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_goto_stmt(CirGotoStatement source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		if(ast_location == null) {
			CirExecution execution = source.execution_of();
			execution = execution.get_ou_flow(0).get_target();
			ast_location = execution.get_statement().get_ast_source();
		}
		else if(ast_location instanceof AstBreakStatement || 
				ast_location instanceof AstContinueStatement || 
				ast_location instanceof AstGotoStatement ||
				ast_location instanceof AstReturnStatement) { /* dirt-goto */ }
		else if(ast_location instanceof AstIfStatement ||
				ast_location instanceof AstForStatement ||
				ast_location instanceof AstWhileStatement ||
				ast_location instanceof AstSwitchStatement) { /* loop-goto */ }
		else {
			throw new IllegalArgumentException(ast_location.getClass().getSimpleName());
		}
		return this.construct_storage(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_beg_stmt(CirBegStatement source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		return this.construct_storage(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_end_stmt(CirEndStatement source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		return this.construct_storage(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_default_stmt(CirDefaultStatement source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		return this.construct_storage(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_label_stmt(CirLabelStatement source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		return this.construct_storage(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_if_end_stmt(CirIfEndStatement source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		return this.construct_storage(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_case_end_stmt(CirCaseEndStatement source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		return this.construct_storage(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_tags_stmt(CirTagStatement source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source as null");
		}
		else if(source instanceof CirBegStatement) {
			return this.parse_cir_beg_stmt((CirBegStatement) source);
		}
		else if(source instanceof CirEndStatement) {
			return this.parse_cir_end_stmt((CirEndStatement) source);
		}
		else if(source instanceof CirLabelStatement) {
			return this.parse_cir_label_stmt((CirLabelStatement) source);
		}
		else if(source instanceof CirDefaultStatement) {
			return this.parse_cir_default_stmt((CirDefaultStatement) source);
		}
		else if(source instanceof CirIfEndStatement) {
			return this.parse_cir_if_end_stmt((CirIfEndStatement) source);
		}
		else if(source instanceof CirCaseEndStatement) {
			return this.parse_cir_case_end_stmt((CirCaseEndStatement) source);
		}
		else {
			throw new IllegalArgumentException("Unsupported: " + source);
		}
	}
	private	UniAbstractStore	parse_cir_statement(CirStatement source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source as null");
		}
		else if(source instanceof CirAssignStatement) {
			return this.parse_cir_assg_stmt((CirAssignStatement) source);
		}
		else if(source instanceof CirIfStatement) {
			return this.parse_cir_ifte_stmt((CirIfStatement) source);
		}
		else if(source instanceof CirCaseStatement) {
			return this.parse_cir_case_stmt((CirCaseStatement) source);
		}
		else if(source instanceof CirGotoStatement) {
			return this.parse_cir_goto_stmt((CirGotoStatement) source);
		}
		else if(source instanceof CirCallStatement) {
			return this.parse_cir_call_stmt((CirCallStatement) source);
		}
		else if(source instanceof CirTagStatement) {
			return this.parse_cir_tags_stmt((CirTagStatement) source);
		}
		else {
			throw new IllegalArgumentException("Unsupported: " + source);
		}
	}
	/* CIR-Elementals */
	private	UniAbstractStore	parse_cir_args_list(CirArgumentList source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		if(ast_location == null) {
			ast_location = source.get_parent().get_ast_source();
		}
		return this.construct_storage(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_field(CirField source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		return this.construct_storage(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_label(CirLabel source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		return this.construct_storage(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_type(CirType source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		return this.construct_storage(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_func_def(CirFunctionDefinition source) throws Exception {
		return this.construct_storage(source.get_ast_source(), source);
	}
	private	UniAbstractStore	parse_cir_func_body(CirFunctionBody source) throws Exception {
		return this.parse_cir(source.get_parent());
	}
	private	UniAbstractStore	parse_cir_element(CirNode source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source as null");
		}
		else if(source instanceof CirArgumentList) {
			return this.parse_cir_args_list((CirArgumentList) source);
		}
		else if(source instanceof CirField) {
			return this.parse_cir_field((CirField) source);
		}
		else if(source instanceof CirLabel) {
			return this.parse_cir_label((CirLabel) source);
		}
		else if(source instanceof CirType) {
			return this.parse_cir_type((CirType) source);
		}
		else if(source instanceof CirFunctionBody) {
			return this.parse_cir_func_body((CirFunctionBody) source);
		}
		else if(source instanceof CirFunctionDefinition) {
			return this.parse_cir_func_def((CirFunctionDefinition) source);
		}
		else {
			throw new IllegalArgumentException("Unsupported: " + source);
		}
	}
	
	/* AST-Based Localize */
	/**
	 * It generates the storage-location that best-matches with the input source.
	 * @param source
	 * @return	the storage-location to which UniAbstractStore best matches with.
	 * @throws Exception
	 */
	private	UniAbstractStore	parse_ast(CirTree cir_tree, AstNode source) throws Exception {
		if(cir_tree == null) {
			throw new IllegalArgumentException("Invalid cir_tree: null");
		}
		else if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof AstExpression) {
			return this.parse_ast_expression(cir_tree, (AstExpression) source);
		}
		else if(source instanceof AstStatement) {
			return this.parse_ast_statement(cir_tree, (AstStatement) source);
		}
		else {
			return this.parse_ast_element(cir_tree, source);
		}
	}
	/* AST-Expression */
	private	UniAbstractStore	parse_ast_id_expr(CirTree cir_tree, AstIdExpression source) throws Exception {
		CirNode cir_location = this.loc_use_expression(cir_tree, source, CirExpression.class);
		return this.construct_storage(source, cir_location);
	}
	private	UniAbstractStore	parse_ast_constant(CirTree cir_tree, AstConstant source) throws Exception {
		CirNode cir_location = this.loc_use_expression(cir_tree, source, CirConstExpression.class);
		return this.construct_storage(source, cir_location);
	}
	private	UniAbstractStore	parse_ast_literal(CirTree cir_tree, AstLiteral source) throws Exception {
		CirNode cir_location = this.loc_use_expression(cir_tree, source, CirStringLiteral.class);
		return this.construct_storage(source, cir_location);
	}
	private	UniAbstractStore	parse_ast_base_expr(CirTree cir_tree, AstBasicExpression source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof AstIdExpression) {
			return this.parse_ast_id_expr(cir_tree, (AstIdExpression) source);
		}
		else if(source instanceof AstConstant) {
			return this.parse_ast_constant(cir_tree, (AstConstant) source);
		}
		else if(source instanceof AstLiteral) {
			return this.parse_ast_literal(cir_tree, (AstLiteral) source);
		}
		else {
			throw new IllegalArgumentException(source.getClass().getSimpleName());
		}
	}
	private	UniAbstractStore	parse_ast_array_expr(CirTree cir_tree, AstArrayExpression source) throws Exception {
		CirNode cir_location = this.loc_use_expression(cir_tree, source, CirDeferExpression.class);
		return this.construct_storage(source, cir_location);
	}
	private	UniAbstractStore	parse_ast_cast_expr(CirTree cir_tree, AstCastExpression source) throws Exception {
		CirNode cir_location = this.loc_use_expression(cir_tree, source, CirCastExpression.class);
		return this.construct_storage(source, cir_location);
	}
	private	UniAbstractStore	parse_ast_comma_expr(CirTree cir_tree, AstCommaExpression source) throws Exception {
		return this.parse_ast(cir_tree, source.get_expression(source.number_of_arguments() - 1));
	}
	private	UniAbstractStore	parse_ast_cond_expr(CirTree cir_tree, AstConditionalExpression source) throws Exception {
		CirNode cir_location = this.loc_cir_location(cir_tree, source, CirImplicator.class, 2);
		return this.construct_storage(source, cir_location);
	}
	private	UniAbstractStore	parse_ast_const_expr(CirTree cir_tree, AstConstExpression source) throws Exception {
		return this.parse_ast(cir_tree, source.get_expression());
	}
	private	UniAbstractStore	parse_ast_paranth_expr(CirTree cir_tree, AstParanthExpression source) throws Exception {
		return this.parse_ast(cir_tree, source.get_sub_expression());
	}
	private	UniAbstractStore	parse_ast_field_expr(CirTree cir_tree, AstFieldExpression source) throws Exception {
		CirNode cir_location = this.loc_use_expression(cir_tree, source, CirFieldExpression.class);
		return this.construct_storage(source, cir_location);
	}
	private	UniAbstractStore	parse_ast_sizeof_expr(CirTree cir_tree, AstSizeofExpression source) throws Exception {
		CirNode cir_location = this.loc_use_expression(cir_tree, source, CirConstExpression.class);
		return this.construct_storage(source, cir_location);
	}
	private	UniAbstractStore	parse_ast_fun_call_expr(CirTree cir_tree, AstFunCallExpression source) throws Exception {
		CirNode cir_location = this.loc_use_expression(cir_tree, source, CirWaitExpression.class);
		return this.construct_storage(source, cir_location);
	}
	private	UniAbstractStore	parse_ast_unary_expr(CirTree cir_tree, AstUnaryExpression source) throws Exception {
		COperator operator = source.get_operator().get_operator(); CirNode cir_location;
		switch(operator) {
		case positive:		return this.parse_ast(cir_tree, source.get_operand());
		case negative:		cir_location = this.loc_use_expression(cir_tree, source, CirArithExpression.class);	break;
		case bit_not:		cir_location = this.loc_use_expression(cir_tree, source, CirBitwsExpression.class);	break;
		case logic_not:		cir_location = this.loc_use_expression(cir_tree, source, CirLogicExpression.class);	break;
		case address_of:	cir_location = this.loc_use_expression(cir_tree, source, CirAddressExpression.class);	break;
		case dereference:	cir_location = this.loc_use_expression(cir_tree, source, CirDeferExpression.class);	break;
		case increment:
		case decrement:
		{
			CirAssignStatement statement = (CirAssignStatement) this.loc_cir_location(cir_tree, source, CirIncreAssignStatement.class);
			cir_location = statement.get_rvalue(); break;
		}
		default:	throw new IllegalArgumentException("Invalid operator: " + operator);
		}
		return this.construct_storage(source, cir_location);
	}
	private	UniAbstractStore	parse_ast_postfix_expr(CirTree cir_tree, AstPostfixExpression source) throws Exception {
		COperator operator = source.get_operator().get_operator(); CirNode cir_location;
		switch(operator) {
		case increment:
		case decrement:	
		{
			cir_location = this.loc_use_expression(cir_tree, source, CirImplicator.class); break;
		}
		default:	throw new IllegalArgumentException("Invalid operator: " + operator);
		}
		return this.construct_storage(source, cir_location);
	}
	private	UniAbstractStore	parse_ast_binary_expr(CirTree cir_tree, AstBinaryExpression source) throws Exception {
		COperator operator = source.get_operator().get_operator();	CirNode cir_location;
		switch(operator) {
		case arith_add:
		case arith_sub:
		case arith_mul:
		case arith_div:
		case arith_mod:	
		{
			cir_location = this.loc_use_expression(cir_tree, source, CirArithExpression.class); break;
		}
		case bit_and:
		case bit_or:
		case bit_xor:
		case left_shift:
		case righ_shift:
		{
			cir_location = this.loc_use_expression(cir_tree, source, CirBitwsExpression.class); break;
		}
		case logic_and:
		case logic_or:
		{
			cir_location = this.loc_cir_location(cir_tree, source, CirImplicator.class, 2); break;
		}
		case greater_tn:
		case greater_eq:
		case smaller_tn:
		case smaller_eq:
		case not_equals:
		case equal_with:
		{
			cir_location = this.loc_use_expression(cir_tree, source, CirRelationExpression.class); break;
		}
		case assign:
		{
			cir_location = this.loc_cir_location(cir_tree, source, CirBinAssignStatement.class); break;
		}
		case arith_add_assign:
		case arith_sub_assign:
		case arith_mul_assign:
		case arith_div_assign:
		case arith_mod_assign:
		{
			cir_location = this.loc_cir_location(cir_tree, source, CirBinAssignStatement.class); break;
		}
		case bit_and_assign:
		case bit_or_assign:
		case bit_xor_assign:
		case left_shift_assign:
		case righ_shift_assign:
		{
			cir_location = this.loc_cir_location(cir_tree, source, CirBinAssignStatement.class); break;
		}
		default:	throw new IllegalArgumentException("Invalid operator: null");
		}
		return this.construct_storage(source, cir_location);
	}
	private	UniAbstractStore	parse_ast_initializer_body(CirTree cir_tree, AstInitializerBody source) throws Exception {
		CirNode cir_location = this.loc_cir_location(cir_tree, source, CirInitializerBody.class);
		return this.construct_storage(source, cir_location);
	}
	private	UniAbstractStore	parse_ast_expression(CirTree cir_tree, AstExpression source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof AstBasicExpression) {
			return this.parse_ast_base_expr(cir_tree, (AstBasicExpression) source);
		}
		else if(source instanceof AstCastExpression) {
			return this.parse_ast_cast_expr(cir_tree, (AstCastExpression) source);
		}
		else if(source instanceof AstArrayExpression) {
			return this.parse_ast_array_expr(cir_tree, (AstArrayExpression) source);
		}
		else if(source instanceof AstCommaExpression) {
			return this.parse_ast_comma_expr(cir_tree, (AstCommaExpression) source);
		}
		else if(source instanceof AstConditionalExpression) {
			return this.parse_ast_cond_expr(cir_tree, (AstConditionalExpression) source);
		}
		else if(source instanceof AstParanthExpression) {
			return this.parse_ast_paranth_expr(cir_tree, (AstParanthExpression) source);
		}
		else if(source instanceof AstConstExpression) {
			return this.parse_ast_const_expr(cir_tree, (AstConstExpression) source);
		}
		else if(source instanceof AstUnaryExpression) {
			return this.parse_ast_unary_expr(cir_tree, (AstUnaryExpression) source);
		}
		else if(source instanceof AstPostfixExpression) {
			return this.parse_ast_postfix_expr(cir_tree, (AstPostfixExpression) source);
		}
		else if(source instanceof AstBinaryExpression) {
			return this.parse_ast_binary_expr(cir_tree, (AstBinaryExpression) source);
		}
		else if(source instanceof AstFunCallExpression) {
			return this.parse_ast_fun_call_expr(cir_tree, (AstFunCallExpression) source);
		}
		else if(source instanceof AstSizeofExpression) {
			return this.parse_ast_sizeof_expr(cir_tree, (AstSizeofExpression) source);
		}
		else if(source instanceof AstFieldExpression) {
			return this.parse_ast_field_expr(cir_tree, (AstFieldExpression) source);
		}
		else if(source instanceof AstInitializerBody) {
			return this.parse_ast_initializer_body(cir_tree, (AstInitializerBody) source);
		}
		else {
			throw new IllegalArgumentException(source.getClass().getSimpleName());
		}
	}
	/* AST-Statements */
	private	UniAbstractStore	parse_ast_break_stmt(CirTree cir_tree, AstBreakStatement source) throws Exception {
		CirNode cir_location = this.loc_cir_location(cir_tree, source, CirGotoStatement.class);
		return this.construct_storage(source, cir_location);
	}
	private	UniAbstractStore	parse_ast_continue_stmt(CirTree cir_tree, AstContinueStatement source) throws Exception {
		CirNode cir_location = this.loc_cir_location(cir_tree, source, CirGotoStatement.class);
		return this.construct_storage(source, cir_location);
	}
	private	UniAbstractStore	parse_ast_goto_stmt(CirTree cir_tree, AstGotoStatement source) throws Exception {
		CirNode cir_location = this.loc_cir_location(cir_tree, source, CirGotoStatement.class);
		return this.construct_storage(source, cir_location);
	}
	private	UniAbstractStore	parse_ast_return_stmt(CirTree cir_tree, AstReturnStatement source) throws Exception {
		CirNode cir_location;
		if(source.has_expression()) {
			cir_location = this.loc_cir_location(cir_tree, source, CirReturnAssignStatement.class);
		}
		else {
			cir_location = this.loc_cir_location(cir_tree, source, CirGotoStatement.class);
		}
		return this.construct_storage(source, cir_location);
	}
	private	UniAbstractStore	parse_ast_compound_stmt(CirTree cir_tree, AstCompoundStatement source) throws Exception {
		CirNode cir_location = this.loc_beg_statement(cir_tree, source);
		return this.construct_storage(source, cir_location);
	}
	private	UniAbstractStore	parse_ast_declaration_stmt(CirTree cir_tree, AstDeclarationStatement source) throws Exception {
		return this.parse_ast(cir_tree, source.get_declaration());
	}
	private	UniAbstractStore	parse_ast_expression_stmt(CirTree cir_tree, AstExpressionStatement source) throws Exception {
		if(source.has_expression()) {
			return this.parse_ast(cir_tree, source.get_expression());
		}
		else {
			CirNode cir_location = this.loc_beg_statement(cir_tree, source);
			return this.construct_storage(source, cir_location);
		}
	}
	private	UniAbstractStore	parse_ast_if_stmt(CirTree cir_tree, AstIfStatement source) throws Exception {
		CirNode cir_location = this.loc_cir_location(cir_tree, source, CirIfStatement.class);
		return this.construct_storage(source, cir_location);
	}
	private	UniAbstractStore	parse_ast_switch_stmt(CirTree cir_tree, AstIfStatement source) throws Exception {
		CirNode cir_location = this.loc_cir_location(cir_tree, source, CirSaveAssignStatement.class);
		return this.construct_storage(source, cir_location);
	}
	private	UniAbstractStore	parse_ast_while_stmt(CirTree cir_tree, AstWhileStatement source) throws Exception {
		CirNode cir_location = this.loc_cir_location(cir_tree, source, CirIfStatement.class);
		return this.construct_storage(source, cir_location);
	}
	private	UniAbstractStore	parse_ast_do_while_stmt(CirTree cir_tree, AstDoWhileStatement source) throws Exception {
		CirNode cir_location = this.loc_cir_location(cir_tree, source, CirIfStatement.class);
		return this.construct_storage(source, cir_location);
	}
	private	UniAbstractStore	parse_ast_for_stmt(CirTree cir_tree, AstForStatement source) throws Exception {
		CirNode cir_location = this.loc_cir_location(cir_tree, source, CirIfStatement.class);
		return this.construct_storage(source, cir_location);
	}
	private	UniAbstractStore	parse_ast_case_stmt(CirTree cir_tree, AstCaseStatement source) throws Exception {
		CirNode cir_location = this.loc_cir_location(cir_tree, source, CirCaseStatement.class);
		return this.construct_storage(source, cir_location);
	}
	private	UniAbstractStore	parse_ast_default_stmt(CirTree cir_tree, AstDefaultStatement source) throws Exception {
		CirNode cir_location = this.loc_cir_location(cir_tree, source, CirDefaultStatement.class);
		return this.construct_storage(source, cir_location);
	}
	private	UniAbstractStore	parse_ast_labeled_stmt(CirTree cir_tree, AstLabeledStatement source) throws Exception {
		CirNode cir_location = this.loc_cir_location(cir_tree, source, CirLabelStatement.class);
		return this.construct_storage(source, cir_location);
	}
	private	UniAbstractStore	parse_ast_statement(CirTree cir_tree, AstStatement source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof AstBreakStatement) {
			return this.parse_ast_break_stmt(cir_tree, (AstBreakStatement) source);
		}
		else if(source instanceof AstContinueStatement) {
			return this.parse_ast_continue_stmt(cir_tree, (AstContinueStatement) source);
		}
		else if(source instanceof AstGotoStatement) {
			return this.parse_ast_goto_stmt(cir_tree, (AstGotoStatement) source);
		}
		else if(source instanceof AstReturnStatement) {
			return this.parse_ast_return_stmt(cir_tree, (AstReturnStatement) source);
		}
		else if(source instanceof AstExpressionStatement) {
			return this.parse_ast_expression_stmt(cir_tree, (AstExpressionStatement) source);
		}
		else if(source instanceof AstDeclarationStatement) {
			return this.parse_ast_declaration_stmt(cir_tree, (AstDeclarationStatement) source);
		}
		else if(source instanceof AstCompoundStatement) {
			return this.parse_ast_compound_stmt(cir_tree, (AstCompoundStatement) source);
		}
		else if(source instanceof AstIfStatement) {
			return this.parse_ast_if_stmt(cir_tree, (AstIfStatement) source);
		}
		else if(source instanceof AstSwitchStatement) {
			return this.parse_ast_switch_stmt(cir_tree, (AstIfStatement) source);
		}
		else if(source instanceof AstForStatement) {
			return this.parse_ast_for_stmt(cir_tree, (AstForStatement) source);
		}
		else if(source instanceof AstWhileStatement) {
			return this.parse_ast_while_stmt(cir_tree, (AstWhileStatement) source);
		}
		else if(source instanceof AstDoWhileStatement) {
			return this.parse_ast_do_while_stmt(cir_tree, (AstDoWhileStatement) source);
		}
		else if(source instanceof AstCaseStatement) {
			return this.parse_ast_case_stmt(cir_tree, (AstCaseStatement) source);
		}
		else if(source instanceof AstDefaultStatement) {
			return this.parse_ast_default_stmt(cir_tree, (AstDefaultStatement) source);
		}
		else if(source instanceof AstLabeledStatement) {
			return this.parse_ast_labeled_stmt(cir_tree, (AstLabeledStatement) source);
		}
		else {
			throw new IllegalArgumentException(source.getClass().getSimpleName());
		}
	}
	/* AST-Elementals */
	private	UniAbstractStore	parse_ast_args_list(CirTree cir_tree, AstArgumentList source) throws Exception {
		CirNode cir_location = this.loc_cir_location(cir_tree, source, CirArgumentList.class);
		return this.construct_storage(source, cir_location);
	}
	private	UniAbstractStore	parse_ast_declaration(CirTree cir_tree, AstDeclaration source) throws Exception {
		if(source.has_declarator_list()) {
			return this.parse_ast(cir_tree, source.get_declarator_list());
		}
		else {
			return this.parse_cir(this.loc_beg_statement(cir_tree, source));
		}
	}
	private	UniAbstractStore	parse_ast_init_declarator_list(CirTree 
			cir_tree, AstInitDeclaratorList source) throws Exception {
		return this.parse_ast(cir_tree, source.get_init_declarator(0));
	}
	private	UniAbstractStore	parse_ast_init_declarator(CirTree cir_tree, AstInitDeclarator source) throws Exception {
		CirNode cir_location = this.loc_cir_location(cir_tree, source, CirInitAssignStatement.class);
		return this.construct_storage(source, cir_location);
	}
	private	UniAbstractStore	parse_ast_declarator(CirTree cir_tree, AstDeclarator source) throws Exception {
		if(source.get_production() == DeclaratorProduction.identifier) {
			return this.parse_ast(cir_tree, source.get_identifier());
		}
		else {
			return this.parse_ast(cir_tree, source.get_declarator());
		}
	}
	private	UniAbstractStore	parse_ast_name(CirTree cir_tree, AstName source) throws Exception {
		AstDeclarator declarator = (AstDeclarator) source.get_parent();
		CirNode cir_location = this.loc_cir_location(cir_tree, declarator, CirDeclarator.class);
		return this.construct_storage(declarator, cir_location);
	}
	private	UniAbstractStore	parse_ast_initializer(CirTree cir_tree, AstInitializer source) throws Exception {
		if(source.is_body()) {
			return this.parse_ast(cir_tree, source.get_body());
		}
		else {
			return this.parse_ast(cir_tree, source.get_expression());
		}
	}
	private	UniAbstractStore	parse_ast_initializer_list(CirTree cir_tree, AstInitializerList source) throws Exception {
		return this.parse_ast(cir_tree, source.get_parent());
	}
	private	UniAbstractStore	parse_ast_field_initializer(CirTree cir_tree, AstFieldInitializer source) throws Exception {
		return this.parse_ast(cir_tree, source.get_initializer());
	}
	private	UniAbstractStore	parse_ast_type_name(CirTree cir_tree, AstTypeName source) throws Exception {
		CirNode cir_location = this.loc_cir_location(cir_tree, source, CirType.class);
		return this.construct_storage(source, cir_location);
	}
	private	UniAbstractStore	parse_ast_field(CirTree cir_tree, AstField source) throws Exception {
		CirNode cir_location = this.loc_cir_location(cir_tree, source, CirField.class);
		return this.construct_storage(source, cir_location);
	}
	private	UniAbstractStore	parse_ast_element(CirTree cir_tree, AstNode source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof AstArgumentList) {
			return this.parse_ast_args_list(cir_tree, (AstArgumentList) source);
		}
		else if(source instanceof AstDeclaration) {
			return this.parse_ast_declaration(cir_tree, (AstDeclaration) source);
		}
		else if(source instanceof AstInitDeclaratorList) {
			return this.parse_ast_init_declarator_list(cir_tree, (AstInitDeclaratorList) source);
		}
		else if(source instanceof AstInitDeclarator) {
			return this.parse_ast_init_declarator(cir_tree, (AstInitDeclarator) source);
		}
		else if(source instanceof AstDeclarator) {
			return this.parse_ast_declarator(cir_tree, (AstDeclarator) source);
		}
		else if(source instanceof AstName) {
			return this.parse_ast_name(cir_tree, (AstName) source);
		}
		else if(source instanceof AstField) {
			return this.parse_ast_field(cir_tree, (AstField) source);
		}
		else if(source instanceof AstTypeName) {
			return this.parse_ast_type_name(cir_tree, (AstTypeName) source);
		}
		else if(source instanceof AstInitializer) {
			return this.parse_ast_initializer(cir_tree, (AstInitializer) source);
		}
		else if(source instanceof AstInitializerBody) {
			return this.parse_ast_initializer_body(cir_tree, (AstInitializerBody) source);
		}
		else if(source instanceof AstInitializerList) {
			return this.parse_ast_initializer_list(cir_tree, (AstInitializerList) source);
		}
		else if(source instanceof AstFieldInitializer) {
			return this.parse_ast_field_initializer(cir_tree, (AstFieldInitializer) source);
		}
		else {
			throw new IllegalArgumentException(source.getClass().getSimpleName());
		}
	}
	
	/* statement-localize */
	/**
	 * It localizes the CIR-based location to a best-matched point in the state
	 * @param cir_location	the location where this store will be localized with
	 * @return				the store-location as best-matching with given input
	 * @throws Exception
	 */
	protected static UniAbstractStore	parse_cir_node(CirNode cir_location) throws Exception {
		return localizer.parse_cir(cir_location);
	}
	/**
	 * It generates the storage-location that best-matches with the input source.
	 * @param source
	 * @return	the storage-location to which UniAbstractStore best matches with.
	 * @throws Exception
	 */
	protected static UniAbstractStore	parse_ast_node(CirTree cir_tree, AstNode source) throws Exception {
		return localizer.parse_ast(cir_tree, source);
	}
	/**
	 * @param cir_tree
	 * @param source
	 * @return the first statement as location to execute the source
	 * @throws Exception
	 */
	protected static UniAbstractStore 	parse_beg_stmt(CirTree cir_tree, AstNode source) throws Exception {
		if(cir_tree == null) {
			throw new IllegalArgumentException("Invalid cir_tree: null");
		}
		else if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else {
			return localizer.construct_storage(source, localizer.loc_beg_statement(cir_tree, source));
		}
	}
	/**
	 * @param cir_tree
	 * @param source
	 * @return the final statement as location to execute the source
	 * @throws Exception
	 */
	protected static UniAbstractStore 	parse_end_stmt(CirTree cir_tree, AstNode source) throws Exception {
		if(cir_tree == null) {
			throw new IllegalArgumentException("Invalid cir_tree: null");
		}
		else if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else {
			return localizer.construct_storage(source, localizer.loc_end_statement(cir_tree, source));
		}
	}
	
}
