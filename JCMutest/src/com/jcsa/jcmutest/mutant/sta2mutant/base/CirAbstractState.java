package com.jcsa.jcmutest.mutant.sta2mutant.base;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import com.jcsa.jcmutest.mutant.sta2mutant.StateMutations;
import com.jcsa.jcparse.lang.irlang.CirNode;
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
import com.jcsa.jcparse.lang.irlang.stmt.CirTagStatement;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.lang.symbol.SymbolNode;
import com.jcsa.jcparse.parse.symbol.process.SymbolProcess;


/**
 * It specifies the abstract execution state specified in the mutation testing.
 * <br>
 * <code>
 * 	CirAbstractState			(execution, store, value)						<br>
 * 	<br>
 * 	|--	CirConditionState		(execution, [stmt], (bool, condition))			<br>
 * 	|--	|--	CirLimitTimesState	(execution, [stmt], (true, int_times))			<br>
 * 	|--	|--	CirReachTimesState	(execution, [stmt], (false, int_times))			<br>
 * 	|--	|--	CirNConstrainState	(execution, [stmt], (true, condition))			<br>
 * 	|--	|--	CirMConstrainState	(execution, [stmt], (false, condition))			<br>
 * 	<br>
 * 	|--	CirPathErrorState		(execution, [stmt], (loperand, roperand))		<br>
 * 	|--	|--	CirBlockErrorState	(execution, [stmt], (orig_exec, muta_exec))		<br>
 * 	|--	|--	CirFlowsErrorState	(execution, [stmt], (orig_stmt, muta_stmt))		<br>
 * 	|--	|--	CirTrapsErrorState	(execution, [stmt], (execution, exception))		<br>
 * 	<br>
 * 	|--	CirDataErrorState		(execution, [expr|cond|dvar|vdef], (val1, val2))<br>
 * 	|--	|--	CirDiferErrorState	(execution, [expr|cond|dvar],	   (val1, val2))<br>
 * 	|--	|--	CirValueErrorState	(execution, [expr|cond|dvar|vdef], (val1, val2))<br>
 * 	|--	|--	CirIncreErrorState	(execution, [expr|dvar|vdef],	(base, differ))	<br>
 * 	|--	|--	CirBixorErrorState	(execution, [expr|dvar|vdef],	(base, differ))	<br>
 * 	<br>
 * </code>
 * 
 * @author yukimula
 *
 */
public abstract class CirAbstractState {
	
	/* definitions */
	/**	location	**/	private CirExecution	point;
	/**	store unit	**/	private CirStateStore	store;
	/** state value	**/	private CirStateValue	value;
	protected CirAbstractState(CirExecution point, CirStateStore 
					store, CirStateValue value) throws Exception {
		if(point == null) {
			throw new IllegalArgumentException("Invalid point: null");
		}
		else if(store == null) {
			throw new IllegalArgumentException("Invalid store: null");
		}
		else if(value == null) {
			throw new IllegalArgumentException("Invalid value: null");
		}
		else {
			this.point = point; this.store = store; this.value = value;
		}
	}
	
	/* getters */
	/**
	 * @return the execution point where the state is defined
	 */
	public CirExecution 	get_execution() { return this.point; }
	/**
	 * @return	the statement where this state is specified
	 */
	public CirStatement		get_statement() { return this.point.get_statement(); }
	/**
	 * @return	the type of the store unit defined in state
	 */
	public CirStoreClass	get_store_type() { return this.store.get_type(); }
	/**
	 * @return	the C-intermediate location where the state is annotated on
	 */
	public CirNode			get_clocation() { return this.store.get_unit(); }
	/**
	 * @return	the symbolic identifier to specify the store unit
	 */
	public SymbolExpression get_store_key() { return this.store.get_skey(); }
	/**
	 * @return the operator (class) of the values defined in this state
	 */
	public CirValueClass	get_operator() { return this.value.get_operator(); }
	/**
	 * @return the left operand to define state value
	 */
	public SymbolExpression	get_loperand() { return this.value.get_loperand(); }
	/**
	 * @return the right operand to define state value
	 */
	public SymbolExpression get_roperand() { return this.value.get_roperand(); }
	
	/* general */
	@Override
	public String toString() {
		return this.point + "[" + this.store + "] := " + this.value;
	}
	@Override
	public int hashCode() { return this.toString().hashCode(); }
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof CirAbstractState) {
			return obj.toString().equals(this.toString());
		}
		else {
			return false;
		}
	}
	
	/* conditions */
	/**
	 * @param execution
	 * @param max_times
	 * @return	[stmt:statement] <== cov_times(true, max_times)
	 * @throws Exception
	 */
	public static CirLimitTimesState lim_time(CirExecution execution, int max_times) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else if(max_times < 0) {
			throw new IllegalArgumentException("Invalid max_times: null");
		}
		else {
			return new CirLimitTimesState(execution, max_times);
		}
	}
	/**
	 * @param execution
	 * @param min_times
	 * @return	[stmt:statement] <== cov_times(false, min_times)
	 * @throws Exception
	 */
	public static CirReachTimesState cov_time(CirExecution execution, int min_times) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else if(min_times <= 0) {
			throw new IllegalArgumentException("Invalid min_times: null");
		}
		else {
			return new CirReachTimesState(execution, min_times);
		}
	}
	/**
	 * @param execution		the execution point in which the symbolic condition is evaluated
	 * @param condition		the condition needs be satisfied at least once at the given node
	 * @return				[stmt:statement] <== cov_cond(true, condition)
	 * @throws Exception
	 */
	public static CirNConstrainState eva_cond(CirExecution execution, Object condition, boolean value) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else {
			return new CirNConstrainState(execution, SymbolFactory.sym_condition(condition, value));
		}
	}
	/**
	 * @param execution		the execution point in which the symbolic condition is evaluated
	 * @param condition		the condition should always be satisfied every time at the point
	 * @return				[stmt:statement] <== cov_cond(false, condition)
	 * @throws Exception
	 */
	public static CirMConstrainState mus_cond(CirExecution execution, Object condition, boolean value) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else {
			return new CirMConstrainState(execution, SymbolFactory.sym_condition(condition, value));
		}
	}
	
	/* path-error */
	/**
	 * @param execution
	 * @param muta_execute
	 * @return	[stmt:statement] <== set_stmt(!muta_execute, muta_execute)
	 * @throws Exception
	 */
	public static CirBlockErrorState set_stmt(CirExecution execution, boolean muta_execute) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else if(execution.get_statement() instanceof CirTagStatement) {
			throw new IllegalArgumentException(execution.get_statement().getClass().getSimpleName());
		}
		else {
			return new CirBlockErrorState(execution, muta_execute);
		}
	}
	/**
	 * @param orig_flow
	 * @param muta_flow
	 * @return	[stmt:source] <== set_flow(orig_target, muta_target)
	 * @throws Exception
	 */
	public static CirFlowsErrorState set_flow(CirExecutionFlow orig_flow, CirExecutionFlow muta_flow) throws Exception {
		if(orig_flow == null) {
			throw new IllegalArgumentException("Invalid orig_flow: null");
		}
		else if(muta_flow == null) {
			throw new IllegalArgumentException("Invalid muta_flow: null");
		}
		else {
			return new CirFlowsErrorState(orig_flow, muta_flow);
		}
	}
	/**
	 * @param execution
	 * @return [stmt:statement] <== set_trap(execution, exception)
	 * @throws Exception
	 */
	public static CirTrapsErrorState set_trap(CirExecution execution) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else {
			return new CirTrapsErrorState(execution.get_graph().get_exit());
		}
	}
	
	/* data-error */
	/**
	 * @param expression
	 * @param muta_value
	 * @return [cond|expr|cvar] <== set_expr(orig_value, muta_value)
	 * @throws Exception
	 */
	public static CirValueErrorState set_expr(CirExpression expression, Object muta_value) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(muta_value == null) {
			throw new IllegalArgumentException("Invalid muta_value: null");
		}
		else if(StateMutations.is_boolean(expression)) {	/* cond */
			SymbolExpression ovalue, mvalue;
			ovalue = SymbolFactory.sym_condition(expression, true);
			mvalue = SymbolFactory.sym_condition(muta_value, true);
			return new CirValueErrorState(expression.execution_of(), 
					CirStateStore.new_unit(expression), ovalue, mvalue);
		}
		else if(StateMutations.is_assigned(expression)) {	/* cvar */
			SymbolExpression ovalue, mvalue;
			CirAssignStatement statement = (CirAssignStatement) expression.statement_of();
			ovalue = SymbolFactory.sym_expression(statement.get_rvalue());
			mvalue = SymbolFactory.sym_expression(muta_value);
			return new CirValueErrorState(expression.execution_of(),
					CirStateStore.new_unit(expression), ovalue, mvalue);
		}
		else {												/* expr */
			SymbolExpression ovalue, mvalue;
			ovalue = SymbolFactory.sym_expression(expression);
			mvalue = SymbolFactory.sym_expression(muta_value);
			return new CirValueErrorState(expression.execution_of(),
					CirStateStore.new_unit(expression), ovalue, mvalue);
		}
	}
	/**
	 * @param expression
	 * @param difference
	 * @return [expr|cvar] <== inc_expr(base_value, difference)
	 * @throws Exception
	 */
	public static CirIncreErrorState inc_expr(CirExpression expression, Object difference) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(difference == null) {
			throw new IllegalArgumentException("Invalid difference: null");
		}
		else if(StateMutations.is_numeric(expression) 
				|| StateMutations.is_address(expression)) {
			SymbolExpression ovalue, mvalue;
			if(StateMutations.is_assigned(expression)) {
				CirAssignStatement statement = (CirAssignStatement) expression.statement_of();
				ovalue = SymbolFactory.sym_expression(statement.get_rvalue());
			}
			else {
				ovalue = SymbolFactory.sym_expression(expression);
			}
			mvalue = SymbolFactory.sym_expression(difference);
			return new CirIncreErrorState(expression.execution_of(),
					CirStateStore.new_unit(expression), ovalue, mvalue);
		}
		else {
			throw new IllegalArgumentException(expression.generate_code(true));
		}
	}
	/**
	 * @param expression
	 * @param difference
	 * @return [expr|cvar] <== xor_expr(base_value, difference)
	 * @throws Exception
	 */
	public static CirBixorErrorState xor_expr(CirExpression expression, Object difference) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(difference == null) {
			throw new IllegalArgumentException("Invalid difference: null");
		}
		else if(StateMutations.is_integer(expression)) {
			SymbolExpression ovalue, mvalue;
			if(StateMutations.is_assigned(expression)) {
				CirAssignStatement statement = (CirAssignStatement) expression.statement_of();
				ovalue = SymbolFactory.sym_expression(statement.get_rvalue());
			}
			else {
				ovalue = SymbolFactory.sym_expression(expression);
			}
			mvalue = SymbolFactory.sym_expression(difference);
			return new CirBixorErrorState(expression.execution_of(),
					CirStateStore.new_unit(expression), ovalue, mvalue);
		}
		else {
			throw new IllegalArgumentException(expression.generate_code(true));
		}
	}
	
	/* vdef-error */
	/**
	 * @param expression
	 * @param muta_value
	 * @return [vdef] <== set_expr(orig_value, muta_value)
	 * @throws Exception
	 */
	public static CirValueErrorState set_vdef(CirExpression expression, 
			SymbolExpression identifier, Object muta_value) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(muta_value == null) {
			throw new IllegalArgumentException("Invalid muta_value: null");
		}
		else if(StateMutations.is_boolean(expression)) {	/* cond */
			SymbolExpression ovalue, mvalue;
			ovalue = SymbolFactory.sym_condition(expression, true);
			mvalue = SymbolFactory.sym_condition(muta_value, true);
			return new CirValueErrorState(expression.execution_of(), 
					CirStateStore.new_vdef(expression, identifier), ovalue, mvalue);
		}
		else {												/* expr */
			SymbolExpression ovalue, mvalue;
			ovalue = SymbolFactory.sym_expression(expression);
			mvalue = SymbolFactory.sym_expression(muta_value);
			return new CirValueErrorState(expression.execution_of(),
					CirStateStore.new_vdef(expression, identifier), ovalue, mvalue);
		}
	}
	/**
	 * @param expression
	 * @param difference
	 * @return [vdef] <== inc_expr(base_value, difference)
	 * @throws Exception
	 */
	public static CirIncreErrorState inc_vdef(CirExpression expression, 
			SymbolExpression identifier, Object difference) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(difference == null) {
			throw new IllegalArgumentException("Invalid difference: null");
		}
		else if(StateMutations.is_numeric(expression) 
				|| StateMutations.is_address(expression)) {
			SymbolExpression ovalue, mvalue;
			if(StateMutations.is_assigned(expression)) {
				CirAssignStatement statement = (CirAssignStatement) expression.statement_of();
				ovalue = SymbolFactory.sym_expression(statement.get_rvalue());
			}
			else {
				ovalue = SymbolFactory.sym_expression(expression);
			}
			mvalue = SymbolFactory.sym_expression(difference);
			return new CirIncreErrorState(expression.execution_of(),
					CirStateStore.new_vdef(expression, identifier), ovalue, mvalue);
		}
		else {
			throw new IllegalArgumentException(expression.generate_code(true));
		}
	}
	/**
	 * @param expression
	 * @param difference
	 * @return [expr|cvar] <== xor_expr(base_value, difference)
	 * @throws Exception
	 */
	public static CirBixorErrorState xor_vdef(CirExpression expression, 
			SymbolExpression identifier, Object difference) throws Exception {
		if(expression == null || expression.statement_of() == null) {
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(difference == null) {
			throw new IllegalArgumentException("Invalid difference: null");
		}
		else if(StateMutations.is_integer(expression)) {
			SymbolExpression ovalue, mvalue;
			ovalue = SymbolFactory.sym_expression(expression);
			mvalue = SymbolFactory.sym_expression(difference);
			return new CirBixorErrorState(expression.execution_of(),
					CirStateStore.new_vdef(expression, identifier), ovalue, mvalue);
		}
		else {
			throw new IllegalArgumentException(expression.generate_code(true));
		}
	}
	
	/**
	 * It validates whether the logical state occurs (satisfied) at the given running context
	 * @param context	null if validate is performed in purely static analysis.
	 * @return			True {satisfied}; False {not-satisfied}; Null {unknown};
	 * @throws Exception
	 */
	public abstract Boolean validate(SymbolProcess context) throws Exception;
	/**
	 * It validates whether the logical state occurs in any possible static context
	 * @return	True {satisfied}; False {not-satisfied}; Null {unknown};
	 * @throws Exception
	 */
	public Boolean validate() throws Exception { return this.validate(null); }
	
	/* supporting utilities */
	/**
	 * @param target
	 * @return collect the list of execution flows from source to this target in decidable form
	 * @throws Exception
	 */
	protected CirExecutionPath get_previous_path(CirExecution target) throws Exception {
		if(target == null) {
			throw new IllegalArgumentException("Invalid target: null");
		}
		else {
			CirExecutionPath path = new CirExecutionPath(target);
			CirExecutionFlow flow; CirExecution source;
			while(true) {
				source = path.get_source();
				if(source.get_in_degree() == 1) {
					flow = source.get_in_flow(0);
					if(flow.get_type() == CirExecutionFlowType.next_flow
						|| flow.get_type() == CirExecutionFlowType.skip_flow
						|| flow.get_type() == CirExecutionFlowType.virt_flow) {
						path.insert(flow);
					}
					else if(flow.get_type() == CirExecutionFlowType.retr_flow) {
						target = source;
						source = source.get_graph().get_execution(source.get_id() - 1);
						flow = CirExecutionFlow.virtual_flow(source, target);
						path.insert(flow);
					}
					else {
						break;
					}
				}
				else {
					break;
				}
			}
			return path;
		}
	}
	/**
	 * @return collect the list of execution flows from source to this target in decidable form
	 * @throws Exception
	 */
	protected CirExecutionPath get_previous_path() throws Exception {
		return this.get_previous_path(this.get_execution());
	}
	/**
	 * @param parent
	 * @param child
	 * @return whether the parent contains the child under its sub-tree
	 */
	private boolean has_symbol_node(SymbolNode parent, SymbolNode child) {
		if(parent == null) {
			return false;
		}
		else if(child == null) {
			return false;
		}
		else {
			Queue<SymbolNode> queue = new LinkedList<SymbolNode>();
			queue.add(parent); 
			while(!queue.isEmpty()) {
				SymbolNode node = queue.poll();
				if(node.equals(child)) {
					return true;
				}
				else {
					for(SymbolNode pchild : node.get_children()) {
						queue.add(pchild);
					}
				}
			}
			return false;
		}
	}
	/**
	 * @param root
	 * @return it collects all the references used in the root node
	 */
	private Collection<SymbolExpression> get_references_in(SymbolNode root) {
		Set<SymbolExpression> references = new HashSet<SymbolExpression>();
		if(root != null) {
			Queue<SymbolNode> queue = new LinkedList<SymbolNode>();
			queue.add(root); 
			while(!queue.isEmpty()) {
				SymbolNode node = queue.poll();
				for(SymbolNode child : node.get_children()) {
					queue.add(child);
				}
				
				if(node instanceof SymbolExpression) {
					SymbolExpression expression = (SymbolExpression) node;
					if(expression.is_reference()) {
						references.add(expression);
					}
				}
			}
		}
		return references;
	}
	/**
	 * @param parent
	 * @param references
	 * @return whether any references is defined in the parent node
	 */
	private boolean is_defined_at(SymbolNode parent, Collection<SymbolExpression> references) {
		if(parent == null) {
			return false;
		}
		else if(references == null || references.isEmpty()) {
			return false;
		}
		else {
			for(SymbolExpression reference : references) {
				if(this.has_symbol_node(parent, reference)) {
					return true;
				}
			}
			return false;
		}
	}
	/**
	 * @param execution
	 * @param references
	 * @return whether any references are defined in the given execution
	 */
	private boolean is_defined_at(CirExecution execution, Collection<SymbolExpression> references) throws Exception {
		if(execution == null) {
			return false;
		}
		else if(references == null || references.isEmpty()) {
			return false;
		}
		else {
			CirStatement statement = execution.get_statement();
			if(statement instanceof CirAssignStatement) {
				SymbolExpression parent = SymbolFactory.sym_expression(((CirAssignStatement) statement).get_lvalue());
				return this.is_defined_at(parent, references);
			}
			else if(statement instanceof CirIfStatement) {
				SymbolExpression parent = SymbolFactory.sym_expression(((CirIfStatement) statement).get_condition());
				return this.is_defined_at(parent, references);
			}
			else if(statement instanceof CirCaseStatement) {
				SymbolExpression parent = SymbolFactory.sym_expression(((CirCaseStatement) statement).get_condition());
				return this.is_defined_at(parent, references);
			}
			else {
				return false;
			}
		}
	}
	/**
	 * @param execution
	 * @param condition
	 * @return find the previous checkpoint (decidable path) to evaluate the condition
	 * @throws Exception
	 */
	protected CirExecution find_previous_checkpoint(CirExecution execution, SymbolExpression condition) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else {
			Collection<SymbolExpression> references = get_references_in(condition);
			CirExecutionPath path = this.get_previous_path(execution);
			Iterator<CirExecutionEdge> iterator = path.get_iterator(true);
			while(iterator.hasNext()) {
				CirExecutionEdge edge = iterator.next();
				if(this.is_defined_at(edge.get_source(), references)) {
					return edge.get_target();
				}
			}
			return path.get_source();
		}
	}
	/**
	 * @param condition
	 * @return
	 * @throws Exception
	 */
	protected CirExecution find_previous_checkpoint(SymbolExpression condition) throws Exception {
		return this.find_previous_checkpoint(this.get_execution(), condition);
	}
	
}
