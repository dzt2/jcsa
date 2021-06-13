package com.jcsa.jcmutest.mutant.sym2mutant.cond;

import java.util.HashMap;
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
	protected static SymConditionTree new_tree(SymInstance instance) throws Exception {
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
	
	
}
