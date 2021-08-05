package com.jcsa.jcparse.flwa.relation;

import com.jcsa.jcparse.flwa.CirInstance;
import com.jcsa.jcparse.flwa.defuse.CDefineUseEdge;
import com.jcsa.jcparse.flwa.defuse.CDefineUseGraph;
import com.jcsa.jcparse.flwa.defuse.CDefineUseNode;
import com.jcsa.jcparse.flwa.dominate.CDominanceGraph;
import com.jcsa.jcparse.flwa.dominate.CDominanceNode;
import com.jcsa.jcparse.flwa.graph.CirInstanceEdge;
import com.jcsa.jcparse.flwa.graph.CirInstanceGraph;
import com.jcsa.jcparse.flwa.graph.CirInstanceNode;
import com.jcsa.jcparse.lang.irlang.expr.CirAddressExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirCastExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirComputeExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirDeferExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirFieldExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirInitializerBody;
import com.jcsa.jcparse.lang.irlang.expr.CirReferExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirValueExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirWaitExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlowType;
import com.jcsa.jcparse.lang.irlang.stmt.CirArgumentList;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCallStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirGotoStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirInitAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirReturnAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirWaitAssignStatement;

/**
 * To build up the relational graph based on dominance and use-define relationships
 *
 * @author yukimula
 *
 */
public class CRelationBuilder {

	/* attributes */
	/** input used to generate the relational graph **/
	private CirInstanceGraph input;
	/** the relational graph being built up by the builder **/
	private CRelationGraph output;

	/* constructor */
	/** singleton constructor **/
	private CRelationBuilder() { }
	/** singleton **/
	private static final CRelationBuilder builder = new CRelationBuilder();

	/* building methods */
	/**
	 * open the builder by setting its input and output
	 * @param input
	 * @param output
	 * @throws Exception
	 */
	private void open(CirInstanceGraph input, CRelationGraph output) throws Exception {
		if(input == null)
			throw new IllegalArgumentException("Invalid input: null");
		else if(output == null)
			throw new IllegalArgumentException("Invalid output: null");
		else { this.input = input; this.output = output; }
	}
	/**
	 * build up the relational graph from C-like intermediate representation
	 * @throws Exception
	 */
	private void build() throws Exception {
		this.create_nodes();
		this.create_aedges();
		this.create_cedges();
		this.create_dedges();
	}
	/**
	 * close the builder by removing its input and output
	 */
	private void close() { this.input = null; this.output = null; }
	/**
	 * build up the relational graph based on the program in C-like intermediate representation
	 * @param input
	 * @param output
	 * @throws Exception
	 */
	protected static void build(CirInstanceGraph input, CRelationGraph output) throws Exception {
		builder.open(input, output);
		builder.build();
		builder.close();
	}

	/* building methods */
	/**
	 * create the nodes within the expression being parsed in C-like intermediate representation
	 * @param instance
	 * @param expression
	 * @throws Exception
	 */
	private void create_nodes(CirInstanceNode instance, CirExpression expression) throws Exception {
		/** 1. all the reference nodes need to be created **/
		if(expression instanceof CirReferExpression) {
			this.output.new_node(instance, expression);
		}

		/** 2. recursive translation based on syntax **/
		if(expression instanceof CirDeferExpression) {
			this.create_nodes(instance, ((CirDeferExpression) expression).get_address());
		}
		else if(expression instanceof CirFieldExpression) {
			this.create_nodes(instance, ((CirFieldExpression) expression).get_body());
		}
		else if(expression instanceof CirAddressExpression) {
			this.create_nodes(instance, ((CirAddressExpression) expression).get_operand());
		}
		else if(expression instanceof CirCastExpression) {
			this.create_nodes(instance, ((CirCastExpression) expression).get_operand());
		}
		else if(expression instanceof CirWaitExpression) {
			this.output.new_node(instance, ((CirWaitExpression) expression).get_function());
			this.create_nodes(instance, ((CirWaitExpression) expression).get_function());
		}
		else if(expression instanceof CirComputeExpression) {
			for(int k = 0; k < expression.number_of_children(); k++) {
				this.create_nodes(instance, ((CirComputeExpression) expression).get_operand(k));
			}
		}
		else if(expression instanceof CirInitializerBody) {
			for(int k = 0; k < expression.number_of_children(); k++) {
				this.create_nodes(instance, ((CirInitializerBody) expression).get_element(k));
			}
		}
	}
	/**
	 * create the nodes within the statement of the instance provided
	 * @param instance
	 * @throws Exception
	 */
	private void create_nodes(CirInstanceNode instance) throws Exception {
		CirStatement statement = instance.get_execution().get_statement();

		if(statement instanceof CirAssignStatement) {
			this.output.new_node(instance, statement);
			this.output.new_node(instance, ((CirAssignStatement) statement).get_lvalue());
			this.output.new_node(instance, ((CirAssignStatement) statement).get_rvalue());

			this.create_nodes(instance, ((CirAssignStatement) statement).get_lvalue());
			this.create_nodes(instance, ((CirAssignStatement) statement).get_rvalue());
		}
		else if(statement instanceof CirIfStatement) {
			this.output.new_node(instance, statement);
			this.output.new_node(instance, ((CirIfStatement) statement).get_condition());

			this.create_nodes(instance, ((CirIfStatement) statement).get_condition());
		}
		else if(statement instanceof CirCaseStatement) {
			this.output.new_node(instance, statement);
			this.output.new_node(instance, ((CirCaseStatement) statement).get_condition());

			this.create_nodes(instance, ((CirCaseStatement) statement).get_condition());
		}
		else if(statement instanceof CirCallStatement) {
			this.output.new_node(instance, statement);
			this.output.new_node(instance, ((CirCallStatement) statement).get_function());

			CirArgumentList arguments = ((CirCallStatement) statement).get_arguments();
			for(int k = 0; k < arguments.number_of_arguments(); k++) {
				this.output.new_node(instance, arguments.get_argument(k));
			}

			this.create_nodes(instance, ((CirCallStatement) statement).get_function());
			for(int k = 0; k < arguments.number_of_arguments(); k++) {
				this.create_nodes(instance, arguments.get_argument(k));
			}
		}
		else if(statement instanceof CirGotoStatement) {
			this.output.new_node(instance, statement);	// TODO create for goto maybe error!
		}
	}
	/**
	 * create all the nodes in the relational graph
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
	/**
	 * create the used_in, function edges within the expression node
	 * @param instance
	 * @param parent
	 * @param expression
	 * @throws Exception
	 */
	private void create_aedges(CirInstanceNode instance,
			CRelationNode parent, CirExpression expression) throws Exception {
		if(expression instanceof CirReferExpression && expression != parent.get_cir_source()) {
			CRelationNode child = this.output.get_node(instance, expression);

			/* determine the type of the connection on AST path from parent to child */
			if(parent.get_cir_source() instanceof CirValueExpression) {
				this.output.connect(CRelationEdgeType.value_include, parent, child);
			}
			else if(parent.get_cir_source() instanceof CirReferExpression) {
				this.output.connect(CRelationEdgeType.refer_include, parent, child);
			}
			else throw new IllegalArgumentException("Invalid source: " + parent.get_cir_source());

			parent = child;	/* for recursive translation in the children layer */
		}

		if(expression instanceof CirDeferExpression) {
			this.create_aedges(instance, parent, ((CirDeferExpression) expression).get_address());
		}
		else if(expression instanceof CirFieldExpression) {
			this.create_aedges(instance, parent, ((CirFieldExpression) expression).get_body());
		}
		else if(expression instanceof CirAddressExpression) {
			this.create_aedges(instance, parent, ((CirAddressExpression) expression).get_operand());
		}
		else if(expression instanceof CirCastExpression) {
			this.create_aedges(instance, parent, ((CirCastExpression) expression).get_operand());
		}
		else if(expression instanceof CirWaitExpression) {
			CRelationNode target = this.output.get_node(
					instance, ((CirWaitExpression) expression).get_function());
			this.output.connect(CRelationEdgeType.function, parent, target);

			this.create_aedges(instance, parent, ((CirWaitExpression) expression).get_function());
		}
		else if(expression instanceof CirComputeExpression) {
			for(int k = 0; k < expression.number_of_children(); k++) {
				this.create_aedges(instance, parent, ((CirComputeExpression) expression).get_operand(k));
			}
		}
		else if(expression instanceof CirInitializerBody) {
			for(int k = 0; k < expression.number_of_children(); k++) {
				this.create_aedges(instance, parent, ((CirInitializerBody) expression).get_element(k));
			}
		}
	}
	/**
	 * create the AST parent-child direct edges in relational graph for nodes in instance,
	 * including:<br>
	 * condition, function, left_value, right_value, used_in
	 * @param instance
	 * @throws Exception
	 */
	private void create_aedges(CirInstanceNode instance) throws Exception {
		CirStatement statement = instance.get_execution().get_statement();

		if(statement instanceof CirAssignStatement) {
			CRelationNode source = this.output.get_node(instance, statement);

			CRelationNode target1 = this.output.get_node(instance,
					((CirAssignStatement) statement).get_lvalue());
			CRelationNode target2 = this.output.get_node(instance,
					((CirAssignStatement) statement).get_rvalue());

			this.output.connect(CRelationEdgeType.left_value, source, target1);
			this.output.connect(CRelationEdgeType.right_value, source, target2);

			this.create_aedges(instance, target1, ((CirAssignStatement) statement).get_lvalue());
			this.create_aedges(instance, target2, ((CirAssignStatement) statement).get_rvalue());
		}
		else if(statement instanceof CirIfStatement) {
			CRelationNode source = this.output.get_node(instance, statement);
			CRelationNode target = this.output.get_node(instance,
					((CirIfStatement) statement).get_condition());
			this.output.connect(CRelationEdgeType.condition, source, target);

			this.create_aedges(instance, target, ((CirIfStatement) statement).get_condition());
		}
		else if(statement instanceof CirCaseStatement) {
			CRelationNode source = this.output.get_node(instance, statement);
			CRelationNode target = this.output.get_node(instance,
					((CirCaseStatement) statement).get_condition());
			this.output.connect(CRelationEdgeType.condition, source, target);

			this.create_aedges(instance, target, ((CirCaseStatement) statement).get_condition());
		}
		else if(statement instanceof CirCallStatement) {
			CRelationNode parent = this.output.get_node(instance, statement);
			CRelationNode source = this.output.get_node(instance, ((CirCallStatement) statement).get_function());

			this.output.connect(CRelationEdgeType.function, parent, source);
			this.create_aedges(instance, source, ((CirCallStatement) statement).get_function());

			CirArgumentList arguments = ((CirCallStatement) statement).get_arguments();
			for(int k = 0; k < arguments.number_of_arguments(); k++) {
				CRelationNode target = this.output.get_node(instance, arguments.get_argument(k));
				this.output.connect(CRelationEdgeType.argument, source, target);
				this.create_aedges(instance, target, arguments.get_argument(k));
			}
		}
	}
	/**
	 * create the interprocedural relationships, including:<br>
	 * wait_point, retr_point, argument, pass_in, pass_ou<br>
	 * @param instance
	 * @throws Exception
	 */
	private void create_iedges(CirInstanceNode instance) throws Exception {
		/**
		 * call_statement ==> wait_statement [wait_point]
		 * call_statement ==> init_assignment [pass_point]
		 * call_statement.argument ==> init_assign.rvalue [pass_in]
		 */
		if(instance.get_execution().get_statement() instanceof CirCallStatement) {
			/** get the execution, instance and statement of waiting point **/
			CirInstanceNode call_instance = instance;
			CirExecution call_execution = call_instance.get_execution();
			CirExecution wait_execution = call_execution.
					get_graph().get_execution(call_execution.get_id() + 1);
			CirInstanceNode wait_instance = this.input.get_instance(
					call_instance.get_context(), wait_execution);

			/** call_statement ==> wait_statement [wait_point] **/
			CRelationNode call_node = this.output.get_node(call_instance, call_execution.get_statement());
			CRelationNode wait_node = this.output.get_node(wait_instance, wait_execution.get_statement());
			this.output.connect(CRelationEdgeType.wait_point, call_node, wait_node);

			/** call_statement.argument ==> init_assign.rvalue [pass_in]  **/
			if(call_instance.get_ou_edge(0).get_type() == CirExecutionFlowType.call_flow) {
				CirInstanceNode next_instance = call_instance.get_ou_edge(0).get_target();
				CirCallStatement call_statement = (CirCallStatement) call_execution.get_statement();
				CirArgumentList arguments = call_statement.get_arguments();

				for(int k = 0; k < arguments.number_of_arguments(); k++) {
					next_instance = next_instance.get_ou_edge(0).get_target();

					if(next_instance.get_execution().get_statement() instanceof CirInitAssignStatement) {
						CirInitAssignStatement init_assign =
								(CirInitAssignStatement) next_instance.get_execution().get_statement();
						CRelationNode argument = this.output.get_node(call_instance, arguments.get_argument(k));
						CRelationNode parameter = this.output.get_node(next_instance, init_assign.get_rvalue());
						this.output.connect(CRelationEdgeType.pass_in, argument, parameter);

						CRelationNode argument_stmt = this.output.get_node(call_instance,
								call_instance.get_execution().get_statement());
						CRelationNode parameter_stmt = this.output.get_node(next_instance,
								next_instance.get_execution().get_statement());
						this.output.connect(CRelationEdgeType.pass_point, argument_stmt, parameter_stmt);
					}
					else { break; }
				}
			}
		}
		/**
		 * wait_statement.rvalue.function ==> call_statement.argument [argument]
		 */
		else if(instance.get_execution().get_statement() instanceof CirWaitAssignStatement) {
			/** get the statement, execution and instance of calling point **/
			CirInstanceNode wait_instance = instance;
			CirExecution wait_execution = wait_instance.get_execution();
			CirExecution call_execution = wait_execution.
					get_graph().get_execution(wait_execution.get_id() - 1);
			CirInstanceNode call_instance = this.input.
					get_instance(wait_instance.get_context(), call_execution);
			CirWaitAssignStatement wait_statement = (CirWaitAssignStatement) wait_execution.get_statement();
			CirCallStatement call_statement = (CirCallStatement) call_execution.get_statement();

			/** wait_statement.rvalue.function ==> call_statement.argument [argument] **/
			CirWaitExpression rvalue = (CirWaitExpression) wait_statement.get_rvalue();
			CRelationNode function = this.output.get_node(wait_instance, rvalue.get_function());
			CirArgumentList arguments = call_statement.get_arguments();
			for(int k = 0; k < arguments.number_of_arguments(); k++) {
				CRelationNode argument = this.output.get_node(call_instance, arguments.get_argument(k));
				this.output.connect(CRelationEdgeType.argument, function, argument);
			}
		}
		/**
		 * retr_statement ==> wait_statement [retr_point]
		 * retr_statement.lvalue ==> wait_statement.rvalue [pass_ou]
		 */
		else if(instance.get_execution().get_statement() instanceof CirReturnAssignStatement) {
			CirInstanceNode retr_instance = instance;
			CirInstanceNode exit_instance = retr_instance.get_ou_edge(0).get_target();
			exit_instance = exit_instance.get_ou_edge(0).get_target();
			if(exit_instance.get_ou_degree() > 0) {
				for(int k = 0; k < exit_instance.get_ou_degree(); k++) {
					/** get the next waiting statement after exit of callee function **/
					CirInstanceNode wait_instance = exit_instance.get_ou_edge(k).get_target();

					/** retr_statement ==> wait_statement [retr_point] **/
					CirReturnAssignStatement retr_statement =
							(CirReturnAssignStatement) retr_instance.get_execution().get_statement();
					CirWaitAssignStatement wait_statement =
							(CirWaitAssignStatement) wait_instance.get_execution().get_statement();
					CRelationNode retr_node = this.output.get_node(retr_instance, retr_statement);
					CRelationNode wait_node = this.output.get_node(wait_instance, wait_statement);
					this.output.connect(CRelationEdgeType.retr_point, retr_node, wait_node);

					/** retr_statement.lvalue ==> wait_statement.rvalue [pass_ou] **/
					CRelationNode source = this.output.get_node(retr_instance, retr_statement.get_lvalue());
					CRelationNode target = this.output.get_node(wait_instance, wait_statement.get_rvalue());
					this.output.connect(CRelationEdgeType.pass_ou, source, target);
				}
			}
		}
	}
	/**
	 * create the control dependence relationship based on the c-edge
	 * @param dominance_graph
	 * @param instance
	 * @throws Exception
	 */
	private void create_cedges(CDominanceGraph dominance_graph, CirInstanceNode instance) throws Exception {
		if(dominance_graph.has_node(instance)) {
			if(this.output.has_nodes(instance)) {
				CDominanceNode dominance_node = dominance_graph.get_node(instance);
				CRelationNode target = this.output.get_node(instance, instance.get_execution().get_statement());

				while(dominance_node != null) {
					if(dominance_node.get_in_degree() > 0) {
						CDominanceNode next_node = dominance_node.get_in_node(0);
						if(next_node.get_instance() instanceof CirInstanceEdge) {
							CirInstanceEdge edge = (CirInstanceEdge) next_node.get_instance();
							if(edge.get_type() == CirExecutionFlowType.true_flow) {
								CirStatement statement = edge.get_source().get_execution().get_statement();
								CirExpression condition;
								if(statement instanceof CirIfStatement) {
									condition = ((CirIfStatement) statement).get_condition();
								}
								else if(statement instanceof CirCaseStatement) {
									condition = ((CirCaseStatement) statement).get_condition();
								}
								else throw new IllegalArgumentException(
										"Invalid statement: " + statement.getClass().getSimpleName());

								CRelationNode source = this.output.get_node(edge.get_source(), condition);
								this.output.connect(CRelationEdgeType.transit_true, source, target);
								break;
							}
							else if(edge.get_type() == CirExecutionFlowType.fals_flow) {
								CirStatement statement = edge.get_source().get_execution().get_statement();
								CirExpression condition;
								if(statement instanceof CirIfStatement) {
									condition = ((CirIfStatement) statement).get_condition();
								}
								else if(statement instanceof CirCaseStatement) {
									condition = ((CirCaseStatement) statement).get_condition();
								}
								else throw new IllegalArgumentException(
										"Invalid statement: " + statement.getClass().getSimpleName());

								CRelationNode source = this.output.get_node(edge.get_source(), condition);
								this.output.connect(CRelationEdgeType.transit_false, source, target);
								break;
							}
						}
						dominance_node = next_node;
					}
					else dominance_node = null;
				}
			}
		}
	}
	/**
	 * create the control dependence relationship over the entire program
	 * @throws Exception
	 */
	private void create_cedges() throws Exception {
		CDominanceGraph dominance_graph = CDominanceGraph.forward_dominance_graph(input);
		for(Object context : this.input.get_contexts()) {
			for(CirInstance instance : this.input.get_instances(context)) {
				if(instance instanceof CirInstanceNode)
					this.create_cedges(dominance_graph, (CirInstanceNode) instance);
			}
		}
	}
	/**
	 * create the data dependence relationship within the instance of statement
	 * @param def_use_graph
	 * @param instance
	 * @throws Exception
	 */
	private void create_dedges(CDefineUseGraph def_use_graph, CirInstanceNode instance) throws Exception {
		if(instance.get_execution().get_statement() instanceof CirAssignStatement) {
			if(def_use_graph.has_nodes(instance) && this.output.has_nodes(instance)) {
				CirAssignStatement statement = (CirAssignStatement) instance.get_execution().get_statement();
				CDefineUseNode def_node = def_use_graph.get_node(instance, statement.get_lvalue());
				CRelationNode source = this.output.get_node(instance, statement.get_lvalue());

				for(CDefineUseEdge def_use_edge : def_node.get_ou_edges()) {
					CDefineUseNode use_node = def_use_edge.get_target();
					CRelationNode target = this.output.get_node(
							use_node.get_instance(), use_node.get_expression());
					this.output.connect(CRelationEdgeType.define_use, source, target);
				}
			}

			CirAssignStatement statement = (CirAssignStatement) instance.get_execution().get_statement();
			CRelationNode source = this.output.get_node(instance, statement.get_rvalue());
			CRelationNode target = this.output.get_node(instance, statement.get_lvalue());
			this.output.connect(CRelationEdgeType.use_define, source, target);
		}
	}
	/**
	 * create the data dependence relationship over the entire program
	 * @throws Exception
	 */
	private void create_dedges() throws Exception {
		CDefineUseGraph def_use_graph = CDefineUseGraph.define_use_graph(input);
		for(Object context : this.input.get_contexts()) {
			for(CirInstance instance : this.input.get_instances(context)) {
				if(instance instanceof CirInstanceNode)
					this.create_dedges(def_use_graph, (CirInstanceNode) instance);
			}
		}
	}
	/**
	 * create the syntax relationships between nodes
	 * @throws Exception
	 */
	private void create_aedges() throws Exception {
		for(Object context : this.input.get_contexts()) {
			for(CirInstance instance : this.input.get_instances(context)) {
				if(instance instanceof CirInstanceNode) {
					this.create_aedges((CirInstanceNode) instance);
					this.create_iedges((CirInstanceNode) instance);
				}
			}
		}
	}

}
