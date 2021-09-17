package com.jcsa.jcmutest.mutant.cir2mutant.stat.anot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.jcsa.jcparse.lang.symbol.SymbolBinaryExpression;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.lang.symbol.SymbolNode;

/**
 * The hierarchical structure of CirAnnotation.
 * 
 * @author yukimula
 *
 */
public class CirAnnotationTree {
	
	/* attributes */
	private Map<CirAnnotation, CirAnnotationNode> nodes;
	private CirAnnotationTree() {
		this.nodes = new HashMap<CirAnnotation, CirAnnotationNode>();
	}
	/**
	 * @param annotations
	 * @return it constructs the tree structure for input annotations
	 * @throws Exception
	 */
	public static CirAnnotationTree new_tree(Iterable<CirAnnotation> annotations) throws Exception {
		CirAnnotationTree tree = new CirAnnotationTree();
		if(annotations != null) {
			for(CirAnnotation annotation : annotations) {
				CirAnnotationNode source = tree.new_node(annotation);
				tree.extend_annotation_node(source); /* extensions */
			}
		}
		return tree;
	}
	
	/* getters */
	/**
	 * @return the number of nodes within this tree
	 */
	public int size() { return this.nodes.size(); }
	/**
	 * @return the set of annotations recorded in this tree
	 */
	public Iterable<CirAnnotation> get_annotations() { return this.nodes.keySet(); }
	/**
	 * @return the set of nodes created in this tree for the given annotations
	 */
	public Iterable<CirAnnotationNode> get_nodes() { return this.nodes.values(); }
	/**
	 * @param annotation
	 * @return it gets the existing node w.r.t. the annotation as given
	 * @throws Exception
	 */
	public CirAnnotationNode get_node(CirAnnotation annotation) throws Exception {
		if(!this.nodes.containsKey(annotation)) {
			throw new IllegalArgumentException("Invalid: " + annotation);
		}
		else {
			return this.nodes.get(annotation);
		}
	}
	
	/* basic methods */
	/**
	 * It recursively captures the sub_conditions in the input when taking it as conjunctive.
	 * @param condition
	 * @param conditions
	 * @throws Exception
	 */
	private void get_conditions_in(SymbolExpression condition, Collection<SymbolExpression> conditions) throws Exception {
		if(condition instanceof SymbolConstant) {
			if(((SymbolConstant) condition).get_bool()) { 
				/* ignore the TRUE operands from conjunctive expression */
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
	 * @return the set of the sub_conditions in the input when taking it as conjunctive.
	 * @throws Exception
	 */
	private Collection<SymbolExpression> get_conditions_in(SymbolExpression condition) throws Exception {
		Set<SymbolExpression> conditions = new HashSet<SymbolExpression>();
		this.get_conditions_in(condition, conditions); return conditions;
	}
	/**
	 * @param max_exec_time
	 * @return [1, 2, 4, 8, ..., max_exec_time]
	 */
	private List<Integer> get_execution_times_from(int max_exec_time) {
		List<Integer> times = new ArrayList<Integer>();
		for(int k = 1; k < max_exec_time; k = k * 2) {
			times.add(Integer.valueOf(k));
		}
		times.add(Integer.valueOf(max_exec_time));
		return times;
	}
	/**
	 * It recursively collects the set of references (identifier|de_reference) in the input node
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
	 * @return the set of reference nodes within the input
	 * @throws Exception
	 */
	private Collection<SymbolExpression> get_references_in(SymbolNode node) throws Exception {
		Set<SymbolExpression> references = new HashSet<SymbolExpression>();
		this.get_references_in(node, references); return references;
	}
	/**
	 * @param node
	 * @param references
	 * @return whether the node uses any reference in the given collection
	 */
	private boolean has_references_in(SymbolNode node, Collection<SymbolExpression> references) {
		if(node == null) {
			return false;
		}
		else if(references.isEmpty()) {
			return false;
		}
		else if(references.contains(node)) {
			return true;
		}
		else {
			for(SymbolNode child : node.get_children()) {
				if(this.has_references_in(child, references)) {
					return true;
				}
			}
			return false;
		}
	}
	/**
	 * @param execution
	 * @param references
	 * @return whether the reference is defined in the given statement of execution point
	 * @throws Exception
	 */
	private boolean has_references_in(CirExecution execution, Collection<SymbolExpression> references) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution as null");
		}
		else if(references == null || references.isEmpty()) {
			return false;
		}
		else {
			CirStatement statement = execution.get_statement();
			if(statement instanceof CirAssignStatement) {
				SymbolExpression node = SymbolFactory.sym_expression(((CirAssignStatement) statement).get_lvalue());
				return references.contains(node);
			}
			else if(statement instanceof CirIfStatement) {
				SymbolExpression node = SymbolFactory.sym_expression(((CirIfStatement) statement).get_condition());
				return this.has_references_in(node, references);
			}
			else if(statement instanceof CirCaseStatement) {
				SymbolExpression node = SymbolFactory.sym_expression(((CirCaseStatement) statement).get_condition());
				return this.has_references_in(node, references);
			}
			else {
				return false;
			}
		}
	}
	/**
	 * @param execution
	 * @param condition
	 * @return it finds the best point to evaluate the condition in the execution
	 * @throws Exception
	 */
	private CirExecution get_available_check_point(CirExecution execution, SymbolExpression condition) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else {
			if(condition instanceof SymbolConstant) {
				if(((SymbolConstant) condition).get_bool()) {
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
			else {
				Collection<SymbolExpression> references = this.get_references_in(condition);
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
	}
	
	/* extension methods */
	/**
	 * @param annotation
	 * @return It creates the node w.r.t. the annotation as given
	 * @throws Exception
	 */
	private CirAnnotationNode new_node(CirAnnotation annotation) throws Exception {
		if(annotation == null) {
			throw new IllegalArgumentException("Invalid annotation: null");
		}
		else {
			if(!this.nodes.containsKey(annotation)) {
				this.nodes.put(annotation, new CirAnnotationNode(this, annotation));
			}
			return this.nodes.get(annotation);
		}
	}
	/**
	 * It recursively extends the nodes from source using static analysis
	 * @param source
	 * @throws Exception
	 */
	private void extend_annotation_node(CirAnnotationNode source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else {
			switch(source.get_annotation().get_logic_type()) {
			case cov_stmt:	this.extend_annotation_node_in_cov_stmt(source); break;
			case eva_expr:	this.extend_annotation_node_in_eva_expr(source); break;
			case trp_stmt:	this.extend_annotation_node_in_trp_stmt(source); break;
			case mut_stmt:	this.extend_annotation_node_in_mut_stmt(source); break;
			case mut_expr:	this.extend_annotation_node_in_mut_expr(source); break;
			case mut_refr:	this.extend_annotation_node_in_mut_refr(source); break;
			case sub_diff:	this.extend_annotation_node_in_sub_diff(source); break;
			case ext_diff:	this.extend_annotation_node_in_ext_diff(source); break;
			case xor_diff:	this.extend_annotation_node_in_xor_diff(source); break;
			default:	throw new IllegalArgumentException("Invalid: " + source.get_annotation());
			}
		}
	}
	/* cond storage class */
	/**
	 * cov_stmt(N) --> cov_stmt(N/2) --> ... --> cov_stmt(1)
	 * @param source
	 * @throws Exception
	 */
	private void extend_annotation_node_in_cov_stmt(CirAnnotationNode source) throws Exception {
		/* 1. capture the execution point and execution times */
		CirAnnotation annotation = source.get_annotation();
		CirExecution execution = this.get_available_check_point(
				annotation.get_execution(), SymbolFactory.sym_constant(Boolean.TRUE));
		int max_exec_time = ((SymbolConstant) annotation.get_symb_value()).get_int();
		List<Integer> times = this.get_execution_times_from(max_exec_time);
		
		/* 2. recursively connect from the source to less executed node */
		for(int k = times.size() - 1; k >= 0; k--) {
			source = source.subsume(CirAnnotation.cov_stmt(execution, times.get(k)));
		}
	}
	/**
	 * It extends the source to the subsumed expression and coverage-requirement.
	 * @param source
	 * @throws Exception
	 */
	private void extend_annotation_node_on_condition(CirAnnotationNode source) throws Exception {
		CirExecution execution = source.get_annotation().get_execution();
		SymbolExpression condition = source.get_annotation().get_symb_value();
		if(condition instanceof SymbolConstant) {
			execution = this.get_available_check_point(execution, SymbolFactory.sym_constant(Boolean.TRUE));
			source.subsume(CirAnnotation.cov_stmt(execution, 1));
		}
		else if(condition instanceof SymbolBinaryExpression) {
			COperator op = ((SymbolBinaryExpression) condition).get_operator().get_operator();
			SymbolExpression loperand = ((SymbolBinaryExpression) condition).get_loperand();
			SymbolExpression roperand = ((SymbolBinaryExpression) condition).get_roperand();
			if(op == COperator.greater_tn) {
				this.extend_annotation_node_on_condition(source.subsume(CirAnnotation.
						eva_expr(execution, SymbolFactory.greater_eq(loperand, roperand))));
				this.extend_annotation_node_on_condition(source.subsume(CirAnnotation.
						eva_expr(execution, SymbolFactory.not_equals(loperand, roperand))));
			}
			else if(op == COperator.smaller_tn) {
				this.extend_annotation_node_on_condition(source.subsume(CirAnnotation.
						eva_expr(execution, SymbolFactory.smaller_eq(loperand, roperand))));
				this.extend_annotation_node_on_condition(source.subsume(CirAnnotation.
						eva_expr(execution, SymbolFactory.not_equals(loperand, roperand))));
			}
			else if(op == COperator.equal_with) {
				this.extend_annotation_node_on_condition(source.subsume(CirAnnotation.
						eva_expr(execution, SymbolFactory.greater_eq(loperand, roperand))));
				this.extend_annotation_node_on_condition(source.subsume(CirAnnotation.
						eva_expr(execution, SymbolFactory.smaller_eq(loperand, roperand))));
			}
			else {
				execution = this.get_available_check_point(execution, SymbolFactory.sym_constant(Boolean.TRUE));
				this.extend_annotation_node_on_condition(source.subsume(CirAnnotation.eva_expr(execution, Boolean.TRUE)));
			}
		}
		else {
			execution = this.get_available_check_point(execution, SymbolFactory.sym_constant(Boolean.TRUE));
			this.extend_annotation_node_on_condition(source.subsume(CirAnnotation.eva_expr(execution, Boolean.TRUE)));
		}
	}
	/**
	 * @param source
	 * @throws Exception
	 */
	private void extend_annotation_node_in_eva_expr(CirAnnotationNode source) throws Exception {
		/* 1. capture the execution point and the sub_conditions */
		CirAnnotation annotation = source.get_annotation();
		CirExecution execution = annotation.get_execution(), check_point;
		SymbolExpression condition = annotation.get_symb_value();
		Collection<SymbolExpression> conditions = this.get_conditions_in(condition);
		
		/* 2. it generates the subsumed annotations from sub_conditions */
		for(SymbolExpression sub_condition : conditions) {
			check_point = this.get_available_check_point(execution, sub_condition);
			source = source.subsume(CirAnnotation.eva_expr(check_point, sub_condition));
			this.extend_annotation_node_on_condition(source);
		}
		
		/* 3. it subsumes to the final coverage point if no subsumed exists */
		if(conditions.isEmpty()) {
			condition = SymbolFactory.sym_constant(Boolean.TRUE);
			check_point = this.get_available_check_point(execution, condition);
			this.extend_annotation_node(source.subsume(CirAnnotation.cov_stmt(check_point, 1)));
		}
	}
	/**
	 * --> cov_stmt
	 * @param source
	 * @throws Exception
	 */
	private void extend_annotation_node_in_trp_stmt(CirAnnotationNode source) throws Exception {
		CirExecution execution = source.get_annotation().get_execution();
		execution = this.get_available_check_point(execution, SymbolFactory.sym_constant(Boolean.TRUE));
		this.extend_annotation_node(source.subsume(CirAnnotation.cov_stmt(execution, 1)));
	}
	/* muta and diff class */
	/**
	 * @param source
	 * @throws Exception
	 */
	private void extend_annotation_node_by_bool(CirAnnotationNode source) throws Exception {
		/* 1. declarations and data getters */
		CirAnnotation annotation = source.get_annotation();
		CirExecution execution = annotation.get_execution();
		SymbolExpression value = annotation.get_symb_value();
		CirLogicClass logic_type = annotation.get_logic_type();
		CirNode store_unit = annotation.get_store_unit();
		
		/* 2. value-class based translation */
		SymbolExpression next_value = null;
		if(value == CirValueScope.expt_value) {
			this.extend_annotation_node(source.subsume(CirAnnotation.trp_stmt(execution)));
		}
		else if(value instanceof SymbolConstant) {
			if(((SymbolConstant) value).get_bool()) {
				next_value = CirValueScope.true_value;
			}
			else {
				next_value = CirValueScope.fals_value;
			}
		}
		else if(value == CirValueScope.true_value) {
			next_value = CirValueScope.bool_value;
		}
		else if(value == CirValueScope.fals_value) {
			next_value = CirValueScope.bool_value;
		}
		else if(value == CirValueScope.bool_value) {
			execution = this.get_available_check_point(execution, SymbolFactory.sym_constant(Boolean.TRUE));
			this.extend_annotation_node(source.subsume(CirAnnotation.cov_stmt(execution, 1)));
		}
		else {
			next_value = CirValueScope.bool_value;
		}
		
		/* 3. mirror-based annotation subsumption */
		if(next_value != null) {
			switch(logic_type) {
			case mut_stmt:	this.extend_annotation_node(source.subsume(CirAnnotation.mut_stmt(execution, next_value))); break;
			case mut_expr:	this.extend_annotation_node(source.subsume(CirAnnotation.mut_expr((CirExpression) store_unit, next_value))); break;
			case mut_refr:	this.extend_annotation_node(source.subsume(CirAnnotation.mut_refr((CirExpression) store_unit, next_value))); break;
			case sub_diff:	this.extend_annotation_node(source.subsume(CirAnnotation.sub_diff((CirExpression) store_unit, next_value))); break;
			case ext_diff:	this.extend_annotation_node(source.subsume(CirAnnotation.ext_diff((CirExpression) store_unit, next_value))); break;
			case xor_diff:	this.extend_annotation_node(source.subsume(CirAnnotation.xor_diff((CirExpression) store_unit, next_value))); break;
			default: 		throw new IllegalArgumentException("Invalid logic_type: " + logic_type);
			}
		}
	}
	/**
	 * @param source
	 * @throws Exception
	 */
	private void extend_annotation_node_by_usig(CirAnnotationNode source) throws Exception {
		/* 1. declarations and data getters */
		CirAnnotation annotation = source.get_annotation();
		CirExecution execution = annotation.get_execution();
		SymbolExpression value = annotation.get_symb_value();
		CirLogicClass logic_type = annotation.get_logic_type();
		CirNode store_unit = annotation.get_store_unit();
		
		/* 2. value-class based translation */
		SymbolExpression next_value = null;
		if(value == CirValueScope.expt_value) {
			this.extend_annotation_node(source.subsume(CirAnnotation.trp_stmt(execution)));
		}
		else if(value instanceof SymbolConstant) {
			if(((SymbolConstant) value).get_long() != 0) {
				next_value = CirValueScope.post_value;
			}
			else {
				next_value = CirValueScope.zero_value;
			}
		}
		else if(value == CirValueScope.post_value) {
			next_value = CirValueScope.nneg_value;
		}
		else if(value == CirValueScope.zero_value) {
			next_value = CirValueScope.nneg_value;
		}
		else if(value == CirValueScope.nneg_value) {
			execution = this.get_available_check_point(execution, SymbolFactory.sym_constant(Boolean.TRUE));
			this.extend_annotation_node(source.subsume(CirAnnotation.cov_stmt(execution, 1)));
		}
		else {
			next_value = CirValueScope.nneg_value;
		}
		
		/* 3. mirror-based annotation subsuming */
		if(next_value != null) {
			switch(logic_type) {
			case mut_expr:	this.extend_annotation_node(source.subsume(CirAnnotation.mut_expr((CirExpression) store_unit, next_value))); break;
			case mut_refr:	this.extend_annotation_node(source.subsume(CirAnnotation.mut_refr((CirExpression) store_unit, next_value))); break;
			default: 		throw new IllegalArgumentException("Invalid logic_type: " + logic_type);
			}
		}
	}
	/**
	 * @param source
	 * @throws Exception
	 */
	private void extend_annotation_node_by_addr(CirAnnotationNode source) throws Exception {
		/* 1. declarations and data getters */
		CirAnnotation annotation = source.get_annotation();
		CirExecution execution = annotation.get_execution();
		SymbolExpression value = annotation.get_symb_value();
		CirLogicClass logic_type = annotation.get_logic_type();
		CirNode store_unit = annotation.get_store_unit();
		
		/* 2. value-class based translation */
		SymbolExpression next_value = null;
		if(value == CirValueScope.expt_value) {
			this.extend_annotation_node(source.subsume(CirAnnotation.trp_stmt(execution)));
		}
		else if(value instanceof SymbolConstant) {
			if(((SymbolConstant) value).get_long() != 0) {
				next_value = CirValueScope.nnul_value;
			}
			else {
				next_value = CirValueScope.null_value;
			}
		}
		else if(value == CirValueScope.null_value) {
			next_value = CirValueScope.addr_value;
		}
		else if(value == CirValueScope.nnul_value) {
			next_value = CirValueScope.addr_value;
		}
		else if(value == CirValueScope.addr_value) {
			execution = this.get_available_check_point(execution, SymbolFactory.sym_constant(Boolean.TRUE));
			this.extend_annotation_node(source.subsume(CirAnnotation.cov_stmt(execution, 1)));
		}
		else {
			next_value = CirValueScope.addr_value;
		}
		
		/* 3. mirror-based annotation subsuming */
		if(next_value != null) {
			switch(logic_type) {
			case mut_expr:	this.extend_annotation_node(source.subsume(CirAnnotation.mut_expr((CirExpression) store_unit, next_value))); break;
			case mut_refr:	this.extend_annotation_node(source.subsume(CirAnnotation.mut_refr((CirExpression) store_unit, next_value))); break;
			case sub_diff:	this.extend_annotation_node(source.subsume(CirAnnotation.sub_diff((CirExpression) store_unit, next_value))); break;
			default: 		throw new IllegalArgumentException("Invalid logic_type: " + logic_type);
			}
		}
	}
	/**
	 * @param source
	 * @throws Exception
	 */
	private void extend_annotation_node_by_numb(CirAnnotationNode source) throws Exception {
		/* 1. declarations and data getters */
		CirAnnotation annotation = source.get_annotation();
		CirExecution execution = annotation.get_execution();
		SymbolExpression value = annotation.get_symb_value();
		CirLogicClass logic_type = annotation.get_logic_type();
		CirNode store_unit = annotation.get_store_unit();
		
		/* 2. value-class based translation */
		List<SymbolExpression> next_values = new ArrayList<SymbolExpression>();
		if(value == CirValueScope.expt_value) {
			this.extend_annotation_node(source.subsume(CirAnnotation.trp_stmt(execution)));
		}
		else if(value instanceof SymbolConstant) {
			if(((SymbolConstant) value).get_double() > 0) {
				next_values.add(CirValueScope.post_value);
			}
			else if(((SymbolConstant) value).get_double() < 0) {
				next_values.add(CirValueScope.negt_value);
			}
			else {
				next_values.add(CirValueScope.zero_value);
			}
		}
		else if(value == CirValueScope.post_value) {
			next_values.add(CirValueScope.nneg_value);
			next_values.add(CirValueScope.nzro_value);
		}
		else if(value == CirValueScope.zero_value) {
			next_values.add(CirValueScope.nneg_value);
			next_values.add(CirValueScope.npos_value);
		}
		else if(value == CirValueScope.negt_value) {
			next_values.add(CirValueScope.npos_value);
			next_values.add(CirValueScope.nzro_value);
		}
		else if(value == CirValueScope.npos_value) {
			next_values.add(CirValueScope.numb_value);
		}
		else if(value == CirValueScope.nneg_value) {
			next_values.add(CirValueScope.numb_value);
		}
		else if(value == CirValueScope.nzro_value) {
			next_values.add(CirValueScope.numb_value);
		}
		else if(value == CirValueScope.numb_value) {
			execution = this.get_available_check_point(execution, SymbolFactory.sym_constant(Boolean.TRUE));
			this.extend_annotation_node(source.subsume(CirAnnotation.cov_stmt(execution, 1)));
		}
		else {
			next_values.add(CirValueScope.numb_value);
		}
		
		/* 3. mirror-based generation and susbuming */
		for(SymbolExpression next_value : next_values) {
			switch(logic_type) {
			case mut_stmt:	this.extend_annotation_node(source.subsume(CirAnnotation.mut_stmt(execution, next_value))); break;
			case mut_expr:	this.extend_annotation_node(source.subsume(CirAnnotation.mut_expr((CirExpression) store_unit, next_value))); break;
			case mut_refr:	this.extend_annotation_node(source.subsume(CirAnnotation.mut_refr((CirExpression) store_unit, next_value))); break;
			case sub_diff:	this.extend_annotation_node(source.subsume(CirAnnotation.sub_diff((CirExpression) store_unit, next_value))); break;
			case ext_diff:	this.extend_annotation_node(source.subsume(CirAnnotation.ext_diff((CirExpression) store_unit, next_value))); break;
			case xor_diff:	this.extend_annotation_node(source.subsume(CirAnnotation.xor_diff((CirExpression) store_unit, next_value))); break;
			default: 		throw new IllegalArgumentException("Invalid logic_type: " + logic_type);
			}
		}
	}
	/**
	 * @param source
	 * @throws Exception
	 */
	private void extend_annotation_node_by_auto(CirAnnotationNode source) throws Exception {
		CirExecution execution = source.get_annotation().get_execution();
		execution = this.get_available_check_point(execution, SymbolFactory.sym_constant(Boolean.TRUE));
		this.extend_annotation_node(source.subsume(CirAnnotation.cov_stmt(execution, 1)));
	}
	/**
	 * @param source
	 * @throws Exception
	 */
	private void extend_annotation_node_in_mut_stmt(CirAnnotationNode source) throws Exception {
		this.extend_annotation_node_by_bool(source);
	}
	/**
	 * @param source
	 * @throws Exception
	 */
	private void extend_annotation_node_in_mut_expr(CirAnnotationNode source) throws Exception {
		switch(source.get_annotation().get_value_type()) {
		case bool:	this.extend_annotation_node_by_bool(source); break;
		case usig:	this.extend_annotation_node_by_usig(source); break;
		case sign:	this.extend_annotation_node_by_numb(source); break;
		case real:	this.extend_annotation_node_by_numb(source); break;
		case addr:	this.extend_annotation_node_by_addr(source); break;
		case auto:	this.extend_annotation_node_by_auto(source); break;
		default: throw new IllegalArgumentException("Invalid: " + source.get_annotation());
		}
	}
	/**
	 * @param source
	 * @throws Exception
	 */
	private void extend_annotation_node_in_mut_refr(CirAnnotationNode source) throws Exception {
		switch(source.get_annotation().get_value_type()) {
		case bool:	this.extend_annotation_node_by_bool(source); break;
		case usig:	this.extend_annotation_node_by_usig(source); break;
		case sign:	this.extend_annotation_node_by_numb(source); break;
		case real:	this.extend_annotation_node_by_numb(source); break;
		case addr:	this.extend_annotation_node_by_addr(source); break;
		case auto:	this.extend_annotation_node_by_auto(source); break;
		default: throw new IllegalArgumentException("Invalid: " + source.get_annotation());
		}
	}
	/**
	 * @param source
	 * @throws Exception
	 */
	private void extend_annotation_node_in_sub_diff(CirAnnotationNode source) throws Exception {
		switch(source.get_annotation().get_value_type()) {
		case bool:	this.extend_annotation_node_by_bool(source); break;
		case usig:	this.extend_annotation_node_by_usig(source); break;
		case sign:	this.extend_annotation_node_by_numb(source); break;
		case real:	this.extend_annotation_node_by_numb(source); break;
		case addr:	this.extend_annotation_node_by_addr(source); break;
		case auto:	this.extend_annotation_node_by_auto(source); break;
		default: throw new IllegalArgumentException("Invalid: " + source.get_annotation());
		}
	}
	/**
	 * @param source
	 * @throws Exception
	 */
	private void extend_annotation_node_in_ext_diff(CirAnnotationNode source) throws Exception {
		switch(source.get_annotation().get_value_type()) {
		case bool:	this.extend_annotation_node_by_bool(source); break;
		case usig:	this.extend_annotation_node_by_usig(source); break;
		case sign:	this.extend_annotation_node_by_numb(source); break;
		case real:	this.extend_annotation_node_by_numb(source); break;
		case addr:	this.extend_annotation_node_by_addr(source); break;
		case auto:	this.extend_annotation_node_by_auto(source); break;
		default: throw new IllegalArgumentException("Invalid: " + source.get_annotation());
		}
	}
	/**
	 * @param source
	 * @throws Exception
	 */
	private void extend_annotation_node_in_xor_diff(CirAnnotationNode source) throws Exception {
		switch(source.get_annotation().get_value_type()) {
		case bool:	this.extend_annotation_node_by_bool(source); break;
		case usig:	this.extend_annotation_node_by_usig(source); break;
		case sign:	this.extend_annotation_node_by_numb(source); break;
		case real:	this.extend_annotation_node_by_numb(source); break;
		case addr:	this.extend_annotation_node_by_addr(source); break;
		case auto:	this.extend_annotation_node_by_auto(source); break;
		default: throw new IllegalArgumentException("Invalid: " + source.get_annotation());
		}
	}
	
}
