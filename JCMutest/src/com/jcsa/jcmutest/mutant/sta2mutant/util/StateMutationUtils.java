package com.jcsa.jcmutest.mutant.sta2mutant.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import com.jcsa.jcmutest.mutant.sta2mutant.base.CirAbstractState;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionEdge;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlowType;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionPath;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.lang.symbol.SymbolNode;

/**
 * This class implements the supporting utility method for state mutation's
 * normalization, evaluation, subsumption inference and other jobs.
 * 
 * @author yukimula
 *
 */
public final class StateMutationUtils {
	
	/* singleton mode */ /** constructor **/ private StateMutationUtils() {}
	private static final StateMutationUtils utils = new StateMutationUtils();
	
	/* decidable path traversal */
	/**
	 * @param target		the execution point to be reached decidablly using the output path
	 * @param across_branch	True (across the decidable true|false flow) False (for otherwise)
	 * @return				the decidable previous execution path until the input target point
	 * @throws Exception
	 */
	private CirExecutionPath inner_previous_path(CirExecution target, boolean across_branch) throws Exception {
		if(target == null) {
			throw new IllegalArgumentException("Invalid target: null");
		}
		else {
			/* 1. initialization */
			CirExecutionPath path = new CirExecutionPath(target);
			CirExecution execution, source; CirExecutionFlow flow;
			
			/* 2. decidable traversal */
			while(true) {
				execution = path.get_source();
				if(execution.get_in_degree() == 1) {
					flow = execution.get_in_flow(0);
					if(flow.get_type() == CirExecutionFlowType.retr_flow) {
						/* across the function calling body */
						source = execution.get_graph().get_execution(execution.get_id() - 1);
						flow = CirExecutionFlow.virtual_flow(source, execution);
						path.insert(flow);
					}
					else if(flow.get_type() == CirExecutionFlowType.true_flow
							|| flow.get_type() == CirExecutionFlowType.fals_flow) {
						if(across_branch) {
							path.insert(flow);
						}
						else {
							break;			/* not across the branch flows */
						}
					}
					else {
						path.insert(flow);	/* decidable normal flow is in */
					}
				}
				else {					
					/* reach the undecidable conjunction */	break;			
				}
			}
			
			/* 3. return previous decidable path */	return path;
		}
	}
	/**
	 * @param target
	 * @return decidable previous path until the target without across any branch
	 * @throws Exception
	 */
	public static CirExecutionPath inblock_previous_path(CirExecution target) throws Exception {
		return utils.inner_previous_path(target, false);
	}
	/**
	 * @param target
	 * @return decidable previous path until the target that may across branches
	 * @throws Exception
	 */
	public static CirExecutionPath oublock_previous_path(CirExecution target) throws Exception {
		return utils.inner_previous_path(target, true);
	}
	
	/* symbolic condition check */
	/**
	 * @param root
	 * @return	the set of reference expressions specified under the root node
	 * @throws Exception
	 */
	private Collection<SymbolExpression> get_references(SymbolNode root) throws Exception {
		if(root == null) {
			throw new IllegalArgumentException("Invalid root: null");
		}
		else {
			Set<SymbolExpression> references = new HashSet<SymbolExpression>();
			Queue<SymbolNode> queue = new LinkedList<SymbolNode>();
			queue.add(root);
			while(!queue.isEmpty()) {
				SymbolNode parent = queue.poll();
				for(SymbolNode child : parent.get_children()) {
					queue.add(child);
				}
				if(parent.is_reference()) {
					references.add((SymbolExpression) parent);
				}
			}
			return references;
		}
	}
	/**
	 * @param root
	 * @param references
	 * @return	whether any reference is used in the root tree
	 * @throws Exception
	 */
	private boolean use_references(SymbolNode root, Collection<SymbolExpression> references) throws Exception {
		if(root == null) {
			return false;
		}
		else if(references == null || references.isEmpty()) {
			return false;
		}
		else {
			Queue<SymbolNode> queue = new LinkedList<SymbolNode>();
			queue.add(root);
			while(!queue.isEmpty()) {
				SymbolNode parent = queue.poll();
				for(SymbolNode child : parent.get_children()) {
					queue.add(child);
				}
				if(references.contains(parent)) {
					return true;
				}
			}
			return false;
		}
	}
	/**
	 * @param execution
	 * @param references
	 * @return whether any reference in collection is re-defined in the given point of execution node
	 * @throws Exception
	 */
	private boolean def_references(CirExecution execution, Collection<SymbolExpression> references) throws Exception {
		if(execution == null) {
			return false;
		}
		else if(references == null || references.isEmpty()) {
			return false;
		}
		else {
			CirStatement statement = execution.get_statement();
			if(statement instanceof CirIfStatement) {
				CirExpression condition = ((CirIfStatement) statement).get_condition();
				return this.use_references(SymbolFactory.sym_expression(condition), references);
			}
			else if(statement instanceof CirCaseStatement) {
				CirExpression condition = ((CirCaseStatement) statement).get_condition();
				return this.use_references(SymbolFactory.sym_expression(condition), references);
			}
			else if(statement instanceof CirAssignStatement) {
				return references.contains(SymbolFactory.sym_expression(((CirAssignStatement) statement).get_lvalue()));
			}
			else {
				return false;
			}
		}
	}
	/**
	 * @param prev_path
	 * @param condition
	 * @return the best previous point to check the satisfaction of the condition using internal state
	 * @throws Exception
	 */
	private CirExecution find_prior_checkpoint(CirExecutionPath prev_path, SymbolExpression condition) throws Exception {
		if(prev_path == null) {
			throw new IllegalArgumentException("Invalid prev_path: null");
		}
		else if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else {
			Collection<SymbolExpression> references = this.get_references(condition);
			Iterator<CirExecutionEdge> iterator = prev_path.get_iterator(true);
			while(iterator.hasNext()) {
				CirExecutionEdge edge = iterator.next();
				if(this.def_references(edge.get_source(), references)) {
					return edge.get_target();
				}
			}
			return prev_path.get_source();
		}
	}
	/**
	 * @param prev_path
	 * @param condition
	 * @return the best previous point to check the satisfaction of the condition using internal state
	 * @throws Exception
	 */
	public static CirExecution find_checkpoint(CirExecutionPath prev_path, SymbolExpression condition) throws Exception {
		return utils.find_prior_checkpoint(prev_path, condition);
	}
	
	/* subsumption-based inference */
	/**
	 * 
	 * @param state
	 * @param outputs
	 * @param context
	 * @throws Exception
	 */
	public static void subsume(CirAbstractState state, Collection<CirAbstractState> outputs, Object context) throws Exception {
		if(state == null) {
			throw new IllegalArgumentException("Invalid state: null");
		}
		else if(outputs == null) {
			throw new IllegalArgumentException("Invalid outputs: null");
		}
		else {
			CirLocalStateInference.local_subsume(state, outputs);
			CirCrossStateInference.cross_infer(state, outputs, context);
			return;
		}
	}
	/**
	 * It computes the set of states directly subsumed by the input in the given context
	 * @param state
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public static Collection<CirAbstractState> subsume(CirAbstractState state, Object context) throws Exception {
		if(state == null) {
			throw new IllegalArgumentException("Invalid state: null");
		}
		else {
			Set<CirAbstractState> outputs = new HashSet<CirAbstractState>();
			subsume(state, outputs, context); return outputs;
		}
	}
	
}
