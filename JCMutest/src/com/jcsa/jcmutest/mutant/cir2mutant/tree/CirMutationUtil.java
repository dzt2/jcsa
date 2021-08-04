package com.jcsa.jcmutest.mutant.cir2mutant.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
import com.jcsa.jcparse.flwa.depend.CDependGraph;
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
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlowType;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionPath;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionPathFinder;
import com.jcsa.jcparse.lang.irlang.stmt.CirArgumentList;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCallStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirTagStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirWaitAssignStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symbol.SymbolBinaryExpression;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.lang.symbol.SymbolNode;
import com.jcsa.jcparse.parse.symbol.SymbolEvaluator;
import com.jcsa.jcparse.test.state.CStateNode;
import com.jcsa.jcparse.test.state.CStatePath;


/**
 * It provides a set of interfaces for constructing the mutation tree and the 
 * generation of the annotations from CirAttribute statically.
 * 
 * @author yukimula
 *
 */
class CirMutationUtil {
	
	/* singleton */	/** constructor **/		private	CirMutationUtil() {}
	protected static final CirMutationUtil util = new CirMutationUtil();
	
	/* precondition tree construction */
	/**
	 * @param flow
	 * @return 	the path constraint for covering the execution flow or null if no
	 * 			constraint is really needed at that point
	 * @throws Exception
	 */
	private CirAttribute get_path_constraint(CirExecutionFlow flow) throws Exception {
		CirAttribute constraint;
		if(flow.get_type() == CirExecutionFlowType.true_flow) {				/* condition as true at if-statement */
			CirStatement statement = flow.get_source().get_statement();
			CirExpression expression;
			if(statement instanceof CirIfStatement) {
				expression = ((CirIfStatement) statement).get_condition();
			}
			else {
				expression = ((CirCaseStatement) statement).get_condition();
			}
			constraint = CirAttribute.new_constraint(flow.get_source(), expression, true);
		}
		else if(flow.get_type() == CirExecutionFlowType.fals_flow) {		/* condition as false at if-statement */
			CirStatement statement = flow.get_source().get_statement();
			CirExpression expression;
			if(statement instanceof CirIfStatement) {
				expression = ((CirIfStatement) statement).get_condition();
			}
			else {
				expression = ((CirCaseStatement) statement).get_condition();
			}
			constraint = CirAttribute.new_constraint(flow.get_source(), expression, false);
		}
		else if(flow.get_type() == CirExecutionFlowType.call_flow) {		/* coverage constraint at call-point */
			constraint = CirAttribute.new_cover_count(flow.get_source(), 1);
		}
		else if(flow.get_type() == CirExecutionFlowType.retr_flow) {		/* coverage constraint at wait-point */
			constraint = CirAttribute.new_cover_count(flow.get_target(), 1);
		}
		else {																/* no constraint is specified otherwise */
			constraint = null;
		}
		return constraint;
	}
	/**
	 * @param execution
	 * @return generate an execution path statically from function entry to target execution
	 * @throws Exception
	 */
	private CirExecutionPath get_execution_path(CirExecution execution) throws Exception {
		CirExecutionPath path = new CirExecutionPath(execution.get_graph().get_entry());
		CirExecutionPathFinder.finder.vf_extend(path, execution); return path;
	}
	/**
	 * @param dependence_graph
	 * @param execution
	 * @return generate an execution path statically using dependence graph to target node
	 * @throws Exception
	 */
	private CirExecutionPath get_execution_path(CDependGraph dependence_graph, CirExecution execution) throws Exception {
		if(dependence_graph == null) return this.get_execution_path(execution);
		return CirExecutionPathFinder.finder.dependence_path(dependence_graph, execution);
	}
	/**
	 * @param state_path
	 * @param execution
	 * @return generate a concrete execution path using dynamic analysis until the target
	 * @throws Exception
	 */
	private CirExecutionPath get_execution_path(CStatePath state_path, CirExecution execution) throws Exception {
		if(state_path == null || state_path.size() == 0) return this.get_execution_path(execution);
		CirExecutionPath path = new CirExecutionPath(state_path.get_node(0).get_execution());
		for(CStateNode state_node : state_path.get_nodes()) {
			CirExecutionPathFinder.finder.vf_extend(path, state_node.get_execution());
			if(state_node.get_execution() == execution) { return path; }
		}
		return path;
	}
	/**
	 * @param root
	 * @param mutation
	 * @param path
	 * @return	construct a reachability path from root to the target mutation using
	 * 			the particular sequence of concrete execution flows as previous path
	 * 			and return the infection edges as the final outputs.
	 * @throws Exception
	 */
	private CirMutationTreeEdge construct_precondition_path(CirMutationTreeNode root,
			CirMutation mutation, CirExecutionPath path) throws Exception {
		if(root == null) {
			throw new IllegalArgumentException("Invalid root: null");
		}
		else if(mutation == null) {
			throw new IllegalArgumentException("Invalid mutation: null");
		}
		else {
			/** I. construct precondition path from root to coverage point **/
			if(path != null) {
				for(CirExecutionEdge edge : path.get_edges()) {
					CirAttribute constraint = this.get_path_constraint(edge.get_flow());
					if(constraint != null) {
						root = root.link(CirMutationTreeType.precondition, 
								constraint, CirMutationTreeFlow.execute).get_target();
					}
				}
			}
			
			/** II. link the path target until the execution point if needed **/
			if(root.get_attribute().get_execution() != mutation.get_execution()) {
				root = root.link(CirMutationTreeType.precondition, 
						CirAttribute.new_cover_count(mutation.get_execution(), 1), 
						CirMutationTreeFlow.execute).get_target();
			}
			
			/** III. construct infection-sub-path in the root context **/
			root = root.link(CirMutationTreeType.midcondition, mutation.
					get_constraint(), CirMutationTreeFlow.execute).get_target();
			return root.link(CirMutationTreeType.midcondition, 
					mutation.get_init_error(), CirMutationTreeFlow.infect);
		}
	}
	/**
	 * @param tree
	 * @return construct the precondition tree in the context statically without context model
	 * @throws Exception
	 */
	protected Iterable<CirMutationTreeEdge> construct_precondition_tree(CirMutationTree tree) throws Exception {
		if(tree == null) {
			throw new IllegalArgumentException("Invalid tree as null");
		}
		else {
			/** I. collect the mutation and correlate them with execution **/
			Map<CirExecution, Collection<CirMutation>> mutations
					= new HashMap<CirExecution, Collection<CirMutation>>();
			Iterable<CirMutation> cir_mutations = tree.get_cir_mutations();
			for(CirMutation cir_mutation : cir_mutations) {
				CirExecution execution = cir_mutation.get_execution();
				if(!mutations.containsKey(execution)) {
					mutations.put(execution, new ArrayList<CirMutation>());
				}
				mutations.get(execution).add(cir_mutation);
			}
			
			/** II. construct the precondition tree for each execution **/
			Collection<CirMutationTreeEdge> edges = new ArrayList<CirMutationTreeEdge>();
			for(CirExecution execution : mutations.keySet()) {
				cir_mutations = mutations.get(execution);
				CirExecutionPath path = this.get_execution_path(execution);
				for(CirMutation cir_mutation : cir_mutations) {
					edges.add(this.construct_precondition_path(
							tree.get_root(), cir_mutation, path));
				}
			}
			return edges;
		}
	}
	/**
	 * @param tree
	 * @param dependence_graph
	 * @return construct the precondition tree in the context statically using PDG model
	 * @throws Exception
	 */
	protected Iterable<CirMutationTreeEdge> construct_precondition_tree(
			CirMutationTree tree, CDependGraph dependence_graph) throws Exception {
		if(tree == null) {
			throw new IllegalArgumentException("Invalid tree: null");
		}
		else if(dependence_graph == null) {
			return this.construct_precondition_tree(tree);
		}
		else {
			/** I. collect the mutation and correlate them with execution **/
			Map<CirExecution, Collection<CirMutation>> mutations
					= new HashMap<CirExecution, Collection<CirMutation>>();
			Iterable<CirMutation> cir_mutations = tree.get_cir_mutations();
			for(CirMutation cir_mutation : cir_mutations) {
				CirExecution execution = cir_mutation.get_execution();
				if(!mutations.containsKey(execution)) {
					mutations.put(execution, new ArrayList<CirMutation>());
				}
				mutations.get(execution).add(cir_mutation);
			}
			
			/** II. construct the precondition tree for each execution **/
			Collection<CirMutationTreeEdge> edges = new ArrayList<CirMutationTreeEdge>();
			for(CirExecution execution : mutations.keySet()) {
				cir_mutations = mutations.get(execution);
				CirExecutionPath path = this.get_execution_path(dependence_graph, execution);
				for(CirMutation cir_mutation : cir_mutations) {
					edges.add(this.construct_precondition_path(
							tree.get_root(), cir_mutation, path));
				}
			}
			return edges;
		}
	}
	/**
	 * @param tree
	 * @param state_path
	 * @return construct the precondition tree using concrete path captured from dynamic analysis
	 * @throws Exception
	 */
	protected Iterable<CirMutationTreeEdge> construct_precondition_tree(CirMutationTree tree, CStatePath state_path) throws Exception {
		if(tree == null) {
			throw new IllegalArgumentException("Invalid tree: null");
		}
		else if(state_path == null) {
			return this.construct_precondition_tree(tree);
		}
		else {
			/** I. collect the mutation and correlate them with execution **/
			Map<CirExecution, Collection<CirMutation>> mutations
					= new HashMap<CirExecution, Collection<CirMutation>>();
			Iterable<CirMutation> cir_mutations = tree.get_cir_mutations();
			for(CirMutation cir_mutation : cir_mutations) {
				CirExecution execution = cir_mutation.get_execution();
				if(!mutations.containsKey(execution)) {
					mutations.put(execution, new ArrayList<CirMutation>());
				}
				mutations.get(execution).add(cir_mutation);
			}
			
			/** II. construct the precondition tree for each execution **/
			Collection<CirMutationTreeEdge> edges = new ArrayList<CirMutationTreeEdge>();
			for(CirExecution execution : mutations.keySet()) {
				cir_mutations = mutations.get(execution);
				CirExecutionPath path = this.get_execution_path(state_path, execution);
				for(CirMutation cir_mutation : cir_mutations) {
					edges.add(this.construct_precondition_path(
							tree.get_root(), cir_mutation, path));
				}
			}
			return edges;
		}
	}
	
	/* propagation methods to generate next errors directly */
	/**
	 * propagate from the source error to the next errors in propagation
	 * @param error
	 * @param errors
	 * @throws Exception
	 */
	private void propagate_on(CirAttribute error, Collection<CirAttribute> errors) throws Exception {
		errors.clear();
		if(error == null) {
			throw new IllegalArgumentException("Invalid error: null");
		}
		else if(error instanceof CirBlockError) { /* no propagation */ }
		else if(error instanceof CirTrapsError) { /* no propagation */ }
		else if(error instanceof CirFlowsError) {
			this.propagate_on_flows_error((CirFlowsError) error, errors);
		}
		else if(error instanceof CirValueError) {
			this.propagate_on_value_error((CirValueError) error, errors);
		}
		else if(error instanceof CirReferError) {
			this.propagate_on_refer_error((CirReferError) error, errors);
		}
		else if(error instanceof CirStateError) { /* no propagation */ }
		else {
			throw new IllegalArgumentException("Invalid: " + error);
		}
	}
	/**
	 * @param error
	 * @param errors
	 * @throws Exception
	 */
	private void propagate_on_flows_error(CirFlowsError error, Collection<CirAttribute> errors) throws Exception {
		/* determine the original and mutated target execution */
		CirExecution orig_target = error.get_original_flow().get_target();
		CirExecution muta_target = error.get_mutation_flow().get_target();
		
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
		
		/* generate the next errors in propagation directly */
		for(CirExecution add_execution : add_executions) {
			if(!(add_execution.get_statement() instanceof CirTagStatement)) {
				errors.add(CirAttribute.new_block_error(add_execution, true));
			}
		}
		for(CirExecution del_execution : del_executions) {
			if(!(del_execution.get_statement() instanceof CirTagStatement)) {
				errors.add(CirAttribute.new_block_error(del_execution, false));
			}
		}
	}
	/**
	 * @param error
	 * @param errors
	 * @throws Exception
	 */
	private void propagate_on_value_error(CirValueError error, Collection<CirAttribute> errors) throws Exception {
		/* declarations */
		CirExpression child = error.get_orig_expression();
		CirNode parent = child.get_parent();
		SymbolExpression muta_child = error.get_muta_expression();
		
		/* syntax-directed translation */
		if(parent == null) {
			throw new IllegalArgumentException("Invalid parent: null");
		}
		else if(parent instanceof CirDeferExpression) {
			errors.add(CirAttribute.new_refer_error((CirExpression) parent, SymbolFactory.dereference(muta_child)));
		}
		else if(parent instanceof CirFieldExpression) {
			errors.add(CirAttribute.new_refer_error((CirExpression) parent, 
					SymbolFactory.field_expression(muta_child, 
							((CirFieldExpression) parent).get_field().get_name())));
		}
		else if(parent instanceof CirAddressExpression) { /* no propagation */ }
		else if(parent instanceof CirCastExpression) {
			errors.add(CirAttribute.new_value_error((CirExpression) parent, 
					SymbolFactory.cast_expression(((CirCastExpression) parent).get_type().get_typename(), muta_child)));
		}
		else if(parent instanceof CirInitializerBody) {
			List<Object> elements = new ArrayList<Object>();
			for(int k = 0; k < ((CirInitializerBody) parent).number_of_elements(); k++) {
				CirExpression orig_element = ((CirInitializerBody) parent).get_element(k);
				if(orig_element == child) {
					elements.add(muta_child);
				}
				else {
					elements.add(SymbolFactory.sym_expression(orig_element));
				}
			}
			errors.add(CirAttribute.new_value_error(
					(CirExpression) parent, SymbolFactory.initializer_list(elements)));
		}
		else if(parent instanceof CirWaitExpression) {
			CirExecution wait_execution = parent.execution_of();
			CirExecution call_execution = wait_execution.
					get_graph().get_execution(wait_execution.get_id() - 1);
			CirCallStatement call_statement = (CirCallStatement) call_execution.get_statement();
			CirArgumentList argument_list = call_statement.get_arguments();
			List<Object> arguments = new ArrayList<Object>();
			for(int k = 0; k < argument_list.number_of_arguments(); k++) {
				arguments.add(argument_list.get_argument(k));
			}
			errors.add(CirAttribute.new_value_error((CirExpression) parent, 
					SymbolFactory.call_expression(muta_child, arguments)));
		}
		else if(parent instanceof CirComputeExpression) {
			COperator operator = ((CirComputeExpression) parent).get_operator();
			SymbolExpression muta_expression;
			switch(operator) {
			case negative:
			{
				muta_expression = SymbolFactory.arith_neg(muta_child);
				break;
			}
			case bit_not:
			{
				muta_expression = SymbolFactory.bitws_rsv(muta_child);
				break;
			}
			case logic_not:
			{
				muta_expression = SymbolFactory.logic_not(muta_child);
				break;
			}
			case arith_add:
			{
				if(((CirComputeExpression) parent).get_operand(0) == child) {
					muta_expression = SymbolFactory.arith_add(
							((CirComputeExpression) parent).get_data_type(),
							muta_child, 
							((CirComputeExpression) parent).get_operand(1));
				}
				else {
					muta_expression = SymbolFactory.arith_add(
							((CirComputeExpression) parent).get_data_type(),
							((CirComputeExpression) parent).get_operand(0), 
							muta_child);
				}
				break;
			}
			case arith_sub:
			{
				if(((CirComputeExpression) parent).get_operand(0) == child) {
					muta_expression = SymbolFactory.arith_sub(
							((CirComputeExpression) parent).get_data_type(),
							muta_child, 
							((CirComputeExpression) parent).get_operand(1));
				}
				else {
					muta_expression = SymbolFactory.arith_sub(
							((CirComputeExpression) parent).get_data_type(),
							((CirComputeExpression) parent).get_operand(0), 
							muta_child);
				}
				break;
			}
			case arith_mul:
			{
				if(((CirComputeExpression) parent).get_operand(0) == child) {
					muta_expression = SymbolFactory.arith_mul(
							((CirComputeExpression) parent).get_data_type(),
							muta_child, 
							((CirComputeExpression) parent).get_operand(1));
				}
				else {
					muta_expression = SymbolFactory.arith_mul(
							((CirComputeExpression) parent).get_data_type(),
							((CirComputeExpression) parent).get_operand(0), 
							muta_child);
				}
				break;
			}
			case arith_div:
			{
				if(((CirComputeExpression) parent).get_operand(0) == child) {
					muta_expression = SymbolFactory.arith_div(
							((CirComputeExpression) parent).get_data_type(),
							muta_child, 
							((CirComputeExpression) parent).get_operand(1));
				}
				else {
					muta_expression = SymbolFactory.arith_div(
							((CirComputeExpression) parent).get_data_type(),
							((CirComputeExpression) parent).get_operand(0), 
							muta_child);
				}
				break;
			}
			case arith_mod:
			{
				if(((CirComputeExpression) parent).get_operand(0) == child) {
					muta_expression = SymbolFactory.arith_mod(
							((CirComputeExpression) parent).get_data_type(),
							muta_child, 
							((CirComputeExpression) parent).get_operand(1));
				}
				else {
					muta_expression = SymbolFactory.arith_mod(
							((CirComputeExpression) parent).get_data_type(),
							((CirComputeExpression) parent).get_operand(0), 
							muta_child);
				}
				break;
			}
			case bit_and:
			{
				if(((CirComputeExpression) parent).get_operand(0) == child) {
					muta_expression = SymbolFactory.bitws_and(
							((CirComputeExpression) parent).get_data_type(),
							muta_child, 
							((CirComputeExpression) parent).get_operand(1));
				}
				else {
					muta_expression = SymbolFactory.bitws_and(
							((CirComputeExpression) parent).get_data_type(),
							((CirComputeExpression) parent).get_operand(0), 
							muta_child);
				}
				break;
			}
			case bit_or:
			{
				if(((CirComputeExpression) parent).get_operand(0) == child) {
					muta_expression = SymbolFactory.bitws_ior(
							((CirComputeExpression) parent).get_data_type(),
							muta_child, 
							((CirComputeExpression) parent).get_operand(1));
				}
				else {
					muta_expression = SymbolFactory.bitws_ior(
							((CirComputeExpression) parent).get_data_type(),
							((CirComputeExpression) parent).get_operand(0), 
							muta_child);
				}
				break;
			}
			case bit_xor:
			{
				if(((CirComputeExpression) parent).get_operand(0) == child) {
					muta_expression = SymbolFactory.bitws_xor(
							((CirComputeExpression) parent).get_data_type(),
							muta_child, 
							((CirComputeExpression) parent).get_operand(1));
				}
				else {
					muta_expression = SymbolFactory.bitws_xor(
							((CirComputeExpression) parent).get_data_type(),
							((CirComputeExpression) parent).get_operand(0), 
							muta_child);
				}
				break;
			}
			case left_shift:
			{
				if(((CirComputeExpression) parent).get_operand(0) == child) {
					muta_expression = SymbolFactory.bitws_lsh(
							((CirComputeExpression) parent).get_data_type(),
							muta_child, 
							((CirComputeExpression) parent).get_operand(1));
				}
				else {
					muta_expression = SymbolFactory.bitws_lsh(
							((CirComputeExpression) parent).get_data_type(),
							((CirComputeExpression) parent).get_operand(0), 
							muta_child);
				}
				break;
			}
			case righ_shift:
			{
				if(((CirComputeExpression) parent).get_operand(0) == child) {
					muta_expression = SymbolFactory.bitws_rsh(
							((CirComputeExpression) parent).get_data_type(),
							muta_child, 
							((CirComputeExpression) parent).get_operand(1));
				}
				else {
					muta_expression = SymbolFactory.bitws_rsh(
							((CirComputeExpression) parent).get_data_type(),
							((CirComputeExpression) parent).get_operand(0), 
							muta_child);
				}
				break;
			}
			case logic_and:
			{
				if(((CirComputeExpression) parent).get_operand(0) == child) {
					muta_expression = SymbolFactory.logic_and(
							muta_child, 
							((CirComputeExpression) parent).get_operand(1));
				}
				else {
					muta_expression = SymbolFactory.logic_and(
							((CirComputeExpression) parent).get_operand(0), 
							muta_child);
				}
				break;
			}
			case logic_or:
			{
				if(((CirComputeExpression) parent).get_operand(0) == child) {
					muta_expression = SymbolFactory.logic_ior(
							muta_child, 
							((CirComputeExpression) parent).get_operand(1));
				}
				else {
					muta_expression = SymbolFactory.logic_ior(
							((CirComputeExpression) parent).get_operand(0), 
							muta_child);
				}
				break;
			}
			case greater_tn:
			{
				if(((CirComputeExpression) parent).get_operand(0) == child) {
					muta_expression = SymbolFactory.greater_tn(
							muta_child, 
							((CirComputeExpression) parent).get_operand(1));
				}
				else {
					muta_expression = SymbolFactory.greater_tn(
							((CirComputeExpression) parent).get_operand(0), 
							muta_child);
				}
				break;
			}
			case greater_eq:
			{
				if(((CirComputeExpression) parent).get_operand(0) == child) {
					muta_expression = SymbolFactory.greater_eq(
							muta_child, 
							((CirComputeExpression) parent).get_operand(1));
				}
				else {
					muta_expression = SymbolFactory.greater_eq(
							((CirComputeExpression) parent).get_operand(0), 
							muta_child);
				}
				break;
			}
			case smaller_tn:
			{
				if(((CirComputeExpression) parent).get_operand(0) == child) {
					muta_expression = SymbolFactory.smaller_tn(
							muta_child, 
							((CirComputeExpression) parent).get_operand(1));
				}
				else {
					muta_expression = SymbolFactory.smaller_tn(
							((CirComputeExpression) parent).get_operand(0), 
							muta_child);
				}
				break;
			}
			case smaller_eq:
			{
				if(((CirComputeExpression) parent).get_operand(0) == child) {
					muta_expression = SymbolFactory.smaller_eq(
							muta_child, 
							((CirComputeExpression) parent).get_operand(1));
				}
				else {
					muta_expression = SymbolFactory.smaller_eq(
							((CirComputeExpression) parent).get_operand(0), 
							muta_child);
				}
				break;
			}
			case equal_with:
			{
				if(((CirComputeExpression) parent).get_operand(0) == child) {
					muta_expression = SymbolFactory.equal_with(
							muta_child, 
							((CirComputeExpression) parent).get_operand(1));
				}
				else {
					muta_expression = SymbolFactory.equal_with(
							((CirComputeExpression) parent).get_operand(0), 
							muta_child);
				}
				break;
			}
			case not_equals:
			{
				if(((CirComputeExpression) parent).get_operand(0) == child) {
					muta_expression = SymbolFactory.not_equals(
							muta_child, 
							((CirComputeExpression) parent).get_operand(1));
				}
				else {
					muta_expression = SymbolFactory.not_equals(
							((CirComputeExpression) parent).get_operand(0), 
							muta_child);
				}
				break;
			}
			default:
			{
				throw new IllegalArgumentException("Invalid operator: null");
			}
			}
			errors.add(CirAttribute.new_value_error((CirExpression) parent, muta_expression));
		}
		else if(parent instanceof CirArgumentList) {
			CirCallStatement call_statement = (CirCallStatement) parent.get_parent();
			CirExecution call_execution = call_statement.execution_of();
			CirExecution wait_execution = call_execution.get_graph().
							get_execution(call_execution.get_id() + 1);
			CirWaitAssignStatement wait_statement = 
					(CirWaitAssignStatement) wait_execution.get_statement();
			CirExpression orig_expression = wait_statement.get_rvalue();
			
			CirArgumentList alist = (CirArgumentList) parent;
			List<Object> arguments = new ArrayList<Object>();
			for(int k = 0; k < alist.number_of_arguments(); k++) {
				arguments.add(alist.get_argument(k));
			}
			SymbolExpression muta_expression = SymbolFactory.call_expression(muta_child, arguments);
			
			errors.add(CirAttribute.new_value_error(orig_expression, muta_expression));
		}
		else if(parent instanceof CirAssignStatement) {
			if(((CirAssignStatement) parent).get_lvalue() == child) { /* no propagation more */ }
			else {
				errors.add(CirAttribute.new_state_error(
						((CirAssignStatement) parent).get_lvalue(), muta_child));
			}
		}
		else if(parent instanceof CirIfStatement || parent instanceof CirCaseStatement) {
			/* no more error propagation */
		}
		else {
			throw new IllegalArgumentException("Invalid: " + parent);
		}
	}
	/**
	 * @param error
	 * @param errors
	 * @throws Exception
	 */
	private void propagate_on_refer_error(CirReferError error, Collection<CirAttribute> errors) throws Exception {
		/* declarations */
		CirExpression child = error.get_orig_expression();
		CirNode parent = child.get_parent();
		SymbolExpression muta_child = error.get_muta_expression();
		
		/* syntax-directed translation */
		if(parent == null) {
			throw new IllegalArgumentException("Invalid parent: null");
		}
		else if(parent instanceof CirDeferExpression) {
			errors.add(CirAttribute.new_refer_error((CirExpression) parent, SymbolFactory.dereference(muta_child)));
		}
		else if(parent instanceof CirFieldExpression) {
			errors.add(CirAttribute.new_refer_error((CirExpression) parent, 
					SymbolFactory.field_expression(muta_child, 
							((CirFieldExpression) parent).get_field().get_name())));
		}
		else if(parent instanceof CirAddressExpression) { 
			errors.add(CirAttribute.new_value_error(
					(CirExpression) parent, SymbolFactory.address_of(muta_child)));
		}
		else if(parent instanceof CirCastExpression) {
			errors.add(CirAttribute.new_value_error((CirExpression) parent, 
					SymbolFactory.cast_expression(((CirCastExpression) parent).get_type().get_typename(), muta_child)));
		}
		else if(parent instanceof CirInitializerBody) {
			List<Object> elements = new ArrayList<Object>();
			for(int k = 0; k < ((CirInitializerBody) parent).number_of_elements(); k++) {
				CirExpression orig_element = ((CirInitializerBody) parent).get_element(k);
				if(orig_element == child) {
					elements.add(muta_child);
				}
				else {
					elements.add(SymbolFactory.sym_expression(orig_element));
				}
			}
			errors.add(CirAttribute.new_value_error(
					(CirExpression) parent, SymbolFactory.initializer_list(elements)));
		}
		else if(parent instanceof CirWaitExpression) {
			CirExecution wait_execution = parent.execution_of();
			CirExecution call_execution = wait_execution.
					get_graph().get_execution(wait_execution.get_id() - 1);
			CirCallStatement call_statement = (CirCallStatement) call_execution.get_statement();
			CirArgumentList argument_list = call_statement.get_arguments();
			List<Object> arguments = new ArrayList<Object>();
			for(int k = 0; k < argument_list.number_of_arguments(); k++) {
				arguments.add(argument_list.get_argument(k));
			}
			errors.add(CirAttribute.new_value_error((CirExpression) parent, 
					SymbolFactory.call_expression(muta_child, arguments)));
		}
		else if(parent instanceof CirComputeExpression) {
			COperator operator = ((CirComputeExpression) parent).get_operator();
			SymbolExpression muta_expression;
			switch(operator) {
			case negative:
			{
				muta_expression = SymbolFactory.arith_neg(muta_child);
				break;
			}
			case bit_not:
			{
				muta_expression = SymbolFactory.bitws_rsv(muta_child);
				break;
			}
			case logic_not:
			{
				muta_expression = SymbolFactory.logic_not(muta_child);
				break;
			}
			case arith_add:
			{
				if(((CirComputeExpression) parent).get_operand(0) == child) {
					muta_expression = SymbolFactory.arith_add(
							((CirComputeExpression) parent).get_data_type(),
							muta_child, 
							((CirComputeExpression) parent).get_operand(1));
				}
				else {
					muta_expression = SymbolFactory.arith_add(
							((CirComputeExpression) parent).get_data_type(),
							((CirComputeExpression) parent).get_operand(0), 
							muta_child);
				}
				break;
			}
			case arith_sub:
			{
				if(((CirComputeExpression) parent).get_operand(0) == child) {
					muta_expression = SymbolFactory.arith_sub(
							((CirComputeExpression) parent).get_data_type(),
							muta_child, 
							((CirComputeExpression) parent).get_operand(1));
				}
				else {
					muta_expression = SymbolFactory.arith_sub(
							((CirComputeExpression) parent).get_data_type(),
							((CirComputeExpression) parent).get_operand(0), 
							muta_child);
				}
				break;
			}
			case arith_mul:
			{
				if(((CirComputeExpression) parent).get_operand(0) == child) {
					muta_expression = SymbolFactory.arith_mul(
							((CirComputeExpression) parent).get_data_type(),
							muta_child, 
							((CirComputeExpression) parent).get_operand(1));
				}
				else {
					muta_expression = SymbolFactory.arith_mul(
							((CirComputeExpression) parent).get_data_type(),
							((CirComputeExpression) parent).get_operand(0), 
							muta_child);
				}
				break;
			}
			case arith_div:
			{
				if(((CirComputeExpression) parent).get_operand(0) == child) {
					muta_expression = SymbolFactory.arith_div(
							((CirComputeExpression) parent).get_data_type(),
							muta_child, 
							((CirComputeExpression) parent).get_operand(1));
				}
				else {
					muta_expression = SymbolFactory.arith_div(
							((CirComputeExpression) parent).get_data_type(),
							((CirComputeExpression) parent).get_operand(0), 
							muta_child);
				}
				break;
			}
			case arith_mod:
			{
				if(((CirComputeExpression) parent).get_operand(0) == child) {
					muta_expression = SymbolFactory.arith_mod(
							((CirComputeExpression) parent).get_data_type(),
							muta_child, 
							((CirComputeExpression) parent).get_operand(1));
				}
				else {
					muta_expression = SymbolFactory.arith_mod(
							((CirComputeExpression) parent).get_data_type(),
							((CirComputeExpression) parent).get_operand(0), 
							muta_child);
				}
				break;
			}
			case bit_and:
			{
				if(((CirComputeExpression) parent).get_operand(0) == child) {
					muta_expression = SymbolFactory.bitws_and(
							((CirComputeExpression) parent).get_data_type(),
							muta_child, 
							((CirComputeExpression) parent).get_operand(1));
				}
				else {
					muta_expression = SymbolFactory.bitws_and(
							((CirComputeExpression) parent).get_data_type(),
							((CirComputeExpression) parent).get_operand(0), 
							muta_child);
				}
				break;
			}
			case bit_or:
			{
				if(((CirComputeExpression) parent).get_operand(0) == child) {
					muta_expression = SymbolFactory.bitws_ior(
							((CirComputeExpression) parent).get_data_type(),
							muta_child, 
							((CirComputeExpression) parent).get_operand(1));
				}
				else {
					muta_expression = SymbolFactory.bitws_ior(
							((CirComputeExpression) parent).get_data_type(),
							((CirComputeExpression) parent).get_operand(0), 
							muta_child);
				}
				break;
			}
			case bit_xor:
			{
				if(((CirComputeExpression) parent).get_operand(0) == child) {
					muta_expression = SymbolFactory.bitws_xor(
							((CirComputeExpression) parent).get_data_type(),
							muta_child, 
							((CirComputeExpression) parent).get_operand(1));
				}
				else {
					muta_expression = SymbolFactory.bitws_xor(
							((CirComputeExpression) parent).get_data_type(),
							((CirComputeExpression) parent).get_operand(0), 
							muta_child);
				}
				break;
			}
			case left_shift:
			{
				if(((CirComputeExpression) parent).get_operand(0) == child) {
					muta_expression = SymbolFactory.bitws_lsh(
							((CirComputeExpression) parent).get_data_type(),
							muta_child, 
							((CirComputeExpression) parent).get_operand(1));
				}
				else {
					muta_expression = SymbolFactory.bitws_lsh(
							((CirComputeExpression) parent).get_data_type(),
							((CirComputeExpression) parent).get_operand(0), 
							muta_child);
				}
				break;
			}
			case righ_shift:
			{
				if(((CirComputeExpression) parent).get_operand(0) == child) {
					muta_expression = SymbolFactory.bitws_rsh(
							((CirComputeExpression) parent).get_data_type(),
							muta_child, 
							((CirComputeExpression) parent).get_operand(1));
				}
				else {
					muta_expression = SymbolFactory.bitws_rsh(
							((CirComputeExpression) parent).get_data_type(),
							((CirComputeExpression) parent).get_operand(0), 
							muta_child);
				}
				break;
			}
			case logic_and:
			{
				if(((CirComputeExpression) parent).get_operand(0) == child) {
					muta_expression = SymbolFactory.logic_and(
							muta_child, 
							((CirComputeExpression) parent).get_operand(1));
				}
				else {
					muta_expression = SymbolFactory.logic_and(
							((CirComputeExpression) parent).get_operand(0), 
							muta_child);
				}
				break;
			}
			case logic_or:
			{
				if(((CirComputeExpression) parent).get_operand(0) == child) {
					muta_expression = SymbolFactory.logic_ior(
							muta_child, 
							((CirComputeExpression) parent).get_operand(1));
				}
				else {
					muta_expression = SymbolFactory.logic_ior(
							((CirComputeExpression) parent).get_operand(0), 
							muta_child);
				}
				break;
			}
			case greater_tn:
			{
				if(((CirComputeExpression) parent).get_operand(0) == child) {
					muta_expression = SymbolFactory.greater_tn(
							muta_child, 
							((CirComputeExpression) parent).get_operand(1));
				}
				else {
					muta_expression = SymbolFactory.greater_tn(
							((CirComputeExpression) parent).get_operand(0), 
							muta_child);
				}
				break;
			}
			case greater_eq:
			{
				if(((CirComputeExpression) parent).get_operand(0) == child) {
					muta_expression = SymbolFactory.greater_eq(
							muta_child, 
							((CirComputeExpression) parent).get_operand(1));
				}
				else {
					muta_expression = SymbolFactory.greater_eq(
							((CirComputeExpression) parent).get_operand(0), 
							muta_child);
				}
				break;
			}
			case smaller_tn:
			{
				if(((CirComputeExpression) parent).get_operand(0) == child) {
					muta_expression = SymbolFactory.smaller_tn(
							muta_child, 
							((CirComputeExpression) parent).get_operand(1));
				}
				else {
					muta_expression = SymbolFactory.smaller_tn(
							((CirComputeExpression) parent).get_operand(0), 
							muta_child);
				}
				break;
			}
			case smaller_eq:
			{
				if(((CirComputeExpression) parent).get_operand(0) == child) {
					muta_expression = SymbolFactory.smaller_eq(
							muta_child, 
							((CirComputeExpression) parent).get_operand(1));
				}
				else {
					muta_expression = SymbolFactory.smaller_eq(
							((CirComputeExpression) parent).get_operand(0), 
							muta_child);
				}
				break;
			}
			case equal_with:
			{
				if(((CirComputeExpression) parent).get_operand(0) == child) {
					muta_expression = SymbolFactory.equal_with(
							muta_child, 
							((CirComputeExpression) parent).get_operand(1));
				}
				else {
					muta_expression = SymbolFactory.equal_with(
							((CirComputeExpression) parent).get_operand(0), 
							muta_child);
				}
				break;
			}
			case not_equals:
			{
				if(((CirComputeExpression) parent).get_operand(0) == child) {
					muta_expression = SymbolFactory.not_equals(
							muta_child, 
							((CirComputeExpression) parent).get_operand(1));
				}
				else {
					muta_expression = SymbolFactory.not_equals(
							((CirComputeExpression) parent).get_operand(0), 
							muta_child);
				}
				break;
			}
			default:
			{
				throw new IllegalArgumentException("Invalid operator: null");
			}
			}
			errors.add(CirAttribute.new_value_error((CirExpression) parent, muta_expression));
		}
		else if(parent instanceof CirArgumentList) {
			CirCallStatement call_statement = (CirCallStatement) parent.get_parent();
			CirExecution call_execution = call_statement.execution_of();
			CirExecution wait_execution = call_execution.get_graph().
							get_execution(call_execution.get_id() + 1);
			CirWaitAssignStatement wait_statement = 
					(CirWaitAssignStatement) wait_execution.get_statement();
			CirExpression orig_expression = wait_statement.get_rvalue();
			
			CirArgumentList alist = (CirArgumentList) parent;
			List<Object> arguments = new ArrayList<Object>();
			for(int k = 0; k < alist.number_of_arguments(); k++) {
				arguments.add(alist.get_argument(k));
			}
			SymbolExpression muta_expression = SymbolFactory.call_expression(muta_child, arguments);
			
			errors.add(CirAttribute.new_value_error(orig_expression, muta_expression));
		}
		else if(parent instanceof CirAssignStatement) {
			if(((CirAssignStatement) parent).get_lvalue() == child) { /* no propagation more */ }
			else {
				errors.add(CirAttribute.new_state_error(
						((CirAssignStatement) parent).get_lvalue(), muta_child));
			}
		}
		else if(parent instanceof CirIfStatement || parent instanceof CirCaseStatement) {
			/* no more error propagation */
		}
		else {
			throw new IllegalArgumentException("Invalid: " + parent);
		}
	}
	/**
	 * recursively generate errors using propagation analysis statically
	 * @param node
	 * @throws Exception
	 */
	private void construct_poscondition_tree(CirMutationTreeNode node) throws Exception {
		Collection<CirAttribute> next_errors = new HashSet<CirAttribute>();
		this.propagate_on(node.get_attribute(), next_errors);
		for(CirAttribute next_error : next_errors) {
			CirMutationTreeNode next_node;
			next_node = node.link(CirMutationTreeType.poscondition, 
					next_error, CirMutationTreeFlow.propagate).get_target();
			this.construct_poscondition_tree(next_node);
		}
	}
	/**
	 * @param tree
	 * @throws Exception
	 */
	protected void construct_poscondition_tree(CirMutationTree tree) throws Exception {
		for(CirMutationTreeEdge infection_edge : tree.get_infection_edges()) {
			this.construct_poscondition_tree(infection_edge.get_target());
		}
	}
	
	/* basic supporting methods */
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
		Set<SymbolExpression> references = new HashSet<SymbolExpression>();
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
	 * @param execution
	 * @param expression
	 * @return find the previous check-point where the expression should be evaluated
	 * @throws Exception
	 */
	private CirExecution find_previous_point(CirExecution execution, SymbolExpression expression) throws Exception {
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
	
	/* annotation generation based on program analysis */
	/**
	 * generate the concrete annotations from the attribute
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in(CirAttribute attribute, Collection<CirAnnotation> annotations) throws Exception {
		if(attribute == null) {
			throw new IllegalArgumentException("Invalid attribute: null");
		}
		/* constraint-class */
		else if(attribute instanceof CirConstraint) {
			this.generate_annotations_in_constraint((CirConstraint) attribute, annotations);
		}
		else if(attribute instanceof CirCoverCount) {
			this.generate_annotations_in_cover_count((CirCoverCount) attribute, annotations);
		}
		/* statement-error */
		else if(attribute instanceof CirBlockError) {
			this.generate_annotations_in_block_error((CirBlockError) attribute, annotations);
		}
		else if(attribute instanceof CirTrapsError) {
			this.generate_annotations_in_traps_error((CirTrapsError) attribute, annotations);
		}
		else if(attribute instanceof CirFlowsError) {
			this.generate_annotations_in_flows_error((CirFlowsError) attribute, annotations);
		}
		/* expression-error */
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
			throw new IllegalArgumentException("Invalid: " + attribute);
		}
	}
	/**
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_block_error(CirBlockError attribute, 
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
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_traps_error(CirTrapsError attribute,
			Collection<CirAnnotation> annotations) throws Exception {
		annotations.add(CirAnnotation.trp_stmt(attribute.get_execution()));
	}
	/**
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_flows_error(CirFlowsError attribute,
			Collection<CirAnnotation> annotations) throws Exception {
		if(attribute.get_original_flow().get_target() != 
				attribute.get_mutation_flow().get_target()) {
			annotations.add(CirAnnotation.mut_flow(attribute.
					get_original_flow(), attribute.get_mutation_flow()));
		}
	}
	/**
	 * divide into sub-expression, generate subsumed expressions and coverage point
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_constraint(CirConstraint attribute,
			Collection<CirAnnotation> annotations) throws Exception {
		/* 1. declarations */
		CirExecution execution = attribute.get_execution();
		SymbolExpression expression = attribute.get_condition();
		Set<SymbolExpression> expressions = new HashSet<SymbolExpression>();
		this.get_symbol_conditions_in(expression, expressions);
		
		/* 2. generate symbolic constraints on right checkpoint */
		for(SymbolExpression condition : expressions) {
			CirExecution checkpoint = this.find_previous_point(execution, expression);
			annotations.add(CirAnnotation.eva_expr(checkpoint, condition));
		}
		
		/* 3. coverage annotation created here */
		if(annotations.isEmpty()) {
			CirExecution checkpoint = this.find_previous_point(
					execution, SymbolFactory.sym_constant(Boolean.TRUE));
			annotations.add(CirAnnotation.cov_stmt(checkpoint, 1));
		}
	}
	/**
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_cover_count(CirCoverCount attribute,
			Collection<CirAnnotation> annotations) throws Exception {
		/* 1. declarations */
		CirExecution execution = attribute.get_execution();
		int execution_times = attribute.get_coverage_count();
		Set<Integer> times = new HashSet<Integer>();
		
		/* 2. generate coverage times annotations */
		for(int time = 1; time <= execution_times; time = time * 2) {
			times.add(Integer.valueOf(time));
		}
		times.add(Integer.valueOf(execution_times));
		
		/* 3. generate annotations of coverage counter */
		for(Integer time : times) { 
			annotations.add(CirAnnotation.cov_stmt(execution, time));
		}
	}
	/**
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_value_error(CirValueError attribute,
			Collection<CirAnnotation> annotations) throws Exception {
		/* 1. declarations */
		CirExecution execution = attribute.get_execution();
		CirExpression expression = attribute.get_orig_expression();
		SymbolExpression orig_value = SymbolFactory.sym_expression(expression);
		SymbolExpression muta_value = attribute.get_muta_expression();
		orig_value = SymbolEvaluator.evaluate_on(orig_value);
		try {
			muta_value = SymbolEvaluator.evaluate_on(muta_value);
		}
		catch(ArithmeticException ex) {
			annotations.add(CirAnnotation.trp_stmt(execution));
			return;
		}
		
		/* 2. compared with original value and filter non-value error */
		if(orig_value.equals(muta_value)) { return;	/* no error occurs */ }
		
		/* 3. general error annotation */
		annotations.add(CirAnnotation.mut_expr(expression, muta_value));
		
		/* 4. constant based generation */
		if(muta_value instanceof SymbolConstant) {
			SymbolConstant constant = (SymbolConstant) muta_value;
			if(CirMutation.is_boolean(expression)) {
				annotations.add(CirAnnotation.set_bool(expression, constant.get_bool()));
				annotations.add(CirAnnotation.chg_bool(expression));
			}
			else if(CirMutation.is_integer(expression)) {
				annotations.add(CirAnnotation.set_numb(expression, constant.get_long()));
				annotations.add(CirAnnotation.chg_numb(expression));
				if(orig_value instanceof SymbolConstant) {
					long original_value = ((SymbolConstant) orig_value).get_long().longValue();
					long mutation_value = ((SymbolConstant) muta_value).get_long().longValue();
					if(mutation_value > original_value) {
						annotations.add(CirAnnotation.inc_scop(expression));
					}
					else if(mutation_value < original_value) {
						annotations.add(CirAnnotation.dec_scop(expression));
					}
					if(Math.abs(mutation_value) > Math.abs(original_value)) {
						annotations.add(CirAnnotation.ext_scop(expression));
					}
					else if(Math.abs(mutation_value) < Math.abs(original_value)) {
						annotations.add(CirAnnotation.dec_scop(expression));
					}
				}
			}
			else if(CirMutation.is_numeric(expression)) {
				annotations.add(CirAnnotation.set_numb(expression, constant.get_double()));
				annotations.add(CirAnnotation.chg_numb(expression));
				if(orig_value instanceof SymbolConstant) {
					double original_value = ((SymbolConstant) orig_value).get_double().doubleValue();
					double mutation_value = ((SymbolConstant) muta_value).get_double().doubleValue();
					if(mutation_value > original_value) {
						annotations.add(CirAnnotation.inc_scop(expression));
					}
					else if(mutation_value < original_value) {
						annotations.add(CirAnnotation.dec_scop(expression));
					}
					if(Math.abs(mutation_value) > Math.abs(original_value)) {
						annotations.add(CirAnnotation.ext_scop(expression));
					}
					else if(Math.abs(mutation_value) < Math.abs(original_value)) {
						annotations.add(CirAnnotation.dec_scop(expression));
					}
				}
			}
			else if(CirMutation.is_pointer(expression)) {
				annotations.add(CirAnnotation.set_addr(expression, constant.get_long()));
				annotations.add(CirAnnotation.chg_addr(expression));
				if(orig_value instanceof SymbolConstant) {
					long original_value = ((SymbolConstant) orig_value).get_long().longValue();
					long mutation_value = ((SymbolConstant) muta_value).get_long().longValue();
					if(mutation_value > original_value) {
						annotations.add(CirAnnotation.inc_scop(expression));
					}
					else if(mutation_value < original_value) {
						annotations.add(CirAnnotation.dec_scop(expression));
					}
					if(Math.abs(mutation_value) > Math.abs(original_value)) {
						annotations.add(CirAnnotation.ext_scop(expression));
					}
					else if(Math.abs(mutation_value) < Math.abs(original_value)) {
						annotations.add(CirAnnotation.dec_scop(expression));
					}
				}
			}
			else {
				annotations.add(CirAnnotation.chg_auto(expression));
			}
		}
		
		/* differential analysis */
		SymbolExpression difference = SymbolFactory.arith_sub(
				expression.get_data_type(), muta_value, orig_value);
		difference = difference.evaluate(null);
		if(difference instanceof SymbolConstant) {
			if(CirMutation.is_integer(expression)) {
				long value = ((SymbolConstant) difference).get_long().longValue();
				if(value > 0) {
					annotations.add(CirAnnotation.inc_scop(expression));
				}
				else if(value < 0) {
					annotations.add(CirAnnotation.dec_scop(expression));
				}
			}
			else {
				double value = ((SymbolConstant) difference).get_double().doubleValue();
				if(value > 0) {
					annotations.add(CirAnnotation.inc_scop(expression));
				}
				else if(value < 0) {
					annotations.add(CirAnnotation.dec_scop(expression));
				}
			}
		}
	}
	/**
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_refer_error(CirReferError attribute,
			Collection<CirAnnotation> annotations) throws Exception {
		/* 1. declarations */
		CirExecution execution = attribute.get_execution();
		CirExpression expression = attribute.get_orig_expression();
		SymbolExpression orig_value = SymbolFactory.sym_expression(expression);
		SymbolExpression muta_value = attribute.get_muta_expression();
		orig_value = SymbolEvaluator.evaluate_on(orig_value);
		try {
			muta_value = SymbolEvaluator.evaluate_on(muta_value);
		}
		catch(ArithmeticException ex) {
			annotations.add(CirAnnotation.trp_stmt(execution));
			return;
		}
		
		/* 2. compared with original value and filter non-value error */
		if(orig_value.equals(muta_value)) { return;	/* no error occurs */ }
		
		/* 3. general error annotation */
		annotations.add(CirAnnotation.mut_refr(expression, muta_value));
		
		/* 4. constant based generation */
		if(muta_value instanceof SymbolConstant) {
			SymbolConstant constant = (SymbolConstant) muta_value;
			if(CirMutation.is_boolean(expression)) {
				annotations.add(CirAnnotation.set_bool(expression, constant.get_bool()));
				annotations.add(CirAnnotation.chg_bool(expression));
			}
			else if(CirMutation.is_integer(expression)) {
				annotations.add(CirAnnotation.set_numb(expression, constant.get_long()));
				annotations.add(CirAnnotation.chg_numb(expression));
				if(orig_value instanceof SymbolConstant) {
					long original_value = ((SymbolConstant) orig_value).get_long().longValue();
					long mutation_value = ((SymbolConstant) muta_value).get_long().longValue();
					if(mutation_value > original_value) {
						annotations.add(CirAnnotation.inc_scop(expression));
					}
					else if(mutation_value < original_value) {
						annotations.add(CirAnnotation.dec_scop(expression));
					}
					if(Math.abs(mutation_value) > Math.abs(original_value)) {
						annotations.add(CirAnnotation.ext_scop(expression));
					}
					else if(Math.abs(mutation_value) < Math.abs(original_value)) {
						annotations.add(CirAnnotation.dec_scop(expression));
					}
				}
			}
			else if(CirMutation.is_numeric(expression)) {
				annotations.add(CirAnnotation.set_numb(expression, constant.get_double()));
				annotations.add(CirAnnotation.chg_numb(expression));
				if(orig_value instanceof SymbolConstant) {
					double original_value = ((SymbolConstant) orig_value).get_double().doubleValue();
					double mutation_value = ((SymbolConstant) muta_value).get_double().doubleValue();
					if(mutation_value > original_value) {
						annotations.add(CirAnnotation.inc_scop(expression));
					}
					else if(mutation_value < original_value) {
						annotations.add(CirAnnotation.dec_scop(expression));
					}
					if(Math.abs(mutation_value) > Math.abs(original_value)) {
						annotations.add(CirAnnotation.ext_scop(expression));
					}
					else if(Math.abs(mutation_value) < Math.abs(original_value)) {
						annotations.add(CirAnnotation.dec_scop(expression));
					}
				}
			}
			else if(CirMutation.is_pointer(expression)) {
				annotations.add(CirAnnotation.set_addr(expression, constant.get_long()));
				annotations.add(CirAnnotation.chg_addr(expression));
				if(orig_value instanceof SymbolConstant) {
					long original_value = ((SymbolConstant) orig_value).get_long().longValue();
					long mutation_value = ((SymbolConstant) muta_value).get_long().longValue();
					if(mutation_value > original_value) {
						annotations.add(CirAnnotation.inc_scop(expression));
					}
					else if(mutation_value < original_value) {
						annotations.add(CirAnnotation.dec_scop(expression));
					}
					if(Math.abs(mutation_value) > Math.abs(original_value)) {
						annotations.add(CirAnnotation.ext_scop(expression));
					}
					else if(Math.abs(mutation_value) < Math.abs(original_value)) {
						annotations.add(CirAnnotation.dec_scop(expression));
					}
				}
			}
			else {
				annotations.add(CirAnnotation.chg_auto(expression));
			}
		}
		
		/* differential analysis */
		SymbolExpression difference = SymbolFactory.arith_sub(
				expression.get_data_type(), muta_value, orig_value);
		difference = difference.evaluate(null);
		if(difference instanceof SymbolConstant) {
			if(CirMutation.is_integer(expression)) {
				long value = ((SymbolConstant) difference).get_long().longValue();
				if(value > 0) {
					annotations.add(CirAnnotation.inc_scop(expression));
				}
				else if(value < 0) {
					annotations.add(CirAnnotation.dec_scop(expression));
				}
			}
			else {
				double value = ((SymbolConstant) difference).get_double().doubleValue();
				if(value > 0) {
					annotations.add(CirAnnotation.inc_scop(expression));
				}
				else if(value < 0) {
					annotations.add(CirAnnotation.dec_scop(expression));
				}
			}
		}
	}
	/**
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	private void generate_annotations_in_state_error(CirStateError attribute,
			Collection<CirAnnotation> annotations) throws Exception {
		/* 1. declarations */
		CirExecution execution = attribute.get_execution();
		CirExpression expression = attribute.get_orig_expression();
		SymbolExpression orig_value = SymbolFactory.sym_expression(expression);
		SymbolExpression muta_value = attribute.get_muta_expression();
		orig_value = SymbolEvaluator.evaluate_on(orig_value);
		try {
			muta_value = SymbolEvaluator.evaluate_on(muta_value);
		}
		catch(ArithmeticException ex) {
			annotations.add(CirAnnotation.trp_stmt(execution));
			return;
		}
		
		/* 2. general error annotation */
		annotations.add(CirAnnotation.mut_stat(expression, muta_value));
		
		/* 3. constant based generation */
		if(muta_value instanceof SymbolConstant) {
			SymbolConstant constant = (SymbolConstant) muta_value;
			if(CirMutation.is_boolean(expression)) {
				annotations.add(CirAnnotation.set_bool(expression, constant.get_bool()));
				annotations.add(CirAnnotation.chg_bool(expression));
			}
			else if(CirMutation.is_integer(expression)) {
				annotations.add(CirAnnotation.set_numb(expression, constant.get_long()));
				annotations.add(CirAnnotation.chg_numb(expression));
			}
			else if(CirMutation.is_numeric(expression)) {
				annotations.add(CirAnnotation.set_numb(expression, constant.get_double()));
				annotations.add(CirAnnotation.chg_numb(expression));
			}
			else if(CirMutation.is_pointer(expression)) {
				annotations.add(CirAnnotation.set_addr(expression, constant.get_long()));
				annotations.add(CirAnnotation.chg_addr(expression));
			}
			else {
				annotations.add(CirAnnotation.chg_auto(expression));
			}
		}
	}
	/**
	 * @param attribute
	 * @param annotations
	 * @throws Exception
	 */
	protected void generate_concrete_annotations(CirAttribute attribute, Collection<CirAnnotation> annotations) throws Exception {
		if(attribute == null) {
			throw new IllegalArgumentException("Invalid attribute: null");
		}
		else if(annotations == null) {
			throw new IllegalArgumentException("Invalid annotations: null");
		}
		else {
			this.generate_annotations_in(attribute, annotations);
		}
	}
	
	/* annotation summarization from concrete evidence */
	/**
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in(
			Collection<CirAnnotation> concrete_annotations, 
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		Map<CirAnnotationType, Collection<CirAnnotation>> maps = new 
				HashMap<CirAnnotationType, Collection<CirAnnotation>>();
		for(CirAnnotation concrete_annotation : concrete_annotations) {
			CirAnnotationType operator;
			switch(concrete_annotation.get_operator()) {
			case eva_expr:
			case cov_stmt:
			case mut_stmt:
			case mut_flow:
			case trp_stmt:	operator = concrete_annotation.get_operator();	break;
			case mut_expr:
			case mut_refr:
			case mut_stat:	operator = CirAnnotationType.mut_expr;			break;
			case chg_bool:
			case chg_numb:
			case chg_addr:
			case chg_auto:	operator = concrete_annotation.get_operator();	break;
			case set_bool:	
			case set_numb:	
			case set_addr:	operator = concrete_annotation.get_operator();	break;
			case inc_scop:
			case dec_scop:	operator = CirAnnotationType.inc_scop;			break;
			case ext_scop:	
			case shk_scop:	operator = CirAnnotationType.ext_scop;			break;
			default:		throw new IllegalArgumentException("Invalid: " + concrete_annotation);
			} 
			if(!maps.containsKey(operator)) maps.put(operator, new HashSet<CirAnnotation>());
			maps.get(operator).add(concrete_annotation);
		}
		
		for(CirAnnotationType operator : maps.keySet()) {
			Collection<CirAnnotation> annotations = maps.get(operator);
			switch(operator) {
			case cov_stmt:	this.summarize_annotations_in_cov_stmt(annotations, abstract_annotations);	break;
			case eva_expr:	this.summarize_annotations_in_eva_expr(annotations, abstract_annotations);	break;
			case mut_stmt:	this.summarize_annotations_in_mut_stmt(annotations, abstract_annotations); 	break;
			case mut_flow:	this.summarize_annotations_in_mut_flow(annotations, abstract_annotations); 	break;
			case trp_stmt:	this.summarize_annotations_in_trp_stmt(annotations, abstract_annotations); 	break;
			case mut_expr:	this.summarize_annotations_in_mut_expr(annotations, abstract_annotations); 	break;
			case chg_bool:	this.summarize_annotations_in_chg_xxxx(annotations, abstract_annotations); 	break;
			case chg_numb:	this.summarize_annotations_in_chg_xxxx(annotations, abstract_annotations); 	break;
			case chg_addr:	this.summarize_annotations_in_chg_xxxx(annotations, abstract_annotations); 	break;
			case chg_auto:	this.summarize_annotations_in_chg_xxxx(annotations, abstract_annotations); 	break;
			case set_bool:	this.summarize_annotations_in_set_bool(annotations, abstract_annotations);	break;
			case set_numb:	this.summarize_annotations_in_set_numb(annotations, abstract_annotations);	break;
			case set_addr:	this.summarize_annotations_in_set_addr(annotations, abstract_annotations); 	break;
			case inc_scop:	this.summarize_annotations_in_inc_scop(annotations, abstract_annotations);	break;
			case ext_scop:	this.summarize_annotations_in_ext_scop(annotations, abstract_annotations); 	break;
			default:		break;
			}
		}
	}
	/**
	 * pass through into summarization
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_mut_stmt(
			Collection<CirAnnotation> concrete_annotations, 
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		abstract_annotations.addAll(concrete_annotations);
	}
	/**
	 * transform into exit point trapping
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_trp_stmt(
			Collection<CirAnnotation> concrete_annotations, 
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		for(CirAnnotation concrete_annotation : concrete_annotations) {
			CirExecution execution = concrete_annotation.get_execution();
			execution = execution.get_graph().get_exit();
			abstract_annotations.add(CirAnnotation.trp_stmt(execution));
		}
	}
	/**
	 * generate add_stmt | del_stmt annotations here
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_mut_flow(
			Collection<CirAnnotation> concrete_annotations, 
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		if(concrete_annotations.size() == 1) {
			for(CirAnnotation concrete_annotation : concrete_annotations) {
				/* declarations */
				CirExecution orig_target = concrete_annotation.get_location().execution_of();
				CirExecution muta_target = (CirExecution) concrete_annotation.get_parameter().get_source();
				
				if(orig_target != muta_target) {
					abstract_annotations.add(concrete_annotation);
					
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
					
					/* generate the next errors in propagation directly */
					for(CirExecution add_execution : add_executions) {
						if(!(add_execution.get_statement() instanceof CirTagStatement)) {
							abstract_annotations.add(CirAnnotation.add_stmt(add_execution));
						}
					}
					for(CirExecution del_execution : del_executions) {
						if(!(del_execution.get_statement() instanceof CirTagStatement)) {
							abstract_annotations.add(CirAnnotation.del_stmt(del_execution));
						}
					}
				}
				break;
			}
		}
	}
	/**
	 * pass through into summarization
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_eva_expr(
			Collection<CirAnnotation> concrete_annotations, 
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		abstract_annotations.addAll(concrete_annotations);
	}
	/**
	 * pass through into summarization
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_cov_stmt(
			Collection<CirAnnotation> concrete_annotations, 
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		abstract_annotations.addAll(concrete_annotations);
	}
	/**
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_mut_expr(
			Collection<CirAnnotation> concrete_annotations, 
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		for(CirAnnotation concrete_annotation : concrete_annotations) {
			CirExpression orig_expression; SymbolExpression muta_expression;
			orig_expression = (CirExpression) concrete_annotation.get_location();
			muta_expression = concrete_annotation.get_parameter();
			if(!(muta_expression instanceof SymbolConstant)) {
				abstract_annotations.add(concrete_annotation);	/* symbolized */
				if(CirMutation.is_boolean(orig_expression)) {
					abstract_annotations.add(CirAnnotation.chg_bool(orig_expression));
				}
				else if(CirMutation.is_numeric(orig_expression)) {
					abstract_annotations.add(CirAnnotation.chg_numb(orig_expression));
				}
				else if(CirMutation.is_pointer(orig_expression)) {
					abstract_annotations.add(CirAnnotation.chg_addr(orig_expression));
				}
				else {
					abstract_annotations.add(CirAnnotation.chg_auto(orig_expression));
				}
			}
		}
	}
	/**
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_chg_xxxx(
			Collection<CirAnnotation> concrete_annotations, 
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		abstract_annotations.addAll(concrete_annotations);
	}
	/**
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_set_bool(
			Collection<CirAnnotation> concrete_annotations, 
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		Set<Boolean> mutation_values = new HashSet<Boolean>();
		CirExpression expression = null; SymbolConstant constant;
		for(CirAnnotation concrete_annotation : concrete_annotations) {
			expression = (CirExpression) concrete_annotation.get_location();
			constant = (SymbolConstant) concrete_annotation.get_parameter();
			mutation_values.add(constant.get_bool().booleanValue());
		}
		
		abstract_annotations.add(CirAnnotation.chg_bool(expression));
		if(mutation_values.size() == 1) {
			boolean mutation_value = mutation_values.iterator().next();
			abstract_annotations.add(CirAnnotation.set_bool(expression, mutation_value));
		}
	}
	/**
	 * @param abstract_annotations
	 * @param domains
	 * @throws Exception
	 */
	private void summarize_based_on_value_domains(
			CirExpression expression,
			Collection<CirAnnotation> abstract_annotations,
			Map<CirAnnotationType, Boolean> domains) throws Exception {
		/* summarizations based on value domain */
		if(domains.get(CirAnnotationType.set_post)) {
			if(domains.get(CirAnnotationType.set_zero)) {
				if(domains.get(CirAnnotationType.set_negt)) {
					abstract_annotations.add(CirAnnotation.chg_numb(expression));
				}
				else {
					abstract_annotations.add(CirAnnotation.set_nneg(expression));
					abstract_annotations.add(CirAnnotation.chg_numb(expression));
				}
			}
			else {
				if(domains.get(CirAnnotationType.set_negt)) {
					abstract_annotations.add(CirAnnotation.set_nzro(expression));
					abstract_annotations.add(CirAnnotation.chg_numb(expression));
				}
				else {
					abstract_annotations.add(CirAnnotation.set_post(expression));
					abstract_annotations.add(CirAnnotation.chg_numb(expression));
				}
			}
		}
		else {
			if(domains.get(CirAnnotationType.set_zero)) {
				if(domains.get(CirAnnotationType.set_negt)) {
					abstract_annotations.add(CirAnnotation.set_npos(expression));
					abstract_annotations.add(CirAnnotation.chg_numb(expression));
				}
				else {
					abstract_annotations.add(CirAnnotation.set_zero(expression));
					abstract_annotations.add(CirAnnotation.chg_numb(expression));
				}
			}
			else {
				if(domains.get(CirAnnotationType.set_negt)) {
					abstract_annotations.add(CirAnnotation.chg_numb(expression));
					abstract_annotations.add(CirAnnotation.set_negt(expression));
				}
				else {
					/* no error occurs in expression */
				}
			}
		}
	}
	/**
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_set_numb(
			Collection<CirAnnotation> concrete_annotations, 
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		/* capture value domain and injected expression in program */
		CirExpression expression = null; 
		Set<SymbolConstant> constants = new HashSet<SymbolConstant>();
		for(CirAnnotation concrete_annotation : concrete_annotations) {
			expression = (CirExpression) concrete_annotation.get_location();
			constants.add((SymbolConstant) concrete_annotation.get_parameter());
		}
		
		/* integer */
		if(CirMutation.is_integer(expression)) {
			/* collect concrete mutation values into set */
			Set<Long> mutation_values = new HashSet<Long>();
			for(SymbolConstant constant : constants) {
				mutation_values.add(constant.get_long());
			}
			
			/* unique constant */
			if(mutation_values.size() == 1) {
				long mutation_value = mutation_values.iterator().next();
				abstract_annotations.add(CirAnnotation.set_numb(expression, mutation_value));
			}
			
			/* domain analysis */
			Map<CirAnnotationType, Boolean> domains = new HashMap<CirAnnotationType, Boolean>();
			domains.put(CirAnnotationType.set_post, Boolean.FALSE);
			domains.put(CirAnnotationType.set_negt, Boolean.FALSE);
			domains.put(CirAnnotationType.set_zero, Boolean.FALSE);
			for(Long mutation_value : mutation_values) {
				if(mutation_value > 0) {
					domains.put(CirAnnotationType.set_post, Boolean.TRUE);
				}
				else if(mutation_value < 0) {
					domains.put(CirAnnotationType.set_negt, Boolean.TRUE);
				}
				else {
					domains.put(CirAnnotationType.set_zero, Boolean.TRUE);
				}
			}
			this.summarize_based_on_value_domains(expression, abstract_annotations, domains);
		}
		/* double */
		else {
			/* collect concrete mutation values into set */
			Set<Double> mutation_values = new HashSet<Double>();
			for(SymbolConstant constant : constants) {
				mutation_values.add(constant.get_double());
			}
			
			/* unique constant */
			if(mutation_values.size() == 1) {
				double mutation_value = mutation_values.iterator().next();
				abstract_annotations.add(CirAnnotation.set_numb(expression, mutation_value));
			}
			
			/* domain analysis */
			Map<CirAnnotationType, Boolean> domains = new HashMap<CirAnnotationType, Boolean>();
			domains.put(CirAnnotationType.set_post, Boolean.FALSE);
			domains.put(CirAnnotationType.set_negt, Boolean.FALSE);
			domains.put(CirAnnotationType.set_zero, Boolean.FALSE);
			for(Double mutation_value : mutation_values) {
				if(mutation_value > 0) {
					domains.put(CirAnnotationType.set_post, Boolean.TRUE);
				}
				else if(mutation_value < 0) {
					domains.put(CirAnnotationType.set_negt, Boolean.TRUE);
				}
				else {
					domains.put(CirAnnotationType.set_zero, Boolean.TRUE);
				}
			}
			this.summarize_based_on_value_domains(expression, abstract_annotations, domains);
		}
	}
	/**
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_set_addr(
			Collection<CirAnnotation> concrete_annotations, 
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		/* capture value domain and injected expression in program */
		CirExpression expression = null; SymbolConstant constant;
		Set<Long> mutation_values = new HashSet<Long>();
		for(CirAnnotation concrete_annotation : concrete_annotations) {
			expression = (CirExpression) concrete_annotation.get_location();
			constant = (SymbolConstant) concrete_annotation.get_parameter();
			mutation_values.add(constant.get_long().longValue());
		}
		
		/* domain analysis */
		Map<CirAnnotationType, Boolean> domains = new HashMap<CirAnnotationType, Boolean>();
		domains.put(CirAnnotationType.set_invp, Boolean.FALSE);
		domains.put(CirAnnotationType.set_null, Boolean.FALSE);
		for(Long mutation_value : mutation_values) {
			if(mutation_value == 0) {
				domains.put(CirAnnotationType.set_null, Boolean.TRUE);
			}
			else {
				domains.put(CirAnnotationType.set_invp, Boolean.TRUE);
			}
		}
		
		if(domains.get(CirAnnotationType.set_invp)) {
			if(domains.get(CirAnnotationType.set_null)) {
				abstract_annotations.add(CirAnnotation.chg_addr(expression));
			}
			else {
				abstract_annotations.add(CirAnnotation.set_invp(expression));
			}
		}
		else {
			if(domains.get(CirAnnotationType.set_null)) {
				abstract_annotations.add(CirAnnotation.set_null(expression));
			}
			else {
				/* no error occurs */
			}
		}
	}
	/**
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_inc_scop(
			Collection<CirAnnotation> concrete_annotations, 
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		Map<CirAnnotationType, Boolean> domains = new HashMap<CirAnnotationType, Boolean>();
		domains.put(CirAnnotationType.inc_scop, Boolean.FALSE);
		domains.put(CirAnnotationType.dec_scop, Boolean.FALSE);
		CirExpression expression = null;
		for(CirAnnotation concrete_annotation : concrete_annotations) {
			expression = (CirExpression) concrete_annotation.get_location();
			switch(concrete_annotation.get_operator()) {
			case inc_scop:	
			case dec_scop:	domains.put(concrete_annotation.get_operator(), Boolean.TRUE);
			default: 		break;
			}
		}
		if(domains.get(CirAnnotationType.inc_scop)) {
			if(domains.get(CirAnnotationType.dec_scop)) {
				if(CirMutation.is_numeric(expression)) {
					abstract_annotations.add(CirAnnotation.chg_numb(expression));
				}
				else {
					abstract_annotations.add(CirAnnotation.chg_addr(expression));
				}
			}
			else {
				abstract_annotations.add(CirAnnotation.inc_scop(expression));
			}
		}
		else {
			if(domains.get(CirAnnotationType.dec_scop)) {
				abstract_annotations.add(CirAnnotation.dec_scop(expression));
			}
			else { /* no error occurs */ }
		}
	}
	/**
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	private void summarize_annotations_in_ext_scop(
			Collection<CirAnnotation> concrete_annotations, 
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		Map<CirAnnotationType, Boolean> domains = new HashMap<CirAnnotationType, Boolean>();
		domains.put(CirAnnotationType.ext_scop, Boolean.FALSE);
		domains.put(CirAnnotationType.shk_scop, Boolean.FALSE);
		CirExpression expression = null;
		for(CirAnnotation concrete_annotation : concrete_annotations) {
			expression = (CirExpression) concrete_annotation.get_location();
			switch(concrete_annotation.get_operator()) {
			case ext_scop:	
			case shk_scop:	domains.put(concrete_annotation.get_operator(), Boolean.TRUE);
			default: 		break;
			}
		}
		if(domains.get(CirAnnotationType.ext_scop)) {
			if(domains.get(CirAnnotationType.shk_scop)) {
				if(CirMutation.is_numeric(expression)) {
					abstract_annotations.add(CirAnnotation.chg_numb(expression));
				}
				else {
					abstract_annotations.add(CirAnnotation.chg_addr(expression));
				}
			}
			else {
				abstract_annotations.add(CirAnnotation.ext_scop(expression));
			}
		}
		else {
			if(domains.get(CirAnnotationType.shk_scop)) {
				abstract_annotations.add(CirAnnotation.shk_scop(expression));
			}
			else { /* no error occurs */ }
		}
	}
	/**
	 * @param concrete_annotations
	 * @param abstract_annotations
	 * @throws Exception
	 */
	protected void summarize_abstract_annotations(
			Collection<CirAnnotation> concrete_annotations, 
			Collection<CirAnnotation> abstract_annotations) throws Exception {
		abstract_annotations.clear();
		this.summarize_annotations_in(concrete_annotations, abstract_annotations);
	}
	
}
