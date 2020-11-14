package com.jcsa.jcmutest.mutant.cir2mutant.muta.refs;

import java.util.Map;

import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirStateError;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.CirMutationParser;
import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;

public class VBRPCirMutationParser extends CirMutationParser {
	
	@Override
	protected CirStatement get_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return this.get_cir_expression(cir_tree, mutation.get_location()).statement_of();
	}
	
	@Override
	protected void generate_infections(CirMutations mutations, CirTree cir_tree, CirStatement statement,
			AstMutation mutation, Map<CirStateError, CirConstraint> infections) throws Exception {
		CirExpression expression = this.get_cir_expression(cir_tree, mutation.get_location());
		SymExpression muta_value; CirConstraint constraint;
		switch(mutation.get_operator()) {
		case set_true: 	
		{
			constraint = mutations.expression_constraint(statement, expression, false);
			muta_value = SymFactory.sym_constant(Boolean.TRUE);
			infections.put(mutations.expr_error(expression, muta_value), constraint);
			break;
		}
		case set_false:	
		{
			constraint = mutations.expression_constraint(statement, expression, true);
			muta_value = SymFactory.sym_constant(Boolean.FALSE);
			infections.put(mutations.expr_error(expression, muta_value), constraint);
			break;
		}
		default: throw new IllegalArgumentException("Invalid operator: " + mutation.get_operator());
		}
		
	}
	
}
