package com.jcsa.jcmutest.mutant.cir2mutant.base;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirTagStatement;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.parse.symbol.process.SymbolProcess;


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
	private static CirExecution find_execution_of(Mutant mutant) throws Exception {
		AstNode ast_location = mutant.get_mutation().get_location();
		CirTree cir_tree = mutant.get_space().get_cir_tree();
		while(ast_location != null) {
			if(ast_location instanceof AstFunctionDefinition) {
				for(CirFunction function : cir_tree.get_function_call_graph().get_functions()) {
					if(function.get_definition().get_ast_source() == ast_location) {
						return function.get_flow_graph().get_exit();
					}
				}
			}
		}
		return cir_tree.get_function_call_graph().get_main_function().get_flow_graph().get_exit();
	}
	/**
	 * @param mutant
	 * @return observation:kil_muta(end, end.statement, integer)
	 * @throws Exception
	 */
	public static SymCondition kil_muta(Mutant mutant) throws Exception {
		if(mutant == null) {
			throw new IllegalArgumentException("Invalid mutant: null");
		}
		else {
			CirExecution execution = find_execution_of(mutant);
			SymbolExpression parameter = SymbolFactory.sym_constant(Integer.valueOf(mutant.get_id()));
			return new SymCondition(SymCategory.observation, SymOperator.kil_muta, execution, execution.get_statement(), parameter);
		}
	}
	
	/* evaluation interfaces */
	private static SymbolExpression eval_on(SymbolExpression expression, SymbolProcess context) throws Exception {
		return expression.evaluate(context);
	}
	/**
	 * @param source
	 * @param context
	 * @return optimized form of symbolic condition
	 * @throws Exception
	 */
	public static SymCondition optimize(SymCondition source, SymbolProcess context) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source as null");
		}
		else {
			switch(source.get_operator()) {
			case cov_stmt:	return CirMutations.optimize_cov_stmt(source, context);
			case eva_expr:	return CirMutations.optimize_eva_expr(source, context);
			case mut_flow:	return CirMutations.optimize_mut_flow(source, context);
			case mut_expr:	return CirMutations.optimize_mut_expr(source, context);
			case mut_refr:	return CirMutations.optimize_mut_refr(source, context);
			case mut_stat:	return CirMutations.optimize_mut_stat(source, context);
			case set_bool:	return CirMutations.optimize_set_bool(source, context);
			case set_auto:	return CirMutations.optimize_set_auto(source, context);
			case set_numb:	return CirMutations.optimize_set_numb(source, context);
			case set_addr:	return CirMutations.optimize_set_addr(source, context);
			case add_stmt:	return CirMutations.optimize_add_stmt(source, context);
			case del_stmt:	return CirMutations.optimize_del_stmt(source, context);
			default: 		return source;
			}
		}
	}
	/**
	 * @param source
	 * @param context
	 * @return cov_stmt(execution, statement, loop_times)
	 * @throws Exception
	 */
	private static SymCondition optimize_cov_stmt(SymCondition source, SymbolProcess context) throws Exception {
		if(context != null) {
			SymbolExpression number = context.get_data_stack().peek_block().load(source.get_execution());
			if(number != null) {
				int number_value = ((SymbolConstant) number).get_int().intValue();
				int expect_value = ((SymbolConstant) source.get_parameter()).get_int().intValue();
				if(expect_value <= number_value) {
					return CirMutations.eva_expr(source.get_execution(), Boolean.TRUE);
				}
			}
		}
		return source;
	}
	/**
	 * @param source
	 * @param context
	 * @return eva_expr(execution, statement, optimize(expression))
	 * @throws Exception
	 */
	private static SymCondition optimize_eva_expr(SymCondition source, SymbolProcess context) throws Exception {
		return CirMutations.eva_expr(source.get_execution(), eval_on(source.get_parameter(), context));
	}
	/**
	 * @param source
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private static SymCondition optimize_mut_flow(SymCondition source, SymbolProcess context) throws Exception {
		CirExecution orig_target = CirMutations.execution_of(source.get_location());
		CirExecution muta_target = (CirExecution) source.get_parameter().get_source();
		if(orig_target == muta_target) {
			return CirMutations.eva_expr(source.get_execution(), Boolean.FALSE);
		}
		return source;
	}
	/**
	 * @param source
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private static SymCondition optimize_mut_expr(SymCondition source, SymbolProcess context) throws Exception {
		SymbolExpression orig_expression = SymbolFactory.sym_expression(source.get_location());
		SymbolExpression muta_expression = source.get_parameter();
		orig_expression = CirMutations.eval_on(orig_expression, context);
		muta_expression = CirMutations.eval_on(muta_expression, context);
		if(orig_expression.equals(muta_expression)) {
			return CirMutations.eva_expr(source.get_execution(), Boolean.FALSE);
		}
		else {
			return CirMutations.mut_expr((CirExpression) source.get_location(), muta_expression);
		}
	}
	/**
	 * @param source
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private static SymCondition optimize_mut_refr(SymCondition source, SymbolProcess context) throws Exception {
		SymbolExpression orig_expression = SymbolFactory.sym_expression(source.get_location());
		SymbolExpression muta_expression = source.get_parameter();
		orig_expression = CirMutations.eval_on(orig_expression, context);
		muta_expression = CirMutations.eval_on(muta_expression, context);
		if(orig_expression.equals(muta_expression)) {
			return CirMutations.eva_expr(source.get_execution(), Boolean.FALSE);
		}
		else {
			return CirMutations.mut_refr((CirExpression) source.get_location(), muta_expression);
		}
	}
	/**
	 * @param source
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private static SymCondition optimize_mut_stat(SymCondition source, SymbolProcess context) throws Exception {
		SymbolExpression orig_expression = SymbolFactory.sym_expression(source.get_location());
		SymbolExpression muta_expression = source.get_parameter();
		orig_expression = CirMutations.eval_on(orig_expression, context);
		muta_expression = CirMutations.eval_on(muta_expression, context);
		if(orig_expression.equals(muta_expression)) {
			return CirMutations.eva_expr(source.get_execution(), Boolean.FALSE);
		}
		else {
			return CirMutations.mut_stat((CirExpression) source.get_location(), muta_expression);
		}
	}
	/**
	 * @param source
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private static SymCondition optimize_set_bool(SymCondition source, SymbolProcess context) throws Exception {
		SymbolExpression orig_expression = SymbolFactory.sym_condition(source.get_location(), true);
		SymbolExpression muta_expression = SymbolFactory.sym_condition(source.get_parameter(), true);
		orig_expression = CirMutations.eval_on(orig_expression, context);
		muta_expression = CirMutations.eval_on(muta_expression, context);
		if(orig_expression.equals(muta_expression)) {
			return CirMutations.eva_expr(source.get_execution(), Boolean.FALSE);
		}
		else {
			return CirMutations.set_bool((CirExpression) source.get_location(), muta_expression);
		}
	}
	/**
	 * @param source
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private static SymCondition optimize_set_numb(SymCondition source, SymbolProcess context) throws Exception {
		SymbolExpression orig_expression = SymbolFactory.sym_expression(source.get_location());
		SymbolExpression muta_expression = source.get_parameter();
		orig_expression = CirMutations.eval_on(orig_expression, context);
		muta_expression = CirMutations.eval_on(muta_expression, context);
		if(orig_expression.equals(muta_expression)) {
			return CirMutations.eva_expr(source.get_execution(), Boolean.FALSE);
		}
		else {
			return CirMutations.set_bool((CirExpression) source.get_location(), muta_expression);
		}
	}
	/**
	 * @param source
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private static SymCondition optimize_set_addr(SymCondition source, SymbolProcess context) throws Exception {
		SymbolExpression orig_expression = SymbolFactory.sym_expression(source.get_location());
		SymbolExpression muta_expression = source.get_parameter();
		orig_expression = CirMutations.eval_on(orig_expression, context);
		muta_expression = CirMutations.eval_on(muta_expression, context);
		if(orig_expression.equals(muta_expression)) {
			return CirMutations.eva_expr(source.get_execution(), Boolean.FALSE);
		}
		else {
			return CirMutations.set_addr((CirExpression) source.get_location(), muta_expression);
		}
	}
	/**
	 * @param source
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private static SymCondition optimize_set_auto(SymCondition source, SymbolProcess context) throws Exception {
		SymbolExpression orig_expression = SymbolFactory.sym_expression(source.get_location());
		SymbolExpression muta_expression = source.get_parameter();
		orig_expression = CirMutations.eval_on(orig_expression, context);
		muta_expression = CirMutations.eval_on(muta_expression, context);
		if(orig_expression.equals(muta_expression)) {
			return CirMutations.eva_expr(source.get_execution(), Boolean.FALSE);
		}
		else {
			return CirMutations.set_auto((CirExpression) source.get_location(), muta_expression);
		}
	}
	/**
	 * @param source
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private static SymCondition optimize_add_stmt(SymCondition source, SymbolProcess context) throws Exception {
		CirStatement statement = source.get_execution().get_statement();
		if(statement instanceof CirTagStatement) {
			return CirMutations.eva_expr(source.get_execution(), Boolean.FALSE);
		}
		else {
			return source;
		}
	}
	/**
	 * @param source
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private static SymCondition optimize_del_stmt(SymCondition source, SymbolProcess context) throws Exception {
		CirStatement statement = source.get_execution().get_statement();
		if(statement instanceof CirTagStatement) {
			return CirMutations.eva_expr(source.get_execution(), Boolean.FALSE);
		}
		else {
			return source;
		}
	}
	
	/* validation interfaces */
	/**
	 * @param source
	 * @param context
	 * @return 
	 * @throws Exception
	 */
	public static Boolean validate(SymCondition source, SymbolProcess context) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source as null");
		}
		else {
			switch(source.get_operator()) {
			case cov_stmt:	return CirMutations.validate_cov_stmt(source, context);
			case eva_expr:	return CirMutations.validate_eva_expr(source, context);
			case add_stmt:	return CirMutations.validate_add_stmt(source, context);
			case del_stmt:	return CirMutations.validate_del_stmt(source, context);
			case trp_stmt:	return CirMutations.validate_trp_stmt(source, context);
			case mut_flow:	return CirMutations.validate_mut_flow(source, context);
			case mut_expr:
			case mut_refr:
			case mut_stat:
			case set_numb:
			case set_auto:
			case set_addr:	return CirMutations.validate_mut_expr(source, context);
			case inc_scop:
			case dec_scop:
			case ext_scop:
			case shk_scop:	
			case set_null:
			case set_invp:
			case chg_bool:
			case chg_numb:
			case chg_addr:
			case chg_auto:	return Boolean.TRUE;
			case set_bool:	return CirMutations.validate_set_bool(source, context);
			case set_true:	return CirMutations.validate_set_true(source, context);
			case set_fals:	return CirMutations.validate_set_fals(source, context);
			case set_zero:	return CirMutations.validate_set_zero(source, context);
			case set_post:	return CirMutations.validate_set_post(source, context);
			case set_negt:	return CirMutations.validate_set_negt(source, context);
			case set_npos:	return CirMutations.validate_set_npos(source, context);
			case set_nneg:	return CirMutations.validate_set_nneg(source, context);
			case set_nzro:	return CirMutations.validate_set_nzro(source, context);
			default:		return null;
			}
		}
	}
	/**
	 * @param source
	 * @param context
	 * @return 
	 * @throws Exception
	 */
	private static Boolean validate_cov_stmt(SymCondition source, SymbolProcess context) throws Exception {
		if(context != null) {
			SymbolExpression number = context.get_data_stack().peek_block().load(source.get_execution());
			if(number != null) {
				int number_value = ((SymbolConstant) number).get_int().intValue();
				int expect_value = ((SymbolConstant) source.get_parameter()).get_int().intValue();
				if(expect_value <= number_value) {
					return Boolean.TRUE;
				}
				else {
					return Boolean.FALSE;
				}
			}
		}
		return null;
	}
	/**
	 * @param source
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private static Boolean validate_eva_expr(SymCondition source, SymbolProcess context) throws Exception {
		SymbolExpression condition = source.get_parameter();
		condition = CirMutations.eval_on(condition, context);
		if(condition instanceof SymbolConstant) {
			return ((SymbolConstant) condition).get_bool();
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
	private static Boolean validate_mut_flow(SymCondition source, SymbolProcess context) throws Exception {
		CirExecution orig_target = CirMutations.execution_of(source.get_location());
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
	private static Boolean validate_add_stmt(SymCondition source, SymbolProcess context) throws Exception {
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
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private static Boolean validate_del_stmt(SymCondition source, SymbolProcess context) throws Exception {
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
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private static Boolean validate_trp_stmt(SymCondition source, SymbolProcess context) throws Exception { return Boolean.TRUE; }
	/**
	 * @param source
	 * @param context
	 * @return 
	 * @throws Exception
	 */
	private static Boolean validate_mut_expr(SymCondition source, SymbolProcess context) throws Exception {
		SymbolExpression orig_expression = SymbolFactory.sym_expression(source.get_location());
		SymbolExpression muta_expression = source.get_parameter();
		orig_expression = CirMutations.eval_on(orig_expression, context);
		muta_expression = CirMutations.eval_on(muta_expression, context);
		if(orig_expression.equals(muta_expression)) {
			return Boolean.FALSE;
		}
		else {
			return null;
		}
	}
	/**
	 * @param source
	 * @param context
	 * @return
	 * @throws Excecption
	 */
	private static Boolean validate_set_bool(SymCondition source, SymbolProcess context) throws Exception {
		SymbolExpression orig_expression = SymbolFactory.sym_condition(source.get_location(), true);
		SymbolExpression muta_expression = SymbolFactory.sym_condition(source.get_parameter(), true);
		orig_expression = CirMutations.eval_on(orig_expression, context);
		muta_expression = CirMutations.eval_on(muta_expression, context);
		if(orig_expression.equals(muta_expression)) {
			return Boolean.FALSE;
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
	private static Boolean validate_set_true(SymCondition source, SymbolProcess context) throws Exception {
		SymbolExpression orig_expression = SymbolFactory.sym_condition(source.get_location(), true);
		orig_expression = CirMutations.eval_on(orig_expression, context);
		if(orig_expression instanceof SymbolConstant) {
			if(((SymbolConstant) orig_expression).get_bool()) {
				return Boolean.FALSE;
			}
			else {
				return Boolean.TRUE;
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
	private static Boolean validate_set_fals(SymCondition source, SymbolProcess context) throws Exception {
		SymbolExpression orig_expression = SymbolFactory.sym_condition(source.get_location(), true);
		orig_expression = CirMutations.eval_on(orig_expression, context);
		if(orig_expression instanceof SymbolConstant) {
			if(((SymbolConstant) orig_expression).get_bool()) {
				return Boolean.TRUE;
			}
			else {
				return Boolean.FALSE;
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
	private static Boolean validate_set_zero(SymCondition source, SymbolProcess context) throws Exception {
		SymbolExpression orig_expression = SymbolFactory.sym_expression(source.get_location());
		orig_expression = CirMutations.eval_on(orig_expression, context);
		if(orig_expression instanceof SymbolConstant) {
			Object number = ((SymbolConstant) orig_expression).get_number();
			if(number instanceof Long) {
				return ((Long) number).longValue() != 0;
			}
			else {
				return ((Double) number).doubleValue() != 0;
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
	private static Boolean validate_set_post(SymCondition source, SymbolProcess context) throws Exception {
		SymbolExpression orig_expression = SymbolFactory.sym_expression(source.get_location());
		orig_expression = CirMutations.eval_on(orig_expression, context);
		if(orig_expression instanceof SymbolConstant) {
			Object number = ((SymbolConstant) orig_expression).get_number();
			if(number instanceof Long) {
				if(((Long) number).longValue() <= 0) {
					return Boolean.TRUE;
				}
				else {
					return null;
				}
			}
			else {
				if(((Double) number).doubleValue() <= 0) {
					return Boolean.TRUE;
				}
				else {
					return null;
				}
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
	private static Boolean validate_set_negt(SymCondition source, SymbolProcess context) throws Exception {
		SymbolExpression orig_expression = SymbolFactory.sym_expression(source.get_location());
		orig_expression = CirMutations.eval_on(orig_expression, context);
		if(orig_expression instanceof SymbolConstant) {
			Object number = ((SymbolConstant) orig_expression).get_number();
			if(number instanceof Long) {
				if(((Long) number).longValue() >= 0) {
					return Boolean.TRUE;
				}
				else {
					return null;
				}
			}
			else {
				if(((Double) number).doubleValue() >= 0) {
					return Boolean.TRUE;
				}
				else {
					return null;
				}
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
	private static Boolean validate_set_npos(SymCondition source, SymbolProcess context) throws Exception {
		SymbolExpression orig_expression = SymbolFactory.sym_expression(source.get_location());
		orig_expression = CirMutations.eval_on(orig_expression, context);
		if(orig_expression instanceof SymbolConstant) {
			Object number = ((SymbolConstant) orig_expression).get_number();
			if(number instanceof Long) {
				if(((Long) number).longValue() > 0) {
					return Boolean.TRUE;
				}
				else {
					return null;
				}
			}
			else {
				if(((Double) number).doubleValue() > 0) {
					return Boolean.TRUE;
				}
				else {
					return null;
				}
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
	private static Boolean validate_set_nneg(SymCondition source, SymbolProcess context) throws Exception {
		SymbolExpression orig_expression = SymbolFactory.sym_expression(source.get_location());
		orig_expression = CirMutations.eval_on(orig_expression, context);
		if(orig_expression instanceof SymbolConstant) {
			Object number = ((SymbolConstant) orig_expression).get_number();
			if(number instanceof Long) {
				if(((Long) number).longValue() < 0) {
					return Boolean.TRUE;
				}
				else {
					return null;
				}
			}
			else {
				if(((Double) number).doubleValue() < 0) {
					return Boolean.TRUE;
				}
				else {
					return null;
				}
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
	private static Boolean validate_set_nzro(SymCondition source, SymbolProcess context) throws Exception {
		SymbolExpression orig_expression = SymbolFactory.sym_expression(source.get_location());
		orig_expression = CirMutations.eval_on(orig_expression, context);
		if(orig_expression instanceof SymbolConstant) {
			Object number = ((SymbolConstant) orig_expression).get_number();
			if(number instanceof Long) {
				if(((Long) number).longValue() == 0) {
					return Boolean.TRUE;
				}
				else {
					return null;
				}
			}
			else {
				if(((Double) number).doubleValue() == 0) {
					return Boolean.TRUE;
				}
				else {
					return null;
				}
			}
		}
		else {
			return null;
		}
	}
	
}
