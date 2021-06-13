package com.jcsa.jcmutest.mutant.sym2mutant.cond;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jcsa.jcmutest.mutant.sym2mutant.base.SymConstraint;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymExpressionError;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymFlowError;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymInstance;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymInstances;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymReferenceError;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymStateValueError;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymTrapError;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.parse.symbol.process.SymbolProcess;

/**
 * The symbolic condition hierarchical block
 * 
 * @author yukimula
 *
 */
public class SymConditionTree {
	
	/* definitions */
	/** the instance that the nodes of conditions in the tree represent **/
	private SymInstance instance;
	/** mapping from condition key to the unique node created in this tree **/
	private Map<SymCondition, SymConditionNode> nodes;
	/** the root node that most represents the source instance (most concrete) **/
	private SymConditionNode root;
	/** the sequence of accumulated evaluation results **/
	private List<Boolean> results;
	
	/* constructor */
	/**
	 * create an empty tree with one root node w.r.t. the instance
	 * @param instance
	 * @throws Exception
	 */
	private SymConditionTree(SymInstance instance) throws Exception {
		if(instance == null) {
			throw new IllegalArgumentException("Invalid instance: null");
		}
		else {
			this.instance = SymInstances.optimize(instance);
			this.nodes = new HashMap<SymCondition, SymConditionNode>();
			this.root = this.get_node(this.get_root_condition());
			this.results = new ArrayList<Boolean>();
		}
	}
	/**
	 * @return the most representative condition to the instance
	 * @throws Exception
	 */
	private SymCondition get_root_condition() throws Exception {
		if(this.instance instanceof SymConstraint) {
			Object[] execute_operator_times = SymInstances.divide_stmt_constraint((SymConstraint) this.instance);
			if(execute_operator_times == null) {
				return SymCondition.eva_expr(this.instance.get_execution(), ((SymConstraint) this.instance).get_condition());
			}
			else {
				CirExecution execution = (CirExecution) execute_operator_times[0];
				int times = ((Integer) execute_operator_times[1]).intValue();
				return SymCondition.cov_stmt(execution, times);
			}
		}
		else if(this.instance instanceof SymFlowError) {
			return SymCondition.mut_flow(((SymFlowError) this.instance).get_original_flow(), 
					((SymFlowError) this.instance).get_mutation_flow());
		}
		else if(this.instance instanceof SymTrapError) {
			return SymCondition.trp_stmt(this.instance.get_execution());
		}
		else if(this.instance instanceof SymExpressionError) {
			return SymCondition.mut_expr(((SymExpressionError) this.instance).get_expression(), 
					((SymExpressionError) this.instance).get_mutation_value());
		}
		else if(this.instance instanceof SymReferenceError) {
			return SymCondition.mut_refr(((SymReferenceError) this.instance).get_expression(), 
					((SymReferenceError) this.instance).get_mutation_value());
		}
		else if(this.instance instanceof SymStateValueError) {
			return SymCondition.mut_stat(
					((SymStateValueError) this.instance).get_expression(), 
					((SymStateValueError) this.instance).get_mutation_value());
		}
		else {
			throw new IllegalArgumentException("Invalid instance: " + this.instance);
		}
	}
	/**
	 * create a tree w.r.t. instance and construct its hierarchy
	 * @param instance
	 * @return
	 * @throws Exception
	 */
	public static SymConditionTree new_tree(SymInstance instance) throws Exception {
		SymConditionTree tree = new SymConditionTree(instance);
		SymConditionUtil.construct_tree(tree); return tree;
	}
	
	/* getters */
	/**
	 * @return the instance that the nodes of conditions in the tree represent
	 */
	public SymInstance get_instance() { return this.instance; }
	/**
	 * @return the execution point where the tree's instance is evaluated on
	 */
	public CirExecution get_execution() { return this.instance.get_execution(); }
	/**
	 * @return the number of nodes created under the tree uniquely
	 */
	public int size() { return this.nodes.size(); }
	/**
	 * @return the set of nodes in the tree, each refers to a unique condition
	 */
	public Iterable<SymConditionNode> get_nodes() { return this.nodes.values(); }
	/**
	 * @return
	 */
	public Iterable<SymCondition> get_conditions() { return this.nodes.keySet(); }
	/**
	 * @return the root node of the tree is most representative to instance
	 */
	public SymConditionNode get_root() { return this.root; }
	/**
	 * @param condition
	 * @return the unique node w.r.t. the condition in the tree
	 * @throws Exception
	 */
	protected SymConditionNode get_node(SymCondition condition) throws Exception {
		if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else {
			if(!this.nodes.containsKey(condition)) {
				this.nodes.put(condition, new SymConditionNode(this, condition));
			}
			return this.nodes.get(condition);
		}
	}
	
	/* evaluation */
	/**
	 * clear the accumulated evaluation results
	 */
	public void clear_results() { this.results.clear(); }
	/**
	 * @param process
	 * @return
	 * @throws Exception
	 */
	public Boolean evaluate(SymbolProcess process) throws Exception {
		Boolean result = this.instance.validate(process);
		this.results.add(result);	return result;
	}
	/**
	 * @return [execute, accept, reject]
	 */
	public int[] count_results() {
		int accepts = 0, rejects = 0;
		for(Boolean result : this.results) {
			if(result != null) {
				if(result.booleanValue()) {
					accepts++;
				}
				else {
					rejects++;
				}
			}
		}
		return new int[] { this.results.size(), accepts, rejects };
	}
	/**
	 * @return how many times the instance was evaluated
	 */
	public int number_of_executions() { return this.results.size(); }
	/**
	 * @return how many times the instance was evaluated as true
	 */
	public int number_of_acceptions() {
		int counter = 0;
		for(Boolean result : this.results) {
			if(result != null && result.booleanValue()) {
				counter++;
			}
		}
		return counter;
	}
	/**
	 * @return how many times the instance was evaluated as false
	 */
	public int number_of_rejections() {
		int counter = 0;
		for(Boolean result : this.results) {
			if(result != null && !result.booleanValue()) {
				counter++;
			}
		}
		return counter;
	}
	/**
	 * @return whether the instance has been evaluated before
	 */
	public boolean is_executed() { return !this.results.isEmpty(); }
	/**
	 * @return whether the instance is evaluated as true at least once
	 */
	public boolean is_accepted() {
		for(Boolean result : this.results) {
			if(result != null && result.booleanValue()) {
				return true;
			}
		}
		return false;
	}
	/**
	 * @return whether the instance is always evaluated false in status
	 */
	public boolean is_rejected() {
		if(this.results.isEmpty()) {
			return false;
		}
		else {
			for(Boolean result : this.results) {
				if(result == null || result.booleanValue()) {
					return false;
				}
			}
			return true;
		}
	}
	/**
	 * @return whether the instance can be evaluated as true in evaluations.
	 */
	public boolean is_acceptable() {
		if(this.results.isEmpty()) {
			return false;
		}
		else {
			for(Boolean result : this.results) {
				if(result == null || result.booleanValue()) {
					return true;
				}
			}
			return false;
		}
	}
	
}
