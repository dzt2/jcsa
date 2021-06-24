package com.jcsa.jcmutest.mutant.cir2mutant.base;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * It implements the factory interfaces for creating symbolic condition and cir-mutation.
 * 
 * @author yukimula
 *
 */
public class CirMutations {
	
	/* factory methods */
	/* constraints */
	/**
	 * @param execution
	 * @param times
	 * @return constraints:cov_stmt(execution, execution.statement, times)
	 * @throws Exception
	 */
	public static SymCondition cov_stmt(CirExecution execution, int times) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else if(times <= 0) {
			throw new IllegalArgumentException("Invalid times: " + times);
		}
		else {
			return new SymCondition(SymCategory.constraints, SymOperator.cov_stmt, execution, 
					execution.get_statement(), SymbolFactory.sym_constant(Integer.valueOf(times)));
		}
	}
	/**
	 * @param execution
	 * @param expression
	 * @return constraints:eva_expr(execution, execution.statement, expression)
	 * @throws Exception
	 */
	public static SymCondition eva_expr(CirExecution execution, Object expression) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else {
			return new SymCondition(SymCategory.constraints, SymOperator.eva_expr, execution,
					execution.get_statement(), SymbolFactory.sym_condition(expression, true));
		}
	}
	/* observation:bool */
	/**
	 * @param expression
	 * @return observation:chg_bool(expression.execution, expression, null)
	 * @throws Exception
	 */
	public static SymCondition chg_bool(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(CirMutations.is_boolean(expression)) {
			return new SymCondition(SymCategory.observation, SymOperator.chg_bool, 
					CirMutations.execution_of(expression), expression, null);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.get_data_type().generate_code());
		}
	}
	/**
	 * @param expression
	 * @return observation:set_true(expression.execution, expression, null)
	 * @throws Exception
	 */
	public static SymCondition set_true(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(CirMutations.is_boolean(expression)) {
			return new SymCondition(SymCategory.observation, SymOperator.set_true, 
					CirMutations.execution_of(expression), expression, null);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.get_data_type().generate_code());
		}
	}
	/**
	 * @param expression
	 * @return observation:set_fals(expression.execution, expression, null)
	 * @throws Exception
	 */
	public static SymCondition set_fals(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(CirMutations.is_boolean(expression)) {
			return new SymCondition(SymCategory.observation, SymOperator.set_fals, 
					CirMutations.execution_of(expression), expression, null);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.get_data_type().generate_code());
		}
	}
	/**
	 * @param expression
	 * @param muta_value
	 * @return observation:set_bool(expression.execution, expression, muta_value)
	 * @throws Exception
	 */
	public static SymCondition set_bool(CirExpression expression, Object muta_value) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(CirMutations.is_boolean(expression)) {
			return new SymCondition(SymCategory.observation, SymOperator.set_bool, 
					CirMutations.execution_of(expression), expression, 
					SymbolFactory.sym_condition(muta_value, true));
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.get_data_type().generate_code());
		}
	}
	/* observation:number */
	/**
	 * @param expression
	 * @return observation:chg_numb(expression.execution, expression, null)
	 * @throws Exception
	 */
	public static SymCondition chg_numb(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(CirMutations.is_numeric(expression)) {
			return new SymCondition(SymCategory.observation, SymOperator.chg_numb, 
					CirMutations.execution_of(expression), expression, null);
		}
		else if(CirMutations.is_address(expression)) {
			return new SymCondition(SymCategory.observation, SymOperator.chg_addr, 
					CirMutations.execution_of(expression), expression, null);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.get_data_type().generate_code());
		}
	}
	/**
	 * @param expression
	 * @return observation:set_post(expression.execution, expression, null)
	 * @throws Exception
	 */
	public static SymCondition set_post(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(CirMutations.is_numeric(expression)) {
			return new SymCondition(SymCategory.observation, SymOperator.set_post, 
					CirMutations.execution_of(expression), expression, null);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.get_data_type().generate_code());
		}
	}
	/**
	 * @param expression
	 * @return observation:set_negt(expression.execution, expression, null)
	 * @throws Exception
	 */
	public static SymCondition set_negt(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(CirMutations.is_numeric(expression)) {
			return new SymCondition(SymCategory.observation, SymOperator.set_negt, 
					CirMutations.execution_of(expression), expression, null);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.get_data_type().generate_code());
		}
	}
	/**
	 * @param expression
	 * @return observation:set_zero(expression.execution, expression, null)
	 * @throws Exception
	 */
	public static SymCondition set_zero(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(CirMutations.is_numeric(expression)) {
			return new SymCondition(SymCategory.observation, SymOperator.set_zero, 
					CirMutations.execution_of(expression), expression, null);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.get_data_type().generate_code());
		}
	}
	/**
	 * @param expression
	 * @return observation:set_npos(expression.execution, expression, null)
	 * @throws Exception
	 */
	public static SymCondition set_npos(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(CirMutations.is_numeric(expression)) {
			return new SymCondition(SymCategory.observation, SymOperator.set_npos, 
					CirMutations.execution_of(expression), expression, null);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.get_data_type().generate_code());
		}
	}
	/**
	 * @param expression
	 * @return observation:set_nneg(expression.execution, expression, null)
	 * @throws Exception
	 */
	public static SymCondition set_nneg(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(CirMutations.is_numeric(expression)) {
			return new SymCondition(SymCategory.observation, SymOperator.set_nneg, 
					CirMutations.execution_of(expression), expression, null);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.get_data_type().generate_code());
		}
	}
	/**
	 * @param expression
	 * @return observation:set_nzro(expression.execution, expression, null)
	 * @throws Exception
	 */
	public static SymCondition set_nzro(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(CirMutations.is_numeric(expression)) {
			return new SymCondition(SymCategory.observation, SymOperator.set_nzro, 
					CirMutations.execution_of(expression), expression, null);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.get_data_type().generate_code());
		}
	}
	/**
	 * @param expression
	 * @param muta_value
	 * @return observation:set_numb(expression.execution, expression, muta_value)
	 * @throws Exception
	 */
	public static SymCondition set_numb(CirExpression expression, Object muta_value) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(CirMutations.is_numeric(expression)) {
			return new SymCondition(SymCategory.observation, SymOperator.set_numb, 
					CirMutations.execution_of(expression), expression, 
					SymbolFactory.sym_expression(muta_value));
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.get_data_type().generate_code());
		}
	}
	/* observation:address */
	/**
	 * @param expression
	 * @return observation:chg_addr(expression.execution, expression, null)
	 * @throws Exception
	 */
	public static SymCondition chg_addr(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(CirMutations.is_address(expression)) {
			return new SymCondition(SymCategory.observation, SymOperator.chg_addr, 
					CirMutations.execution_of(expression), expression, null);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.get_data_type().generate_code());
		}
	}
	/**
	 * @param expression
	 * @return observation:set_null(expression.execution, expression, null)
	 * @throws Exception
	 */
	public static SymCondition set_null(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(CirMutations.is_address(expression)) {
			return new SymCondition(SymCategory.observation, SymOperator.set_null, 
					CirMutations.execution_of(expression), expression, null);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.get_data_type().generate_code());
		}
	}
	/**
	 * @param expression
	 * @return observation:set_null(expression.execution, expression, null)
	 * @throws Exception
	 */
	public static SymCondition set_invp(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(CirMutations.is_address(expression)) {
			return new SymCondition(SymCategory.observation, SymOperator.set_invp, 
					CirMutations.execution_of(expression), expression, null);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.get_data_type().generate_code());
		}
	}
	/**
	 * @param expression
	 * @param muta_value
	 * @return observation:set_addr(expression.execution, expression, muta_value)
	 * @throws Exception
	 */
	public static SymCondition set_addr(CirExpression expression, Object muta_value) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(CirMutations.is_address(expression)) {
			return new SymCondition(SymCategory.observation, SymOperator.set_addr, 
					CirMutations.execution_of(expression), expression, 
					SymbolFactory.sym_expression(muta_value));
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.get_data_type().generate_code());
		}
	}
	/* observation:auto */
	/**
	 * @param expression
	 * @return observation:chg_auto(expression.execution, expression, null)
	 * @throws Exception
	 */
	public static SymCondition chg_auto(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(CirMutations.is_nonauto(expression)) {
			return new SymCondition(SymCategory.observation, SymOperator.chg_auto, 
					CirMutations.execution_of(expression), expression, null);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.get_data_type().generate_code());
		}
	}
	/**
	 * @param expression
	 * @param muta_value
	 * @return observation:set_auto(expression.execution, expression, muta_value)
	 * @throws Exception
	 */
	public static SymCondition set_auto(CirExpression expression, Object muta_value) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(CirMutations.is_nonauto(expression)) {
			return new SymCondition(SymCategory.observation, SymOperator.set_auto, 
					CirMutations.execution_of(expression), expression, 
					SymbolFactory.sym_expression(muta_value));
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.get_data_type().generate_code());
		}
	}
	/* observation:value */
	/**
	 * @param expression
	 * @return observation:inc_scop(expression.execution, expression, null)
	 * @throws Exception
	 */
	public static SymCondition inc_scop(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(CirMutations.is_numeric(expression) || CirMutations.is_address(expression)) {
			return new SymCondition(SymCategory.observation, SymOperator.inc_scop, 
					CirMutations.execution_of(expression), expression, null);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.get_data_type().generate_code());
		}
	}
	/**
	 * @param expression
	 * @return observation:dec_scop(expression.execution, expression, null)
	 * @throws Exception
	 */
	public static SymCondition dec_scop(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(CirMutations.is_numeric(expression) || CirMutations.is_address(expression)) {
			return new SymCondition(SymCategory.observation, SymOperator.dec_scop, 
					CirMutations.execution_of(expression), expression, null);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.get_data_type().generate_code());
		}
	}
	/**
	 * @param expression
	 * @return observation:ext_scop(expression.execution, expression, null)
	 * @throws Exception
	 */
	public static SymCondition ext_scop(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(CirMutations.is_numeric(expression) || CirMutations.is_address(expression)) {
			return new SymCondition(SymCategory.observation, SymOperator.ext_scop, 
					CirMutations.execution_of(expression), expression, null);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.get_data_type().generate_code());
		}
	}
	/**
	 * @param expression
	 * @return observation:shk_scop(expression.execution, expression, null)
	 * @throws Exception
	 */
	public static SymCondition shk_scop(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else if(CirMutations.is_numeric(expression) || CirMutations.is_address(expression)) {
			return new SymCondition(SymCategory.observation, SymOperator.shk_scop, 
					CirMutations.execution_of(expression), expression, null);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + expression.get_data_type().generate_code());
		}
	}
	/* top:observation */
	/**
	 * @param expression
	 * @return observation:mut_expr(expression.execution, expression, value)
	 * @throws Exception
	 */
	public static SymCondition mut_expr(CirExpression expression, Object value) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else {
			return new SymCondition(SymCategory.observation, SymOperator.mut_expr,
					CirMutations.execution_of(expression), expression, 
					SymbolFactory.sym_expression(value));
		}
	}
	/**
	 * @param expression
	 * @return observation:mut_refr(expression.execution, expression, value)
	 * @throws Exception
	 */
	public static SymCondition mut_refr(CirExpression expression, Object value) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else {
			return new SymCondition(SymCategory.observation, SymOperator.mut_refr,
					CirMutations.execution_of(expression), expression, SymbolFactory.sym_expression(value));
		}
	}
	/**
	 * @param expression
	 * @return observation:mut_stat(expression.execution, expression, value)
	 * @throws Exception
	 */
	public static SymCondition mut_stat(CirExpression expression, Object value) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: " + expression);
		}
		else {
			return new SymCondition(SymCategory.observation, SymOperator.mut_stat,
					CirMutations.execution_of(expression), expression, SymbolFactory.sym_expression(value));
		}
	}
	/* observation:stmt */
	/**
	 * @param execution
	 * @return observation:add_stmt(execution, execution.statement, null)
	 * @throws Exception
	 */
	public static SymCondition add_stmt(CirExecution execution) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else {
			return new SymCondition(SymCategory.observation, SymOperator.add_stmt, execution, execution.get_statement(), null);
		}
	}
	/**
	 * @param execution
	 * @return observation:del_stmt(execution, execution.statement, null)
	 * @throws Exception
	 */
	public static SymCondition del_stmt(CirExecution execution) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else {
			return new SymCondition(SymCategory.observation, SymOperator.del_stmt, execution, execution.get_statement(), null);
		}
	}
	/**
	 * @param execution
	 * @return observation:trp_stmt(execution, execution.statement, null)
	 * @throws Exception
	 */
	public static SymCondition trp_stmt(CirExecution execution) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else {
			return new SymCondition(SymCategory.observation, SymOperator.trp_stmt, execution, execution.get_statement(), null);
		}
	}
	/**
	 * @param orig_flow
	 * @param muta_flow
	 * @return observation:mut_flow(orig_flow.source, orig_flow.target, muta_flow.target)
	 * @throws Exception
	 */
	public static SymCondition mut_flow(CirExecutionFlow orig_flow, CirExecutionFlow muta_flow) throws Exception {
		if(orig_flow == null) {
			throw new IllegalArgumentException("Invalid orig_flow: null");
		}
		else if(muta_flow == null) {
			throw new IllegalArgumentException("Invalid muta_flow: null");
		}
		else {
			return new SymCondition(SymCategory.observation, SymOperator.mut_flow,
					orig_flow.get_source(), orig_flow.get_target().get_statement(),
					SymbolFactory.sym_expression(muta_flow.get_target()));
		}
	}
	
	/* expression categorized */
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
	
	/* cir-mutation factory */
	/**
	 * @param constraint
	 * @param init_error
	 * @return create an infection-pair 
	 * @throws Exception
	 */
	public static CirMutation cir_mutation(SymCondition constraint, SymCondition init_error) throws Exception {
		return new CirMutation(constraint, init_error);
	}
	
}
