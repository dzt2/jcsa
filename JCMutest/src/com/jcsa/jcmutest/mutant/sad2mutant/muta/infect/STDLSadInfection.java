package com.jcsa.jcmutest.mutant.sad2mutant.muta.infect;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadFactory;
import com.jcsa.jcmutest.mutant.sad2mutant.muta.SadInfection;
import com.jcsa.jcmutest.mutant.sad2mutant.muta.SadVertex;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.irlang.AstCirPair;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirTagStatement;

public class STDLSadInfection extends SadInfection {
	
	private void collect_statements(CirTree tree, AstNode location,
			Collection<CirStatement> statements) throws Exception {
		AstCirPair range = this.get_cir_range(tree, location);
		if(range.executional()) {
			statements.add(range.get_beg_statement());
			statements.add(range.get_end_statement());
		}
		for(int k = 0; k < location.number_of_children(); k++) {
			this.collect_statements(tree, location.get_child(k), statements);
		}
	}
	
	@Override
	protected void get_infect(CirTree tree, AstMutation mutation, SadVertex reach_node) throws Exception {
		Set<CirStatement> statements = new HashSet<CirStatement>();
		this.collect_statements(tree, mutation.get_location(), statements);
		for(CirStatement statement : statements) {
			if(!(statement instanceof CirTagStatement)) {
				this.connect(reach_node, SadFactory.del_statement(statement));
			}
		}
	}

}
