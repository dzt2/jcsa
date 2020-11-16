package com.jcsa.jcparse.flwa.defuse;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jcsa.jcparse.base.BitSequence;
import com.jcsa.jcparse.base.BitSet;
import com.jcsa.jcparse.base.BitSetBase;
import com.jcsa.jcparse.flwa.CirInstance;
import com.jcsa.jcparse.flwa.analysis.AbsInterpreter;
import com.jcsa.jcparse.flwa.analysis.AbsOperator;
import com.jcsa.jcparse.flwa.analysis.AbsValue;
import com.jcsa.jcparse.flwa.analysis.BitSetValue;
import com.jcsa.jcparse.flwa.graph.CirInstanceEdge;
import com.jcsa.jcparse.flwa.graph.CirInstanceGraph;
import com.jcsa.jcparse.flwa.graph.CirInstanceNode;
import com.jcsa.jcparse.lang.irlang.expr.CirAddressExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirCastExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirComputeExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirDeferExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirFieldExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirNameExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirWaitExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlowType;
import com.jcsa.jcparse.lang.irlang.stmt.CirArgumentList;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCallStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirInitAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirReturnAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirWaitAssignStatement;

/**
 * To build up the definition-usage links between variables within the program under analysis.
 * 
 * @author yukimula
 *
 */
class CDefineUseBuilder {
	
	/* operator implementation */
	/**
	 * Abstract operator used to compute the valid assignment instances in forward analysis.
	 * @author yukimula
	 *
	 */
	protected static class CDefineUseOperator implements AbsOperator {
		
		private BitSetBase bitset_base;
		/**
		 * create the operator to determine valid assignment in data flow analysis
		 * @param graph
		 * @throws Exception
		 */
		protected CDefineUseOperator(CirInstanceGraph graph) throws Exception {
			Set<Object> assignments = new HashSet<Object>();
			for(Object context : graph.get_contexts()) {
				for(CirInstance instance : graph.get_instances(context)) {
					if(instance instanceof CirInstanceNode) {
						CirStatement statement = ((CirInstanceNode) 
								instance).get_execution().get_statement();
						if(statement instanceof CirAssignStatement) {
							assignments.add(instance);
						}
					}
				}
			}
			this.bitset_base = BitSetBase.base(assignments);
		}
		
		/* initial methods */
		@Override
		public AbsValue initial_value(CirInstanceNode exec_instance) throws Exception {
			return new BitSetValue(this.bitset_base.new_set());
		}
		@Override
		public AbsValue initial_value(CirInstanceEdge flow_instance) throws Exception {
			return new BitSetValue(this.bitset_base.new_set());
		}
		
		/* update methods */ 
		@Override
		public AbsValue update_value(CirInstanceNode exec_instance, boolean forward) throws Exception {
			/* declarations */
			BitSet result = this.bitset_base.new_set();
			BitSequence result_bits = result.get_set();
			Iterable<CirInstanceEdge> edges;
			if(forward)
				 edges = exec_instance.get_in_edges();
			else edges = exec_instance.get_ou_edges();
			CirExecution execution = exec_instance.get_execution();
			CirStatement statement = execution.get_statement();
			
			/* Y = X1 | X2 | X3 | ... | Xn except the entry and exit */
			/*
			if(forward && statement instanceof CirBegStatement) {}
			else if(!forward && statement instanceof CirEndStatement) {}
			else {
				for(CirInstanceEdge edge : edges) {
					BitSetValue value = (BitSetValue) edge.get_state();
					BitSequence bitstring = value.get().get_set();
					result_bits.set(result_bits.or(bitstring));
				}
			}
			*/
			for(CirInstanceEdge edge : edges) {
				BitSetValue value = (BitSetValue) edge.get_state();
				BitSequence bitstring = value.get().get_set();
				result_bits.set(result_bits.or(bitstring));
			}
			
			/* Kill(Y, stmt) */
			if(statement instanceof CirAssignStatement) {
				CirExpression lvalue = ((CirAssignStatement) statement).get_lvalue();
				// String refer_code = lvalue.generate_code();
				// String refer_code = CDefineUseBuilder.get_unique_expression(exec_instance, lvalue);
				String refer_code = exec_instance.generate_code(lvalue);
				
				for(int k = 0; k < result_bits.length(); k++) {
					if(result_bits.get(k)) {
						CirInstanceNode old_instance = 
								(CirInstanceNode) result.get_base().get(k);
						CirAssignStatement stmt = (CirAssignStatement) 
								old_instance.get_execution().get_statement();
						// String old_refer = stmt.get_lvalue().generate_code();
						/*String old_refer = CDefineUseBuilder.
								get_unique_expression(old_instance, stmt.get_lvalue());*/
						String old_refer = old_instance.generate_code(stmt.get_lvalue());
						
						if(old_refer.equals(refer_code)) 
							result_bits.set(k, BitSequence.BIT0);
					}
				}
			}
			
			return new BitSetValue(result);
		}
		@Override
		public AbsValue update_value(CirInstanceEdge flow_instance, boolean forward) throws Exception {
			BitSet result = this.bitset_base.new_set();
			BitSequence result_bits = result.get_set();
			
			CirInstanceNode source;
			if(forward)
				 source = flow_instance.get_source();
			else source = flow_instance.get_target();
			BitSetValue pvalue = (BitSetValue) source.get_state();
			result_bits.set(pvalue.get().get_set());
			
			CirStatement statement = source.get_execution().get_statement();
			if(statement instanceof CirAssignStatement) {
				result.add(source);
			}
			
			return new BitSetValue(result);
		}
		
	}
	
	/* attributes */
	/** abstract operator for valid assignment analysis **/
	private CDefineUseOperator operator;
	/** the program flow graph being analysis **/
	private CirInstanceGraph input;
	/** the definition-usage graph being constructed **/
	private CDefineUseGraph output;
	
	/* constructor */
	/** singleton of the builder **/
	protected static final CDefineUseBuilder builder = new CDefineUseBuilder();
	/** constructor of definition-usage graph builder **/
	private CDefineUseBuilder() { this.operator = null; this.input = null; this.output = null; }
	
	/* processing methods */
	/**
	 * Set the input program and initialize the operator for abstract interpretation.
	 * @param input
	 * @throws Exception
	 */
	private void set_inputs(CirInstanceGraph input) throws Exception {
		if(input == null)
			throw new IllegalArgumentException("Invalid input: null");
		else {
			this.operator = new CDefineUseOperator(input);
			this.input = input;
		}
	}
	/**
	 * Perform data flow analysis to solve the dominance set of the program elements
	 * @param forward
	 * @throws Exception
	 */
	private void analysis(boolean forward) throws Exception {
		AbsInterpreter interpreter;
		if(forward)
			interpreter = AbsInterpreter.forward_interpreter(operator);
		else interpreter = AbsInterpreter.backward_interpreter(operator);
		interpreter.interpret(input);
	}
	/**
	 * construct the dominance graph with respect to the dominance set
	 * computed during the program analysis.
	 * @param output
	 * @throws Exception
	 */
	private void get_output(CDefineUseGraph output) throws Exception {
		if(output == null)
			throw new IllegalArgumentException("Invalid output as null");
		else {
			this.output = output;	
			this.create_nodes();
			this.create_edges();
			this.create_links();
		}
	}
	/**
	 * Construct the definition-usage graph with respect to the input program under the given direction
	 * @param input
	 * @param forward true when the graph describes forward dominance relationships
	 * @param output
	 * @throws Exception
	 */
	protected void build(CirInstanceGraph input, CDefineUseGraph output) throws Exception {
		this.set_inputs(input);
		this.analysis(true);
		this.get_output(output);
	}
	
	/* building methods */
	/**
	 * create the usage nodes within the provided expression tree
	 * @param instance
	 * @param expression
	 * @throws Exception
	 */
	private void create_nodes(CirInstanceNode instance, CirExpression expression) throws Exception {
		if(expression instanceof CirNameExpression) {
			this.output.new_node(false, instance, expression);
		}
		else if(expression instanceof CirDeferExpression) {
			this.output.new_node(false, instance, expression);
			
			this.create_nodes(instance, ((CirDeferExpression) expression).get_address());
		}
		else if(expression instanceof CirFieldExpression) {
			this.output.new_node(false, instance, expression);
			
			this.create_nodes(instance, ((CirFieldExpression) expression).get_body());
		}
		else if(expression instanceof CirAddressExpression) {
			this.create_nodes(instance, ((CirAddressExpression) expression).get_operand());
		}
		else if(expression instanceof CirCastExpression) {
			this.create_nodes(instance, ((CirCastExpression) expression).get_operand());
		}
		else if(expression instanceof CirWaitExpression) {
			this.create_nodes(instance, ((CirWaitExpression) expression).get_function());
		}
		else if(expression instanceof CirComputeExpression) {
			for(int k = 0; k < expression.number_of_children(); k++) {
				this.create_nodes(instance, ((CirComputeExpression) expression).get_operand(k));
			}
		}
	}
	/**
	 * create the nodes for definition and usage points within the instance of statement being executed.
	 * @param instance
	 * @throws Exception
	 */
	private void create_nodes(CirInstanceNode instance) throws Exception {
		CirStatement statement = instance.get_execution().get_statement();
		if(statement instanceof CirAssignStatement) {
			output.new_node(true, instance, ((CirAssignStatement) statement).get_lvalue());
			this.create_nodes(instance, ((CirAssignStatement) statement).get_lvalue());
			this.create_nodes(instance, ((CirAssignStatement) statement).get_rvalue());
		}
		else if(statement instanceof CirCallStatement) {
			this.create_nodes(instance, ((CirCallStatement) statement).get_function());
			
			CirArgumentList list = ((CirCallStatement) statement).get_arguments();
			for(int k = 0; k < list.number_of_arguments(); k++) 
				this.create_nodes(instance, list.get_argument(k));
		}
		else if(statement instanceof CirCaseStatement) {
			this.create_nodes(instance, ((CirCaseStatement) statement).get_condition());
		}
		else if(statement instanceof CirIfStatement) {
			this.create_nodes(instance, ((CirIfStatement) statement).get_condition());
		}
	}
	/**
	 * create the nodes for all the definition and usage points through the entire program flow graph.
	 * @throws Exception
	 */
	private void create_nodes() throws Exception {
		for(Object context : this.input.get_contexts()) {
			for(CirInstance instance : this.input.get_instances(context)) {
				if(instance instanceof CirInstanceNode) {
					this.create_nodes((CirInstanceNode) instance);
				}
			}
		}
	}
	/** used to collect all the nodes (usage and definitio) in the statement being executed **/
	private Map<String, List<CDefineUseNode>> expressions = new HashMap<String, List<CDefineUseNode>>();
	/**
	 * create the edges to usage from its last definition(s) based on the valid assignment instances.
	 * @param instance
	 * @throws Exception
	 */
	private void create_edges(CirInstanceNode instance) throws Exception {
		/* 1. collect the expressions  */
		this.expressions.clear();
		for(CDefineUseNode node : this.output.get_nodes(instance)) {
			// String key = node.get_reference();
			// String key = CDefineUseBuilder.get_unique_expression(instance, node.get_expression());
			String key = instance.generate_code(node.get_expression());
			if(!this.expressions.containsKey(key)) 
				this.expressions.put(key, new LinkedList<CDefineUseNode>());
			this.expressions.get(key).add(node);
		}
		
		/* 2. link from the valid assignment definition points */
		BitSetValue value = (BitSetValue) instance.get_state();
		BitSequence bitstring = value.get().get_set();
		for(int k = 0; k < bitstring.length(); k++) {
			if(bitstring.get(k)) {
				CirInstanceNode prev_node = (CirInstanceNode) value.get().get_base().get(k);
				CirAssignStatement statement = 
						(CirAssignStatement) prev_node.get_execution().get_statement();
				CDefineUseNode source = output.get_node(prev_node, statement.get_lvalue());
				// String source_reference = source.get_reference();
				/*String source_reference = CDefineUseBuilder.
						get_unique_expression(source.get_instance(), source.get_expression());*/
				String source_reference = source.get_instance().generate_code(source.get_expression());
				
				if(this.expressions.containsKey(source_reference)) {
					for(CDefineUseNode target : this.expressions.get(source_reference)) {
						source.link_to(target);
					}
				}
			}
		}
	}
	/**
	 * create the edges to usage from its last definition(s) based on the valid assignment instances
	 * over the entire program.
	 * @throws Exception
	 */
	private void create_edges() throws Exception {
		for(Object context : this.input.get_contexts()) {
			for(CirInstance instance : this.input.get_instances(context)) {
				if(instance instanceof CirInstanceNode) {
					if(this.output.has_nodes((CirInstanceNode) instance))
						this.create_edges((CirInstanceNode) instance);
				}
			}
		}
	}
	/**
	 * link the arguments to the return assigned points.
	 * @param instance
	 * @throws Exception
	 */
	private void call_wait_links(CirInstanceNode instance) throws Exception {
		if(instance.get_ou_edge(0).get_type() == CirExecutionFlowType.skip_flow || 
				instance.get_ou_edge(0).get_type() == CirExecutionFlowType.virt_flow) {
			CirExecution call_execution = instance.get_execution();
			CirExecution wait_execution = call_execution.get_graph().
					get_execution(call_execution.get_id() + 1);
			CirInstanceNode next_instance = this.input.
					get_instance(instance.get_context(), wait_execution);
			CirWaitAssignStatement next_statement = 
					(CirWaitAssignStatement) wait_execution.get_statement();
			CDefineUseNode def = this.output.get_node(next_instance, next_statement.get_lvalue());
			
			for(CDefineUseNode use : this.output.get_nodes(instance)) {
				if(use.is_usage()) { use.link_to(def); }
			}
		}
	}
	/**
	 * link the arguments to the parameters in callee function
	 * @param instance
	 * @throws Exception
	 */
	private void call_parm_links(CirInstanceNode instance) throws Exception {
		if(instance.get_ou_edge(0).get_type() == CirExecutionFlowType.call_flow) {
			CirInstanceNode entry_node = instance.get_ou_edge(0).get_target();
			CirInstanceNode search_node = entry_node.get_ou_edge(0).get_target();
			CirCallStatement call_statement = 
					(CirCallStatement) instance.get_execution().get_statement();
			
			CirArgumentList arguments = call_statement.get_arguments();
			for(int k = 0; k < arguments.number_of_arguments(); k++) {
				if(search_node.get_execution().get_statement() instanceof CirInitAssignStatement) {
					CirInitAssignStatement param_statement = 
							(CirInitAssignStatement) search_node.get_execution().get_statement();
					CDefineUseNode def = this.output.get_node(search_node, param_statement.get_lvalue());
					CDefineUseNode use = this.output.new_node(false, instance, arguments.get_argument(k));
					use.link_to(def);
				}
				else break;
			}
		}
	}
	/**
	 * create the links from return-assignment definition points to the function expression
	 * within the wait expression in the wait assignment statement.
	 * @param instance
	 * @throws Exception
	 */
	private void retr_wait_links(CirInstanceNode instance) throws Exception {
		CirInstanceNode retr_goto_instance = instance.get_ou_edge(0).get_target();
		CirInstanceNode exit_instance = retr_goto_instance.get_ou_edge(0).get_target();
		if(exit_instance.get_ou_degree() == 1) {
			CirInstanceNode wait_instance = exit_instance.get_ou_edge(0).get_target();
			
			CirWaitAssignStatement wait_statement = 
					(CirWaitAssignStatement) wait_instance.get_execution().get_statement();
			CirWaitExpression wait_expression = (CirWaitExpression) wait_statement.get_rvalue();
			CDefineUseNode use = this.output.get_node(wait_instance, wait_expression.get_function());
			
			CirReturnAssignStatement retr_statement = 
					(CirReturnAssignStatement) instance.get_execution().get_statement();
			CDefineUseNode def = this.output.get_node(instance, retr_statement.get_lvalue());
			
			def.link_to(use);
		}
	}
	/**
	 * create the links from usage to the definition it corresponds to within the statement
	 * @param instance
	 * @throws Exception
	 */
	private void create_links(CirInstanceNode instance) throws Exception {
		CirStatement statement = instance.get_execution().get_statement();
		if(statement instanceof CirAssignStatement) {
			if(!(statement instanceof CirWaitAssignStatement)) {
				CDefineUseNode def = this.output.get_node(
						instance, ((CirAssignStatement) statement).get_lvalue());
				for(CDefineUseNode use : this.output.get_nodes(instance)) {
					if(use.is_usage()) { use.link_to(def); }
				}
			}
			
			if(statement instanceof CirReturnAssignStatement) {
				this.retr_wait_links(instance);
			}
		}
		else if(statement instanceof CirCallStatement) {
			this.call_wait_links(instance);
			this.call_parm_links(instance);
		}
	}
	/**
	 * create all the links through 
	 * @throws Exception
	 */
	private void create_links() throws Exception {
		for(Object context : this.input.get_contexts()) {
			for(CirInstance instance : this.input.get_instances(context)) {
				if(instance instanceof CirInstanceNode) {
					if(this.output.has_nodes((CirInstanceNode) instance))
						this.create_links((CirInstanceNode) instance);
				}
			}
		}
	}
	
}
