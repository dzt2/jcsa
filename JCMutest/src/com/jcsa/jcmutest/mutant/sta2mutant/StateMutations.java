package com.jcsa.jcmutest.mutant.sta2mutant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.cir2mutant.CirMutations;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirAbstErrorState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirAbstractState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirConditionState;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.StateMutationParsers;
import com.jcsa.jcmutest.mutant.sta2mutant.tree.StateCrossInference;
import com.jcsa.jcmutest.mutant.sta2mutant.tree.StateLocalInference;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
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
import com.jcsa.jcparse.lang.symbol.SymbolIdentifier;
import com.jcsa.jcparse.lang.symbol.SymbolNode;
import com.jcsa.jcparse.parse.symbol.process.SymbolProcess;

/**
 * This class defines the constant values used to describe execution states
 * (abstract) in mutation analysis.
 * 
 * @author yukimula
 *
 */
public class StateMutations {
	
	/* definitions */
	/** {true, false} **/
	public static final SymbolExpression bool_value = SymbolFactory.variable(CBasicTypeImpl.bool_type, "@BoolValue");
	/** true **/
	public static final SymbolExpression true_value = SymbolFactory.variable(CBasicTypeImpl.bool_type, "@TrueValue");
	/** false **/
	public static final SymbolExpression fals_value = SymbolFactory.variable(CBasicTypeImpl.bool_type, "@FalsValue");
	/** integer or double **/
	public static final SymbolExpression numb_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@NumbValue");
	/** { x | x > 0 } **/
	public static final SymbolExpression post_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@PostValue");
	/** { x | x < 0 } **/
	public static final SymbolExpression negt_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@NegtValue");
	/** 0 **/
	public static final SymbolExpression zero_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@ZeroValue");
	/** { x | x <= 0 } **/
	public static final SymbolExpression npos_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@NposValue");
	/** { x | x >= 0 } **/
	public static final SymbolExpression nneg_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@NnegValue");
	/** { x | x != 0 } **/
	public static final SymbolExpression nzro_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@NzroValue");
	/** address value in pointer **/
	public static final SymbolExpression addr_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@AddrValue");
	/** null **/
	public static final SymbolExpression null_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@NullValue");
	/** {p | p != null} **/
	public static final SymbolExpression nnul_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@NnulValue");
	/** abstract value of the exception **/
	public static final SymbolExpression trap_value = SymbolFactory.variable(CBasicTypeImpl.bool_type, "@Exception");	
	
	/* exception-included symbolic computation */
	/**
	 * @param root
	 * @return whether the node has trap_value among it.
	 */
	private static boolean has_trap_value(SymbolNode root) {
		Queue<SymbolNode> queue = new LinkedList<SymbolNode>();
		queue.add(root); SymbolNode parent;
		while(!queue.isEmpty()) {
			parent = queue.poll();
			if(parent.is_leaf()) {
				if(parent instanceof SymbolIdentifier
					&& parent.equals(trap_value))
				return true;
			}
			else {
				for(SymbolNode child : parent.get_children()) {
					queue.add(child);
				}
			}
		}
		return false;
	}
	/**
	 * @param expression
	 * @param context
	 * @return trap_value iff. arithmetic operation occurs
	 * @throws Exception
	 */
	private static SymbolExpression compute(SymbolExpression expression, SymbolProcess context) throws Exception {
		if(expression == null) {												/* input-validate */
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(has_trap_value(expression)) {									/* trap at this point */
			return trap_value;
		}
		else {																	/* otherwise, compute */
			try {
				expression = expression.evaluate(context);
			}
			catch(ArithmeticException ex) {
				expression = trap_value;
			}
			return expression;
		}
	}
	/**
	 * @param expression
	 * @param context
	 * @return optimized expression from the context or trap_value
	 * @throws Exception
	 */
	public static SymbolExpression evaluate(SymbolExpression expression, SymbolProcess context) throws Exception {
		return compute(expression, context);
	}
	/**
	 * @param expression
	 * @return optimized expression from the context or trap_value
	 * @throws Exception
	 */
	public static SymbolExpression evaluate(SymbolExpression expression) throws Exception {
		return compute(expression, null);
	}
	/**
	 * @param expression
	 * @return whether the symbolic expression contains trapping exception results
	 * @throws Exception
	 */
	public static boolean is_trap_value(SymbolExpression expression) throws Exception { return has_trap_value(expression); }
	/**
	 * @param expression
	 * @return whether the expression uses any abstract value within
	 * @throws Exception
	 */
	private static boolean is_abst_value(SymbolExpression expression) throws Exception {
		if(expression == null) {
			return false;
		}
		else {
			return expression.equals(bool_value) || expression.equals(true_value) || expression.equals(fals_value)
					|| expression.equals(post_value) || expression.equals(negt_value) || expression.equals(zero_value)
					|| expression.equals(npos_value) || expression.equals(nneg_value) || expression.equals(nzro_value)
					|| expression.equals(numb_value) || expression.equals(null_value) || expression.equals(nnul_value)
					|| expression.equals(addr_value);
		}
	}
	/**
	 * @param root
	 * @return whether it contains the abstract values defined
	 * @throws Exception
	 */
	public static boolean has_abst_value(SymbolNode root) throws Exception {
		if(root == null) {
			return false;
		}
		else {
			Queue<SymbolNode> queue = new LinkedList<SymbolNode>();
			queue.add(root); SymbolNode parent;
			while(!queue.isEmpty()) {
				parent = queue.poll();
				if(parent.is_leaf()) {
					if(parent instanceof SymbolIdentifier
						&& is_abst_value((SymbolExpression) parent))
					return true;
				}
				else {
					for(SymbolNode child : parent.get_children()) {
						queue.add(child);
					}
				}
			}
			return false;
		}
	}
	
	/* type classifier */
	/**
	 * @param data_type
	 * @return
	 */
	public static boolean is_void(CType data_type) {
		try {
			return CTypeAnalyzer.is_void(data_type);
		}
		catch(Exception ex) {
			return false;
		}
	}
	/**
	 * @param data_type
	 * @return whether the data type is a boolean
	 */
	public static boolean is_boolean(CType data_type) {
		try {
			return CTypeAnalyzer.is_boolean(data_type);
		}
		catch(Exception ex) {
			return false;
		}
	}
	/**
	 * @param data_type
	 * @return whether the data type is a unsigned
	 */
	public static boolean is_usigned(CType data_type) {
		try {
			if(CTypeAnalyzer.is_integer(data_type)) {
				return CTypeAnalyzer.is_unsigned(data_type);
			}
			else {
				return false;
			}
		}
		catch(Exception ex) {
			return false;
		}
	}
	/**
	 * @param data_type
	 * @return whether the data type is a integer
	 */
	public static boolean is_integer(CType data_type) {
		try {
			return CTypeAnalyzer.is_integer(data_type);
		}
		catch(Exception ex) {
			return false;
		}
	}
	/**
	 * @param data_type
	 * @return whether the data type is real
	 */
	public static boolean is_doubles(CType data_type) {
		try {
			return CTypeAnalyzer.is_real(data_type);
		}
		catch(Exception ex) {
			return false;
		}
	}
	/**
	 * @param data_type
	 * @return whether the data type is integer or real
	 */
	public static boolean is_numeric(CType data_type) {
		try {
			return CTypeAnalyzer.is_integer(data_type) || CTypeAnalyzer.is_real(data_type);
		}
		catch(Exception ex) {
			return false;
		}
	}
	/**
	 * @param data_type
	 * @return whether the data type is a address pointer
	 */
	public static boolean is_address(CType data_type) {
		try {
			return CTypeAnalyzer.is_pointer(data_type);
		}
		catch(Exception ex) {
			return false;
		}
	}
	
	/* location classifier */
	/**
	 * @param expression
	 * @return
	 */
	public static boolean is_void(CirExpression expression) {
		return CirMutations.is_void(expression.get_data_type());
	}
	/**
	 * @param location
	 * @return whether the expression is a boolean
	 */
	public static boolean is_boolean(CirExpression expression) {
		if(expression == null) {
			return false;
		}
		else if(is_boolean(expression.get_data_type())) {
			return true;
		}
		else if(expression.get_parent() instanceof CirStatement) {
			CirStatement statement = expression.statement_of();
			return statement instanceof CirIfStatement || statement instanceof CirCaseStatement;
		}
		else {
			return false;
		}
	}
	/**
	 * @param expression
	 * @return whether the expression is a unsigned integer
	 */
	public static boolean is_usigned(CirExpression expression) {
		if(expression == null) {
			return false;
		}
		else {
			return is_usigned(expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @return whether the expression is an integer
	 */
	public static boolean is_integer(CirExpression expression) {
		if(expression == null) {
			return false;
		}
		else {
			return is_integer(expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @return whether the expression is a real
	 */
	public static boolean is_doubles(CirExpression expression) {
		if(expression == null) {
			return false;
		}
		else {
			return is_doubles(expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @return whether the expression is a real or integer
	 */
	public static boolean is_numeric(CirExpression expression) {
		if(expression == null) {
			return false;
		}
		else {
			return is_integer(expression) || is_doubles(expression);
		}
	}
	/**
	 * @param expression
	 * @return whether the expression is a pointer
	 */
	public static boolean is_address(CirExpression expression) {
		if(expression == null) {
			return false;
		}
		else {
			return is_address(expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @return whether the expression is a reference defined in left-side of assignment
	 */
	public static boolean is_assigned(CirExpression expression) {
		if(expression == null) {
			return false;
		}
		else if(expression.get_parent() instanceof CirAssignStatement) {
			CirAssignStatement statement = (CirAssignStatement) expression.get_parent();
			return statement.get_lvalue() == expression;
		}
		else {
			return false;
		}
	}
	
	/* mutation create */
	/**
	 * It creates a RIP-model of state mutation into some given point.
	 * @param point
	 * @param istate
	 * @param pstate
	 * @throws Exception
	 */
	public static StateMutation new_mutation(CirExecution point, 
			CirConditionState istate, CirAbstErrorState pstate) throws Exception {
		return new StateMutation(point, istate, pstate);
	}
	/**
	 * It parses the syntactic mutation to a set of state mutations in terms of
	 * C-intermediate representative of the program under analysis and testing.
	 * 
	 * @param mutant		syntactic mutation
	 * @return				the set of state mutations or empty for failure
	 * @throws Exception
	 */
	public static Collection<StateMutation> parse(Mutant mutant) throws Exception {
		if(mutant == null) {
			throw new IllegalArgumentException("Invalid mutant: null");
		}
		else {
			try {
				return StateMutationParsers.parse(mutant.
						get_space().get_cir_tree(), mutant.get_mutation());
			}
			catch(Exception ex) {
				// ex.printStackTrace();
				return new ArrayList<StateMutation>();
			}
		}
	}
	
	/* utility methods */
	/**
	 * @param target		the final execution point that the path reaches to
	 * @param across_branch	True if the decidable path should get across
	 * @return				the decidable previous path to the target within the
	 * 						internal procedure part.
	 * @throws Exception
	 */
	private static CirExecutionPath inner_previous_path(CirExecution 
					target, boolean across_branch) throws Exception {
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
						source = execution.get_graph().get_execution(execution.get_id() - 1);
						flow = CirExecutionFlow.virtual_flow(source, execution);
						path.insert(flow);	/* across the function calling */
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
		return inner_previous_path(target, false);
	}
	/**
	 * @param target
	 * @return decidable previous path until the target that may across branches
	 * @throws Exception
	 */
	public static CirExecutionPath oublock_previous_path(CirExecution target) throws Exception {
		return inner_previous_path(target, true);
	}
	
	/* symbolic condition check */
	/**
	 * @param root
	 * @return	the set of reference expressions specified under the root node
	 * @throws Exception
	 */
	private static Collection<SymbolExpression> get_references(SymbolNode root) throws Exception {
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
	private static boolean use_references(SymbolNode root, Collection<SymbolExpression> references) throws Exception {
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
	private static boolean def_references(CirExecution execution, Collection<SymbolExpression> references) throws Exception {
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
				return use_references(SymbolFactory.sym_expression(condition), references);
			}
			else if(statement instanceof CirCaseStatement) {
				CirExpression condition = ((CirCaseStatement) statement).get_condition();
				return use_references(SymbolFactory.sym_expression(condition), references);
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
	private static CirExecution find_prior_checkpoint(CirExecutionPath prev_path, SymbolExpression condition) throws Exception {
		if(prev_path == null) {
			throw new IllegalArgumentException("Invalid prev_path: null");
		}
		else if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else {
			Collection<SymbolExpression> references = get_references(condition);
			Iterator<CirExecutionEdge> iterator = prev_path.get_iterator(true);
			while(iterator.hasNext()) {
				CirExecutionEdge edge = iterator.next();
				if(def_references(edge.get_source(), references)) {
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
		return find_prior_checkpoint(prev_path, condition);
	}
	
	/* state subsumed inference */
	/**
	 * It generates the set of abstract states directly subsumed by the input state under the given context
	 * @param state		the source state from which the subsumed states are inferred
	 * @param context	CDependGraph | CStatePath | CirExecutionPath | null
	 * @return			the set of abstract states being subsumed by the state in the given context
	 * @throws Exception
	 */
	public static Collection<CirAbstractState> subsume(CirAbstractState state, Object context) throws Exception {
		if(state == null) {
			throw new IllegalArgumentException("Invalid state: null");
		}
		else {
			Set<CirAbstractState> outputs = new HashSet<CirAbstractState>();
			outputs.addAll(StateLocalInference.local_subsume(state));
			outputs.addAll(StateCrossInference.cross_subsume(state, context));
			return outputs;
		}
	}
	
	// TODO implement more methods...
	
}
