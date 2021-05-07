package com.jcsa.jcmutest.mutant.sym2mutant.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.jcsa.jcmutest.mutant.sym2mutant.base.SymConstraint;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymExpressionError;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymFlowError;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymInstance;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymReferenceError;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymStateError;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymTrapError;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymValueError;
import com.jcsa.jcmutest.mutant.sym2mutant.tree.SymCondition;
import com.jcsa.jcmutest.mutant.sym2mutant.tree.SymOperator;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionEdge;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionPath;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionPathFinder;
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
import com.jcsa.jcparse.parse.symbol.SymbolEvaluator;
import com.jcsa.jcparse.parse.symbol.SymbolStateContexts;

/**
 * It is used to standardize SymInstance and generate SymCondition(s) from them.
 * 
 * @author yukimula
 *
 */
public class SymConditionUtils {
	
	/* classifier of expression */
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
	/**
	 * @param expr
	 * @param contexts
	 * @return
	 * @throws Exception
	 */
	private static SymbolExpression evaluate_on(SymbolExpression expr, SymbolStateContexts contexts) throws Exception {
		return SymbolEvaluator.evaluate_on(expr, contexts);
	}
	
	/* CIR location analyzers */
	/**
	 * @param expression
	 * @return whether the expression is a reference
	 * @throws Exception
	 */
	private static boolean is_reference(SymbolNode expression) throws Exception {
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
	 * collect all the reference(s) node under the expression
	 * @param expression
	 * @param references
	 * @throws Exception
	 */
	private static void get_references_in(SymbolNode expression, Collection<SymbolExpression> references) throws Exception {
		if(SymConditionUtils.is_reference(expression))
			references.add((SymbolExpression) expression);
		for(SymbolNode child : expression.get_children()) {
			SymConditionUtils.get_references_in(child, references);
		}
	}
	/**
	 * @param expression
	 * @return the set of references used in the expression structure
	 * @throws Exception
	 */
	private static Collection<SymbolExpression> get_references_in(SymbolExpression expression) throws Exception {
		Collection<SymbolExpression> references = new HashSet<SymbolExpression>();
		SymConditionUtils.get_references_in(expression, references);
		return references;
	}
	/**
	 * @param expression
	 * @param references
	 * @return whether the expression contains the usage of the references in the set
	 * @throws Exception
	 */
	private static boolean has_references_in(SymbolExpression expression, Collection<SymbolExpression> references) throws Exception {
		if(references.isEmpty())
			return false;
		else if(references.contains(expression)) {
			return true;
		}
		else {
			for(SymbolNode child : expression.get_children()) {
				if(child instanceof SymbolExpression) {
					SymbolExpression sub_expression = (SymbolExpression) child;
					if(SymConditionUtils.has_references_in(sub_expression, references))
						return true;
				}
			}
			return false;
		}
	}
	/**
	 * @param execution
	 * @param references
	 * @return whether the reference is defined (or limited) in the execution
	 * @throws Exception
	 */
	private static boolean has_reference_defined(CirExecution execution, Collection<SymbolExpression> references) throws Exception {
		CirStatement statement = execution.get_statement();
		if(references.isEmpty())
			return false;
		else if(statement instanceof CirAssignStatement) {
			if(references.contains(SymbolFactory.sym_expression(((CirAssignStatement) statement).get_lvalue()))) {
				return true;
			}
			else {
				return false;
			}
		}
		else if(statement instanceof CirIfStatement) {
			return SymConditionUtils.has_references_in(SymbolFactory.
					sym_expression(((CirIfStatement) statement).get_condition()), references);
		}
		else if(statement instanceof CirCaseStatement) {
			return SymConditionUtils.has_references_in(SymbolFactory.
					sym_expression(((CirCaseStatement) statement).get_condition()), references);
		}
		else {
			return false;
		}
	}
	
	/* SymConstraint --> SymCondition(*) */
	/**
	 * @param expression
	 * @return [execution, operator, times] or null if not coverage-constraint
	 * @throws Exception
	 */
	private static Object[] divide_stmt_constraint(SymbolExpression expression) throws Exception {
		if(expression == null)
			throw new IllegalArgumentException("Invalid expression: null");
		else if(expression instanceof SymbolBinaryExpression) {
			SymbolExpression loperand = ((SymbolBinaryExpression) expression).get_loperand();
			SymbolExpression roperand = ((SymbolBinaryExpression) expression).get_roperand();
			COperator operator = ((SymbolBinaryExpression) expression).get_operator().get_operator();
			if(loperand instanceof SymbolIdentifier && loperand.get_source() instanceof CirExecution) {
				SymbolConstant times = (SymbolConstant) roperand;
				return new Object[] { loperand.get_source(), operator, times.get_int() };
			}
			else if(roperand instanceof SymbolIdentifier && roperand.get_source() instanceof CirExecution) {
				SymbolConstant times = (SymbolConstant) loperand;
				switch(operator) {
				case greater_eq:	operator = COperator.smaller_eq;	break;
				case greater_tn:	operator = COperator.smaller_tn;	break;
				case smaller_eq:	operator = COperator.greater_eq;	break;
				case smaller_tn:	operator = COperator.greater_tn;	break;
				case equal_with:	operator = COperator.equal_with;	break;
				case not_equals:	operator = COperator.not_equals;	break;
				default: throw new IllegalArgumentException("Invalid operator: " + operator);
				}
				return new Object[] { roperand.get_source(), operator, times.get_int() };
			}
			else {
				return null;
			}
		}
		else
			return null;
	}
	/**
	 * @param expression
	 * @param expressions
	 * @throws Exception
	 */
	private static void divide_in_conjunctions(SymbolExpression expression, Collection<SymbolExpression> expressions) throws Exception {
		if(expression instanceof SymbolBinaryExpression) {
			SymbolExpression loperand = ((SymbolBinaryExpression) expression).get_loperand();
			COperator operator = ((SymbolBinaryExpression) expression).get_operator().get_operator();
			SymbolExpression roperand = ((SymbolBinaryExpression) expression).get_roperand();
			if(operator == COperator.logic_and) {
				SymConditionUtils.divide_in_conjunctions(loperand, expressions);
				SymConditionUtils.divide_in_conjunctions(roperand, expressions);
			}
			else {
				expressions.add(SymbolFactory.sym_condition(expression, true));
			}
		}
		else if(expression instanceof SymbolConstant) {
			if(((SymbolConstant) expression).get_bool()) {
				return;
			}
			else {
				expressions.add(SymbolFactory.sym_expression(Boolean.FALSE));
			}
		}
		else {
			expressions.add(SymbolFactory.sym_condition(expression, true));
		}
	}
	/**
	 * @param constraint
	 * @return translate the initial constraint into sub-conditions in its original execution point.
	 * @throws Exception
	 */
	private static Collection<SymCondition> parse_constraint_I(SymConstraint constraint) throws Exception {
		if(constraint == null)
			throw new IllegalArgumentException("Invalid constraint: null");
		else {
			/* 1. collect the sub-expressions in its conjunction */
			Set<SymbolExpression> expressions = new HashSet<SymbolExpression>();
			SymConditionUtils.divide_in_conjunctions(constraint.get_condition(), expressions);
			
			/* 2. translate the sub-expressions into initial conditions */
			CirExecution execution = constraint.get_execution();
			Set<SymCondition> conditions = new HashSet<SymCondition>();
			for(SymbolExpression expression : expressions) {
				Object[] exec_oprt_times = SymConditionUtils.divide_stmt_constraint(expression);
				if(exec_oprt_times != null) {
					CirExecution cov_execution = (CirExecution) exec_oprt_times[0];
					COperator cov_operator = (COperator) exec_oprt_times[1];
					int cov_times = ((Integer) exec_oprt_times[2]).intValue();
					conditions.add(SymCondition.cov_stmt(cov_execution, cov_operator, cov_times));
				}
				else {
					conditions.add(SymCondition.eva_expr(execution, expression, true));
				}
			}
			conditions.add(SymCondition.cov_stmt(execution, COperator.greater_eq, 1));
			
			/* 3. return initial set of conditions from constraint */	return conditions;
		}
	}
	/**
	 * @param condition eva_expr | cov_stmt
	 * @return the closest execution point where the condition can be evaluated
	 * @throws Exception
	 */
	private static CirExecution find_closest_prev_execution(SymCondition condition) throws Exception {
		/* A. evaluation */
		if(condition.get_operator() == SymOperator.eva_expr) {
			SymbolExpression expression = condition.get_parameter();
			CirExecution execution = condition.get_execution();
			Collection<SymbolExpression> references = SymConditionUtils.get_references_in(expression);
			
			/* constant expression */
			if(references.isEmpty()) {
				if(execution.get_graph().get_function().get_graph().get_main_function() != null) {
					return execution.get_graph().get_function().get_graph().get_main_function().get_flow_graph().get_entry();
				}
				else {
					return execution.get_graph().get_entry();
				}
			}
			/* variable expression */
			else {
				CirExecutionPath prev_path = CirExecutionPathFinder.finder.db_extend(execution);
				Iterator<CirExecutionEdge> iterator = prev_path.get_reverse_edges();
				while(iterator.hasNext()) {
					CirExecutionEdge edge = iterator.next();
					if(SymConditionUtils.has_reference_defined(edge.get_target(), references)) {
						return edge.get_target();
					}
				}
				return prev_path.get_source();
			}
		}
		/* B. coverage */
		else if(condition.get_operator() == SymOperator.cov_stmt) {
			CirExecutionPath prev_path = CirExecutionPathFinder.finder.db_extend(condition.get_execution());
			Iterator<CirExecutionEdge> iterator = prev_path.get_reverse_edges();
			while(iterator.hasNext()) {
				CirExecutionEdge edge = iterator.next();
				CirStatement statement = edge.get_source().get_statement();
				if(statement instanceof CirIfStatement || statement instanceof CirCaseStatement) {
					return edge.get_target();
				}
			}
			return prev_path.get_source();
		}
		/* C. invalid */
		else {
			throw new IllegalArgumentException("Invalid: " + condition.toString());
		}
	}
	/**
	 * @param input_conditions
	 * @return the set of conditions (constraint or coverage)
	 * @throws Exception
	 */
	private static Collection<SymCondition> parse_constraint_II(Collection<SymCondition> input_conditions) throws Exception {
		Collection<SymCondition> output_conditions = new HashSet<SymCondition>();
		for(SymCondition input_condition : input_conditions) {
			CirExecution prev_execution = SymConditionUtils.find_closest_prev_execution(input_condition);
			if(input_condition.get_operator() == SymOperator.eva_expr) {
				output_conditions.add(SymCondition.eva_expr(prev_execution, input_condition.get_parameter(), true));
			}
			else if(input_condition.get_operator() == SymOperator.cov_stmt) {
				Object[] exec_oprt_times = SymConditionUtils.divide_stmt_constraint(input_condition.get_parameter());
				COperator operator = (COperator) exec_oprt_times[1];
				int times = ((Integer) exec_oprt_times[2]).intValue();
				output_conditions.add(SymCondition.cov_stmt(prev_execution, operator, times));
			}
			else {
				throw new IllegalArgumentException("Invalid condition: " + input_condition);
			}
		}
		return output_conditions;
	}
	/**
	 * @param expression
	 * @param expressions
	 * @throws Exception
	 */
	private static void find_subsumed_expressions(SymbolExpression expression, Collection<SymbolExpression> expressions) throws Exception {
		if(expression instanceof SymbolBinaryExpression) {
			SymbolExpression loperand = ((SymbolBinaryExpression) expression).get_loperand();
			SymbolExpression roperand = ((SymbolBinaryExpression) expression).get_roperand();
			COperator operator = ((SymbolBinaryExpression) expression).get_operator().get_operator();
			
			/* a <= b: { a <= b } */
			if(operator == COperator.smaller_eq) {
				expressions.add(expression);
			}
			/* a < b: { a < b, a <= b, a != b, b != a } */
			else if(operator == COperator.smaller_tn) {
				expressions.add(expression);
				expressions.add(SymbolFactory.smaller_eq(loperand, roperand));
				expressions.add(SymbolFactory.not_equals(loperand, roperand));
				expressions.add(SymbolFactory.not_equals(roperand, loperand));
			}
			/* a >= b: { b <= a } */
			else if(operator == COperator.greater_eq) {
				expressions.add(SymbolFactory.smaller_eq(roperand, loperand));
			}
			/* a > b: { b < a, b <= a, b != a, a != b } */
			else if(operator == COperator.greater_tn) {
				expressions.add(SymbolFactory.smaller_tn(roperand, loperand));
				expressions.add(SymbolFactory.smaller_eq(roperand, loperand));
				expressions.add(SymbolFactory.not_equals(roperand, loperand));
				expressions.add(SymbolFactory.not_equals(loperand, roperand));
			}
			/* a == b: { a == b, b == a, a <= b, b <= a } */
			else if(operator == COperator.equal_with) {
				expressions.add(expression);
				expressions.add(SymbolFactory.equal_with(roperand, loperand));
				expressions.add(SymbolFactory.smaller_eq(loperand, roperand));
				expressions.add(SymbolFactory.smaller_eq(roperand, loperand));
			}
			/* a != b: { a != b, b != a } */
			else if(operator == COperator.not_equals) {
				expressions.add(expression);
				expressions.add(SymbolFactory.not_equals(roperand, loperand));
			}
			else {
				expressions.add(expression);
			}
		}
		else {
			expressions.add(expression);
		}
	}
	/**
	 * @param times
	 * @return the set of execution times smaller than times
	 * @throws Exception
	 */
	private static Collection<Integer> get_sample_times(int max_times) throws Exception {
		Collection<Integer> times = new ArrayList<Integer>();
		for(int k = 1; k <= max_times; k = k * 2) {
			times.add(Integer.valueOf(k));
		}
		times.add(Integer.valueOf(max_times));
		return times;
	}
	/**
	 * @param input_conditions
	 * @return the complete set of conditions subsumed by input conditions 
	 * @throws Exception
	 */
	private static Collection<SymCondition> parse_constraint_III(Collection<SymCondition> input_conditions) throws Exception {
		Collection<SymCondition> output_conditions = new HashSet<SymCondition>();
		Collection<SymbolExpression> expressions = new HashSet<SymbolExpression>();
		
		for(SymCondition input_condition : input_conditions) {
			if(input_condition.get_operator() == SymOperator.eva_expr) {
				expressions.clear();
				SymConditionUtils.find_subsumed_expressions(input_condition.get_parameter(), expressions);
				for(SymbolExpression expression : expressions) {
					output_conditions.add(SymCondition.eva_expr(input_condition.get_execution(), expression, true));
				}
			}
			else if(input_condition.get_operator() == SymOperator.cov_stmt) {
				CirExecution execution = input_condition.get_execution();
				Object[] exec_oprt_times = SymConditionUtils.divide_stmt_constraint(input_condition.get_parameter());
				COperator operator = (COperator) exec_oprt_times[1];
				int max_times = ((Integer) exec_oprt_times[2]).intValue();
				Collection<Integer> times = SymConditionUtils.get_sample_times(max_times);
				for(Integer time : times) {
					output_conditions.add(SymCondition.cov_stmt(execution, operator, time.intValue()));
				}
			}
			else {
				throw new IllegalArgumentException("Invalid condition: " + input_condition);
			}
		}
		
		return output_conditions;
	}
	/**
	 * @param constraint
	 * @return the set of symbolic conditions generated from input constraint
	 * @throws Exception
	 */
	private static Collection<SymCondition> conditions_of(SymConstraint constraint) throws Exception {
		return SymConditionUtils.parse_constraint_III(
				SymConditionUtils.parse_constraint_II(
						SymConditionUtils.parse_constraint_I(constraint)));
	}
	/* SymStateError --> SymCondition(*) */
	/**
	 * observation:trp_stmt(execution, statement, null)
	 * @param error
	 * @param conditions
	 * @throws Exception
	 */
	private static void gen_conditions_for(SymTrapError error, Collection<SymCondition> conditions) throws Exception {
		conditions.add(SymCondition.trp_stmt(error.get_execution()));
	}
	/**
	 * observation:mut_flow(source, orig_target, muta_target)
	 * observation:add_stmt(...)
	 * observation:del_stmt(...)
	 * @param error
	 * @param conditions
	 * @throws Exception
	 */
	private static void gen_conditions_for(SymFlowError error, Collection<SymCondition> conditions) throws Exception {
		CirExecutionFlow orig_flow = error.get_original_flow();
		CirExecutionFlow muta_flow = error.get_mutation_flow();
		
		conditions.add(SymCondition.mut_flow(orig_flow, muta_flow));
		
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
			conditions.add(SymCondition.add_stmt(execution));
		}
		for(CirExecution execution : del_executions) {
			conditions.add(SymCondition.del_stmt(execution));
		}
	}
	/**
	 * @param error
	 * @param conditions
	 * @throws Exception
	 */
	private static void gen_conditions_for(SymValueError error, Collection<SymCondition> conditions) throws Exception {
		/* declarations */
		CirExecution execution = error.get_execution();
		CirExpression location = error.get_expression();
		SymbolExpression orig_value = error.get_original_value();
		SymbolExpression muta_value = error.get_mutation_value();
		
		/* initialization */
		orig_value = SymConditionUtils.evaluate_on(orig_value, null);
		try {
			muta_value = SymConditionUtils.evaluate_on(muta_value, null);
		}
		catch(ArithmeticException ex) {
			conditions.add(SymCondition.trp_stmt(execution));
			return;
		}
		
		/* BOOL */
		if(SymConditionUtils.is_boolean(location)) {
			orig_value = SymbolFactory.sym_condition(orig_value, true);
			muta_value = SymbolFactory.sym_condition(muta_value, true);
			orig_value = SymConditionUtils.evaluate_on(orig_value, null);
			muta_value = SymConditionUtils.evaluate_on(muta_value, null);
			
			if(muta_value instanceof SymbolConstant) {
				if(((SymbolConstant) muta_value).get_bool().booleanValue())
					conditions.add(SymCondition.set_true(location));
				else
					conditions.add(SymCondition.set_fals(location));
			}
			else {
				conditions.add(SymCondition.set_bool(location, muta_value));
			}
		}
		/* NUMB */
		else if(SymConditionUtils.is_numeric(location)) {
			conditions.add(SymCondition.set_numb(location, muta_value));
			
			if(muta_value instanceof SymbolConstant) {
				Object number = ((SymbolConstant) muta_value).get_number();
				if(number instanceof Long) {
					if(((Long) number).longValue() > 0) {
						conditions.add(SymCondition.set_post(location));
					}
					else if(((Long) number).longValue() < 0) {
						conditions.add(SymCondition.set_negt(location));
					}
					else {
						conditions.add(SymCondition.set_zero(location));
					}
				}
				else {
					if(((Double) number).doubleValue() > 0) {
						conditions.add(SymCondition.set_post(location));
					}
					else if(((Double) number).doubleValue() < 0) {
						conditions.add(SymCondition.set_negt(location));
					}
					else {
						conditions.add(SymCondition.set_zero(location));
					}
				}
			}
			
			SymbolExpression diff = SymbolFactory.arith_sub(location.get_data_type(), orig_value, muta_value);
			diff = SymConditionUtils.evaluate_on(diff, null);
			if(diff instanceof SymbolConstant) {
				Object number = ((SymbolConstant) diff).get_number();
				if(number instanceof Long) {
					if(((Long) number).longValue() > 0) {
						conditions.add(SymCondition.inc_scop(location));
					}
					else if(((Long) number).longValue() < 0) {
						conditions.add(SymCondition.dec_scop(location));
					}
				}
				else if(number instanceof Double) {
					if(((Double) number).doubleValue() > 0) {
						conditions.add(SymCondition.inc_scop(location));
					}
					else if(((Double) number).doubleValue() < 0) {
						conditions.add(SymCondition.dec_scop(location));
					}
				}
			}
			
			if(orig_value instanceof SymbolConstant) {
				Object x = ((SymbolConstant) orig_value).get_number();
				if(muta_value instanceof SymbolConstant) {
					Object y = ((SymbolConstant) muta_value).get_number();
					if(x instanceof Long) {
						if(y instanceof Long) {
							if(Math.abs(((Long) x).doubleValue()) > Math.abs(((Long) y).doubleValue())) {
								conditions.add(SymCondition.ext_scop(location));
							}
							else {
								conditions.add(SymCondition.shk_scop(location));
							}
						}
						else {
							if(Math.abs(((Long) x).doubleValue()) > Math.abs(((Double) y).doubleValue())) {
								conditions.add(SymCondition.ext_scop(location));
							}
							else {
								conditions.add(SymCondition.shk_scop(location));
							}
						}
					}
					else {
						if(y instanceof Long) {
							if(Math.abs(((Double) x).doubleValue()) > Math.abs(((Long) y).doubleValue())) {
								conditions.add(SymCondition.ext_scop(location));
							}
							else {
								conditions.add(SymCondition.shk_scop(location));
							}
						}
						else {
							if(Math.abs(((Double) x).doubleValue()) > Math.abs(((Double) y).doubleValue())) {
								conditions.add(SymCondition.ext_scop(location));
							}
							else {
								conditions.add(SymCondition.shk_scop(location));
							}
						}
					}
				}
			}
		}
		else if(SymConditionUtils.is_address(location)) {
			conditions.add(SymCondition.set_addr(location, muta_value));
			
			if(muta_value instanceof SymbolConstant) {
				if(((SymbolConstant) muta_value).get_long().longValue() == 0) {
					conditions.add(SymCondition.set_null(location));
				}
				else {
					conditions.add(SymCondition.set_invp(location));
				}
			}
			else {
				conditions.add(SymCondition.set_invp(location));
			}
			
			SymbolExpression diff = SymbolFactory.arith_sub(location.get_data_type(), orig_value, muta_value);
			diff = SymConditionUtils.evaluate_on(diff, null);
			if(diff instanceof SymbolConstant) {
				Object number = ((SymbolConstant) diff).get_number();
				if(number instanceof Long) {
					if(((Long) number).longValue() > 0) {
						conditions.add(SymCondition.inc_scop(location));
					}
					else if(((Long) number).longValue() < 0) {
						conditions.add(SymCondition.dec_scop(location));
					}
				}
				else if(number instanceof Double) {
					if(((Double) number).doubleValue() > 0) {
						conditions.add(SymCondition.inc_scop(location));
					}
					else if(((Double) number).doubleValue() < 0) {
						conditions.add(SymCondition.dec_scop(location));
					}
				}
			}
		}
		else {
			conditions.add(SymCondition.set_auto(location, muta_value));
		}
		
		/* summarization */
		if(muta_value.equals(orig_value)) {
			conditions.clear();
		}
		else {
			if(error instanceof SymExpressionError) {
				conditions.add(SymCondition.mut_expr(location));
			}
			else if(error instanceof SymReferenceError) {
				conditions.add(SymCondition.mut_refr(location));
			}
			else {
				conditions.add(SymCondition.mut_stat(location));
			}
		}
	}
	private static Collection<SymCondition> conditions_of(SymStateError error) throws Exception {
		Collection<SymCondition> conditions = new HashSet<SymCondition>();
		if(error instanceof SymFlowError) {
			SymConditionUtils.gen_conditions_for((SymFlowError) error, conditions);
		}
		else if(error instanceof SymTrapError) {
			SymConditionUtils.gen_conditions_for((SymTrapError) error, conditions);
		}
		else {
			SymConditionUtils.gen_conditions_for((SymValueError) error, conditions);
		}
		return conditions;
	}
	/* SymInstance --> SymCondition[*] */
	/**
	 * @param instance
	 * @param contexts
	 * @return
	 * @throws Exception
	 */
	public static Collection<SymCondition> sym_conditions(SymInstance instance, SymbolStateContexts contexts) throws Exception {
		instance = SymInstanceUtils.optimize(instance, contexts);
		if(instance instanceof SymConstraint) {
			return SymConditionUtils.conditions_of((SymConstraint) instance);
		}
		else {
			return SymConditionUtils.conditions_of((SymStateError) instance);
		}
	}
	/**
	 * @param instance
	 * @return
	 * @throws Exception
	 */
	public static Collection<SymCondition> sym_conditions(SymInstance instance) throws Exception {
		return SymConditionUtils.sym_conditions(instance, null);
	}
	
	
	
	
	
	
}
