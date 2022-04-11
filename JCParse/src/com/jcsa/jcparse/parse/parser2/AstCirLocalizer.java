package com.jcsa.jcparse.parse.parser2;

import java.util.List;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.decl.AstDeclaration;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstInitDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstInitDeclaratorList;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstName;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator.DeclaratorProduction;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstFieldInitializer;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializer;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerBody;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerList;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstBasicExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstConstant;
import com.jcsa.jcparse.lang.astree.expr.base.AstIdExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstLiteral;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncrePostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncreUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstPointUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstPostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstArgumentList;
import com.jcsa.jcparse.lang.astree.expr.othr.AstArrayExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstCastExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstCommaExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConditionalExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConstExpression;
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
import com.jcsa.jcparse.lang.astree.stmt.AstStatementList;
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
import com.jcsa.jcparse.lang.irlang.expr.CirFieldExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirIdentifier;
import com.jcsa.jcparse.lang.irlang.expr.CirImplicator;
import com.jcsa.jcparse.lang.irlang.expr.CirInitializerBody;
import com.jcsa.jcparse.lang.irlang.expr.CirLogicExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirNameExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirRelationExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirReturnPoint;
import com.jcsa.jcparse.lang.irlang.expr.CirStringLiteral;
import com.jcsa.jcparse.lang.irlang.expr.CirWaitExpression;
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
import com.jcsa.jcparse.lang.irlang.stmt.CirLabelStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirReturnAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirSaveAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirTagStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirWaitAssignStatement;
import com.jcsa.jcparse.lang.lexical.COperator;

public class AstCirLocalizer {
	
	/* definitions */
	/** the C-intermediate program for localization **/
	private	CirTree cir_tree;
	/**
	 * It constructs a localizer for ast-cir connection in given tree space
	 * @param cir_tree
	 * @throws IllegalArgumentException
	 */
	public AstCirLocalizer(CirTree cir_tree) throws IllegalArgumentException {
		if(cir_tree == null) {
			throw new IllegalArgumentException("Invalid cir_tree: null");
		}
		else {
			this.cir_tree = cir_tree;
		}
	}
	
	/* basic methods */
	/**
	 * @param source
	 * @return it finds the real expression of the source represents
	 * @throws Exception
	 */
	private	AstExpression 	get_ast_expression(AstNode source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof AstParanthExpression) {
			return this.get_ast_expression(((AstParanthExpression) source).get_sub_expression());
		}
		else if(source instanceof AstConstExpression) {
			return this.get_ast_expression(((AstConstExpression) source).get_expression());
		}
		else if(source instanceof AstFieldInitializer) {
			return this.get_ast_expression(((AstFieldInitializer) source).get_initializer());
		}
		else if(source instanceof AstInitializer) {
			if(((AstInitializer) source).is_body()) {
				return this.get_ast_expression(((AstInitializer) source).get_body());
			}
			else {
				return this.get_ast_expression(((AstInitializer) source).get_expression());
			}
		}
		else if(source instanceof AstInitializerList || source instanceof AstArgumentList) {
			return this.get_ast_expression(source.get_parent());
		}
		else if(source instanceof AstExpression) {
			return (AstExpression) source;
		}
		else {
			throw new IllegalArgumentException(source.getClass().getSimpleName());
		}
	}
	/**
	 * @param source
	 * @param cir_class
	 * @return the list of CirNode(s) corresponds to the ast-source.
	 */
	private List<CirNode>	get_cir_locations(AstNode source, Class<?> cir_class) throws Exception {
		if(this.cir_tree == null) {
			throw new IllegalArgumentException("Invalid cir_tree: null");
		}
		else if(source == null) {
			throw new IllegalArgumentException("Invalid source as null");
		}
		else if(cir_class == null) {
			return this.cir_tree.get_cir_nodes(source);
		}
		else {
			return this.cir_tree.get_cir_nodes(source, cir_class);
		}
	}
	/**
	 * @param source
	 * @return the first statement enclosed in the source
	 * @throws Exception
	 */
	private	CirStatement	get_beg_statement(AstNode source) throws Exception {
		if(this.cir_tree == null) {
			throw new IllegalArgumentException("Invalid cir_tree: null");
		}
		else if(source == null) {
			throw new IllegalArgumentException("Invalid source as null");
		}
		else {
			while(!this.cir_tree.has_cir_range(source)) {
				source = source.get_parent();
			}
			return this.cir_tree.get_cir_range(source).get_beg_statement();
		}
	}
	/**
	 * @param source
	 * @return the final statement enclosed in the source
	 * @throws Exception
	 */
	private	CirStatement	get_end_statement(AstNode source) throws Exception {
		if(this.cir_tree == null) {
			throw new IllegalArgumentException("Invalid cir_tree: null");
		}
		else if(source == null) {
			throw new IllegalArgumentException("Invalid source as null");
		}
		else {
			while(!this.cir_tree.has_cir_range(source)) {
				source = source.get_parent();
			}
			return this.cir_tree.get_cir_range(source).get_end_statement();
		}
	}
	/**
	 * @param source
	 * @param cir_class
	 * @param k
	 * @return the kth location w.r.t. the source in given class or null if out-of-range
	 * @throws Exception
	 */
	private	CirNode			get_cir_location(AstNode source, Class<?> cir_class, int k) throws Exception {
		List<CirNode> cir_locations = this.get_cir_locations(source, cir_class);
		if(k < 0 || k >= cir_locations.size()) {
			return null;
		}
		else {
			return cir_locations.get(k);
		}
	}
	/**
	 * @param source
	 * @param cir_class
	 * @return it finds the expression w.r.t. the source of given class
	 * @throws Exception
	 */
	private	CirExpression	get_cir_expression(AstNode source, Class<?> cir_class) throws Exception {
		List<CirNode> cir_locations = this.get_cir_locations(source, cir_class);
		CirExpression expression = null;
		for(CirNode cir_location : cir_locations) {
			if(cir_location instanceof CirExpression) {
				expression = (CirExpression) cir_location;
				if(expression.execution_of() != null) break;
			}
		}
		return expression;
	}
	
	/* CIR-localizer */
	/**
	 * @param source
	 * @return it infers the ast-location that the source represents
	 * @throws Exception
	 */
	public AstNode localize(CirNode cir_location) throws Exception {
		return this.loc_cir(cir_location);
	}
	private	AstNode	loc_cir(CirNode source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("invalid source: null");
		}
		else if(source instanceof CirExpression) {
			return this.loc_cir_expression((CirExpression) source);
		}
		else if(source instanceof CirStatement) {
			return this.loc_cir_statement((CirStatement) source);
		}
		else {
			throw new IllegalArgumentException(source.getClass().getSimpleName());
		}
	}
	private	AstNode	loc_cir_declarator(CirDeclarator source) throws Exception 	{ return source.get_ast_source(); }
	private	AstNode	loc_cir_identifier(CirIdentifier source) throws Exception 	{ return source.get_ast_source(); }
	private	AstNode	loc_cir_retr_point(CirReturnPoint source) throws Exception 	{ return source.get_ast_source(); }
	private	AstNode	loc_cir_implicator(CirImplicator source) throws Exception	{
		CirStatement statement = source.statement_of();
		AstNode ast_location = source.get_ast_source();
		if(ast_location instanceof AstSwitchStatement) {
			return this.get_ast_expression(((AstSwitchStatement) ast_location).get_condition());
		}
		else if(ast_location instanceof AstConditionalExpression) {
			if(statement instanceof CirSaveAssignStatement && 
					statement.get_ast_source() == ast_location) {
				return ((CirAssignStatement) statement).get_rvalue().get_ast_source();
			}
			else {
				return ast_location;
			}
		}
		else if(ast_location instanceof AstFunCallExpression) {
			return ast_location;
		}
		else if(ast_location instanceof AstIncrePostfixExpression) {
			return ast_location;
		}
		else if(ast_location instanceof AstLogicBinaryExpression) {
			if(statement instanceof CirSaveAssignStatement && statement.get_ast_source() == ast_location) {
				return ((CirSaveAssignStatement) statement).get_rvalue().get_ast_source();
			}
			else {
				return ast_location;
			}
		}
		else {
			throw new IllegalArgumentException(ast_location.getClass().getSimpleName());
		}
	}
	private	AstNode	loc_cir_name_expression(CirNameExpression source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("invalid source: null");
		}
		else if(source instanceof CirDeclarator) {
			return this.loc_cir_declarator((CirDeclarator) source);
		}
		else if(source instanceof CirIdentifier) {
			return this.loc_cir_identifier((CirIdentifier) source);
		}
		else if(source instanceof CirImplicator) {
			return this.loc_cir_implicator((CirImplicator) source);
		}
		else if(source instanceof CirReturnPoint) {
			return this.loc_cir_retr_point((CirReturnPoint) source);
		}
		else {
			throw new IllegalArgumentException(source.getClass().getSimpleName());
		}
	}
	private	AstNode	loc_cir_defer_expression(CirDeferExpression source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		if(ast_location == null) {
			ast_location = source.get_parent().get_ast_source();
			if(ast_location instanceof AstFunCallExpression) {
				return this.get_ast_expression(((AstFunCallExpression) ast_location).get_function());
			}
			else if(ast_location instanceof AstFieldExpression) {
				return this.get_ast_expression(((AstFieldExpression) ast_location).get_body());
			}
			else {
				throw new IllegalArgumentException(ast_location.getClass().getSimpleName());
			}
		}
		else if(ast_location instanceof AstPointUnaryExpression) {
			return ast_location;
		}
		else if(ast_location instanceof AstArrayExpression) {
			return ast_location;
		}
		else {
			throw new IllegalArgumentException(ast_location.getClass().getSimpleName());
		}
	}
	private	AstNode	loc_cir_field_expression(CirFieldExpression source) throws Exception { return source.get_ast_source(); }
	private	AstNode	loc_cir_const_expression(CirConstExpression source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		if(ast_location == null) {
			ast_location = source.statement_of().get_ast_source();
			if(ast_location instanceof AstIncreUnaryExpression) {
				return ((AstIncreUnaryExpression) ast_location).get_operator();
			}
			else if(ast_location instanceof AstIncrePostfixExpression) {
				return ((AstIncrePostfixExpression) ast_location).get_operator();
			}
			else {
				throw new IllegalArgumentException(ast_location.getClass().getSimpleName());
			}
		}
		else if(ast_location instanceof AstConstant ||
				ast_location instanceof AstExpressionStatement ||
				ast_location instanceof AstSizeofExpression ||
				ast_location instanceof AstIdExpression) {
			return ast_location;
		}
		else {
			throw new IllegalArgumentException(ast_location.getClass().getSimpleName());
		}
	}
	private	AstNode	loc_cir_string_literal(CirStringLiteral source) throws Exception { return source.get_ast_source(); }
	private	AstNode	loc_cir_default_value(CirDefaultValue source) throws Exception {
		return source.statement_of().get_ast_source();
	}
	private	AstNode	loc_cir_address_expression(CirAddressExpression source) throws Exception { return source.get_ast_source(); }
	private	AstNode	loc_cir_cast_expression(CirCastExpression source) throws Exception { return source.get_ast_source(); }
	private	AstNode	loc_cir_initializer_body(CirInitializerBody source) throws Exception { return source.get_ast_source(); }
	private	AstNode	loc_cir_wait_expression(CirWaitExpression source) throws Exception { return source.get_ast_source(); }
	private	AstNode	loc_cir_arith_expression(CirArithExpression source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		if(ast_location == null) {
			return source.get_parent().get_ast_source();
		}
		else if(ast_location instanceof AstArithBinaryExpression ||
				ast_location instanceof AstArithAssignExpression ||
				ast_location instanceof AstArithUnaryExpression) { return ast_location; }
		else {
			throw new IllegalArgumentException(ast_location.getClass().getSimpleName());
		}
	}
	private	AstNode	loc_cir_bitws_expression(CirBitwsExpression source) throws Exception { return source.get_ast_source(); }
	private	AstNode	loc_cir_logic_expression(CirLogicExpression source) throws Exception { 
		AstNode ast_location = source.get_ast_source();
		if(ast_location == null) {
			AstLogicBinaryExpression expression = 
					(AstLogicBinaryExpression) source.statement_of().get_ast_source();
			return this.get_ast_expression(expression.get_loperand());
		}
		else if(ast_location instanceof AstLogicUnaryExpression) {
			return ast_location;
		}
		else {
			throw new IllegalArgumentException(ast_location.getClass().getSimpleName());
		}
	}
	private	AstNode	loc_cir_relation_expression(CirRelationExpression source) throws Exception { return source.get_ast_source(); }
	private	AstNode	loc_cir_compute_expression(CirComputeExpression source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source as: null");
		}
		else if(source instanceof CirArithExpression) {
			return this.loc_cir_arith_expression((CirArithExpression) source);
		}
		else if(source instanceof CirBitwsExpression) {
			return this.loc_cir_bitws_expression((CirBitwsExpression) source);
		}
		else if(source instanceof CirLogicExpression) {
			return this.loc_cir_logic_expression((CirLogicExpression) source);
		}
		else if(source instanceof CirRelationExpression) {
			return this.loc_cir_relation_expression((CirRelationExpression) source);
		}
		else {
			throw new IllegalArgumentException(source.getClass().getSimpleName());
		}
	}
	private	AstNode	loc_cir_expression(CirExpression source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("invalid source: null");
		}
		else if(source instanceof CirNameExpression) {
			return this.loc_cir_name_expression((CirNameExpression) source);
		}
		else if(source instanceof CirConstExpression) {
			return this.loc_cir_const_expression((CirConstExpression) source);
		}
		else if(source instanceof CirStringLiteral) {
			return this.loc_cir_string_literal((CirStringLiteral) source);
		}
		else if(source instanceof CirDefaultValue) {
			return this.loc_cir_default_value((CirDefaultValue) source);
		}
		else if(source instanceof CirDeferExpression) {
			return this.loc_cir_defer_expression((CirDeferExpression) source);
		}
		else if(source instanceof CirFieldExpression) {
			return this.loc_cir_field_expression((CirFieldExpression) source);
		}
		else if(source instanceof CirComputeExpression) {
			return this.loc_cir_compute_expression((CirComputeExpression) source);
		}
		else if(source instanceof CirAddressExpression) {
			return this.loc_cir_address_expression((CirAddressExpression) source);
		}
		else if(source instanceof CirWaitExpression) {
			return this.loc_cir_wait_expression((CirWaitExpression) source);
		}
		else if(source instanceof CirCastExpression) {
			return this.loc_cir_cast_expression((CirCastExpression) source);
		}
		else if(source instanceof CirInitializerBody) {
			return this.loc_cir_initializer_body((CirInitializerBody) source);
		}
		else {
			throw new IllegalArgumentException(source.getClass().getSimpleName());
		}
	}
	private	AstNode	loc_cir_bin_assign_statement(CirBinAssignStatement source) throws Exception { return source.get_ast_source(); }
	private	AstNode	loc_cir_ini_assign_statement(CirInitAssignStatement source) throws Exception { return source.get_ast_source(); }
	private	AstNode	loc_cir_inc_assign_statement(CirIncreAssignStatement source) throws Exception { return source.get_ast_source(); } 
	private	AstNode	loc_cir_sav_assign_statement(CirSaveAssignStatement source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		if(ast_location == null) {
			throw new IllegalArgumentException("Invalid ast_location: null");
		}
		else if(ast_location instanceof AstSwitchStatement || 
				ast_location instanceof AstIncrePostfixExpression) {
			return ast_location;
		}
		else if(ast_location instanceof AstConditionalExpression
				|| ast_location instanceof AstLogicBinaryExpression) {
			return source.get_rvalue().get_ast_source();
		}
		else {
			throw new IllegalArgumentException(ast_location.getClass().getSimpleName());
		}
	}
	private	AstNode	loc_cir_ret_assign_statement(CirReturnAssignStatement source) throws Exception { return source.get_ast_source(); }
	private	AstNode	loc_cir_wat_assign_statement(CirWaitAssignStatement source) throws Exception { return source.get_ast_source(); }
	private	AstNode	loc_cir_assign_statement(CirAssignStatement source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof CirBinAssignStatement) {
			return this.loc_cir_bin_assign_statement((CirBinAssignStatement) source);
		}
		else if(source instanceof CirInitAssignStatement) {
			return this.loc_cir_ini_assign_statement((CirInitAssignStatement) source);
		}
		else if(source instanceof CirSaveAssignStatement) {
			return this.loc_cir_sav_assign_statement((CirSaveAssignStatement) source);
		}
		else if(source instanceof CirIncreAssignStatement) {
			return this.loc_cir_inc_assign_statement((CirIncreAssignStatement) source);
		}
		else if(source instanceof CirWaitAssignStatement) {
			return this.loc_cir_wat_assign_statement((CirWaitAssignStatement) source);
		}
		else if(source instanceof CirReturnAssignStatement) {
			return this.loc_cir_ret_assign_statement((CirReturnAssignStatement) source);
		}
		else {
			throw new IllegalArgumentException(source.getClass().getSimpleName());
		}
	}
	private	AstNode	loc_cir_call_statement(CirCallStatement source) throws Exception { return source.get_ast_source(); }
	private	AstNode	loc_cir_if_statement(CirIfStatement source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		if(ast_location == null) {
			throw new IllegalArgumentException("invalid ast_location as: null");
		}
		else if(ast_location instanceof AstDoWhileStatement || 
				ast_location instanceof AstForStatement || 
				ast_location instanceof AstWhileStatement ||
				ast_location instanceof AstIfStatement) { return ast_location; }
		else if(ast_location instanceof AstLogicBinaryExpression
				|| ast_location instanceof AstConditionalExpression) {
			return ast_location;
		}
		else {
			throw new IllegalArgumentException(ast_location.getClass().getSimpleName());
		}
	}
	private	AstNode	loc_cir_case_statement(CirCaseStatement source) throws Exception { return source.get_ast_source(); }
	private	AstNode	loc_cir_goto_statement(CirGotoStatement source) throws Exception {
		AstNode ast_location = source.get_ast_source();
		if(ast_location == null) {
			// throw new IllegalArgumentException("invalid ast_location as: null");
			return source.execution_of().get_ou_flow(0).get_target().get_statement().get_ast_source();
		}
		else if(ast_location instanceof AstBreakStatement ||
				ast_location instanceof AstContinueStatement ||
				ast_location instanceof AstGotoStatement ||
				ast_location instanceof AstSwitchStatement ||
				ast_location instanceof AstReturnStatement) { return ast_location; }
		else if(ast_location instanceof AstWhileStatement
				|| ast_location instanceof AstForStatement
				|| ast_location instanceof AstIfStatement) { return ast_location; }
		else {
			throw new IllegalArgumentException(ast_location.getClass().getSimpleName());
		}
	}
	private	AstNode	loc_cir_beg_statement(CirBegStatement source) throws Exception { return source.get_ast_source(); }
	private	AstNode	loc_cir_end_statement(CirEndStatement source) throws Exception { return source.get_ast_source(); }
	private	AstNode	loc_cir_label_statement(CirLabelStatement source) throws Exception { return source.get_ast_source(); }
	private	AstNode	loc_cir_default_statement(CirDefaultStatement source) throws Exception { return source.get_ast_source(); }
	private	AstNode	loc_cir_if_end_statement(CirIfEndStatement source) throws Exception { return source.get_ast_source(); }
	private	AstNode	loc_cir_case_end_statement(CirCaseEndStatement source) throws Exception { return source.get_ast_source(); }
	private	AstNode	loc_cir_tag_statement(CirTagStatement source) throws Exception {
		if(source instanceof CirBegStatement) {
			return this.loc_cir_beg_statement((CirBegStatement) source);
		}
		else if(source instanceof CirEndStatement) {
			return this.loc_cir_end_statement((CirEndStatement) source);
		}
		else if(source instanceof CirLabelStatement) {
			return this.loc_cir_label_statement((CirLabelStatement) source);
		}
		else if(source instanceof CirDefaultStatement) {
			return this.loc_cir_default_statement((CirDefaultStatement) source);
		}
		else if(source instanceof CirIfEndStatement) {
			return this.loc_cir_if_end_statement((CirIfEndStatement) source);
		}
		else if(source instanceof CirCaseEndStatement) {
			return this.loc_cir_case_end_statement((CirCaseEndStatement) source);
		}
		else {
			throw new IllegalArgumentException(source.getClass().getSimpleName());
		}
	}
	private	AstNode	loc_cir_statement(CirStatement source) throws Exception {
		if(source instanceof CirAssignStatement) {
			return this.loc_cir_assign_statement((CirAssignStatement) source);
		}
		else if(source instanceof CirCallStatement) {
			return this.loc_cir_call_statement((CirCallStatement) source);
		}
		else if(source instanceof CirIfStatement) {
			return this.loc_cir_if_statement((CirIfStatement) source);
		}
		else if(source instanceof CirCaseStatement) {
			return this.loc_cir_case_statement((CirCaseStatement) source);
		}
		else if(source instanceof CirGotoStatement) {
			return this.loc_cir_goto_statement((CirGotoStatement) source);
		}
		else if(source instanceof CirTagStatement) {
			return this.loc_cir_tag_statement((CirTagStatement) source);
		}
		else {
			throw new IllegalArgumentException(source.getClass().getSimpleName());
		}
	}
	
	/* AST-LOCATED */
	/**
	 * @param source
	 * @return it infers the C-intermediate location that the source represents
	 * @throws Exception
	 */
	public CirNode localize(AstNode source) throws Exception {
		return this.loc_ast(source);
	}
	/**
	 * @param source
	 * @param beg_end	true to return first statement; or the final otherwise
	 * @return
	 * @throws Exception
	 */
	public CirStatement localize(AstNode source, boolean beg_end) throws Exception {
		if(beg_end) {
			return this.get_beg_statement(source);
		}
		else {
			return this.get_end_statement(source);
		}
	}
	private	CirNode	loc_ast(AstNode source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof AstExpression) {
			return this.loc_ast_expression((AstExpression) source);
		}
		else if(source instanceof AstStatement) {
			return this.loc_ast_statement((AstStatement) source);
		}
		else {
			return this.loc_ast_otherwise(source);
		}
	}
	private	CirNode	loc_ast_expression(AstExpression source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof AstBasicExpression) {
			return this.loc_ast_basic_expression((AstBasicExpression) source);
		}
		else if(source instanceof AstBinaryExpression) {
			return this.loc_ast_binary_expression((AstBinaryExpression) source);
		}
		else if(source instanceof AstUnaryExpression) {
			return this.loc_ast_unary_expression((AstUnaryExpression) source);
		}
		else if(source instanceof AstPostfixExpression) {
			return this.loc_ast_postfix_expression((AstPostfixExpression) source);
		}
		else if(source instanceof AstArrayExpression) {
			return this.loc_ast_array_expression((AstArrayExpression) source);
		}
		else if(source instanceof AstCastExpression) {
			return this.loc_ast_cast_expression((AstCastExpression) source);
		}
		else if(source instanceof AstCommaExpression) {
			return this.loc_ast_comma_expression((AstCommaExpression) source);
		}
		else if(source instanceof AstConditionalExpression) {
			return this.loc_ast_conditional_expression((AstConditionalExpression) source);
		}
		else if(source instanceof AstConstExpression) {
			return this.loc_ast_const_expression((AstConstExpression) source);
		}
		else if(source instanceof AstParanthExpression) {
			return this.loc_ast_paranth_expression((AstParanthExpression) source);
		}
		else if(source instanceof AstFieldExpression) {
			return this.loc_ast_field_expression((AstFieldExpression) source);
		}
		else if(source instanceof AstFunCallExpression) {
			return this.loc_ast_call_expression((AstFunCallExpression) source);
		}
		else if(source instanceof AstInitializerBody) {
			return this.loc_ast_initializer_body((AstInitializerBody) source);
		}
		else if(source instanceof AstSizeofExpression) {
			return this.loc_ast_sizeof_expression((AstSizeofExpression) source);
		}
		else {
			throw new IllegalArgumentException(source.getClass().getSimpleName());
		}
	}
	private	CirNode	loc_ast_statement(AstStatement source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof AstDeclarationStatement) {
			return this.loc_ast_declaration_statement((AstDeclarationStatement) source);
		}
		else if(source instanceof AstExpressionStatement) {
			return this.loc_ast_expression_statement((AstExpressionStatement) source);
		}
		else if(source instanceof AstCompoundStatement) {
			return this.loc_ast_compound_statement((AstCompoundStatement) source);
		}
		else if(source instanceof AstBreakStatement) {
			return this.loc_ast_break_statement((AstBreakStatement) source);
		}
		else if(source instanceof AstContinueStatement) {
			return this.loc_ast_continue_statement((AstContinueStatement) source);
		}
		else if(source instanceof AstGotoStatement) {
			return this.loc_ast_goto_statement((AstGotoStatement) source);
		}
		else if(source instanceof AstCaseStatement) {
			return this.loc_ast_case_statement((AstCaseStatement) source);
		}
		else if(source instanceof AstDefaultStatement) {
			return this.loc_ast_default_statement((AstDefaultStatement) source);
		}
		else if(source instanceof AstLabeledStatement) {
			return this.loc_ast_labeled_statement((AstLabeledStatement) source);
		}
		else if(source instanceof AstReturnStatement) {
			return this.loc_ast_return_statement((AstReturnStatement) source);
		}
		else if(source instanceof AstIfStatement) {
			return this.loc_ast_if_statement((AstIfStatement) source);
		}
		else if(source instanceof AstSwitchStatement) {
			return this.loc_ast_switch_statement((AstSwitchStatement) source);
		}
		else if(source instanceof AstForStatement) {
			return this.loc_ast_for_statement((AstForStatement) source);
		}
		else if(source instanceof AstWhileStatement) {
			return this.loc_ast_while_statement((AstWhileStatement) source);
		}
		else if(source instanceof AstDoWhileStatement) {
			return this.loc_ast_do_while_statement((AstDoWhileStatement) source);
		}
		else {
			throw new IllegalArgumentException(source.getClass().getSimpleName());
		}
	}
	private CirNode loc_ast_otherwise(AstNode source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof AstStatementList) {
			return this.loc_ast_statement_list((AstStatementList) source);
		}
		else if(source instanceof AstDeclaration) {
			return this.loc_ast_declaration((AstDeclaration) source);
		}
		else if(source instanceof AstInitDeclaratorList) {
			return this.loc_ast_init_declarator_list((AstInitDeclaratorList) source);
		}
		else if(source instanceof AstInitDeclarator) {
			return this.loc_ast_init_declarator((AstInitDeclarator) source);
		}
		else if(source instanceof AstDeclarator) {
			return this.loc_ast_declarator((AstDeclarator) source);
		}
		else if(source instanceof AstInitializer) {
			return this.loc_ast_initializer((AstInitializer) source);
		}
		else if(source instanceof AstFieldInitializer) {
			return this.loc_ast_field_initializer((AstFieldInitializer) source);
		}
		else if(source instanceof AstInitializerList) {
			return this.loc_ast_initializer_list((AstInitializerList) source);
		}
		else if(source instanceof AstName) {
			return this.loc_ast_name((AstName) source);
		}
		else {
			throw new IllegalArgumentException(source.getClass().getSimpleName());
		}
	}
	/* AST-Expression */
	private	CirNode	loc_ast_id_expression(AstIdExpression source) throws Exception {
		return this.get_cir_expression(source, CirExpression.class);
	}
	private	CirNode	loc_ast_constant(AstConstant source) throws Exception {
		return this.get_cir_expression(source, CirConstExpression.class);
	}
	private	CirNode	loc_ast_literal(AstLiteral source) throws Exception {
		return this.get_cir_expression(source, CirStringLiteral.class);
	}
	private	CirNode	loc_ast_basic_expression(AstBasicExpression source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof AstIdExpression) {
			return this.loc_ast_id_expression((AstIdExpression) source);
		}
		else if(source instanceof AstConstant) {
			return this.loc_ast_constant((AstConstant) source);
		}
		else if(source instanceof AstLiteral) {
			return this.loc_ast_literal((AstLiteral) source);
		}
		else {
			throw new IllegalArgumentException(source.getClass().getSimpleName());
		}
	}
	private	CirNode	loc_ast_arith_unary_expression(AstArithUnaryExpression source) throws Exception {
		if(source.get_operator().get_operator() == COperator.positive) {
			return this.loc_ast(source.get_operand());
		}
		else {
			return this.get_cir_expression(source, CirArithExpression.class);
		}
	}
	private	CirNode	loc_ast_bitws_unary_expression(AstBitwiseUnaryExpression source) throws Exception {
		return this.get_cir_expression(source, CirBitwsExpression.class);
	}
	private	CirNode	loc_ast_logic_unary_expression(AstLogicUnaryExpression source) throws Exception {
		return this.get_cir_expression(source, CirLogicExpression.class);
	}
	private	CirNode	loc_ast_point_unary_expression(AstPointUnaryExpression source) throws Exception {
		return this.get_cir_expression(source, CirExpression.class);
	}
	private	CirNode	loc_ast_incre_unary_expression(AstIncreUnaryExpression source) throws Exception {
		CirIncreAssignStatement statement = (CirIncreAssignStatement) 
				this.get_cir_location(source, CirIncreAssignStatement.class, 0);
		return statement.get_rvalue();
	}
	private	CirNode	loc_ast_unary_expression(AstUnaryExpression source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(source instanceof AstArithUnaryExpression) {
			return this.loc_ast_arith_unary_expression((AstArithUnaryExpression) source);
		}
		else if(source instanceof AstBitwiseUnaryExpression) {
			return this.loc_ast_bitws_unary_expression((AstBitwiseUnaryExpression) source);
		}
		else if(source instanceof AstLogicUnaryExpression) {
			return this.loc_ast_logic_unary_expression((AstLogicUnaryExpression) source);
		}
		else if(source instanceof AstPointUnaryExpression) {
			return this.loc_ast_point_unary_expression((AstPointUnaryExpression) source);
		}
		else if(source instanceof AstIncreUnaryExpression) {
			return this.loc_ast_incre_unary_expression((AstIncreUnaryExpression) source);
		}
		else {
			throw new IllegalArgumentException(source.getClass().getSimpleName());
		}
	}
	private	CirNode	loc_ast_postfix_expression(AstPostfixExpression source) throws Exception {
		return this.get_cir_expression(source, CirImplicator.class);
	}
	private	CirNode	loc_ast_array_expression(AstArrayExpression source) throws Exception {
		return this.get_cir_expression(source, CirDeferExpression.class);
	}
	private	CirNode	loc_ast_cast_expression(AstCastExpression source) throws Exception {
		return this.get_cir_expression(source, CirCastExpression.class);
	}
	private	CirNode	loc_ast_comma_expression(AstCommaExpression source) throws Exception {
		AstExpression target = source.get_expression(source.number_of_arguments() - 1);
		return this.loc_ast(target);
	}
	private	CirNode	loc_ast_field_expression(AstFieldExpression source) throws Exception {
		return this.get_cir_expression(source, CirFieldExpression.class);
	}
	private	CirNode	loc_ast_call_expression(AstFunCallExpression source) throws Exception {
		return this.get_cir_expression(source, CirWaitExpression.class);
	}
	private	CirNode	loc_ast_sizeof_expression(AstSizeofExpression source) throws Exception {
		return this.get_cir_expression(source, CirConstExpression.class);
	}
	private	CirNode	loc_ast_initializer_body(AstInitializerBody source) throws Exception {
		return this.get_cir_expression(source, CirInitializerBody.class);
	}
	private	CirNode	loc_ast_paranth_expression(AstParanthExpression source) throws Exception {
		return this.loc_ast(source.get_sub_expression());
	}
	private	CirNode	loc_ast_const_expression(AstConstExpression source) throws Exception {
		return this.loc_ast(source.get_expression());
	}
	private	CirNode	loc_ast_conditional_expression(AstConditionalExpression source) throws Exception {
		return this.get_cir_location(source, CirImplicator.class, 2);
	}
	private	CirNode	loc_ast_binary_expression(AstBinaryExpression source) throws Exception {
		switch(source.get_operator().get_operator()) {
		case arith_add:
		case arith_sub:
		case arith_mul:
		case arith_div:
		case arith_mod:			return this.get_cir_expression(source, CirArithExpression.class);
		case bit_and:
		case bit_or:
		case bit_xor:
		case left_shift:
		case righ_shift:		return this.get_cir_expression(source, CirBitwsExpression.class);
		case greater_tn:
		case greater_eq:
		case smaller_tn:
		case smaller_eq:
		case equal_with:
		case not_equals:		return this.get_cir_expression(source, CirRelationExpression.class);
		case logic_and:
		case logic_or:			return this.get_cir_location(source, CirImplicator.class, 3);
		case assign:			return this.get_cir_location(source, CirAssignStatement.class, 0);
		case arith_add_assign:
		case arith_sub_assign:
		case arith_mul_assign:
		case arith_div_assign:
		case arith_mod_assign:	return this.get_cir_location(source, CirAssignStatement.class, 0);
		case bit_and_assign:
		case bit_or_assign:
		case bit_xor_assign:
		case left_shift_assign:
		case righ_shift_assign:	return this.get_cir_location(source, CirAssignStatement.class, 0);
		default:				throw new IllegalArgumentException("Invalid: " + source.generate_code());
		}
	}
	/* AST-Statements */
	private	CirNode	loc_ast_break_statement(AstBreakStatement source) throws Exception {
		return this.get_cir_location(source, CirGotoStatement.class, 0);
	}
	private	CirNode	loc_ast_continue_statement(AstContinueStatement source) throws Exception {
		return this.get_cir_location(source, CirGotoStatement.class, 0);
	}
	private	CirNode	loc_ast_goto_statement(AstGotoStatement source) throws Exception {
		return this.get_cir_location(source, CirGotoStatement.class, 0);
	}
	private CirNode	loc_ast_return_statement(AstReturnStatement source) throws Exception {
		if(source.has_expression()) {
			return this.get_cir_location(source, CirReturnAssignStatement.class, 0);
		}
		else {
			return this.get_cir_location(source, CirGotoStatement.class, 0);
		}
	}
	private	CirNode	loc_ast_labeled_statement(AstLabeledStatement source) throws Exception {
		return this.get_cir_location(source, CirLabelStatement.class, 0);
	}
	private	CirNode	loc_ast_case_statement(AstCaseStatement source) throws Exception {
		return this.get_cir_location(source, CirCaseStatement.class, 0);
	}
	private	CirNode	loc_ast_default_statement(AstDefaultStatement source) throws Exception {
		return this.get_cir_location(source, CirDefaultStatement.class, 0);
	}
	private	CirNode	loc_ast_if_statement(AstIfStatement source) throws Exception {
		return this.get_cir_location(source, CirIfStatement.class, 0);
	}
	private	CirNode	loc_ast_switch_statement(AstSwitchStatement source) throws Exception {
		return this.get_cir_location(source, CirSaveAssignStatement.class, 0);
	}
	private	CirNode	loc_ast_for_statement(AstForStatement source) throws Exception {
		return this.get_cir_location(source, CirIfStatement.class, 0);
	}
	private	CirNode	loc_ast_while_statement(AstWhileStatement source) throws Exception {
		return this.get_cir_location(source, CirIfStatement.class, 0);
	}
	private	CirNode	loc_ast_do_while_statement(AstDoWhileStatement source) throws Exception {
		return this.get_cir_location(source, CirIfStatement.class, 0);
	}
	private	CirNode	loc_ast_compound_statement(AstCompoundStatement source) throws Exception {
		return this.get_beg_statement(source);
	}
	private	CirNode	loc_ast_expression_statement(AstExpressionStatement source) throws Exception {
		if(source.has_expression()) {
			return this.loc_ast(source.get_expression());
		}
		else {
			return this.get_beg_statement(source);
		}
	}
	private	CirNode	loc_ast_declaration_statement(AstDeclarationStatement source) throws Exception {
		return this.loc_ast(source.get_declaration());
	}
	/* AST-Specifiers */
	private	CirNode	loc_ast_statement_list(AstStatementList source) throws Exception {
		return this.loc_ast(source.get_parent());
	}
	private	CirNode	loc_ast_declaration(AstDeclaration source) throws Exception {
		if(source.has_declarator_list()) {
			return this.loc_ast(source.get_declarator_list());
		}
		else {
			return this.get_beg_statement(source);
		}
	}
	private	CirNode	loc_ast_init_declarator_list(AstInitDeclaratorList source) throws Exception {
		return this.loc_ast(source.get_init_declarator(0));
	}
	private	CirNode	loc_ast_init_declarator(AstInitDeclarator source) throws Exception {
		if(source.has_initializer()) {
			return this.get_cir_location(source, CirInitAssignStatement.class, 0);
		}
		else {
			return this.loc_ast(source.get_declarator());
		}
	}
	private	CirNode	loc_ast_declarator(AstDeclarator source) throws Exception {
		if(source.get_production() == DeclaratorProduction.identifier) {
			return this.loc_ast(source.get_identifier());
		}
		else {
			return this.loc_ast(source.get_declarator());
		}
	}
	private	CirNode	loc_ast_name(AstName source) throws Exception {
		return this.get_cir_location(source, CirExpression.class, 0);
	}
	private	CirNode	loc_ast_initializer(AstInitializer source) throws Exception {
		if(source.is_body()) {
			return this.loc_ast(source.get_body());
		}
		else {
			return this.loc_ast(source.get_expression());
		}
	}
	private	CirNode	loc_ast_initializer_list(AstInitializerList source) throws Exception {
		return this.loc_ast(source.get_parent());
	}
	private	CirNode	loc_ast_field_initializer(AstFieldInitializer source) throws Exception {
		return this.loc_ast(source.get_initializer());
	}
	
}
