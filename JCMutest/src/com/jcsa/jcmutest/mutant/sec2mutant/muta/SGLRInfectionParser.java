package com.jcsa.jcmutest.mutant.sec2mutant.muta;

import com.jcsa.jcmutest.mutant.sec2mutant.apis.SecInfectionParser;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecFactory;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecConstraint;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstGotoStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstLabeledStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirGotoStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirLabelStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SGLRInfectionParser extends SecInfectionParser {

	@Override
	protected CirStatement get_location() throws Exception {
		AstNode location = this.mutation.get_location();
		return (CirStatement) this.cir_tree.get_localizer().
				get_cir_nodes(location, CirGotoStatement.class).get(0);
	}

	@Override
	protected void generate_infections() throws Exception {
		AstGotoStatement source_location = 
				(AstGotoStatement) this.mutation.get_location().get_parent();
		AstLabeledStatement target_location = (AstLabeledStatement) 
					((AstNode) this.mutation.get_parameter()).get_parent();
		
		CirStatement source = (CirStatement) this.cir_tree.get_localizer().
				get_cir_nodes(source_location, CirGotoStatement.class).get(0);
		CirStatement target = (CirStatement) this.cir_tree.get_localizer().
				get_cir_nodes(target_location, CirLabelStatement.class).get(0);
		
		SecConstraint constraint = 
				SecFactory.assert_constraint(this.statement, Boolean.TRUE, true);
		this.add_infection(constraint, SecFactory.set_statement(source, target));
	}

}
