package com.jcsa.jcparse.lopt.models.depend;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlowType;
import com.jcsa.jcparse.lang.irlang.stmt.CirArgumentList;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCallStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirInitAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirReturnAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirTagStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirWaitAssignStatement;
import com.jcsa.jcparse.lopt.CirInstance;
import com.jcsa.jcparse.lopt.ingraph.CirInstanceEdge;
import com.jcsa.jcparse.lopt.ingraph.CirInstanceGraph;
import com.jcsa.jcparse.lopt.ingraph.CirInstanceNode;
import com.jcsa.jcparse.lopt.models.defuse.CDefineUseEdge;
import com.jcsa.jcparse.lopt.models.defuse.CDefineUseGraph;
import com.jcsa.jcparse.lopt.models.defuse.CDefineUseNode;
import com.jcsa.jcparse.lopt.models.dominate.CDominanceGraph;
import com.jcsa.jcparse.lopt.models.dominate.CDominanceNode;

public class CDependGraph {
	
	private CirInstanceGraph program_graph;
	private Map<CirInstanceNode, CDependNode> nodes;
	private CDependGraph(CirInstanceGraph program_graph) throws Exception {
		if(program_graph == null)
			throw new IllegalArgumentException("Invalid program_graph: null");
		else {
			this.program_graph = program_graph;
			this.nodes = new HashMap<CirInstanceNode, CDependNode>();
			
			this.build_nodes();
			this.build_pedges();
			this.build_dedges();
		}
	}
	
	public CirInstanceGraph get_program_graph() { return this.program_graph; }
	public int size() { return this.nodes.size(); }
	public Iterable<CDependNode> get_nodes() { return nodes.values(); }
	public boolean has_node(CirInstanceNode instance) { return nodes.containsKey(instance); }
	public CDependNode get_node(CirInstanceNode instance) throws Exception {
		return this.nodes.get(instance);
	}
	
	private void build_nodes() throws Exception {
		for(Object context : this.program_graph.get_contexts()) {
			for(CirInstance instance : program_graph.get_instances(context)) {
				if(instance instanceof CirInstanceNode) {
					CirExecution execution = ((CirInstanceNode) instance).get_execution();
					if(execution != null) {
						if(!(execution.get_statement() instanceof CirTagStatement)) {
							this.new_node((CirInstanceNode) instance);
						}
					}
				}
			}
		}
	}
	private CDependNode new_node(CirInstanceNode instance) throws Exception {
		if(instance == null)
			throw new IllegalArgumentException("Invalid instance");
		else {
			if(!this.nodes.containsKey(instance)) {
				CDependNode node = new CDependNode(this, instance);
				this.nodes.put(instance, node);
			}
			return this.nodes.get(instance);
		}
	}
	
	private void build_pedge(CDominanceGraph dominance_graph, CDependNode source) throws Exception {
		if(dominance_graph.has_node(source.get_instance())) {
			CDominanceNode dominance_node = dominance_graph.get_node(source.get_instance());
			while(dominance_node != null) {
				if(dominance_node.get_in_degree() > 0) {
					CDominanceNode next_node = dominance_node.get_in_node(0);
					if(next_node.get_instance() instanceof CirInstanceEdge) {
						CirInstanceEdge edge = (CirInstanceEdge) next_node.get_instance();
						if(edge.get_type() == CirExecutionFlowType.true_flow) {
							CDependNode target = this.get_node(edge.get_source());
							source.predicate_depend(target, true); break;
						}
						else if(edge.get_type() == CirExecutionFlowType.fals_flow) {
							CDependNode target = this.get_node(edge.get_source());
							source.predicate_depend(target, false); break;
						}
					}
					dominance_node = next_node;
				}
				else dominance_node = null;
			}
		}
	}
	private void build_cedge(CDependNode source) throws Exception {
		if(source.get_statement() instanceof CirWaitAssignStatement) {
			CirExecution wait_execution = source.get_execution();
			CirExecution call_execution;
			switch(wait_execution.get_in_flow(0).get_type()) {
			case skip_flow:
				call_execution = wait_execution.get_in_flow(0).get_source(); break;
			case retr_flow:
				call_execution = wait_execution.get_graph().get_function().get_graph().
						get_calling(wait_execution.get_in_flow(0)).get_call_execution();
				break;
			default: throw new IllegalArgumentException("Invalid input flow");
			}
			
			CirInstanceNode target_instance = this.program_graph.get_instance(
					source.get_instance().get_context(), call_execution);
			CDependNode target = this.get_node(target_instance);
			
			source.wait_call_depend(target);
		}
	}
	private void build_pedges() throws Exception {
		CDominanceGraph dominance_graph = CDominanceGraph.forward_dominance_graph(program_graph);
		for(CDependNode source : this.nodes.values()) {
			this.build_pedge(dominance_graph, source); 
			this.build_cedge(source);
		}
	}
	
	private void build_dedge(CDefineUseGraph def_use_graph, CDependNode target) throws Exception {
		CirStatement target_statement = target.get_statement();
		
		if(target_statement instanceof CirReturnAssignStatement) {
			CDefineUseNode define_node = def_use_graph.get_node(target.get_instance(), 
					((CirReturnAssignStatement) target_statement).get_lvalue());
			
			for(CDefineUseEdge def_use_edge : define_node.get_ou_edges()) {
				CDefineUseNode use_node = def_use_edge.get_target();
				CDependNode source = this.get_node(use_node.get_instance());
				// CirWaitAssignStatement source_statement = (CirWaitAssignStatement) source.get_statement();
				source.wait_retr_depend(target);
			}
		}
		else if(target_statement instanceof CirAssignStatement) {
			CDefineUseNode define_node = def_use_graph.get_node(target.get_instance(), 
					((CirAssignStatement) target_statement).get_lvalue());
			
			Set<CDependNode> sources = new HashSet<CDependNode>();
			for(CDefineUseEdge def_use_edge : define_node.get_ou_edges()) {
				CDefineUseNode use_node = def_use_edge.get_target();
				sources.add(this.get_node(use_node.get_instance()));
			}
			
			for(CDependNode source : sources) {
				source.use_define_depend(target);
			}
		}
		else if(target_statement instanceof CirCallStatement) {
			CirArgumentList arguments = ((CirCallStatement) target_statement).get_arguments();
			
			CirInstanceNode call_point = target.get_instance();
			CirInstanceNode cursor = call_point.get_ou_edge(0).get_target();
			for(int k = 0; k < arguments.number_of_arguments(); k++) {
				CirExpression argument = arguments.get_argument(k);
				cursor = cursor.get_ou_edge(0).get_target();
				
				if(cursor.get_execution().get_statement() instanceof CirInitAssignStatement) {
					CDependNode source = this.get_node(cursor);
					source.param_arg_depend(target, argument);
				}
				else { break; }
			}
		}
		
	}
	private void build_dedges() throws Exception {
		CDefineUseGraph def_use_graph = CDefineUseGraph.define_use_graph(program_graph);
		for(CDependNode target : this.nodes.values()) {
			this.build_dedge(def_use_graph, target);
		}
	}
	
	public static CDependGraph graph(CirInstanceGraph program_graph) throws Exception {
		return new CDependGraph(program_graph);
	}
	
}
