package com.jcsa.jcparse.test.state;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jcsa.jcparse.lang.CRunTemplate;
import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlowType;
import com.jcsa.jcparse.lang.irlang.impl.CirLocalizer;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCallStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirEndStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirGotoStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.test.inst.InstrumentalLine;
import com.jcsa.jcparse.test.inst.InstrumentalLines;


/**
 * State transition path, each node refers to a statement being executed during
 * testing, of which expressions are evaluated and values are preserved in unit.
 * 
 * @author yukimula
 *
 */
public class CStatePath {
	
	/* definitions */
	/** C-intermediate representation for testing **/
	private CirTree cir_tree;
	/** the sequence of statements being executed **/
	private List<CStateNode> nodes;
	/**
	 * create an empty state path w.r.t. the C code
	 * @param cir_tree
	 * @throws Exception
	 */
	public CStatePath(CirTree cir_tree) throws IllegalArgumentException {
		if(cir_tree == null)
			throw new IllegalArgumentException("Invalid cir_tree: null");
		else {
			this.cir_tree = cir_tree;
			this.nodes = new ArrayList<CStateNode>();
		}
	}
	
	/* getters */
	/**
	 * @return C-intermediate representation for testing
	 */
	public CirTree get_cir_tree() { return this.cir_tree; }
	/**
	 * @return the sequence of statements being executed
	 */
	public Iterable<CStateNode> get_nodes() { return this.nodes; }
	/**
	 * @return the number of the nodes in transition path
	 */
	public int size() { return this.nodes.size(); }
	/**
	 * @param index
	 * @return the node of statement being executed in the path
	 * @throws IndexOutOfBoundsException
	 */
	public CStateNode get_node(int index) throws IndexOutOfBoundsException {
		return this.nodes.get(index);
	}
	/**
	 * @return the last node of statement being executed
	 */
	public CStateNode get_last_node() {
		if(this.nodes.isEmpty())
			return null;
		else
			return this.nodes.get(this.nodes.size() - 1);
	}
	
	/* setters */
	/**
	 * append the node to the tail of the path
	 * @param node
	 * @throws Exception
	 */
	private void add_node(CStateNode node) throws Exception {
		if(node == null || node.path != null)
			throw new IllegalArgumentException("Invalid node: " + node);
		else {
			node.path = this;
			node.index = this.nodes.size();
			this.nodes.add(node);
		}
	}
	/**
	 * append the execution to the tail of the path
	 * @param execution
	 * @throws Exception
	 */
	public void append(CirExecution execution, Map<CirExpression, Object> values) throws Exception {
		if(execution == null)
			throw new IllegalArgumentException("Invalid execution: null");
		else if(values == null)
			throw new IllegalArgumentException("Invalid values as null");
		else {
			/* 1. create the next node w.r.t. the execution in the path */
			CStateNode target_node = new CStateNode(execution);
			CirStatement statement = execution.get_statement();
			
			/* 2. collect the expressions and update the values */
			Set<CirExpression> expressions = CirLocalizer.expressions_in(statement);
			for(CirExpression expression : expressions) {
				if(values.containsKey(expression) && 
					!CirLocalizer.is_left_reference(expression)) {
					Object value = values.get(expression);
					if(value != null) {
						target_node.set_unit(expression, value);
					}
					values.remove(expression);
				}
			}
			
			/* 3. complete the path from last-node to next-node */
			if(!this.nodes.isEmpty()) {
				CStateNode source_node = this.get_last_node();
				List<CirExecution> path = 
						complete_path_between(source_node, target_node);
				for(CirExecution curr_execution : path) {
					this.add_node(new CStateNode(curr_execution));
				}
			}
			
			/* 4. append the node into the path */
			this.add_node(target_node);
		}
	}
	/**
	 * @param source_node
	 * @param target_node
	 * @return generate the execution from the source to the target
	 * @throws Exception
	 */
	private List<CirExecution> complete_path_between(
			CStateNode source_node,
			CStateNode target_node) throws Exception {
		List<CirExecution> path = new ArrayList<CirExecution>();
		CirExecution source_execution = source_node.get_execution();
		CirExecution target_execution = target_node.get_execution();
		CirExecution curr_execution = source_execution;
		CirStatement target_statement = target_execution.get_statement();
		
		while(curr_execution != target_execution) {
			if(curr_execution != source_execution && curr_execution != target_execution)
				path.add(curr_execution);
			
			/* determine the next node being executed from curr_execution */
			CirStatement statement = curr_execution.get_statement();
			if(statement instanceof CirAssignStatement || statement instanceof CirGotoStatement) {
				curr_execution = curr_execution.get_ou_flow(0).get_target();
			}
			else if(statement instanceof CirCallStatement) {
				if(curr_execution.get_ou_flow(0).get_type() == CirExecutionFlowType.call_flow) {
					curr_execution = curr_execution.get_ou_flow(0).get_target();
				}
				else {
					curr_execution = target_execution;	/* annex to the target */
				}
			}
			else if(statement instanceof CirIfStatement) {
				CStateUnit condition = source_node.get_unit(((CirIfStatement) statement).get_condition());
				if(condition != null) {
					if(condition.get_bool()) {
						for(CirExecutionFlow flow : curr_execution.get_ou_flows()) {
							if(flow.get_type() == CirExecutionFlowType.true_flow) {
								curr_execution = flow.get_target();
								break;
							}
						}
					}
					else {
						for(CirExecutionFlow flow : curr_execution.get_ou_flows()) {
							if(flow.get_type() == CirExecutionFlowType.fals_flow) {
								curr_execution = flow.get_target();
								break;
							}
						}
					}
				}
				else {
					curr_execution = target_execution;	/* annex to the target */
				}
			}
			else if(statement instanceof CirCaseStatement) {
				if(statement == target_statement) {
					curr_execution = target_execution;	/* annex to the target */
				}
				else {
					for(CirExecutionFlow flow : curr_execution.get_ou_flows()) {
						if(flow.get_type() == CirExecutionFlowType.fals_flow) {
							curr_execution = flow.get_target();
							break;
						}
					}
				}
			}
			else if(statement instanceof CirEndStatement) {
				curr_execution = target_execution;	/* annex to the target */
			}
			else {
				curr_execution = curr_execution.get_ou_flow(0).get_target();
			}
		}
		
		return path;
	}
	
	/* parsing method */
	/**
	 * @param template
	 * @param ast_tree
	 * @param cir_tree
	 * @param instrumental_file
	 * @return the executional path generated from the instrumental file
	 * @throws Exception
	 */
	public static CStatePath read_path(CRunTemplate template, AstTree ast_tree, 
			CirTree cir_tree, File instrumental_file) throws Exception {
		if(template == null)
			throw new IllegalArgumentException("Invalid template: null");
		else if(ast_tree == null)
			throw new IllegalArgumentException("Invalid ast_tree: null");
		else if(cir_tree == null)
			throw new IllegalArgumentException("Invalid cir_tree: null");
		else if(instrumental_file == null || !instrumental_file.exists())
			throw new IllegalArgumentException("Invalid instrumental file");
		else {
			List<InstrumentalLine> lines = InstrumentalLines.
					complete_lines(template, ast_tree, instrumental_file);
			return CStatePathBuilder.get_path(lines, cir_tree);
		}
	}
	
}
