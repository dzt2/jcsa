package com.jcsa.jcmutest.mutant.sec2mutant.mod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecFactory;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecDescription;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.SecInfection;
import com.jcsa.jcparse.flwa.depend.CDependEdge;
import com.jcsa.jcparse.flwa.depend.CDependGraph;
import com.jcsa.jcparse.flwa.depend.CDependNode;
import com.jcsa.jcparse.flwa.depend.CDependPredicate;
import com.jcsa.jcparse.flwa.depend.CDependType;
import com.jcsa.jcparse.flwa.graph.CirInstanceNode;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlowType;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SecPathBuilder {
	
	protected static Map<CirInstanceNode, List<CirExecutionFlow>> dominance_paths(
			CDependGraph graph, CirStatement statement) throws Exception {
		CirExecution execution = statement.
				get_tree().get_localizer().get_execution(statement);
		Iterable<CirInstanceNode> instances = 
				graph.get_program_graph().get_instances_of(execution);
		Map<CirInstanceNode, List<CirExecutionFlow>> paths = 
				new HashMap<CirInstanceNode, List<CirExecutionFlow>>();
		
		for(CirInstanceNode instance : instances) {
			if(graph.has_node(instance)) {
				List<CirExecutionFlow> path = new ArrayList<CirExecutionFlow>();
				CDependNode node = graph.get_node(instance);
				while(node != null) {
					CDependNode next_node = null;
					for(CDependEdge edge : node.get_ou_edges()) {
						if(edge.get_type() == CDependType.predicate_depend) {
							CDependPredicate predicate = (CDependPredicate) edge.get_element();
							CirExecution if_execution = edge.get_target().get_execution();
							if(predicate.get_predicate_value()) {
								for(CirExecutionFlow flow : if_execution.get_ou_flows()) {
									if(flow.get_type() == CirExecutionFlowType.true_flow) {
										path.add(flow); next_node = edge.get_target(); break;
									}
								}
							}
							else {
								for(CirExecutionFlow flow : if_execution.get_ou_flows()) {
									if(flow.get_type() == CirExecutionFlowType.fals_flow) {
										path.add(flow); next_node = edge.get_target(); break;
									}
								}
							}
						}
						else if(edge.get_type() == CDependType.stmt_call_depend) {
							CirExecution call_execution = edge.get_target().get_execution();
							for(CirExecutionFlow flow : call_execution.get_ou_flows()) {
								if(flow.get_type() == CirExecutionFlowType.call_flow) {
									path.add(flow); next_node = edge.get_target(); break;
								}
							}
						}
						else if(edge.get_type() == CDependType.wait_retr_depend) {
							CirExecution wait_execution = edge.get_source().get_execution();
							for(CirExecutionFlow flow : wait_execution.get_in_flows()) {
								if(flow.get_type() == CirExecutionFlowType.retr_flow) {
									path.add(flow); next_node = edge.get_target(); break;
								}
							}
						}
					}
					node = next_node;
				}
				for(int k = 0; k < path.size() / 2; k++) {
					CirExecutionFlow prev = path.get(k);
					CirExecutionFlow next = path.get(path.size() - 1 - k);
					path.set(k, next); path.set(path.size() - 1 - k, prev);
				}
				paths.put(instance, path);
			}
		}
		return paths;
	}
	
	protected static SecStateNode build_path(CDependGraph dgraph, SecInfection infection, SecStateGraph sgraph) throws Exception {
		CirStatement statement = infection.get_statement();
		Map<CirInstanceNode, List<CirExecutionFlow>> paths = dominance_paths(dgraph, statement);
		SecStateNode target = sgraph.get_node(SecFactory.execute_constraint(statement, 1));
		
		for(List<CirExecutionFlow> path : paths.values()) {
			SecStateNode next_node = target, prev_node;
			for(CirExecutionFlow flow : path) {
				SecDescription constraint; 
				CirStatement source_statement = flow.get_source().get_statement();
				switch(flow.get_type()) {
				case true_flow:
				{
					if(source_statement instanceof CirIfStatement) {
						constraint = SecFactory.assert_constraint(source_statement, 
								((CirIfStatement) source_statement).get_condition(), true);
					}
					else if(source_statement instanceof CirCaseStatement) {
						constraint = SecFactory.assert_constraint(source_statement, 
								((CirCaseStatement) source_statement).get_condition(), true);
					}
					else {
						throw new IllegalArgumentException(source_statement.generate_code(true));
					}
					break;
				}
				case fals_flow:
				{
					if(source_statement instanceof CirIfStatement) {
						constraint = SecFactory.assert_constraint(source_statement, 
								((CirIfStatement) source_statement).get_condition(), false);
					}
					else if(source_statement instanceof CirCaseStatement) {
						constraint = SecFactory.assert_constraint(source_statement, 
								((CirCaseStatement) source_statement).get_condition(), false);
					}
					else {
						throw new IllegalArgumentException(source_statement.generate_code(true));
					}
					break;
				}
				case call_flow:
				{
					constraint = SecFactory.assert_constraint(source_statement, Boolean.TRUE, true);
					break;
				}
				case retr_flow:
				{
					source_statement = flow.get_target().get_statement();
					constraint = SecFactory.assert_constraint(source_statement, Boolean.TRUE, true);
					break;
				}
				default: throw new IllegalArgumentException(flow.toString());
				}
				prev_node = sgraph.get_node(SecFactory.execute_constraint(source_statement, 1));
				prev_node.connect(next_node, constraint);
				next_node = prev_node;
			}
		}
		
		return target;
	}
	
}
