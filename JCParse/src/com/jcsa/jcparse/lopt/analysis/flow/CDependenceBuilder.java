package com.jcsa.jcparse.lopt.analysis.flow;

import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlowType;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lopt.CirInstance;
import com.jcsa.jcparse.lopt.ingraph.CirInstanceEdge;
import com.jcsa.jcparse.lopt.ingraph.CirInstanceGraph;
import com.jcsa.jcparse.lopt.ingraph.CirInstanceNode;

/**
 * To build up the program dependence graph based on program flow graph
 * @author yukimula
 *
 */
class CDependenceBuilder {
	
	/* attributes and constructor */
	/** the program flow graph used for dependence analysis **/
	private CirInstanceGraph input;
	/** the program dependence graph to be constructed **/
	private CDependenceGraph output;
	/** constructor **/
	private CDependenceBuilder() {}
	/** singleton of the program dependence builder **/
	protected static final CDependenceBuilder builder = new CDependenceBuilder();
	
	/* building methods */
	/**
	 * construct the program dependence with respect to the input flow graph
	 * @param input
	 * @param output
	 * @throws Exception
	 */
	protected void build(CirInstanceGraph input, CDependenceGraph output) throws Exception {
		this.set_input(input, output);
		this.create_nodes();
		this.create_cedges();
		this.create_dedges();
	}
	/**
	 * set the program flow graph and dependence graph for construction
	 * @param input
	 * @param output
	 * @throws Exception
	 */
	private void set_input(CirInstanceGraph input, CDependenceGraph output) throws Exception {
		if(input == null)
			throw new IllegalArgumentException("Invalid input: null");
		else if(output == null)
			throw new IllegalArgumentException("Invalid output null");
		else { this.input = input; this.output = output; }
	}
	/**
	 * create all the nodes for the statement instance within the dependence graph
	 * @throws Exception
	 */
	private void create_nodes() throws Exception {
		for(Object context : this.input.get_contexts()) {
			for(CirInstance instance : input.get_instances(context)) {
				if(instance instanceof CirInstanceNode) {
					this.output.new_node((CirInstanceNode) instance);
				}
			}
		}
	}
	/**
	 * create the control dependence from the source node to the target of a conditional statement.
	 * @param dominance_graph
	 * @param source
	 * @throws Exception
	 */
	private void create_cedge(CDominanceGraph dominance_graph, CDependenceNode source) throws Exception {
		CirInstanceNode source_instance = source.get_instance();
		if(dominance_graph.has_node(source_instance)) {
			CDominanceNode dominance_node = dominance_graph.get_node(source_instance);
			
			while(dominance_node != null) {
				/* find the next valid node directly dominating this one */
				if(dominance_node.get_in_degree() > 0) {
					
					/* determine whether the node is a control flow dominating */
					if(dominance_node.is_flow()) {
						CirInstanceEdge edge = (CirInstanceEdge) dominance_node.get_instance();
						CirInstanceNode target_instance = edge.get_source();
						
						/* true control dependence */
						if(edge.get_type() == CirExecutionFlowType.true_flow) {
							CDependenceNode target = this.output.get_node(target_instance);
							source.control_depend(target, true);  return;
						}
						/* false control dependence */
						else if(edge.get_type() == CirExecutionFlowType.fals_flow) {
							CDependenceNode target = this.output.get_node(target_instance);
							source.control_depend(target, false); return;
						}
					}
					
					/* find the next valid directly dominating node */
					dominance_node = dominance_node.get_in_node(0);
				}
				/* no more node exists over the dominance path */
				else dominance_node = null;
			}
			
		}
	}
	/**
	 * create the control dependence relationships between the statement nodes in dependence graph.
	 * @throws Exception
	 */
	private void create_cedges() throws Exception {
		CDominanceGraph dominance_graph = 
				CDominanceGraph.forward_dominance_graph(input);
		for(CDependenceNode source : this.output.get_nodes()) 
			this.create_cedge(dominance_graph, source);
	}
	/**
	 * create the data dependence on the specified node (from definition to usage that decides its value
	 * and from usage to the definition of which last value is determined).
	 * @param define_use_graph
	 * @param source
	 * @throws Exception
	 */
	private void create_dedge(CDefineUseGraph define_use_graph, CDependenceNode source) throws Exception {
		CirInstanceNode source_instance = source.get_instance();
		CirStatement source_statement = source_instance.get_execution().get_statement();
		
		if(define_use_graph.has_nodes(source_instance) && 
				source_statement instanceof CirAssignStatement) {
			CirExpression def_expr = ((CirAssignStatement) source_statement).get_lvalue();
			CDefineUseNode def_node = define_use_graph.get_node(source_instance, def_expr);
			
			for(CDefineUseEdge def_use_edge : def_node.get_ou_edges()) {
				CirInstanceNode target_instance = def_use_edge.get_target().get_instance();
				CDependenceNode target = this.output.get_node(target_instance);
				target.data_depend(source, false, def_use_edge.get_target().get_expression(), def_expr);
			}
			
			for(CDefineUseEdge use_def_edge : def_node.get_in_edges()) {
				CirInstanceNode target_istance = use_def_edge.get_source().get_instance();
				CDependenceNode target = this.output.get_node(target_istance);
				source.data_depend(target, true, def_expr, use_def_edge.get_source().get_expression());
			}
		}
	}
	/**
	 * create the data dependence over the entire program statements
	 * @throws Exception
	 */
	private void create_dedges() throws Exception {
		CDefineUseGraph define_use_graph = CDefineUseGraph.define_use_graph(input);
		for(CDependenceNode source : this.output.get_nodes()) 
			this.create_dedge(define_use_graph, source);
	}
	
}
