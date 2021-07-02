package com.jcsa.jcmutest.mutant.cir2mutant.base;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.cir2mutant.CirMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * It provides interfaces to create symbolic conditions and cir-mutations.
 * 
 * @author yukimula
 *
 */
public class SymConditions {
	
	/* basic methods */
	/**
	 * @param expression
	 * @return whether the expression can be taken as boolean
	 * @throws Exception
	 */
	public static boolean is_boolean(CirExpression expression) throws Exception {
		if(expression == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else {
			CType type = CTypeAnalyzer.get_value_type(expression.get_data_type());
			if(CTypeAnalyzer.is_boolean(type)) {
				return true;
			}
			else {
				CirStatement statement = expression.statement_of();
				if(statement instanceof CirIfStatement) {
					return ((CirIfStatement) statement).get_condition() == expression;
				}
				else if(statement instanceof CirCaseStatement) {
					return ((CirCaseStatement) statement).get_condition() == expression;
				}
				else {
					return false;
				}
			}
		}
	}
	/**
	 * @param expression
	 * @return type of the expression is integer, real
	 * @throws Exception
	 */
	public static boolean is_numeric(CirExpression expression) throws Exception {
		if(expression == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else {
			CType type = CTypeAnalyzer.get_value_type(expression.get_data_type());
			return CTypeAnalyzer.is_number(type);
		}
	}
	/**
	 * @param expression
	 * @return 
	 * @throws Exception
	 */
	public static boolean is_address(CirExpression expression) throws Exception {
		if(expression == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else {
			CType type = CTypeAnalyzer.get_value_type(expression.get_data_type());
			return CTypeAnalyzer.is_pointer(type);
		}
	}
	/**
	 * @param expression
	 * @return whether the expression is taken as non-numeric
	 * @throws Exception
	 */
	public static boolean is_nonauto(CirExpression expression) throws Exception {
		return !is_boolean(expression) && !is_numeric(expression) && !is_address(expression);
	}
	/**
	 * @param node
	 * @return the execution of the C-intermediate representative node
	 * @throws Exception
	 */
	public static CirExecution execution_of(CirNode node) throws Exception {
		while(node != null) {
			if(node instanceof CirStatement) {
				CirStatement statement = (CirStatement) node;
				return node.get_tree().get_localizer().get_execution(statement);
			}
			else {
				node = node.get_parent();
			}
		}
		return null;
	}
	
	/* evaluation category */
	/**
	 * @param execution
	 * @param times
	 * @return evaluation:cov_stmt(execution, statement, times)
	 * @throws Exception
	 */
	public static SymCondition cov_stmt(CirExecution execution, int times) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else if(times < 0) {
			throw new IllegalArgumentException("Invalid times: " + times);
		}
		else {
			return new SymCondition(SymCategory.evaluation, SymOperator.cov_stmt, execution, 
					execution.get_statement(), SymbolFactory.sym_constant(Integer.valueOf(times)));
		}
	}
	/**
	 * @param execution
	 * @param condition
	 * @param value
	 * @return evaluation:eva_expr(execution, statement, condition-as-value)
	 * @throws Exception
	 */
	public static SymCondition eva_expr(CirExecution execution, Object condition, boolean value) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else {
			return new SymCondition(SymCategory.evaluation, SymOperator.eva_expr, execution,
					execution.get_statement(), SymbolFactory.sym_condition(condition, value));
		}
	}
	
	/* path-error category */
	/**
	 * @param execution
	 * @return path_error:inc_exec(execution, statement, null)
	 * @throws Exception
	 */
	public static SymCondition inc_exec(CirExecution execution) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else {
			return new SymCondition(SymCategory.path_error, SymOperator.inc_exec, execution, execution.get_statement(), null);
		}
	}
	/**
	 * @param execution
	 * @return path_error:dec_exec(execution, statement, null)
	 * @throws Exception
	 */
	public static SymCondition dec_exec(CirExecution execution) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else {
			return new SymCondition(SymCategory.path_error, SymOperator.dec_exec, execution, execution.get_statement(), null);
		}
	}
	/**
	 * @param execution
	 * @return path_error:del_exec(execution, statement, null)
	 * @throws Exception
	 */
	public static SymCondition del_exec(CirExecution execution) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else {
			return new SymCondition(SymCategory.path_error, SymOperator.del_exec, execution, execution.get_statement(), null);
		}
	}
	/**
	 * @param orig_flow
	 * @param muta_flow
	 * @return path_error:mut_flow(if_execution, orig_target, muta_target)
	 * @throws Exception
	 */
	public static SymCondition mut_flow(CirExecutionFlow orig_flow, CirExecutionFlow muta_flow) throws Exception {
		if(orig_flow == null) {
			throw new IllegalArgumentException("Invalid orig_flow: null");
		}
		else if(muta_flow == null) {
			throw new IllegalArgumentException("Invalid muta_flow: null");
		}
		else if(orig_flow.get_source() != muta_flow.get_source()) {
			throw new IllegalArgumentException("Unmatched: " + orig_flow + " --> " + muta_flow);
		}
		else {
			return new SymCondition(SymCategory.path_error, SymOperator.mut_flow, orig_flow.get_source(), 
					orig_flow.get_target().get_statement(), SymbolFactory.sym_expression(muta_flow.get_target()));
		}
	}
	
	/* data_error mut_type */
	/**
	 * @param orig_expression
	 * @param muta_expression
	 * @return data_error:mut_expr(execution, orig_expr, muta_expr)
	 * @throws Exception
	 */
	public static SymCondition mut_expr(CirExpression orig_expression, Object muta_expression) throws Exception {
		if(orig_expression == null) {
			throw new IllegalArgumentException("Invalid orig_expression: null");
		}
		else if(muta_expression == null) {
			throw new IllegalArgumentException("Invalid muta_expression: null");
		}
		else {
			CirExecution execution = SymConditions.execution_of(orig_expression);
			return new SymCondition(SymCategory.data_error, SymOperator.mut_expr, execution, 
					orig_expression, SymbolFactory.sym_expression(muta_expression));
		}
	}
	/**
	 * @param orig_expression
	 * @param muta_expression
	 * @return data_error:mut_refr(execution, orig_expr, muta_expr)
	 * @throws Exception
	 */
	public static SymCondition mut_refr(CirExpression orig_expression, Object muta_expression) throws Exception {
		if(orig_expression == null) {
			throw new IllegalArgumentException("Invalid orig_expression: null");
		}
		else if(muta_expression == null) {
			throw new IllegalArgumentException("Invalid muta_expression: null");
		}
		else {
			CirExecution execution = SymConditions.execution_of(orig_expression);
			return new SymCondition(SymCategory.data_error, SymOperator.mut_refr, execution, 
					orig_expression, SymbolFactory.sym_expression(muta_expression));
		}
	}
	/**
	 * @param orig_expression
	 * @param muta_expression
	 * @return data_error:mut_stat(execution, orig_expr, muta_expr)
	 * @throws Exception
	 */
	public static SymCondition mut_stat(CirExpression orig_expression, Object muta_expression) throws Exception {
		if(orig_expression == null) {
			throw new IllegalArgumentException("Invalid orig_expression: null");
		}
		else if(muta_expression == null) {
			throw new IllegalArgumentException("Invalid muta_expression: null");
		}
		else {
			CirExecution execution = SymConditions.execution_of(orig_expression);
			return new SymCondition(SymCategory.data_error, SymOperator.mut_stat, execution, 
					orig_expression, SymbolFactory.sym_expression(muta_expression));
		}
	}
	
	/* data_error boolean */
	/**
	 * @param orig_expression
	 * @param muta_expression
	 * @return set_bool(execution, orig_expr, muta_expr as bool)
	 * @throws Exception
	 */
	public static SymCondition set_bool(CirExpression orig_expression, Object muta_expression) throws Exception {
		if(orig_expression == null) {
			throw new IllegalArgumentException("Invalid orig_expression: null");
		}
		else if(muta_expression == null) {
			throw new IllegalArgumentException("Invalid muta_expression: null");
		}
		else if(SymConditions.is_boolean(orig_expression)) {
			CirExecution execution = SymConditions.execution_of(orig_expression);
			return new SymCondition(SymCategory.data_error, SymOperator.set_bool, execution,
					orig_expression, SymbolFactory.sym_condition(muta_expression, true));
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + orig_expression);
		}
	}
	/**
	 * @param orig_expression
	 * @return data_error:set_true(execution, orig_expr, null)
	 * @throws Exception
	 */
	public static SymCondition set_true(CirExpression orig_expression) throws Exception {
		if(orig_expression == null) {
			throw new IllegalArgumentException("Invalid orig_expression: null");
		}
		else if(SymConditions.is_boolean(orig_expression)) {
			CirExecution execution = SymConditions.execution_of(orig_expression);
			return new SymCondition(SymCategory.data_error, SymOperator.set_true, execution, orig_expression, null);
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + orig_expression);
		}
	}
	/**
	 * @param orig_expression
	 * @return data_error:set_fals(execution, orig_expr, null)
	 * @throws Exception
	 */
	public static SymCondition set_fals(CirExpression orig_expression) throws Exception {
		if(orig_expression == null) {
			throw new IllegalArgumentException("Invalid orig_expression: null");
		}
		else if(SymConditions.is_boolean(orig_expression)) {
			CirExecution execution = SymConditions.execution_of(orig_expression);
			return new SymCondition(SymCategory.data_error, SymOperator.set_fals, execution, orig_expression, null);
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + orig_expression);
		}
	}
	/**
	 * @param orig_expression
	 * @return data_error:chg_bool(execution, orig_expr, null)
	 * @throws Exception
	 */
	public static SymCondition chg_bool(CirExpression orig_expression) throws Exception {
		if(orig_expression == null) {
			throw new IllegalArgumentException("Invalid orig_expression: null");
		}
		else if(SymConditions.is_boolean(orig_expression)) {
			CirExecution execution = SymConditions.execution_of(orig_expression);
			return new SymCondition(SymCategory.data_error, SymOperator.chg_bool, execution, orig_expression, null);
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + orig_expression);
		}
	}
	
	/* data_error numeric */
	/**
	 * @param orig_expression
	 * @param muta_expression
	 * @return data_error:set_numb(execution, orig_expr, muta_expr)
	 * @throws Exception
	 */
	public static SymCondition set_numb(CirExpression orig_expression, Object muta_expression) throws Exception {
		if(orig_expression == null) {
			throw new IllegalArgumentException("Invalid orig_expression: null");
		}
		else if(muta_expression == null) {
			throw new IllegalArgumentException("Invalid muta_expression: null");
		}
		else if(SymConditions.is_numeric(orig_expression)) {
			CirExecution execution = SymConditions.execution_of(orig_expression);
			return new SymCondition(SymCategory.data_error, SymOperator.set_numb, execution,
					orig_expression, SymbolFactory.sym_expression(muta_expression));
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + orig_expression);
		}
	}
	/**
	 * @param orig_expression
	 * @return data_error:set_post(execution, orig_expr, null)
	 * @throws Exception
	 */
	public static SymCondition set_post(CirExpression orig_expression) throws Exception {
		if(orig_expression == null) {
			throw new IllegalArgumentException("Invalid orig_expression: null");
		}
		else if(SymConditions.is_numeric(orig_expression)) {
			CirExecution execution = SymConditions.execution_of(orig_expression);
			return new SymCondition(SymCategory.data_error, SymOperator.set_post, execution, orig_expression, null);
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + orig_expression);
		}
	}
	/**
	 * @param orig_expression
	 * @return data_error:set_zero(execution, orig_expr, null)
	 * @throws Exception
	 */
	public static SymCondition set_zero(CirExpression orig_expression) throws Exception {
		if(orig_expression == null) {
			throw new IllegalArgumentException("Invalid orig_expression: null");
		}
		else if(SymConditions.is_numeric(orig_expression)) {
			CirExecution execution = SymConditions.execution_of(orig_expression);
			return new SymCondition(SymCategory.data_error, SymOperator.set_zero, execution, orig_expression, null);
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + orig_expression);
		}
	}
	/**
	 * @param orig_expression
	 * @return data_error:set_negt(execution, orig_expr, null)
	 * @throws Exception
	 */
	public static SymCondition set_negt(CirExpression orig_expression) throws Exception {
		if(orig_expression == null) {
			throw new IllegalArgumentException("Invalid orig_expression: null");
		}
		else if(SymConditions.is_numeric(orig_expression)) {
			CirExecution execution = SymConditions.execution_of(orig_expression);
			return new SymCondition(SymCategory.data_error, SymOperator.set_negt, execution, orig_expression, null);
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + orig_expression);
		}
	}
	/**
	 * @param orig_expression
	 * @return data_error:set_npos(execution, orig_expr, null)
	 * @throws Exception
	 */
	public static SymCondition set_npos(CirExpression orig_expression) throws Exception {
		if(orig_expression == null) {
			throw new IllegalArgumentException("Invalid orig_expression: null");
		}
		else if(SymConditions.is_numeric(orig_expression)) {
			CirExecution execution = SymConditions.execution_of(orig_expression);
			return new SymCondition(SymCategory.data_error, SymOperator.set_npos, execution, orig_expression, null);
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + orig_expression);
		}
	}
	/**
	 * @param orig_expression
	 * @return data_error:set_nzro(execution, orig_expr, null)
	 * @throws Exception
	 */
	public static SymCondition set_nzro(CirExpression orig_expression) throws Exception {
		if(orig_expression == null) {
			throw new IllegalArgumentException("Invalid orig_expression: null");
		}
		else if(SymConditions.is_numeric(orig_expression)) {
			CirExecution execution = SymConditions.execution_of(orig_expression);
			return new SymCondition(SymCategory.data_error, SymOperator.set_nzro, execution, orig_expression, null);
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + orig_expression);
		}
	}
	/**
	 * @param orig_expression
	 * @return data_error:set_nneg(execution, orig_expr, null)
	 * @throws Exception
	 */
	public static SymCondition set_nneg(CirExpression orig_expression) throws Exception {
		if(orig_expression == null) {
			throw new IllegalArgumentException("Invalid orig_expression: null");
		}
		else if(SymConditions.is_numeric(orig_expression)) {
			CirExecution execution = SymConditions.execution_of(orig_expression);
			return new SymCondition(SymCategory.data_error, SymOperator.set_nneg, execution, orig_expression, null);
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + orig_expression);
		}
	}
	/**
	 * @param orig_expression
	 * @return data_error:chg_numb(execution, orig_expr, null)
	 * @throws Exception
	 */
	public static SymCondition chg_numb(CirExpression orig_expression) throws Exception {
		if(orig_expression == null) {
			throw new IllegalArgumentException("Invalid orig_expression: null");
		}
		else if(SymConditions.is_numeric(orig_expression)) {
			CirExecution execution = SymConditions.execution_of(orig_expression);
			return new SymCondition(SymCategory.data_error, SymOperator.chg_numb, execution, orig_expression, null);
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + orig_expression);
		}
	}
	
	/* data_error pointer */
	/**
	 * @param orig_expression
	 * @param muta_expression
	 * @return data_error:set_addr(execution, orig_expr, muta_expr)
	 * @throws Exception
	 */
	public static SymCondition set_addr(CirExpression orig_expression, Object muta_expression) throws Exception {
		if(orig_expression == null) {
			throw new IllegalArgumentException("Invalid orig_expression: null");
		}
		else if(muta_expression == null) {
			throw new IllegalArgumentException("Invalid muta_expression: null");
		}
		else if(SymConditions.is_address(orig_expression)) {
			CirExecution execution = SymConditions.execution_of(orig_expression);
			return new SymCondition(SymCategory.data_error, SymOperator.set_addr, execution,
					orig_expression, SymbolFactory.sym_expression(muta_expression));
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + orig_expression);
		}
	}
	/**
	 * @param orig_expression
	 * @return data_error:set_null(execution, orig_expr, null)
	 * @throws Exception
	 */
	public static SymCondition set_null(CirExpression orig_expression) throws Exception {
		if(orig_expression == null) {
			throw new IllegalArgumentException("Invalid orig_expression: null");
		}
		else if(SymConditions.is_address(orig_expression)) {
			CirExecution execution = SymConditions.execution_of(orig_expression);
			return new SymCondition(SymCategory.data_error, SymOperator.set_null, execution, orig_expression, null);
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + orig_expression);
		}
	}
	/**
	 * @param orig_expression
	 * @return data_error:set_invp(execution, orig_expr, null)
	 * @throws Exception
	 */
	public static SymCondition set_invp(CirExpression orig_expression) throws Exception {
		if(orig_expression == null) {
			throw new IllegalArgumentException("Invalid orig_expression: null");
		}
		else if(SymConditions.is_address(orig_expression)) {
			CirExecution execution = SymConditions.execution_of(orig_expression);
			return new SymCondition(SymCategory.data_error, SymOperator.set_invp, execution, orig_expression, null);
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + orig_expression);
		}
	}
	/**
	 * @param orig_expression
	 * @return data_error:chg_addr(execution, orig_expr, null)
	 * @throws Exception
	 */
	public static SymCondition chg_addr(CirExpression orig_expression) throws Exception {
		if(orig_expression == null) {
			throw new IllegalArgumentException("Invalid orig_expression: null");
		}
		else if(SymConditions.is_address(orig_expression)) {
			CirExecution execution = SymConditions.execution_of(orig_expression);
			return new SymCondition(SymCategory.data_error, SymOperator.chg_addr, execution, orig_expression, null);
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + orig_expression);
		}
	}
	
	/* data_error auto */
	/**
	 * @param orig_expression
	 * @param muta_expression
	 * @return data_error:set_auto(execution, orig_expr, muta_expr)
	 * @throws Exception
	 */
	public static SymCondition set_auto(CirExpression orig_expression, Object muta_expression) throws Exception {
		if(orig_expression == null) {
			throw new IllegalArgumentException("Invalid orig_expression: null");
		}
		else if(muta_expression == null) {
			throw new IllegalArgumentException("Invalid muta_expression: null");
		}
		else if(SymConditions.is_nonauto(orig_expression)) {
			CirExecution execution = SymConditions.execution_of(orig_expression);
			return new SymCondition(SymCategory.data_error, SymOperator.set_auto, execution,
					orig_expression, SymbolFactory.sym_expression(muta_expression));
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + orig_expression);
		}
	}
	/**
	 * @param orig_expression
	 * @param muta_expression
	 * @return data_error:chg_auto(execution, orig_expr, null)
	 * @throws Exception
	 */
	public static SymCondition chg_auto(CirExpression orig_expression) throws Exception {
		if(orig_expression == null) {
			throw new IllegalArgumentException("Invalid orig_expression: null");
		}
		else if(SymConditions.is_nonauto(orig_expression)) {
			CirExecution execution = SymConditions.execution_of(orig_expression);
			return new SymCondition(SymCategory.data_error, SymOperator.chg_auto, execution,
					orig_expression, null);
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + orig_expression);
		}
	}
	
	/* data_error scopes */
	/**
	 * @param orig_expression
	 * @return data_error:inc_scop(execution, orig_expr, null)
	 * @throws Exception
	 */
	public static SymCondition inc_scop(CirExpression orig_expression) throws Exception {
		if(orig_expression == null) {
			throw new IllegalArgumentException("Invalid orig_expression: null");
		}
		else if(SymConditions.is_numeric(orig_expression) ||
				SymConditions.is_address(orig_expression)) {
			CirExecution execution = SymConditions.execution_of(orig_expression);
			return new SymCondition(SymCategory.data_error, SymOperator.inc_scop, execution, orig_expression, null);
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + orig_expression);
		}
	}
	/**
	 * @param orig_expression
	 * @return data_error:dec_scop(execution, orig_expr, null)
	 * @throws Exception
	 */
	public static SymCondition dec_scop(CirExpression orig_expression) throws Exception {
		if(orig_expression == null) {
			throw new IllegalArgumentException("Invalid orig_expression: null");
		}
		else if(SymConditions.is_numeric(orig_expression) ||
				SymConditions.is_address(orig_expression)) {
			CirExecution execution = SymConditions.execution_of(orig_expression);
			return new SymCondition(SymCategory.data_error, SymOperator.dec_scop, execution, orig_expression, null);
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + orig_expression);
		}
	}
	/**
	 * @param orig_expression
	 * @return data_error:ext_scop(execution, orig_expr, null)
	 * @throws Exception
	 */
	public static SymCondition ext_scop(CirExpression orig_expression) throws Exception {
		if(orig_expression == null) {
			throw new IllegalArgumentException("Invalid orig_expression: null");
		}
		else if(SymConditions.is_numeric(orig_expression) ||
				SymConditions.is_address(orig_expression)) {
			CirExecution execution = SymConditions.execution_of(orig_expression);
			return new SymCondition(SymCategory.data_error, SymOperator.ext_scop, execution, orig_expression, null);
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + orig_expression);
		}
	}
	/**
	 * @param orig_expression
	 * @return data_error:shk_scop(execution, orig_expr, null)
	 * @throws Exception
	 */
	public static SymCondition shk_scop(CirExpression orig_expression) throws Exception {
		if(orig_expression == null) {
			throw new IllegalArgumentException("Invalid orig_expression: null");
		}
		else if(SymConditions.is_numeric(orig_expression) ||
				SymConditions.is_address(orig_expression)) {
			CirExecution execution = SymConditions.execution_of(orig_expression);
			return new SymCondition(SymCategory.data_error, SymOperator.shk_scop, execution, orig_expression, null);
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + orig_expression);
		}
	}
	
	/* kill_failure mutant */
	/**
	 * @param mutant
	 * @return kill_fault:ast_kill(entry, statement, mutant.id)
	 * @throws Exception
	 */
	public static SymCondition ast_kill(Mutant mutant) throws Exception {
		if(mutant == null) {
			throw new IllegalArgumentException("Invalid mutant: null");
		}
		else {
			AstNode ast_location = mutant.get_mutation().get_location();
			CirTree cir_tree = mutant.get_space().get_cir_tree();
			while(ast_location != null) {
				if(ast_location instanceof AstFunctionDefinition) {
					CirStatement statement = cir_tree.get_localizer().beg_statement(ast_location);
					CirExecution execution = SymConditions.execution_of(statement);
					return new SymCondition(SymCategory.kill_fault, SymOperator.ast_kill, execution, 
							statement, SymbolFactory.sym_constant(Integer.valueOf(mutant.get_id())));
				}
				else {
					ast_location = ast_location.get_parent();
				}
			}
			throw new IllegalArgumentException("Not belong to any function");
		}
	}
	/**
	 * @param mutant
	 * @param cir_mutation
	 * @return kill_fault:ast_kill(entry, statement, "mid:hashcode")
	 * @throws Exception
	 */
	public static SymCondition cir_kill(Mutant mutant, Object cir_mutation) throws Exception {
		if(mutant == null) {
			throw new IllegalArgumentException("Invalid mutant: null");
		}
		else {
			AstNode ast_location = mutant.get_mutation().get_location();
			CirTree cir_tree = mutant.get_space().get_cir_tree();
			while(ast_location != null) {
				if(ast_location instanceof AstFunctionDefinition) {
					CirStatement statement = cir_tree.get_localizer().beg_statement(ast_location);
					CirExecution execution = SymConditions.execution_of(statement);
					int mid = mutant.get_id(), cid = cir_mutation.toString().hashCode();
					return new SymCondition(SymCategory.kill_fault, SymOperator.cir_kill, 
							execution, statement, SymbolFactory.literal(mid + ":" + cid));
				}
				else {
					ast_location = ast_location.get_parent();
				}
			}
			throw new IllegalArgumentException("Not belong to any function");
		}
	}
	/**
	 * @param constraint
	 * @param init_error
	 * @return infection condition and initial state error
	 * @throws Exception
	 */
	public static CirMutation cir_mutation(SymCondition constraint, SymCondition init_error) throws Exception {
		return new CirMutation(constraint, init_error);
	}
	
}
