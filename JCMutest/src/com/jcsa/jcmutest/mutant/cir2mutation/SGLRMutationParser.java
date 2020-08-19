package com.jcsa.jcmutest.mutant.cir2mutation;

import java.util.Collection;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutations;
import com.jcsa.jcparse.lang.astree.stmt.AstGotoStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstLabeledStatement;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.stmt.CirGotoStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirLabelStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SGLRMutationParser extends CirMutationParser {

	@Override
	public void parse(CirTree cir_tree, AstMutation source, Collection<CirMutation> targets) throws Exception {
		AstGotoStatement source_location = (AstGotoStatement) source.get_location();
		AstLabeledStatement target_location = (AstLabeledStatement) source.get_parameter();
		CirStatement goto_statement = (CirStatement) 
				this.get_cir_node(cir_tree, source_location, CirGotoStatement.class, 0);
		CirStatement label_statement = (CirStatement) this.
					get_cir_node(cir_tree, target_location, CirLabelStatement.class, 0);
		targets.add(CirMutations.SGLR(goto_statement, label_statement));
	}

}
