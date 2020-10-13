package com.jcsa.jcmutest.mutant.cir2mutant.muta.oxxx;

import java.util.Map;

import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirStateError;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.CirMutationParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.oprt.CirSetOperatorParsers;
import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirSaveAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class OLXNCirMutationParser extends CirMutationParser {

	@Override
	protected CirStatement get_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return this.get_end_statement(cir_tree, mutation.get_location());
	}

	@Override
	protected void generate_infections(CirMutations mutations, CirTree cir_tree, CirStatement statement,
			AstMutation mutation, Map<CirStateError, CirConstraint> infections) throws Exception {
		CirExpression expression = this.get_cir_expression(cir_tree, mutation.get_location());
		
		CirAssignStatement save1 = (CirAssignStatement) this.get_cir_node(
				cir_tree, mutation.get_location(), CirSaveAssignStatement.class);
		CirAssignStatement save2 = (CirAssignStatement) this.get_cir_nodes(
				cir_tree, mutation.get_location(), CirSaveAssignStatement.class).get(1);
		
		CirExpression loperand = save1.get_rvalue(), roperand = save2.get_rvalue();
		CirSetOperatorParsers.generate_infections(mutation, 
				statement, expression, loperand, roperand, mutations, infections);
	}

}
