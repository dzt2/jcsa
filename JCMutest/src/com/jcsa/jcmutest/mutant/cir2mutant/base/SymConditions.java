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
import com.jcsa.jcparse.lang.irlang.stmt.CirTagStatement;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.parse.symbol.SymbolEvaluator;
import com.jcsa.jcparse.parse.symbol.process.SymbolProcess;

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
	/**
	 * @param expression
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private static SymbolExpression evaluate(SymbolExpression expression, SymbolProcess context) throws Exception {
		return SymbolEvaluator.evaluate_on(expression, context);
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
	/**
	 * @param execution
	 * @return path_error:trp_exec(execution, statement, null)
	 * @throws Exception
	 */
	public static SymCondition trp_exec(CirExecution execution) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else {
			return new SymCondition(SymCategory.path_error, SymOperator.trp_exec, execution, execution.get_statement(), null);
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
	
	/* optimization methods */
	/**
	 * @param source
	 * @return recursively optimize source condition to simplified form
	 * @throws Exception
	 */
	protected static SymCondition optimize(SymCondition source, SymbolProcess context) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else {
			switch(source.get_operator()) {
			case cov_stmt:	return source;
			case eva_expr:	return SymConditions.optimize_eva_expr(source, context);
			
			case mut_expr:	return SymConditions.optimize_set_expr(source, context);
			case mut_refr:	return SymConditions.optimize_set_expr(source, context);
			case mut_stat:	return SymConditions.optimize_set_expr(source, context);
			case set_bool:	return SymConditions.optimize_set_expr(source, context);
			case set_numb:	return SymConditions.optimize_set_expr(source, context);
			case set_addr:	return SymConditions.optimize_set_expr(source, context);
			case set_auto:	return SymConditions.optimize_set_expr(source, context);
			
			case mut_flow:	return SymConditions.optimize_mut_flow(source);
			
			case ast_kill:	return source;
			case cir_kill:	return source;
			default:		return source;
			}
		}
	}
	/**
	 * @param source
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private static SymCondition optimize_eva_expr(SymCondition source, SymbolProcess context) throws Exception {
		SymbolExpression condition = source.get_parameter();
		condition = SymConditions.evaluate(condition, context);
		if(condition instanceof SymbolConstant) {
			if(((SymbolConstant) condition).get_bool()) {
				return SymConditions.cov_stmt(source.get_execution(), 1);
			}
			else {
				return SymConditions.eva_expr(source.get_execution(), Boolean.FALSE, true);
			}
		}
		else {
			return SymConditions.eva_expr(source.get_execution(), condition, true);
		}
	}
	/**
	 * @param source
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private static SymCondition optimize_set_expr(SymCondition source, SymbolProcess context) throws Exception {
		CirExpression location = (CirExpression) source.get_location();
		SymbolExpression orig_expression = SymbolFactory.sym_expression(location);
		SymbolExpression muta_expression = source.get_parameter();
		if(source.get_operator() == SymOperator.set_bool) {
			orig_expression = SymbolFactory.sym_condition(orig_expression, true);
			muta_expression = SymbolFactory.sym_condition(muta_expression, true);
		}
		orig_expression = SymConditions.evaluate(orig_expression, context);
		muta_expression = SymConditions.evaluate(muta_expression, context);
		
		if(orig_expression.equals(muta_expression)) {
			return SymConditions.eva_expr(source.get_execution(), Boolean.FALSE, true);
		}
		else {
			switch(source.get_operator()) {
			case mut_expr:	return SymConditions.mut_expr(location, muta_expression);
			case mut_refr:	return SymConditions.mut_refr(location, muta_expression);
			case mut_stat:	return SymConditions.mut_stat(location, muta_expression);
			case set_bool:	return SymConditions.set_bool(location, muta_expression);
			case set_numb:	return SymConditions.set_numb(location, muta_expression);
			case set_addr:	return SymConditions.set_addr(location, muta_expression);
			case set_auto:	return SymConditions.set_auto(location, muta_expression);
			default:		throw new IllegalArgumentException("Invalid: " + source);
			}
		}
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private static SymCondition optimize_mut_flow(SymCondition source) throws Exception {
		CirExecution orig_target = SymConditions.execution_of(source.get_location());
		CirExecution muta_target = (CirExecution) source.get_parameter().get_source();
		if(orig_target == muta_target) {
			return SymConditions.eva_expr(source.get_execution(), Boolean.FALSE, true);
		}
		else {
			return source;
		}
	}
	
	/* evaluation methods */
	/**
	 * @param source
	 * @param context
	 * @return true(satisfied); false(not satisfied); null(unknown)
	 * @throws Exception
	 */
	protected static Boolean evaluate(SymCondition source, SymbolProcess context) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else {
			switch(source.get_operator()) {
			case ast_kill:	return null;
			case cir_kill:	return null;
			
			case cov_stmt:	return SymConditions.evaluate_cov_stmt(source, context);
			case eva_expr:	return SymConditions.evaluate_eva_expr(source, context);
			
			case inc_exec:	return SymConditions.evaluate_mut_exec(source);
			case dec_exec:	return SymConditions.evaluate_mut_exec(source);
			case del_exec:	return SymConditions.evaluate_mut_exec(source);
			
			case mut_flow:	return SymConditions.evaluate_mut_flow(source);
			
			case mut_expr:	return SymConditions.evaluate_set_expr(source, context);
			case mut_refr:	return SymConditions.evaluate_set_expr(source, context);
			case mut_stat:	return SymConditions.evaluate_set_expr(source, context);
			case set_bool:	return SymConditions.evaluate_set_expr(source, context);
			case set_addr:	return SymConditions.evaluate_set_expr(source, context);
			case set_auto:	return SymConditions.evaluate_set_expr(source, context);
			case set_numb:	return SymConditions.evaluate_set_expr(source, context);
			
			case set_post:	return SymConditions.evaluate_set_post(source, context);
			case set_negt:	return SymConditions.evaluate_set_negt(source, context);
			case set_zero:	return SymConditions.evaluate_set_zero(source, context);
			case set_npos:	return SymConditions.evaluate_set_npos(source, context);
			case set_nneg:	return SymConditions.evaluate_set_nneg(source, context);
			case set_nzro:	return SymConditions.evaluate_set_nzro(source, context);
			case set_null:	return SymConditions.evaluate_set_zero(source, context);
			case set_invp:	return SymConditions.evaluate_set_nzro(source, context);
			case inc_scop:	return Boolean.TRUE;
			case dec_scop:	return Boolean.TRUE;
			case ext_scop:	return Boolean.TRUE;
			case shk_scop:	return Boolean.TRUE;
			
			case chg_bool:	return Boolean.TRUE;
			case chg_addr:	return Boolean.TRUE;
			case chg_auto:	return Boolean.TRUE;
			case chg_numb:	return Boolean.TRUE;
			case trp_exec:	return Boolean.TRUE;
				
			default:		throw new IllegalArgumentException("Invalid source: null");
			}
		}
	}
	/**
	 * @param source
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private static Boolean evaluate_cov_stmt(SymCondition source, SymbolProcess context) throws Exception {
		if(context != null) {
			CirExecution execution = source.get_execution();
			SymbolExpression key = SymbolFactory.sym_expression(execution);
			SymbolExpression expect_times = source.get_parameter();
			SymbolExpression actual_times = context.get_data_stack().load(key);
			if(actual_times != null) {
				int expect = ((SymbolConstant) expect_times).get_int();
				int actual = ((SymbolConstant) actual_times).get_int();
				return actual >= expect;
			}
			else {
				return null;
			}
		}
		else {
			return null;
		}
	}
	/**
	 * @param source
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private static Boolean evaluate_eva_expr(SymCondition source, SymbolProcess context) throws Exception {
		SymbolExpression condition = source.get_parameter();
		condition = SymConditions.evaluate(condition, context);
		if(condition instanceof SymbolConstant) {
			return ((SymbolConstant) condition).get_bool();
		}
		else {
			return null;
		}
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private static Boolean evaluate_mut_exec(SymCondition source) throws Exception {
		CirStatement statement = source.get_execution().get_statement();
		if(statement instanceof CirTagStatement) {
			return Boolean.FALSE;
		}
		else {
			return Boolean.TRUE;
		}
	}
	/**
	 * @param source
	 * @return
	 * @throws Exception
	 */
	private static Boolean evaluate_mut_flow(SymCondition source) throws Exception {
		CirExecution orig_target = SymConditions.execution_of(source.get_location());
		CirExecution muta_target = (CirExecution) source.get_parameter().get_source();
		if(orig_target == muta_target) {
			return Boolean.FALSE;
		}
		else {
			return Boolean.TRUE;
		}
	}
	/**
	 * @param source
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private static Boolean evaluate_set_expr(SymCondition source, SymbolProcess context) throws Exception {
		CirExpression location = (CirExpression) source.get_location();
		SymbolExpression orig_expression = SymbolFactory.sym_expression(location);
		SymbolExpression muta_expression = source.get_parameter();
		if(source.get_operator() == SymOperator.set_bool) {
			orig_expression = SymbolFactory.sym_condition(orig_expression, true);
			muta_expression = SymbolFactory.sym_condition(muta_expression, true);
		}
		orig_expression = SymConditions.evaluate(orig_expression, context);
		muta_expression = SymConditions.evaluate(muta_expression, context);
		
		if(orig_expression.equals(muta_expression)) {
			return Boolean.FALSE;
		}
		else {
			SymbolExpression difference = SymbolFactory.not_equals(orig_expression, muta_expression);
			difference = SymConditions.evaluate(difference, context);
			if(difference instanceof SymbolConstant) {
				return ((SymbolConstant) difference).get_bool();
			}
			else {
				return null;
			}
		}
	}
	/**
	 * @param source
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private static Boolean evaluate_set_post(SymCondition source, SymbolProcess context) throws Exception {
		SymbolExpression loperand = SymbolFactory.sym_expression(source.get_location());
		SymbolExpression roperand = SymbolFactory.sym_constant(Integer.valueOf(0));
		SymbolExpression condition = SymbolFactory.smaller_eq(loperand, roperand);
		condition = SymConditions.evaluate(condition, context);
		if(condition instanceof SymbolConstant) {
			if(((SymbolConstant) condition).get_bool()) {
				return Boolean.TRUE;
			}
			else {
				return null;
			}
		}
		else {
			return null;
		}
	}
	/**
	 * @param source
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private static Boolean evaluate_set_negt(SymCondition source, SymbolProcess context) throws Exception {
		SymbolExpression loperand = SymbolFactory.sym_expression(source.get_location());
		SymbolExpression roperand = SymbolFactory.sym_constant(Integer.valueOf(0));
		SymbolExpression condition = SymbolFactory.greater_eq(loperand, roperand);
		condition = SymConditions.evaluate(condition, context);
		if(condition instanceof SymbolConstant) {
			if(((SymbolConstant) condition).get_bool()) {
				return Boolean.TRUE;
			}
			else {
				return null;
			}
		}
		else {
			return null;
		}
	}
	/**
	 * @param source
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private static Boolean evaluate_set_zero(SymCondition source, SymbolProcess context) throws Exception {
		SymbolExpression loperand = SymbolFactory.sym_expression(source.get_location());
		SymbolExpression roperand = SymbolFactory.sym_constant(Integer.valueOf(0));
		SymbolExpression condition = SymbolFactory.not_equals(loperand, roperand);
		condition = SymConditions.evaluate(condition, context);
		if(condition instanceof SymbolConstant) {
			if(((SymbolConstant) condition).get_bool()) {
				return Boolean.TRUE;
			}
			else {
				return null;
			}
		}
		else {
			return null;
		}
	}
	/**
	 * @param source
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private static Boolean evaluate_set_npos(SymCondition source, SymbolProcess context) throws Exception {
		SymbolExpression loperand = SymbolFactory.sym_expression(source.get_location());
		SymbolExpression roperand = SymbolFactory.sym_constant(Integer.valueOf(0));
		SymbolExpression condition = SymbolFactory.greater_tn(loperand, roperand);
		condition = SymConditions.evaluate(condition, context);
		if(condition instanceof SymbolConstant) {
			if(((SymbolConstant) condition).get_bool()) {
				return Boolean.TRUE;
			}
			else {
				return null;
			}
		}
		else {
			return null;
		}
	}
	/**
	 * @param source
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private static Boolean evaluate_set_nneg(SymCondition source, SymbolProcess context) throws Exception {
		SymbolExpression loperand = SymbolFactory.sym_expression(source.get_location());
		SymbolExpression roperand = SymbolFactory.sym_constant(Integer.valueOf(0));
		SymbolExpression condition = SymbolFactory.smaller_tn(loperand, roperand);
		condition = SymConditions.evaluate(condition, context);
		if(condition instanceof SymbolConstant) {
			if(((SymbolConstant) condition).get_bool()) {
				return Boolean.TRUE;
			}
			else {
				return null;
			}
		}
		else {
			return null;
		}
	}
	/**
	 * @param source
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private static Boolean evaluate_set_nzro(SymCondition source, SymbolProcess context) throws Exception {
		SymbolExpression loperand = SymbolFactory.sym_expression(source.get_location());
		SymbolExpression roperand = SymbolFactory.sym_constant(Integer.valueOf(0));
		SymbolExpression condition = SymbolFactory.equal_with(loperand, roperand);
		condition = SymConditions.evaluate(condition, context);
		if(condition instanceof SymbolConstant) {
			if(((SymbolConstant) condition).get_bool()) {
				return Boolean.TRUE;
			}
			else {
				return null;
			}
		}
		else {
			return null;
		}
	}
	
}
