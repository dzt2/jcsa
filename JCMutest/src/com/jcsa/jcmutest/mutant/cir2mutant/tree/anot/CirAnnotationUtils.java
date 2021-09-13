package com.jcsa.jcmutest.mutant.cir2mutant.tree.anot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.jcsa.jcmutest.mutant.cir2mutant.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAttribute;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirBlockError;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirCoverCount;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirDiferError;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirFlowsError;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirReferError;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirStateError;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirTrapsError;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirValueError;
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
import com.jcsa.jcparse.lang.irlang.stmt.CirTagStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symbol.SymbolBinaryExpression;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.lang.symbol.SymbolNode;
import com.jcsa.jcparse.parse.symbol.process.SymbolProcess;

/**
 * It implements the generation, concretization as well as summarization from the
 * CirAttribute or the CirAnnotation themselves statically.
 * 
 * @author yukimula
 *
 */
final class CirAnnotationUtils {
	
	/* singleton */	/** constructor **/	private CirAnnotationUtils() {}
	private static final CirAnnotationUtils utils = new CirAnnotationUtils();
	
	/* symbolic expression analysis */
	/**
	 * It collects the sub_conditions within the input condition taking it as the logical conjunctive expression
	 * @param condition
	 * @param conditions
	 * @throws Exception
	 */
	private void get_conditions_in_conjunct(SymbolExpression condition, Collection<SymbolExpression> conditions) throws Exception {
		if(condition instanceof SymbolConstant) {
			if(((SymbolConstant) condition).get_bool()) {
				/* ignore the true operand from the conjunctive expression */
			}
			else {
				conditions.add(SymbolFactory.sym_constant(Boolean.FALSE));
			}
		}
		else if(condition instanceof SymbolBinaryExpression) {
			COperator op = ((SymbolBinaryExpression) condition).get_operator().get_operator();
			SymbolExpression loperand = ((SymbolBinaryExpression) condition).get_loperand();
			SymbolExpression roperand = ((SymbolBinaryExpression) condition).get_roperand();
			if(op == COperator.logic_and) {
				this.get_conditions_in_conjunct(loperand, conditions);
				this.get_conditions_in_conjunct(roperand, conditions);
			}
			else {
				conditions.add(SymbolFactory.sym_condition(condition, true));
			}
		}
		else {
			conditions.add(SymbolFactory.sym_condition(condition, true));
		}
	}
	/**
	 * It generates the sub_conditions that are subsumed by the input condition using the relational implication.
	 * @param condition
	 * @param conditions
	 * @throws Exception
	 */
	private void get_conditions_by_subsumed(SymbolExpression condition, Collection<SymbolExpression> conditions) throws Exception {
		if(condition instanceof SymbolConstant) {
			if(((SymbolConstant) condition).get_bool()) {
				/* ignore the true operand from the conjunctive expression */
			}
			else {
				conditions.add(SymbolFactory.sym_constant(Boolean.FALSE));
			}
		}
		else if(condition instanceof SymbolBinaryExpression) {
			COperator op = ((SymbolBinaryExpression) condition).get_operator().get_operator();
			SymbolExpression loperand = ((SymbolBinaryExpression) condition).get_loperand();
			SymbolExpression roperand = ((SymbolBinaryExpression) condition).get_roperand();
			
			if(op == COperator.greater_tn) {
				conditions.add(SymbolFactory.greater_eq(loperand, roperand));
				conditions.add(SymbolFactory.not_equals(loperand, roperand));
			}
			else if(op == COperator.smaller_tn) {
				conditions.add(SymbolFactory.smaller_eq(loperand, roperand));
				conditions.add(SymbolFactory.not_equals(loperand, roperand));
			}
			else if(op == COperator.equal_with) {
				conditions.add(SymbolFactory.greater_eq(loperand, roperand));
				conditions.add(SymbolFactory.smaller_eq(loperand, roperand));
			}
			conditions.add(SymbolFactory.sym_condition(condition, true));
		}
		else {
			conditions.add(SymbolFactory.sym_condition(condition, true));
		}
	}
	/**
	 * @param constraint
	 * @return the set of conditions divided and subsumed from the conjunctive input constraint
	 * @throws Exception
	 */
	private Collection<SymbolExpression> get_conditions_in(SymbolExpression constraint) throws Exception {
		Set<SymbolExpression> sub_conditions = new HashSet<SymbolExpression>();
		Set<SymbolExpression> sum_conditions = new HashSet<SymbolExpression>();
		this.get_conditions_in_conjunct(constraint, sum_conditions);
		for(SymbolExpression sub_condition : sub_conditions) {
			this.get_conditions_by_subsumed(sub_condition, sum_conditions);
		}
		return sum_conditions;
	}
	/**
	 * It recursively captures the references used in the expression
	 * @param expression
	 * @param references
	 * @throws Exception
	 */
	private void get_references_in_expression(SymbolNode expression, Collection<SymbolExpression> references) throws Exception {
		if(expression.is_reference()) {
			references.add((SymbolExpression) expression);
		}
		for(SymbolNode child : expression.get_children()) {
			this.get_references_in_expression(child, references);
		}
	}
	/**
	 * @param expression
	 * @return the set of symbolic references used in the expression
	 * @throws Exception
	 */
	private Collection<SymbolExpression> get_references_in(SymbolNode expression) throws Exception {
		Set<SymbolExpression> references = new HashSet<SymbolExpression>();
		this.get_references_in_expression(expression, references);
		return references;
	}
	/**
	 * @param expression
	 * @param references
	 * @return whether any reference in the expression is defined in the input collection
	 * @throws Exception
	 */
	private boolean has_references_in(SymbolNode expression, Collection<SymbolExpression> references) throws Exception {
		if(expression == null) {
			return false;
		}
		else if(references == null || references.isEmpty()) {
			return false;
		}
		else if(references.contains(expression)) {
			return true;
		}
		else {
			for(SymbolNode child : expression.get_children()) {
				if(this.has_references_in(child, references)) {
					return true;
				}
			}
			return false;
		}
	}
	
	/* decidable path based check-point */
	/**
	 * @param max_times
	 * @param execution_times
	 */
	private Collection<Integer> get_execution_times_from(int max_times) {
		Collection<Integer> execution_times = new ArrayList<Integer>();
		for(int times = 1; times < max_times; times = times * 2) {
			execution_times.add(Integer.valueOf(times));
		}
		execution_times.add(Integer.valueOf(max_times));
		return execution_times;
	}
	/**
	 * @param execution
	 * @param references
	 * @return whether any reference is used in the statement of the input execution point
	 * @throws Exception
	 */
	private boolean has_references_in(CirExecution execution, Collection<SymbolExpression> references) throws Exception {
		if(execution == null) {
			return false;
		}
		else if(references == null || references.isEmpty()) {
			return false;
		}
		else {
			CirStatement statement = execution.get_statement();
			if(statement instanceof CirAssignStatement) {
				return references.contains(SymbolFactory.sym_expression(
						((CirAssignStatement) statement).get_lvalue()));
			}
			else if(statement instanceof CirIfStatement) {
				SymbolExpression condition = SymbolFactory.sym_expression(((CirIfStatement) statement).get_condition());
				return this.has_references_in(condition, references);
			}
			else if(statement instanceof CirCaseStatement) {
				SymbolExpression condition = SymbolFactory.sym_expression(((CirCaseStatement) statement).get_condition());
				return this.has_references_in(condition, references);
			}
			else {
				return false;
			}
		}
	}
	/**
	 * @param execution
	 * @param expression
	 * @return it determines the best-prior check-point to evaluate the expression
	 * @throws Exception
	 */
	private CirExecution find_prior_checkpoint(CirExecution execution, SymbolExpression expression) throws Exception {
		if(expression instanceof SymbolConstant) {
			if(((SymbolConstant) expression).get_bool()) {
				CirExecutionPath prev_path = CirExecutionPathFinder.finder.db_extend(execution);
				Iterator<CirExecutionEdge> iterator = prev_path.get_iterator(true);
				while(iterator.hasNext()) {
					CirExecutionEdge edge = iterator.next();
					switch(edge.get_type()) {
					case true_flow:	return edge.get_target();
					case fals_flow:	return edge.get_target();
					default:		break;
					}
				}
				return prev_path.get_source();
			}
			else {
				return execution.get_graph().get_entry();
			}
		}
		else {
			CirExecutionPath prev_path = CirExecutionPathFinder.finder.db_extend(execution);
			Iterator<CirExecutionEdge> iterator = prev_path.get_iterator(true);
			Collection<SymbolExpression> references = this.get_references_in(expression);
			while(iterator.hasNext()) {
				CirExecutionEdge edge = iterator.next();
				if(this.has_references_in(edge.get_source(), references)) {
					return edge.get_target();
				}
			}
			return prev_path.get_source();
		}
	}
	/**
	 * true --> add_executions; false --> del_executions;
	 * @param orig_target
	 * @param muta_target
	 * @return
	 * @throws Exception
	 */
	private Map<Boolean, Collection<CirExecution>>	find_add_del_executions(CirExecution orig_target, CirExecution muta_target) throws Exception {
		if(orig_target == null) {
			throw new IllegalArgumentException("Invalid orig_target: null");
		}
		else if(muta_target == null) {
			throw new IllegalArgumentException("Invalid muta_target: null");
		}
		else {
			/* compute the statements being added or deleted in testing */
			Collection<CirExecution> add_executions = new HashSet<CirExecution>();
			Collection<CirExecution> del_executions = new HashSet<CirExecution>();
			CirExecutionPath orig_path = CirExecutionPathFinder.finder.df_extend(orig_target);
			CirExecutionPath muta_path = CirExecutionPathFinder.finder.df_extend(muta_target);
			for(CirExecutionEdge edge : muta_path.get_edges()) { add_executions.add(edge.get_source()); }
			for(CirExecutionEdge edge : orig_path.get_edges()) { del_executions.add(edge.get_source()); }
			add_executions.add(muta_path.get_target()); del_executions.add(orig_path.get_target());

			/* removed the common part for corrections */
			Collection<CirExecution> com_executions = new HashSet<CirExecution>();
			for(CirExecution execution : add_executions) {
				if(del_executions.contains(execution)) {
					com_executions.add(execution);
				}
			}
			add_executions.removeAll(com_executions);
			del_executions.removeAll(com_executions);

			/* construct mapping from true|false to collections */
			Map<Boolean, Collection<CirExecution>> results =
					new HashMap<Boolean, Collection<CirExecution>>();
			results.put(Boolean.TRUE, add_executions);
			results.put(Boolean.FALSE, del_executions);
			return results;
		}
	}
	
	/* generation: CirAttribute --> CirAnnotation* */
	/**
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_cover_count(CirCoverCount attribute, Collection<CirAnnotation> annotations) throws Exception {
		CirExecution execution = attribute.get_execution();
		int max_exec_time = attribute.get_coverage_count();
		for(Integer time : this.get_execution_times_from(max_exec_time)) {
			annotations.add(CirAnnotation.cov_stmt(execution, time.intValue()));
		}
		return;
	}
	/**
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_constraints(CirConstraint attribute, Collection<CirAnnotation> annotations) throws Exception {
		/* 1. captures the sub_conditions divided and subsumed from constraint */
		CirExecution execution = attribute.get_execution(), check_point;
		SymbolExpression constraint = attribute.get_condition();
		constraint = CirAnnotationValue.safe_evaluate(constraint, null);
		Collection<SymbolExpression> conditions = this.get_conditions_in(constraint);
		
		/* 2. generate the eva_expr annotation from basic sub_conditions */
		for(SymbolExpression condition : conditions) {
			check_point = this.find_prior_checkpoint(execution, condition);
			annotations.add(CirAnnotation.eva_expr(check_point, condition, true));
		}
		
		/* 3. it complements the coverage requirement if no condition used */
		if(conditions.isEmpty()) {
			SymbolExpression condition = SymbolFactory.sym_constant(Boolean.TRUE);
			check_point = this.find_prior_checkpoint(execution, condition);
			annotations.add(CirAnnotation.cov_stmt(check_point, 1));
		}
	}
	/**
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_block_error(CirBlockError attribute, Collection<CirAnnotation> annotations) throws Exception {
		CirExecution execution = attribute.get_execution();
		boolean original_execution, mutation_execution;
		if(execution.get_statement() instanceof CirTagStatement) { return; }
		else if(attribute.is_executed()) {
			original_execution = Boolean.FALSE;
			mutation_execution = Boolean.TRUE;
		}
		else {
			original_execution = Boolean.TRUE;
			mutation_execution = Boolean.FALSE;
		}
		annotations.add(CirAnnotation.ori_stmt(execution, original_execution));
		annotations.add(CirAnnotation.mut_stmt(execution, mutation_execution));
		annotations.add(CirAnnotation.cmp_diff(execution, SymbolFactory.sym_constant(mutation_execution)));
	}
	/**
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_flows_error(CirFlowsError attribute, Collection<CirAnnotation> annotations) throws Exception {
		CirExecution orig_target = attribute.get_original_flow().get_target();
		CirExecution muta_target = attribute.get_mutation_flow().get_target();
		if(orig_target != muta_target) {
			Map<Boolean, Collection<CirExecution>> maps = this.find_add_del_executions(orig_target, muta_target);
			for(Boolean executed : maps.keySet()) {
				for(CirExecution execution : maps.get(executed)) {
					if(execution.get_statement() instanceof CirTagStatement) {
						continue;
					}
					else if(executed.booleanValue()) {
						annotations.add(CirAnnotation.ori_stmt(execution, false));
						annotations.add(CirAnnotation.mut_stmt(execution, true));
						annotations.add(CirAnnotation.cmp_diff(execution, SymbolFactory.sym_constant(true)));
					}
					else {
						annotations.add(CirAnnotation.ori_stmt(execution, true));
						annotations.add(CirAnnotation.mut_stmt(execution, false));
						annotations.add(CirAnnotation.cmp_diff(execution, SymbolFactory.sym_constant(false)));
					}
				}
			}
		}
	}
	/**
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_traps_error(CirTrapsError attribute, Collection<CirAnnotation> annotations) throws Exception {
		annotations.add(CirAnnotation.trp_stmt(attribute.get_execution()));
	}
	/**
	 * @param expression
	 * @param value
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_exprs_error(CirExpression expression, 
			Object value, Collection<CirAnnotation> annotations) throws Exception {
		/* 1. initialize the original and mutated annotation */
		CirExecution execution = expression.execution_of();
		CirAnnotation orig_annotation;
		if(CirMutations.is_assigned(expression)) {
			CirAssignStatement stmt = (CirAssignStatement) expression.statement_of();
			orig_annotation = CirAnnotation.ori_expr(expression, stmt.get_rvalue());
		}
		else {
			orig_annotation = CirAnnotation.ori_expr(expression, expression);
		}
		CirAnnotation muta_annotation = CirAnnotation.mut_expr(expression, value);
		if(orig_annotation.get_symb_value() == CirAnnotationValue.expt_value
			|| muta_annotation.get_symb_value() == CirAnnotationValue.expt_value) {
			annotations.add(CirAnnotation.trp_stmt(execution)); 
			return;	/* simply return traps when arithmetic exception is thrown */
		}
		
		/* 2. compare whether original equals with mutated values */
		SymbolExpression orig_value = orig_annotation.get_symb_value();
		SymbolExpression muta_value = muta_annotation.get_symb_value();
		if(orig_value.equals(muta_value)) {
			SymbolExpression condition = SymbolFactory.sym_constant(Boolean.FALSE);
			CirExecution check_point = this.find_prior_checkpoint(execution, condition);
			annotations.add(CirAnnotation.eva_expr(check_point, condition, true));
			return;	/* invalid infection constraint involved in testing */
		}
		/* 3. otherwise, insert the annotation along with cmp_diff */
		else {
			annotations.add(orig_annotation);
			annotations.add(muta_annotation);
			annotations.add(CirAnnotation.cmp_diff(expression, SymbolFactory.not_equals(orig_value, muta_value)));
		}
		
		/* 4. difference generation based on its data type */
		if(CirMutations.is_numeric(expression)) {
			annotations.add(CirAnnotation.sub_diff(expression, CirAnnotationValue.sub_difference(expression, orig_value, muta_value)));
			annotations.add(CirAnnotation.ext_diff(expression, CirAnnotationValue.ext_difference(expression, orig_value, muta_value)));
		}
		if(CirMutations.is_address(expression)) {
			annotations.add(CirAnnotation.sub_diff(expression, CirAnnotationValue.sub_difference(expression, orig_value, muta_value)));
		}
		if(CirMutations.is_integer(expression)) {
			annotations.add(CirAnnotation.xor_diff(expression, CirAnnotationValue.xor_difference(expression, orig_value, muta_value)));
		}
	}
	/**
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_difer_error(CirDiferError attribute, Collection<CirAnnotation> annotations) throws Exception {
		CirExpression expression = attribute.get_orig_expression();
		SymbolExpression value = attribute.get_muta_expression();
		this.generate_annotations_in_exprs_error(expression, value, annotations);
	}
	/**
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_value_error(CirValueError attribute, Collection<CirAnnotation> annotations) throws Exception {
		CirExpression expression = attribute.get_orig_expression();
		SymbolExpression value = attribute.get_muta_expression();
		this.generate_annotations_in_exprs_error(expression, value, annotations);
	}
	/**
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_refer_error(CirReferError attribute, Collection<CirAnnotation> annotations) throws Exception {
		CirExpression expression = attribute.get_orig_expression();
		SymbolExpression value = attribute.get_muta_expression();
		this.generate_annotations_in_exprs_error(expression, value, annotations);
	}
	/**
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_state_error(CirStateError attribute, Collection<CirAnnotation> annotations) throws Exception {
		CirExpression expression = attribute.get_orig_expression();
		SymbolExpression value = attribute.get_muta_expression();
		this.generate_annotations_in_exprs_error(expression, value, annotations);
	}
	/**
	 * It generates the symbolic annotations from the attribute directly
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in(CirAttribute attribute, Collection<CirAnnotation> annotations) throws Exception {
		if(attribute == null) {
			throw new IllegalArgumentException("Invalid attribute as null");
		}
		else if(annotations == null) {
			throw new IllegalArgumentException("Invalid annotations: null");
		}
		else if(attribute instanceof CirCoverCount) {
			this.generate_annotations_in_cover_count((CirCoverCount) attribute, annotations);
		}
		else if(attribute instanceof CirConstraint) {
			this.generate_annotations_in_constraints((CirConstraint) attribute, annotations);
		}
		else if(attribute instanceof CirBlockError) {
			this.generate_annotations_in_block_error((CirBlockError) attribute, annotations);
		}
		else if(attribute instanceof CirFlowsError) {
			this.generate_annotations_in_flows_error((CirFlowsError) attribute, annotations);
		}
		else if(attribute instanceof CirTrapsError) {
			this.generate_annotations_in_traps_error((CirTrapsError) attribute, annotations);
		}
		else if(attribute instanceof CirDiferError) {
			this.generate_annotations_in_difer_error((CirDiferError) attribute, annotations);
		}
		else if(attribute instanceof CirValueError) {
			this.generate_annotations_in_value_error((CirValueError) attribute, annotations);
		}
		else if(attribute instanceof CirReferError) {
			this.generate_annotations_in_refer_error((CirReferError) attribute, annotations);
		}
		else if(attribute instanceof CirStateError) {
			this.generate_annotations_in_state_error((CirStateError) attribute, annotations);
		}
		else {
			throw new IllegalArgumentException("Unsupported: " + attribute);
		}
	}
	/**
	 * It generates the symbolic annotations from the attribute directly
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	protected static void generate_annotations(CirAttribute attribute, Collection<CirAnnotation> annotations) throws Exception {
		utils.generate_annotations_in(attribute, annotations);
	}
	
	/* concretization: CirAnnotation --> CirAnnotation+ */
	/**
	 * @param annotation
	 * @param context
	 * @param annotations
	 * @throws Exception
	 */
	private void concretize_annotations_in_cov_stmt(CirAnnotation annotation,
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception {
		if(context != null) {
			/* fetch the execution and its maximal times */
			CirExecution execution = annotation.get_exec_point();
			int execution_times = ((SymbolConstant) 
					annotation.get_symb_value()).get_int().intValue();
			
			/* fecth how many times the statement is executed before */
			SymbolExpression source = SymbolFactory.sym_expression(execution);
			SymbolExpression target = context.get_data_stack().load(source);
			int executed_times;
			if(target == null) {
				executed_times = 0;
			}
			else {
				executed_times = ((SymbolConstant) target).get_int().intValue();
			}
			
			/* determine whether the coverage requirement is achieved */
			Boolean result = Boolean.valueOf(executed_times >= execution_times);
			annotations.add(CirAnnotation.eva_expr(execution, result, true));
		}
		else { /* undecidable result */ }
	}
	/**
	 * @param annotation
	 * @param context
	 * @param annotations
	 * @throws Exception
	 */
	private void concretize_annotations_in_eva_expr(CirAnnotation annotation,
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception {
		/* 1. determine the constraint value under the context */
		SymbolExpression condition = annotation.get_symb_value();
		condition = CirAnnotationValue.safe_evaluate(condition, context);
		CirExecution execution = annotation.get_exec_point();
		
		/* 2. exception occurs meaning that the mutant is killed */
		if(condition == CirAnnotationValue.expt_value) {
			annotations.add(CirAnnotation.eva_expr(execution, Boolean.TRUE, true));
		}
		/* 3. evaluation results as constant will be accumulated */
		else if(condition instanceof SymbolConstant) {
			Boolean result = ((SymbolConstant) condition).get_bool();
			annotations.add(CirAnnotation.eva_expr(execution, result, true));
		}
		else { /* otherwise, ignore the constant results anyway */ }
	}
	/**
	 * @param annotation
	 * @param context
	 * @param annotations
	 * @throws Exception
	 */
	private void concretize_annotations_in_trp_stmt(CirAnnotation annotation,
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception {
		annotations.add(annotation);
	}
	/**
	 * @param annotation
	 * @param context
	 * @param annotations
	 * @throws Exception
	 */
	private void concretize_annotations_in_ori_stmt(CirAnnotation annotation,
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception {
		/* 1. evaluate the symbolic expression under execution */
		CirExecution execution = annotation.get_exec_point();
		SymbolExpression value = annotation.get_symb_value();
		
		/* 3. accumulate the evaluation results as constants */
		if(value instanceof SymbolConstant) {
			Boolean result = ((SymbolConstant) value).get_bool();
			annotations.add(CirAnnotation.ori_stmt(execution, result));
		}
		/* 4. otherwise, ignore the evaluation result when it is not constant */
		else {
			return;
		}
	}
	/**
	 * @param annotation
	 * @param context
	 * @param annotations
	 * @throws Exception
	 */
	private void concretize_annotations_in_mut_stmt(CirAnnotation annotation,
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception {
		/* 1. evaluate the symbolic expression under execution */
		CirExecution execution = annotation.get_exec_point();
		SymbolExpression value = annotation.get_symb_value();
		
		/* 3. accumulate the evaluation results as constants */
		if(value instanceof SymbolConstant) {
			Boolean result = ((SymbolConstant) value).get_bool();
			annotations.add(CirAnnotation.mut_stmt(execution, result));
		}
		/* 4. otherwise, ignore the evaluation result when it is not constant */
		else {
			return;
		}
	}
	/**
	 * @param annotation
	 * @param context
	 * @param annotations
	 * @throws Exception
	 */
	private void concretize_annotations_in_ori_expr(CirAnnotation annotation,
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception {
		/* 1. evaluate the symbolic expression under context */
		CirExecution execution = annotation.get_exec_point();
		CirExpression expression = (CirExpression) annotation.get_store_unit();
		SymbolExpression value = annotation.get_symb_value();
		value = CirAnnotationValue.safe_evaluate(value, context);
		
		/* 2. trap occurs will transformed as trapping error */
		if(value == CirAnnotationValue.expt_value) {
			annotations.add(CirAnnotation.trp_stmt(execution));
		}
		/* 3. accumulate the evaluation results as constants */
		else if(value instanceof SymbolConstant) {
			annotations.add(CirAnnotation.ori_expr(expression, value));
		}
		/* 4. otherwise, ignore the evaluation result when it is not constant */
		else {
			return;
		}
	}
	/**
	 * @param annotation
	 * @param context
	 * @param annotations
	 * @throws Exception
	 */
	private void concretize_annotations_in_mut_expr(CirAnnotation annotation,
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception {
		/* 1. evaluate the symbolic expression under context */
		CirExecution execution = annotation.get_exec_point();
		CirExpression expression = (CirExpression) annotation.get_store_unit();
		SymbolExpression value = annotation.get_symb_value();
		value = CirAnnotationValue.safe_evaluate(value, context);
		
		/* 2. trap occurs will transformed as trapping error */
		if(value == CirAnnotationValue.expt_value) {
			annotations.add(CirAnnotation.trp_stmt(execution));
		}
		/* 3. accumulate the evaluation results as constants */
		else if(value instanceof SymbolConstant) {
			annotations.add(CirAnnotation.mut_expr(expression, value));
		}
		/* 4. otherwise, ignore the evaluation result when it is not constant */
		else {
			return;
		}
	}
	/**
	 * @param annotation
	 * @param context
	 * @param annotations
	 * @throws Exception
	 */
	private void concretize_annotations_in_cmp_diff(CirAnnotation annotation,
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception {
		/* 1. evaluation the symbolic value of the annotation */
		SymbolExpression value = annotation.get_symb_value();
		value = CirAnnotationValue.safe_evaluate(value, context);
		CirExecution execution = annotation.get_exec_point();
		CirNode location = annotation.get_store_unit();
		
		/* 2. accumulate the trapping when it simply happens */
		if(value == CirAnnotationValue.expt_value) {
			annotations.add(CirAnnotation.trp_stmt(execution));
		}
		/* 3. accumulate the constants when it is evaluated */
		else if(value instanceof SymbolConstant) {
			if(location instanceof CirStatement) {
				annotations.add(CirAnnotation.cmp_diff(execution, value));
			}
			else if(location instanceof CirExpression) {
				annotations.add(CirAnnotation.cmp_diff((CirExpression) location, value));
			}
			else {
				throw new IllegalArgumentException("Invalid: " + location);
			}
		}
		/* 4. otherwise, ignore the evaluation result anyway */
		else {
			return;
		}
	}
	/**
	 * @param annotation
	 * @param context
	 * @param annotations
	 * @throws Exception
	 */
	private void concretize_annotations_in_sub_diff(CirAnnotation annotation,
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception {
		/* 1. evaluate the symbolic expression under context */
		CirExecution execution = annotation.get_exec_point();
		CirExpression expression = (CirExpression) annotation.get_store_unit();
		SymbolExpression value = annotation.get_symb_value();
		value = CirAnnotationValue.safe_evaluate(value, context);
		
		/* 2. trap occurs will transformed as trapping error */
		if(value == CirAnnotationValue.expt_value) {
			annotations.add(CirAnnotation.trp_stmt(execution));
		}
		/* 3. accumulate the evaluation results as constants */
		else if(value instanceof SymbolConstant) {
			annotations.add(CirAnnotation.sub_diff(expression, value));
		}
		/* 4. otherwise, ignore the evaluation result when it is not constant */
		else {
			return;
		}
	}
	/**
	 * @param annotation
	 * @param context
	 * @param annotations
	 * @throws Exception
	 */
	private void concretize_annotations_in_ext_diff(CirAnnotation annotation,
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception {
		/* 1. evaluate the symbolic expression under context */
		CirExecution execution = annotation.get_exec_point();
		CirExpression expression = (CirExpression) annotation.get_store_unit();
		SymbolExpression value = annotation.get_symb_value();
		value = CirAnnotationValue.safe_evaluate(value, context);
		
		/* 2. trap occurs will transformed as trapping error */
		if(value == CirAnnotationValue.expt_value) {
			annotations.add(CirAnnotation.trp_stmt(execution));
		}
		/* 3. accumulate the evaluation results as constants */
		else if(value instanceof SymbolConstant) {
			annotations.add(CirAnnotation.ext_diff(expression, value));
		}
		/* 4. otherwise, ignore the evaluation result when it is not constant */
		else {
			return;
		}
	}
	/**
	 * @param annotation
	 * @param context
	 * @param annotations
	 * @throws Exception
	 */
	private void concretize_annotations_in_xor_diff(CirAnnotation annotation,
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception {
		/* 1. evaluate the symbolic expression under context */
		CirExecution execution = annotation.get_exec_point();
		CirExpression expression = (CirExpression) annotation.get_store_unit();
		SymbolExpression value = annotation.get_symb_value();
		value = CirAnnotationValue.safe_evaluate(value, context);
		
		/* 2. trap occurs will transformed as trapping error */
		if(value == CirAnnotationValue.expt_value) {
			annotations.add(CirAnnotation.trp_stmt(execution));
		}
		/* 3. accumulate the evaluation results as constants */
		else if(value instanceof SymbolConstant) {
			annotations.add(CirAnnotation.xor_diff(expression, value));
		}
		/* 4. otherwise, ignore the evaluation result when it is not constant */
		else {
			return;
		}
	}
	/**
	 * It generates the concrete annotations from the input under the context.
	 * @param annotation
	 * @param context
	 * @param annotations
	 * @throws Exception
	 */
	private void concretize_annotations_in(CirAnnotation annotation, 
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception {
		if(annotation == null) {
			throw new IllegalArgumentException("Invalid annotation: null");
		}
		else if(annotations == null) {
			throw new IllegalArgumentException("No output is established");
		}
		else {
			switch(annotation.get_value_type()) {
			case cov_stmt:	this.concretize_annotations_in_cov_stmt(annotation, context, annotations); break;
			case eva_expr:	this.concretize_annotations_in_eva_expr(annotation, context, annotations); break;
			case trp_stmt:	this.concretize_annotations_in_trp_stmt(annotation, context, annotations); break;
			case ori_stmt:	this.concretize_annotations_in_ori_stmt(annotation, context, annotations); break;
			case mut_stmt:	this.concretize_annotations_in_mut_stmt(annotation, context, annotations); break;
			case ori_expr:	this.concretize_annotations_in_ori_expr(annotation, context, annotations); break;
			case mut_expr:	this.concretize_annotations_in_mut_expr(annotation, context, annotations); break;
			case cmp_diff:	this.concretize_annotations_in_cmp_diff(annotation, context, annotations); break;
			case sub_diff:	this.concretize_annotations_in_sub_diff(annotation, context, annotations); break;
			case ext_diff:	this.concretize_annotations_in_ext_diff(annotation, context, annotations); break;
			case xor_diff:	this.concretize_annotations_in_xor_diff(annotation, context, annotations); break;
			default:		throw new IllegalArgumentException("Invalid annotation as " + annotation);
			}
		}
	}
	/**
	 * It generates the concrete annotations from the input under the context.
	 * @param annotation
	 * @param context
	 * @param annotations
	 * @throws Exception
	 */
	protected static void concretize_annotations(CirAnnotation annotation, 
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception {
		utils.concretize_annotations_in(annotation, context, annotations);
	}
	
	/* summarization: CirAnnotation[CirAnnotation*] --> CirAnnotation* */
	/**
	 * @param annotation
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_cov_stmt(CirAnnotation annotation,
			Collection<CirAnnotation> concrete_annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		abstract_annotations.add(annotation);
	}
	/**
	 * @param annotation
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_eva_expr(CirAnnotation annotation,
			Collection<CirAnnotation> concrete_annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		abstract_annotations.add(annotation);
	}
	/**
	 * @param annotation
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_trp_stmt(CirAnnotation annotation,
			Collection<CirAnnotation> concrete_annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		if(!concrete_annotations.isEmpty()) {
			abstract_annotations.add(annotation);
		}
	}
	/**
	 * @param annotation
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_ori_stmt(CirAnnotation annotation,
			Collection<CirAnnotation> concrete_annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		/* NOTE: mut_stmt and ori_stmt are not considered in features */
	}
	/**
	 * @param annotation
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_mut_stmt(CirAnnotation annotation,
			Collection<CirAnnotation> concrete_annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		/* NOTE: mut_stmt and ori_stmt are not considered in features */
	}
	/**
	 * @param annotation
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_ori_expr(CirAnnotation annotation,
			Collection<CirAnnotation> concrete_annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		/* 1. collect the concrete values or return as trapping */
		CirExecution execution = annotation.get_exec_point();
		CirExpression expression = (CirExpression) annotation.get_store_unit();
		Set<SymbolExpression> values = new HashSet<SymbolExpression>();
		for(CirAnnotation concrete_annotation : concrete_annotations) {
			SymbolExpression value = concrete_annotation.get_symb_value();
			if(value instanceof SymbolConstant) {
				values.add(value);
			}
			else if(value == CirAnnotationValue.expt_value) {
				abstract_annotations.add(CirAnnotation.trp_stmt(execution));
				return;
			}
		}
		
		/* 2. summarize the abstract values and update into annotations */
		Collection<SymbolExpression> scopes = CirAnnotationValue.find_scopes(expression, values);
		for(SymbolExpression scope : scopes) {
			abstract_annotations.add(CirAnnotation.ori_expr(expression, scope));
		}
		abstract_annotations.add(annotation);
	}
	/**
	 * @param annotation
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_mut_expr(CirAnnotation annotation,
			Collection<CirAnnotation> concrete_annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		/* 1. collect the concrete values or return as trapping */
		CirExecution execution = annotation.get_exec_point();
		CirExpression expression = (CirExpression) annotation.get_store_unit();
		Set<SymbolExpression> values = new HashSet<SymbolExpression>();
		for(CirAnnotation concrete_annotation : concrete_annotations) {
			SymbolExpression value = concrete_annotation.get_symb_value();
			if(value instanceof SymbolConstant) {
				values.add(value);
			}
			else if(value == CirAnnotationValue.expt_value) {
				abstract_annotations.add(CirAnnotation.trp_stmt(execution));
				return;
			}
		}
		
		/* 2. summarize the abstract values and update into annotations */
		Collection<SymbolExpression> scopes = CirAnnotationValue.find_scopes(expression, values);
		for(SymbolExpression scope : scopes) {
			abstract_annotations.add(CirAnnotation.mut_expr(expression, scope));
		}
		abstract_annotations.add(annotation);
	}
	/**
	 * @param annotation
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_cmp_diff(CirAnnotation annotation,
			Collection<CirAnnotation> concrete_annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		if(annotation.get_store_unit() instanceof CirStatement) {
			/* S-1. capture the boolean values hold by concrete annotations */
			Collection<Boolean> values = new HashSet<Boolean>();
			CirExecution execution = annotation.get_exec_point();
			for(CirAnnotation concrete_annotation : concrete_annotations) {
				SymbolExpression value = concrete_annotation.get_symb_value();
				if(value instanceof SymbolConstant) {
					values.add(((SymbolConstant) value).get_bool());
				}
				else if(value == CirAnnotationValue.expt_value) {
					abstract_annotations.add(CirAnnotation.trp_stmt(execution));
					return;
				}
			}
			
			/* S-2. summarize the abstract annotations from concrete ones */
			if(values.size() > 1) {
				abstract_annotations.add(annotation);
			}
			else if(values.contains(Boolean.TRUE)) {
				abstract_annotations.add(CirAnnotation.cmp_diff(execution, CirAnnotationValue.true_value));
			}
			else if(values.contains(Boolean.FALSE)) {
				abstract_annotations.add(CirAnnotation.cmp_diff(execution, CirAnnotationValue.fals_value));
			}
			else { /* none of values are created */ }
		}
		else {
			/* S-1. capture the boolean values hold by concrete annotations */
			Collection<Boolean> values = new HashSet<Boolean>();
			CirExecution execution = annotation.get_exec_point();
			CirExpression expression = (CirExpression) annotation.get_store_unit();
			for(CirAnnotation concrete_annotation : concrete_annotations) {
				SymbolExpression value = concrete_annotation.get_symb_value();
				if(value instanceof SymbolConstant) {
					values.add(((SymbolConstant) value).get_bool());
				}
				else if(value == CirAnnotationValue.expt_value) {
					abstract_annotations.add(CirAnnotation.trp_stmt(execution));
					return;
				}
			}
			
			/* E-2. summarize the abstract annotations from the concrete ones */
			if(values.size() > 1) {
				abstract_annotations.add(CirAnnotation.cmp_diff(expression, CirAnnotationValue.bool_value));
			}
			else if(values.contains(Boolean.TRUE)) {
				abstract_annotations.add(CirAnnotation.cmp_diff(expression, CirAnnotationValue.true_value));
			}
			else if(values.contains(Boolean.FALSE)) {
				abstract_annotations.add(CirAnnotation.cmp_diff(expression, CirAnnotationValue.fals_value));
			}
			else { /* none of values are created */ }
		}
	}
	/**
	 * @param annotation
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_sub_diff(CirAnnotation annotation,
			Collection<CirAnnotation> concrete_annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		/* E-1. capture the scopes of value domains in target expression */
		CirExecution execution = annotation.get_exec_point();
		CirExpression expression = (CirExpression) annotation.get_store_unit();
		Set<SymbolExpression> values = new HashSet<SymbolExpression>();
		for(CirAnnotation concrete_annotation : concrete_annotations) {
			SymbolExpression value = concrete_annotation.get_symb_value();
			if(value == CirAnnotationValue.expt_value) {
				abstract_annotations.add(CirAnnotation.trp_stmt(execution));
				return;
			}
			else if(value instanceof SymbolConstant) {
				values.add(value);
			}
		}
		Collection<SymbolExpression> scopes = CirAnnotationValue.find_scopes(expression, values);
		
		/* E-2. summarize the abstract scopes from concrete annotations */
		for(SymbolExpression scope : scopes) {
			abstract_annotations.add(CirAnnotation.sub_diff(expression, scope));
		}
	}
	/**
	 * @param annotation
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_xor_diff(CirAnnotation annotation,
			Collection<CirAnnotation> concrete_annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		/* E-1. capture the scopes of value domains in target expression */
		CirExecution execution = annotation.get_exec_point();
		CirExpression expression = (CirExpression) annotation.get_store_unit();
		Set<SymbolExpression> values = new HashSet<SymbolExpression>();
		for(CirAnnotation concrete_annotation : concrete_annotations) {
			SymbolExpression value = concrete_annotation.get_symb_value();
			if(value == CirAnnotationValue.expt_value) {
				abstract_annotations.add(CirAnnotation.trp_stmt(execution));
				return;
			}
			else if(value instanceof SymbolConstant) {
				values.add(value);
			}
		}
		Collection<SymbolExpression> scopes = CirAnnotationValue.find_scopes(expression, values);
		
		/* E-2. summarize the abstract scopes from concrete annotations */
		for(SymbolExpression scope : scopes) {
			abstract_annotations.add(CirAnnotation.xor_diff(expression, scope));
		}
	}
	/**
	 * @param annotation
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_ext_diff(CirAnnotation annotation,
			Collection<CirAnnotation> concrete_annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		/* E-1. capture the scopes of value domains in target expression */
		CirExecution execution = annotation.get_exec_point();
		CirExpression expression = (CirExpression) annotation.get_store_unit();
		Set<SymbolExpression> values = new HashSet<SymbolExpression>();
		for(CirAnnotation concrete_annotation : concrete_annotations) {
			SymbolExpression value = concrete_annotation.get_symb_value();
			if(value == CirAnnotationValue.expt_value) {
				abstract_annotations.add(CirAnnotation.trp_stmt(execution));
				return;
			}
			else if(value instanceof SymbolConstant) {
				values.add(value);
			}
		}
		Collection<SymbolExpression> scopes = CirAnnotationValue.find_scopes(expression, values);
		
		/* E-2. summarize the abstract scopes from concrete annotations */
		for(SymbolExpression scope : scopes) {
			abstract_annotations.add(CirAnnotation.ext_diff(expression, scope));
		}
	}
	/**
	 * It summarizes the abstract annotations from symbolic and concrete ones.
	 * @param annotation
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in(CirAnnotation annotation,
			Collection<CirAnnotation> concrete_annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		if(annotation == null) {
			throw new IllegalArgumentException("Invalid annotation as null");
		}
		else if(concrete_annotations == null) {
			throw new IllegalArgumentException("Invalid concrete_annotations");
		}
		else if(abstract_annotations == null) {
			throw new IllegalArgumentException("Invalid abstract_annotations");
		}
		else {
			switch(annotation.get_value_type()) {
			case cov_stmt:	this.summarize_annotations_in_cov_stmt(annotation, concrete_annotations, abstract_annotations); break;
			case eva_expr:	this.summarize_annotations_in_eva_expr(annotation, concrete_annotations, abstract_annotations); break;
			case trp_stmt:	this.summarize_annotations_in_trp_stmt(annotation, concrete_annotations, abstract_annotations); break;
			case ori_stmt:	this.summarize_annotations_in_ori_stmt(annotation, concrete_annotations, abstract_annotations); break;
			case mut_stmt:	this.summarize_annotations_in_mut_stmt(annotation, concrete_annotations, abstract_annotations); break;
			case ori_expr:	this.summarize_annotations_in_ori_expr(annotation, concrete_annotations, abstract_annotations); break;
			case mut_expr:	this.summarize_annotations_in_mut_expr(annotation, concrete_annotations, abstract_annotations); break;
			case cmp_diff:	this.summarize_annotations_in_cmp_diff(annotation, concrete_annotations, abstract_annotations); break;
			case sub_diff:	this.summarize_annotations_in_sub_diff(annotation, concrete_annotations, abstract_annotations); break;
			case xor_diff:	this.summarize_annotations_in_xor_diff(annotation, concrete_annotations, abstract_annotations); break;
			case ext_diff:	this.summarize_annotations_in_ext_diff(annotation, concrete_annotations, abstract_annotations); break;
			default:		throw new IllegalArgumentException("Unsupport: " + annotation);
			}
		}
	}
	/**
	 * It summarizes the abstract annotations from symbolic and concrete ones.
	 * @param annotation
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	protected static void summarize_annotations(CirAnnotation annotation,
			Collection<CirAnnotation> concrete_annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		utils.summarize_annotations_in(annotation, concrete_annotations, abstract_annotations);
	}
	
}
