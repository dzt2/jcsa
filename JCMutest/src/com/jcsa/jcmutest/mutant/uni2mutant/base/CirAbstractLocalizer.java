package com.jcsa.jcmutest.mutant.uni2mutant.base;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstConstant;
import com.jcsa.jcparse.lang.astree.expr.base.AstIdExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncrePostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncreUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstPointUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstArrayExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConditionalExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstFieldExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstFunCallExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstSizeofExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstCaseStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstExpressionStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstSwitchStatement;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirAddressExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirArithExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirBitwsExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirCastExpression;
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
import com.jcsa.jcparse.lang.irlang.expr.CirRelationExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirReturnPoint;
import com.jcsa.jcparse.lang.irlang.expr.CirStringLiteral;
import com.jcsa.jcparse.lang.irlang.expr.CirWaitExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlowType;
import com.jcsa.jcparse.lang.irlang.stmt.CirBinAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCallStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIncreAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirInitAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirReturnAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirSaveAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirWaitAssignStatement;

/**
 * 	It implements the localization of CirNode and AstNode to locate state-point.
 * 	
 * 	@author yukimula
 *
 */
final class CirAbstractLocalizer {
	
	/** singleton **/  /* constructor */  private CirAbstractLocalizer() { }
	static final CirAbstractLocalizer localizer = new CirAbstractLocalizer();
	
	/* CIR-guided localization */
	/**
	 * It generates the abstract state location guided by the cir-node
	 * @param cir_location
	 * @return
	 * @throws Exception
	 */
	protected CirAbstractStore localize(CirNode cir_location) throws Exception {
		return localizer.loc(cir_location);
	}
	/**
	 * It generates the abstract state location guided by the cir-node
	 * @param cir_location
	 * @return
	 * @throws Exception
	 */
	private CirAbstractStore 	loc(CirNode cir_location) throws Exception {
		if(cir_location == null) {
			throw new IllegalArgumentException("Invalid cir_location: null");
		}
		else if(cir_location.execution_of() == null) {		
			throw new IllegalArgumentException("Not executional: " + cir_location);
		}
		else if(cir_location instanceof CirDeclarator) {
			return this.loc_declarator((CirDeclarator) cir_location);
		}
		else if(cir_location instanceof CirIdentifier) {
			return this.loc_identifier((CirIdentifier) cir_location);
		}
		else if(cir_location instanceof CirImplicator) {
			return this.loc_implicator((CirImplicator) cir_location);
		}
		else if(cir_location instanceof CirReturnPoint) {
			return this.loc_return_pnt((CirReturnPoint) cir_location);
		}
		else if(cir_location instanceof CirConstExpression) {
			return this.loc_const_expr((CirConstExpression) cir_location);
		}
		else if(cir_location instanceof CirStringLiteral) {
			return this.loc_str_literal((CirStringLiteral) cir_location);
		}
		else if(cir_location instanceof CirDefaultValue) {
			return this.loc_default_val((CirDefaultValue) cir_location);
		}
		else if(cir_location instanceof CirFieldExpression) {
			return this.loc_field_expr((CirFieldExpression) cir_location);
		}
		else if(cir_location instanceof CirDeferExpression) {
			return this.loc_defer_expr((CirDeferExpression) cir_location);
		}
		else if(cir_location instanceof CirAddressExpression) {
			return this.loc_addr_expr((CirAddressExpression) cir_location);
		}
		else if(cir_location instanceof CirCastExpression) {
			return this.loc_cast_expr((CirCastExpression) cir_location);
		}
		else if(cir_location instanceof CirWaitExpression) {
			return this.loc_wait_expr((CirWaitExpression) cir_location);
		}
		else if(cir_location instanceof CirInitializerBody) {
			return this.loc_init_body((CirInitializerBody) cir_location);
		}
		else if(cir_location instanceof CirArithExpression) {
			return this.loc_arith_expr((CirArithExpression) cir_location);
		}
		else if(cir_location instanceof CirBitwsExpression) {
			return this.loc_bitws_expr((CirBitwsExpression) cir_location);
		}
		else if(cir_location instanceof CirLogicExpression) {
			return this.loc_logic_expr((CirLogicExpression) cir_location);
		}
		else if(cir_location instanceof CirRelationExpression) {
			return this.loc_relate_expr((CirRelationExpression) cir_location);
		}
		else if(cir_location instanceof CirBinAssignStatement) {
			return this.loc_binr_assign((CirBinAssignStatement) cir_location);
		}
		else if(cir_location instanceof CirIncreAssignStatement) {
			return this.loc_incr_assign((CirIncreAssignStatement) cir_location);
		}
		else if(cir_location instanceof CirSaveAssignStatement) {
			return this.loc_save_assign((CirSaveAssignStatement) cir_location);
		}
		else if(cir_location instanceof CirReturnAssignStatement) {
			return this.loc_retr_assign((CirReturnAssignStatement) cir_location);
		}
		else if(cir_location instanceof CirInitAssignStatement) {
			return this.loc_init_assign((CirInitAssignStatement) cir_location);
		}
		else if(cir_location instanceof CirWaitAssignStatement) {
			return this.loc_wait_assign((CirWaitAssignStatement) cir_location);
		}
		// TODO implement the syntax-directed c-intermediate transformation
		else {
			throw new IllegalArgumentException("Unsupport: " + cir_location);
		}
	}
	/* basic expression */
	private	CirAbstractStore	loc_declarator(CirDeclarator cir_location) throws Exception {
		return CirAbstractStore.cir_expr(cir_location.get_ast_source(), cir_location);
	}
	private	CirAbstractStore	loc_identifier(CirIdentifier cir_location) throws Exception {
		return CirAbstractStore.cir_expr(cir_location.get_ast_source(), cir_location);
	}
	private	CirAbstractStore	loc_implicator(CirImplicator cir_location) throws Exception {
		/* null is invalid */
		AstNode ast_location = cir_location.get_ast_source();
		if(ast_location == null) {
			throw new IllegalArgumentException("Invalid: " + ast_location);
		}
		/* refr(case_stmt.expr, cir_location, case_execution)
		 * refr(switch_stmt.condition, cir_location, save_assign) */
		else if(ast_location instanceof AstSwitchStatement) {
			if(cir_location.statement_of() instanceof CirCaseStatement) {
				CirCaseStatement case_stmt = (CirCaseStatement) cir_location.statement_of();
				AstCaseStatement ast_case_stmt = (AstCaseStatement) case_stmt.get_ast_source();
				return CirAbstractStore.cir_expr(ast_case_stmt.get_expression(), cir_location);
			}
			else if(cir_location.get_parent() instanceof CirSaveAssignStatement) {
				ast_location = ((AstSwitchStatement) ast_location).get_condition();
				return CirAbstractStore.cir_expr(ast_location, cir_location);
			}
			else {
				return CirAbstractStore.cir_expr(ast_location, cir_location);
			}
		}
		/* refr(cond_expr.t_operand, cir_location, t_assignment) 
		 * refr(cond_expr.f_operand, cir_location, f_assignment)
		 * expr(cond_expr, cir_location (used), used_point) 	*/
		else if(ast_location instanceof AstConditionalExpression) {
			AstConditionalExpression cond_expr = (AstConditionalExpression) ast_location;
			if(cir_location.get_parent() instanceof CirSaveAssignStatement) {
				CirSaveAssignStatement save_stmt = (CirSaveAssignStatement) cir_location.get_parent();
				CirExecution exe_location = save_stmt.execution_of();
				if(exe_location.get_in_flow(0).get_type() == CirExecutionFlowType.true_flow) {
					return CirAbstractStore.cir_expr(cond_expr.get_true_branch(), cir_location);
				}
				else {
					return CirAbstractStore.cir_expr(cond_expr.get_false_branch(), cir_location);
				}
			}
			else {
				return CirAbstractStore.cir_expr(ast_location, cir_location);
			}
		}
		/* refr(call_expression, cir_location, wait_assignment)
		 * expr(call_expression, cir_location, exe_location)
		 * */
		else if(ast_location instanceof AstFunCallExpression) {
			return CirAbstractStore.cir_expr(ast_location, cir_location);
		}
		/* refr(incre_expression, cir_location, save_assignment)
		 * expr(incre_expression, cir_location, used_statement)
		 * */
		else if(ast_location instanceof AstIncrePostfixExpression) {
			return CirAbstractStore.cir_expr(ast_location, cir_location);
		}
		/* cond(logic.loperand, cir_location, IFexecution)
		 * refr(logic.loperand, cir_location, save_assign)
		 * refr(logic.roperand, cir_location, save_assign)
		 * expr(logic_expression, cir_location, execution)
		 * */
		else if(ast_location instanceof AstLogicBinaryExpression) {
			CirStatement cir_statement = cir_location.statement_of();
			AstExpression loperand = ((AstLogicBinaryExpression) ast_location).get_loperand();
			if(cir_statement instanceof CirIfStatement) {
				if(cir_statement.get_ast_source() == ast_location) {
					return CirAbstractStore.cir_expr(loperand, cir_location);
				}
				else {
					return CirAbstractStore.cir_expr(ast_location, cir_location);
				}
			}
			else if(cir_statement instanceof CirSaveAssignStatement) {
				if(cir_statement.get_ast_source() == ast_location) {
					CirExpression rvalue = ((CirSaveAssignStatement) cir_statement).get_rvalue();
					return CirAbstractStore.cir_expr(rvalue.get_ast_source(), cir_location);
				}
				else {
					return CirAbstractStore.cir_expr(ast_location, cir_location);
				}
			}
			else {
				return CirAbstractStore.cir_expr(ast_location, cir_location);
			}
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + cir_location);
		}
	}
	private	CirAbstractStore	loc_return_pnt(CirReturnPoint cir_location) throws Exception {
		/* refr(return.keyword, cir_location, retr_assignment) */
		return CirAbstractStore.cir_expr(cir_location.get_ast_source(), cir_location);
	}
	private	CirAbstractStore	loc_const_expr(CirConstExpression cir_location) throws Exception {
		AstNode ast_location = cir_location.get_ast_source();
		if(ast_location == null) {
			/* const(1) --> {++/--} */
			CirStatement cir_statement = cir_location.statement_of();
			if(cir_statement instanceof CirIncreAssignStatement) {
				ast_location = cir_statement.get_ast_source();
				if(ast_location instanceof AstIncreUnaryExpression) {
					ast_location = ((AstIncreUnaryExpression) ast_location).get_operator();
				}
				else {
					ast_location = ((AstIncrePostfixExpression) ast_location).get_operator();
				}
				return CirAbstractStore.cir_expr(ast_location, cir_location);
			}
			else {
				throw new IllegalArgumentException("Unsupport: " + cir_statement);
			}
		}
		else if(ast_location instanceof AstExpressionStatement) {
			return CirAbstractStore.cir_expr(ast_location, cir_location);
		}
		else if(ast_location instanceof AstConstant) {
			return CirAbstractStore.cir_expr(ast_location, cir_location);
		}
		else if(ast_location instanceof AstIdExpression) {
			return CirAbstractStore.cir_expr(ast_location, cir_location);
		}
		else if(ast_location instanceof AstSizeofExpression) {
			return CirAbstractStore.cir_expr(ast_location, cir_location);
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + ast_location);
		}
	}
	private CirAbstractStore	loc_str_literal(CirStringLiteral cir_location) throws Exception {
		return CirAbstractStore.cir_expr(cir_location.get_ast_source(), cir_location);
	}
	private	CirAbstractStore	loc_default_val(CirDefaultValue cir_location) throws Exception {
		CirInitAssignStatement statement = (CirInitAssignStatement) cir_location.statement_of();
		return CirAbstractStore.cir_expr(statement.get_ast_source(), cir_location);
	}
	/* composite expression */
	private	CirAbstractStore	loc_defer_expr(CirDeferExpression cir_location) throws Exception {
		AstNode ast_location = cir_location.get_ast_source();
		if(ast_location == null) {
			if(cir_location.get_parent() instanceof CirFieldExpression) {
				CirFieldExpression cir_expression = (CirFieldExpression) cir_location.get_parent();
				AstFieldExpression field_expression = (AstFieldExpression) cir_expression.get_ast_source();
				return CirAbstractStore.cir_expr(field_expression.get_body(), cir_location);
			}
			else if(cir_location.get_parent() instanceof CirCallStatement) {
				CirCallStatement cir_statement = (CirCallStatement) cir_location.get_parent();
				AstFunCallExpression call_expression = (AstFunCallExpression) cir_statement.get_ast_source();
				return CirAbstractStore.cir_expr(call_expression, cir_location);
			}
			else {
				throw new IllegalArgumentException("Unsupport: " + cir_location);
			}
		}
		else if(ast_location instanceof AstArrayExpression) {
			return CirAbstractStore.cir_expr(ast_location, cir_location);
		}
		else if(ast_location instanceof AstPointUnaryExpression) {
			return CirAbstractStore.cir_expr(ast_location, cir_location);
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + ast_location);
		}
	}
	private	CirAbstractStore	loc_field_expr(CirFieldExpression cir_location) throws Exception {
		return CirAbstractStore.cir_expr(cir_location.get_ast_source(), cir_location);
	}
	private	CirAbstractStore	loc_addr_expr(CirAddressExpression cir_location) throws Exception {
		return CirAbstractStore.cir_expr(cir_location.get_ast_source(), cir_location);
	}
	private	CirAbstractStore	loc_cast_expr(CirCastExpression cir_location) throws Exception {
		return CirAbstractStore.cir_expr(cir_location.get_ast_source(), cir_location);
	}
	private	CirAbstractStore	loc_init_body(CirInitializerBody cir_location) throws Exception {
		return CirAbstractStore.cir_expr(cir_location.get_ast_source(), cir_location);
	}
	private	CirAbstractStore 	loc_wait_expr(CirWaitExpression cir_location) throws Exception {
		return CirAbstractStore.cir_expr(cir_location.get_ast_source(), cir_location);
	}
	private	CirAbstractStore	loc_arith_expr(CirArithExpression cir_location) throws Exception {
		AstNode ast_location = cir_location.get_ast_source();
		if(ast_location == null) {
			if(cir_location.get_parent() instanceof CirDeferExpression) {
				CirDeferExpression parent = (CirDeferExpression) cir_location.get_parent();
				return CirAbstractStore.cir_expr(parent.get_ast_source(), cir_location);
			}
			else if(cir_location.get_parent() instanceof CirIncreAssignStatement) {
				CirIncreAssignStatement parent = (CirIncreAssignStatement) cir_location.get_parent();
				return CirAbstractStore.cir_expr(parent.get_ast_source(), cir_location);
			}
			else {
				throw new IllegalArgumentException("Invalid: " + cir_location);
			}
		}
		else if(ast_location instanceof AstArithAssignExpression) {
			return CirAbstractStore.cir_expr(ast_location, cir_location);
		}
		else if(ast_location instanceof AstArithBinaryExpression) {
			return CirAbstractStore.cir_expr(ast_location, cir_location);
		}
		else if(ast_location instanceof AstArithUnaryExpression) {
			return CirAbstractStore.cir_expr(ast_location, cir_location);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + ast_location);
		}
	}
	private	CirAbstractStore	loc_bitws_expr(CirBitwsExpression cir_location) throws Exception {
		return CirAbstractStore.cir_expr(cir_location.get_ast_source(), cir_location);
	}
	private	CirAbstractStore	loc_logic_expr(CirLogicExpression cir_location) throws Exception {
		AstNode ast_location = cir_location.get_ast_source();
		if(ast_location == null) {
			CirStatement statement = cir_location.statement_of();
			AstLogicBinaryExpression expression = (AstLogicBinaryExpression) statement.get_ast_source();
			return CirAbstractStore.cir_expr(expression.get_loperand(), cir_location);
		}
		else {
			return CirAbstractStore.cir_expr(ast_location, cir_location);
		}
	}
	private	CirAbstractStore	loc_relate_expr(CirRelationExpression cir_location) throws Exception {
		AstNode ast_location = cir_location.get_ast_source();
		if(ast_location instanceof AstCaseStatement) {
			ast_location = ((AstCaseStatement) ast_location).get_expression().get_expression();
		}
		return CirAbstractStore.cir_expr(ast_location, cir_location);
	}
	/* cir-based statements */
	private	CirAbstractStore	loc_binr_assign(CirBinAssignStatement cir_location) throws Exception {
		AstNode ast_location = cir_location.get_ast_source();
		return CirAbstractStore.cir_stmt(CirAbstractStoreClass.assg, ast_location, cir_location);
	}
	private	CirAbstractStore	loc_save_assign(CirSaveAssignStatement cir_location) throws Exception {
		AstNode ast_location = cir_location.get_ast_source();
		if(ast_location instanceof AstConditionalExpression) {
			AstNode ast_operand = cir_location.get_rvalue().get_ast_source();
			return CirAbstractStore.cir_stmt(CirAbstractStoreClass.assg, ast_operand, cir_location);
		}
		else if(ast_location instanceof AstIncrePostfixExpression) {
			return CirAbstractStore.cir_stmt(CirAbstractStoreClass.assg, ast_location, cir_location);
		}
		else if(ast_location instanceof AstSwitchStatement) {
			ast_location = ((AstSwitchStatement) ast_location).get_condition();
			return CirAbstractStore.cir_stmt(CirAbstractStoreClass.assg, ast_location, cir_location);
		}
		else if(ast_location instanceof AstLogicBinaryExpression) {
			AstNode ast_operand = cir_location.get_rvalue().get_ast_source();
			return CirAbstractStore.cir_stmt(CirAbstractStoreClass.assg, ast_operand, cir_location);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + ast_location);
		}
	}
	private	CirAbstractStore	loc_incr_assign(CirIncreAssignStatement cir_location) throws Exception {
		return CirAbstractStore.cir_stmt(CirAbstractStoreClass.assg, cir_location.get_ast_source(), cir_location);
	}
	private	CirAbstractStore	loc_retr_assign(CirReturnAssignStatement cir_location) throws Exception {
		return CirAbstractStore.cir_stmt(CirAbstractStoreClass.retr, cir_location.get_ast_source(), cir_location);
	}
	private	CirAbstractStore	loc_init_assign(CirInitAssignStatement cir_location) throws Exception {
		return CirAbstractStore.cir_stmt(CirAbstractStoreClass.assg, cir_location.get_ast_source(), cir_location);
	}
	private CirAbstractStore	loc_wait_assign(CirWaitAssignStatement cir_location) throws Exception {
		return CirAbstractStore.cir_stmt(CirAbstractStoreClass.wait, cir_location.get_ast_source(), cir_location);
	}
	
	
	
	
	
	
}
