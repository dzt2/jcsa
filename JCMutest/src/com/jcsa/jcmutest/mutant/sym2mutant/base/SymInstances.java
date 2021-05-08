package com.jcsa.jcmutest.mutant.sym2mutant.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jcsa.jcmutest.mutant.sym2mutant.CirMutation;
import com.jcsa.jcmutest.mutant.sym2mutant.pass.CirAddressOfPropagator;
import com.jcsa.jcmutest.mutant.sym2mutant.pass.CirArgumentListPropagator;
import com.jcsa.jcmutest.mutant.sym2mutant.pass.CirArithAddPropagator;
import com.jcsa.jcmutest.mutant.sym2mutant.pass.CirArithDivPropagator;
import com.jcsa.jcmutest.mutant.sym2mutant.pass.CirArithModPropagator;
import com.jcsa.jcmutest.mutant.sym2mutant.pass.CirArithMulPropagator;
import com.jcsa.jcmutest.mutant.sym2mutant.pass.CirArithNegPropagator;
import com.jcsa.jcmutest.mutant.sym2mutant.pass.CirArithSubPropagator;
import com.jcsa.jcmutest.mutant.sym2mutant.pass.CirAssignPropagator;
import com.jcsa.jcmutest.mutant.sym2mutant.pass.CirBitwsAndPropagator;
import com.jcsa.jcmutest.mutant.sym2mutant.pass.CirBitwsIorPropagator;
import com.jcsa.jcmutest.mutant.sym2mutant.pass.CirBitwsLshPropagator;
import com.jcsa.jcmutest.mutant.sym2mutant.pass.CirBitwsRshPropagator;
import com.jcsa.jcmutest.mutant.sym2mutant.pass.CirBitwsRsvPropagator;
import com.jcsa.jcmutest.mutant.sym2mutant.pass.CirBitwsXorPropagator;
import com.jcsa.jcmutest.mutant.sym2mutant.pass.CirDereferencePropagator;
import com.jcsa.jcmutest.mutant.sym2mutant.pass.CirEqualWithPropagator;
import com.jcsa.jcmutest.mutant.sym2mutant.pass.CirErrorPropagator;
import com.jcsa.jcmutest.mutant.sym2mutant.pass.CirFieldOfPropagator;
import com.jcsa.jcmutest.mutant.sym2mutant.pass.CirGreaterEqPropagator;
import com.jcsa.jcmutest.mutant.sym2mutant.pass.CirGreaterTnPropagator;
import com.jcsa.jcmutest.mutant.sym2mutant.pass.CirInitializerPropagator;
import com.jcsa.jcmutest.mutant.sym2mutant.pass.CirLogicAndPropagator;
import com.jcsa.jcmutest.mutant.sym2mutant.pass.CirLogicIorPropagator;
import com.jcsa.jcmutest.mutant.sym2mutant.pass.CirLogicNotPropagator;
import com.jcsa.jcmutest.mutant.sym2mutant.pass.CirNotEqualsPropagator;
import com.jcsa.jcmutest.mutant.sym2mutant.pass.CirSmallerEqPropagator;
import com.jcsa.jcmutest.mutant.sym2mutant.pass.CirSmallerTnPropagator;
import com.jcsa.jcmutest.mutant.sym2mutant.pass.CirTypeCastPropagator;
import com.jcsa.jcmutest.mutant.sym2mutant.pass.CirWaitValuePropagator;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirAddressExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirCastExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirComputeExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirDeferExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirFieldExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirInitializerBody;
import com.jcsa.jcparse.lang.irlang.expr.CirWaitExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.stmt.CirArgumentList;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symbol.SymbolBinaryExpression;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.lang.symbol.SymbolIdentifier;
import com.jcsa.jcparse.parse.symbol.SymbolEvaluator;
import com.jcsa.jcparse.parse.symbol.SymbolStateContexts;


/**
 * It provides interfaces to construct and optimize SymInstance within the mutation testing context.
 * 
 * @author yukimula
 *
 */
public class SymInstances {
	
	/* utilities methods */
	/**
	 * @param location
	 * @return the execution point where location is defined
	 * @throws Exception
	 */
	public static CirExecution get_execution(CirNode location) throws Exception {
		while(location != null) {
			if(location instanceof CirStatement) {
				return location.get_tree().get_localizer().get_execution((CirStatement) location);
			}
			else {
				location = location.get_parent();
			}
		}
		throw new IllegalArgumentException("No execution point found.");
	}
	/**
	 * @param expression
	 * @return perform context insensitive evaluation to simplify the expression
	 * @throws Exception
	 */
	public static SymbolExpression evaluate(SymbolExpression expression, SymbolStateContexts contexts) throws Exception {
		return SymbolEvaluator.evaluate_on(expression, contexts);
	}
	/**
	 * @param constraint
	 * @return [ execution, operator, times ] or null if not statement coverage constraint
	 * @throws Exception
	 */
	public static Object[] divide_stmt_constraint(SymConstraint constraint) throws Exception {
		if(constraint == null)
			return null;
		else {
			SymbolExpression condition = constraint.get_condition();
			if(condition instanceof SymbolBinaryExpression) {
				SymbolExpression loperand = ((SymbolBinaryExpression) condition).get_loperand();
				SymbolExpression roperand = ((SymbolBinaryExpression) condition).get_roperand();
				COperator operator = ((SymbolBinaryExpression) condition).get_operator().get_operator();
				
				if(loperand instanceof SymbolIdentifier && loperand.get_source() instanceof CirExecution) {
					CirExecution execution = (CirExecution) loperand.get_source();
					Integer times = ((SymbolConstant) roperand).get_int();
					return new Object[] { execution, operator, times };
				}
				else if(roperand instanceof SymbolIdentifier && roperand.get_source() instanceof CirExecution) {
					CirExecution execution = (CirExecution) roperand.get_source();
					Integer times = ((SymbolConstant) loperand).get_int();
					switch(operator) {
					case greater_tn:	operator = COperator.smaller_tn;	break;
					case greater_eq:	operator = COperator.smaller_eq;	break;
					case smaller_tn:	operator = COperator.greater_tn;	break;
					case smaller_eq:	operator = COperator.greater_eq;	break;
					case equal_with:	break;
					case not_equals:	break;
					default: throw new IllegalArgumentException("Invalid operator: " + operator);
					}
					return new Object[] { execution, operator, times };
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
	/**
	 * @param condition
	 * @return [ execution, operator, times ] or null if not statement coverage constraint
	 * @throws Exception
	 */
	public static Object[] divide_stmt_constraint(SymbolExpression condition) throws Exception {
		if(condition == null)
			return null;
		else {
			if(condition instanceof SymbolBinaryExpression) {
				SymbolExpression loperand = ((SymbolBinaryExpression) condition).get_loperand();
				SymbolExpression roperand = ((SymbolBinaryExpression) condition).get_roperand();
				COperator operator = ((SymbolBinaryExpression) condition).get_operator().get_operator();
				
				if(loperand instanceof SymbolIdentifier && loperand.get_source() instanceof CirExecution) {
					CirExecution execution = (CirExecution) loperand.get_source();
					Integer times = ((SymbolConstant) roperand).get_int();
					return new Object[] { execution, operator, times };
				}
				else if(roperand instanceof SymbolIdentifier && roperand.get_source() instanceof CirExecution) {
					CirExecution execution = (CirExecution) roperand.get_source();
					Integer times = ((SymbolConstant) loperand).get_int();
					switch(operator) {
					case greater_tn:	operator = COperator.smaller_tn;	break;
					case greater_eq:	operator = COperator.smaller_eq;	break;
					case smaller_tn:	operator = COperator.greater_tn;	break;
					case smaller_eq:	operator = COperator.greater_eq;	break;
					case equal_with:	break;
					case not_equals:	break;
					default: throw new IllegalArgumentException("Invalid operator: " + operator);
					}
					return new Object[] { execution, operator, times };
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
	
	/* constructors */
	/**
	 * @param execution execution point in control flow graph where the constraint is evaluated
	 * @param expression expression to be evaluated as true or false at particular statement
	 * @param value the boolean value expected to be hold by specified expression at statement
	 * @return create a constraint evaluating the expression as value (true or false) at specified execution point.
	 * @throws Exception
	 */
	public static SymConstraint expr_constraint(CirExecution execution, Object expression, boolean value) throws Exception {
		if(execution == null)
			throw new IllegalArgumentException("Invalid execution: null");
		else if(expression == null)
			throw new IllegalArgumentException("Invalid expression: null");
		else 
			return new SymConstraint(execution, SymbolFactory.sym_condition(expression, value));
	}
	/**
	 * @param statement the statement of which execution point in control flow graph where the constraint is evaluated
	 * @param expression expression to be evaluated as true or false at particular statement
	 * @param value the boolean value expected to be hold by specified expression at statement
	 * @return create a constraint evaluating the expression as value (true or false) at specified execution point.
	 * @throws Exception
	 */
	public static SymConstraint expr_constraint(CirStatement statement, Object expression, boolean value) throws Exception {
		if(statement == null)
			throw new IllegalArgumentException("Invalid statement: null");
		else if(expression == null)
			throw new IllegalArgumentException("Invalid expression: null");
		else 
			return new SymConstraint(SymInstances.get_execution(statement), SymbolFactory.sym_condition(expression, value));
	}
	/**
	 * @param execution the execution point of which statement is counted during coverage analysis
	 * @param operator {<, <=, >, >=, ==, !=}
	 * @param times the times used as right operand
	 * @return evaluate(execution, execution.statement operator times)
	 * @throws Exception
	 */
	public static SymConstraint stmt_constraint(CirExecution execution, COperator operator, int times) throws Exception {
		if(execution == null)
			throw new IllegalArgumentException("Invalid execution: null");
		else if(operator == null)
			throw new IllegalArgumentException("Invalid operator: null");
		else if(times <= 0)
			throw new IllegalArgumentException("Invalid times: " + times);
		else {
			SymbolExpression loperand = SymbolFactory.sym_expression(execution);
			SymbolExpression roperand = SymbolFactory.sym_expression(Integer.valueOf(times));
			
			SymbolExpression condition;
			switch(operator) {
			case greater_tn:	condition = SymbolFactory.greater_tn(loperand, roperand);	break;
			case greater_eq:	condition = SymbolFactory.greater_eq(loperand, roperand);	break;
			case smaller_tn:	condition = SymbolFactory.smaller_tn(loperand, roperand);	break;
			case smaller_eq:	condition = SymbolFactory.smaller_eq(loperand, roperand);	break;
			case equal_with:	condition = SymbolFactory.equal_with(loperand, roperand);	break;
			case not_equals:	condition = SymbolFactory.not_equals(loperand, roperand);	break;
			default: 			throw new IllegalArgumentException("Invalid operator: " + operator);
			}
			
			return new SymConstraint(execution, condition);
		}
	}
	/**
	 * @param execution the execution point of which statement is counted during coverage analysis
	 * @param operator {<, <=, >, >=, ==, !=}
	 * @param times the times used as right operand
	 * @return evaluate(execution, execution.statement operator times)
	 * @throws Exception
	 */
	public static SymConstraint stmt_constraint(CirStatement statement, COperator operator, int times) throws Exception {
		if(statement == null)
			throw new IllegalArgumentException("Invalid statement: null");
		else if(operator == null)
			throw new IllegalArgumentException("Invalid operator: null");
		else if(times <= 0)
			throw new IllegalArgumentException("Invalid times: " + times);
		else {
			CirExecution execution = SymInstances.get_execution(statement);
			SymbolExpression loperand = SymbolFactory.sym_expression(execution);
			SymbolExpression roperand = SymbolFactory.sym_expression(Integer.valueOf(times));
			
			SymbolExpression condition;
			switch(operator) {
			case greater_tn:	condition = SymbolFactory.greater_tn(loperand, roperand);	break;
			case greater_eq:	condition = SymbolFactory.greater_eq(loperand, roperand);	break;
			case smaller_tn:	condition = SymbolFactory.smaller_tn(loperand, roperand);	break;
			case smaller_eq:	condition = SymbolFactory.smaller_eq(loperand, roperand);	break;
			case equal_with:	condition = SymbolFactory.equal_with(loperand, roperand);	break;
			case not_equals:	condition = SymbolFactory.not_equals(loperand, roperand);	break;
			default: 			throw new IllegalArgumentException("Invalid operator: " + operator);
			}
			
			return new SymConstraint(execution, condition);
		}
	}
	/**
	 * @param orig_flow
	 * @param muta_flow
	 * @return create a flow error that replaces original flow with mutation flow being executed during testing
	 * @throws Exception
	 */
	public static SymFlowError flow_error(CirExecutionFlow orig_flow, CirExecutionFlow muta_flow) throws Exception {
		if(orig_flow == null)
			throw new IllegalArgumentException("Invalid orig_flow: null");
		else if(muta_flow == null)
			throw new IllegalArgumentException("Invalid muta_flow: null");
		else if(orig_flow.get_source() != muta_flow.get_source())
			throw new IllegalArgumentException("Unmatched: " + orig_flow + " --> " + muta_flow);
		else 
			return new SymFlowError(orig_flow.get_source(), orig_flow, muta_flow);
	}
	/**
	 * @param execution
	 * @return creating a trapping point at specified execution point.
	 * @throws Exception
	 */
	public static SymTrapError trap_error(CirExecution execution) throws Exception {
		if(execution == null)
			throw new IllegalArgumentException("Invalid execution: null");
		else
			return new SymTrapError(execution);
	}
	/**
	 * @param execution
	 * @return creating a trapping point at specified execution point.
	 * @throws Exception
	 */
	public static SymTrapError trap_error(CirStatement statement) throws Exception {
		if(statement == null)
			throw new IllegalArgumentException("Invalid statement: null");
		else
			return new SymTrapError(SymInstances.get_execution(statement));
	}
	/**
	 * @param orig_expression the original expression to be replaced
	 * @param muta_expression the expression to replace the original
	 * @return
	 * @throws Exception
	 */
	public static SymExpressionError expr_error(CirExpression orig_expression, Object muta_expression) throws Exception {
		if(orig_expression == null || orig_expression.statement_of() == null)
			throw new IllegalArgumentException("Invalid original expression: null");
		else if(muta_expression == null)
			throw new IllegalArgumentException("Invalid mutation expression: null");
		else
			return new SymExpressionError(
					SymInstances.get_execution(orig_expression),
					orig_expression,
					SymbolFactory.sym_expression(orig_expression),
					SymbolFactory.sym_expression(muta_expression));
	}
	/**
	 * @param orig_expression the original expression to be replaced
	 * @param muta_expression the expression to replace the original
	 * @return
	 * @throws Exception
	 */
	public static SymReferenceError refr_error(CirExpression orig_expression, Object muta_expression) throws Exception {
		if(orig_expression == null || orig_expression.statement_of() == null)
			throw new IllegalArgumentException("Invalid original expression: null");
		else if(muta_expression == null)
			throw new IllegalArgumentException("Invalid mutation expression: null");
		else
			return new SymReferenceError(
					SymInstances.get_execution(orig_expression),
					orig_expression,
					SymbolFactory.sym_expression(orig_expression),
					SymbolFactory.sym_expression(muta_expression));
	}
	/**
	 * @param orig_expression the original expression to be replaced
	 * @param muta_expression the expression to replace the original
	 * @return
	 * @throws Exception
	 */
	public static SymStateValueError stat_error(CirExpression orig_expression, Object muta_expression) throws Exception {
		if(orig_expression == null || orig_expression.statement_of() == null)
			throw new IllegalArgumentException("Invalid original expression: null");
		else if(muta_expression == null)
			throw new IllegalArgumentException("Invalid mutation expression: null");
		else
			return new SymStateValueError(
					SymInstances.get_execution(orig_expression),
					orig_expression,
					SymbolFactory.sym_expression(orig_expression),
					SymbolFactory.sym_expression(muta_expression));
	}
	
	/* optimizations */
	private static SymConstraint opt_constraint(SymConstraint constraint, SymbolStateContexts contexts) throws Exception {
		if(constraint == null)
			throw new IllegalArgumentException("Invalid constraint: null");
		else {
			SymbolExpression condition = constraint.get_condition();
			condition = SymInstances.evaluate(condition, contexts);
			return new SymConstraint(constraint.get_execution(), condition);
		}
	}
	private static SymFlowError  opt_flow_error(SymFlowError error, SymbolStateContexts contexts) throws Exception {
		if(error == null)
			throw new IllegalArgumentException("Invalid error: null");
		return error;
	}
	private static SymTrapError  opt_trap_error(SymTrapError error, SymbolStateContexts contexts) throws Exception {
		if(error == null)
			throw new IllegalArgumentException("Invalid error: null");
		return error;
	}
	private static SymStateError opt_expr_error(SymExpressionError error, SymbolStateContexts contexts) throws Exception {
		if(error == null)
			throw new IllegalArgumentException("Invalid error: null");
		else {
			SymbolExpression orig_value = SymInstances.evaluate(error.get_original_value(), contexts);
			SymbolExpression muta_value;
			try {
				muta_value = SymInstances.evaluate(error.get_mutation_value(), contexts);
			}
			catch(ArithmeticException ex) {
				return new SymTrapError(error.get_execution());
			}
			return new SymExpressionError(error.get_execution(), error.get_expression(), orig_value, muta_value);
		}
	}
	private static SymStateError opt_refr_error(SymReferenceError error, SymbolStateContexts contexts) throws Exception {
		if(error == null)
			throw new IllegalArgumentException("Invalid error: null");
		else {
			SymbolExpression orig_value = error.get_original_value();
			SymbolExpression muta_value;
			try {
				muta_value = SymInstances.evaluate(error.get_mutation_value(), contexts);
			}
			catch(ArithmeticException ex) {
				return new SymTrapError(error.get_execution());
			}
			return new SymReferenceError(error.get_execution(), error.get_expression(), orig_value, muta_value);
		}
	}
	private static SymStateError opt_stat_error(SymStateValueError error, SymbolStateContexts contexts) throws Exception {
		if(error == null)
			throw new IllegalArgumentException("Invalid error: null");
		else {
			SymbolExpression orig_value = error.get_original_value();
			SymbolExpression muta_value;
			try {
				muta_value = SymInstances.evaluate(error.get_mutation_value(), contexts);
			}
			catch(ArithmeticException ex) {
				return new SymTrapError(error.get_execution());
			}
			return new SymStateValueError(error.get_execution(), error.get_expression(), orig_value, muta_value);
		}
	}
	/**
	 * @param instance the symbolic instance to be evaluated and simplified
	 * @param contexts contextual information used to concretize the inputs
	 * @return
	 * @throws Exception
	 */
	public static SymInstance optimize(SymInstance instance, SymbolStateContexts contexts) throws Exception {
		if(instance == null)
			throw new IllegalArgumentException("Invalid instance: null");
		else if(instance instanceof SymConstraint)
			return SymInstances.opt_constraint((SymConstraint) instance, contexts);
		else if(instance instanceof SymFlowError)
			return SymInstances.opt_flow_error((SymFlowError) instance, contexts);
		else if(instance instanceof SymTrapError)
			return SymInstances.opt_trap_error((SymTrapError) instance, contexts);
		else if(instance instanceof SymExpressionError)
			return SymInstances.opt_expr_error((SymExpressionError) instance, contexts);
		else if(instance instanceof SymReferenceError)
			return SymInstances.opt_refr_error((SymReferenceError) instance, contexts);
		else if(instance instanceof SymStateValueError)
			return SymInstances.opt_stat_error((SymStateValueError) instance, contexts);
		else
			throw new IllegalArgumentException("Unsupported: " + instance.getClass().getSimpleName());
	}
	/**
	 * @param instance
	 * @return context insensitive optimized solution
	 * @throws Exception
	 */
	public static SymInstance optimize(SymInstance instance) throws Exception {
		return SymInstances.optimize(instance, null);
	}
	
	/* CIR-mutation */
	/**
	 * @param constraint
	 * @param state_error
	 * @return create a constraint-error pair as infection object.
	 * @throws Exception
	 */
	public static CirMutation cir_mutation(SymConstraint constraint, SymStateError state_error) throws Exception {
		if(constraint == null)
			throw new IllegalArgumentException("Invalid constriant: null");
		else if(state_error == null)
			throw new IllegalArgumentException("Invalid state_error null");
		else 
			return new CirMutation(constraint, state_error);
	}
	/** mapping from expression operator to the propagator for generating state error **/
	static private Map<COperator, CirErrorPropagator> propagators;
	static {
		propagators = new HashMap<COperator, CirErrorPropagator>();
		
		propagators.put(COperator.arith_add, new CirArithAddPropagator());
		propagators.put(COperator.arith_sub, new CirArithSubPropagator());
		propagators.put(COperator.arith_mul, new CirArithMulPropagator());
		propagators.put(COperator.arith_div, new CirArithDivPropagator());
		propagators.put(COperator.arith_mod, new CirArithModPropagator());
		propagators.put(COperator.negative, new CirArithNegPropagator());
		
		propagators.put(COperator.bit_not, new CirBitwsRsvPropagator());
		propagators.put(COperator.bit_and, new CirBitwsAndPropagator());
		propagators.put(COperator.bit_or, new CirBitwsIorPropagator());
		propagators.put(COperator.bit_xor, new CirBitwsXorPropagator());
		propagators.put(COperator.left_shift, new CirBitwsLshPropagator());
		propagators.put(COperator.righ_shift, new CirBitwsRshPropagator());
		
		propagators.put(COperator.assign, new CirAssignPropagator());
		propagators.put(COperator.address_of, new CirAddressOfPropagator());
		propagators.put(COperator.dereference, new CirDereferencePropagator());
		
		propagators.put(COperator.greater_eq, new CirGreaterEqPropagator());
		propagators.put(COperator.greater_tn, new CirGreaterTnPropagator());
		propagators.put(COperator.smaller_eq, new CirSmallerEqPropagator());
		propagators.put(COperator.smaller_tn, new CirSmallerTnPropagator());
		propagators.put(COperator.equal_with, new CirEqualWithPropagator());
		propagators.put(COperator.not_equals, new CirNotEqualsPropagator());
		
		propagators.put(COperator.logic_and, new CirLogicAndPropagator());
		propagators.put(COperator.logic_or, new CirLogicIorPropagator());
		propagators.put(COperator.logic_not, new CirLogicNotPropagator());
		
		propagators.put(COperator.arith_add_assign, new CirFieldOfPropagator());
		propagators.put(COperator.arith_sub_assign, new CirTypeCastPropagator());
		propagators.put(COperator.arith_mul_assign, new CirInitializerPropagator());
		propagators.put(COperator.arith_div_assign, new CirArgumentListPropagator());
		propagators.put(COperator.arith_mod_assign, new CirWaitValuePropagator());
	}
	/**
	 * generate the error-constraint pair in local propagation from source error and append
	 * them in the propagations table.
	 * @param cir_mutations
	 * @param source_error
	 * @param propagations
	 * @throws Exception
	 */
	private static void propagate_on(SymStateError source_error, Map<SymStateError, SymConstraint> propagations) throws Exception {
		/* get the next location for error of propagation */
		CirExpression location;
		if(source_error instanceof SymExpressionError) {
			location = ((SymExpressionError) source_error).get_expression();
		}
		else if(source_error instanceof SymReferenceError) {
			location = ((SymReferenceError) source_error).get_expression();
		}
		else {
			location = null;
		}
		
		/* syntax-directed error propagation algorithms */
		if(location != null) {
			CirNode parent = location.get_parent();
			
			if(parent instanceof CirDeferExpression) {
				propagators.get(COperator.dereference).propagate(source_error, location, parent, propagations);
			}
			else if(parent instanceof CirFieldExpression) {
				propagators.get(COperator.arith_add_assign).propagate(source_error, location, parent, propagations);
			}
			else if(parent instanceof CirAddressExpression) {
				propagators.get(COperator.address_of).propagate(source_error, location, parent, propagations);
			}
			else if(parent instanceof CirCastExpression) {
				propagators.get(COperator.arith_sub_assign).propagate(source_error, location, parent, propagations);
			}
			else if(parent instanceof CirInitializerBody) {
				propagators.get(COperator.arith_mul_assign).propagate(source_error, location, parent, propagations);
			}
			else if(parent instanceof CirWaitExpression) {
				propagators.get(COperator.arith_mod_assign).propagate(source_error, location, parent, propagations);
			}
			else if(parent instanceof CirComputeExpression) {
				propagators.get(((CirComputeExpression) parent).get_operator()).propagate(source_error, location, parent, propagations);
			}
			else if(parent instanceof CirArgumentList) {
				propagators.get(COperator.arith_div_assign).propagate(source_error, location, parent, propagations);
			}
			else if(parent instanceof CirIfStatement
					|| parent instanceof CirCaseStatement) {
				CirStatement statement = (CirStatement) parent;
				CirExecution execution = statement.get_tree().get_localizer().get_execution(statement);
				CirExecutionFlow true_flow = execution.get_ou_flow(0);
				CirExecutionFlow fals_flow = execution.get_ou_flow(1);
				
				CirExpression condition;
				if(statement instanceof CirIfStatement) {
					condition = ((CirIfStatement) statement).get_condition();
				}
				else {
					condition = ((CirCaseStatement) statement).get_condition();
				}
				
				propagations.put(SymInstances.flow_error(true_flow, fals_flow), 
						SymInstances.expr_constraint(statement, condition, true));
				propagations.put(SymInstances.flow_error(fals_flow, true_flow), 
						SymInstances.expr_constraint(statement, condition, false));
			}
			else if(parent instanceof CirAssignStatement) {
				propagators.get(COperator.assign).propagate(source_error, location, parent, propagations);
			}
		}
	}
	/**
	 * @param cir_mutations
	 * @param source_error
	 * @return the set of CirMutation generated from source error as its next propagation gender
	 * @throws Exception
	 */
	public static Collection<CirMutation> propagate(SymStateError source_error) throws Exception {
		if(source_error == null)
			throw new IllegalArgumentException("Invalid source_error: null");
		else {
			List<CirMutation> next_mutations = new ArrayList<CirMutation>();
			Map<SymStateError, SymConstraint> propagations = new HashMap<SymStateError, SymConstraint>();
			SymInstances.propagate_on(source_error, propagations);
			for(SymStateError next_error : propagations.keySet()) {
				SymConstraint constraint = propagations.get(next_error);
				next_mutations.add(SymInstances.cir_mutation(constraint, next_error));
			}
			return next_mutations;
		}
	}
	
}
