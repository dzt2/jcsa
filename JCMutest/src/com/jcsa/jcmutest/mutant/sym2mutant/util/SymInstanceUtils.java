package com.jcsa.jcmutest.mutant.sym2mutant.util;

import com.jcsa.jcmutest.mutant.sym2mutant.CirMutation;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymConstraint;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymExpressionError;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymFlowError;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymInstance;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymReferenceError;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymStateError;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymStateValueError;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymTrapError;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymValueError;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirReferExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.parse.symbol.SymbolEvaluator;
import com.jcsa.jcparse.parse.symbol.SymbolStateContexts;

/**
 * It implements the interfaces for creating SymInstance objects in the program under test.
 * 
 * @author yukimula
 *
 */
public class SymInstanceUtils {
	
	/* basic method */
	/**
	 * @param expression
	 * @param contexts
	 * @return the output expression evaluated from input expression using contexts
	 * @throws Exception
	 */
	private static SymbolExpression evaluate_on(SymbolExpression expression, SymbolStateContexts contexts) throws Exception {
		return SymbolEvaluator.evaluate_on(expression, contexts);
	}
	
	/* constraint producer */
	/**
	 * @param execution
	 * @param expression
	 * @param value
	 * @return create an expression-constraint at execution point using specified expression == value
	 * @throws Exception
	 */
	public static SymConstraint expr_constraint(CirExecution execution, Object expression, boolean value) throws Exception {
		if(execution == null)
			throw new IllegalArgumentException("Invalid execution: null");
		else if(expression == null)
			throw new IllegalArgumentException("No expression provided");
		else {
			return new SymConstraint(execution, SymbolFactory.sym_condition(expression, value));
		}
	}
	/**
	 * @param statement
	 * @param expression
	 * @param value
	 * @return create an expression-constraint at execution point using specified expression == value
	 * @throws Exception
	 */
	public static SymConstraint expr_constraint(CirStatement statement, Object expression, boolean value) throws Exception {
		if(statement == null)
			throw new IllegalArgumentException("Invalid statement: null");
		else if(expression == null)
			throw new IllegalArgumentException("No expression provided");
		else {
			CirExecution execution = statement.get_tree().get_localizer().get_execution(statement);
			return new SymConstraint(execution, SymbolFactory.sym_condition(expression, value));
		}
	}
	/**
	 * @param execution statement being executed in the times-constraints
	 * @param operator {<, <=, ==, >=, >}
	 * @param times times as limit of being executed in the program
	 * @return constraint being evaluated to check the times of execution point being run
	 * @throws Exception
	 */
	public static SymConstraint stmt_constraint(CirExecution execution, COperator operator, int times) throws Exception {
		if(execution == null)
			throw new IllegalArgumentException("Invalid execution: null");
		else if(times <= 0)
			throw new IllegalArgumentException("Invalid times: " + times);
		else {
			SymbolExpression loperand = SymbolFactory.sym_expression(execution);
			SymbolExpression roperand = SymbolFactory.sym_expression(Integer.valueOf(times));
			switch(operator) {
			case smaller_tn:	return new SymConstraint(execution, SymbolFactory.smaller_tn(loperand, roperand));
			case smaller_eq:	return new SymConstraint(execution, SymbolFactory.smaller_eq(loperand, roperand));
			case greater_tn:	return new SymConstraint(execution, SymbolFactory.greater_tn(loperand, roperand));
			case greater_eq:	return new SymConstraint(execution, SymbolFactory.greater_eq(loperand, roperand));
			case equal_with:	return new SymConstraint(execution, SymbolFactory.equal_with(loperand, roperand));
			case not_equals:	return new SymConstraint(execution, SymbolFactory.not_equals(loperand, roperand));
			default: 			throw new IllegalArgumentException("Invalid operator: " + operator);
			}
		}
	}
	/**
	 * @param statement statement being executed in the times-constraints
	 * @param operator {<, <=, ==, >=, >}
	 * @param times times as limit of being executed in the program
	 * @return constraint being evaluated to check the times of execution point being run
	 * @throws Exception
	 */
	public static SymConstraint stmt_constraint(CirStatement statement, COperator operator, int times) throws Exception {
		if(statement == null)
			throw new IllegalArgumentException("Invalid execution: null");
		else if(times <= 0)
			throw new IllegalArgumentException("Invalid times: " + times);
		else {
			CirExecution execution = statement.get_tree().get_localizer().get_execution(statement);
			SymbolExpression loperand = SymbolFactory.sym_expression(execution);
			SymbolExpression roperand = SymbolFactory.sym_expression(Integer.valueOf(times));
			switch(operator) {
			case smaller_tn:	return new SymConstraint(execution, SymbolFactory.smaller_tn(loperand, roperand));
			case smaller_eq:	return new SymConstraint(execution, SymbolFactory.smaller_eq(loperand, roperand));
			case greater_tn:	return new SymConstraint(execution, SymbolFactory.greater_tn(loperand, roperand));
			case greater_eq:	return new SymConstraint(execution, SymbolFactory.greater_eq(loperand, roperand));
			case equal_with:	return new SymConstraint(execution, SymbolFactory.equal_with(loperand, roperand));
			case not_equals:	return new SymConstraint(execution, SymbolFactory.not_equals(loperand, roperand));
			default: 			throw new IllegalArgumentException("Invalid operator: " + operator);
			}
		}
	}
	
	/* statement errors */
	/**
	 * @param orig_flow the original flow being replaced
	 * @param muta_flow the mutation flow to replace 
	 * @return flow_error(orig_flow, muta_flow)
	 * @throws Exception
	 */
	public static SymFlowError flow_error(CirExecutionFlow orig_flow, 
			CirExecutionFlow muta_flow) throws Exception {
		if(orig_flow == null)
			throw new IllegalArgumentException("Invalid orig_flow: null");
		else if(muta_flow == null)
			throw new IllegalArgumentException("Invalid muta_flow: null");
		else if(orig_flow.get_source() != muta_flow.get_source())
			throw new IllegalArgumentException("Unmatched: " + orig_flow + "\t" + muta_flow);
		else if(orig_flow.get_target() == muta_flow.get_target())
			throw new IllegalArgumentException("Unmatched: " + orig_flow + "\t" + muta_flow);
		else return new SymFlowError(orig_flow.get_source(), orig_flow, muta_flow);
	}
	/**
	 * @param statement where the exception will be thrown
	 * @return trap_error(statement)
	 * @throws Exception
	 */
	public static SymTrapError trap_error(CirStatement statement) throws Exception {
		if(statement == null)
			throw new IllegalArgumentException("Invalid execution: null");
		else {
			CirExecution execution = statement.get_tree().get_localizer().get_execution(statement);
			return new SymTrapError(execution);
		}
	}
	/**
	 * @param execution where the exception will be thrown
	 * @return trap_error(statement)
	 * @throws Exception
	 */
	public static SymTrapError trap_error(CirExecution execution) throws Exception {
		if(execution == null)
			throw new IllegalArgumentException("Invalid execution: null");
		else {
			return new SymTrapError(execution);
		}
	}
	
	/* value errors */
	/**
	 * @param orig_expression original expression being replaced
	 * @param muta_expression the expression to replace original
	 * @return expr_error(orig_expression.statement, orig_expression, muta_expression)
	 * @throws Exception
	 */
	public static SymExpressionError expr_error(CirExpression orig_expression, Object muta_expression) throws Exception {
		if(orig_expression == null || orig_expression.statement_of() == null)
			throw new IllegalArgumentException("Invalid orig_expression: null");
		else if(muta_expression == null)
			throw new IllegalArgumentException("Invalid muta_expression: null");
		else {
			CirExecution execution = orig_expression.get_tree().
					get_localizer().get_execution(orig_expression.statement_of());
			SymbolExpression orig_expr = SymbolFactory.sym_expression(orig_expression);
			SymbolExpression muta_expr = SymbolFactory.sym_expression(muta_expression);
			return new SymExpressionError(execution, orig_expression, orig_expr, muta_expr);
		}
	}
	/**
	 * @param orig_expression original expression being replaced
	 * @param muta_expression the expression to replace original
	 * @return refer_error(orig_expression.statement, orig_expression, muta_expression)
	 * @throws Exception
	 */
	public static SymReferenceError refr_error(CirReferExpression orig_expression, Object muta_expression) throws Exception {
		if(orig_expression == null || orig_expression.statement_of() == null)
			throw new IllegalArgumentException("Invalid orig_expression: null");
		else if(muta_expression == null)
			throw new IllegalArgumentException("Invalid muta_expression: null");
		else {
			CirExecution execution = orig_expression.get_tree().
					get_localizer().get_execution(orig_expression.statement_of());
			SymbolExpression orig_expr = SymbolFactory.sym_expression(orig_expression);
			SymbolExpression muta_expr = SymbolFactory.sym_expression(muta_expression);
			return new SymReferenceError(execution, orig_expression, orig_expr, muta_expr);
		}
	}
	/**
	 * @param orig_expression original expression being replaced
	 * @param muta_expression the expression to replace original
	 * @return state_error(orig_expression.statement, orig_expression, muta_expression)
	 * @throws Exception
	 */
	public static SymStateValueError stat_error(CirReferExpression orig_expression, Object muta_expression) throws Exception {
		if(orig_expression == null || orig_expression.statement_of() == null)
			throw new IllegalArgumentException("Invalid orig_expression: null");
		else if(muta_expression == null)
			throw new IllegalArgumentException("Invalid muta_expression: null");
		else {
			CirExecution execution = orig_expression.get_tree().
					get_localizer().get_execution(orig_expression.statement_of());
			SymbolExpression orig_expr = SymbolFactory.sym_expression(orig_expression);
			SymbolExpression muta_expr = SymbolFactory.sym_expression(muta_expression);
			return new SymStateValueError(execution, orig_expression, orig_expr, muta_expr);
		}
	}
	
	/* CIR mutation */
	/**
	 * @param constraint the constraint to cause the infection in the mutation
	 * @param state_error the initial error caused by the infection once the constraint is met
	 * @return the infection-pair of [constraint, state_error] created in the factory
	 * @throws Exception
	 */
	public static CirMutation cir_mutation(SymConstraint constraint, SymStateError state_error) throws Exception {
		if(constraint == null)
			throw new IllegalArgumentException("Invalid constraint: null");
		else if(state_error == null)
			throw new IllegalArgumentException("Invalid state_error: null");
		else {
			return new CirMutation(constraint, state_error);
		}
	}
	/**
	 * @param constraint the constraint to be optimized
	 * @param contexts the contexts used to concreting the input constraint
	 * @return the evaluated output constraint from input based on contexts
	 * @throws Exception
	 */
	private static SymConstraint optimize(SymConstraint constraint, SymbolStateContexts contexts) throws Exception {
		if(constraint == null)
			throw new IllegalArgumentException("Invalid constraint: null");
		else {
			CirExecution execution = constraint.get_execution();
			SymbolExpression expression = SymInstanceUtils.
					evaluate_on(constraint.get_condition(), contexts);
			return new SymConstraint(execution, expression);
		}
	}
	/**
	 * @param state_error input state error being optimized
	 * @param contexts
	 * @return the output state error being optimized from the contexts
	 * @throws Exception
	 */
	private static SymStateError optimize(SymStateError state_error, SymbolStateContexts contexts) throws Exception {
		if(state_error == null)
			throw new IllegalArgumentException("Invalid state_error: null");
		else if(state_error instanceof SymFlowError || state_error instanceof SymTrapError)
			return state_error;
		else if(state_error instanceof SymValueError) {
			CirExecution execution = state_error.get_execution();
			CirExpression expression = ((SymValueError) state_error).get_expression();
			SymbolExpression orig_value = ((SymValueError) state_error).get_original_value();
			SymbolExpression muta_value = ((SymValueError) state_error).get_mutation_value();
			
			try {
				orig_value = SymInstanceUtils.evaluate_on(orig_value, contexts);
				muta_value = SymInstanceUtils.evaluate_on(orig_value, contexts);
			}
			catch(ArithmeticException ex) {
				return new SymTrapError(state_error.get_execution());
			}
			
			if(state_error instanceof SymExpressionError) {
				return new SymExpressionError(execution, expression, orig_value, muta_value);
			}
			else if(state_error instanceof SymReferenceError) {
				return new SymReferenceError(execution, expression, orig_value, muta_value);
			}
			else if(state_error instanceof SymStateValueError) {
				return new SymStateValueError(execution, expression, orig_value, muta_value);
			}
			else {
				throw new IllegalArgumentException("Invalid: " + state_error.getClass().getSimpleName());
			}
		}
		else {
			throw new IllegalArgumentException("Invalid: " + state_error.getClass().getSimpleName());
		}
	}
	/**
	 * @param instance
	 * @param contexts
	 * @return
	 * @throws Exception
	 */
	public static SymInstance optimize(SymInstance instance, SymbolStateContexts contexts) throws Exception {
		if(instance instanceof SymConstraint)
			return SymInstanceUtils.optimize((SymConstraint) instance, contexts);
		else
			return SymInstanceUtils.optimize((SymStateError) instance, contexts);
	}
	/**
	 * @param mutation
	 * @param contexts
	 * @return concrete mutation instance from the contexts
	 * @throws Exception
	 */
	public static CirMutation optimize(CirMutation mutation, SymbolStateContexts contexts) throws Exception {
		return new CirMutation(
				SymInstanceUtils.optimize(mutation.get_constraint(), contexts),
				SymInstanceUtils.optimize(mutation.get_state_error(), contexts));
	}
	
}
