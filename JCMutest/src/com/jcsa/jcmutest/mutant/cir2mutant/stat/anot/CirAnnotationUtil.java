package com.jcsa.jcmutest.mutant.cir2mutant.stat.anot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionEdge;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionPath;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionPathFinder;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.lang.irlang.graph.CirFunctionCallGraph;
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
 * It implements the generation, concretization and summarization of CirAnnotation
 * from CirAttribute or CirAnnotation itself.
 * 
 * @author yukimula
 *
 */
final class CirAnnotationUtil {
	
	
	/* singleton */	/** private creator **/	private CirAnnotationUtil() {}
	private static final CirAnnotationUtil util = new CirAnnotationUtil();
	
	
	/* basic methods */
	/**
	 * It recursively derives the sub_conditions from input condition into the given collection.
	 * @param condition
	 * @param conditions
	 * @throws Exception
	 */
	private void get_conditions_in(SymbolExpression condition, Collection<SymbolExpression> conditions) throws Exception {
		if(condition instanceof SymbolConstant) {
			if(((SymbolConstant) condition).get_bool()) {
				/* TRUE operands in conjunctive expression are useless! */
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
				this.get_conditions_in(loperand, conditions);
				this.get_conditions_in(roperand, conditions);
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
	 * @param condition
	 * @return the set of sub-conditions in the input expression when taking it as conjunctive logic
	 * @throws Exception
	 */
	private Collection<SymbolExpression> get_conditions_in(SymbolExpression condition) throws Exception {
		Set<SymbolExpression> conditions = new HashSet<SymbolExpression>();
		this.get_conditions_in(condition, conditions);
		return conditions;
	}
	/**
	 * It recursively derives the reference expressions defined in the symbolic node context.
	 * @param node
	 * @param references
	 * @throws Exception
	 */
	private void get_references_in(SymbolNode node, Collection<SymbolExpression> references) throws Exception {
		if(node.is_reference()) {
			references.add((SymbolExpression) node);
		}
		for(SymbolNode child : node.get_children()) {
			this.get_references_in(child, references);
		}
	}
	/**
	 * @param node
	 * @return the set of reference expressions incorporated in the context of input symbolic node
	 * @throws Exception
	 */
	private Collection<SymbolExpression> get_references_in(SymbolNode node) throws Exception {
		Set<SymbolExpression> references = new HashSet<SymbolExpression>();
		this.get_references_in(node, references);
		return references;
	}
	/**
	 * @param node
	 * @param references
	 * @return whether any nodes in the input context contain the given references collection.
	 * @throws Exception
	 */
	private boolean has_references_in(SymbolNode node, Collection<SymbolExpression> references) throws Exception {
		if(references.contains(node)) {
			return true;	/* the reference is contained in node */
		}
		else {				/* recursively search under the child */
			for(SymbolNode child : node.get_children()) {
				if(this.has_references_in(child, references)) {
					return true;
				}
			}
			return false;
		}
	}
	/**
	 * @param statement
	 * @param references
	 * @return whether any references are defined in the given statement
	 * @throws Exception
	 */
	private boolean has_references_in(CirExecution execution, Collection<SymbolExpression> references) throws Exception {
		CirStatement statement = execution.get_statement();
		if(references == null || references.isEmpty()) {
			return false;	/* not included for empty set */
		}
		else if(statement instanceof CirAssignStatement) {
			SymbolExpression lvalue = SymbolFactory.sym_expression(
					((CirAssignStatement) statement).get_lvalue());
			return references.contains(lvalue);	/* being assigned */
		}
		else if(statement instanceof CirIfStatement) {
			SymbolExpression condition = SymbolFactory.sym_expression(
					((CirIfStatement) statement).get_condition());
			return this.has_references_in(condition, references);
		}
		else if(statement instanceof CirCaseStatement) {
			SymbolExpression condition = SymbolFactory.sym_expression(
					((CirCaseStatement) statement).get_condition());
			return this.has_references_in(condition, references);
		}
		else {
			return false;
		}
	}
	/**
	 * @param execution
	 * @param condition
	 * @return the prior point where the condition could be evaluated before the given point
	 * @throws Exception
	 */
	private CirExecution get_prior_checkpoint(CirExecution execution, Object condition) throws Exception {
		/* generate the symbolic expression for analysis */
		SymbolExpression expression = SymbolFactory.sym_expression(condition);
		expression = CirValueScope.safe_evaluate(expression, null);
		
		/* A. in constant case, return the prior decidable point */
		if(expression instanceof SymbolConstant) {
			if(((SymbolConstant) expression).get_bool()) {
				CirExecutionPath path = CirExecutionPathFinder.finder.db_extend(execution);
				Iterator<CirExecutionEdge> iterator = path.get_iterator(true);
				while(iterator.hasNext()) {
					CirExecutionEdge edge = iterator.next();
					CirStatement statement = edge.get_target().get_statement();
					if(statement instanceof CirIfStatement
						|| statement instanceof CirCaseStatement) {
						return edge.get_target();
					}
				}
				return path.get_source();
			}
			else {
				return execution.get_graph().get_entry();
			}
		}
		/* B. otherwise, move backward until the definition node is found */
		else {
			Collection<SymbolExpression> references = this.get_references_in(expression);
			CirExecutionPath path = CirExecutionPathFinder.finder.db_extend(execution);
			Iterator<CirExecutionEdge> iterator = path.get_iterator(true);
			while(iterator.hasNext()) {
				CirExecutionEdge edge = iterator.next();
				if(this.has_references_in(edge.get_source(), references)) {
					return edge.get_target();
				}
			}
			return path.get_source();
		}
		
	}
	/**
	 * @param orig_target
	 * @param muta_target
	 * @return true --> add_executions; false --> del_executions;
	 * @throws Exception
	 */
	private Map<Boolean, Collection<CirExecution>> infer_flag_executions(CirExecution orig_target, CirExecution muta_target) throws Exception {
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
	/**
	 * @param max_exec_time
	 * @return [1, 2, 4, 8, ..., max_exec_time]
	 */
	private List<Integer> get_execution_times_util(int max_exec_time) {
		List<Integer> times = new ArrayList<Integer>();
		for(int k = 1; k < max_exec_time; k = k * 2) {
			times.add(Integer.valueOf(k));
		}
		times.add(Integer.valueOf(max_exec_time));
		return times;
	}
	
	
	/* generation methods */
	/**
	 * cov_stmt(execution.prior_checkpoint, execute_times)
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_cover_count(CirCoverCount attribute, Collection<CirAnnotation> annotations) throws Exception {
		/* 1. derive the execution and execution times element */
		CirExecution execution = attribute.get_execution();
		int execute_times = attribute.get_coverage_count();
		
		/* 2. generate the execution times under maximal times */
		List<Integer> times = this.get_execution_times_util(execute_times);
		execution = this.get_prior_checkpoint(execution, Boolean.TRUE);
		
		/* 3. cov_stmt(check_point, times) {1, 2, 4, 8, 16, ... max_times} */
		for(Integer time : times) {
			annotations.add(CirAnnotation.cov_stmt(execution, time.intValue()));
		}
	}
	/**
	 * eva_expr(execution.prior_checkpoint, condition.optimize(null))
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_constraints(CirConstraint attribute, Collection<CirAnnotation> annotations) throws Exception {
		/* 1. derive the original execution and symbolic constraint */
		CirExecution execution = attribute.get_execution(), check_point;
		SymbolExpression condition = attribute.get_condition();
		
		/* 2. extract the sub_conditions from the constraint for analysis */
		Collection<SymbolExpression> sub_conditions = this.
				get_conditions_in(CirValueScope.safe_evaluate(condition, null));
		
		/* 3. generate eva_expr(prior_checkpoint, optimal_condition) */
		for(SymbolExpression sub_condition : sub_conditions) {
			check_point = this.get_prior_checkpoint(execution, sub_condition);
			annotations.add(CirAnnotation.eva_expr(check_point, sub_condition));
		}
		
		/* 4. in case that it is TRUE, transform as coverage requirement */
		if(sub_conditions.isEmpty()) {
			check_point = this.get_prior_checkpoint(execution, Boolean.TRUE);
			annotations.add(CirAnnotation.cov_stmt(check_point, 1));
		}
	}
	/**
	 * mut_stmt{execution}([stmt:statement], [bool:exec_flag])
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_block_error(CirBlockError attribute, Collection<CirAnnotation> annotations) throws Exception {
		CirExecution execution = attribute.get_execution();
		Boolean exec_flag = Boolean.valueOf(attribute.is_executed());
		if(execution.get_statement() instanceof CirTagStatement) { }
		else {
			annotations.add(CirAnnotation.mut_stmt(execution, exec_flag));
		}
	}
	/**
	 * mut_stmt{next_target}([stmt:statement], [bool:exec_flag])*
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_flows_error(CirFlowsError attribute, Collection<CirAnnotation> annotations) throws Exception {
		Map<Boolean, Collection<CirExecution>> maps = this.infer_flag_executions(
				attribute.get_original_flow().get_target(), 
				attribute.get_mutation_flow().get_target());
		
		for(Boolean exec_flag : maps.keySet()) {
			for(CirExecution execution : maps.get(exec_flag)) {
				if(execution.get_statement() instanceof CirTagStatement) {
					continue;
				}
				else {
					annotations.add(CirAnnotation.mut_stmt(execution, exec_flag));
				}
			}
		}
	}
	/**
	 * trap_stmt(exit, mut_value)
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_traps_error(CirTrapsError attribute, Collection<CirAnnotation> annotations) throws Exception {
		annotations.add(CirAnnotation.trp_stmt(attribute.get_execution()));
	}
	/**
	 * It generates the annotations for representing the error to replace expression with muta_value
	 * @param expression
	 * @param muta_value
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_exprs_error(CirExpression expression, 
			Object value, Collection<CirAnnotation> annotations) throws Exception {
		/* 1. initialize the original and mutated values */
		CirAnnotation orig_annotation, muta_annotation;
		if(CirMutations.is_assigned(expression)) {
			CirAssignStatement statement = (CirAssignStatement) expression.get_parent();
			orig_annotation = CirAnnotation.mut_refr(expression, statement.get_rvalue());
			muta_annotation = CirAnnotation.mut_refr(expression, value);
		}
		else {
			orig_annotation = CirAnnotation.mut_expr(expression, expression);
			muta_annotation = CirAnnotation.mut_expr(expression, value);
		}
		SymbolExpression orig_value = orig_annotation.get_symb_value();
		SymbolExpression muta_value = muta_annotation.get_symb_value();
		CirExecution execution = expression.execution_of();
		
		/* 2. generate trp-error if trapping actually occurs there */
		if(orig_value == CirValueScope.expt_value || muta_value == CirValueScope.expt_value) {
			annotations.add(CirAnnotation.trp_stmt(execution));
			return;
		}
		/* 3. none of annotation is generated if equivalent occurs */
		else if(orig_value.equals(muta_value)) { return; }
		/* 4. otherwise, insert the mutated annotation in outcomes */
		else {
			annotations.add(muta_annotation);
		}
		
		/* 5. generate differentiated annotations for specified type */
		SymbolExpression difference;
		if(CirMutations.is_numeric(expression) && CirMutations.is_numeric(orig_value.get_data_type())) {
			difference = CirValueScope.sub_differentiate(orig_value, muta_value);
			annotations.add(CirAnnotation.sub_diff(expression, difference));
			
			difference = CirValueScope.ext_differentiate(orig_value, muta_value);
			annotations.add(CirAnnotation.ext_diff(expression, difference));
		}
		if(CirMutations.is_address(expression) && CirMutations.is_address(orig_value.get_data_type())) {
			difference = CirValueScope.sub_differentiate(orig_value, muta_value);
			annotations.add(CirAnnotation.sub_diff(expression, difference));
		}
		if(CirMutations.is_integer(expression) && CirMutations.is_integer(orig_value.get_data_type())) {
			difference = CirValueScope.xor_differentiate(orig_value, muta_value);
			annotations.add(CirAnnotation.xor_diff(expression, difference));
		}
	}
	/**
	 * --> {mut_expr|mut_refr} {sub_diff, xor_diff, ext_diff}*
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_difer_error(CirDiferError attribute, Collection<CirAnnotation> annotations) throws Exception {
		this.generate_annotations_in_exprs_error(attribute.get_orig_expression(), attribute.get_muta_expression(), annotations);
	}
	/**
	 * --> {mut_expr|mut_refr} {sub_diff, xor_diff, ext_diff}*
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_value_error(CirValueError attribute, Collection<CirAnnotation> annotations) throws Exception {
		this.generate_annotations_in_exprs_error(attribute.get_orig_expression(), attribute.get_muta_expression(), annotations);
	}
	/**
	 * --> {mut_expr|mut_refr} {sub_diff, xor_diff, ext_diff}*
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_refer_error(CirReferError attribute, Collection<CirAnnotation> annotations) throws Exception {
		this.generate_annotations_in_exprs_error(attribute.get_orig_expression(), attribute.get_muta_expression(), annotations);
	}
	/**
	 * --> {mut_expr|mut_refr} {sub_diff, xor_diff, ext_diff}*
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_state_error(CirStateError attribute, Collection<CirAnnotation> annotations) throws Exception {
		this.generate_annotations_in_exprs_error(attribute.get_orig_expression(), attribute.get_muta_expression(), annotations);
	}
	/**
	 * It generates the symbolic representative annotations for given attribute
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
	 * It generates the symbolic representative annotations for given attribute
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	protected static void generate_annotations(CirAttribute attribute, Collection<CirAnnotation> annotations) throws Exception {
		util.generate_annotations_in(attribute, annotations);
	}
	
	
	/* concretize methods */
	/**
	 * do nothing on operation
	 * @param annotation
	 * @param annotations
	 * @throws Exception
	 */
	private void concretize_annotations_in_cov_stmt(CirAnnotation annotation, SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception { }
	/**
	 * do nothing on operation
	 * @param annotation
	 * @param annotations
	 * @throws Exception
	 */
	private void concretize_annotations_in_eva_expr(CirAnnotation annotation, SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception { }
	/**
	 * do nothing on operation
	 * @param annotation
	 * @param annotations
	 * @throws Exception
	 */
	private void concretize_annotations_in_trp_stmt(CirAnnotation annotation, SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception { }
	/**
	 * simply copy to the 
	 * @param annotation
	 * @param annotations
	 * @throws Exception
	 */
	private void concretize_annotations_in_mut_stmt(CirAnnotation annotation, SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception {
		CirExecution execution = annotation.get_execution();
		SymbolExpression value = annotation.get_symb_value();
		value = CirValueScope.safe_evaluate(value, context);
		if(value instanceof SymbolConstant) {
			if(((SymbolConstant) value).get_bool()) {
				annotations.add(CirAnnotation.mut_stmt(execution, Boolean.TRUE));
			}
			else {
				annotations.add(CirAnnotation.mut_stmt(execution, Boolean.FALSE));
			}
		}
	}
	/**
	 * simply copy to the outputs
	 * @param annotation
	 * @param context
	 * @param annotations
	 * @throws Exception
	 */
	private void concretize_annotations_in_mut_expr(CirAnnotation annotation, SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception {
		CirExpression expression = (CirExpression) annotation.get_store_unit();
		SymbolExpression value = CirValueScope.safe_evaluate(annotation.get_symb_value(), context);
		if(value == CirValueScope.expt_value) {
			annotations.add(CirAnnotation.trp_stmt(annotation.get_execution()));
		}
		else if(value instanceof SymbolConstant) {
			annotations.add(CirAnnotation.mut_expr(expression, value));
		}
	}
	/**
	 * simply copy to the outputs
	 * @param annotation
	 * @param context
	 * @param annotations
	 * @throws Exception
	 */
	private void concretize_annotations_in_mut_refr(CirAnnotation annotation, SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception {
		CirExpression expression = (CirExpression) annotation.get_store_unit();
		SymbolExpression value = CirValueScope.safe_evaluate(annotation.get_symb_value(), context);
		if(value == CirValueScope.expt_value) {
			annotations.add(CirAnnotation.trp_stmt(annotation.get_execution()));
		}
		else if(value instanceof SymbolConstant) {
			annotations.add(CirAnnotation.mut_refr(expression, value));
		}
	}
	/**
	 * simply copy to the outputs
	 * @param annotation
	 * @param context
	 * @param annotations
	 * @throws Exception
	 */
	private void concretize_annotations_in_sub_diff(CirAnnotation annotation, SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception {
		CirExpression expression = (CirExpression) annotation.get_store_unit();
		SymbolExpression value = CirValueScope.safe_evaluate(annotation.get_symb_value(), context);
		if(value == CirValueScope.expt_value) {
			annotations.add(CirAnnotation.trp_stmt(annotation.get_execution()));
		}
		else if(value instanceof SymbolConstant) {
			annotations.add(CirAnnotation.sub_diff(expression, value));
		}
	}
	/**
	 * simply copy to the outputs
	 * @param annotation
	 * @param context
	 * @param annotations
	 * @throws Exception
	 */
	private void concretize_annotations_in_ext_diff(CirAnnotation annotation, SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception {
		CirExpression expression = (CirExpression) annotation.get_store_unit();
		SymbolExpression value = CirValueScope.safe_evaluate(annotation.get_symb_value(), context);
		if(value == CirValueScope.expt_value) {
			annotations.add(CirAnnotation.trp_stmt(annotation.get_execution()));
		}
		else if(value instanceof SymbolConstant) {
			annotations.add(CirAnnotation.ext_diff(expression, value));
		}
	}
	/**
	 * simply copy to the outputs
	 * @param annotation
	 * @param context
	 * @param annotations
	 * @throws Exception
	 */
	private void concretize_annotations_in_xor_diff(CirAnnotation annotation, SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception {
		CirExpression expression = (CirExpression) annotation.get_store_unit();
		SymbolExpression value = CirValueScope.safe_evaluate(annotation.get_symb_value(), context);
		if(value == CirValueScope.expt_value) {
			annotations.add(CirAnnotation.trp_stmt(annotation.get_execution()));
		}
		else if(value instanceof SymbolConstant) {
			annotations.add(CirAnnotation.xor_diff(expression, value));
		}
	}
	/**
	 * It concretize the input annotation using the given context and put into the output collection
	 * @param annotation
	 * @param context
	 * @param annotations
	 * @throws Exception
	 */
	private void concretize_annotations_in(CirAnnotation annotation, SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception {
		if(annotation == null) {
			throw new IllegalArgumentException("Invalid annotation as null");
		}
		else if(annotations == null) {
			throw new IllegalArgumentException("Invalid annotations: null");
		}
		else {
			switch(annotation.get_logic_type()) {
			case cov_stmt:	this.concretize_annotations_in_cov_stmt(annotation, context, annotations); break;
			case eva_expr:	this.concretize_annotations_in_eva_expr(annotation, context, annotations); break;
			case trp_stmt:	this.concretize_annotations_in_trp_stmt(annotation, context, annotations); break;
			case mut_stmt:	this.concretize_annotations_in_mut_stmt(annotation, context, annotations); break;
			case mut_expr:	this.concretize_annotations_in_mut_expr(annotation, context, annotations); break;
			case mut_refr:	this.concretize_annotations_in_mut_refr(annotation, context, annotations); break;
			case sub_diff:	this.concretize_annotations_in_sub_diff(annotation, context, annotations); break;
			case ext_diff:	this.concretize_annotations_in_ext_diff(annotation, context, annotations); break;
			case xor_diff:	this.concretize_annotations_in_xor_diff(annotation, context, annotations); break;
			default:		throw new IllegalArgumentException("Invalid annotation:" + annotation.toString());
			}
		}
	}
	/**
	 * It concretize the input annotation using the given context and put into the output collection
	 * @param annotation
	 * @param context
	 * @param annotations
	 * @throws Exception
	 */
	protected static void concretize_annotations(CirAnnotation annotation, SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception {
		util.concretize_annotations_in(annotation, context, annotations);
	}
	
	
	/* summarize methods */
	/**
	 * simply copy the source annotation to output
	 * @param annotation
	 * @param con_annotations
	 * @param abs_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_cov_stmt(CirAnnotation annotation, 
			Collection<CirAnnotation> con_annotations,
			Collection<CirAnnotation> abs_annotations) throws Exception { abs_annotations.add(annotation); }
	/**
	 * simply copy the source annotation to output
	 * @param annotation
	 * @param con_annotations
	 * @param abs_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_eva_expr(CirAnnotation annotation, 
			Collection<CirAnnotation> con_annotations,
			Collection<CirAnnotation> abs_annotations) throws Exception { abs_annotations.add(annotation); }
	/**
	 * simply copy the source annotation to output
	 * @param annotation
	 * @param con_annotations
	 * @param abs_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_trp_stmt(CirAnnotation annotation, 
			Collection<CirAnnotation> con_annotations,
			Collection<CirAnnotation> abs_annotations) throws Exception { abs_annotations.add(annotation); }
	/**
	 * @param annotation
	 * @param con_annotations
	 * @return the set of abstract value scopes summarized from annotation and its concrete values
	 * @throws Exception
	 */
	private Collection<SymbolExpression> summarize_abstract_scopes(
			CirAnnotation annotation, Collection<CirAnnotation> con_annotations) throws Exception {
		/* 1. capture the concrete values from the annotations */
		List<SymbolExpression> values = new ArrayList<SymbolExpression>();
		for(CirAnnotation con_annotation : con_annotations) {
			values.add(con_annotation.get_symb_value());
		}
		
		/* 2. summarize from the concrete values if not empty */
		if(!values.isEmpty()) {
			return CirValueScope.sum_value_scopes_in(annotation.get_value_type(), values);
		}
		/* 3. otherwise, insert the general scopes via types */
		else {
			Collection<SymbolExpression> scopes = new HashSet<SymbolExpression>();
			switch(annotation.get_value_type()) {
			case bool:	scopes.add(CirValueScope.bool_value); break;
			case usig:	scopes.add(CirValueScope.nneg_value); break;
			case sign:	scopes.add(CirValueScope.numb_value); break;
			case real:	scopes.add(CirValueScope.numb_value); break;
			case addr:	scopes.add(CirValueScope.addr_value); break;
			default:	break;
			}
			return scopes;
		}
	}
	/**
	 * It summarizes from the concrete values and representative
	 * @param annotation
	 * @param con_annotations
	 * @param abs_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_mut_stmt(CirAnnotation annotation, 
			Collection<CirAnnotation> con_annotations,
			Collection<CirAnnotation> abs_annotations) throws Exception {
		Collection<SymbolExpression> scopes = this.summarize_abstract_scopes(annotation, con_annotations);
		
		if(scopes.contains(CirValueScope.expt_value)) {
			abs_annotations.add(CirAnnotation.trp_stmt(annotation.get_execution()));
		}
		else if(scopes.isEmpty()) {
			abs_annotations.add(annotation);
		}
		else {
			for(SymbolExpression scope : scopes) {
				abs_annotations.add(CirAnnotation.mut_stmt(annotation.get_execution(), scope));
			}
		}
	}
	/**
	 * It summarizes from the concrete values and representative
	 * @param annotation
	 * @param con_annotations
	 * @param abs_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_mut_expr(CirAnnotation annotation, 
			Collection<CirAnnotation> con_annotations,
			Collection<CirAnnotation> abs_annotations) throws Exception {
		CirExpression expression = (CirExpression) annotation.get_store_unit();
		Collection<SymbolExpression> scopes = this.summarize_abstract_scopes(annotation, con_annotations);
		
		if(scopes.contains(CirValueScope.expt_value)) {
			abs_annotations.add(CirAnnotation.trp_stmt(annotation.get_execution()));
		}
		else if(scopes.isEmpty()) {
			abs_annotations.add(annotation);
		}
		else {
			for(SymbolExpression scope : scopes) 
				abs_annotations.add(CirAnnotation.mut_expr(expression, scope));
			abs_annotations.add(annotation);
		}
	}
	/**
	 * It summarizes from the concrete values and representative
	 * @param annotation
	 * @param con_annotations
	 * @param abs_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_mut_refr(CirAnnotation annotation, 
			Collection<CirAnnotation> con_annotations,
			Collection<CirAnnotation> abs_annotations) throws Exception {
		CirExpression expression = (CirExpression) annotation.get_store_unit();
		Collection<SymbolExpression> scopes = this.summarize_abstract_scopes(annotation, con_annotations);
		
		if(scopes.contains(CirValueScope.expt_value)) {
			abs_annotations.add(CirAnnotation.trp_stmt(annotation.get_execution()));
		}
		else if(scopes.isEmpty()) {
			abs_annotations.add(annotation);
		}
		else {
			for(SymbolExpression scope : scopes) 
				abs_annotations.add(CirAnnotation.mut_refr(expression, scope));
			abs_annotations.add(annotation);
		}
	}
	/**
	 * It summarizes from the concrete values and representative
	 * @param annotation
	 * @param con_annotations
	 * @param abs_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_sub_diff(CirAnnotation annotation, 
			Collection<CirAnnotation> con_annotations,
			Collection<CirAnnotation> abs_annotations) throws Exception {
		CirExpression expression = (CirExpression) annotation.get_store_unit();
		Collection<SymbolExpression> scopes = this.summarize_abstract_scopes(annotation, con_annotations);
		
		if(scopes.contains(CirValueScope.expt_value)) {
			abs_annotations.add(CirAnnotation.trp_stmt(annotation.get_execution()));
		}
		else if(scopes.isEmpty()) {
			// abs_annotations.add(annotation);
		}
		else {
			for(SymbolExpression scope : scopes) 
				abs_annotations.add(CirAnnotation.sub_diff(expression, scope));
		}
	}
	/**
	 * It summarizes from the concrete values and representative
	 * @param annotation
	 * @param con_annotations
	 * @param abs_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_ext_diff(CirAnnotation annotation, 
			Collection<CirAnnotation> con_annotations,
			Collection<CirAnnotation> abs_annotations) throws Exception {
		CirExpression expression = (CirExpression) annotation.get_store_unit();
		Collection<SymbolExpression> scopes = this.summarize_abstract_scopes(annotation, con_annotations);
		
		if(scopes.contains(CirValueScope.expt_value)) {
			abs_annotations.add(CirAnnotation.trp_stmt(annotation.get_execution()));
		}
		else if(scopes.isEmpty()) {
			// abs_annotations.add(annotation);
		}
		else {
			for(SymbolExpression scope : scopes) 
				abs_annotations.add(CirAnnotation.ext_diff(expression, scope));
		}
	}
	/**
	 * It summarizes from the concrete values and representative
	 * @param annotation
	 * @param con_annotations
	 * @param abs_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_xor_diff(CirAnnotation annotation, 
			Collection<CirAnnotation> con_annotations,
			Collection<CirAnnotation> abs_annotations) throws Exception {
		CirExpression expression = (CirExpression) annotation.get_store_unit();
		Collection<SymbolExpression> scopes = this.summarize_abstract_scopes(annotation, con_annotations);
		if(scopes.contains(CirValueScope.expt_value)) {
			abs_annotations.add(CirAnnotation.trp_stmt(annotation.get_execution()));
		}
		else if(scopes.isEmpty()) {
			// abs_annotations.add(annotation);
		}
		else {
			for(SymbolExpression scope : scopes) 
				abs_annotations.add(CirAnnotation.xor_diff(expression, scope));
		}
	}
	/**
	 * It summarizes from the representative and concrete annotations
	 * @param annotation
	 * @param con_annotations
	 * @param abs_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in(CirAnnotation annotation,
			Collection<CirAnnotation> con_annotations,
			Collection<CirAnnotation> abs_annotations) throws Exception {
		if(annotation == null) {
			throw new IllegalArgumentException("Invalid annotation: null");
		}
		else if(con_annotations == null) {
			throw new IllegalArgumentException("Invalid annotations: null");
		}
		else if(abs_annotations == null) {
			throw new IllegalArgumentException("Invalid annotations: null");
		}
		else {
			switch(annotation.get_logic_type()) {
			case cov_stmt:	this.summarize_annotations_in_cov_stmt(annotation, con_annotations, abs_annotations); break;
			case eva_expr:	this.summarize_annotations_in_eva_expr(annotation, con_annotations, abs_annotations); break;
			case trp_stmt:	this.summarize_annotations_in_trp_stmt(annotation, con_annotations, abs_annotations); break;
			case mut_stmt:	this.summarize_annotations_in_mut_stmt(annotation, con_annotations, abs_annotations); break;
			case mut_expr:	this.summarize_annotations_in_mut_expr(annotation, con_annotations, abs_annotations); break;
			case mut_refr:	this.summarize_annotations_in_mut_refr(annotation, con_annotations, abs_annotations); break;
			case sub_diff:	this.summarize_annotations_in_sub_diff(annotation, con_annotations, abs_annotations); break;
			case ext_diff:	this.summarize_annotations_in_ext_diff(annotation, con_annotations, abs_annotations); break;
			case xor_diff:	this.summarize_annotations_in_xor_diff(annotation, con_annotations, abs_annotations); break;
			default:		throw new IllegalArgumentException("Unsupport annotation: " + annotation.toString());
			}
		}
	}
	/**
	 * It summarizes from the representative and concrete annotations
	 * @param annotation
	 * @param con_annotations
	 * @param abs_annotations
	 * @throws Exception
	 */
	protected static void summarize_annotations(CirAnnotation annotation,
			Collection<CirAnnotation> con_annotations,
			Collection<CirAnnotation> abs_annotations) throws Exception {
		util.summarize_annotations_in(annotation, con_annotations, abs_annotations);
	}
	
	
	/* subsume methods */
	/**
	 * It extends from the input condition to the ones it directly subsumes in the logical space
	 * @param condition
	 * @param conditions
	 * @throws Exception
	 */
	private void extend_subsumed_conditions_from(SymbolExpression condition, 
				Collection<SymbolExpression> conditions) throws Exception {
		if(condition instanceof SymbolConstant) {
			if(((SymbolConstant) condition).get_bool()) {
				/* ignore the sub-conditions for true operand in condition */
			}
			else {
				conditions.add(SymbolFactory.sym_constant(Boolean.TRUE));
			}
		}
		else if(condition instanceof SymbolBinaryExpression) {
			COperator op = ((SymbolBinaryExpression) condition).get_operator().get_operator();
			SymbolExpression loperand = ((SymbolBinaryExpression) condition).get_loperand();
			SymbolExpression roperand = ((SymbolBinaryExpression) condition).get_roperand();
			
			if(op == COperator.logic_and) {
				this.extend_subsumed_conditions_from(loperand, conditions);
				this.extend_subsumed_conditions_from(roperand, conditions);
			}
			else if(op == COperator.logic_or) {
				/* disjunctive expression will be subsumed by its sub_condition */
				conditions.add(SymbolFactory.sym_constant(Boolean.TRUE));
			}
			else if(op == COperator.smaller_tn) {
				conditions.add(SymbolFactory.smaller_eq(loperand, roperand));
				conditions.add(SymbolFactory.not_equals(loperand, roperand));
				if(loperand instanceof SymbolConstant) {
					long lvalue = ((SymbolConstant) loperand).get_long().longValue();
					if(lvalue > 0) {
						conditions.add(SymbolFactory.smaller_tn(0, roperand));
					}
				}
				else if(roperand instanceof SymbolConstant) {
					long rvalue = ((SymbolConstant) roperand).get_long().longValue();
					if(rvalue < 0) {
						conditions.add(SymbolFactory.smaller_tn(loperand, 0));
					}
				}
			}
			else if(op == COperator.smaller_eq) {
				if(loperand instanceof SymbolConstant) {
					long lvalue = ((SymbolConstant) loperand).get_long().longValue();
					if(lvalue > 0) {
						conditions.add(SymbolFactory.smaller_tn(0, roperand));
					}
				}
				else if(roperand instanceof SymbolConstant) {
					long rvalue = ((SymbolConstant) roperand).get_long().longValue();
					if(rvalue < 0) {
						conditions.add(SymbolFactory.smaller_tn(loperand, 0));
					}
				}
				if(conditions.isEmpty()) {
					conditions.add(SymbolFactory.sym_constant(Boolean.TRUE));
				}
			}
			else if(op == COperator.equal_with) {
				conditions.add(SymbolFactory.smaller_eq(loperand, roperand));
				conditions.add(SymbolFactory.greater_eq(loperand, roperand));
				if(loperand instanceof SymbolConstant) {
					long lvalue = ((SymbolConstant) loperand).get_long().longValue();
					if(lvalue != 0) {
						conditions.add(SymbolFactory.not_equals(0, roperand));
					}
				}
				else if(roperand instanceof SymbolConstant) {
					long rvalue = ((SymbolConstant) roperand).get_long().longValue();
					if(rvalue != 0) {
						conditions.add(SymbolFactory.not_equals(loperand, 0));
					}
				}
			}
			else {
				conditions.add(SymbolFactory.sym_constant(Boolean.TRUE));
			}
		}
		else {
			conditions.add(SymbolFactory.sym_constant(Boolean.TRUE));
		}
	}
	/**
	 * It infers the set of expressions subsumed from the input (boolean) expression based on abstract domain analysis
	 * @param type
	 * @param expression
	 * @param expressions
	 * @throws Exception
	 */
	private void extend_subsumed_scopes_from_bool(SymbolExpression expression, Collection<SymbolExpression> expressions) throws Exception {
		if(expression instanceof SymbolConstant) {
			if(((SymbolConstant) expression).get_bool()) {
				expressions.add(CirValueScope.true_value);
			}
			else {
				expressions.add(CirValueScope.fals_value);
			}
		}
		else if(expression == CirValueScope.true_value) {
			expressions.add(CirValueScope.bool_value);
		}
		else if(expression == CirValueScope.fals_value) {
			expressions.add(CirValueScope.bool_value);
		}
		else if(expression == CirValueScope.bool_value) { }
		else { 
			expressions.add(CirValueScope.bool_value);
		}
	}
	/**
	 * It infers the set of expressions subsumed from the input (unsigned) expression based on abstract domain analysis
	 * @param expression
	 * @param expressions
	 * @throws Exception
	 */
	private void extend_subsumed_scopes_from_usig(SymbolExpression expression, Collection<SymbolExpression> expressions) throws Exception {
		if(expression instanceof SymbolConstant) {
			if(((SymbolConstant) expression).get_long() != 0) {
				expressions.add(CirValueScope.post_value);
			}
			else {
				expressions.add(CirValueScope.zero_value);
			}
		}
		else if(expression == CirValueScope.post_value) {
			expressions.add(CirValueScope.nneg_value);
		}
		else if(expression == CirValueScope.zero_value) {
			expressions.add(CirValueScope.nneg_value);
		}
		else if(expression == CirValueScope.nneg_value) { }
		else {
			expressions.add(CirValueScope.nneg_value);
		}
	}
	/**
	 * It infers the set of expressions subsumed from the input (integer) expression based on abstract domain analysis.
	 * @param expression
	 * @param expressions
	 * @throws Exception
	 */
	private void extend_subsumed_scopes_from_sign(SymbolExpression expression, Collection<SymbolExpression> expressions) throws Exception {
		if(expression instanceof SymbolConstant) {
			if(((SymbolConstant) expression).get_long() > 0) {
				expressions.add(CirValueScope.post_value);
			}
			else if(((SymbolConstant) expression).get_long() < 0) {
				expressions.add(CirValueScope.negt_value);
			}
			else {
				expressions.add(CirValueScope.zero_value);
			}
		}
		else if(expression == CirValueScope.post_value) {
			expressions.add(CirValueScope.nneg_value);
			expressions.add(CirValueScope.nzro_value);
		}
		else if(expression == CirValueScope.zero_value) {
			expressions.add(CirValueScope.nneg_value);
			expressions.add(CirValueScope.npos_value);
		}
		else if(expression == CirValueScope.negt_value) {
			expressions.add(CirValueScope.npos_value);
			expressions.add(CirValueScope.nzro_value);
		}
		else if(expression == CirValueScope.npos_value) {
			expressions.add(CirValueScope.numb_value);
		}
		else if(expression == CirValueScope.nzro_value) {
			expressions.add(CirValueScope.numb_value);
		}
		else if(expression == CirValueScope.nneg_value) {
			expressions.add(CirValueScope.numb_value);
		}
		else if(expression == CirValueScope.numb_value) { }
		else {
			expressions.add(CirValueScope.numb_value);
		}
	}
	/**
	 * It infers the set of expressions subsumed from the input (double) expression based on abstract domain analysis.
	 * @param expression
	 * @param expressions
	 * @throws Exception
	 */
	private void extend_subsumed_scopes_from_real(SymbolExpression expression, Collection<SymbolExpression> expressions) throws Exception {
		if(expression instanceof SymbolConstant) {
			if(((SymbolConstant) expression).get_double() > 0) {
				expressions.add(CirValueScope.post_value);
			}
			else if(((SymbolConstant) expression).get_double() < 0) {
				expressions.add(CirValueScope.negt_value);
			}
			else {
				expressions.add(CirValueScope.zero_value);
			}
		}
		else if(expression == CirValueScope.post_value) {
			expressions.add(CirValueScope.nneg_value);
			expressions.add(CirValueScope.nzro_value);
		}
		else if(expression == CirValueScope.zero_value) {
			expressions.add(CirValueScope.nneg_value);
			expressions.add(CirValueScope.npos_value);
		}
		else if(expression == CirValueScope.negt_value) {
			expressions.add(CirValueScope.npos_value);
			expressions.add(CirValueScope.nzro_value);
		}
		else if(expression == CirValueScope.npos_value) {
			expressions.add(CirValueScope.numb_value);
		}
		else if(expression == CirValueScope.nzro_value) {
			expressions.add(CirValueScope.numb_value);
		}
		else if(expression == CirValueScope.nneg_value) {
			expressions.add(CirValueScope.numb_value);
		}
		else if(expression == CirValueScope.numb_value) { }
		else {
			expressions.add(CirValueScope.numb_value);
		}
	}
	/**
	 * It infers the set of expressions subsumed from the input address expression based on abstract domain analysis.
	 * @param expression
	 * @param expressions
	 * @throws Exception
	 */
	private void extend_subsumed_scopes_from_addr(SymbolExpression expression, Collection<SymbolExpression> expressions) throws Exception {
		if(expression instanceof SymbolConstant) {
			if(((SymbolConstant) expression).get_long() == 0) {
				expressions.add(CirValueScope.null_value);
			}
			else {
				expressions.add(CirValueScope.nnul_value);
			}
		}
		else if(expression == CirValueScope.null_value) {
			expressions.add(CirValueScope.addr_value);
		}
		else if(expression == CirValueScope.nnul_value) {
			expressions.add(CirValueScope.addr_value);
		}
		else if(expression == CirValueScope.addr_value) { }
		else {
			expressions.add(CirValueScope.addr_value);
		}
	}
	/**
	 * @param annotation
	 * @param expressions
	 * @throws Exception
	 */
	private void extend_subsumed_scopes_from(CirAnnotation annotation, Collection<SymbolExpression> expressions) throws Exception {
		CirValueClass vtype = annotation.get_value_type();
		SymbolExpression symb_value = annotation.get_symb_value();
		switch(vtype) {
		case bool:	this.extend_subsumed_scopes_from_bool(symb_value, expressions); break;
		case usig:	this.extend_subsumed_scopes_from_usig(symb_value, expressions); break;
		case sign:	this.extend_subsumed_scopes_from_sign(symb_value, expressions); break;
		case real:	this.extend_subsumed_scopes_from_real(symb_value, expressions); break;
		case addr:	this.extend_subsumed_scopes_from_addr(symb_value, expressions); break;
		default:	break;
		}
	}
	/* extension methods */
	/**
	 * It recursively extends from the source of annotation to its directly subsumed ones.
	 * @param source
	 * @throws Exception
	 */
	private void extend_annotations_from(CirAnnotationNode source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source as null");
		}
		else {
			/* type-oriented extension */
			switch(source.get_annotation().get_logic_type()) {
			case cov_stmt:	this.extend_annotations_from_cov_stmt(source); break;
			case eva_expr:	this.extend_annotations_from_eva_expr(source); break;
			case trp_stmt:	this.extend_annotations_from_trp_stmt(source); break;
			case mut_stmt:	this.extend_annotations_from_mut_stmt(source); break;
			case mut_expr:	this.extend_annotations_from_mut_expr(source); break;
			case mut_refr:	this.extend_annotations_from_mut_refr(source); break;
			case sub_diff:	this.extend_annotations_from_sub_diff(source); break;
			case ext_diff:	this.extend_annotations_from_ext_diff(source); break;
			case xor_diff:	this.extend_annotations_from_xor_diff(source); break;
			default: throw new IllegalArgumentException("Unsupport: " + source.get_annotation());
			}
		}
	}
	/**
	 * cov_stmt(E, 1) <-- cov_stmt(E, 2) <-- ... <-- cov_stmt(E, N) <-- cov_stmt(E', N)
	 * @param source
	 * @throws Exception
	 */
	private void extend_annotations_from_cov_stmt(CirAnnotationNode source) throws Exception {
		/* 1. attribute getters */
		CirAnnotation annotation = source.get_annotation();
		CirExecution execution = annotation.get_execution(), check_point;
		int max_times = ((SymbolConstant) annotation.get_symb_value()).get_int();
		
		/* 2. determine the check-point for directly subsuming */
		check_point = this.get_prior_checkpoint(execution, Boolean.TRUE);
		if(check_point != execution) {
			source = source.subsume(CirAnnotation.cov_stmt(check_point, max_times));
		}
		
		/* 3. infer the execution times sequence and subsumed */
		List<Integer> times = this.get_execution_times_util(max_times);
		for(int k = times.size() - 2; k >= 0; k--) {
			source = source.subsume(CirAnnotation.cov_stmt(check_point, times.get(k)));
		}
	}
	/**
	 * eva_expr(E, expr) --> eva_expr(E, expr')+ --> cov_stmt(E, 1)
	 * @param source
	 * @throws Exception
	 */
	private void extend_annotations_from_eva_expr(CirAnnotationNode source) throws Exception {
		/* 1. attributes getter and initialization */
		CirAnnotation annotation = source.get_annotation();
		CirExecution execution = annotation.get_execution(), check_point;
		SymbolExpression condition = annotation.get_symb_value();
		
		/* 2. infer the next conditions directly being subsumed */
		Set<SymbolExpression> conditions = new HashSet<SymbolExpression>();
		this.extend_subsumed_conditions_from(condition, conditions);
		
		/* 3. recursively extend the eva_expr to its subsumed sub-conditions */
		if(!conditions.isEmpty()) {
			for(SymbolExpression sub_condition : conditions) {
				check_point = this.get_prior_checkpoint(execution, sub_condition);
				this.extend_annotations_from(source.subsume(CirAnnotation.eva_expr(check_point, sub_condition)));
			}
		}
		/* 4. for TRUE-operand, directly subsumes the coverage requirement */
		else {
			check_point = this.get_prior_checkpoint(execution, Boolean.TRUE);
			this.extend_annotations_from(source.subsume(CirAnnotation.cov_stmt(check_point, 1)));
		}
	}
	/**
	 * trp_stmt(E) --> trp_stmt(E')
	 * @param source
	 * @throws Exception
	 */
	private void extend_annotations_from_trp_stmt(CirAnnotationNode source) throws Exception {
		CirExecution execution = source.get_annotation().get_execution();
		CirExecution check_point = this.get_prior_checkpoint(execution, Boolean.TRUE);
		source.subsume(CirAnnotation.cov_stmt(check_point, 1));
		
		CirFunctionCallGraph graph = execution.get_graph().get_function().get_graph();
		CirFunction main_function = graph.get_main_function();
		if(main_function != execution.get_graph().get_function() && main_function != null) {
			source.subsume(CirAnnotation.trp_stmt(main_function.get_flow_graph().get_exit()));
		}
	}
	/**
	 * @param source
	 * @throws Exception
	 */
	private void extend_annotations_from_mut_stmt(CirAnnotationNode source) throws Exception {
		/* 1. infer the next symbolic values for mut_stmt */
		CirAnnotation annotation = source.get_annotation();
		Set<SymbolExpression> expressions = new HashSet<SymbolExpression>();
		this.extend_subsumed_scopes_from(annotation, expressions);
		
		/* 2. extend from mut_stmt annotation nodes from */
		CirExecution execution = annotation.get_execution();
		if(expressions.isEmpty()) {
			execution = this.get_prior_checkpoint(execution, Boolean.TRUE);
			source.subsume(CirAnnotation.cov_stmt(execution, 1));
		}
		else {
			for(SymbolExpression expression : expressions) {
				this.extend_annotations_from(source.subsume(CirAnnotation.mut_stmt(execution, expression)));
			}
		}
	}
	/**
	 * @param source
	 * @throws Exception
	 */
	private void extend_annotations_from_mut_expr(CirAnnotationNode source) throws Exception {
		/* 1. infer the next abstract value scopes */
		CirAnnotation annotation = source.get_annotation();
		Set<SymbolExpression> expressions = new HashSet<SymbolExpression>();
		this.extend_subsumed_scopes_from(annotation, expressions);
		
		/* 2. extend to coverage or subsumed expression scopes */
		if(expressions.isEmpty()) {
			CirExecution execution = annotation.get_execution();
			execution = this.get_prior_checkpoint(execution, Boolean.TRUE);
			source.subsume(CirAnnotation.cov_stmt(execution, 1));
		}
		else {
			for(SymbolExpression expression : expressions) {
				CirExpression orig_expression = (CirExpression) annotation.get_store_unit();
				this.extend_annotations_from(source.subsume(CirAnnotation.mut_expr(orig_expression, expression)));
			}
		}
	}
	/**
	 * @param source
	 * @throws Exception
	 */
	private void extend_annotations_from_mut_refr(CirAnnotationNode source) throws Exception {
		/* 1. infer the next abstract value scopes */
		CirAnnotation annotation = source.get_annotation();
		Set<SymbolExpression> expressions = new HashSet<SymbolExpression>();
		this.extend_subsumed_scopes_from(annotation, expressions);
		
		/* 2. extend to coverage or subsumed expression scopes */
		if(expressions.isEmpty()) {
			CirExecution execution = annotation.get_execution();
			execution = this.get_prior_checkpoint(execution, Boolean.TRUE);
			source.subsume(CirAnnotation.cov_stmt(execution, 1));
		}
		else {
			for(SymbolExpression expression : expressions) {
				CirExpression orig_expression = (CirExpression) annotation.get_store_unit();
				this.extend_annotations_from(source.subsume(CirAnnotation.mut_refr(orig_expression, expression)));
			}
		}
	}
	/**
	 * @param source
	 * @throws Exception
	 */
	private void extend_annotations_from_sub_diff(CirAnnotationNode source) throws Exception {
		/* 1. infer the next abstract value scopes */
		CirAnnotation annotation = source.get_annotation();
		Set<SymbolExpression> expressions = new HashSet<SymbolExpression>();
		this.extend_subsumed_scopes_from(annotation, expressions);
		
		/* 2. extend to coverage or subsumed expression scopes */
		if(expressions.isEmpty()) {
			CirExecution execution = annotation.get_execution();
			execution = this.get_prior_checkpoint(execution, Boolean.TRUE);
			source.subsume(CirAnnotation.cov_stmt(execution, 1));
		}
		else {
			for(SymbolExpression expression : expressions) {
				CirExpression orig_expression = (CirExpression) annotation.get_store_unit();
				this.extend_annotations_from(source.subsume(CirAnnotation.sub_diff(orig_expression, expression)));
			}
		}
	}
	/**
	 * @param source
	 * @throws Exception
	 */
	private void extend_annotations_from_ext_diff(CirAnnotationNode source) throws Exception {
		/* 1. infer the next abstract value scopes */
		CirAnnotation annotation = source.get_annotation();
		Set<SymbolExpression> expressions = new HashSet<SymbolExpression>();
		this.extend_subsumed_scopes_from(annotation, expressions);
		
		/* 2. extend to coverage or subsumed expression scopes */
		if(expressions.isEmpty()) {
			CirExecution execution = annotation.get_execution();
			execution = this.get_prior_checkpoint(execution, Boolean.TRUE);
			source.subsume(CirAnnotation.cov_stmt(execution, 1));
		}
		else {
			for(SymbolExpression expression : expressions) {
				CirExpression orig_expression = (CirExpression) annotation.get_store_unit();
				this.extend_annotations_from(source.subsume(CirAnnotation.ext_diff(orig_expression, expression)));
			}
		}
	}
	/**
	 * @param source
	 * @throws Exception
	 */
	private void extend_annotations_from_xor_diff(CirAnnotationNode source) throws Exception {
		/* 1. infer the next abstract value scopes */
		CirAnnotation annotation = source.get_annotation();
		Set<SymbolExpression> expressions = new HashSet<SymbolExpression>();
		this.extend_subsumed_scopes_from(annotation, expressions);
		
		/* 2. extend to coverage or subsumed expression scopes */
		if(expressions.isEmpty()) {
			CirExecution execution = annotation.get_execution();
			execution = this.get_prior_checkpoint(execution, Boolean.TRUE);
			source.subsume(CirAnnotation.cov_stmt(execution, 1));
		}
		else {
			for(SymbolExpression expression : expressions) {
				CirExpression orig_expression = (CirExpression) annotation.get_store_unit();
				this.extend_annotations_from(source.subsume(CirAnnotation.xor_diff(orig_expression, expression)));
			}
		}
	}
	/**
	 * @param source
	 * @throws Exception
	 */
	protected static void extend_annotations(CirAnnotationNode source) throws Exception {
		util.extend_annotations_from(source);
	}
	
	
}
