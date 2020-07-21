package com.jcsa.jcparse.lopt.models.depend;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jcsa.jcparse.lang.irlang.expr.CirAddressExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirCastExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirComputeExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirDeferExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirFieldExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirInitializerBody;
import com.jcsa.jcparse.lang.irlang.expr.CirReferExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirWaitExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.stmt.CirArgumentList;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCallStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirReturnAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirWaitAssignStatement;
import com.jcsa.jcparse.lopt.ingraph.CirInstanceNode;

public class CDependNode {
	
	private CDependGraph graph;
	private CirInstanceNode instance;
	private List<CDependEdge> in, ou;
	
	protected CDependNode(CDependGraph graph, CirInstanceNode instance) throws Exception {
		if(graph == null)
			throw new IllegalArgumentException("Invalid graph: null");
		else if(instance == null)
			throw new IllegalArgumentException("Invalid instance: null");
		else {
			this.graph = graph; this.instance = instance;
			this.in = new LinkedList<CDependEdge>();
			this.ou = new LinkedList<CDependEdge>();
		}
	}
	
	public CDependGraph get_graph() { return this.graph; }
	public CirInstanceNode get_instance() { return this.instance; }
	public CirExecution get_execution() { return this.instance.get_execution(); }
	public CirStatement get_statement() { return this.instance.get_execution().get_statement(); }
	public Iterable<CDependEdge> get_in_edges() { return this.in; }
	public Iterable<CDependEdge> get_ou_edges() { return this.ou; }
	protected void predicate_depend(CDependNode target, boolean predicate_value) throws Exception {
		CirStatement target_stmt = target.get_statement();
		
		CDependEdge edge;
		if(target_stmt instanceof CirIfStatement) {
			CDependPredicate predicate = new CDependPredicate(
					(CirIfStatement) target_stmt, predicate_value);
			edge = new CDependEdge(CDependType.predicate_depend, this, target, predicate);
		}
		else if(target_stmt instanceof CirCaseStatement) {
			CDependPredicate predicate = new CDependPredicate(
					(CirCaseStatement) target_stmt, predicate_value);
			edge = new CDependEdge(CDependType.predicate_depend, this, target, predicate);
		}
		else {
			throw new IllegalArgumentException("Invalid: " + target_stmt);
		}
		
		this.ou.add(edge); target.in.add(edge); 
	}
	protected void stmt_call_depend(CDependNode target) throws Exception {
		if(target.get_statement() instanceof CirCallStatement) {
			CDependEdge edge = new CDependEdge(CDependType.
					stmt_call_depend, this, target, null);
			this.ou.add(edge); target.in.add(edge); 
		}
		else {
			throw new IllegalArgumentException("Invalid target");
		}
	}
	protected void stmt_exit_depend(CDependNode target) throws Exception {
		if(target.get_statement() instanceof CirWaitAssignStatement) {
			CDependEdge edge = new CDependEdge(CDependType.
					stmt_exit_depend, this, target, null);
			this.ou.add(edge); target.in.add(edge);
		}
		else {
			throw new IllegalArgumentException("Invalid target");
		}
	}
	protected void use_define_depend(CDependNode target) throws Exception {
		CirStatement source_stmt = this.get_statement();
		CirStatement target_stmt = target.get_statement();
		
		if(target_stmt instanceof CirAssignStatement) {
			CirExpression def = ((CirAssignStatement) target_stmt).get_lvalue();
			Set<CirExpression> usage_set = new HashSet<CirExpression>();
			this.collect_usage_set(source_stmt, usage_set, def.generate_code(true));
			
			if(!usage_set.isEmpty()) {
				CirExecution source_execution = this.get_execution();
				CirExecution target_execution = target.get_execution();
				Set<CirExecutionFlow> flows = this.get_reachable_set(target_execution, source_execution);
				
				for(CirExpression use : usage_set) {
					CDependReference reference = new CDependReference(def, use);
					reference.set_flow_path(flows);
					
					CDependEdge edge = new CDependEdge(CDependType.use_defin_depend, this, target, reference);
					this.ou.add(edge); target.in.add(edge); 
				}
			}
		}
	}
	private void collect_usage_set(CirStatement statement, Set<CirExpression> uses, String key) throws Exception {
		if(statement instanceof CirAssignStatement) {
			this.collect_usage_set(((CirAssignStatement) statement).get_lvalue(), uses, key);
			this.collect_usage_set(((CirAssignStatement) statement).get_rvalue(), uses, key);
			uses.remove(((CirAssignStatement) statement).get_lvalue());
		}
		else if(statement instanceof CirCallStatement) {
			this.collect_usage_set(((CirCallStatement) statement).get_function(), uses, key);
			CirArgumentList arguments = ((CirCallStatement) statement).get_arguments();
			for(int k = 0; k < arguments.number_of_arguments(); k++) {
				this.collect_usage_set(arguments.get_argument(k), uses, key);
			}
		}
		else if(statement instanceof CirIfStatement) {
			this.collect_usage_set(((CirIfStatement) statement).get_condition(), uses, key);
		}
		else if(statement instanceof CirCaseStatement) {
			this.collect_usage_set(((CirCaseStatement) statement).get_condition(), uses, key);
		}
	}
	private void collect_usage_set(CirExpression expression, Set<CirExpression> uses, String key) throws Exception {
		if(expression instanceof CirReferExpression) {
			if(expression.generate_code(true).equals(key)) {
				uses.add(expression);
			}
		}
		
		if(expression instanceof CirDeferExpression) {
			this.collect_usage_set(((CirDeferExpression) expression).get_address(), uses, key);
		}
		else if(expression instanceof CirFieldExpression) {
			this.collect_usage_set(((CirFieldExpression) expression).get_body(), uses, key);
		}
		else if(expression instanceof CirAddressExpression) {
			this.collect_usage_set(((CirAddressExpression) expression).get_operand(), uses, key);
		}
		else if(expression instanceof CirCastExpression) {
			this.collect_usage_set(((CirCastExpression) expression).get_operand(), uses, key);
		}
		else if(expression instanceof CirComputeExpression) {
			int length = ((CirComputeExpression) expression).number_of_operand();
			for(int k = 0; k < length; k++) {
				CirExpression operand = ((CirComputeExpression) expression).get_operand(k);
				this.collect_usage_set(operand, uses, key);
			}
		}
		else if(expression instanceof CirInitializerBody) {
			int length = ((CirInitializerBody) expression).number_of_elements();
			for(int k = 0; k < length; k++) {
				CirExpression element = ((CirInitializerBody) expression).get_element(k);
				this.collect_usage_set(element, uses, key);
			}
		}
		else if(expression instanceof CirWaitExpression) {
			this.collect_usage_set(((CirWaitExpression) expression).get_function(), uses, key);
		}
		
	}
	private void solve_reachable_set(CirExecutionFlow flow, CirExecution target, Map<CirExecutionFlow, Boolean> solutions) throws Exception {
		if(!solutions.containsKey(flow)) {
			if(flow.get_target() == target) {
				solutions.put(flow, true);
			}
			else {
				solutions.put(flow, false);
				
				CirExecution next_execution;
				switch(flow.get_type()) {
				case call_flow:
				{
					next_execution = flow.get_source().get_graph().get_function().
							get_graph().get_calling(flow).get_wait_execution();
				}
				break;
				case retr_flow: next_execution = null; break;
				default: next_execution = flow.get_target(); break;
				}
				
				if(next_execution != null) {
					boolean reach = false;
					for(CirExecutionFlow next_flow : next_execution.get_ou_flows()) {
						this.solve_reachable_set(next_flow, target, solutions);
						if(solutions.get(next_flow)) reach = true;
					}
					solutions.put(flow, reach);
				}
			}
		}
	}
	private Set<CirExecutionFlow> get_reachable_set(CirExecution source, CirExecution target) throws Exception {
		Map<CirExecutionFlow, Boolean> solutions = new HashMap<CirExecutionFlow, Boolean>();
		for(CirExecutionFlow flow : source.get_ou_flows()) {
			this.solve_reachable_set(flow, target, solutions);
		}
		
		Set<CirExecutionFlow> all_flows = new HashSet<CirExecutionFlow>();
		for(CirExecutionFlow flow : solutions.keySet()) {
			if(solutions.get(flow)) {
				all_flows.add(flow);
			}
		}
		
		Set<CirExecutionFlow> control_flows = new HashSet<CirExecutionFlow>();
		for(CirExecutionFlow flow : all_flows) {
			switch(flow.get_type()) {
			case true_flow:
			case fals_flow:
			{
				CirExecution if_execution = flow.get_source();
				if(!(all_flows.contains(if_execution.get_ou_flow(0))) || 
					!(all_flows.contains(if_execution.get_ou_flow(1)))) {
					control_flows.add(flow);
				}
			}
			break;
			default: break;
			}
		}
		
		return control_flows;
	}
	protected void param_arg_depend(CDependNode target, CirExpression argument) throws Exception {
		CirAssignStatement assign = (CirAssignStatement) this.get_statement();
		CDependReference reference = new CDependReference(argument, assign.get_rvalue());
		reference.add_flow(target.get_execution().get_ou_flow(0));
		
		CDependEdge edge = new CDependEdge(CDependType.param_arg_depend, this, target, reference);
		this.ou.add(edge); target.in.add(edge); 
	}
	protected void wait_retr_depend(CDependNode target) throws Exception {
		CirWaitAssignStatement wait_stmt = (CirWaitAssignStatement) this.get_statement();
		CirReturnAssignStatement retr_stmt = (CirReturnAssignStatement) target.get_statement();
		
		CDependReference reference = new CDependReference(retr_stmt.get_lvalue(), wait_stmt.get_rvalue());
		reference.add_flow(this.get_execution().get_in_flow(0));
		
		CDependEdge edge = new CDependEdge(CDependType.wait_retr_depend, this, target, reference);
		this.ou.add(edge); target.in.add(edge); 
	}
	
	@Override
	public String toString() {
		return this.get_execution().toString() + "@" + this.instance.get_context().hashCode();
	}
}
