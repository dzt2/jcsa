package com.jcsa.jcmutest.mutant.sym2mutant.muta.oxxo;

import java.util.Map;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymConstraint;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymStateError;
import com.jcsa.jcmutest.mutant.sym2mutant.muta.CirMutationParser;
import com.jcsa.jcmutest.mutant.sym2mutant.muta.oprt.CirSetOperatorParsers;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirBinAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class OEXACirMutationParser extends CirMutationParser {

	@Override
	protected CirStatement get_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return (CirStatement) this.get_cir_node(cir_tree, mutation.get_location(), CirBinAssignStatement.class);
	}

	@Override
	protected void generate_infections(CirTree cir_tree, CirStatement statement,
			AstMutation mutation, Map<SymStateError, SymConstraint> infections) throws Exception {
		CirAssignStatement assign_stmt = (CirAssignStatement) this.get_cir_node(
				cir_tree, mutation.get_location(), CirBinAssignStatement.class);
		CirExpression expression = assign_stmt.get_rvalue();
		CirExpression loperand = assign_stmt.get_lvalue();
		CirExpression roperand = assign_stmt.get_rvalue();
		CirSetOperatorParsers.generate_infections(mutation, statement, 
				expression, loperand, roperand, infections);
	}

}
