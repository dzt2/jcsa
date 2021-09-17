package com.jcsa.jcmutest.mutant.cir2mutant.stat.anot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirTagStatement;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.parse.symbol.process.SymbolProcess;

/**
 * It implements the generation, concretize, summarization and extension on the
 * CirAnnotation from CirAttribute or themselves.
 * 
 * @author yukimula
 *
 */
public class CirAnnotationUtil {
	
	/* singleton mode *//** constructor **/ private CirAnnotationUtil() {}
	private static final CirAnnotationUtil util = new CirAnnotationUtil();
	
	/* basic methods */
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
	
	/* generation of representative annotation from CirAttribute */
	/**
	 * cov_stmt{execution}([stmt:statement], [usig:cover_counter])
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_cover_count(CirCoverCount attribute, Collection<CirAnnotation> annotations) throws Exception {
		CirExecution execution = attribute.get_execution();
		int execute_times = attribute.get_coverage_count();
		annotations.add(CirAnnotation.cov_stmt(execution, execute_times));
	}
	/**
	 * eva_expr{execution}([stmt:statement], [bool:condition])
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_constraints(CirConstraint attribute, Collection<CirAnnotation> annotations) throws Exception {
		CirExecution execution = attribute.get_execution();
		SymbolExpression condition = attribute.get_condition();
		annotations.add(CirAnnotation.eva_expr(execution, condition));
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
		Map<Boolean, Collection<CirExecution>> maps = this.find_add_del_executions(
				attribute.get_original_flow().get_target(), attribute.get_mutation_flow().get_target());
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
	 * trp_stmt{execution}([stmt:statement], [bool:expt_value])
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_traps_error(CirTrapsError attribute, Collection<CirAnnotation> annotations) throws Exception {
		annotations.add(CirAnnotation.trp_stmt(attribute.get_execution()));
	}
	/**
	 * It generates the annotations for expression-related state error attribute
	 * @param expression
	 * @param muta_value
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
		/*
		if(expression.get_ast_source() != null)
		System.out.println(orig_annotation + "\t==>\t" + expression.get_data_type() + " in " + 
		expression.statement_of().generate_code(true) + 
				" as " + expression.get_ast_source().generate_code() + "\t" + CirMutations.is_boolean(expression));
		*/
		SymbolExpression orig_value = orig_annotation.get_symb_value();
		SymbolExpression muta_value = muta_annotation.get_symb_value();
		CirExecution execution = expression.execution_of();
		
		/* 2. generate trp-error if trapping actually occurs there */
		if(orig_value == CirValueScope.expt_value 
			|| muta_value == CirValueScope.expt_value) {
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
	public static void generate_annotations(CirAttribute attribute, Collection<CirAnnotation> annotations) throws Exception {
		util.generate_annotations_in(attribute, annotations);
	}
	
	/* concretize of representative annotation to concrete values */
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
	public static void concretize_annotations(CirAnnotation annotation, SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception {
		util.concretize_annotations_in(annotation, context, annotations);
	}
	
	/* summarization of concrete and representative annotations to abstract values */
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
	public static void summarize_annotations(CirAnnotation annotation,
			Collection<CirAnnotation> con_annotations,
			Collection<CirAnnotation> abs_annotations) throws Exception {
		util.summarize_annotations_in(annotation, con_annotations, abs_annotations);
	}
	
}
