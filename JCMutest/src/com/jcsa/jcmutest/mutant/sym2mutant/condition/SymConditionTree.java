package com.jcsa.jcmutest.mutant.sym2mutant.condition;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.jcsa.jcmutest.mutant.sym2mutant.base.SymConstraint;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymExpressionError;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymFlowError;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymInstance;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymInstances;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymReferenceError;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymStateError;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymStateValueError;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymTrapError;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.parse.symbol.process.SymbolProcess;

public class SymConditionTree {
	
	/* definitions */
	/** the instance that the tree is to evaluate **/
	private SymInstance instance;
	/** the mapping from key to the unique node in tree **/
	private Map<String, SymConditionNode> nodes;
	/** the root node as the concrete representative of the instance **/
	private SymConditionNode root;
	
	/* constructor */
	/**
	 * create a empty tree with single root w.r.t. instance being presented
	 * @param instance
	 * @throws Exception
	 */
	protected SymConditionTree(SymInstance instance) throws Exception {
		if(instance == null) {
			throw new IllegalArgumentException("Invalid instance as null");
		}
		else {
			this.instance = SymInstances.optimize(instance);
			this.nodes = new HashMap<String, SymConditionNode>();
			this.root = this.get_node(this.create_root_condition());
		}
	}
	/**
	 * @param instance
	 * @return create the representative node to the instance
	 * @throws Exception
	 */
	private SymCondition create_root_condition() throws Exception {
		SymInstance instance = this.instance;
		CirExecution execution = instance.get_execution();
		if(instance instanceof SymConstraint) {
			Object[] execute_operator_times = SymInstances.divide_stmt_constraint((SymConstraint) instance);
			if(execute_operator_times == null) {
				SymbolExpression expression = ((SymConstraint) instance).get_condition();
				return SymCondition.eva_expr(execution, expression);
			}
			else {
				execution = (CirExecution) execute_operator_times[0];
				int times = ((Integer) execute_operator_times[2]).intValue();
				return SymCondition.cov_stmt(execution, times);
			}
		}
		else if(instance instanceof SymFlowError) {
			CirExecutionFlow orig_flow = ((SymFlowError) instance).get_original_flow();
			CirExecutionFlow muta_flow = ((SymFlowError) instance).get_mutation_flow();
			return SymCondition.mut_flow(orig_flow, muta_flow);
		}
		else if(instance instanceof SymTrapError) {
			return SymCondition.trp_stmt(execution);
		}
		else if(instance instanceof SymExpressionError) {
			CirExpression expression = ((SymExpressionError) instance).get_expression();
			SymbolExpression muta_value = ((SymExpressionError) instance).get_mutation_value();
			return SymCondition.mut_expr(expression, muta_value);
		}
		else if(instance instanceof SymReferenceError) {
			CirExpression expression = ((SymReferenceError) instance).get_expression();
			SymbolExpression muta_value = ((SymReferenceError) instance).get_mutation_value();
			return SymCondition.mut_refr(expression, muta_value);
		}
		else if(instance instanceof SymStateValueError) {
			CirExpression expression = ((SymStateValueError) instance).get_expression();
			SymbolExpression muta_value = ((SymStateValueError) instance).get_mutation_value();
			return SymCondition.mut_stat(expression, muta_value);
		}
		else {
			throw new IllegalArgumentException(instance.getClass().getSimpleName());
		}
	}
	
	/* getters */
	/**
	 * @return the instance that the tree is to evaluate
	 */
	public SymInstance get_instance() { return this.instance; }
	/**
	 * @return whether the instance is a constraint
	 */
	public boolean is_constraint() { return this.instance instanceof SymConstraint; }
	/**
	 * @return whether the instance is a state error
	 */
	public boolean is_state_error() { return this.instance instanceof SymStateError; }
	/**
	 * @return the execution where the tree of instance is evaluated
	 */
	public CirExecution get_execution() { return this.instance.get_execution(); }
	/**
	 * @return the set of nodes created in this hierarchy
	 */
	public Iterable<SymConditionNode> get_nodes() { return this.nodes.values(); }
	/**
	 * @return the number of nodes in this tree
	 */
	public int size() { return this.nodes.size(); }
	/**
	 * @return the root node as the concrete representative of the instance
	 */
	public SymConditionNode get_root() { return this.root; }
	/**
	 * @return the set of leafs under the tree
	 */
	public Collection<SymConditionNode> get_leafs() {
		Queue<SymConditionNode> queue = new LinkedList<SymConditionNode>();
		Set<SymConditionNode> records = new HashSet<SymConditionNode>();
		Set<SymConditionNode> leafs = new HashSet<SymConditionNode>();
		queue.add(this.root); records.add(this.root);
		while(!queue.isEmpty()) {
			SymConditionNode node = queue.poll();
			if(node.is_leaf()) {
				leafs.add(node);
			}
			else {
				for(SymConditionNode child : node.get_children()) {
					if(child.get_tree() == this && !records.contains(child)) {
						records.add(child); 
						queue.add(child);
					}
				}
			}
		}
		return leafs;
	}
	
	/* setters */
	/**
	 * @param condition
	 * @return the unique node w.r.t. the condition in the hierarchical space
	 * @throws Exception
	 */
	protected SymConditionNode get_node(SymCondition condition) throws Exception {
		if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else {
			String key = condition.toString();
			if(!this.nodes.containsKey(key)) {
				this.nodes.put(key, new SymConditionNode(this, condition));
			}
			return this.nodes.get(key);
		}
	}
	/**
	 * clear the results accumulated in all the nodes
	 */
	protected void clear_results() {
		for(SymConditionNode node : this.nodes.values()) {
			node.clear_results();
		}
	}
	
	
}
