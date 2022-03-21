package com.jcsa.jcmutest.mutant.uni2mutant.base;

import java.util.List;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.decl.AstDeclaration;
import com.jcsa.jcparse.lang.astree.decl.AstTypeName;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstInitDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstInitDeclaratorList;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstParameterDeclaration;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator.DeclaratorProduction;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstFieldInitializer;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializer;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerBody;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerList;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstConstant;
import com.jcsa.jcparse.lang.astree.expr.base.AstIdExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstLiteral;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncrePostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncreUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstPointUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstRelationExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstShiftAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstShiftBinaryExpression;
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
import com.jcsa.jcparse.lang.irlang.stmt.CirWaitAssignStatement;
import com.jcsa.jcparse.lang.irlang.unit.CirFunctionBody;
import com.jcsa.jcparse.lang.irlang.unit.CirFunctionDefinition;
import com.jcsa.jcparse.lang.lexical.COperator;


/**
 * It implements the creation of UniAbstractStore.
 * 
 * @author yukimula
 *
 */
final class UniStoreLocalizer {
	
	/* singleton pattern */ /** constructor **/ private UniStoreLocalizer() { }
	private static final UniStoreLocalizer localizer = new UniStoreLocalizer();
	
	/* basic methods */
	/**
	 * It creates a store location based on the role of the cir-location.
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
			CirExecution exe_location = cir_location.execution_of();
			CirNode parent = cir_location.get_parent();
			UniAbstractSType store_class;
			if(parent == null) {
				throw new IllegalArgumentException("Invalid parent: null");
			}
			else if(parent instanceof CirIfStatement || 
					parent instanceof CirCaseStatement) {
				store_class = UniAbstractSType.cond_expr;
			}
			else if(parent instanceof CirArgumentList) {
				store_class = UniAbstractSType.args_expr;
			}
			else if(parent instanceof CirAssignStatement) {
				if(((CirAssignStatement) parent).get_lvalue() == cir_location) {
					store_class = UniAbstractSType.cdef_expr;
				}
				else {
					store_class = UniAbstractSType.oprd_expr;
				}
			}
			else if(parent instanceof CirFieldExpression
					|| parent instanceof CirDeferExpression) {
				store_class = UniAbstractSType.refr_expr;
			}
			else {
				store_class = UniAbstractSType.oprd_expr;
			}
			return new UniAbstractStore(store_class, ast_location, cir_location, exe_location);
		}
	}
	/**
	 * It creates a store location based on the given class and cir-node
	 * @param store_class
	 * @param ast_location
	 * @param cir_location
	 * @return
	 * @throws Exception
	 */
	private	UniAbstractStore	new_cir_node(UniAbstractSType store_class, AstNode ast_location, CirNode cir_location) throws Exception {
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
			return new UniAbstractStore(store_class, ast_location, cir_location, cir_location.execution_of());
		}
	}
	/**
	 * It derives the representative of the expression without paranthesis and others...
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	private	AstExpression		der_ast_expr(AstNode expression) throws Exception {
		while(true) {
			if(expression instanceof AstParanthExpression) {
				expression = ((AstParanthExpression) expression).get_sub_expression();
			}
			else if(expression instanceof AstConstExpression) {
				expression = ((AstConstExpression) expression).get_expression();
			}
			else if(expression instanceof AstInitializer) {
				if(((AstInitializer) expression).is_body()) {
					expression = ((AstInitializer) expression).get_body();
				}
				else {
					expression = ((AstInitializer) expression).get_expression();
				}
			}
			else { break; }
		}
		return (AstExpression) expression;
	}
	
	/* CIR-based State-Store Localization */
	/**
	 * @param cir_location
	 * @return it localizes the state-location of given cir-location as centered
	 * @throws Exception
	 */
	protected static UniAbstractStore loc(CirNode cir_location) throws Exception {
		return localizer.loc_cir(cir_location);
	}
	/**
	 * @param cir_location
	 * @return it localizes the state-location of given cir-location as centered
	 * @throws Exception
	 */
	private	UniAbstractStore 	loc_cir(CirNode cir_location) throws Exception {
		if(cir_location == null) {
			throw new IllegalArgumentException("Invalid cir_location: null");
		}
		else if(cir_location.execution_of() == null) {
			throw new IllegalArgumentException("Invalid exe_location: null");
		}
		else if(cir_location instanceof CirArgumentList) {
			return this.loc_cir_argument_list((CirArgumentList) cir_location);
		}
		else if(cir_location instanceof CirFunctionBody) {
			return this.loc_cir_function_body((CirFunctionBody) cir_location);
		}
		else if(cir_location instanceof CirFunctionDefinition) {
			return this.loc_cir_function_definition((CirFunctionDefinition) cir_location);
		}
		else if(cir_location instanceof CirLabel) {
			return this.loc_cir_label((CirLabel) cir_location);
		}
		else if(cir_location instanceof CirField) {
			return this.loc_cir_field((CirField) cir_location);
		}
		else if(cir_location instanceof CirType) {
			return this.loc_cir_type((CirType) cir_location);
		}
		else if(cir_location instanceof CirBinAssignStatement) {
			return this.loc_cir_bin_assign_stmt((CirBinAssignStatement) cir_location);
		}
		else if(cir_location instanceof CirSaveAssignStatement) {
			return this.loc_cir_sav_assign_stmt((CirSaveAssignStatement) cir_location);
		}
		else if(cir_location instanceof CirIncreAssignStatement) {
			return this.loc_cir_inc_assign_stmt((CirIncreAssignStatement) cir_location);
		}
		else if(cir_location instanceof CirInitAssignStatement) {
			return this.loc_cir_ini_assign_stmt((CirInitAssignStatement) cir_location);
		}
		else if(cir_location instanceof CirWaitAssignStatement) {
			return this.loc_cir_wat_assign_stmt((CirWaitAssignStatement) cir_location);
		}
		else if(cir_location instanceof CirReturnAssignStatement) {
			return this.loc_cir_ret_assign_stmt((CirReturnAssignStatement) cir_location);
		}
		else if(cir_location instanceof CirCallStatement) {
			return this.loc_cir_call_stmt((CirCallStatement) cir_location);
		}
		else if(cir_location instanceof CirIfStatement) {
			return this.loc_cir_if_stmt((CirIfStatement) cir_location);
		}
		else if(cir_location instanceof CirCaseStatement) {
			return this.loc_cir_case_stmt((CirCaseStatement) cir_location);
		}
		else if(cir_location instanceof CirBegStatement) {
			return this.loc_cir_beg_stmt((CirBegStatement) cir_location);
		}
		else if(cir_location instanceof CirEndStatement) {
			return this.loc_cir_end_stmt((CirEndStatement) cir_location);
		}
		else if(cir_location instanceof CirIfEndStatement) {
			return this.loc_cir_if_end_stmt((CirIfEndStatement) cir_location);
		}
		else if(cir_location instanceof CirCaseEndStatement) {
			return this.loc_cir_case_end_stmt((CirCaseEndStatement) cir_location);
		}
		else if(cir_location instanceof CirDefaultStatement) {
			return this.loc_cir_default_stmt((CirDefaultStatement) cir_location);
		}
		else if(cir_location instanceof CirLabelStatement) {
			return this.loc_cir_label_stmt((CirLabelStatement) cir_location);
		}
		else if(cir_location instanceof CirGotoStatement) {
			return this.loc_cir_goto_stmt((CirGotoStatement) cir_location);
		}
		else if(cir_location instanceof CirDeclarator) {
			return this.loc_cir_declarator((CirDeclarator) cir_location);
		}
		else if(cir_location instanceof CirIdentifier) {
			return this.loc_cir_identifier((CirIdentifier) cir_location);
		}
		else if(cir_location instanceof CirImplicator) {
			return this.loc_cir_implicator((CirImplicator) cir_location);
		}
		else if(cir_location instanceof CirReturnPoint) {
			return this.loc_cir_retr_point((CirReturnPoint) cir_location);
		}
		else if(cir_location instanceof CirDeferExpression) {
			return this.loc_cir_defer_expr((CirDeferExpression) cir_location);
		}
		else if(cir_location instanceof CirFieldExpression) {
			return this.loc_cir_field_expr((CirFieldExpression) cir_location);
		}
		else if(cir_location instanceof CirConstExpression) {
			return this.loc_cir_const_expr((CirConstExpression) cir_location);
		}
		else if(cir_location instanceof CirStringLiteral) {
			return this.loc_cir_str_literal((CirStringLiteral) cir_location);
		}
		else if(cir_location instanceof CirDefaultValue) {
			return this.loc_cir_default_val((CirDefaultValue) cir_location);
		}
		else if(cir_location instanceof CirAddressExpression) {
			return this.loc_cir_addr_expr((CirAddressExpression) cir_location);
		}
		else if(cir_location instanceof CirWaitExpression) {
			return this.loc_cir_wait_expr((CirWaitExpression) cir_location);
		}
		else if(cir_location instanceof CirInitializerBody) {
			return this.loc_cir_init_body((CirInitializerBody) cir_location);
		}
		else if(cir_location instanceof CirCastExpression) {
			return this.loc_cir_cast_expr((CirCastExpression) cir_location);
		}
		else if(cir_location instanceof CirArithExpression) {
			return this.loc_cir_arith_expr((CirArithExpression) cir_location);
		}
		else if(cir_location instanceof CirBitwsExpression) {
			return this.loc_cir_bitws_expr((CirBitwsExpression) cir_location);
		}
		else if(cir_location instanceof CirLogicExpression) {
			return this.loc_cir_logic_expr((CirLogicExpression) cir_location);
		}
		else if(cir_location instanceof CirRelationExpression) {
			return this.loc_cir_relate_expr((CirRelationExpression) cir_location);
		}
		else {
			throw new IllegalArgumentException(cir_location.getClass().getSimpleName());
		}
	}
	/* CIR-Elements Classes */
	private	UniAbstractStore 	loc_cir_argument_list(CirArgumentList cir_location) throws Exception {
		AstNode ast_location = cir_location.get_ast_source();
		if(ast_location == null) {
			ast_location = cir_location.get_parent().get_ast_source();
		}
		return this.new_cir_node(UniAbstractSType.args_list, cir_location.get_ast_source(), cir_location);
	}
	private	UniAbstractStore	loc_cir_function_definition(CirFunctionDefinition cir_location) throws Exception {
		AstNode ast_location = cir_location.get_ast_source();
		if(ast_location == null) {
			ast_location = cir_location.get_parent().get_ast_source();
		}
		return this.new_cir_node(UniAbstractSType.func_defs, cir_location.get_ast_source(), cir_location);
	}
	private	UniAbstractStore	loc_cir_function_body(CirFunctionBody cir_location) throws Exception {
		AstNode ast_location = cir_location.get_ast_source();
		if(ast_location == null) {
			ast_location = cir_location.get_parent().get_parent().get_ast_source();
		}
		return this.new_cir_node(UniAbstractSType.stmt_list, cir_location.get_ast_source(), cir_location);
	}
	private	UniAbstractStore	loc_cir_field(CirField cir_location) throws Exception {
		return this.new_cir_node(UniAbstractSType.fiel_name, cir_location.get_ast_source(), cir_location);
	}
	private	UniAbstractStore	loc_cir_label(CirLabel cir_location) throws Exception {
		return this.new_cir_node(UniAbstractSType.labl_name, cir_location.get_ast_source(), cir_location);
	}
	private	UniAbstractStore	loc_cir_type(CirType cir_location) throws Exception {
		return this.new_cir_node(UniAbstractSType.type_name, cir_location.get_ast_source(), cir_location);
	}
	/* CIR-Statement Classes */
	private	UniAbstractStore	loc_cir_bin_assign_stmt(CirBinAssignStatement cir_location) throws Exception {
		return this.new_cir_node(UniAbstractSType.assg_stmt, cir_location.get_ast_source(), cir_location);
	}
	private	UniAbstractStore	loc_cir_inc_assign_stmt(CirIncreAssignStatement cir_location) throws Exception {
		return this.new_cir_node(UniAbstractSType.assg_stmt, cir_location.get_ast_source(), cir_location);
	}
	private	UniAbstractStore	loc_cir_sav_assign_stmt(CirSaveAssignStatement cir_location) throws Exception {
		AstNode ast_location = cir_location.get_ast_source();
		if(ast_location == null) {
			throw new IllegalArgumentException("Invalid ast_location: null");
		}
		else if(ast_location instanceof AstConditionalExpression) {
			/* as either t_operand or f_operand in cond_expression */
			ast_location = cir_location.get_rvalue().get_ast_source();
		}
		else if(ast_location instanceof AstIncrePostfixExpression) {
			/* x++ or x-- as save-assignment statement to localize */
		}
		else if(ast_location instanceof AstSwitchStatement) {
			/* as the statement to save values of switch.condition */
			ast_location = cir_location.get_rvalue().get_ast_source();
		}
		else if(ast_location instanceof AstLogicBinaryExpression) {
			/* as the assignment to save value of loperand|roperand in &&, || */
			ast_location = cir_location.get_rvalue().get_ast_source();
		}
		else {
			throw new IllegalArgumentException(ast_location.getClass().getSimpleName());
		}
		return this.new_cir_node(UniAbstractSType.assg_stmt, ast_location, cir_location);
	}
	private	UniAbstractStore	loc_cir_ini_assign_stmt(CirInitAssignStatement cir_location) throws Exception {
		return this.new_cir_node(UniAbstractSType.assg_stmt, cir_location.get_ast_source(), cir_location);
	}
	private	UniAbstractStore	loc_cir_ret_assign_stmt(CirReturnAssignStatement cir_location) throws Exception {
		return this.new_cir_node(UniAbstractSType.retr_stmt, cir_location.get_ast_source(), cir_location);
	}
	private	UniAbstractStore	loc_cir_wat_assign_stmt(CirWaitAssignStatement cir_location) throws Exception {
		return this.new_cir_node(UniAbstractSType.wait_stmt, cir_location.get_ast_source(), cir_location);
	}
	private	UniAbstractStore	loc_cir_call_stmt(CirCallStatement cir_location) throws Exception {
		return this.new_cir_node(UniAbstractSType.call_stmt, cir_location.get_ast_source(), cir_location);
	}
	private	UniAbstractStore	loc_cir_case_stmt(CirCaseStatement cir_location) throws Exception {
		return this.new_cir_node(UniAbstractSType.cond_stmt, cir_location.get_ast_source(), cir_location);
	}
	private	UniAbstractStore	loc_cir_if_stmt(CirIfStatement cir_location) throws Exception {
		AstNode ast_location = cir_location.get_ast_source();
		if(ast_location == null) {
			throw new IllegalArgumentException("Invalid ast_location: null");
		}
		else if(ast_location instanceof AstConditionalExpression
				|| ast_location instanceof AstLogicBinaryExpression
				|| ast_location instanceof AstIfStatement) {
			/* if-statement as the conditional expression to branch */
			return this.new_cir_node(UniAbstractSType.cond_stmt, ast_location, cir_location);
		}
		else if(ast_location instanceof AstWhileStatement
				|| ast_location instanceof AstForStatement
				|| ast_location instanceof AstDoWhileStatement) {
			/* loop-statement as the while, for, do_while statement */
			return this.new_cir_node(UniAbstractSType.loop_stmt, ast_location, cir_location);
		}
		else {
			throw new IllegalArgumentException(ast_location.getClass().getSimpleName());
		}
	}
	private	UniAbstractStore	loc_cir_beg_stmt(CirBegStatement cir_location) throws Exception {
		return this.new_cir_node(UniAbstractSType.bend_node, cir_location.get_parent().get_ast_source(), cir_location);
	}
	private	UniAbstractStore	loc_cir_end_stmt(CirEndStatement cir_location) throws Exception {
		return this.new_cir_node(UniAbstractSType.bend_node, cir_location.get_parent().get_ast_source(), cir_location);
	}
	private	UniAbstractStore	loc_cir_default_stmt(CirDefaultStatement cir_location) throws Exception {
		return this.new_cir_node(UniAbstractSType.labl_node, cir_location.get_ast_source(), cir_location);
	}
	private	UniAbstractStore	loc_cir_if_end_stmt(CirIfEndStatement cir_location) throws Exception {
		return this.new_cir_node(UniAbstractSType.conv_node, cir_location.get_ast_source(), cir_location);
	}
	private	UniAbstractStore	loc_cir_case_end_stmt(CirCaseEndStatement cir_location) throws Exception {
		return this.new_cir_node(UniAbstractSType.conv_node, cir_location.get_ast_source(), cir_location);
	}
	private	UniAbstractStore	loc_cir_label_stmt(CirLabelStatement cir_location) throws Exception {
		return this.new_cir_node(UniAbstractSType.labl_node, cir_location.get_ast_source(), cir_location);
	}
	private	UniAbstractStore	loc_cir_goto_stmt(CirGotoStatement cir_location) throws Exception {
		AstNode ast_location = cir_location.get_ast_source();
		if(ast_location == null) {
			CirExecution execution = cir_location.execution_of();
			execution = execution.get_in_flow(0).get_source();
			ast_location = execution.get_statement().get_ast_source();
			return this.new_cir_node(UniAbstractSType.gend_node, ast_location, cir_location);
		}
		else if(ast_location instanceof AstBreakStatement || 
				ast_location instanceof AstContinueStatement ||
				ast_location instanceof AstIfStatement) {
			return this.new_cir_node(UniAbstractSType.gend_node, ast_location, cir_location);
		}
		else if(ast_location instanceof AstGotoStatement || 
				ast_location instanceof AstSwitchStatement) {
			return this.new_cir_node(UniAbstractSType.skip_node, ast_location, cir_location);
		}
		else if(ast_location instanceof AstForStatement ||
				ast_location instanceof AstWhileStatement ||
				ast_location instanceof AstDoWhileStatement) {
			return this.new_cir_node(UniAbstractSType.rlop_node, ast_location, cir_location);
		}
		else if(ast_location instanceof AstReturnStatement) {
			return this.new_cir_node(UniAbstractSType.retr_stmt, ast_location, cir_location);
		}
		else {
			throw new IllegalArgumentException(ast_location.getClass().getSimpleName());
		}
	}
	/* CIR-Expression Classes */
	private	UniAbstractStore	loc_cir_declarator(CirDeclarator cir_location) throws Exception {
		return this.new_cir_expr(cir_location.get_ast_source(), cir_location);
	}
	private	UniAbstractStore	loc_cir_identifier(CirIdentifier cir_location) throws Exception {
		return this.new_cir_expr(cir_location.get_ast_source(), cir_location);
	}
	private	UniAbstractStore	loc_cir_implicator(CirImplicator cir_location) throws Exception {
		AstNode ast_location = cir_location.get_ast_source();
		if(ast_location == null) {
			throw new IllegalArgumentException("Invalid ast_location as null");
		}
		else if(ast_location instanceof AstSwitchStatement) {	/* switch.condition */
			ast_location = this.der_ast_expr(((AstSwitchStatement) ast_location).get_condition());
		}
		else if(ast_location instanceof AstConditionalExpression) {
			CirNode parent = cir_location.get_parent();
			if(parent instanceof CirSaveAssignStatement) {	/* true|false operand */
				ast_location = ((CirSaveAssignStatement) parent).get_rvalue().get_ast_source();
				ast_location = this.der_ast_expr((AstExpression) ast_location);
			}
		}
		else if(ast_location instanceof AstFunCallExpression) { /* as wait-lvalue */ }
		else if(ast_location instanceof AstIncrePostfixExpression) { /* to save temporal value of x++ */ 
			ast_location = ((AstIncrePostfixExpression) ast_location).get_operand();
		}
		else if(ast_location instanceof AstLogicBinaryExpression) {
			CirNode parent = cir_location.get_parent();
			if(parent instanceof CirSaveAssignStatement) {	/* true|false operand */
				ast_location = ((AstLogicBinaryExpression) ast_location).get_loperand();
				ast_location = this.der_ast_expr((AstExpression) ast_location);
			}
			else if(parent instanceof CirIfStatement || parent instanceof CirLogicExpression) {
				ast_location = ((AstLogicBinaryExpression) ast_location).get_loperand();
				ast_location = this.der_ast_expr((AstExpression) ast_location);
			}
			else { /* as the temporal to save the final result of entire expression */ }
		}
		else {
			throw new IllegalArgumentException(ast_location.getClass().getSimpleName());
		}
		return this.new_cir_expr(ast_location, cir_location);
	}
	private	UniAbstractStore	loc_cir_retr_point(CirReturnPoint cir_location) throws Exception {
		return this.new_cir_expr(cir_location.get_ast_source(), cir_location);
	}
	private	UniAbstractStore	loc_cir_defer_expr(CirDeferExpression cir_location) throws Exception {
		AstNode ast_location = cir_location.get_ast_source();
		if(ast_location == null) {
			ast_location = cir_location.get_address().get_ast_source();
		}
		else if(ast_location instanceof AstArrayExpression || 
				ast_location instanceof AstPointUnaryExpression) { /* as dereference used */ }
		else {
			throw new IllegalArgumentException(ast_location.getClass().getSimpleName());
		}
		return this.new_cir_expr(ast_location, cir_location);
	}
	private	UniAbstractStore	loc_cir_field_expr(CirFieldExpression cir_location) throws Exception {
		return this.new_cir_expr(cir_location.get_ast_source(), cir_location);
	}
	private	UniAbstractStore	loc_cir_const_expr(CirConstExpression cir_location) throws Exception {
		AstNode ast_location = cir_location.get_ast_source();
		if(ast_location == null) {
			CirIncreAssignStatement statement = (CirIncreAssignStatement) cir_location.statement_of();
			ast_location = statement.get_ast_source();
			if(ast_location instanceof AstIncrePostfixExpression) {
				ast_location = ((AstIncrePostfixExpression) ast_location).get_operator();
			}
			else if(ast_location instanceof AstIncrePostfixExpression) {
				ast_location = ((AstIncrePostfixExpression) ast_location).get_operator();
			}
			else {
				throw new IllegalArgumentException(ast_location.getClass().getSimpleName());
			}
		}
		else if(ast_location instanceof AstConstant || 
				ast_location instanceof AstIdExpression || 
				ast_location instanceof AstSizeofExpression ||
				ast_location instanceof AstExpressionStatement) { /* natural constant */ }
		else {
			throw new IllegalArgumentException(ast_location.getClass().getSimpleName());
		}
		return this.new_cir_expr(ast_location, cir_location);
	}
	private	UniAbstractStore	loc_cir_str_literal(CirStringLiteral cir_location) throws Exception {
		return this.new_cir_expr(cir_location.get_ast_source(), cir_location);
	}
	private	UniAbstractStore	loc_cir_default_val(CirDefaultValue cir_location) throws Exception {
		CirInitAssignStatement statement = (CirInitAssignStatement) cir_location.statement_of();
		AstNode ast_location = statement.get_ast_source();
		if(ast_location instanceof AstInitDeclarator) {
			if(((AstInitDeclarator) ast_location).has_initializer()) {
				ast_location = ((AstInitDeclarator) ast_location).get_initializer();
				ast_location = this.der_ast_expr(ast_location);
			}
		}
		else if(ast_location instanceof AstParameterDeclaration) {
			if(((AstParameterDeclaration) ast_location).has_declarator()) {
				ast_location = ((AstParameterDeclaration) ast_location).get_declarator();
			}
		}
		else {
			throw new IllegalArgumentException("Invalid: " + ast_location);
		}
		return this.new_cir_expr(ast_location, cir_location);
	}
	private	UniAbstractStore	loc_cir_init_body(CirInitializerBody cir_location) throws Exception {
		return this.new_cir_expr(cir_location.get_ast_source(), cir_location);
	}
	private	UniAbstractStore	loc_cir_wait_expr(CirWaitExpression cir_location) throws Exception {
		return this.new_cir_expr(cir_location.get_ast_source(), cir_location);
	}
	private	UniAbstractStore	loc_cir_cast_expr(CirCastExpression cir_location) throws Exception {
		return this.new_cir_expr(cir_location.get_ast_source(), cir_location);
	}
	private	UniAbstractStore	loc_cir_addr_expr(CirAddressExpression cir_location) throws Exception {
		return this.new_cir_expr(cir_location.get_ast_source(), cir_location);
	}
	private	UniAbstractStore	loc_cir_arith_expr(CirArithExpression cir_location) throws Exception {
		AstNode ast_location = cir_location.get_ast_source();
		if(ast_location == null) {
			CirNode parent = cir_location.get_parent();
			if(parent instanceof CirIncreAssignStatement) {
				ast_location = parent.get_ast_source();
			}
			else if(parent instanceof CirDeferExpression) {
				ast_location = parent.get_ast_source();
			}
			else {
				throw new IllegalArgumentException("Invalid: " + parent);
			}
		}
		else if(ast_location instanceof AstArithAssignExpression
				|| ast_location instanceof AstArithBinaryExpression
				|| ast_location instanceof AstArithUnaryExpression) { /* usual case */ }
		else {
			throw new IllegalArgumentException("Invalid: " + ast_location);
		}
		return this.new_cir_expr(ast_location, cir_location);
	}
	private	UniAbstractStore	loc_cir_bitws_expr(CirBitwsExpression cir_location) throws Exception {
		return this.new_cir_expr(cir_location.get_ast_source(), cir_location);
	}
	private	UniAbstractStore	loc_cir_logic_expr(CirLogicExpression cir_location) throws Exception {
		AstNode ast_location = cir_location.get_ast_source();
		if(ast_location == null) {
			CirIfStatement statement = (CirIfStatement) cir_location.statement_of();
			AstLogicBinaryExpression expression = (AstLogicBinaryExpression) statement.get_ast_source();
			ast_location = this.der_ast_expr(expression.get_loperand());
		}
		return this.new_cir_expr(ast_location, cir_location);
	}
	private	UniAbstractStore	loc_cir_relate_expr(CirRelationExpression cir_location) throws Exception {
		return this.new_cir_expr(cir_location.get_ast_source(), cir_location);
	}
	
	/* AST-centered localization */
	/**
	 * It localizes the best-matched state point of AstNode under CirTree
	 * @param cir_tree
	 * @param ast_location
	 * @return
	 * @throws Exception
	 */
	protected static UniAbstractStore loc(CirTree cir_tree, AstNode ast_location) throws Exception {
		return localizer.loc_ast(cir_tree, ast_location);
	}
	/** used to localize the CirNode of AstNode **/	private	CirTree	cir_tree;
	private	UniAbstractStore	loc_ast(CirTree cir_tree, AstNode ast_location) throws Exception {
		this.cir_tree = cir_tree;
		if(ast_location == null) {
			throw new IllegalArgumentException("Invalid ast_location: null");
		}
		else if(ast_location instanceof AstStatement) {
			return this.loc_ast_statement((AstStatement) ast_location);
		}
		else if(ast_location instanceof AstExpression) {
			return this.loc_ast_expression((AstExpression) ast_location);
		}
		else {
			return this.loc_ast_otherwise(ast_location);
		}
	}
	/**
	 * @param ast_location
	 * @param cir_class		null to return all corresponding CirNode(s) of inputs
	 * @return the list of CirNode(s) referring to the ast-location with given type
	 * @throws Exception
	 */
	private CirNode				loc_ast_tree(AstNode ast_location, Class<?> cir_class) throws Exception {
		List<CirNode> cir_nodes;
		if(cir_class == null) {
			cir_nodes = this.cir_tree.get_cir_nodes(ast_location);
		}
		else {
			cir_nodes = this.cir_tree.get_cir_nodes(ast_location, cir_class);
		}
		for(int k = cir_nodes.size() - 1; k >= 0; k--) {
			CirNode cir_node = cir_nodes.get(k);
			if(cir_node.execution_of() != null) {
				return cir_node;
			}
		}
		return null;
	}
	/* AST-Specifiers Classes */
	private	UniAbstractStore	loc_ast_otherwise(AstNode ast_location) throws Exception {
		if(ast_location == null) {
			throw new IllegalArgumentException("Invalid ast_location: null");
		}
		else if(ast_location instanceof AstDeclaration) {
			return this.loc_ast_declaration((AstDeclaration) ast_location);
		}
		else if(ast_location instanceof AstInitDeclaratorList) {
			return this.loc_ast_init_declarator_list((AstInitDeclaratorList) ast_location);
		}
		else if(ast_location instanceof AstArgumentList) {
			return this.loc_ast_argument_list((AstArgumentList) ast_location);
		}
		else if(ast_location instanceof AstInitDeclarator) {
			return this.loc_ast_init_declarator((AstInitDeclarator) ast_location);
		}
		else if(ast_location instanceof AstDeclarator) {
			return this.loc_ast_declarator((AstDeclarator) ast_location);
		}
		else if(ast_location instanceof AstInitializer) {
			return this.loc_ast_initializer((AstInitializer) ast_location);
		}
		else if(ast_location instanceof AstInitializerList) {
			return this.loc_ast_initializer_list((AstInitializerList) ast_location);
		}
		else if(ast_location instanceof AstFieldInitializer) {
			return this.loc_ast_field_initializer((AstFieldInitializer) ast_location);
		}
		else if(ast_location instanceof AstTypeName) {
			return this.loc_ast_type_name((AstTypeName) ast_location);
		}
		else if(ast_location instanceof AstStatementList) {
			return this.loc_ast_stmt_list((AstStatementList) ast_location);
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + ast_location);
		}
	}
	private	UniAbstractStore	loc_ast_argument_list(AstArgumentList ast_location) throws Exception {
		CirNode cir_location = this.loc_ast_tree(ast_location, CirArgumentList.class);
		return this.new_cir_node(UniAbstractSType.args_list, ast_location, cir_location);
	}
	private	UniAbstractStore	loc_ast_declarator(AstDeclarator ast_location) throws Exception {
		while(ast_location.get_production() != DeclaratorProduction.identifier) {
			ast_location = ast_location.get_declarator();
		}
		CirNode cir_location = this.loc_ast_tree(ast_location, CirDeclarator.class);
		return this.new_cir_expr(ast_location, (CirExpression) cir_location);
	}
	private	UniAbstractStore	loc_ast_init_declarator(AstInitDeclarator ast_location) throws Exception {
		CirNode cir_location = this.loc_ast_tree(ast_location, CirInitAssignStatement.class);
		return this.new_cir_node(UniAbstractSType.assg_stmt, ast_location, cir_location);
	}
	private	UniAbstractStore	loc_ast_initializer(AstInitializer ast_location) throws Exception {
		if(ast_location.is_body()) {
			return this.loc_ast(this.cir_tree, ast_location.get_body());
		}
		else {
			return this.loc_ast(this.cir_tree, ast_location.get_expression());
		}
	}
	private	UniAbstractStore	loc_ast_initializer_list(AstInitializerList ast_location) throws Exception {
		return this.loc_ast(this.cir_tree, ast_location.get_parent());
	}
	private	UniAbstractStore	loc_ast_field_initializer(AstFieldInitializer ast_location) throws Exception {
		return this.loc_ast(this.cir_tree, ast_location.get_initializer());
	}
	private	UniAbstractStore	loc_ast_type_name(AstTypeName ast_location) throws Exception {
		CirNode cir_location = this.loc_ast_tree(ast_location, CirType.class);
		return this.new_cir_node(UniAbstractSType.type_name, ast_location, cir_location);
	}
	private	UniAbstractStore	loc_ast_stmt_list(AstStatementList ast_location) throws Exception {
		CirStatement cir_location = this.cir_tree.get_localizer().beg_statement(ast_location);
		return this.loc_cir(cir_location);
	}
	private	UniAbstractStore	loc_ast_declaration(AstDeclaration ast_location) throws Exception {
		if(ast_location.has_declarator_list()) {
			return this.loc_ast(this.cir_tree, ast_location.get_declarator_list());
		}
		else {
			throw new IllegalArgumentException("Undefined init_declarator_list.");
		}
	}
	private	UniAbstractStore	loc_ast_init_declarator_list(AstInitDeclaratorList ast_location) throws Exception {
		return this.loc_ast(this.cir_tree, ast_location.get_init_declarator(0));
	}
	/* AST-Statement Classes */
	private	UniAbstractStore	loc_ast_statement(AstStatement ast_location) throws Exception {
		if(ast_location == null) {
			throw new IllegalArgumentException("Invalid ast_location: null");
		}
		else if(ast_location instanceof AstBreakStatement) {
			return this.loc_ast_break_stmt((AstBreakStatement) ast_location);
		}
		else if(ast_location instanceof AstCompoundStatement) {
			return this.loc_ast_comp_stmt((AstCompoundStatement) ast_location);
		}
		else if(ast_location instanceof AstCaseStatement) {
			return this.loc_ast_case_stmt((AstCaseStatement) ast_location);
		}
		else if(ast_location instanceof AstContinueStatement) {
			return this.loc_ast_continue_stmt((AstContinueStatement) ast_location);
		}
		else if(ast_location instanceof AstDeclarationStatement) {
			return this.loc_ast_decl_stmt((AstDeclarationStatement) ast_location);
		}
		else if(ast_location instanceof AstDefaultStatement) {
			return this.loc_ast_default_stmt((AstDefaultStatement) ast_location);
		}
		else if(ast_location instanceof AstDoWhileStatement) {
			return this.loc_ast_do_while_stmt((AstDoWhileStatement) ast_location);
		}
		else if(ast_location instanceof AstExpressionStatement) {
			return this.loc_ast_expr_stmt((AstExpressionStatement) ast_location);
		}
		else if(ast_location instanceof AstForStatement) {
			return this.loc_ast_for_stmt((AstForStatement) ast_location);
		}
		else if(ast_location instanceof AstWhileStatement) {
			return this.loc_ast_while_stmt((AstWhileStatement) ast_location);
		}
		else if(ast_location instanceof AstGotoStatement) {
			return this.loc_ast_goto_stmt((AstGotoStatement) ast_location);
		}
		else if(ast_location instanceof AstLabeledStatement) {
			return this.loc_ast_label_stmt((AstLabeledStatement) ast_location);
		}
		else if(ast_location instanceof AstReturnStatement) {
			return this.loc_ast_return_stmt((AstReturnStatement) ast_location);
		}
		else if(ast_location instanceof AstIfStatement) {
			return this.loc_ast_if_statement((AstIfStatement) ast_location);
		}
		else if(ast_location instanceof AstSwitchStatement) {
			return this.loc_ast_switch_stmt((AstSwitchStatement) ast_location);
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + ast_location);
		}
	}
	private	UniAbstractStore	loc_ast_break_stmt(AstBreakStatement ast_location) throws Exception {
		CirNode cir_location = this.loc_ast_tree(ast_location, CirGotoStatement.class);
		return this.new_cir_node(UniAbstractSType.gend_node, ast_location, cir_location);
	}
	private	UniAbstractStore	loc_ast_comp_stmt(AstCompoundStatement ast_location) throws Exception {
		return this.loc_ast(this.cir_tree, ast_location.get_statement_list());
	}
	private	UniAbstractStore	loc_ast_continue_stmt(AstContinueStatement ast_location) throws Exception {
		CirNode cir_location = this.loc_ast_tree(ast_location, CirGotoStatement.class);
		return this.new_cir_node(UniAbstractSType.rlop_node, ast_location, cir_location);
	}
	private	UniAbstractStore	loc_ast_decl_stmt(AstDeclarationStatement ast_location) throws Exception {
		return this.loc_ast(this.cir_tree, ast_location.get_declaration());
	}
	private	UniAbstractStore	loc_ast_default_stmt(AstDefaultStatement ast_location) throws Exception {
		CirNode cir_location = this.loc_ast_tree(ast_location, CirDefaultStatement.class);
		return this.new_cir_node(UniAbstractSType.labl_node, ast_location, cir_location);
	}
	private	UniAbstractStore	loc_ast_do_while_stmt(AstDoWhileStatement ast_location) throws Exception {
		CirNode cir_location = this.loc_ast_tree(ast_location, CirIfStatement.class);
		return this.new_cir_node(UniAbstractSType.loop_stmt, ast_location, cir_location);
	}
	private	UniAbstractStore	loc_ast_expr_stmt(AstExpressionStatement ast_location) throws Exception {
		if(ast_location.has_expression()) {
			return this.loc_ast(this.cir_tree, ast_location.get_expression());
		}
		else if(ast_location.get_parent() instanceof AstForStatement) {
			AstForStatement statement = (AstForStatement) ast_location.get_parent();
			return this.loc_ast(this.cir_tree, statement);
		}
		else {
			throw new IllegalArgumentException("No-sub-expression is specified");
		}
	}
	private	UniAbstractStore	loc_ast_for_stmt(AstForStatement ast_location) throws Exception {
		CirNode cir_location = this.loc_ast_tree(ast_location, CirIfStatement.class);
		return this.new_cir_node(UniAbstractSType.loop_stmt, ast_location, cir_location);
	}
	private	UniAbstractStore	loc_ast_while_stmt(AstWhileStatement ast_location) throws Exception {
		CirNode cir_location = this.loc_ast_tree(ast_location, CirIfStatement.class);
		return this.new_cir_node(UniAbstractSType.loop_stmt, ast_location, cir_location);
	}
	private	UniAbstractStore 	loc_ast_if_statement(AstIfStatement ast_location) throws Exception {
		CirNode cir_location = this.loc_ast_tree(ast_location, CirIfStatement.class);
		return this.new_cir_node(UniAbstractSType.cond_stmt, ast_location, cir_location);
	}
	private	UniAbstractStore	loc_ast_switch_stmt(AstSwitchStatement ast_location) throws Exception {
		CirNode cir_location = this.loc_ast_tree(ast_location, CirSaveAssignStatement.class);
		return this.new_cir_node(UniAbstractSType.assg_stmt, ast_location, cir_location);
	}
	private	UniAbstractStore	loc_ast_case_stmt(AstCaseStatement ast_location) throws Exception {
		CirNode cir_location = this.loc_ast_tree(ast_location, CirCaseStatement.class);
		return this.new_cir_node(UniAbstractSType.cond_stmt, ast_location, cir_location);
	}
	private	UniAbstractStore	loc_ast_goto_stmt(AstGotoStatement ast_location) throws Exception {
		CirNode cir_location = this.loc_ast_tree(ast_location, CirGotoStatement.class);
		return this.new_cir_node(UniAbstractSType.skip_node, ast_location, cir_location);
	}
	private	UniAbstractStore	loc_ast_label_stmt(AstLabeledStatement ast_location) throws Exception {
		CirNode cir_location = this.loc_ast_tree(ast_location, CirLabelStatement.class);
		return this.new_cir_node(UniAbstractSType.labl_node, ast_location, cir_location);
	}
	private	UniAbstractStore	loc_ast_return_stmt(AstReturnStatement ast_location) throws Exception {
		CirNode cir_location;
		if(ast_location.has_expression()) {
			cir_location = this.loc_ast_tree(ast_location, CirReturnAssignStatement.class);
			return this.new_cir_node(UniAbstractSType.retr_stmt, ast_location, cir_location);
		}
		else {
			cir_location = this.loc_ast_tree(ast_location, CirGotoStatement.class);
			return this.new_cir_node(UniAbstractSType.retr_stmt, ast_location, cir_location);
		}
	}
	/* AST-Expression Classes */
	private	UniAbstractStore	loc_ast_expression(AstExpression ast_location) throws Exception {
		if(ast_location == null) {
			throw new IllegalArgumentException("Invalid ast_location: null");
		}
		else if(ast_location instanceof AstIdExpression) {
			return this.loc_ast_id_expr((AstIdExpression) ast_location);
		}
		else if(ast_location instanceof AstConstant) {
			return this.loc_ast_constant((AstConstant) ast_location);
		}
		else if(ast_location instanceof AstLiteral) {
			return this.loc_ast_literal((AstLiteral) ast_location);
		}
		else if(ast_location instanceof AstArrayExpression) {
			return this.loc_ast_array_expr((AstArrayExpression) ast_location);
		}
		else if(ast_location instanceof AstCastExpression) {
			return this.loc_ast_cast_expr((AstCastExpression) ast_location);
		}
		else if(ast_location instanceof AstCommaExpression) {
			return this.loc_ast_comma_expr((AstCommaExpression) ast_location);
		}
		else if(ast_location instanceof AstConditionalExpression) {
			return this.loc_ast_cond_expr((AstConditionalExpression) ast_location);
		}
		else if(ast_location instanceof AstConstExpression) {
			return this.loc_ast_const_expr((AstConstExpression) ast_location);
		}
		else if(ast_location instanceof AstParanthExpression) {
			return this.loc_ast_paranth_expr((AstParanthExpression) ast_location);
		}
		else if(ast_location instanceof AstSizeofExpression) {
			return this.loc_ast_sizeof_expr((AstSizeofExpression) ast_location);
		}
		else if(ast_location instanceof AstInitializerBody) {
			return this.loc_ast_initializer_body((AstInitializerBody) ast_location);
		}
		else if(ast_location instanceof AstFieldExpression) {
			return this.loc_ast_field_expr((AstFieldExpression) ast_location);
		}
		else if(ast_location instanceof AstFunCallExpression) {
			return this.loc_ast_fun_call_expr((AstFunCallExpression) ast_location);
		}
		else if(ast_location instanceof AstArithUnaryExpression) {
			return this.loc_ast_arith_unary_expr((AstArithUnaryExpression) ast_location);
		}
		else if(ast_location instanceof AstBitwiseUnaryExpression) {
			return this.loc_ast_bitws_unary_expr((AstBitwiseUnaryExpression) ast_location);
		}
		else if(ast_location instanceof AstLogicUnaryExpression) {
			return this.loc_ast_logic_unary_expr((AstLogicUnaryExpression) ast_location);
		}
		else if(ast_location instanceof AstPointUnaryExpression) {
			return this.loc_ast_point_unary_expr((AstPointUnaryExpression) ast_location);
		}
		else if(ast_location instanceof AstIncreUnaryExpression) {
			return this.loc_ast_incre_unary_expr((AstIncreUnaryExpression) ast_location);
		}
		else if(ast_location instanceof AstIncrePostfixExpression) {
			return this.loc_ast_incre_postfx_expr((AstIncrePostfixExpression) ast_location);
		}
		else if(ast_location instanceof AstArithBinaryExpression) {
			return this.loc_ast_arith_binary_expr((AstArithBinaryExpression) ast_location);
		}
		else if(ast_location instanceof AstBitwiseBinaryExpression) {
			return this.loc_ast_bitws_binary_expr((AstBitwiseBinaryExpression) ast_location);
		}
		else if(ast_location instanceof AstLogicBinaryExpression) {
			return this.loc_ast_logic_binary_expr((AstLogicBinaryExpression) ast_location);
		}
		else if(ast_location instanceof AstRelationExpression) {
			return this.loc_ast_relation_binary_expr((AstRelationExpression) ast_location);
		}
		else if(ast_location instanceof AstShiftBinaryExpression) {
			return this.loc_ast_shift_binary_expr((AstShiftBinaryExpression) ast_location);
		}
		else if(ast_location instanceof AstAssignExpression) {
			return this.loc_ast_assign_expr((AstAssignExpression) ast_location);
		}
		else if(ast_location instanceof AstArithAssignExpression) {
			return this.loc_ast_airth_assign_expr((AstArithAssignExpression) ast_location);
		}
		else if(ast_location instanceof AstBitwiseAssignExpression) {
			return this.loc_ast_bitws_assign_expr((AstBitwiseAssignExpression) ast_location);
		}
		else if(ast_location instanceof AstShiftAssignExpression) {
			return this.loc_ast_shift_assign_expr((AstShiftAssignExpression) ast_location);
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + ast_location);
		}
	}
	private	UniAbstractStore	loc_ast_array_expr(AstArrayExpression ast_location) throws Exception {
		CirExpression cir_location = (CirExpression) this.loc_ast_tree(ast_location, CirDeferExpression.class);
		return this.new_cir_expr(ast_location, cir_location);
	}
	private	UniAbstractStore	loc_ast_id_expr(AstIdExpression ast_location) throws Exception {
		CirExpression cir_location = (CirExpression) this.loc_ast_tree(ast_location, CirExpression.class);
		return this.new_cir_expr(ast_location, cir_location);
	}
	private	UniAbstractStore	loc_ast_constant(AstConstant ast_location) throws Exception {
		CirExpression cir_location = (CirExpression) this.loc_ast_tree(ast_location, CirConstExpression.class);
		return this.new_cir_expr(ast_location, cir_location);
	}
	private	UniAbstractStore	loc_ast_literal(AstLiteral ast_location) throws Exception {
		CirExpression cir_location = (CirExpression) this.loc_ast_tree(ast_location, CirStringLiteral.class);
		return this.new_cir_expr(ast_location, cir_location);
	}
	private	UniAbstractStore	loc_ast_cast_expr(AstCastExpression ast_location) throws Exception {
		CirExpression cir_location = (CirExpression) this.loc_ast_tree(ast_location, CirCastExpression.class);
		return this.new_cir_expr(ast_location, cir_location);
	}
	private	UniAbstractStore	loc_ast_comma_expr(AstCommaExpression ast_location) throws Exception {
		return this.loc_ast(this.cir_tree, ast_location.get_expression(ast_location.number_of_arguments() - 1));
	}
	private	UniAbstractStore	loc_ast_cond_expr(AstConditionalExpression ast_location) throws Exception {
		CirExpression cir_location = (CirExpression) this.loc_ast_tree(ast_location, CirImplicator.class);
		return this.new_cir_expr(ast_location, cir_location);
	}
	private	UniAbstractStore	loc_ast_const_expr(AstConstExpression ast_location) throws Exception {
		return this.loc_ast(this.cir_tree, ast_location.get_expression());
	}
	private	UniAbstractStore	loc_ast_paranth_expr(AstParanthExpression ast_location) throws Exception {
		return this.loc_ast(this.cir_tree, ast_location.get_sub_expression());
	}
	private	UniAbstractStore	loc_ast_sizeof_expr(AstSizeofExpression ast_location) throws Exception {
		CirExpression cir_location = (CirExpression) this.loc_ast_tree(ast_location, CirConstExpression.class);
		return this.new_cir_expr(ast_location, cir_location);
	}
	private	UniAbstractStore	loc_ast_initializer_body(AstInitializerBody ast_location) throws Exception {
		CirNode cir_location = this.loc_ast_tree(ast_location, CirInitializerBody.class);
		return this.new_cir_expr(ast_location, (CirExpression) cir_location);
	}
	private	UniAbstractStore	loc_ast_field_expr(AstFieldExpression ast_location) throws Exception {
		CirExpression cir_location = (CirExpression) this.loc_ast_tree(ast_location, CirFieldExpression.class);
		return this.new_cir_expr(ast_location, cir_location);
	}
	private	UniAbstractStore	loc_ast_fun_call_expr(AstFunCallExpression ast_location) throws Exception {
		CirExpression cir_location = (CirExpression) this.loc_ast_tree(ast_location, CirWaitExpression.class);
		return this.new_cir_expr(ast_location, cir_location);
	}
	private	UniAbstractStore	loc_ast_arith_unary_expr(AstArithUnaryExpression ast_location) throws Exception {
		COperator operator = ast_location.get_operator().get_operator();
		if(operator == COperator.positive) {
			return this.loc_ast(this.cir_tree, ast_location.get_operand());
		}
		else if(operator == COperator.negative) {
			CirExpression cir_location = (CirExpression) this.
					loc_ast_tree(ast_location, CirArithExpression.class);
			return this.new_cir_expr(ast_location, cir_location);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + operator.toString());
		}
	}
	private	UniAbstractStore	loc_ast_bitws_unary_expr(AstBitwiseUnaryExpression ast_location) throws Exception {
		CirExpression cir_location = (CirExpression) this.
				loc_ast_tree(ast_location, CirBitwsExpression.class);
		return this.new_cir_expr(ast_location, cir_location);
	}
	private	UniAbstractStore	loc_ast_logic_unary_expr(AstLogicUnaryExpression ast_location) throws Exception {
		CirExpression cir_location = (CirExpression) this.
				loc_ast_tree(ast_location, CirLogicExpression.class);
		return this.new_cir_expr(ast_location, cir_location);
	}
	private	UniAbstractStore	loc_ast_point_unary_expr(AstPointUnaryExpression ast_location) throws Exception {
		CirExpression cir_location = (CirExpression) this.
				loc_ast_tree(ast_location, CirExpression.class);
		return this.new_cir_expr(ast_location, cir_location);
	}
	private	UniAbstractStore	loc_ast_incre_unary_expr(AstIncreUnaryExpression ast_location) throws Exception {
		CirAssignStatement statement = (CirAssignStatement) this.
				loc_ast_tree(ast_location, CirIncreAssignStatement.class);
		return this.new_cir_expr(ast_location, statement.get_rvalue());
	}
	private	UniAbstractStore	loc_ast_incre_postfx_expr(AstIncrePostfixExpression ast_location) throws Exception {
		CirAssignStatement statement = (CirAssignStatement) this.
				loc_ast_tree(ast_location, CirSaveAssignStatement.class);
		return this.new_cir_expr(ast_location, statement.get_rvalue());
	}
	private	UniAbstractStore	loc_ast_arith_binary_expr(AstArithBinaryExpression ast_location) throws Exception {
		CirExpression cir_location = (CirExpression) this.
				loc_ast_tree(ast_location, CirArithExpression.class);
		return this.new_cir_expr(ast_location, cir_location); 
	}
	private	UniAbstractStore	loc_ast_bitws_binary_expr(AstBitwiseBinaryExpression ast_location) throws Exception {
		CirExpression cir_location = (CirExpression) this.
				loc_ast_tree(ast_location, CirBitwsExpression.class);
		return this.new_cir_expr(ast_location, cir_location); 
	}
	private	UniAbstractStore	loc_ast_shift_binary_expr(AstShiftBinaryExpression ast_location) throws Exception {
		CirExpression cir_location = (CirExpression) this.
				loc_ast_tree(ast_location, CirBitwsExpression.class);
		return this.new_cir_expr(ast_location, cir_location); 
	}
	private	UniAbstractStore	loc_ast_relation_binary_expr(AstRelationExpression ast_location) throws Exception {
		CirExpression cir_location = (CirExpression) this.
				loc_ast_tree(ast_location, CirRelationExpression.class);
		return this.new_cir_expr(ast_location, cir_location); 
	}
	private	UniAbstractStore	loc_ast_logic_binary_expr(AstLogicBinaryExpression ast_location) throws Exception {
		CirExpression cir_location = (CirExpression) this.
				loc_ast_tree(ast_location, CirImplicator.class);
		return this.new_cir_expr(ast_location, cir_location); 
	}
	private	UniAbstractStore	loc_ast_assign_expr(AstAssignExpression ast_location) throws Exception {
		CirAssignStatement statement = (CirAssignStatement) this.
				loc_ast_tree(ast_location, CirAssignStatement.class);
		return this.new_cir_expr(ast_location, statement.get_rvalue());
	}
	private	UniAbstractStore	loc_ast_airth_assign_expr(AstArithAssignExpression ast_location) throws Exception {
		CirAssignStatement statement = (CirAssignStatement) this.
				loc_ast_tree(ast_location, CirAssignStatement.class);
		return this.new_cir_expr(ast_location, statement.get_rvalue());
	}
	private	UniAbstractStore	loc_ast_bitws_assign_expr(AstBitwiseAssignExpression ast_location) throws Exception {
		CirAssignStatement statement = (CirAssignStatement) this.
				loc_ast_tree(ast_location, CirAssignStatement.class);
		return this.new_cir_expr(ast_location, statement.get_rvalue());
	}
	private	UniAbstractStore	loc_ast_shift_assign_expr(AstShiftAssignExpression ast_location) throws Exception {
		CirAssignStatement statement = (CirAssignStatement) this.
				loc_ast_tree(ast_location, CirAssignStatement.class);
		return this.new_cir_expr(ast_location, statement.get_rvalue());
	}
	
}
