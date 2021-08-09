package com.jcsa.jcmutest.mutant.cir2mutant.tree;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.jcsa.jcmutest.mutant.cir2mutant.CirMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAttribute;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirBlockError;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirCoverCount;
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
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symbol.SymbolBinaryExpression;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.lang.symbol.SymbolNode;
import com.jcsa.jcparse.parse.symbol.SymbolEvaluator;
import com.jcsa.jcparse.parse.symbol.process.SymbolProcess;

/**
 * It implement the transformation of CirAnnotation from CirAttribute and
 * summarization of CirAnnotation from concrete CirAnnotation.
 * 
 * @author yukimula
 *
 */
public class CirAnnotationUtil {
	
	/* singleton mode */	/** constructor **/	private CirAnnotationUtil() { }
	protected static final CirAnnotationUtil util = new CirAnnotationUtil();
	
	/* basic approach */
	/**
	 * recursively collect the symbolic references under the node
	 * @param node
	 * @param references to preserve the output references being collected
	 */
	private void get_symbol_references_in(SymbolNode node, Collection<SymbolExpression> references) {
		if(node.is_reference()) references.add((SymbolExpression) node);
		for(SymbolNode child : node.get_children()) {
			this.get_symbol_references_in(child, references);
		}
	}
	/**
	 * @param node
	 * @return the set of references defined in the node
	 */
	private Collection<SymbolExpression> get_symbol_references_in(SymbolNode node) {
		Set<SymbolExpression> references = new HashSet<>();
		this.get_symbol_references_in(node, references); return references;
	}
	/**
	 * @param node
	 * @param references
	 * @return whether there is reference used in the node
	 */
	private boolean has_symbol_references_in(SymbolNode node, Collection<SymbolExpression> references) {
		if(references.isEmpty()) {
			return false;
		}
		else if(references.contains(node)) {
			return true;
		}
		else {
			for(SymbolNode child : node.get_children()) {
				if(this.has_symbol_references_in(child, references)) {
					return true;
				}
			}
			return false;
		}
	}
	/**
	 * @param execution
	 * @param references
	 * @return whether any references is limited in the execution (IF|CASE|ASSIGN)
	 */
	private boolean has_symbol_references_in(CirExecution execution,  Collection<SymbolExpression> references) throws Exception {
		if(references.isEmpty()) {
			return false;
		}
		else {
			CirStatement statement = execution.get_statement();
			if(statement instanceof CirAssignStatement) {
				return references.contains(SymbolFactory.
						sym_expression(((CirAssignStatement) statement).get_lvalue()));
			}
			else if(statement instanceof CirIfStatement) {
				return this.has_symbol_references_in(SymbolFactory.sym_expression(
						((CirIfStatement) statement).get_condition()), references);
			}
			else if(statement instanceof CirCaseStatement) {
				return this.has_symbol_references_in(SymbolFactory.sym_expression(
						((CirCaseStatement) statement).get_condition()), references);
			}
			else {
				return false;
			}
		}
	}
	/**
	 * recursive collect the symbolic conditions defined in logical conjunction
	 * @param expression
	 * @param expressions
	 * @throws Exception
	 */
	private void get_symbol_conditions_in(SymbolExpression expression, Collection<SymbolExpression> expressions) throws Exception {
		if(expression instanceof SymbolConstant) {
			if(((SymbolConstant) expression).get_bool()) {
				/* ignore TRUE since it is equivalent to cov_stmt(1) */
			}
			else {
				expressions.add(SymbolFactory.sym_constant(Boolean.FALSE));
			}
		}
		else if(expression instanceof SymbolBinaryExpression) {
			COperator operator = ((SymbolBinaryExpression) expression).get_operator().get_operator();
			if(operator == COperator.logic_and) {
				this.get_symbol_conditions_in(((SymbolBinaryExpression) expression).get_loperand(), expressions);
				this.get_symbol_conditions_in(((SymbolBinaryExpression) expression).get_roperand(), expressions);
			}
			else {
				expressions.add(SymbolFactory.sym_condition(expression, true));
			}
		}
		else {
			expressions.add(SymbolFactory.sym_condition(expression, true));
		}
	}
	/**
	 * @param expression
	 * @param conditions
	 * @throws Exception
	 */
	private void generate_symbol_conditions_in(SymbolExpression expression, Collection<SymbolExpression> conditions) throws Exception {
		Set<SymbolExpression> sub_expressions = new HashSet<SymbolExpression>();
		this.get_symbol_conditions_in(expression, sub_expressions);
		for(SymbolExpression sub_expression : sub_expressions) {
			conditions.add(sub_expression);
			if(sub_expression instanceof SymbolBinaryExpression) {
				COperator operator = ((SymbolBinaryExpression) sub_expression).get_operator().get_operator();
				SymbolExpression loperand = ((SymbolBinaryExpression) sub_expression).get_loperand();
				SymbolExpression roperand = ((SymbolBinaryExpression) sub_expression).get_roperand();
				switch(operator) {
				case greater_tn:	/** (>, >=, !=) **/
				{
					conditions.add(SymbolFactory.greater_eq(loperand, roperand));
					conditions.add(SymbolFactory.not_equals(loperand, roperand));
					break;
				}
				case smaller_tn:	/** (<, <=, !=) **/
				{
					conditions.add(SymbolFactory.smaller_eq(loperand, roperand));
					conditions.add(SymbolFactory.not_equals(loperand, roperand));
					break;
				}
				case equal_with:	/** (==, <=, >=) **/
				{
					conditions.add(SymbolFactory.greater_eq(loperand, roperand));
					conditions.add(SymbolFactory.smaller_eq(loperand, roperand));
					break;
				}
				default:			
				{
					break;
				}
				}
			}
		}
	}
	/**
	 * @param execution
	 * @param expression
	 * @return find the previous check-point where the expression should be evaluated
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
			Collection<SymbolExpression> references = this.get_symbol_references_in(expression);
			while(iterator.hasNext()) {
				CirExecutionEdge edge = iterator.next();
				if(this.has_symbol_references_in(edge.get_source(), references)) {
					return edge.get_target();
				}
			}
			return prev_path.get_source();
		}
	}
	/**
	 * @param expression
	 * @param context
	 * @return symbolic evaluation
	 * @throws Exception
	 */
	private SymbolExpression symbol_evaluate(SymbolExpression expression, SymbolProcess context) throws Exception {
		return SymbolEvaluator.evaluate_on(expression, context);
	}
	/**
	 * true --> add_executions; false --> del_executions;
	 * @param orig_target
	 * @param muta_target
	 * @return
	 * @throws Exception
	 */
	private Map<Boolean, Collection<CirExecution>>	get_add_del_executions(CirExecution orig_target, CirExecution muta_target) throws Exception {
		if(orig_target == null) {
			throw new IllegalArgumentException("Invalid orig_target: null");
		}
		else if(muta_target == null) {
			throw new IllegalArgumentException("Invalid muta_target: null");
		}
		else {
			/* compute the statements being added or deleted in testing */
			Collection<CirExecution> add_executions = new HashSet<>();
			Collection<CirExecution> del_executions = new HashSet<>();
			CirExecutionPath orig_path = CirExecutionPathFinder.finder.df_extend(orig_target);
			CirExecutionPath muta_path = CirExecutionPathFinder.finder.df_extend(muta_target);
			for(CirExecutionEdge edge : muta_path.get_edges()) { add_executions.add(edge.get_source()); }
			for(CirExecutionEdge edge : orig_path.get_edges()) { del_executions.add(edge.get_source()); }
			add_executions.add(muta_path.get_target()); del_executions.add(orig_path.get_target());

			/* removed the common part for corrections */
			Collection<CirExecution> com_executions = new HashSet<>();
			for(CirExecution execution : add_executions) {
				if(del_executions.contains(execution)) {
					com_executions.add(execution);
				}
			}
			add_executions.removeAll(com_executions);
			del_executions.removeAll(com_executions);

			/* construct mapping from true|false to collections */
			Map<Boolean, Collection<CirExecution>> results =
					new HashMap<>();
			results.put(Boolean.TRUE, add_executions);
			results.put(Boolean.FALSE, del_executions);
			return results;
		}
	}
	
	/* concrete annotation generation */
	/**
	 * generate the set of concrete annotations from attribute using context 
	 * @param attribute		the attribute to be translated as concrete annotations
	 * @param context		the context to be used to evaluate the source attribute
	 * @param annotations	the collection to collect concrete annotations generated
	 * @throws Exception
	 */
	protected void generate_annotations(CirAttribute attribute, SymbolProcess 
			context, Collection<CirAnnotation> annotations) throws Exception {
		if(attribute == null) {
			throw new IllegalArgumentException("Invalid attribute as null");
		}
		else if(annotations == null) {
			throw new IllegalArgumentException("No collection to caputure output");
		}
		else if(attribute instanceof CirCoverCount) {
			this.generate_annotations_in((CirCoverCount) attribute, context, annotations);
		}
		else if(attribute instanceof CirConstraint) {
			this.generate_annotations_in((CirConstraint) attribute, context, annotations);
		}
		else if(attribute instanceof CirBlockError) {
			this.generate_annotations_in((CirBlockError) attribute, context, annotations);
		}
		else if(attribute instanceof CirFlowsError) {
			this.generate_annotations_in((CirFlowsError) attribute, context, annotations);
		}
		else if(attribute instanceof CirTrapsError) {
			this.generate_annotations_in((CirTrapsError) attribute, context, annotations);
		}
		else if(attribute instanceof CirValueError) {
			this.generate_annotations_in((CirValueError) attribute, context, annotations);
		}
		else if(attribute instanceof CirReferError) {
			this.generate_annotations_in((CirReferError) attribute, context, annotations);
		}
		else if(attribute instanceof CirStateError) {
			this.generate_annotations_in((CirStateError) attribute, context, annotations);
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + attribute);
		}
	}
	/* constraint or stmt_error class */
	/**
	 * @param attribute
	 * @param context
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in(CirCoverCount attribute,
			SymbolProcess context, 
			Collection<CirAnnotation> annotations) throws Exception {
		/* declarations and getters */
		CirExecution execution = attribute.get_execution();
		int execute_times = attribute.get_coverage_count();
		Set<Integer> counters = new HashSet<Integer>();
		
		/* generate domain counters in given times */
		for(int times = 1; times < execute_times; times = times * 2) {
			counters.add(Integer.valueOf(times));
		}
		counters.add(Integer.valueOf(execute_times));
		
		/* concrete annotation generated here... */
		for(Integer counter : counters) {
			annotations.add(CirAnnotation.cov_stmt(execution, counter));
		}
	}
	/**
	 * @param attribute
	 * @param context
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in(CirConstraint attribute,
			SymbolProcess context, 
			Collection<CirAnnotation> annotations) throws Exception {
		/* declarations and getters */
		CirExecution execution = attribute.get_execution(), checkpoint;
		SymbolExpression condition = attribute.get_condition();
		condition = this.symbol_evaluate(condition, null);
		Set<SymbolExpression> conditions = new HashSet<SymbolExpression>();
		this.generate_symbol_conditions_in(condition, conditions);
		
		/* generate annotations based on the conditions */
		for(SymbolExpression sub_condition : conditions) {
			checkpoint = this.find_prior_checkpoint(execution, sub_condition);
			annotations.add(CirAnnotation.eva_expr(checkpoint, sub_condition));
		}
		
		/* insert coverage if no other constraint needed */
		if(!conditions.isEmpty()) {
			checkpoint = this.find_prior_checkpoint(
					execution, SymbolFactory.sym_constant(Boolean.TRUE));
			annotations.add(CirAnnotation.cov_stmt(checkpoint, 1));
		}
	}
	/**
	 * @param attribute
	 * @param context
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in(CirBlockError attribute,
			SymbolProcess context,
			Collection<CirAnnotation> annotations) throws Exception {
		CirExecution execution = attribute.get_execution();
		if(attribute.is_executed()) {
			annotations.add(CirAnnotation.add_stmt(execution));
		}
		else {
			annotations.add(CirAnnotation.del_stmt(execution));
		}
	}
	/**
	 * @param attribute
	 * @param context
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in(CirFlowsError attribute, 
			SymbolProcess context,
			Collection<CirAnnotation> annotations) throws Exception {
		CirExecution orig_target = attribute.get_original_flow().get_target();
		CirExecution muta_target = attribute.get_mutation_flow().get_target();
		Map<Boolean, Collection<CirExecution>> results =
						this.get_add_del_executions(orig_target, muta_target);
		for(Boolean result : results.keySet()) {
			for(CirExecution execution : results.get(result)) {
				if(result.booleanValue()) {
					annotations.add(CirAnnotation.add_stmt(execution));
				}
				else {
					annotations.add(CirAnnotation.del_stmt(execution));
				}
			}
		}
	}
	/**
	 * @param attribute
	 * @param context
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in(CirTrapsError attribute,
			SymbolProcess context,
			Collection<CirAnnotation> annotations) throws Exception {
		CirExecution execution = attribute.get_execution();
		execution = execution.get_graph().get_exit();
		annotations.add(CirAnnotation.trp_stmt(execution));
	}
	/* expr_error class and analysis */
	/**
	 * @param expression
	 * @param muta_value
	 * @throws Exception
	 */
	private void generate_domain_annotations_in_numb(CirExpression expression, 
			long muta_value, Collection<CirAnnotation> annotations) throws Exception {
		if(muta_value > 0) {
			annotations.add(CirAnnotation.set_post(expression));
			annotations.add(CirAnnotation.set_nneg(expression));
			annotations.add(CirAnnotation.set_nzro(expression));
		}
		else if(muta_value < 0) {
			annotations.add(CirAnnotation.set_negt(expression));
			annotations.add(CirAnnotation.set_npos(expression));
			annotations.add(CirAnnotation.set_nzro(expression));
		}
		else {
			annotations.add(CirAnnotation.set_zero(expression));
			annotations.add(CirAnnotation.set_npos(expression));
			annotations.add(CirAnnotation.set_nneg(expression));
		}
	}
	/**
	 * @param expression
	 * @param muta_value
	 * @throws Exception
	 */
	private void generate_domain_annotations_in_numb(CirExpression expression, 
			double muta_value, Collection<CirAnnotation> annotations) throws Exception {
		if(muta_value > 0) {
			annotations.add(CirAnnotation.set_post(expression));
			annotations.add(CirAnnotation.set_nneg(expression));
			annotations.add(CirAnnotation.set_nzro(expression));
		}
		else if(muta_value < 0) {
			annotations.add(CirAnnotation.set_negt(expression));
			annotations.add(CirAnnotation.set_npos(expression));
			annotations.add(CirAnnotation.set_nzro(expression));
		}
		else {
			annotations.add(CirAnnotation.set_zero(expression));
			annotations.add(CirAnnotation.set_npos(expression));
			annotations.add(CirAnnotation.set_nneg(expression));
		}
	}
	/**
	 * @param expression
	 * @param muta_value
	 * @throws Exception
	 */
	private void generate_domain_annotations_in_addr(CirExpression expression, 
			long muta_value, Collection<CirAnnotation> annotations) throws Exception {
		if(muta_value == 0) {
			annotations.add(CirAnnotation.set_null(expression));
		}
		else {
			annotations.add(CirAnnotation.set_invp(expression));
		}
	}
	/**
	 * @param expression
	 * @param muta_expression
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_set_constant(CirExpression expression,
			SymbolConstant muta_constant, Collection<CirAnnotation> annotations) throws Exception {
		if(CirMutation.is_boolean(expression)) {
			annotations.add(CirAnnotation.set_bool(expression, muta_constant.get_bool()));
			annotations.add(CirAnnotation.chg_bool(expression));
		}
		else if(CirMutation.is_integer(expression)) {
			annotations.add(CirAnnotation.set_numb(expression, muta_constant.get_long()));
			this.generate_domain_annotations_in_numb(expression, muta_constant.get_long(), annotations);
			annotations.add(CirAnnotation.chg_numb(expression));
		}
		else if(CirMutation.is_numeric(expression)) {
			annotations.add(CirAnnotation.set_numb(expression, muta_constant.get_double()));
			this.generate_domain_annotations_in_numb(expression, muta_constant.get_double(), annotations);
			annotations.add(CirAnnotation.chg_numb(expression));
		}
		else if(CirMutation.is_pointer(expression)) {
			annotations.add(CirAnnotation.set_addr(expression, muta_constant.get_long()));
			this.generate_domain_annotations_in_addr(expression, muta_constant.get_long(), annotations);
			annotations.add(CirAnnotation.chg_addr(expression));
		}
		else {
			annotations.add(CirAnnotation.set_auto(expression, muta_constant.get_constant()));
			annotations.add(CirAnnotation.chg_auto(expression));
		}
	}
	/**
	 * @param expression
	 * @param muta_expression
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_set_symbolic(CirExpression expression, Boolean is_reference,
			SymbolExpression muta_expression, Collection<CirAnnotation> annotations) throws Exception {
		if(is_reference == null) {
			annotations.add(CirAnnotation.mut_stat(expression, muta_expression));
		}
		else if(is_reference) {
			annotations.add(CirAnnotation.mut_refr(expression, muta_expression));
		}
		else {
			annotations.add(CirAnnotation.mut_expr(expression, muta_expression));
		}
		
		if(CirMutation.is_boolean(expression)) {
			annotations.add(CirAnnotation.chg_bool(expression));
		}
		else if(CirMutation.is_numeric(expression)) {
			annotations.add(CirAnnotation.chg_numb(expression));
		}
		else if(CirMutation.is_pointer(expression)) {
			annotations.add(CirAnnotation.chg_addr(expression));
		}
		else {
			annotations.add(CirAnnotation.chg_auto(expression));
		}
	}
	/**
	 * @param expression
	 * @param orig_expression
	 * @param muta_expression
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_scope_compare(CirExpression expression,
			SymbolExpression orig_expression, SymbolExpression muta_expression,
			SymbolProcess context, Collection<CirAnnotation> annotations) throws Exception {
		/* inc_scop + dec_scop */
		SymbolExpression difference = SymbolFactory.arith_sub(
				expression.get_data_type(), muta_expression, orig_expression);
		difference = this.symbol_evaluate(difference, context);
		if(difference instanceof SymbolConstant) {
			Object number = ((SymbolConstant) difference).get_number();
			if(number instanceof Long) {
				long value = ((Long) number).longValue();
				if(value > 0) {
					annotations.add(CirAnnotation.inc_scop(expression));
				}
				else if(value < 0) {
					annotations.add(CirAnnotation.dec_scop(expression));
				}
			}
			else {
				double value = ((Double) number).doubleValue();
				if(value > 0) {
					annotations.add(CirAnnotation.inc_scop(expression));
				}
				else if(value < 0) {
					annotations.add(CirAnnotation.dec_scop(expression));
				}
			}
		}
		
		/* ext_scop + shk_scop */
		if(orig_expression instanceof SymbolConstant && 
				muta_expression instanceof SymbolConstant) {
			SymbolConstant orig_constant = (SymbolConstant) orig_expression;
			SymbolConstant muta_constant = (SymbolConstant) muta_expression;
			if(CirMutation.is_integer(expression)) {
				long orig_value = Math.abs(orig_constant.get_long());
				long muta_value = Math.abs(muta_constant.get_long());
				if(muta_value > orig_value) {
					annotations.add(CirAnnotation.ext_scop(expression));
				}
				else if(muta_value < orig_value) {
					annotations.add(CirAnnotation.shk_scop(expression));
				} 
			}
			else if(CirMutation.is_pointer(expression)) {
				long orig_value = Math.abs(orig_constant.get_long());
				long muta_value = Math.abs(muta_constant.get_long());
				if(muta_value > orig_value) {
					annotations.add(CirAnnotation.ext_scop(expression));
				}
				else if(muta_value < orig_value) {
					annotations.add(CirAnnotation.shk_scop(expression));
				}
			}
			else {
				double orig_value = Math.abs(orig_constant.get_double());
				double muta_value = Math.abs(muta_constant.get_double());
				if(muta_value > orig_value) {
					annotations.add(CirAnnotation.ext_scop(expression));
				}
				else if(muta_value < orig_value) {
					annotations.add(CirAnnotation.shk_scop(expression));
				}
			}
		}
	}
	/**
	 * @param attribute
	 * @param context
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in(CirValueError attribute,
			SymbolProcess context,
			Collection<CirAnnotation> annotations) throws Exception {
		/* 1. initialization */
		CirExecution execution = attribute.get_execution();
		CirExpression expression = (CirExpression) attribute.get_location();
		SymbolExpression orig_expression = SymbolFactory.sym_expression(expression);
		SymbolExpression muta_expression = attribute.get_muta_expression();
		orig_expression = this.symbol_evaluate(orig_expression, context);
		try {
			muta_expression = this.symbol_evaluate(muta_expression, context);
		}
		catch(ArithmeticException ex) {
			annotations.add(CirAnnotation.trp_stmt(execution.get_graph().get_exit()));
			return;
		}
		
		/* 2. compared with two values and return if they are equivalent */
		if(orig_expression.equals(muta_expression)) { return;	/* none */ }
		
		/* 3. muta_expression as constant will generate set_xxx annotation */
		if(muta_expression instanceof SymbolConstant) {
			this.generate_annotations_in_set_constant(
					expression, (SymbolConstant) muta_expression, annotations);
		}
		/* 4. otherwise, it only generates chg_xxx and mut_expr as alternate */
		else {
			this.generate_annotations_in_set_symbolic(
					expression, false, muta_expression, annotations);
		}
		
		/* 5. scope analysis using symbolic difference evaluation */
		if(CirMutation.is_numeric(expression) || CirMutation.is_pointer(expression)) {
			this.generate_annotations_in_scope_compare(expression, 
					orig_expression, muta_expression, context, annotations);
		}
	}
	/**
	 * @param attribute
	 * @param context
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in(CirReferError attribute, SymbolProcess context,
			Collection<CirAnnotation> annotations) throws Exception {
		/* 1. initialization */
		CirExecution execution = attribute.get_execution();
		CirExpression expression = (CirExpression) attribute.get_location();
		SymbolExpression orig_expression = SymbolFactory.sym_expression(expression);
		SymbolExpression muta_expression = attribute.get_muta_expression();
		orig_expression = this.symbol_evaluate(orig_expression, context);
		try {
			muta_expression = this.symbol_evaluate(muta_expression, context);
		}
		catch(ArithmeticException ex) {
			annotations.add(CirAnnotation.trp_stmt(execution.get_graph().get_exit()));
			return;
		}
		
		/* 2. compared with two values and return if they are equivalent */
		if(orig_expression.equals(muta_expression)) { return;	/* none */ }
		
		/* 3. muta_expression as constant will generate set_xxx annotation */
		if(muta_expression instanceof SymbolConstant) {
			this.generate_annotations_in_set_constant(
					expression, (SymbolConstant) muta_expression, annotations);
		}
		/* 4. otherwise, it only generates chg_xxx and mut_expr as alternate */
		else {
			this.generate_annotations_in_set_symbolic(
					expression, true, muta_expression, annotations);
		}
		
		/* 5. scope analysis using symbolic difference evaluation */
		if(CirMutation.is_numeric(expression) || CirMutation.is_pointer(expression)) {
			this.generate_annotations_in_scope_compare(expression, 
					orig_expression, muta_expression, context, annotations);
		}
	}
	/**
	 * @param attribute
	 * @param context
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in(CirStateError attribute,
			SymbolProcess context,
			Collection<CirAnnotation> annotations) throws Exception {
		CirExpression expression = (CirExpression) attribute.get_location();
		SymbolExpression muta_expression = attribute.get_parameter();
		try {
			muta_expression = this.symbol_evaluate(muta_expression, context);
		}
		catch(ArithmeticException ex) {
			annotations.add(CirAnnotation.trp_stmt(attribute.get_execution().get_graph().get_exit()));
		}
		
		if(muta_expression instanceof SymbolConstant) {
			this.generate_annotations_in_set_constant(
					expression, (SymbolConstant) muta_expression, annotations);
		}
		else {
			this.generate_annotations_in_set_symbolic(
					expression, null, muta_expression, annotations);
		}
	}
	
	/* abstract annotation summarization */
	/**
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	protected void summarize_annotations(
			Collection<CirAnnotation> concrete_annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		Map<CirAnnotationType, Collection<CirAnnotation>> maps = 
				this.distribute_annotations(concrete_annotations);
		for(CirAnnotationType type : maps.keySet()) {
			Collection<CirAnnotation> annotations = maps.get(type);
			switch(type) {
			case cov_stmt:	this.summarize_annotations_in_cov_stmt(annotations, abstract_annotations); break;
			case eva_expr:	this.summarize_annotations_in_eva_expr(annotations, abstract_annotations); break;
			case mut_flow:	this.summarize_annotations_in_mut_flow(annotations, abstract_annotations); break;
			case mut_stmt:	this.summarize_annotations_in_mut_stmt(annotations, abstract_annotations); break;
			case trp_stmt:	this.summarize_annotations_in_trp_stmt(annotations, abstract_annotations); break;
			case mut_expr:	this.summarize_annotations_in_mut_expr(annotations, abstract_annotations); break;
			case mut_refr:	this.summarize_annotations_in_mut_refr(annotations, abstract_annotations); break;
			case mut_stat:	this.summarize_annotations_in_mut_stat(annotations, abstract_annotations); break;
			case chg_bool:	this.summarize_annotations_in_chg_xxxx(annotations, abstract_annotations); break;
			case chg_numb:	this.summarize_annotations_in_chg_xxxx(annotations, abstract_annotations); break;
			case chg_addr:	this.summarize_annotations_in_chg_xxxx(annotations, abstract_annotations); break;
			case chg_auto:	this.summarize_annotations_in_chg_xxxx(annotations, abstract_annotations); break;
			case set_bool:	this.summarize_annotations_in_set_xxxx(annotations, abstract_annotations); break;
			case set_numb:	this.summarize_annotations_in_set_xxxx(annotations, abstract_annotations); break;
			case set_addr:	this.summarize_annotations_in_set_xxxx(annotations, abstract_annotations); break;
			case set_auto:	this.summarize_annotations_in_set_xxxx(annotations, abstract_annotations); break;
			case set_post:	this.summarize_annotations_in_set_post(annotations, abstract_annotations); break;
			case set_zero:	this.summarize_annotations_in_set_zero(annotations, abstract_annotations); break;
			case set_negt:	this.summarize_annotations_in_set_negt(annotations, abstract_annotations); break;
			case set_null:	this.summarize_annotations_in_set_null(annotations, abstract_annotations); break;
			case inc_scop:	this.summarize_annotations_in_inc_scop(annotations, abstract_annotations); break;
			case ext_scop:	this.summarize_annotations_in_ext_scop(annotations, abstract_annotations); break;
			default: 		throw new IllegalArgumentException(type.toString());
			}
		}
	}
	/**
	 * @param annotations
	 * @return the mapping from unique type to the collection of related annotations
	 */
	private Map<CirAnnotationType, Collection<CirAnnotation>> distribute_annotations(Collection<CirAnnotation> annotations) {
		Map<CirAnnotationType, Collection<CirAnnotation>> results = 
				new HashMap<CirAnnotationType, Collection<CirAnnotation>>();
		for(CirAnnotation annotation : annotations) {
			CirAnnotationType type = annotation.get_operator();
			switch(type) {
			case cov_stmt:
			case eva_expr:
			case mut_flow:
			case mut_stmt:
			case trp_stmt:
			case mut_expr:
			case mut_refr:
			case mut_stat:
			case set_bool:
			case set_numb:
			case set_addr:
			case set_auto:
			case chg_addr:
			case chg_numb:
			case chg_bool:
			case chg_auto:	type = annotation.get_operator();	break;
			case set_post:
			case set_npos:	type = CirAnnotationType.set_post; 	break;
			case set_negt:
			case set_nneg:	type = CirAnnotationType.set_negt;	break;
			case set_zero:
			case set_nzro:	type = CirAnnotationType.set_zero;	break;
			case set_null:
			case set_invp:	type = CirAnnotationType.set_null;	break;
			case inc_scop:
			case dec_scop:	type = CirAnnotationType.inc_scop;	break;
			case ext_scop:
			case shk_scop:	type = CirAnnotationType.ext_scop;	break;
			default:		throw new IllegalArgumentException(annotation.toString());
			}
			
			if(!results.containsKey(type)) 
				results.put(type, new HashSet<CirAnnotation>());
			results.get(type).add(annotation);
		}
		return results;
	}
	/* constraint class */
	/**
	 * @param annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_cov_stmt(Collection<CirAnnotation> annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		abstract_annotations.addAll(annotations);
	}
	/**
	 * @param annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_eva_expr(Collection<CirAnnotation> annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		abstract_annotations.addAll(annotations);
	}
	/* stmt_error class */
	/**
	 * @param annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_mut_flow(Collection<CirAnnotation> annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		if(annotations.size() == 1) {
			CirAnnotation annotation = annotations.iterator().next();
			CirExecution orig_target = annotation.get_location().execution_of();
			CirExecution muta_target = (CirExecution) annotation.get_parameter().get_source();
			Map<Boolean, Collection<CirExecution>> results = this.get_add_del_executions(orig_target, muta_target);
			for(Boolean result : results.keySet()) {
				for(CirExecution execution : results.get(result)) {
					if(result) {
						abstract_annotations.add(CirAnnotation.add_stmt(execution));
					}
					else {
						abstract_annotations.add(CirAnnotation.del_stmt(execution));
					}
				}
			}
		}
	}
	/**
	 * @param annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_mut_stmt(Collection<CirAnnotation> annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		abstract_annotations.addAll(annotations);
	}
	/**
	 * @param annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_trp_stmt(Collection<CirAnnotation> annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		abstract_annotations.addAll(annotations);
	}
	/* symb_error class */
	/**
	 * @param annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_mut_expr(Collection<CirAnnotation> annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		if(annotations.size() == 1) {
			CirAnnotation annotation = annotations.iterator().next();
			abstract_annotations.add(annotation);
		}
	}
	/**
	 * @param annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_mut_refr(Collection<CirAnnotation> annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		if(annotations.size() == 1) {
			CirAnnotation annotation = annotations.iterator().next();
			abstract_annotations.add(annotation);
		}
	}
	/**
	 * @param annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_mut_stat(Collection<CirAnnotation> annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		if(annotations.size() == 1) {
			CirAnnotation annotation = annotations.iterator().next();
			abstract_annotations.add(annotation);
		}
	}
	/* chg_xxxx class */
	/**
	 * @param annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_chg_xxxx(Collection<CirAnnotation> annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		abstract_annotations.addAll(annotations);
	}
	/* set_xxxx class */
	/**
	 * @param annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_set_xxxx(Collection<CirAnnotation> annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		if(annotations.size() == 1) {
			CirAnnotation annotation = annotations.iterator().next();
			abstract_annotations.add(annotation);
		}
	}
	/* scope analysis */
	/**
	 * @param annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_set_post(Collection<CirAnnotation> annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		Set<CirAnnotationType> types = new HashSet<CirAnnotationType>();
		for(CirAnnotation annotation : annotations) {
			types.add(annotation.get_operator());
		}
		if(types.size() == 1) {
			abstract_annotations.addAll(annotations);
		}
	}
	/**
	 * @param annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_set_zero(Collection<CirAnnotation> annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		Set<CirAnnotationType> types = new HashSet<CirAnnotationType>();
		for(CirAnnotation annotation : annotations) {
			types.add(annotation.get_operator());
		}
		if(types.size() == 1) {
			abstract_annotations.addAll(annotations);
		}
	}
	/**
	 * @param annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_set_negt(Collection<CirAnnotation> annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		Set<CirAnnotationType> types = new HashSet<CirAnnotationType>();
		for(CirAnnotation annotation : annotations) {
			types.add(annotation.get_operator());
		}
		if(types.size() == 1) {
			abstract_annotations.addAll(annotations);
		}
	}
	/**
	 * @param annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_set_null(Collection<CirAnnotation> annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		Set<CirAnnotationType> types = new HashSet<CirAnnotationType>();
		for(CirAnnotation annotation : annotations) {
			types.add(annotation.get_operator());
		}
		if(types.size() == 1) {
			abstract_annotations.addAll(annotations);
		}
	}
	/**
	 * @param annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_inc_scop(Collection<CirAnnotation> annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		Set<CirAnnotationType> types = new HashSet<CirAnnotationType>();
		for(CirAnnotation annotation : annotations) {
			types.add(annotation.get_operator());
		}
		if(types.size() == 1) {
			abstract_annotations.addAll(annotations);
		}
	}
	/**
	 * @param annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_ext_scop(Collection<CirAnnotation> annotations,
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		Set<CirAnnotationType> types = new HashSet<CirAnnotationType>();
		for(CirAnnotation annotation : annotations) {
			types.add(annotation.get_operator());
		}
		if(types.size() == 1) {
			abstract_annotations.addAll(annotations);
		}
	}
	
}
