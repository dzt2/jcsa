package com.jcsa.jcmutest.mutant.cir2mutant.cerr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.jcsa.jcmutest.mutant.cir2mutant.CirMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirAddressOfPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirArgumentListPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirArithAddPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirArithDivPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirArithModPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirArithMulPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirArithNegPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirArithSubPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirAssignPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirBitwsAndPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirBitwsIorPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirBitwsLshPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirBitwsRshPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirBitwsRsvPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirBitwsXorPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirDereferencePropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirEqualWithPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirErrorPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirFieldOfPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirGreaterEqPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirGreaterTnPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirInitializerPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirLogicAndPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirLogicIorPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirLogicNotPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirNotEqualsPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirSmallerEqPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirSmallerTnPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirTypeCastPropagator;
import com.jcsa.jcmutest.mutant.cir2mutant.gate.CirWaitValuePropagator;
import com.jcsa.jcparse.flwa.symbol.SymEvaluator;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
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
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionEdge;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionPath;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionPathFinder;
import com.jcsa.jcparse.lang.irlang.stmt.CirArgumentList;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.sym.SymBinaryExpression;
import com.jcsa.jcparse.lang.sym.SymConstant;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;
import com.jcsa.jcparse.lang.sym.SymIdentifier;
import com.jcsa.jcparse.lang.sym.SymNode;


/**
 * It implements the interface for optimizing or proceeding symbolic instance evaluated during testing.
 * 
 * @author yukimula
 *
 */
public class SymInstanceUtils {
	
	/* singleton mode */
	/** mapping from expression operator to the propagator for generating state error **/
	private Map<COperator, CirErrorPropagator> propagators;
	/** private constructor for producing singleton of utilities **/
	private SymInstanceUtils() { 
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
	/** the singleton of symbolic instance utility for algorithms **/
	private static final SymInstanceUtils utils = new SymInstanceUtils();
	
	/* symbolic constraint optimization */
	/**
	 * @param expression
	 * @return whether the expression is logical AND
	 */
	private boolean is_conjunction(SymExpression expression) {
		if(expression instanceof SymBinaryExpression) {
			return ((SymBinaryExpression) expression).get_operator().get_operator() == COperator.logic_and;
		}
		else {
			return false;
		}
	}
	/**
	 * collect the conditions in the conjunction
	 * @param expression
	 * @param conditions
	 * @throws Exception
	 */
	private boolean collect_conditions_in(SymExpression expression, Collection<SymExpression> conditions) throws Exception {
		if(expression instanceof SymConstant) {
			if(((SymConstant) expression).get_bool()) {
				return true;
			}
			else {
				conditions.clear();
				conditions.add(SymFactory.sym_constant(Boolean.FALSE));
				return false;
			}
		}
		else if(this.is_conjunction(expression)) {
			if(this.collect_conditions_in(((SymBinaryExpression) expression).get_loperand(), conditions)) {
				return this.collect_conditions_in(((SymBinaryExpression) expression).get_roperand(), conditions);
			}
			else {
				return false;
			}
		}
		else {
			conditions.add(SymFactory.sym_condition(expression, true));
			return true;
		}
	}
	/**
	 * @param cir_mutations
	 * @param constraint
	 * @return the set of constraints as elemental conditions in the constraint of source
	 * @throws Exception
	 */
	private Collection<SymConstraint> divide_in_constraints(CirMutations cir_mutations, SymConstraint constraint) throws Exception {
		if(cir_mutations == null)
			throw new IllegalArgumentException("Invalid cir_mutations: null");
		else if(constraint == null)
			throw new IllegalArgumentException("Invalid constraint as null");
		else {
			/* 1. collect the conditions in the conjunction of expression */
			Set<SymExpression> conditions = new HashSet<SymExpression>();
			this.collect_conditions_in(constraint.get_condition(), conditions);
			
			/* 2. generate the set of symbolic constraints w.r.t. the conditions */
			Set<SymConstraint> constraints = new HashSet<SymConstraint>();
			for(SymExpression condition : conditions) {
				constraints.add(cir_mutations.expression_constraint(
						constraint.get_statement(), condition, true));
			}
			constraints.add(cir_mutations.statement_constraint(constraint.get_statement(), 1));
			return constraints;
		}
	}
	/**
	 * @param condition
	 * @param statement
	 * @return whether the symbolic expression in condition is defined in the statement
	 * @throws Exception
	 */
	private boolean is_defined(SymExpression condition, CirStatement statement) throws Exception {
		if(statement instanceof CirAssignStatement) {
			SymExpression definition = SymFactory.sym_expression(
					((CirAssignStatement) statement).get_lvalue());
			
			Queue<SymNode> queue = new LinkedList<SymNode>();
			queue.add(condition); SymNode sym_node;
			while(!queue.isEmpty()) {
				sym_node = queue.poll();
				for(SymNode child : sym_node.get_children()) {
					queue.add(child);
				}
				if(sym_node.equals(definition)) {
					return true;
				}
			}
			
			return false;
		}
		else {
			return false;
		}
	}
	/**
	 * whether the condition is in form of statement_ID >= times(integer_Constant)
	 * @param condition
	 * @return the times required for execution the target statement or -1 if it is not
	 * @throws Exception
	 */
	private boolean is_statement_condition(SymExpression condition) throws Exception {
		if(condition instanceof SymBinaryExpression) {
			SymExpression loperand = ((SymBinaryExpression) condition).get_loperand();
			SymExpression roperand = ((SymBinaryExpression) condition).get_roperand();
			if(loperand instanceof SymIdentifier && loperand.get_source() instanceof CirExecution) {
				return true;
			}
			else if(roperand instanceof SymIdentifier && roperand.get_source() instanceof CirExecution) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}
	/**
	 * @param condition
	 * @return the execution to which the operand in condition refers to or null if it is not
	 * @throws Exception
	 */
	private CirExecution execution_in_condition(SymExpression condition) throws Exception {
		if(condition instanceof SymBinaryExpression) {
			SymExpression loperand = ((SymBinaryExpression) condition).get_loperand();
			SymExpression roperand = ((SymBinaryExpression) condition).get_roperand();
			if(loperand instanceof SymIdentifier && loperand.get_source() instanceof CirExecution) {
				return (CirExecution) loperand.get_source();
			}
			else if(roperand instanceof SymIdentifier && roperand.get_source() instanceof CirExecution) {
				return (CirExecution) roperand.get_source();
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
	 * @param condition
	 * @return the times that the execution in condition is required
	 * @throws Exception
	 */
	private int int_times_of_condition(SymExpression condition) throws Exception {
		if(condition instanceof SymBinaryExpression) {
			SymExpression loperand = ((SymBinaryExpression) condition).get_loperand();
			SymExpression roperand = ((SymBinaryExpression) condition).get_roperand();
			if(loperand instanceof SymIdentifier && loperand.get_source() instanceof CirExecution) {
				return ((SymConstant) roperand).get_int();
			}
			else if(roperand instanceof SymIdentifier && roperand.get_source() instanceof CirExecution) {
				return ((SymConstant) loperand).get_int();
			}
			else {
				return -1;
			}
		}
		else {
			return -1;
		}
	}
	/**
	 * @param execution
	 * @param condition
	 * @return the statement point where the condition can reach to prior
	 * @throws Exception
	 */
	private CirExecution prev_reach_point(CirExecution execution, SymExpression condition) throws Exception {
		if(condition instanceof SymConstant) {
			return execution.get_graph().get_entry();
		}
		else if(this.is_statement_condition(condition)) {
			return this.execution_in_condition(condition);
		}
		else {
			CirExecutionPath path = CirExecutionPathFinder.finder.db_extend(execution);
			Iterator<CirExecutionEdge> iterator = path.get_reverse_edges();
			while(iterator.hasNext()) {
				CirExecutionEdge edge = iterator.next();
				CirExecution prev_node = edge.get_source();
				if(this.is_defined(condition, prev_node.get_statement())) {
					return edge.get_target();
				}
			}
			return path.get_source();
		}
	}
	/**
	 * @param cir_mutations
	 * @param constraint
	 * @return the constraint is improved to the statement where the condition can be evaluated earliest.
	 * @throws Exception
	 */
	private SymConstraint path_improvement(CirMutations cir_mutations, SymConstraint constraint) throws Exception {
		if(cir_mutations == null)
			throw new IllegalArgumentException("Invalid cir_mutations as null");
		else if(constraint == null)
			throw new IllegalArgumentException("Invalid constraint as null");
		else {
			CirExecution execution = this.prev_reach_point(
					constraint.get_execution(), constraint.get_condition());
			return cir_mutations.expression_constraint(execution.
					get_statement(), constraint.get_condition(), true);
		}
	}
	/**
	 * generate the set of conditions subsuming the expression
	 * @param expression
	 * @param conditions
	 * @throws Exception
	 */
	private void generate_subsume_set(SymExpression expression, Collection<SymExpression> conditions) throws Exception {
		if(expression instanceof SymBinaryExpression) {
			SymBinaryExpression bin_expression = (SymBinaryExpression) expression;
			COperator operator = ((SymBinaryExpression) expression).get_operator().get_operator();
			switch(operator) {
			case smaller_tn:
			{
				conditions.add(bin_expression);
				conditions.add(SymFactory.smaller_eq(bin_expression.get_loperand(), bin_expression.get_roperand()));
				conditions.add(SymFactory.not_equals(bin_expression.get_loperand(), bin_expression.get_roperand()));
				break;
			}
			case smaller_eq:
			{
				conditions.add(bin_expression);
				break;
			}
			case greater_tn:
			{
				conditions.add(SymFactory.smaller_tn(bin_expression.get_roperand(), bin_expression.get_loperand()));
				conditions.add(SymFactory.smaller_eq(bin_expression.get_roperand(), bin_expression.get_loperand()));
				conditions.add(SymFactory.not_equals(bin_expression.get_roperand(), bin_expression.get_loperand()));
				break;
			}
			case greater_eq:
			{
				conditions.add(SymFactory.smaller_eq(bin_expression.get_roperand(), bin_expression.get_loperand()));
				break;
			}
			case equal_with:
			{
				conditions.add(bin_expression);
				conditions.add(SymFactory.smaller_eq(bin_expression.get_loperand(), bin_expression.get_roperand()));
				conditions.add(SymFactory.smaller_eq(bin_expression.get_roperand(), bin_expression.get_loperand()));
				break;
			}
			case not_equals:
			{
				conditions.add(bin_expression);
				conditions.add(SymFactory.not_equals(bin_expression.get_roperand(), bin_expression.get_loperand()));
				conditions.add(SymFactory.smaller_tn(bin_expression.get_loperand(), bin_expression.get_roperand()));
				conditions.add(SymFactory.smaller_tn(bin_expression.get_roperand(), bin_expression.get_loperand()));
				break;
			}
			default:
			{
				conditions.add(expression);
				break;
			}
			}
		}
		else {
			conditions.add(expression);
		}
	}
	/**
	 * @param cir_mutations
	 * @param constraint
	 * @return the set of constraints subsumed from the original conditions (extended)
	 * @throws Exception
	 */
	private Collection<SymConstraint> subsume_constraints(CirMutations cir_mutations, SymConstraint constraint) throws Exception {
		if(cir_mutations == null)
			throw new IllegalArgumentException("Invalid cir_mutations as null");
		else if(constraint == null)
			throw new IllegalArgumentException("Invalid constraint as null");
		else {
			Set<SymConstraint> constraints = new HashSet<SymConstraint>();
			Set<SymExpression> conditions = new HashSet<SymExpression>();
			this.generate_subsume_set(constraint.get_condition(), conditions);
			for(SymExpression condition : conditions) {
				constraints.add(cir_mutations.expression_constraint(constraint.get_statement(), condition, true));
			}
			return constraints;
		}
	}
	/**
	 * @param cir_mutations
	 * @param constraint
	 * @return the set of symbolic constraints improved from the given one
	 * @throws Exception
	 */
	public static Collection<SymConstraint> improve_constraints(CirMutations cir_mutations, SymConstraint constraint) throws Exception {
		Collection<SymConstraint> divide_constraints = utils.divide_in_constraints(cir_mutations, constraint);
		Collection<SymConstraint> improv_constraints = new HashSet<SymConstraint>();
		for(SymConstraint divide_constraint : divide_constraints) 
			improv_constraints.add(utils.path_improvement(cir_mutations, divide_constraint));
		Collection<SymConstraint> subsum_constraints = new HashSet<SymConstraint>();
		for(SymConstraint improv_constraint : improv_constraints) 
			subsum_constraints.addAll(utils.subsume_constraints(cir_mutations, improv_constraint));
		return subsum_constraints;
	}
	
	/* annotation methods as supporting */
	/**
	 * @param expression
	 * @return true if the expression is logical or used as condition of IF-statement
	 * @throws Exception
	 */
	private boolean is_boolean(CirExpression expression) throws Exception {
		CType type = expression.get_data_type();
		if(type == null) {
			return false;
		}
		else {
			type = CTypeAnalyzer.get_value_type(type);
			if(CTypeAnalyzer.is_boolean(type)) {
				return true;
			}
			else {
				CirNode parent = expression.get_parent();
				if(parent instanceof CirIfStatement) {
					return ((CirIfStatement) parent).get_condition() == expression;
				}
				else if(parent instanceof CirCaseStatement) {
					return ((CirCaseStatement) parent).get_condition() == expression;
				}
				else {
					return false;
				}
			}
		}
	}
	/**
	 * @param expression
	 * @return true if the expression is integer or double, and used as operand in arithmetic, bitwise expressions
	 * @throws Exception
	 */
	private boolean is_numeric(CirExpression expression) throws Exception {
		CType type = expression.get_data_type();
		if(type == null)
			return false;
		else
			return CTypeAnalyzer.is_number(CTypeAnalyzer.get_value_type(type));
	}
	/**
	 * @param expression
	 * @return true if the expression is a pointer and used in arithmetic or parameter expressions
	 * @throws Exception
	 */
	private boolean is_address(CirExpression expression) throws Exception {
		CType type = expression.get_data_type();
		if(type == null)
			return false;
		else
			return CTypeAnalyzer.is_pointer(CTypeAnalyzer.get_value_type(type));
	}
	/**
	 * generate the annotation for an expression in boolean context
	 * @param expression
	 * @param orig_value
	 * @param muta_value
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_boolean_expression(CirExpression expression, 
			SymExpression orig_value, SymExpression muta_value, 
			Collection<CirAnnotation> annotations) throws Exception {
		annotations.add(new CirAnnotation(CirAnnotateType.chg_bool, expression, null));
		if(muta_value instanceof SymConstant) {
			if(((SymConstant) muta_value).get_bool())
				annotations.add(new CirAnnotation(CirAnnotateType.set_true, expression, null));
			else
				annotations.add(new CirAnnotation(CirAnnotateType.set_false, expression, null));
		}
		annotations.add(new CirAnnotation(CirAnnotateType.set_bool, expression, muta_value));
	}
	/**
	 * generate the annotation for an expression in numeric context
	 * @param expression
	 * @param orig_value
	 * @param muta_value
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_numeric_expression(CirExpression expression, 
			SymExpression orig_value, SymExpression muta_value, 
			Collection<CirAnnotation> annotations) throws Exception {
		annotations.add(new CirAnnotation(CirAnnotateType.chg_numb, expression, null));
		annotations.add(new CirAnnotation(CirAnnotateType.set_numb, expression, muta_value));
		
		/* value domain property */
		if(muta_value instanceof SymConstant) {
			Object number = ((SymConstant) muta_value).get_number();
			if(number instanceof Long) {
				long value = ((Long) number).longValue();
				if(value > 0) {
					annotations.add(new CirAnnotation(CirAnnotateType.set_post, expression, null));
				}
				else if(value < 0) {
					annotations.add(new CirAnnotation(CirAnnotateType.set_negt, expression, null));
				}
				else {
					annotations.add(new CirAnnotation(CirAnnotateType.set_zero, expression, null));
				}
			}
			else {
				double value = ((Double) number).doubleValue();
				if(value > 0) {
					annotations.add(new CirAnnotation(CirAnnotateType.set_post, expression, null));
				}
				else if(value < 0) {
					annotations.add(new CirAnnotation(CirAnnotateType.set_negt, expression, null));
				}
				else {
					annotations.add(new CirAnnotation(CirAnnotateType.set_zero, expression, null));
				}
			}
		}
		
		/* difference property */
		SymExpression difference = SymFactory.arith_sub(expression.get_data_type(), muta_value, orig_value);
		difference = SymEvaluator.evaluate_on(difference, null);
		if(difference instanceof SymConstant) {
			Object number = ((SymConstant) difference).get_number();
			if(number instanceof Long) {
				long value = ((Long) number).longValue();
				if(value > 0) {
					annotations.add(new CirAnnotation(CirAnnotateType.inc_value, expression, null));
				}
				else if(value < 0) {
					annotations.add(new CirAnnotation(CirAnnotateType.dec_value, expression, null));
				}
			}
			else {
				double value = ((Double) number).doubleValue();
				if(value > 0) {
					annotations.add(new CirAnnotation(CirAnnotateType.inc_value, expression, null));
				}
				else if(value < 0) {
					annotations.add(new CirAnnotation(CirAnnotateType.dec_value, expression, null));
				}
			}
		}
		
		/* value range property */
		orig_value = SymEvaluator.evaluate_on(orig_value, null);
		muta_value = SymEvaluator.evaluate_on(muta_value, null);
		if(orig_value instanceof SymConstant) {
			Object lnumber = ((SymConstant) orig_value).get_number();
			if(muta_value instanceof SymConstant) {
				Object rnumber = ((SymConstant) muta_value).get_number();
				if(lnumber instanceof Long) {
					long x = ((Long) lnumber).longValue();
					if(rnumber instanceof Long) {
						long y = ((Long) rnumber).longValue();
						if(Math.abs(y) > Math.abs(x)) {
							annotations.add(new CirAnnotation(CirAnnotateType.ext_value, expression, null));
						}
						else if(Math.abs(y) < Math.abs(x)) {
							annotations.add(new CirAnnotation(CirAnnotateType.shk_value, expression, null));
						}
					}
					else {
						double y = ((Double) rnumber).doubleValue();
						if(Math.abs(y) > Math.abs(x)) {
							annotations.add(new CirAnnotation(CirAnnotateType.ext_value, expression, null));
						}
						else if(Math.abs(y) < Math.abs(x)) {
							annotations.add(new CirAnnotation(CirAnnotateType.shk_value, expression, null));
						}
					}
				}
				else {
					double x = ((Double) lnumber).doubleValue();
					if(rnumber instanceof Long) {
						long y = ((Long) rnumber).longValue();
						if(Math.abs(y) > Math.abs(x)) {
							annotations.add(new CirAnnotation(CirAnnotateType.ext_value, expression, null));
						}
						else if(Math.abs(y) < Math.abs(x)) {
							annotations.add(new CirAnnotation(CirAnnotateType.shk_value, expression, null));
						}
					}
					else {
						double y = ((Double) rnumber).doubleValue();
						if(Math.abs(y) > Math.abs(x)) {
							annotations.add(new CirAnnotation(CirAnnotateType.ext_value, expression, null));
						}
						else if(Math.abs(y) < Math.abs(x)) {
							annotations.add(new CirAnnotation(CirAnnotateType.shk_value, expression, null));
						}
					}
				}
			}
		}
	}
	/**
	 * generate the annotation for an expression in address context
	 * @param expression
	 * @param orig_value
	 * @param muta_value
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_address_expression(CirExpression expression, 
			SymExpression orig_value, SymExpression muta_value, 
			Collection<CirAnnotation> annotations) throws Exception {
		annotations.add(new CirAnnotation(CirAnnotateType.chg_addr, expression, null));
		annotations.add(new CirAnnotation(CirAnnotateType.set_addr, expression, muta_value));
		
		/* value domain property */
		if(muta_value instanceof SymConstant) {
			long value = ((SymConstant) muta_value).get_long();
			if(value == 0) {
				annotations.add(new CirAnnotation(CirAnnotateType.set_null, expression, null));
			}
			else {
				annotations.add(new CirAnnotation(CirAnnotateType.set_invp, expression, null));
			}
		}
		
		/* difference property */
		SymExpression difference = SymFactory.arith_sub(expression.get_data_type(), muta_value, orig_value);
		difference = SymEvaluator.evaluate_on(difference, null);
		if(difference instanceof SymConstant) {
			Object number = ((SymConstant) difference).get_number();
			long value = ((Long) number).longValue();
			if(value > 0) {
				annotations.add(new CirAnnotation(CirAnnotateType.inc_value, expression, null));
			}
			else if(value < 0) {
				annotations.add(new CirAnnotation(CirAnnotateType.dec_value, expression, null));
			}
		}
	}
	/**
	 * generate annotations for expression value error
	 * @param expression
	 * @param orig_value
	 * @param muta_value
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_expression(CirExpression expression, 
			SymExpression orig_value, SymExpression muta_value, 
			Collection<CirAnnotation> annotations) throws Exception {
		if(orig_value.equals(muta_value)) {
			annotations.clear();
			return;
		}
		else if(this.is_boolean(expression)) {
			this.generate_annotations_in_boolean_expression(expression, orig_value, muta_value, annotations);
		}
		else if(this.is_numeric(expression)) {
			this.generate_annotations_in_numeric_expression(expression, orig_value, muta_value, annotations);
		}
		else if(this.is_address(expression)) {
			this.generate_annotations_in_address_expression(expression, orig_value, muta_value, annotations);
		}
		else {
			annotations.add(new CirAnnotation(CirAnnotateType.chg_auto, expression, null));
			annotations.add(new CirAnnotation(CirAnnotateType.set_auto, expression, muta_value));
		}
	}
	
	/* symbolic annotation generator */
	/**
	 * @param constraint
	 * @return	Translation rules are:
	 * 			|-- stmt_id >= times 	--> covr_stmt(statement, times)
	 * 			|--	(execution, TRUE)	-->	covr_stmt(statement, 1)
	 * 			|--	(execution, other)	--> eval_stmt(statement, expression)
	 * @throws Exception
	 */
	private void annotate_constraint(SymConstraint constraint, 
			Collection<CirAnnotation> annotations) throws Exception {
		if(constraint == null)
			throw new IllegalArgumentException("Invalid constraint as null");
		else {
			CirExecution execution = constraint.get_execution();
			SymExpression condition = constraint.get_condition();
			
			/* 	*********************************************************
			 * 	|-- stmt_id >= times 	--> covr_stmt(statement, times)	|
			 * 	|--	(execution, TRUE)	-->	covr_stmt(statement, 1) 	|
			 * 	*********************************************************/
			int times;
			if(condition instanceof SymConstant) {
				if(((SymConstant) condition).get_bool()) {
					times = 1;
				}
				else {
					times = -1;
				}
			}
			else {
				times = this.int_times_of_condition(condition);
			}
			if(times > 0) {
				annotations.add(new CirAnnotation(CirAnnotateType.covr_stmt, 
						execution.get_statement(), 
						SymFactory.sym_constant(Integer.valueOf(times))));
			}
			/* |--	(execution, other)	--> eval_stmt(statement, expression) */
			else {
				annotations.add(new CirAnnotation(CirAnnotateType.eval_stmt,
						execution.get_statement(), condition));
			}
		}
	}
	/**
	 * 	trap_error	|--	trap_stmt(statement, null)
	 * 	@param state_error
	 * 	@param annotations
	 * 	@throws Exception
	 */
	private void annotate_trap_error(SymTrapError state_error, Collection<CirAnnotation> annotations) throws Exception {
		annotations.add(new CirAnnotation(CirAnnotateType.trap_stmt, state_error.get_statement(), null));
	}
	/**
	 * flow_error |-- add_stmt(statement, null) + del_stmt(statement, null)
	 * @param state_error
	 * @param annotations
	 * @throws Exception
	 */
	private void annotate_flow_error(SymFlowError state_error, Collection<CirAnnotation> annotations) throws Exception {
		/* 1. get statements that should be executed in original program */
		Set<CirStatement> del_statements = new HashSet<CirStatement>();
		CirExecutionPath orig_path = CirExecutionPathFinder.finder.df_extend(state_error.get_original_flow().get_target());
		for(CirExecutionEdge edge : orig_path.get_edges()) { del_statements.add(edge.get_source().get_statement()); }
		del_statements.add(orig_path.get_target().get_statement());
		
		/* 2. get statements that should be executed in mutated program */
		Set<CirStatement> add_statements = new HashSet<CirStatement>();
		CirExecutionPath muta_path = CirExecutionPathFinder.finder.df_extend(state_error.get_mutation_flow().get_target());
		for(CirExecutionEdge edge : muta_path.get_edges()) { add_statements.add(edge.get_source().get_statement()); }
		add_statements.add(muta_path.get_target().get_statement());
		
		/* 3. remove the common part between original and mutation path */
		Set<CirStatement> common_statements = new HashSet<CirStatement>();
		for(CirStatement statement : del_statements) {
			if(add_statements.contains(statement)) {
				common_statements.add(statement);
			}
		}
		add_statements.removeAll(common_statements);
		del_statements.removeAll(common_statements);
		
		/* 4. append annotations for flow error */
		for(CirStatement statement : add_statements) 
			annotations.add(new CirAnnotation(CirAnnotateType.add_stmt, statement, null));
		for(CirStatement statement : del_statements) 
			annotations.add(new CirAnnotation(CirAnnotateType.del_stmt, statement, null));
	}
	/**
	 * @param state_error
	 * @param annotations
	 * @throws Exception
	 */
	private void annotate_expr_error(SymExpressionError state_error, Collection<CirAnnotation> annotations) throws Exception {
		annotations.add(new CirAnnotation(CirAnnotateType.mut_value, state_error.get_expression(), null));
		this.generate_annotations_in_expression(state_error.get_expression(), 
				state_error.get_original_value(), state_error.get_mutation_value(), annotations);
	}
	/**
	 * @param state_error
	 * @param annotations
	 * @throws Exception
	 */
	private void annotate_refr_error(SymReferenceError state_error, Collection<CirAnnotation> annotations) throws Exception {
		annotations.add(new CirAnnotation(CirAnnotateType.mut_refer, state_error.get_expression(), null));
		this.generate_annotations_in_expression(state_error.get_expression(), 
				state_error.get_original_value(), state_error.get_mutation_value(), annotations);
	}
	/**
	 * @param state_error
	 * @param annotations
	 * @throws Exception
	 */
	private void annotate_stat_error(SymStateValueError state_error, Collection<CirAnnotation> annotations) throws Exception {
		annotations.add(new CirAnnotation(CirAnnotateType.mut_state, state_error.get_expression(), null));
		this.generate_annotations_in_expression(state_error.get_expression(), 
				state_error.get_original_value(), state_error.get_mutation_value(), annotations);
	}
	/**
	 * @param instance
	 * @return the set of annotations to describe the symbolic instance
	 * @throws Exception
	 */
	public static Collection<CirAnnotation> annotations(SymInstance instance) throws Exception {
		Set<CirAnnotation> annotations = new HashSet<CirAnnotation>();
		if(instance instanceof SymConstraint)
			utils.annotate_constraint((SymConstraint) instance, annotations);
		else if(instance instanceof SymTrapError)
			utils.annotate_trap_error((SymTrapError) instance, annotations);
		else if(instance instanceof SymFlowError)
			utils.annotate_flow_error((SymFlowError) instance, annotations);
		else if(instance instanceof SymExpressionError)
			utils.annotate_expr_error((SymExpressionError) instance, annotations);
		else if(instance instanceof SymReferenceError)
			utils.annotate_refr_error((SymReferenceError) instance, annotations);
		else if(instance instanceof SymStateValueError)
			utils.annotate_stat_error((SymStateValueError) instance, annotations);
		else
			throw new IllegalArgumentException("Invalid instance as: " + instance);
		return annotations;
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
	private void propagate_on(CirMutations cir_mutations, SymStateError source_error, 
				Map<SymStateError, SymConstraint> propagations) throws Exception {
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
				this.propagators.get(COperator.dereference).propagate(cir_mutations, source_error, location, parent, propagations);
			}
			else if(parent instanceof CirFieldExpression) {
				this.propagators.get(COperator.arith_add_assign).propagate(cir_mutations, source_error, location, parent, propagations);
			}
			else if(parent instanceof CirAddressExpression) {
				this.propagators.get(COperator.address_of).propagate(cir_mutations, source_error, location, parent, propagations);
			}
			else if(parent instanceof CirCastExpression) {
				this.propagators.get(COperator.arith_sub_assign).propagate(cir_mutations, source_error, location, parent, propagations);
			}
			else if(parent instanceof CirInitializerBody) {
				this.propagators.get(COperator.arith_mul_assign).propagate(cir_mutations, source_error, location, parent, propagations);
			}
			else if(parent instanceof CirWaitExpression) {
				this.propagators.get(COperator.arith_mod_assign).propagate(cir_mutations, source_error, location, parent, propagations);
			}
			else if(parent instanceof CirComputeExpression) {
				this.propagators.get(((CirComputeExpression) parent).get_operator()).propagate(cir_mutations, source_error, location, parent, propagations);
			}
			else if(parent instanceof CirArgumentList) {
				this.propagators.get(COperator.arith_div_assign).propagate(cir_mutations, source_error, location, parent, propagations);
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
				
				propagations.put(cir_mutations.flow_error(true_flow, fals_flow), 
						cir_mutations.expression_constraint(statement, condition, true));
				propagations.put(cir_mutations.flow_error(fals_flow, true_flow), 
						cir_mutations.expression_constraint(statement, condition, false));
			}
			else if(parent instanceof CirAssignStatement) {
				propagators.get(COperator.assign).propagate(cir_mutations, source_error, location, parent, propagations);
			}
		}
	}
	/**
	 * @param cir_mutations
	 * @param source_error
	 * @return the set of CirMutation generated from source error as its next propagation gender
	 * @throws Exception
	 */
	public static Collection<CirMutation> propagate(CirMutations cir_mutations, SymStateError source_error) throws Exception {
		if(cir_mutations == null)
			throw new IllegalArgumentException("Invalid cir_mutations: null");
		else if(source_error == null)
			throw new IllegalArgumentException("Invalid source_error: null");
		else {
			List<CirMutation> next_mutations = new ArrayList<CirMutation>();
			Map<SymStateError, SymConstraint> propagations = new HashMap<SymStateError, SymConstraint>();
			utils.propagate_on(cir_mutations, source_error, propagations);
			for(SymStateError next_error : propagations.keySet()) {
				SymConstraint constraint = propagations.get(next_error);
				next_mutations.add(cir_mutations.new_mutation(constraint, next_error));
			}
			return next_mutations;
		}
	}
	
}
