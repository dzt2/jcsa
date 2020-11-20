package com.jcsa.jcmutest.mutant.cir2mutant.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymExpressionError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymFlowError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymReferenceError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymStateError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymStateValueError;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymTrapError;
import com.jcsa.jcparse.flwa.symbol.CStateContexts;
import com.jcsa.jcparse.flwa.symbol.SymEvaluator;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlowType;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.sym.SymBinaryExpression;
import com.jcsa.jcparse.lang.sym.SymConstant;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;

/**
 * It implements the generation of annotations from state error as well as constraint.
 * 
 * @author yukimula
 *
 */
public class CirAnnotations {
	
	/* singleton constructor */
	/** private constructor **/
	private CirAnnotations() { }
	/** the maximal distance for search statements in flow error **/
	private static final int maximal_path_distance = 8;
	/** singleton mode **/
	public static final CirAnnotations annotations = new CirAnnotations();
	
	/* constraint generator */
	/**
	 * generate the conditions among the 
	 * @param condition
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_condition(CirStatement statement,
			SymExpression condition, 
			Collection<CirAnnotation> annotations) throws Exception {
		if(condition instanceof SymBinaryExpression) {
			SymExpression lcondition = ((SymBinaryExpression) condition).get_loperand();
			SymExpression rcondition = ((SymBinaryExpression) condition).get_roperand();
			switch(((SymBinaryExpression) condition).get_operator().get_operator()) {
			case logic_and:
			{
				this.generate_annotations_in_condition(statement, lcondition, annotations);
				this.generate_annotations_in_condition(statement, rcondition, annotations);
				break;
			}
			default:
			{
				condition = SymFactory.sym_condition(condition, true);
				annotations.add(new CirAnnotation(CirAnnotateType.eval_stmt, statement, condition));
				break;
			}
			}
		}
		else if(condition instanceof SymConstant) {
			if(((SymConstant) condition).get_bool()) {
				/* ignore the true requirement in the statement */
			}
			else {
				annotations.add(new CirAnnotation(CirAnnotateType.eval_stmt, 
						statement, SymFactory.sym_constant(Boolean.FALSE)));
			}
		}
		else {
			condition = SymFactory.sym_condition(condition, true);
			annotations.add(new CirAnnotation(CirAnnotateType.eval_stmt, statement, condition));
		}
	}
	/**
	 * @param constraint
	 * @param contexts used to optimize the constraint
	 * @param optimize whether to optimize the symbolic condition
	 * @return the annotations describing the target constraint
	 * @throws Exception
	 */
	private Collection<CirAnnotation> generate_annotations_for_constraint(
			SymConstraint constraint, CStateContexts contexts, boolean optimize) throws Exception {
		CirStatement statement = constraint.get_statement();
		SymExpression condition = constraint.get_condition();
		if(optimize) 
			condition = SymEvaluator.evaluate_on(condition, contexts);
		
		List<CirAnnotation> annotations = new ArrayList<CirAnnotation>();
		annotations.add(new CirAnnotation(CirAnnotateType.covr_stmt, statement, 
				SymFactory.sym_constant(Integer.valueOf(1))));
		this.generate_annotations_in_condition(statement, condition, annotations);
		return annotations;
	}
	
	/* statement error generator */
	/**
	 * @param state_error
	 * @return {trap_stmt(statement, null)}
	 * @throws Exception
	 */
	private Collection<CirAnnotation> generate_annotations_for_trap_error(
			SymTrapError state_error) throws Exception {
		List<CirAnnotation> annotations = new ArrayList<CirAnnotation>();
		annotations.add(new CirAnnotation(CirAnnotateType.trap_stmt,
				state_error.get_statement(), null));
		return annotations;
	}
	/**
	 * find the execution nodes since the source w.r.t. the maximal distance as given
	 * @param source
	 * @param targets
	 * @param distance
	 */
	private void find_executions_since(CirExecution source, Set<CirExecution> targets, int distance) {
		targets.add(source);
		if(distance > 0) {
			for(CirExecutionFlow flow : source.get_ou_flows()) {
				CirExecution target;
				if(flow.get_type() == CirExecutionFlowType.call_flow) {
					CirExecution call_execution = flow.get_source();
					CirExecution wait_execution = call_execution.get_graph().
							get_execution(call_execution.get_id() + 1);
					target = wait_execution;
				}
				else if(flow.get_type() == CirExecutionFlowType.retr_flow) {
					target = null;
				}
				else {
					target = flow.get_target();
				}
				
				if(target != null) {
					this.find_executions_since(target, targets, distance - 1);
				}
			}
		}
	}
	/**
	 * @param source
	 * @param distance
	 * @return the set of execution nodes since the source with maximal distance as given
	 */
	private Set<CirExecution> find_executions_since(CirExecution source, int distance) {
		Set<CirExecution> targets = new HashSet<CirExecution>();
		this.find_executions_since(source, targets, distance);
		return targets;
	}
	/**
	 * @param original_flow
	 * @param mutation_flow
	 * @param maximal_distance
	 * @return true --> add_statements; false --> del_statements.
	 */
	private Map<Boolean, Set<CirExecution>> search_add_and_del_sets(
			CirExecutionFlow original_flow, CirExecutionFlow mutation_flow,
			int maximal_distance) {
		Set<CirExecution> orig_executions = this.find_executions_since(original_flow.get_target(), maximal_distance);
		Set<CirExecution> muta_executions = this.find_executions_since(mutation_flow.get_target(), maximal_distance);
		
		Set<CirExecution> common_executions = new HashSet<CirExecution>();
		for(CirExecution execution : orig_executions) {
			if(muta_executions.contains(execution)) {
				common_executions.add(execution);
			}
		}
		
		Set<CirExecution> add_executions = new HashSet<CirExecution>();
		Set<CirExecution> del_executions = new HashSet<CirExecution>();
		for(CirExecution execution : muta_executions) {
			if(!common_executions.contains(execution)) {
				add_executions.add(execution);
			}
		}
		for(CirExecution execution : orig_executions) {
			if(!common_executions.contains(execution)) {
				del_executions.add(execution);
			}
		}
		
		Map<Boolean, Set<CirExecution>> results = new HashMap<Boolean, Set<CirExecution>>();
		results.put(Boolean.TRUE, add_executions);
		results.put(Boolean.FALSE, del_executions);
		return results;
	}
	/**
	 * @param state_error
	 * @param maximal_distance
	 * @return {add_stmt*, del_stmt*}
	 * @throws Exception
	 */
	private Collection<CirAnnotation> generate_annotations_for_flow_error(
			SymFlowError state_error, int maximal_distance) throws Exception {
		Map<Boolean, Set<CirExecution>> results = this.search_add_and_del_sets(
				state_error.get_original_flow(), state_error.get_mutation_flow(), 
				maximal_distance);
		List<CirAnnotation> annotations = new ArrayList<CirAnnotation>();
		for(CirExecution add_execution : results.get(Boolean.TRUE)) {
			annotations.add(new CirAnnotation(CirAnnotateType.add_stmt, add_execution.get_statement(), null));
		}
		for(CirExecution del_execution : results.get(Boolean.FALSE)) {
			annotations.add(new CirAnnotation(CirAnnotateType.del_stmt, del_execution.get_statement(), null));
		}
		return annotations;
	}
	
	/* value error foundations */
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
	private boolean is_integer(CirExpression expression) throws Exception {
		CType type = expression.get_data_type();
		if(type == null)
			return false;
		else
			return CTypeAnalyzer.is_integer(CTypeAnalyzer.get_value_type(type));
	}
	private boolean is_numeric(CirExpression expression) throws Exception {
		CType type = expression.get_data_type();
		if(type == null)
			return false;
		else
			return CTypeAnalyzer.is_number(CTypeAnalyzer.get_value_type(type));
	}
	private boolean is_address(CirExpression expression) throws Exception {
		CType type = expression.get_data_type();
		if(type == null)
			return false;
		else
			return CTypeAnalyzer.is_pointer(CTypeAnalyzer.get_value_type(type));
	}
	private void generate_annotations_in_boolean_expression(CirExpression expression, 
			SymExpression orig_value, SymExpression muta_value, CStateContexts contexts,
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
	private void generate_annotations_in_numeric_expression(CirExpression expression, 
			SymExpression orig_value, SymExpression muta_value, CStateContexts contexts,
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
		difference = SymEvaluator.evaluate_on(difference, contexts);
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
		orig_value = SymEvaluator.evaluate_on(orig_value, contexts);
		muta_value = SymEvaluator.evaluate_on(muta_value, contexts);
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
	private void generate_annotations_in_address_expression(CirExpression expression, 
			SymExpression orig_value, SymExpression muta_value, CStateContexts contexts,
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
		difference = SymEvaluator.evaluate_on(difference, contexts);
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
			SymExpression orig_value, SymExpression muta_value, CStateContexts contexts,
			Collection<CirAnnotation> annotations) throws Exception {
		if(orig_value.equals(muta_value)) {
			annotations.clear();
			return;
		}
		else if(this.is_boolean(expression)) {
			this.generate_annotations_in_boolean_expression(expression, orig_value, muta_value, contexts, annotations);
		}
		else if(this.is_numeric(expression)) {
			this.generate_annotations_in_numeric_expression(expression, orig_value, muta_value, contexts, annotations);
		}
		else if(this.is_address(expression)) {
			this.generate_annotations_in_address_expression(expression, orig_value, muta_value, contexts, annotations);
		}
		else {
			annotations.add(new CirAnnotation(CirAnnotateType.chg_auto, expression, null));
			annotations.add(new CirAnnotation(CirAnnotateType.set_auto, expression, muta_value));
		}
	}
	
	/* expression error generator */
	private Collection<CirAnnotation> generate_annotations_for_expr_error(
			SymExpressionError state_error, CStateContexts contexts) throws Exception {
		List<CirAnnotation> annotations = new ArrayList<CirAnnotation>();
		annotations.add(new CirAnnotation(CirAnnotateType.mut_value, state_error.get_expression(), null));
		this.generate_annotations_in_expression(state_error.get_expression(), 
				state_error.get_original_value(), state_error.get_mutation_value(), contexts, annotations);
		return annotations;
	}
	private Collection<CirAnnotation> generate_annotations_for_refr_error(
			SymReferenceError state_error, CStateContexts contexts) throws Exception {
		List<CirAnnotation> annotations = new ArrayList<CirAnnotation>();
		annotations.add(new CirAnnotation(CirAnnotateType.mut_refer, state_error.get_expression(), null));
		this.generate_annotations_in_expression(state_error.get_expression(), 
				state_error.get_original_value(), state_error.get_mutation_value(), contexts, annotations);
		return annotations;
	}
	private Collection<CirAnnotation> generate_annotations_for_stat_error(
			SymStateValueError state_error, CStateContexts contexts) throws Exception {
		List<CirAnnotation> annotations = new ArrayList<CirAnnotation>();
		annotations.add(new CirAnnotation(CirAnnotateType.mut_state, state_error.get_expression(), null));
		this.generate_annotations_in_expression(state_error.get_expression(), 
				state_error.get_original_value(), state_error.get_mutation_value(), contexts, annotations);
		return annotations;
	}
	
	/* generator */
	public static Collection<CirAnnotation> annotations(SymConstraint constraint) throws Exception {
		if(constraint == null)
			throw new IllegalArgumentException("Invalid constraint: null");
		else
			return annotations.generate_annotations_for_constraint(constraint, null, false);
	}
	public static Collection<CirAnnotation> annotations(SymConstraint constraint, CStateContexts contexts) throws Exception {
		if(constraint == null)
			throw new IllegalArgumentException("Invalid constraint: null");
		else
			return annotations.generate_annotations_for_constraint(constraint, contexts, true);
	}
	public static Collection<CirAnnotation> annotations(SymStateError state_error) throws Exception {
		return annotations(state_error, null);
	}
	public static Collection<CirAnnotation> annotations(SymStateError state_error, CStateContexts contexts) throws Exception {
		if(state_error == null)
			throw new IllegalArgumentException("Invalid state_error: null");
		else if(state_error instanceof SymTrapError)
			return annotations.generate_annotations_for_trap_error((SymTrapError) state_error);
		else if(state_error instanceof SymFlowError)
			return annotations.generate_annotations_for_flow_error((SymFlowError) state_error, maximal_path_distance);
		else if(state_error instanceof SymExpressionError)
			return annotations.generate_annotations_for_expr_error((SymExpressionError) state_error, contexts);
		else if(state_error instanceof SymReferenceError)
			return annotations.generate_annotations_for_refr_error((SymReferenceError) state_error, contexts);
		else if(state_error instanceof SymStateValueError)
			return annotations.generate_annotations_for_stat_error((SymStateValueError) state_error, contexts);
		else
			throw new IllegalArgumentException("Invalid: " + state_error);
	}
	
	public Boolean validate(SymStateError state_error, CStateContexts contexts) throws Exception {
		if(state_error == null)
			throw new IllegalArgumentException("Invalid state_error: null");
		else if(state_error instanceof SymExpressionError) {
			CirExpression expression = ((SymExpressionError) state_error).get_expression();
			SymExpression orig_value = ((SymExpressionError) state_error).get_original_value();
			SymExpression muta_value = ((SymExpressionError) state_error).get_mutation_value();
			orig_value = SymEvaluator.evaluate_on(orig_value, contexts);
			muta_value = SymEvaluator.evaluate_on(muta_value, contexts);
			if(orig_value.generate_code().equals(muta_value.generate_code())) {
				return Boolean.FALSE;
			}
			else if(orig_value instanceof SymConstant && muta_value instanceof SymConstant) {
				SymConstant lconstant = (SymConstant) orig_value;
				SymConstant rconstant = (SymConstant) muta_value;
				if(this.is_boolean(expression)) {
					return Boolean.valueOf(lconstant.get_bool() != rconstant.get_bool());
				}
				else if(this.is_integer(expression)) {
					return Boolean.valueOf(lconstant.get_long() != rconstant.get_long());
				}
				else if(this.is_numeric(expression)) {
					return Boolean.valueOf(lconstant.get_double() != rconstant.get_double());
				}
				else {
					return Boolean.FALSE;
				}
			}
			else {
				return null;
			}
		}
		else if(state_error instanceof SymReferenceError) {
			CirExpression expression = ((SymReferenceError) state_error).get_expression();
			SymExpression orig_value = ((SymReferenceError) state_error).get_original_value();
			SymExpression muta_value = ((SymReferenceError) state_error).get_mutation_value();
			orig_value = SymEvaluator.evaluate_on(orig_value, contexts);
			muta_value = SymEvaluator.evaluate_on(muta_value, contexts);
			if(orig_value.generate_code().equals(muta_value.generate_code())) {
				return Boolean.FALSE;
			}
			else if(orig_value instanceof SymConstant && muta_value instanceof SymConstant) {
				SymConstant lconstant = (SymConstant) orig_value;
				SymConstant rconstant = (SymConstant) muta_value;
				if(this.is_boolean(expression)) {
					return Boolean.valueOf(lconstant.get_bool() != rconstant.get_bool());
				}
				else if(this.is_integer(expression)) {
					return Boolean.valueOf(lconstant.get_long() != rconstant.get_long());
				}
				else if(this.is_numeric(expression)) {
					return Boolean.valueOf(lconstant.get_double() != rconstant.get_double());
				}
				else {
					return Boolean.FALSE;
				}
			}
			else {
				return null;
			}
		}
		else if(state_error instanceof SymStateValueError) {
			CirExpression expression = ((SymStateValueError) state_error).get_expression();
			SymExpression orig_value = ((SymStateValueError) state_error).get_original_value();
			SymExpression muta_value = ((SymStateValueError) state_error).get_mutation_value();
			orig_value = SymEvaluator.evaluate_on(orig_value, contexts);
			muta_value = SymEvaluator.evaluate_on(muta_value, contexts);
			if(orig_value.generate_code().equals(muta_value.generate_code())) {
				return Boolean.FALSE;
			}
			else if(orig_value instanceof SymConstant && muta_value instanceof SymConstant) {
				SymConstant lconstant = (SymConstant) orig_value;
				SymConstant rconstant = (SymConstant) muta_value;
				if(this.is_boolean(expression)) {
					return Boolean.valueOf(lconstant.get_bool() != rconstant.get_bool());
				}
				else if(this.is_integer(expression)) {
					return Boolean.valueOf(lconstant.get_long() != rconstant.get_long());
				}
				else if(this.is_numeric(expression)) {
					return Boolean.valueOf(lconstant.get_double() != rconstant.get_double());
				}
				else {
					return Boolean.FALSE;
				}
			}
			else {
				return null;
			}
		}
		else if(state_error instanceof SymTrapError) {
			return Boolean.TRUE;
		}
		else if(state_error instanceof SymFlowError) {
			return Boolean.valueOf(
					((SymFlowError) state_error).get_original_flow().get_target() != 
					((SymFlowError) state_error).get_mutation_flow().get_target());
		}
		else
			throw new IllegalArgumentException("Invalid: " + state_error);
	}
	
}
