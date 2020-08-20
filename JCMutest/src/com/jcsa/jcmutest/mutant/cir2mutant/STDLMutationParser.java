package com.jcsa.jcmutest.mutant.cir2mutant;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutations;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.irlang.AstCirPair;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirTagStatement;

public class STDLMutationParser extends CirMutationParser {

	@Override
	protected void parse(CirTree tree, AstMutation source, List<CirMutation> targets) throws Exception {
		Set<CirStatement> statements = new HashSet<CirStatement>();
		this.collect(tree, source.get_location(), statements);
		for(CirStatement statement : statements) {
			if(!(statement instanceof CirTagStatement)) {
				targets.add(CirMutations.delete_stmt(statement));
			}
		}
	}
	
	private void collect(CirTree tree, AstNode location, Set<CirStatement> statements) throws Exception {
		AstCirPair range = this.get_cir_range(tree, location);
		if(range != null && range.executional()) {
			statements.add(range.get_beg_statement());
			statements.add(range.get_end_statement());
		}
		for(int k = 0; k < location.number_of_children(); k++) {
			this.collect(tree, location.get_child(k), statements);
		}
	}
	
}
