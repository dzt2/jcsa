package com.jcsa.jcmuta.mutant.error2mutation.infection;

import java.util.Map;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.error2mutation.StateError;
import com.jcsa.jcmuta.mutant.error2mutation.StateErrorGraph;
import com.jcsa.jcmuta.mutant.error2mutation.StateInfection;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symb.StateConstraints;

public class VTRPInfection extends StateInfection {
	
	@Override
	protected CirStatement get_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return this.get_beg_statement(cir_tree, this.get_location(mutation));
	}
	
	@Override
	protected void get_infections(CirTree cir_tree, AstMutation mutation, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
