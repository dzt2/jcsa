package com.jcsa.jcmuta.mutant.ast2mutation;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstRelationExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

public class ORANMutationGenerator extends AstMutationGenerator {
	
	private static final List<COperator> replaces = new LinkedList<COperator>();
	static {
		replaces.add(COperator.arith_add);
		replaces.add(COperator.arith_sub);
		replaces.add(COperator.arith_mul);
		replaces.add(COperator.arith_div);
		replaces.add(COperator.arith_mod);
	}
	
	@Override
	protected void collect_locations(AstNode location, Collection<AstNode> locations) throws Exception {
		if(location instanceof AstRelationExpression)
			locations.add(location);
	}

	@Override
	protected void generate_mutations(AstNode location, Collection<AstMutation> mutations) throws Exception {
		AstRelationExpression expression = (AstRelationExpression) location;
		for(COperator replace : replaces) {
			if(expression.get_operator().get_operator() != replace) {
				mutations.add(AstMutation.ORAN(expression, replace));
			}
		}
	}

}
