package com.jcsa.jcmutest.mutant.cir2mutant;

import java.util.List;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutations;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstGotoStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstLabeledStatement;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.stmt.CirGotoStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirLabelStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SGLRMutationParser extends CirMutationParser {

	@Override
	protected void parse(CirTree tree, AstMutation source, List<CirMutation> targets) throws Exception {
		AstGotoStatement src_stmt = (AstGotoStatement) source.get_location().get_parent();
		AstLabeledStatement trg_stmt = 
					(AstLabeledStatement) ((AstNode) source.get_parameter()).get_parent();
		CirStatement source_statement = (CirStatement) 
						this.get_cir_nodes(tree, src_stmt, CirGotoStatement.class).get(0);
		CirStatement target_statement = (CirStatement) 
						this.get_cir_nodes(tree, trg_stmt, CirLabelStatement.class).get(0);
		targets.add(CirMutations.set_goto_stmt(source_statement, target_statement));
	}

}
