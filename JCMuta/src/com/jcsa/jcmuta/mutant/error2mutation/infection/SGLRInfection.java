package com.jcsa.jcmuta.mutant.error2mutation.infection;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.error2mutation.PathConditions;
import com.jcsa.jcmuta.mutant.error2mutation.StateError;
import com.jcsa.jcmuta.mutant.error2mutation.StateErrorGraph;
import com.jcsa.jcmuta.mutant.error2mutation.StateInfection;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.stmt.CirGotoStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symb.StateConstraints;

public class SGLRInfection extends StateInfection {
	
	private CirGotoStatement get_source_of(CirTree cir_tree, AstMutation mutation) throws Exception {
		return (CirGotoStatement) cir_tree.get_cir_nodes(mutation.get_location().get_parent(), CirGotoStatement.class).get(0);
	}
	
	private CirGotoStatement get_target_of(CirTree cir_tree, AstMutation mutation) throws Exception {
		AstNode location = (AstNode) mutation.get_parameter();
		return (CirGotoStatement) cir_tree.get_cir_nodes(location.get_parent(), CirGotoStatement.class).get(0);
	}
	
	@Override
	protected CirStatement get_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return this.get_source_of(cir_tree, mutation);
	}

	@Override
	protected void get_infections(CirTree cir_tree, AstMutation mutation, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirStatement source = this.get_source_of(cir_tree, mutation);
		CirStatement target = this.get_target_of(cir_tree, mutation);
		CirStatement end = cir_tree.get_function_call_graph().
				get_function(source).get_flow_graph().get_exit().get_statement();
		
		Set<CirExecutionFlow> origin_path = 
				PathConditions.might_be_path(PathConditions.paths_of(source, end));
		Set<CirExecutionFlow> mutate_path = 
				PathConditions.might_be_path(PathConditions.paths_of(target, end));
		
		Set<CirStatement> non_execute_set = new HashSet<CirStatement>();
		for(CirExecutionFlow flow : origin_path) {
			if(!mutate_path.contains(flow)) {
				CirStatement s1 = flow.get_source().get_statement();
				CirStatement s2 = flow.get_target().get_statement();
				non_execute_set.add(s1); non_execute_set.add(s2);
			}
		}
		
		Set<CirStatement> execute_set = new HashSet<CirStatement>();
		for(CirExecutionFlow flow : mutate_path) {
			if(!origin_path.contains(flow)) {
				CirStatement s1 = flow.get_source().get_statement();
				CirStatement s2 = flow.get_target().get_statement();
				execute_set.add(s1); execute_set.add(s2);
			}
		}
		
		StateConstraints constraints = new StateConstraints(true);
		
		for(CirStatement statement : execute_set) {
			output.put(graph.get_error_set().execute(statement), constraints);
		}
		for(CirStatement statement : non_execute_set) {
			output.put(graph.get_error_set().not_execute(statement), constraints);
		}
	}

}
