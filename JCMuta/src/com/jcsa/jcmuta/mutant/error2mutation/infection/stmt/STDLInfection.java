package com.jcsa.jcmuta.mutant.error2mutation.infection.stmt;

import java.util.Map;
import java.util.Set;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.error2mutation.PathConditions;
import com.jcsa.jcmuta.mutant.error2mutation.StateError;
import com.jcsa.jcmuta.mutant.error2mutation.StateErrorGraph;
import com.jcsa.jcmuta.mutant.error2mutation.StateEvaluation;
import com.jcsa.jcmuta.mutant.error2mutation.StateInfection;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirTagStatement;
import com.jcsa.jcparse.lang.symb.StateConstraints;

public class STDLInfection extends StateInfection {

	@Override
	protected CirStatement get_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return PathConditions.find_cir_location(cir_tree, this.get_location(mutation));
	}

	@Override
	protected void get_infections(CirTree cir_tree, AstMutation mutation, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		Set<CirStatement> statements = 
				this.collect_statements_in(cir_tree, this.get_location(mutation));
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		
		for(CirStatement statement : statements) {
			if(!(statement instanceof CirTagStatement)) {
				output.put(graph.get_error_set().not_execute(statement), constraints);
			}
		}
	}

}
