package com.jcsa.jcmutest.mutant.sym2mutant.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.jcsa.jcparse.lang.irlang.expr.CirReferExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirWaitExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.stmt.CirArgumentList;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
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
	
	/* singleton mode */
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
	/* state error propagations */
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
				
				propagations.put(SymInstanceUtils.flow_error(true_flow, fals_flow), 
						SymInstanceUtils.expr_constraint(statement, condition, true));
				propagations.put(SymInstanceUtils.flow_error(fals_flow, true_flow), 
						SymInstanceUtils.expr_constraint(statement, condition, false));
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
			SymInstanceUtils.propagate_on(source_error, propagations);
			for(SymStateError next_error : propagations.keySet()) {
				SymConstraint constraint = propagations.get(next_error);
				next_mutations.add(SymInstanceUtils.cir_mutation(constraint, next_error));
			}
			return next_mutations;
		}
	}
	
}
