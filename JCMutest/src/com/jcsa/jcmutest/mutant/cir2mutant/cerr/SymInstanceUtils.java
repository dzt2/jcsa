package com.jcsa.jcmutest.mutant.cir2mutant.cerr;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import com.jcsa.jcparse.flwa.symbol.SymEvaluator;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionEdge;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionPath;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionPathFinder;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.sym.SymBinaryExpression;
import com.jcsa.jcparse.lang.sym.SymConstant;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;
import com.jcsa.jcparse.lang.sym.SymNode;

/**
 * It implements the interface for optimizing or proceeding symbolic instance evaluated during testing.
 * 
 * @author yukimula
 *
 */
public class SymInstanceUtils {
	
	/* singleton mode */
	/** private constructor for producing singleton of utilities **/
	private SymInstanceUtils() { }
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
	 * whether the condition is in form of execution_ID >= integer_times
	 * @param condition
	 * @return integer_times in right-side or -1 if it is not the statement condition
	 * @throws Exception
	 */
	private int is_statement_condition(CirExecution execution, SymExpression condition) throws Exception {
		if(condition instanceof SymBinaryExpression) {
			COperator operator = ((SymBinaryExpression) condition).get_operator().get_operator();
			if(operator == COperator.greater_eq) {
				SymConstant roperand = (SymConstant) ((SymBinaryExpression) condition).get_roperand();
				return roperand.get_int();
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
		else if(this.is_statement_condition(execution, condition) > 0) {
			return execution;
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
				times = this.is_statement_condition(execution, condition);
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
	
	/* annotation methods as supporting */
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
	
	/* generate annotations */
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
	
}
