package com.jcsa.jcmutest.mutant.cir2mutant.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.jcsa.jcmutest.mutant.cir2mutant.CirMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAttribute;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirBlockError;
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
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.test.state.CStateNode;
import com.jcsa.jcparse.test.state.CStatePath;

/**
 * It provides a set of interfaces for constructing the mutation tree and the 
 * generation of the annotations from CirAttribute statically.
 * 
 * @author yukimula
 *
 */
public class CirMutationUtil {
	
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
	
	/* annotation generation based on feature analysis */
	
	
}
