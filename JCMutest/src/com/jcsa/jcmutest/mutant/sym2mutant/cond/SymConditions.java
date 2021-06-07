package com.jcsa.jcmutest.mutant.sym2mutant.cond;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import com.jcsa.jcmutest.mutant.sym2mutant.base.SymConstraint;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymExpressionError;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymFlowError;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymInstance;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymInstances;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymReferenceError;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymTrapError;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymValueError;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionEdge;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionPath;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionPathFinder;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symbol.SymbolBinaryExpression;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.lang.symbol.SymbolFieldExpression;
import com.jcsa.jcparse.lang.symbol.SymbolIdentifier;
import com.jcsa.jcparse.lang.symbol.SymbolNode;
import com.jcsa.jcparse.lang.symbol.SymbolUnaryExpression;


/**
 * It implements interfaces for constructing and generating symbolic condition(s) from particular symbolic instance.
 * 
 * @author yukimula
 *
 */
public class SymConditions {
	
	/* classifier methods */
	/**
	 * @param expression
	 * @return whether the expression is taken as a boolean or IF-condition
	 * @throws Exception
	 */
	public static boolean is_boolean(CirExpression expression) throws Exception {
		CType type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		if(CTypeAnalyzer.is_boolean(type))
			return true;
		else {
			CirNode parent = expression.get_parent();
			if(parent instanceof CirIfStatement || parent instanceof CirCaseStatement)
				return true;
			else
				return false;
		}
	}
	/**
	 * @param expression
	 * @return whether the expression is integer or floating
	 * @throws Exception
	 */
	public static boolean is_numeric(CirExpression expression) throws Exception {
		CType type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		return CTypeAnalyzer.is_number(type);
	}
	/**
	 * @param expression
	 * @return whether the expression is pointer
	 * @throws Exception
	 */
	public static boolean is_address(CirExpression expression) throws Exception {
		CType type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		return CTypeAnalyzer.is_pointer(type);
	}
	
	/* constructors */
	/**
	 * @param execution
	 * @param expression
	 * @param value
	 * @return assertion:eva_expr(execution, statement, expression == value)
	 * @throws Exception
	 */
	public static SymCondition eva_expr(CirExecution execution, Object expression, boolean value) throws Exception {
		if(execution == null)
			throw new IllegalArgumentException("Invalid execution: null");
		else if(expression == null)
			throw new IllegalArgumentException("Invalid expression: null");
		else {
			return new SymCondition(SymCategory.assertion, SymOperator.eva_expr, 
					execution, execution.get_statement(),
					SymbolFactory.sym_condition(expression, value));
		}
	}
	/**
	 * @param execution
	 * @param operator
	 * @param times
	 * @return assertion:cov_stmt(execution, statement, statement {<, <=, ==, !=, >=, >} times)
	 * @throws Exception
	 */
	public static SymCondition cov_stmt(CirExecution execution, COperator operator, int times) throws Exception {
		if(execution == null)
			throw new IllegalArgumentException("Invalid execution: null");
		else if(operator == null)
			throw new IllegalArgumentException("Invalid operator: null");
		else if(times <= 0)
			throw new IllegalArgumentException("Invalid times: " + times);
		else {
			SymbolExpression loperand = SymbolFactory.sym_expression(execution);
			SymbolExpression roperand = SymbolFactory.sym_expression(Integer.valueOf(times));
			SymbolExpression parameter;
			switch(operator) {
			case smaller_tn:	parameter = SymbolFactory.smaller_tn(loperand, roperand);	break;
			case smaller_eq:	parameter = SymbolFactory.smaller_eq(loperand, roperand);	break;
			case equal_with:	parameter = SymbolFactory.equal_with(loperand, roperand);	break;
			case not_equals:	parameter = SymbolFactory.not_equals(loperand, roperand);	break;
			case greater_eq:	parameter = SymbolFactory.greater_eq(loperand, roperand);	break;
			case greater_tn:	parameter = SymbolFactory.greater_tn(loperand, roperand);	break;
			default: 			throw new IllegalArgumentException("Invalid operator: " + operator);
			}
			return new SymCondition(SymCategory.assertion, SymOperator.cov_stmt, 
								execution, execution.get_statement(), parameter);
		}
	}
	/**
	 * @param execution
	 * @return observation:mut_stmt(execution, statement, true);
	 * @throws Exception
	 */
	public static SymCondition add_stmt(CirExecution execution) throws Exception {
		if(execution == null)
			throw new IllegalArgumentException("Invalid execution: null");
		else 
			return new SymCondition(SymCategory.observation, SymOperator.mut_stmt, execution, 
					execution.get_statement(), SymbolFactory.sym_expression(Boolean.TRUE));
	}
	/**
	 * @param execution
	 * @return observation:mut_stmt(execution, statement, false);
	 * @throws Exception
	 */
	public static SymCondition del_stmt(CirExecution execution) throws Exception {
		if(execution == null)
			throw new IllegalArgumentException("Invalid execution: null");
		else 
			return new SymCondition(SymCategory.observation, SymOperator.mut_stmt, execution, 
					execution.get_statement(), SymbolFactory.sym_expression(Boolean.FALSE));
	}
	/**
	 * @param execution
	 * @return observation:trp_stmt(execution, statement, null)
	 * @throws Exception
	 */
	public static SymCondition trp_stmt(CirExecution execution) throws Exception {
		if(execution == null)
			throw new IllegalArgumentException("Invalid execution: null");
		else 
			return new SymCondition(SymCategory.observation, SymOperator.trp_stmt, 
										execution, execution.get_statement(), null);
	}
	/**
	 * @param orig_flow original flow to be replaced
	 * @param muta_flow mutation flow to replace the original flow
	 * @return observation:mut_flow(source, orig_target, muta_target)
	 * @throws Exception
	 */
	public static SymCondition mut_flow(CirExecutionFlow orig_flow, CirExecutionFlow muta_flow) throws Exception {
		if(orig_flow == null)
			throw new IllegalArgumentException("Invalid orig_flow: null");
		else if(muta_flow == null)
			throw new IllegalArgumentException("Invalid muta_flow: null");
		else if(orig_flow.get_source() != muta_flow.get_source())
			throw new IllegalArgumentException("Unmatched: " + orig_flow + "\t" + muta_flow);
		else 
			return new SymCondition(SymCategory.observation, SymOperator.mut_flow, orig_flow.get_source(),
					orig_flow.get_target().get_statement(), SymbolFactory.sym_expression(muta_flow.get_target()));
	}
	/**
	 * @param expression
	 * @return observation:set_bool(expression.statement, expression, null)
	 * @throws Exception
	 */
	public static SymCondition chg_bool(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null)
			throw new IllegalArgumentException("Invalid expression: null");
		else if(SymConditions.is_boolean(expression)) {
			CirExecution execution = SymInstances.get_execution(expression);
			return new SymCondition(SymCategory.observation, SymOperator.set_bool, execution, expression, null);
		}
		else
			throw new IllegalArgumentException("Not a condition: " + expression.generate_code(true));
	}
	/**
	 * @param expression
	 * @return observation:set_bool(expression.statement, expression, null)
	 * @throws Exception
	 */
	public static SymCondition set_true(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null)
			throw new IllegalArgumentException("Invalid expression: null");
		else if(SymConditions.is_boolean(expression)) {
			CirExecution execution = SymInstances.get_execution(expression);
			return new SymCondition(SymCategory.observation, SymOperator.set_bool, 
					execution, expression, SymbolFactory.sym_expression(Boolean.TRUE));
		}
		else
			throw new IllegalArgumentException("Not a condition: " + expression.generate_code(true));
	}
	/**
	 * @param expression
	 * @return observation:set_bool(expression.statement, expression, null)
	 * @throws Exception
	 */
	public static SymCondition set_fals(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null)
			throw new IllegalArgumentException("Invalid expression: null");
		else if(SymConditions.is_boolean(expression)) {
			CirExecution execution = SymInstances.get_execution(expression);
			return new SymCondition(SymCategory.observation, SymOperator.set_bool, 
					execution, expression, SymbolFactory.sym_expression(Boolean.FALSE));
		}
		else
			throw new IllegalArgumentException("Not a condition: " + expression.generate_code(true));
	}
	/**
	 * @param expression
	 * @return observation:set_bool(expression.statement, expression, muta_value == true)
	 * @throws Exception
	 */
	public static SymCondition set_bool(CirExpression expression, Object muta_value) throws Exception {
		if(expression == null || expression.statement_of() == null)
			throw new IllegalArgumentException("Invalid expression: null");
		else if(muta_value == null)
			throw new IllegalArgumentException("Invalid muta_value: null");
		else if(SymConditions.is_boolean(expression)) {
			CirExecution execution = SymInstances.get_execution(expression);
			return new SymCondition(SymCategory.observation, SymOperator.set_bool, 
					execution, expression, SymbolFactory.sym_condition(muta_value, true));
		}
		else
			throw new IllegalArgumentException("Not a condition: " + expression.generate_code(true));
	}
	/**
	 * @param expression
	 * @return observation:set_numb(execution, expression, null)
	 * @throws Exception
	 */
	public static SymCondition chg_numb(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null)
			throw new IllegalArgumentException("Invalid expression: null");
		else if(SymConditions.is_numeric(expression)) {
			CirExecution execution = SymInstances.get_execution(expression);
			return new SymCondition(SymCategory.observation, SymOperator.set_numb, execution, expression, null);
		}
		else
			throw new IllegalArgumentException("Not a numeric: " + expression.generate_code(true));
	}
	/**
	 * @param expression
	 * @return observation:set_numb(execution, expression, null)
	 * @throws Exception
	 */
	public static SymCondition set_numb(CirExpression expression, Object muta_value) throws Exception {
		if(expression == null || expression.statement_of() == null)
			throw new IllegalArgumentException("Invalid expression: null");
		else if(muta_value == null)
			throw new IllegalArgumentException("Invalid muta_value: null");
		else if(SymConditions.is_numeric(expression)) {
			CirExecution execution = SymInstances.get_execution(expression);
			return new SymCondition(SymCategory.observation, SymOperator.set_numb, execution, 
									expression, SymbolFactory.sym_expression(muta_value));
		}
		else
			throw new IllegalArgumentException("Not a numeric: " + expression.generate_code(true));
	}
	/**
	 * @param expression
	 * @return observation:set_post(execution, expression, null)
	 * @throws Exception
	 */
	public static SymCondition set_post(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null)
			throw new IllegalArgumentException("Invalid expression: null");
		else if(SymConditions.is_numeric(expression)) {
			CirExecution execution = SymInstances.get_execution(expression);
			return new SymCondition(SymCategory.observation, SymOperator.set_post, execution, expression, null);
		}
		else
			throw new IllegalArgumentException("Not a numeric: " + expression.generate_code(true));
	}
	/**
	 * @param expression
	 * @return observation:set_negt(execution, expression, null)
	 * @throws Exception
	 */
	public static SymCondition set_negt(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null)
			throw new IllegalArgumentException("Invalid expression: null");
		else if(SymConditions.is_numeric(expression)) {
			CirExecution execution = SymInstances.get_execution(expression);
			return new SymCondition(SymCategory.observation, SymOperator.set_negt, execution, expression, null);
		}
		else
			throw new IllegalArgumentException("Not a numeric: " + expression.generate_code(true));
	}
	/**
	 * @param expression
	 * @return observation:set_zero(execution, expression, null)
	 * @throws Exception
	 */
	public static SymCondition set_zero(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null)
			throw new IllegalArgumentException("Invalid expression: null");
		else if(SymConditions.is_numeric(expression)) {
			CirExecution execution = SymInstances.get_execution(expression);
			return new SymCondition(SymCategory.observation, SymOperator.set_zero, execution, expression, null);
		}
		else
			throw new IllegalArgumentException("Not a numeric: " + expression.generate_code(true));
	}
	/**
	 * @param expression
	 * @return observation:set_addr(expression.statement, expression, null)
	 * @throws Exception
	 */
	public static SymCondition chg_addr(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null)
			throw new IllegalArgumentException("Invalid expression: null");
		else if(SymConditions.is_address(expression)) {
			CirExecution execution = SymInstances.get_execution(expression);
			return new SymCondition(SymCategory.observation, SymOperator.set_addr, execution, expression, null);
		}
		else
			throw new IllegalArgumentException("Not a pointer: " + expression.generate_code(true));
	}
	/**
	 * @param expression
	 * @return observation:set_addr(expression.statement, expression, muta_value)
	 * @throws Exception
	 */
	public static SymCondition set_addr(CirExpression expression, Object muta_value) throws Exception {
		if(expression == null || expression.statement_of() == null)
			throw new IllegalArgumentException("Invalid expression: null");
		else if(muta_value == null)
			throw new IllegalArgumentException("Invalid muta_value: null");
		else if(SymConditions.is_address(expression)) {
			CirExecution execution = SymInstances.get_execution(expression);
			return new SymCondition(SymCategory.observation, SymOperator.set_addr, 
					execution, expression, SymbolFactory.sym_expression(muta_value));
		}
		else
			throw new IllegalArgumentException("Not a pointer: " + expression.generate_code(true));
	}
	/**
	 * @param expression
	 * @return observation:set_invp(expression.statement, expression, null)
	 * @throws Exception
	 */
	public static SymCondition set_invp(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null)
			throw new IllegalArgumentException("Invalid expression: null");
		else if(SymConditions.is_address(expression)) {
			CirExecution execution = SymInstances.get_execution(expression);
			return new SymCondition(SymCategory.observation, SymOperator.set_invp, execution, expression, null);
		}
		else
			throw new IllegalArgumentException("Not a pointer: " + expression.generate_code(true));
	}
	/**
	 * @param expression
	 * @return observation:set_null(expression.statement, expression, null)
	 * @throws Exception
	 */
	public static SymCondition set_null(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null)
			throw new IllegalArgumentException("Invalid expression: null");
		else if(SymConditions.is_address(expression)) {
			CirExecution execution = SymInstances.get_execution(expression);
			return new SymCondition(SymCategory.observation, SymOperator.set_null, execution, expression, null);
		}
		else
			throw new IllegalArgumentException("Not a pointer: " + expression.generate_code(true));
	}
	/**
	 * @param expression
	 * @return observation:set_auto(expression.statement, expression, null)
	 * @throws Exception
	 */
	public static SymCondition chg_auto(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null)
			throw new IllegalArgumentException("Invalid expression: null");
		else if(SymConditions.is_boolean(expression) || SymConditions.is_numeric(expression) || SymConditions.is_address(expression))
			throw new IllegalArgumentException("Not available: " + expression.generate_code(true));
		else {
			CirExecution execution = SymInstances.get_execution(expression);
			return new SymCondition(SymCategory.observation, SymOperator.set_auto, execution, expression, null);
		}
	}
	/**
	 * @param expression
	 * @return observation:set_auto(expression.statement, expression, muta_value)
	 * @throws Exception
	 */
	public static SymCondition set_auto(CirExpression expression, Object muta_value) throws Exception {
		if(expression == null || expression.statement_of() == null)
			throw new IllegalArgumentException("Invalid expression: null");
		else if(SymConditions.is_boolean(expression) || SymConditions.is_numeric(expression) || SymConditions.is_address(expression))
			throw new IllegalArgumentException("Not available: " + expression.generate_code(true));
		else {
			CirExecution execution = SymInstances.get_execution(expression);
			return new SymCondition(SymCategory.observation, SymOperator.set_auto, execution, expression, SymbolFactory.sym_expression(muta_value));
		}
	}
	/**
	 * @param expression
	 * @return observation:inc_scop(expression.statement, expression, null)
	 * @throws Exception
	 */
	public static SymCondition inc_scop(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null)
			throw new IllegalArgumentException("Invalid expression: null");
		else if(SymConditions.is_numeric(expression) || SymConditions.is_address(expression)) {
			CirExecution execution = SymInstances.get_execution(expression);
			return new SymCondition(SymCategory.observation, SymOperator.inc_scop, execution, expression, null);
		}
		else 
			throw new IllegalArgumentException("Not available: " + expression.generate_code(true));
	}
	/**
	 * @param expression
	 * @return observation:dec_scop(expression.statement, expression, null)
	 * @throws Exception
	 */
	public static SymCondition dec_scop(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null)
			throw new IllegalArgumentException("Invalid expression: null");
		else if(SymConditions.is_numeric(expression) || SymConditions.is_address(expression)) {
			CirExecution execution = SymInstances.get_execution(expression);
			return new SymCondition(SymCategory.observation, SymOperator.dec_scop, execution, expression, null);
		}
		else 
			throw new IllegalArgumentException("Not available: " + expression.generate_code(true));
	}
	/**
	 * @param expression
	 * @return observation:ext_scop(expression.statement, expression, null)
	 * @throws Exception
	 */
	public static SymCondition ext_scop(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null)
			throw new IllegalArgumentException("Invalid expression: null");
		else if(SymConditions.is_numeric(expression) || SymConditions.is_address(expression)) {
			CirExecution execution = SymInstances.get_execution(expression);
			return new SymCondition(SymCategory.observation, SymOperator.ext_scop, execution, expression, null);
		}
		else 
			throw new IllegalArgumentException("Not available: " + expression.generate_code(true));
	}
	/**
	 * @param expression
	 * @return observation:shk_scop(expression.statement, expression, null)
	 * @throws Exception
	 */
	public static SymCondition shk_scop(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null)
			throw new IllegalArgumentException("Invalid expression: null");
		else if(SymConditions.is_numeric(expression) || SymConditions.is_address(expression)) {
			CirExecution execution = SymInstances.get_execution(expression);
			return new SymCondition(SymCategory.observation, SymOperator.shk_scop, execution, expression, null);
		}
		else 
			throw new IllegalArgumentException("Not available: " + expression.generate_code(true));
	}
	/**
	 * @param expression
	 * @return observation:mut_expr(expression.statement, expression, null)
	 * @throws Exception
	 */
	public static SymCondition mut_expr(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null)
			throw new IllegalArgumentException("Invalid expression: null");
		else {
			CirExecution execution = SymInstances.get_execution(expression);
			return new SymCondition(SymCategory.observation, SymOperator.mut_expr, execution, expression, null);
		}
	}
	/**
	 * @param expression
	 * @return observation:mut_refr(expression.statement, expression, null)
	 * @throws Exception
	 */
	public static SymCondition mut_refr(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null)
			throw new IllegalArgumentException("Invalid expression: null");
		else {
			CirExecution execution = SymInstances.get_execution(expression);
			return new SymCondition(SymCategory.observation, SymOperator.mut_refr, execution, expression, null);
		}
	}
	/**
	 * @param expression
	 * @return observation:mut_stat(expression.statement, expression, null)
	 * @throws Exception
	 */
	public static SymCondition mut_stat(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() == null)
			throw new IllegalArgumentException("Invalid expression: null");
		else {
			CirExecution execution = SymInstances.get_execution(expression);
			return new SymCondition(SymCategory.observation, SymOperator.mut_stat, execution, expression, null);
		}
	}
	
	/* define-usage analyzer */
	/**
	 * @param expression
	 * @return {identifier, field_expression, dereference}
	 */
	private static boolean is_reference(SymbolNode expression) {
		if(expression instanceof SymbolIdentifier) {
			return true;
		}
		else if(expression instanceof SymbolFieldExpression) {
			return true;
		}
		else if(expression instanceof SymbolUnaryExpression) {
			return ((SymbolUnaryExpression) expression).get_operator().get_operator() == COperator.dereference;
		}
		else {
			return false;
		}
	}
	/**
	 * collect all the references used in the expression
	 * @param expression
	 * @param references
	 * @throws Exception
	 */
	private static void get_references(SymbolNode expression, Collection<SymbolExpression> references) throws Exception {
		if(SymConditions.is_reference(expression)) {
			references.add((SymbolExpression) expression);
		}
		for(SymbolNode child : expression.get_children()) {
			SymConditions.get_references(child, references);
		}
	}
	/**
	 * @param expression
	 * @return collect all the references used in the expression
	 * @throws Exception
	 */
	private static Collection<SymbolExpression> get_references(SymbolNode expression) throws Exception {
		if(expression == null)
			throw new IllegalArgumentException("Invalid expression; null");
		else {
			Collection<SymbolExpression> references = new HashSet<SymbolExpression>();
			SymConditions.get_references(expression, references);
			return references;
		}
	}
	/**
	 * @param expression
	 * @param references
	 * @return whether any references are used in the expression tree
	 */
	private static boolean has_references(SymbolNode expression, Collection<SymbolExpression> references) {
		if(expression == null)
			throw new IllegalArgumentException("Invalid expression: null");
		else if(references.isEmpty())
			return false;
		else if(references.contains(expression))
			return true;
		else {
			for(SymbolNode child : expression.get_children()) {
				if(SymConditions.has_references(child, references)) {
					return true;
				}
			}
			return false;
		}
	}
	/**
	 * @param execution
	 * @param references
	 * @return whether any references in set are defined at the execution point
	 * @throws Exception
	 */
	private static boolean has_references_defined(CirExecution execution, Collection<SymbolExpression> references) throws Exception {
		if(execution == null)
			throw new IllegalArgumentException("Invalid execution: null");
		else if(references.isEmpty())
			return false;
		else {
			CirStatement statement = execution.get_statement();
			if(statement instanceof CirIfStatement) {
				return SymConditions.has_references(SymbolFactory.sym_expression(
						((CirIfStatement) statement).get_condition()), references);
			}
			else if(statement instanceof CirCaseStatement) {
				return SymConditions.has_references(SymbolFactory.sym_expression(
						((CirCaseStatement) statement).get_condition()), references);
			}
			else if(statement instanceof CirAssignStatement) {
				SymbolExpression reference = SymbolFactory.sym_expression(
						((CirAssignStatement) statement).get_lvalue());
				return references.contains(reference);
			}
			else {
				return false;
			}
		}
	}
	
	/* generators */
	/**
	 * collect the set of conditions in the conjunction structure of expression
	 * @param expression
	 * @param sub_conditions
	 */
	private static void collect_conditions(SymbolExpression expression, Collection<SymbolExpression> sub_conditions) throws Exception {
		if(expression == null)
			throw new IllegalArgumentException("Invalid expression: null");
		else if(sub_conditions == null)
			throw new IllegalArgumentException("Invalid sub_conditions: null");
		else {
			/* A. TRUE IS IGNORED */
			if(expression instanceof SymbolConstant) {
				if(((SymbolConstant) expression).get_bool()) {
					/* true is ignored */
				}
				else {
					sub_conditions.add(SymbolFactory.sym_expression(Boolean.FALSE));
				}
			}
			/* B. BINARY EXPRESSION */
			else if(expression instanceof SymbolBinaryExpression) {
				SymbolExpression loperand = ((SymbolBinaryExpression) expression).get_loperand();
				SymbolExpression roperand = ((SymbolBinaryExpression) expression).get_roperand();
				COperator operator = ((SymbolBinaryExpression) expression).get_operator().get_operator();
				if(operator == COperator.logic_and || operator == COperator.bit_and) {
					SymConditions.collect_conditions(loperand, sub_conditions);
					SymConditions.collect_conditions(roperand, sub_conditions);
				}
				else {
					sub_conditions.add(SymbolFactory.sym_condition(expression, true));
				}
			}
			/* C. OTHERWISE CASE */
			else {
				sub_conditions.add(SymbolFactory.sym_condition(expression, true));
			}
		}
	}
	/**
	 * @param constraint
	 * @return 
	 * @throws Exception
	 */
	private static Collection<SymCondition> gen_constraint_1(SymConstraint constraint) throws Exception {
		if(constraint == null)
			throw new IllegalArgumentException("Invalid constraint: null");
		else {
			/* 1. collect all the sub-conditions in the constraint conjunctions */
			Collection<SymbolExpression> conditions = new HashSet<SymbolExpression>();
			SymConditions.collect_conditions(constraint.get_condition(), conditions);
			
			/* 2. generate the set of symbolic conditions from constraint */
			Collection<SymCondition> sym_conditions = new HashSet<SymCondition>();
			CirExecution execution = constraint.get_execution();
			for(SymbolExpression condition : conditions) {
				Object[] exec_oprt_time = SymInstances.divide_stmt_constraint(condition);
				/** evaluation condition **/
				if(exec_oprt_time == null) {
					sym_conditions.add(SymConditions.eva_expr(execution, condition, true));
				}
				/** coverage condition **/
				else {
					CirExecution cov_execution = (CirExecution) exec_oprt_time[0];
					COperator cov_operator = (COperator) exec_oprt_time[1];
					Integer cov_times = (Integer) exec_oprt_time[2];
					sym_conditions.add(SymConditions.cov_stmt(cov_execution, cov_operator, cov_times.intValue()));
				}
			}
			sym_conditions.add(SymConditions.cov_stmt(execution, COperator.greater_eq, 1));
			
			/* 3. initial set of conditions from constraint */	return sym_conditions;
		}
	}
	/**
	 * @param condition
	 * @return the execution point that can be taken as checkpoint of the condition
	 * @throws Exception
	 */
	private static SymCondition improve_condition_on_path(SymCondition condition) throws Exception {
		if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		/* evaluation condition */
		else if(condition.get_operator() == SymOperator.eva_expr) {
			/* declarations */
			CirExecution execution = condition.get_execution();
			SymbolExpression expression = condition.get_parameter();
			
			/* A. find the program entry as constant checkpoint */
			if(expression instanceof SymbolConstant) {
				CirFunction function = execution.get_graph().get_function().get_graph().get_main_function();
				if(function == null) { function = execution.get_graph().get_function(); }
				if(((SymbolConstant) expression).get_bool()) {
					return SymConditions.eva_expr(function.get_flow_graph().get_entry(), Boolean.TRUE, true);
				}
				else {
					return SymConditions.eva_expr(function.get_flow_graph().get_entry(), Boolean.FALSE, true);
				}
			}
			/* B. find non-defined point as closest checkpoints */
			else {
				CirExecutionPath prev_path = CirExecutionPathFinder.finder.db_extend(execution);
				Iterator<CirExecutionEdge> iterator = prev_path.get_reverse_edges();
				Collection<SymbolExpression> references = SymConditions.get_references(expression);
				while(iterator.hasNext()) {
					CirExecutionEdge edge = iterator.next();
					if(SymConditions.has_references_defined(edge.get_source(), references)) {
						return SymConditions.eva_expr(edge.get_target(), expression, true);
					}
				}
				return SymConditions.eva_expr(prev_path.get_source(), expression, true);
			}
		}
		/* coverage condition */
		else if(condition.get_operator() == SymOperator.cov_stmt) {
			/* divide coverage constraint and obtain covered execution */
			Object[] exec_oprt_time = SymInstances.
					divide_stmt_constraint(condition.get_parameter());
			CirExecution execution = (CirExecution) exec_oprt_time[0];
			COperator operator = (COperator) exec_oprt_time[1];
			Integer times = (Integer) exec_oprt_time[2];
			
			CirExecutionPath prev_path = CirExecutionPathFinder.finder.db_extend(execution);
			Iterator<CirExecutionEdge> iterator = prev_path.get_reverse_edges();
			while(iterator.hasNext()) {
				CirExecutionEdge edge = iterator.next();
				CirStatement statement = edge.get_source().get_statement();
				if(statement instanceof CirIfStatement || statement instanceof CirCaseStatement) {
					return SymConditions.cov_stmt(prev_path.get_target(), operator, times);
				}
			}
			return SymConditions.cov_stmt(prev_path.get_source(), operator, times);
		}
		else {
			throw new IllegalArgumentException("Unsupported: " + condition);
		}
	}
	/**
	 * @param input_conditions
	 * @return improve each symbolic condition to the checkpoint closest to current one
	 * @throws Exception
	 */
	private static Collection<SymCondition> gen_constraint_2(Collection<SymCondition> input_conditions) throws Exception {
		if(input_conditions == null)
			throw new IllegalArgumentException("Invalid input_conditions as null");
		else {
			Collection<SymCondition> output_conditions = new HashSet<SymCondition>();
			for(SymCondition input_condition : input_conditions) {
				output_conditions.add(SymConditions.improve_condition_on_path(input_condition));
			}
			return output_conditions;
		}
	}
	/**
	 * generate the subsumed conditions from input condition
	 * @param condition
	 * @param subsumed_conditions
	 * @throws Exception
	 */
	private static void gen_subsumed_conditions(SymCondition condition, Collection<SymCondition> subsumed_conditions) throws Exception {
		if(condition == null)
			throw new IllegalArgumentException("Invalid condition: null");
		else if(subsumed_conditions == null)
			throw new IllegalArgumentException("Invalid subsumed_conditions");
		/* A. generate subsumed expressions from input condition */
		else if(condition.get_operator() == SymOperator.eva_expr) {
			/* declarations */
			CirExecution execution = condition.get_execution();
			SymbolExpression expression = condition.get_parameter();
			
			/* subsumed by relational expression */
			if(expression instanceof SymbolBinaryExpression) {
				SymbolExpression loperand = ((SymbolBinaryExpression) expression).get_loperand();
				SymbolExpression roperand = ((SymbolBinaryExpression) expression).get_roperand();
				COperator operator = ((SymbolBinaryExpression) expression).get_operator().get_operator();
				
				/* a <= b: { a <= b } */
				if(operator == COperator.smaller_eq) {
					subsumed_conditions.add(SymConditions.eva_expr(execution, expression, true));
				}
				/* a < b: { a < b, a <= b, a != b, b != a } */
				else if(operator == COperator.smaller_tn) {
					subsumed_conditions.add(SymConditions.eva_expr(execution, expression, true));
					subsumed_conditions.add(SymConditions.eva_expr(execution, SymbolFactory.smaller_eq(loperand, roperand), true));
					subsumed_conditions.add(SymConditions.eva_expr(execution, SymbolFactory.not_equals(loperand, roperand), true));
					subsumed_conditions.add(SymConditions.eva_expr(execution, SymbolFactory.not_equals(roperand, loperand), true));
				}
				/* a >= b: { b <= a } */
				else if(operator == COperator.greater_eq) {
					subsumed_conditions.add(SymConditions.eva_expr(execution, SymbolFactory.smaller_eq(roperand, loperand), true));
				}
				/* a > b: { b < a, b <= a, b != a, a != b } */
				else if(operator == COperator.greater_tn) {
					subsumed_conditions.add(SymConditions.eva_expr(execution, SymbolFactory.smaller_tn(roperand, loperand), true));
					subsumed_conditions.add(SymConditions.eva_expr(execution, SymbolFactory.smaller_eq(roperand, loperand), true));
					subsumed_conditions.add(SymConditions.eva_expr(execution, SymbolFactory.not_equals(roperand, loperand), true));
					subsumed_conditions.add(SymConditions.eva_expr(execution, SymbolFactory.not_equals(loperand, roperand), true));
				}
				/* a == b: { a == b, b == a, a <= b, b <= a } */
				else if(operator == COperator.equal_with) {
					subsumed_conditions.add(SymConditions.eva_expr(execution, expression, true));
					subsumed_conditions.add(SymConditions.eva_expr(execution, SymbolFactory.equal_with(roperand, loperand), true));
					subsumed_conditions.add(SymConditions.eva_expr(execution, SymbolFactory.smaller_eq(loperand, roperand), true));
					subsumed_conditions.add(SymConditions.eva_expr(execution, SymbolFactory.smaller_eq(roperand, loperand), true));
				}
				/* a != b: { a != b, b != a } */
				else if(operator == COperator.not_equals) {
					subsumed_conditions.add(SymConditions.eva_expr(execution, expression, true));
					subsumed_conditions.add(SymConditions.eva_expr(execution, SymbolFactory.not_equals(roperand, loperand), true));
				}
				else {
					subsumed_conditions.add(SymConditions.eva_expr(execution, expression, true));
				}
			}
			else {
				subsumed_conditions.add(condition);
			}
		}
		/* B. generate coverage conditions from input condition */
		else if(condition.get_operator() == SymOperator.cov_stmt) {
			Object[] exec_oprt_time = SymInstances.divide_stmt_constraint(condition.get_parameter());
			CirExecution execution = (CirExecution) exec_oprt_time[0];
			COperator operator = (COperator) exec_oprt_time[1];
			Integer times = (Integer) exec_oprt_time[2];
			for(int k = 1; k < times; k = k * 2) {
				subsumed_conditions.add(SymConditions.cov_stmt(execution, operator, k));
			}
			subsumed_conditions.add(SymConditions.cov_stmt(execution, operator, times));
		}
		else
			throw new IllegalArgumentException("Unsupported: " + condition);
	}
	/**
	 * generate the subsumed set of conditions from inputs
	 * @param input_conditions
	 * @throws Exception
	 */
	private static Collection<SymCondition> gen_constraint_3(Collection<SymCondition> input_conditions) throws Exception {
		if(input_conditions == null)
			throw new IllegalArgumentException("Invalid input_conditions as null");
		else {
			Collection<SymCondition> output_conditions = new HashSet<SymCondition>();
			for(SymCondition input_condition : input_conditions) {
				SymConditions.gen_subsumed_conditions(input_condition, output_conditions);
			}
			return output_conditions;
		}
	}
	/**
	 * @param constraint
	 * @return
	 * @throws Exception
	 */
	private static Collection<SymCondition> gen_constraint(SymConstraint constraint) throws Exception {
		return SymConditions.gen_constraint_3(SymConditions.gen_constraint_2(SymConditions.gen_constraint_1(constraint)));
	}
	/**
	 * @param error
	 * @return
	 * @throws Exception
	 */
	private static Collection<SymCondition> gen_flow_error(SymFlowError error) throws Exception {
		Collection<SymCondition> conditions = new HashSet<SymCondition>();
		CirExecutionFlow orig_flow = error.get_original_flow();
		CirExecutionFlow muta_flow = error.get_mutation_flow();
		
		conditions.add(SymConditions.mut_flow(orig_flow, muta_flow));
		
		Collection<CirExecution> add_executions = new HashSet<CirExecution>();
		Collection<CirExecution> del_executions = new HashSet<CirExecution>();
		CirExecutionPath orig_path = CirExecutionPathFinder.finder.df_extend(orig_flow.get_target());
		CirExecutionPath muta_path = CirExecutionPathFinder.finder.df_extend(muta_flow.get_target());
		for(CirExecutionEdge edge : muta_path.get_edges()) { add_executions.add(edge.get_source()); }
		for(CirExecutionEdge edge : orig_path.get_edges()) { del_executions.add(edge.get_source()); }
		add_executions.add(muta_path.get_target()); del_executions.add(orig_path.get_target());
		
		Collection<CirExecution> com_executions = new HashSet<CirExecution>();
		for(CirExecution execution : add_executions) {
			if(del_executions.contains(execution)) {
				com_executions.add(execution);
			}
		}
		add_executions.removeAll(com_executions);
		del_executions.removeAll(com_executions);
		
		for(CirExecution execution : add_executions) {
			conditions.add(SymConditions.add_stmt(execution));
		}
		for(CirExecution execution : del_executions) {
			conditions.add(SymConditions.del_stmt(execution));
		}
		
		return conditions;
	}
	private static Collection<SymCondition> gen_trap_error(SymTrapError error) throws Exception {
		Collection<SymCondition> conditions = new HashSet<SymCondition>();
		conditions.add(SymConditions.trp_stmt(error.get_execution()));
		return conditions;
	}
	private static Collection<SymCondition> gen_expr_error(SymValueError error) throws Exception {
		/* declarations */
		Collection<SymCondition> conditions = new HashSet<SymCondition>();
		CirExecution execution = error.get_execution();
		CirExpression location = error.get_expression();
		SymbolExpression orig_value = error.get_original_value();
		SymbolExpression muta_value = error.get_mutation_value();
		
		/* initialization */
		orig_value = SymInstances.evaluate(orig_value, null);
		try {
			muta_value = SymInstances.evaluate(muta_value, null);
		}
		catch(ArithmeticException ex) {
			conditions.add(SymConditions.trp_stmt(execution));
			return conditions;
		}
		if(orig_value.equals(muta_value)) {
			conditions.add(SymConditions.eva_expr(execution, Boolean.FALSE, true));
			return conditions;
		}
		
		/* BOOL */
		if(SymConditions.is_boolean(location)) {
			orig_value = SymbolFactory.sym_condition(orig_value, true);
			muta_value = SymbolFactory.sym_condition(muta_value, true);
			orig_value = SymInstances.evaluate(orig_value, null);
			muta_value = SymInstances.evaluate(muta_value, null);
			
			if(muta_value instanceof SymbolConstant) {
				if(((SymbolConstant) muta_value).get_bool().booleanValue())
					conditions.add(SymConditions.set_true(location));
				else
					conditions.add(SymConditions.set_fals(location));
			}
			else {
				conditions.add(SymConditions.set_bool(location, muta_value));
			}
		}
		/* NUMB */
		else if(SymConditions.is_numeric(location)) {
			conditions.add(SymConditions.set_numb(location, muta_value));
			
			if(muta_value instanceof SymbolConstant) {
				Object number = ((SymbolConstant) muta_value).get_number();
				if(number instanceof Long) {
					if(((Long) number).longValue() > 0) {
						conditions.add(SymConditions.set_post(location));
					}
					else if(((Long) number).longValue() < 0) {
						conditions.add(SymConditions.set_negt(location));
					}
					else {
						conditions.add(SymConditions.set_zero(location));
					}
				}
				else {
					if(((Double) number).doubleValue() > 0) {
						conditions.add(SymConditions.set_post(location));
					}
					else if(((Double) number).doubleValue() < 0) {
						conditions.add(SymConditions.set_negt(location));
					}
					else {
						conditions.add(SymConditions.set_zero(location));
					}
				}
			}
			
			SymbolExpression diff = SymbolFactory.arith_sub(location.get_data_type(), muta_value, orig_value);
			diff = SymInstances.evaluate(diff, null);
			if(diff instanceof SymbolConstant) {
				Object number = ((SymbolConstant) diff).get_number();
				if(number instanceof Long) {
					if(((Long) number).longValue() > 0) {
						conditions.add(SymConditions.inc_scop(location));
					}
					else if(((Long) number).longValue() < 0) {
						conditions.add(SymConditions.dec_scop(location));
					}
				}
				else if(number instanceof Double) {
					if(((Double) number).doubleValue() > 0) {
						conditions.add(SymConditions.inc_scop(location));
					}
					else if(((Double) number).doubleValue() < 0) {
						conditions.add(SymConditions.dec_scop(location));
					}
				}
			}
			
			if(orig_value instanceof SymbolConstant) {
				Object x = ((SymbolConstant) orig_value).get_number();
				if(muta_value instanceof SymbolConstant) {
					Object y = ((SymbolConstant) muta_value).get_number();
					if(x instanceof Long) {
						if(y instanceof Long) {
							if(Math.abs(((Long) x).doubleValue()) < Math.abs(((Long) y).doubleValue())) {
								conditions.add(SymConditions.ext_scop(location));
							}
							else {
								conditions.add(SymConditions.shk_scop(location));
							}
						}
						else {
							if(Math.abs(((Long) x).doubleValue()) < Math.abs(((Double) y).doubleValue())) {
								conditions.add(SymConditions.ext_scop(location));
							}
							else {
								conditions.add(SymConditions.shk_scop(location));
							}
						}
					}
					else {
						if(y instanceof Long) {
							if(Math.abs(((Double) x).doubleValue()) < Math.abs(((Long) y).doubleValue())) {
								conditions.add(SymConditions.ext_scop(location));
							}
							else {
								conditions.add(SymConditions.shk_scop(location));
							}
						}
						else {
							if(Math.abs(((Double) x).doubleValue()) < Math.abs(((Double) y).doubleValue())) {
								conditions.add(SymConditions.ext_scop(location));
							}
							else {
								conditions.add(SymConditions.shk_scop(location));
							}
						}
					}
				}
			}
		}
		else if(SymConditions.is_address(location)) {
			conditions.add(SymConditions.set_addr(location, muta_value));
			
			if(muta_value instanceof SymbolConstant) {
				if(((SymbolConstant) muta_value).get_long().longValue() == 0) {
					conditions.add(SymConditions.set_null(location));
				}
				else {
					conditions.add(SymConditions.set_invp(location));
				}
			}
			else {
				conditions.add(SymConditions.set_invp(location));
			}
			
			SymbolExpression diff = SymbolFactory.arith_sub(location.get_data_type(), muta_value, orig_value);
			diff = SymInstances.evaluate(diff, null);
			if(diff instanceof SymbolConstant) {
				Object number = ((SymbolConstant) diff).get_number();
				if(number instanceof Long) {
					if(((Long) number).longValue() > 0) {
						conditions.add(SymConditions.inc_scop(location));
					}
					else if(((Long) number).longValue() < 0) {
						conditions.add(SymConditions.dec_scop(location));
					}
				}
				else if(number instanceof Double) {
					if(((Double) number).doubleValue() > 0) {
						conditions.add(SymConditions.inc_scop(location));
					}
					else if(((Double) number).doubleValue() < 0) {
						conditions.add(SymConditions.dec_scop(location));
					}
				}
			}
		}
		else {
			conditions.add(SymConditions.set_auto(location, muta_value));
		}
		
		/* summarization */
		if(muta_value.equals(orig_value)) {
			conditions.clear();
		}
		else {
			if(error instanceof SymExpressionError) {
				conditions.add(SymConditions.mut_expr(location));
			}
			else if(error instanceof SymReferenceError) {
				conditions.add(SymConditions.mut_refr(location));
			}
			else {
				conditions.add(SymConditions.mut_stat(location));
			}
		}
		
		return conditions;
	}
	/**
	 * @param instance
	 * @return generate the symbolic conditions describing the instance
	 * @throws Exception
	 */
	public static Collection<SymCondition> generate(SymInstance instance) throws Exception {
		if(instance == null)
			throw new IllegalArgumentException("Invalid instance as null");
		else if(instance instanceof SymConstraint)
			return SymConditions.gen_constraint((SymConstraint) instance);
		else if(instance instanceof SymFlowError)
			return SymConditions.gen_flow_error((SymFlowError) instance);
		else if(instance instanceof SymTrapError)
			return SymConditions.gen_trap_error((SymTrapError) instance);
		else if(instance instanceof SymValueError)
			return SymConditions.gen_expr_error((SymValueError) instance);
		else
			throw new IllegalArgumentException("Unsupported: " + instance);
	}
	
}
