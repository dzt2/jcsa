package com.jcsa.jcmutest.mutant.sym2mutant.tree;

import com.jcsa.jcmutest.mutant.sym2mutant.util.SymConditionUtils;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * 	Structural symbolic condition is defined as following:	<br>
 * 	1. category: either "assertion" or "observation";		<br>
 * 	2. operator: operator to refine the descriptions;		<br>
 * 	3. execution: the execution point of the condition;		<br>
 * 	4. location: the CIR-node where the condition defined;	<br>
 * 	5. parameter: symbolic expression or null if useless;	<br>
 * @author yukimula
 *
 */
public class SymCondition {
	
	/* attributes */
	/** either "assertion" or "observation" **/
	private SymCategory category;
	/** operator to refine the descriptions **/
	private SymOperator operator;
	/** the execution point of the condition **/
	private CirExecution execution;
	/** the CIR-node where the condition defined **/
	private CirNode location;
	/** symbolic expression or null if useless **/
	private SymbolExpression parameter;
	/**
	 * private creator for factory mode
	 * @param category	either "assertion" or "observation"
	 * @param operator	operator to refine the descriptions
	 * @param execution	the execution point of the condition
	 * @param location	the CIR-node where the condition defined
	 * @param parameter	symbolic expression or null if useless
	 * @throws IllegalArgumentException
	 */
	private SymCondition(SymCategory category, SymOperator operator, CirExecution execution, 
			CirNode location, SymbolExpression parameter) throws IllegalArgumentException {
		if(category == null)
			throw new IllegalArgumentException("Invalid category: null");
		else if(operator == null)
			throw new IllegalArgumentException("Invalid operator: null");
		else if(execution == null)
			throw new IllegalArgumentException("Invalid execution: null");
		else if(location == null)
			throw new IllegalArgumentException("Invalid location: null");
		else {
			this.category = category;
			this.operator = operator;
			this.execution = execution;
			this.location = location;
			this.parameter = parameter;
		}
	}
	
	/* getters */
	/**
	 * @return either "assertion" or "observation"
	 */
	public SymCategory get_category() { return this.category; }
	/**
	 * @return operator to refine the descriptions
	 */
	public SymOperator get_operator() { return this.operator; }
	/**
	 * @return the execution point of the condition
	 */
	public CirExecution get_execution() { return this.execution; }
	/**
	 * @return the CIR-node where the condition defined
	 */
	public CirNode get_location() { return this.location; }
	/**
	 * @return symbolic expression or null if useless
	 */
	public SymbolExpression get_parameter() { return this.parameter; }
	/**
	 * @return whether parameter is used in this condition
	 */
	public boolean has_parameter() { return this.parameter != null; }
	
	/* basic methods */
	/**
	 * @param expression
	 * @return whether the expression is taken as a boolean or IF-condition
	 * @throws Exception
	 */
	private static boolean is_boolean(CirExpression expression) throws Exception {
		return SymConditionUtils.is_boolean(expression);
	}
	/**
	 * @param expression
	 * @return whether the expression is integer or floating
	 * @throws Exception
	 */
	private static boolean is_numeric(CirExpression expression) throws Exception {
		return SymConditionUtils.is_numeric(expression);
	}
	/**
	 * @param expression
	 * @return whether the expression is pointer
	 * @throws Exception
	 */
	private static boolean is_address(CirExpression expression) throws Exception {
		return SymConditionUtils.is_address(expression);
	}
	
	/* factory methods */
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
		else if(orig_flow.get_target() == muta_flow.get_target())
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
		else if(SymCondition.is_boolean(expression)) {
			CirExecution execution = expression.get_tree().get_localizer().get_execution(expression.statement_of());
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
		else if(SymCondition.is_boolean(expression)) {
			CirExecution execution = expression.get_tree().get_localizer().get_execution(expression.statement_of());
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
		else if(SymCondition.is_boolean(expression)) {
			CirExecution execution = expression.get_tree().get_localizer().get_execution(expression.statement_of());
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
		else if(SymCondition.is_boolean(expression)) {
			CirExecution execution = expression.get_tree().get_localizer().get_execution(expression.statement_of());
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
		else if(SymCondition.is_numeric(expression)) {
			CirExecution execution = expression.get_tree().get_localizer().get_execution(expression.statement_of());
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
		else if(SymCondition.is_numeric(expression)) {
			CirExecution execution = expression.get_tree().get_localizer().get_execution(expression.statement_of());
			return new SymCondition(SymCategory.observation, SymOperator.set_numb, execution, expression, SymbolFactory.sym_expression(muta_value));
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
		else if(SymCondition.is_numeric(expression)) {
			CirExecution execution = expression.get_tree().get_localizer().get_execution(expression.statement_of());
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
		else if(SymCondition.is_numeric(expression)) {
			CirExecution execution = expression.get_tree().get_localizer().get_execution(expression.statement_of());
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
		else if(SymCondition.is_numeric(expression)) {
			CirExecution execution = expression.get_tree().get_localizer().get_execution(expression.statement_of());
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
		else if(SymCondition.is_address(expression)) {
			CirExecution execution = expression.get_tree().get_localizer().get_execution(expression.statement_of());
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
		else if(SymCondition.is_address(expression)) {
			CirExecution execution = expression.get_tree().get_localizer().get_execution(expression.statement_of());
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
		else if(SymCondition.is_address(expression)) {
			CirExecution execution = expression.get_tree().get_localizer().get_execution(expression.statement_of());
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
		else if(SymCondition.is_address(expression)) {
			CirExecution execution = expression.get_tree().get_localizer().get_execution(expression.statement_of());
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
		else if(SymCondition.is_boolean(expression) || SymCondition.is_numeric(expression) || SymCondition.is_address(expression))
			throw new IllegalArgumentException("Not available: " + expression.generate_code(true));
		else {
			CirExecution execution = expression.get_tree().get_localizer().get_execution(expression.statement_of());
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
		else if(SymCondition.is_boolean(expression) || SymCondition.is_numeric(expression) || SymCondition.is_address(expression))
			throw new IllegalArgumentException("Not available: " + expression.generate_code(true));
		else {
			CirExecution execution = expression.get_tree().get_localizer().get_execution(expression.statement_of());
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
		else if(SymCondition.is_numeric(expression) || SymCondition.is_address(expression)) {
			CirExecution execution = expression.get_tree().get_localizer().get_execution(expression.statement_of());
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
		else if(SymCondition.is_numeric(expression) || SymCondition.is_address(expression)) {
			CirExecution execution = expression.get_tree().get_localizer().get_execution(expression.statement_of());
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
		else if(SymCondition.is_numeric(expression) || SymCondition.is_address(expression)) {
			CirExecution execution = expression.get_tree().get_localizer().get_execution(expression.statement_of());
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
		else if(SymCondition.is_numeric(expression) || SymCondition.is_address(expression)) {
			CirExecution execution = expression.get_tree().get_localizer().get_execution(expression.statement_of());
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
			CirExecution execution = expression.get_tree().get_localizer().get_execution(expression.statement_of());
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
			CirExecution execution = expression.get_tree().get_localizer().get_execution(expression.statement_of());
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
			CirExecution execution = expression.get_tree().get_localizer().get_execution(expression.statement_of());
			return new SymCondition(SymCategory.observation, SymOperator.mut_stat, execution, expression, null);
		}
	}
	
	/* compare */
	@Override
	public String toString() {
		if(this.parameter == null)
			return this.category + "@" + this.operator + "@" + this.execution + "@" + this.location.get_node_id();
		else {
			try {
				return this.category + "@" + this.operator + "@" + this.execution + "@" + 
						this.location.get_node_id() + "@" + this.parameter.generate_code(true);
			}
			catch(Exception ex) {
				return null;
			}
		}
	}
	@Override
	public int hashCode() { return this.toString().hashCode(); }
	@Override
	public boolean equals(Object obj) {
		if(obj == this)
			return true;
		else if(obj instanceof SymCondition)
			return this.toString().equals(obj.toString());
		else
			return false;
	}
	
}