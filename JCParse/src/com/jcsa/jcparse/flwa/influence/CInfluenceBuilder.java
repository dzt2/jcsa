package com.jcsa.jcparse.flwa.influence;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import com.jcsa.jcparse.flwa.graph.CirInstanceGraph;
import com.jcsa.jcparse.flwa.graph.CirInstanceNode;
import com.jcsa.jcparse.flwa.relation.CRelationEdge;
import com.jcsa.jcparse.flwa.relation.CRelationGraph;
import com.jcsa.jcparse.flwa.relation.CRelationNode;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirField;
import com.jcsa.jcparse.lang.irlang.expr.CirFieldExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirWaitExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.stmt.CirCallStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirGotoStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirLabel;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirTagStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirWaitAssignStatement;

/**
 * Used to build up the program influence graph.
 * 
 * @author yukimula
 *
 */
public class CInfluenceBuilder {
	/* constructor */
	/** the program flow graph used as input to build up influence **/
	private CirInstanceGraph input;
	/** the relational graph used to construct the program influence **/
	private CRelationGraph relations;
	/** the influence graph to be constructed from the flow graph **/
	private CInfluenceGraph output;
	/** singleton constructor **/
	private CInfluenceBuilder() { }
	/** singleton **/
	private static final CInfluenceBuilder builder = new CInfluenceBuilder();
	
	/* building methods */
	/**
	 * open the builder by setting its input and output
	 * @param input
	 * @param output
	 * @throws Exception
	 */
	private void open(CirInstanceGraph input, CInfluenceGraph output) throws Exception {
		if(input == null)
			throw new IllegalArgumentException("Invalid input: null");
		else if(output == null)
			throw new IllegalArgumentException("Invalid output: null");
		else { 
			this.input = input; this.output = output; 
		}
	}
	/**
	 * build up the influence graph from C-like intermediate representation
	 * @throws Exception
	 */
	private void build() throws Exception {
		this.relations = CRelationGraph.graph(input);
		this.create_nodes(); this.create_edges();
	}
	/**
	 * close the builder by removing its input and output
	 */
	private void close() { this.input = null; this.output = null; this.relations = null; }
	/**
	 * build up the influence graph based on the program in C-like intermediate representation
	 * @param input
	 * @param output
	 * @throws Exception
	 */
	protected static void build(CirInstanceGraph input, CInfluenceGraph output) throws Exception {
		builder.open(input, output);
		builder.build();
		builder.close();
	}
	
	/* parsing methods */
	private Queue<CirNode> cir_queue = new LinkedList<CirNode>();
	/**
	 * create all the nodes for the program elements in input code.
	 * @throws Exception
	 */
	private void create_nodes() throws Exception {
		for(CirInstanceNode instance : this.relations.get_instances()) {
			CirStatement statement = instance.get_execution().get_statement();
			cir_queue.add(statement);
			
			while(!cir_queue.isEmpty()) {
				CirNode cir_source = cir_queue.poll();
				
				if(cir_source instanceof CirStatement
					|| cir_source instanceof CirExpression
					|| cir_source instanceof CirLabel
					|| cir_source instanceof CirField) {
					this.output.new_node(instance, cir_source);
				}
				
				for(CirNode child : cir_source.get_children()) {
					cir_queue.add(child);
				}
			}
		}
	}
	/**
	 * translate the relation to the respect influence between nodes
	 * @param relation_edge
	 * @throws Exception
	 */
	private void create_edges(CRelationEdge relation_edge) throws Exception {
		CRelationNode source_node = relation_edge.get_source();
		CRelationNode target_node = relation_edge.get_target();
		CInfluenceNode source = this.output.get_node(source_node.get_instance(), source_node.get_cir_source());
		CInfluenceNode target = this.output.get_node(target_node.get_instance(), target_node.get_cir_source());
		
		switch(relation_edge.get_type()) {
		/** condition[stmt-->expr] |--> execute_condition **/
		case condition: 
			this.output.connect(CInfluenceEdgeType.execute_condition, source, target);
			break;
		
		/** lvalue[stmt-->refer] |--> none **/
		case left_value: break;
		
		/** rvalue[stmt-->expr] |--> execute_right_value **/
		case right_value: 
			this.output.connect(CInfluenceEdgeType.execute_right_value, source, target);
			break;
		
		/**
		 * 1. call_stmt --> function |--> execute_function
		 * 2. wait_expr <-- function |--> fun_wait_assign
		 * **/
		case function:
		{
			CirNode cir_source = source.get_cir_source();
			if(cir_source instanceof CirCallStatement) {
				this.output.connect(CInfluenceEdgeType.execute_function, source, target);
			}
			else if(cir_source instanceof CirWaitExpression) {
				this.output.connect(CInfluenceEdgeType.fun_wait_assign, target, source);
			}
			else throw new IllegalArgumentException(cir_source.getClass().getSimpleName());
		}
		break;
		
		/**
		 * 1. call_stmt.function --> call_stmt.argument |--> call_by_argument[call_stmt.fun, call_stmt.arg]
		 * 2. wait_expr.function <-- call_stmt.argument |--> argument_to_wait[call_stmt.arg, wait_expr.fun]
		 * **/
		case argument:
		{
			CirNode cir_source = source.get_cir_source();
			if(cir_source.get_parent() instanceof CirCallStatement) {
				this.output.connect(CInfluenceEdgeType.call_by_argument, source, target);
			}
			else if(cir_source.get_parent() instanceof CirWaitExpression) { 
				this.output.connect(CInfluenceEdgeType.argument_to_wait, target, source);
			}
			else throw new IllegalArgumentException(cir_source.get_parent().generate_code(true));
		}
		break;
		
		/** refer_include[refr<--expr] |--> operand_used_in[child, parent] **/
		case refer_include: 
		{
			CirNode child = target.get_cir_source();
			CirNode parent = source.get_cir_source();
			while(child != parent) {
				CInfluenceNode x = this.output.get_node(target.get_instance(), child);
				CInfluenceNode y = this.output.get_node(target.get_instance(), child.get_parent());
				this.output.connect(CInfluenceEdgeType.operand_used_in, x, y);
				child = child.get_parent();
			}
		}
		break;
		
		/** value_include[expr<--refr] |--> operand_used_in[child, parent] **/
		case value_include:
		{
			CirNode child = target.get_cir_source();
			CirNode parent = source.get_cir_source();
			while(child != parent) {
				CInfluenceNode x = this.output.get_node(target.get_instance(), child);
				CInfluenceNode y = this.output.get_node(target.get_instance(), child.get_parent());
				this.output.connect(CInfluenceEdgeType.operand_used_in, x, y);
				child = child.get_parent();
			}
		}
		break;
		
		/** pass_point --> ignored **/
		case pass_point: /*this.output.connect(CInfluenceEdgeType.exec_p, source, target);*/ break;
		case wait_point: /*this.output.connect(CInfluenceEdgeType.exec_w, source, target);*/ break;
		case retr_point: /*this.output.connect(CInfluenceEdgeType.exec_r, source, target);*/ break;
		
		case define_use: this.output.connect(CInfluenceEdgeType.def_use_assign, source, target); break;
		case use_define: this.output.connect(CInfluenceEdgeType.use_def_assign, source, target); break;
		case pass_in:	 this.output.connect(CInfluenceEdgeType.arg_param_assign, source, target); break;
		case pass_ou:	 this.output.connect(CInfluenceEdgeType.retr_wait_assign, source, target); break;
		
		case transit_true:	this.output.connect(CInfluenceEdgeType.execute_when_true, source, target); break;
		case transit_false:	this.output.connect(CInfluenceEdgeType.execute_when_false, source, target); break;
		
		/** invalid case **/
		default: throw new IllegalArgumentException("Unable to translate: " + relation_edge.get_type());
		}
	}
	/**
	 * generate external influence edges within:
	 * field --> field_expression	operand_used_in
	 * goto_statement --> label		execute_to_label
	 * @param relation_node
	 * @throws Exception
	 */
	private void create_edges(CRelationNode relation_node) throws Exception {
		CirInstanceNode instance = relation_node.get_instance();
		CirNode cir_source = relation_node.get_cir_source();
		
		/** goto_statement --> label: execute_to_label **/
		if(cir_source instanceof CirGotoStatement) {
			CInfluenceNode source = this.output.get_node(instance, cir_source);
			CInfluenceNode target = this.output.get_node(instance, ((CirGotoStatement) cir_source).get_label());
			this.output.connect(CInfluenceEdgeType.execute_to_label, source, target);
			
			Object context = instance.get_context();
			Queue<CirExecution> exe_queue = new LinkedList<CirExecution>();
			Set<CirExecution> visited_set = new HashSet<CirExecution>();
			exe_queue.add(instance.get_execution());
			while(!exe_queue.isEmpty()) {
				CirExecution execution = exe_queue.poll();
				boolean is_continue = true;
				CirStatement statement = execution.get_statement();
				
				if(statement instanceof CirTagStatement) {
					/* skip the useless statement */
				}
				else if(statement instanceof CirIfStatement
						|| statement instanceof CirCaseStatement
						|| statement instanceof CirCallStatement
						|| statement instanceof CirWaitAssignStatement) {
					is_continue = false;
				}
				
				/* append the statement into consideration */
				if(this.input.has_instance(context, execution)) {
					CirInstanceNode instance_node = this.input.get_instance(context, execution);
					if(this.output.has_node(instance_node, statement)) {
						CInfluenceNode next_target = this.output.get_node(instance_node, statement);
						this.output.connect(CInfluenceEdgeType.label_to_statement, target, next_target);
					}
				}
				
				if(is_continue) {
					for(CirExecutionFlow flow : execution.get_ou_flows()) {
						CirExecution next = flow.get_target();
						if(!visited_set.contains(next)) {
							exe_queue.add(next); visited_set.add(next);
						}
					}
				}
			}	// end of while
		}
		/** field --> field_expression	operand_used_in **/
		else if(cir_source instanceof CirFieldExpression) {
			CInfluenceNode source = this.output.get_node(instance, ((CirFieldExpression) cir_source).get_field());
			CInfluenceNode target = this.output.get_node(instance, cir_source);
			this.output.connect(CInfluenceEdgeType.operand_used_in, source, target);
		}
	}
	/**
	 * translate the relations in relational graph to the influence in influence graph
	 * @throws Exception
	 */
	private void create_edges() throws Exception {
		for(CirInstanceNode instance : this.relations.get_instances()) {
			for(CRelationNode relation_node : this.relations.get_nodes(instance)) {
				this.create_edges(relation_node);
				for(CRelationEdge relation_edge : relation_node.get_ou_edges()) {
					this.create_edges(relation_edge);
				}
			}
		}
	}
	
}
