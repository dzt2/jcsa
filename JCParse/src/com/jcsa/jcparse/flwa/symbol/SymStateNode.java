package com.jcsa.jcparse.flwa.symbol;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirAddressExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirFieldExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirReferExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlowType;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;
import com.jcsa.jcparse.test.state.CStateNode;
import com.jcsa.jcparse.test.state.CStatePath;
import com.jcsa.jcparse.test.state.CStateUnit;

/**
 * It maintains the symbolic state of a statement executed during testing,
 * including the guidance condition and the valuations of its expressions.
 * 
 * @author yukimula
 *
 */
public class SymStateNode {
	
	/* definitions */
	/** the statement being executed in this node **/
	private CirExecution execution;
	/** the condition required before reaching this node **/
	private SymExpression prev_condition;
	/** the valuations of the expressions in this node **/
	private List<SymStateUnit> units;
	
	/* constructor */
	/**
	 * create the state node w.r.t. the statement with TRUE guidance
	 * as well as non-context valuation in the point.
	 * @param statement
	 * @throws Exception
	 */
	protected SymStateNode(CirStatement statement) throws Exception {
		if(statement == null)
			throw new IllegalArgumentException("Invalid statement: null");
		else {
			this.execution = statement.get_tree().get_localizer().get_execution(statement);
			this.prev_condition = SymFactory.new_constant(Boolean.TRUE);
			this.units = new LinkedList<SymStateUnit>();
			this.build_state_units_in(statement);
		}
	}
	private boolean is_left_reference(CirExpression expression) throws Exception {
		CirNode parent = expression.get_parent();
		if(expression instanceof CirReferExpression) {
			if(parent instanceof CirAssignStatement) {
				return ((CirAssignStatement) parent).get_lvalue() == expression;
			}
			else if(parent instanceof CirFieldExpression) {
				return ((CirFieldExpression) parent).get_body() == expression;
			}
			else if(parent instanceof CirAddressExpression) {
				return ((CirAddressExpression) parent).get_operand() == expression;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}
	private void build_state_units_in(CirNode location) throws Exception {
		for(CirNode child : location.get_children()) {
			this.build_state_units_in(child);
		}
		
		if(location instanceof CirExpression) {
			if(!this.is_left_reference((CirExpression) location)) {
				this.units.add(new SymStateUnit((CirExpression) location));
			}
		}
	}
	
	/* getters */
	/**
	 * @return the execution of this statement
	 */
	public CirExecution get_execution() { return this.execution; }
	/**
	 * @return the statement being executed in this node
	 */
	public CirStatement get_statement() { return this.execution.get_statement(); }
	/**
	 * @return the guidance condition for reaching this point from prior one
	 */
	public SymExpression get_guidance() { return this.prev_condition; }
	/**
	 * @return the valuations of its expressions in the statement
	 */
	public Iterable<SymStateUnit> get_state_units() { return this.units; }
	/**
	 * whether there is unit w.r.t. the expression in this statement
	 * @param expression
	 * @return
	 */
	public boolean has_unit(CirExpression expression) {
		for(SymStateUnit unit : this.units) {
			if(unit.get_location() == expression) {
				return true;
			}
		}
		return false;
	}
	/**
	 * @param expression
	 * @return the unit of valuation of the expression in this statement
	 * @throws Exception
	 */
	public SymStateUnit get_unit(CirExpression expression) throws Exception {
		if(expression == null || expression.statement_of() != this.execution.get_statement())
			throw new IllegalArgumentException("Invalid: " + expression);
		else {
			for(SymStateUnit unit : this.units) {
				if(unit.get_location() == expression) {
					return unit;
				}
			}
			throw new IllegalArgumentException("Invalid: " + expression);
		}
	}
	
	/* setters */
	/**
	 * set the guidance condition for executing this statement
	 * @param guidance
	 * @throws Exception
	 */
	private void set_guidance(SymExpression guidance) throws Exception {
		if(guidance == null)
			throw new IllegalArgumentException("Invalid guidance: null");
		else {
			guidance = SymFactory.sym_condition(guidance, true);
		}
	}
	/**
	 * update the value hold by the expression
	 * @param expression
	 * @param value
	 * @throws Exception
	 */
	public void set_unit(CirExpression expression, SymExpression value) throws Exception {
		if(expression == null || expression.statement_of() != this.get_statement())
			throw new IllegalArgumentException("Invalid: " + expression);
		else if(value == null)
			throw new IllegalArgumentException("Invalid value as null");
		else {
			this.get_unit(expression).set_value(value);
		}
	}
	/**
	 * update the valuation and guidance in this program.
	 * @param contexts
	 * @param flow
	 * @throws Exception
	 */
	public void update(CStateContexts contexts, CirExecutionFlow flow) throws Exception {
		if(flow == null || flow.get_target().get_statement() != this.get_statement())
			throw new IllegalArgumentException("Invalid flow: " + flow.toString());
		else {
			SymExpression prev_value, post_value;
			for(SymStateUnit unit : this.units) {
				prev_value = SymFactory.parse(unit.get_location());
				post_value = SymEvaluator.evaluate_on(prev_value, contexts);
				unit.set_value(post_value);
			}
			
			CirStatement statement = flow.get_source().get_statement();
			CirExpression condition;
			if(flow.get_type() == CirExecutionFlowType.true_flow) {
				if(statement instanceof CirIfStatement) {
					condition = ((CirIfStatement) statement).get_condition();
				}
				else {
					condition = ((CirCaseStatement) statement).get_condition();
				}
				this.set_guidance(SymEvaluator.evaluate_on(SymFactory.sym_condition(condition, true), contexts));
			}
			else if(flow.get_type() == CirExecutionFlowType.fals_flow) {
				if(statement instanceof CirIfStatement) {
					condition = ((CirIfStatement) statement).get_condition();
				}
				else {
					condition = ((CirCaseStatement) statement).get_condition();
				}
				this.set_guidance(SymEvaluator.evaluate_on(SymFactory.sym_condition(condition, false), contexts));
			}
			else {
				this.set_guidance(SymFactory.parse(Boolean.TRUE));
			}
		}
	}
	/**
	 * update the valuation and guidance in this program.
	 * @param contexts
	 * @param execution
	 * @throws Exception
	 */
	public void update(CStateContexts contexts, CirExecution execution) throws Exception {
		if(execution == null || execution.get_statement() == this.get_statement())
			throw new IllegalArgumentException("Invalid flow: " + execution.toString());
		else if(execution.get_in_degree() == 1) {
			this.update(contexts, execution.get_in_flow(0));
		}
		else {
			SymExpression prev_value, post_value;
			for(SymStateUnit unit : this.units) {
				prev_value = SymFactory.parse(unit.get_location());
				post_value = SymEvaluator.evaluate_on(prev_value, contexts);
				unit.set_value(post_value);
			}
			this.set_guidance(SymFactory.new_constant(Boolean.TRUE));
		}
	}
	
	/* concrete evaluations */
	/**
	 * @param contexts
	 * @param state_node
	 * @return
	 * @throws Exception
	 */
	private static SymStateNode generate(CStateContexts contexts, CStateNode state_node) throws Exception {
		/* accumulate the state node to the contexts */
		contexts.accumulate(state_node);
		
		/* update and evaluate the valuation in the node */
		SymStateNode sym_node = new SymStateNode(state_node.get_statement());
		for(SymStateUnit sym_unit : sym_node.units) {
			CirExpression expression = sym_unit.get_location();
			if(state_node.has_unit(expression)) {
				CStateUnit state_unit = state_node.get_unit(expression);
				sym_unit.set_value(state_unit.get_value());
			}
			else {
				sym_unit.set_value(SymEvaluator.evaluate_on(
						SymFactory.parse(expression), contexts));
			}
		}
		
		/* find the execution flow from prev-node to state-node */
		CirExecution target = state_node.get_execution();
		CirExecutionFlow in_flow;
		if(target.get_in_degree() == 1) {
			in_flow = target.get_in_flow(0);
		}
		else if(state_node.get_prev_node() != null) {
			CStateNode prev_node = state_node.get_prev_node();
			CirExecution source = prev_node.get_execution();
			in_flow = null;
			for(CirExecutionFlow flow : source.get_ou_flows()) {
				if(flow.get_target() == target) {
					in_flow = flow;
					break;
				}
			}
		}
		else {
			in_flow = null;
		}
		
		/* set the guidance constraint on the path condition */
		if(in_flow != null) {
			CirStatement statement = in_flow.get_source().get_statement();
			CirExpression condition;
			if(in_flow.get_type() == CirExecutionFlowType.true_flow) {
				if(statement instanceof CirIfStatement) {
					condition = ((CirIfStatement) statement).get_condition();
				}
				else {
					condition = ((CirCaseStatement) statement).get_condition();
				}
				sym_node.set_guidance(SymEvaluator.evaluate_on(SymFactory.sym_condition(condition, true), contexts));
			}
			else if(in_flow.get_type() == CirExecutionFlowType.fals_flow) {
				if(statement instanceof CirIfStatement) {
					condition = ((CirIfStatement) statement).get_condition();
				}
				else {
					condition = ((CirCaseStatement) statement).get_condition();
				}
				sym_node.set_guidance(SymEvaluator.evaluate_on(SymFactory.sym_condition(condition, false), contexts));
			}
			else {
				sym_node.set_guidance(SymFactory.parse(Boolean.TRUE));
			}
		}
		else {
			sym_node.set_guidance(SymFactory.parse(Boolean.TRUE));
		}
		
		return sym_node;
	}
	/**
	 * @param state_path
	 * @return generate the symbolic path from concrete evaluation
	 * @throws Exception
	 */
	public static List<SymStateNode> generate(CStatePath state_path) throws Exception {
		List<SymStateNode> sym_path = new ArrayList<SymStateNode>();
		CStateContexts contexts = new CStateContexts();
		for(CStateNode state_node : state_path.get_nodes()) {
			SymStateNode sym_node = generate(contexts, state_node);
			sym_path.add(sym_node);
		}
		return sym_path;
	}
	
}
