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
import com.jcsa.jcparse.lang.astree.expr.base.AstConstant;
import com.jcsa.jcparse.lang.astree.expr.base.AstIdExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncrePostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstPointUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstArgumentList;
import com.jcsa.jcparse.lang.astree.expr.othr.AstArrayExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConditionalExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConstExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstField;
import com.jcsa.jcparse.lang.astree.expr.othr.AstFunCallExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstParanthExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstSizeofExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstBreakStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstContinueStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstExpressionStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstGotoStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstIfStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstReturnStatement;
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
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * 	It implements the localization from AstNode or CirNode to UniAbstractStore.
 * 	
 * 	@author yukimula
 *
 */
final class UniStoreLocalizer {
	
	/* singleton mode */  /** constructor **/  private UniStoreLocalizer() { }
	private static final UniStoreLocalizer localizer = new UniStoreLocalizer();
	
	/* localization-interface */
	/**
	 * @param cir_location
	 * @return it transforms the cir-location to the store-localization
	 * @throws Exception
	 */
	protected static UniAbstractStore parse(CirNode cir_location) throws Exception {
		return localizer.parse_cir(cir_location);
	}
	/**
	 * @param exe_location
	 * @return it transforms the statement to the store-localization
	 * @throws Exception
	 */
	protected static UniAbstractStore parse(CirExecution exe_location) throws Exception {
		if(exe_location == null) {
			throw new IllegalArgumentException("Invalid exe_location: null");
		}
		else {
			return localizer.parse_cir(exe_location.get_statement());
		}
	}
	/**
	 * @param cir_tree
	 * @param ast_location
	 * @return	it transforms the ast-location using C-intermediate representative tree
	 * @throws Exception
	 */
	protected static UniAbstractStore parse(CirTree cir_tree, AstNode ast_location) throws Exception {
		if(cir_tree == null) {
			throw new IllegalArgumentException("Invalid cir_tree: null");
		}
		else if(ast_location == null) {
			throw new IllegalArgumentException("Invalid ast_location: null");
		}
		else { return localizer.parse_ast(cir_tree, ast_location); }
	}
	
	/* localization methods */
	/**
	 * 	It derives the real child being represented by the input location.<br>
	 * 	<code>
	 * 	AstParanthExpression		--> derive(sub_expression)			<br>
	 * 	AstConstExpression			-->	derive(sub_expression)			<br>
	 * 	AstInitializer				-->	derive(body|expression)			<br>
	 * 	AstFieldInitializer			-->	derive(initializer)				<br>
	 * 	AstDeclarator				--> derive*(name)					<br>
	 * 	AstInitializerBody			-->	initializer_list				<br>
	 * 	AstExpressionStatement		--> derive*(expression)				<br>
	 * 	otherwise					--> itself							<br>
	 * 	</code>
	 * 	
	 *	@param ast_location
	 * 	@return
	 */
	private	AstNode				local_ast_child(AstNode ast_location) {
		if(ast_location instanceof AstParanthExpression) {
			ast_location = ((AstParanthExpression) ast_location).get_sub_expression();
			return this.local_ast_child(ast_location);
		}
		else if(ast_location instanceof AstConstExpression) {
			ast_location = ((AstConstExpression) ast_location).get_expression();
			return this.local_ast_child(ast_location);
		}
		else if(ast_location instanceof AstExpressionStatement) {
			if(((AstExpressionStatement) ast_location).has_expression()) {
				ast_location = ((AstExpressionStatement) ast_location).get_expression();
				return this.local_ast_child(ast_location);
			}
			else {
				return ast_location;
			}
		}
		else if(ast_location instanceof AstInitializer) {
			if(((AstInitializer) ast_location).is_body()) {
				ast_location = ((AstInitializer) ast_location).get_body();
			}
			else {
				ast_location = ((AstInitializer) ast_location).get_expression();
			}
			return this.local_ast_child(ast_location);
		}
		else if(ast_location instanceof AstFieldInitializer) {
			ast_location = ((AstFieldInitializer) ast_location).get_initializer();
			return this.local_ast_child(ast_location);
		}
		else if(ast_location instanceof AstInitializerBody) {
			ast_location = ((AstInitializerBody) ast_location).get_initializer_list();
			return this.local_ast_child(ast_location);
		}
		else if(ast_location instanceof AstDeclarator) {
			if(((AstDeclarator) ast_location).get_production() == DeclaratorProduction.identifier) {
				ast_location = ((AstDeclarator) ast_location).get_identifier();
			}
			else {
				ast_location = ((AstDeclarator) ast_location).get_declarator();
			}
			return this.local_ast_child(ast_location);
		}
		else {
			return ast_location;
		}
	}
	private	List<CirNode>		local_cir_nodes(CirTree cir_tree, AstNode ast_location, Class<?> cir_class) {
		return cir_tree.get_localizer().get_cir_nodes(ast_location, cir_class);
	}
	private	CirNode				local_first_node(CirTree cir_tree, AstNode ast_location, Class<?> cir_class) {
		List<CirNode> cir_nodes = this.local_cir_nodes(cir_tree, ast_location, cir_class);
		if(cir_nodes.isEmpty()) {
			return null;
		}
		else {
			return cir_nodes.get(0);
		}
	}
	private	CirNode				local_final_node(CirTree cir_tree, AstNode ast_location, Class<?> cir_class) {
		List<CirNode> cir_nodes = this.local_cir_nodes(cir_tree, ast_location, cir_class);
		if(cir_nodes.isEmpty()) {
			return null;
		}
		else {
			return cir_nodes.get(cir_nodes.size() - 1);
		}
	}
	private	CirExecution		local_beg_execution(CirTree cir_tree, AstNode ast_location) throws Exception {
		return cir_tree.get_localizer().beg_statement(ast_location).execution_of();
	}
	private	CirExecution		local_end_execution(CirTree cir_tree, AstNode ast_location) throws Exception {
		return cir_tree.get_localizer().end_statement(ast_location).execution_of();
	}
	
	/* basic construction */
	/**
	 * It creates an isolated store-location with the given parameters
	 * @param store_class	the category of the store-location created
	 * @param ast_location	the abstract syntax node this store refers
	 * @param cir_location	the C-intermediation node the store refers
	 * @return				the store-location created by given inputs
	 * @throws Exception
	 */
	private	UniAbstractStore	new_storage(UniAbstractLType store_class,
			AstNode ast_location, CirNode cir_location) throws Exception {
		if(store_class == null) {
			throw new IllegalArgumentException("Invalid store_class: null");
		}
		else if(ast_location == null) {
			throw new IllegalArgumentException("Invalid ast_location: null");
		}
		else if(cir_location == null) {
			throw new IllegalArgumentException("Invalid cir_location: null");
		}
		else if(cir_location.execution_of() == null) {
			throw new IllegalArgumentException("Invalid exe_location: null");
		}
		else {
			return new UniAbstractStore(store_class, ast_location, cir_location, cir_location.execution_of());
		}
	}
	/**
	 * It creates a store-location of which class best-matches with the expression types
	 * @param ast_location
	 * @param cir_location
	 * @return
	 * @throws Exception
	 */
	private	UniAbstractStore	new_cir_expr(AstNode ast_location, CirExpression cir_location) throws Exception {
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
			UniAbstractLType store_class;
			if(cir_location.get_parent() instanceof CirAssignStatement) {
				CirAssignStatement statement = (CirAssignStatement) cir_location.statement_of();
				if(statement.get_lvalue() == cir_location) {
					store_class = UniAbstractLType.cdef_expr;
				}
				else {
					store_class = UniAbstractLType.used_expr;
				}
			}
			else if(cir_location.get_parent() instanceof CirArgumentList) {
				store_class = UniAbstractLType.argv_expr;
			}
			else if(SymbolFactory.is_bool(cir_location.get_data_type())
					|| cir_location.get_parent() instanceof CirIfStatement
					|| cir_location.get_parent() instanceof CirCaseStatement) {
				store_class = UniAbstractLType.bool_expr;
			}
			else {
				store_class = UniAbstractLType.used_expr;
			}
			return this.new_storage(store_class, ast_location, cir_location);
		}
	}
	/**
	 * It creates a store-location of which class best-matches with the statement inputs
	 * @param ast_location
	 * @param cir_location
	 * @return
	 * @throws Exception
	 */
	private	UniAbstractStore	new_cir_stmt(AstNode ast_location, CirStatement cir_location) throws Exception {
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
			UniAbstractLType store_class;
			if(cir_location instanceof CirAssignStatement) {
				store_class = UniAbstractLType.assg_stmt;
			}
			else if(cir_location instanceof CirIfStatement) {
				store_class = UniAbstractLType.ifte_stmt;
			}
			else if(cir_location instanceof CirCaseStatement) {
				store_class = UniAbstractLType.case_stmt;
			}
			else if(cir_location instanceof CirCallStatement) {
				store_class = UniAbstractLType.call_stmt;
			}
			else if(cir_location instanceof CirGotoStatement) {
				store_class = UniAbstractLType.goto_stmt;
			}
			else if(cir_location instanceof CirBegStatement
					|| cir_location instanceof CirEndStatement) {
				store_class = UniAbstractLType.bend_stmt;
			}
			else if(cir_location instanceof CirLabelStatement
					|| cir_location instanceof CirDefaultStatement) {
				store_class = UniAbstractLType.labl_elem;
			}
			else if(cir_location instanceof CirIfEndStatement
					|| cir_location instanceof CirCaseEndStatement) {
				store_class = UniAbstractLType.conv_stmt;
			}
			else {
				throw new IllegalArgumentException(cir_location.getClass().getSimpleName());
			}
			return this.new_storage(store_class, ast_location, cir_location);
		}
	}
	/**
	 * It creates a store-location of which class best matches with the element as given
	 * @param ast_location
	 * @param cir_location
	 * @return
	 * @throws Exception
	 */
	private	UniAbstractStore	new_cir_elem(AstNode ast_location, CirNode cir_location) throws Exception {
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
			UniAbstractLType store_class;
			if(cir_location instanceof CirField) {
				store_class = UniAbstractLType.fiel_elem;
			}
			else if(cir_location instanceof CirLabel) {
				store_class = UniAbstractLType.labl_elem;
			}
			else if(cir_location instanceof CirArgumentList) {
				store_class = UniAbstractLType.args_elem;
			}
			else if(cir_location instanceof CirType) {
				store_class = UniAbstractLType.type_elem;
			}
			else {
				throw new IllegalArgumentException(cir_location.getClass().getSimpleName());
			}
			return this.new_storage(store_class, ast_location, cir_location);
		}
	}
	/**
	 * It creates a store-location of which class best-matches with the Cir-based location
	 * @param ast_location
	 * @param cir_location
	 * @return
	 * @throws Exception
	 */
	private	UniAbstractStore	new_cir_node(AstNode ast_location, CirNode cir_location) throws Exception {
		if(ast_location == null) {
			throw new IllegalArgumentException("Invalid ast_location: null");
		}
		else if(cir_location == null) {
			throw new IllegalArgumentException("Invalid cir_location: null");
		}
		else if(cir_location.execution_of() == null) {
			throw new IllegalArgumentException("Invalid exe_location: null");
		}
		else if(cir_location instanceof CirExpression) {
			return this.new_cir_expr(ast_location, (CirExpression) cir_location);
		}
		else if(cir_location instanceof CirStatement) {
			return this.new_cir_stmt(ast_location, (CirStatement) cir_location);
		}
		else {
			return this.new_cir_elem(ast_location, cir_location);
		}
	}
	
	/* CIR-Based Construction */
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
		return this.new_cir_node(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_declarator(CirDeclarator source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		return this.new_cir_node(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_implicator(CirImplicator source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		if(ast_location == null) {
			throw new IllegalArgumentException("Invalid ast_location: null");
		}
		else if(ast_location instanceof AstSwitchStatement) {
			ast_location = ((AstSwitchStatement) ast_location).get_condition();
			ast_location = this.local_ast_child(ast_location);
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
			loperand = this.local_ast_child(loperand); 
			
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
		return this.new_cir_node(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_retr_point(CirReturnPoint source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		return this.new_cir_node(ast_location, source);
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
		return this.new_cir_node(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_field_expr(CirFieldExpression source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		return this.new_cir_node(ast_location, source);
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
		return this.new_cir_node(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_st_literal(CirStringLiteral source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		return this.new_cir_node(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_defa_value(CirDefaultValue source) throws Exception {
		CirAssignStatement statement = (CirAssignStatement) source.statement_of();
		AstNode ast_location = statement.get_lvalue().get_ast_source();
		return this.new_cir_node(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_addr_expr(CirAddressExpression source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		return this.new_cir_node(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_cast_expr(CirCastExpression source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		return this.new_cir_node(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_init_body(CirInitializerBody source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		return this.new_cir_node(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_wait_expr(CirWaitExpression source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		return this.new_cir_node(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_arith_expr(CirArithExpression source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		if(ast_location == null) {
			ast_location = source.get_parent().get_ast_source();
		}
		return this.new_cir_node(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_bitws_expr(CirBitwsExpression source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		return this.new_cir_node(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_logic_expr(CirLogicExpression source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		return this.new_cir_node(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_relation_expr(CirRelationExpression source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		return this.new_cir_node(ast_location, source);
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
		return this.new_cir_node(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_inc_assgn(CirIncreAssignStatement source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		return this.new_cir_node(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_ini_assgn(CirInitAssignStatement source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		return this.new_cir_node(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_ret_assgn(CirReturnAssignStatement source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		return this.new_cir_node(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_wat_assgn(CirWaitAssignStatement source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		return this.new_cir_node(ast_location, source);
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
		return this.new_cir_node(ast_location, source);
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
		return this.new_cir_node(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_case_stmt(CirCaseStatement source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		return this.new_cir_node(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_call_stmt(CirCallStatement source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		return this.new_cir_node(ast_location, source);
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
		return this.new_cir_node(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_beg_stmt(CirBegStatement source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		return this.new_cir_node(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_end_stmt(CirEndStatement source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		return this.new_cir_node(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_default_stmt(CirDefaultStatement source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		return this.new_cir_node(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_label_stmt(CirLabelStatement source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		return this.new_cir_node(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_if_end_stmt(CirIfEndStatement source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		return this.new_cir_node(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_case_end_stmt(CirCaseEndStatement source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		return this.new_cir_node(ast_location, source);
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
		return this.new_cir_node(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_field(CirField source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		return this.new_cir_node(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_label(CirLabel source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		return this.new_cir_node(ast_location, source);
	}
	private	UniAbstractStore	parse_cir_type(CirType source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		return this.new_cir_node(ast_location, source);
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
		else {
			throw new IllegalArgumentException("Unsupported: " + source);
		}
	}
	
	/* AST-based Parsing */
	/**
	 * It transforms the abstract syntax node based on C-intermediate program
	 * @param cir_tree
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private	UniAbstractStore	parse_ast(CirTree cir_tree, AstNode source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		// TODO implement the syntax-directed algorithm for transformation...
		else {
			throw new IllegalArgumentException(source.getClass().getSimpleName());
		}
	}
	/* AST-Elementals */
	private	UniAbstractStore	parse_ast_args_list(CirTree cir_tree, AstArgumentList source) throws Exception {
		CirNode cir_location = this.local_first_node(cir_tree, source, CirArgumentList.class);
		return this.new_cir_node(source, cir_location);
	}
	private	UniAbstractStore	parse_ast_declaration(CirTree cir_tree, AstDeclaration source) throws Exception {
		if(source.has_declarator_list()) {
			return this.parse_ast(cir_tree, source.get_declarator_list());
		}
		else {
			return this.parse_cir(this.local_beg_execution(cir_tree, source).get_statement());
		}
	}
	private	UniAbstractStore	parse_ast_init_declarator_list(CirTree 
			cir_tree, AstInitDeclaratorList source) throws Exception {
		return this.parse_ast(cir_tree, source.get_init_declarator(0));
	}
	private	UniAbstractStore	parse_ast_init_declarator(CirTree cir_tree, AstInitDeclarator source) throws Exception {
		CirNode cir_location = this.local_first_node(cir_tree, source, CirInitAssignStatement.class);
		return this.new_cir_node(source, cir_location);
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
		CirNode cir_location = this.local_first_node(cir_tree, declarator, CirDeclarator.class);
		return this.new_cir_node(declarator, cir_location);
	}
	private	UniAbstractStore	parse_ast_initializer(CirTree cir_tree, AstInitializer source) throws Exception {
		if(source.is_body()) {
			return this.parse_ast(cir_tree, source.get_body());
		}
		else {
			return this.parse_ast(cir_tree, source.get_expression());
		}
	}
	private	UniAbstractStore	parse_ast_initializer_body(CirTree cir_tree, AstInitializerBody source) throws Exception {
		CirNode cir_location = this.local_first_node(cir_tree, source, CirInitializerBody.class);
		return this.new_cir_node(source, cir_location);
	}
	private	UniAbstractStore	parse_ast_initializer_list(CirTree cir_tree, AstInitializerList source) throws Exception {
		return this.parse_ast(cir_tree, source.get_parent());
	}
	private	UniAbstractStore	parse_ast_field_initializer(CirTree cir_tree, AstFieldInitializer source) throws Exception {
		return this.parse_ast(cir_tree, source.get_initializer());
	}
	private	UniAbstractStore	parse_ast_type_name(CirTree cir_tree, AstTypeName source) throws Exception {
		CirNode cir_location = this.local_first_node(cir_tree, source, CirType.class);
		return this.new_cir_node(source, cir_location);
	}
	private	UniAbstractStore	parse_ast_field(CirTree cir_tree, AstField source) throws Exception {
		CirNode cir_location = this.local_first_node(cir_tree, source, CirField.class);
		return this.new_cir_node(source, cir_location);
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
	/* AST-Expression */
	
	
	
	
	
}
