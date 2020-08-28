package com.jcsa.jcmutest.mutant.sad2mutant.muta.infect.refs;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadAssertion;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadExpression;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadFactory;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadParser;
import com.jcsa.jcmutest.mutant.sad2mutant.muta.SadVertex;
import com.jcsa.jcmutest.mutant.sad2mutant.muta.infect.SadInfection;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class VTRPSadInfection extends SadInfection {
	
	private SadExpression get_constraint(AstMutation mutation, CirExpression expression) throws Exception {
		SadExpression loperand = (SadExpression) SadParser.cir_parse(expression);
		switch(mutation.get_operator()) {
		case trap_on_pos:	
			return SadFactory.greater_tn(CBasicTypeImpl.
					bool_type, loperand, SadFactory.constant(0));
		case trap_on_neg:
			return SadFactory.smaller_tn(CBasicTypeImpl.
					bool_type, loperand, SadFactory.constant(0));
		case trap_on_zro:
			return SadFactory.equal_with(CBasicTypeImpl.
					bool_type, loperand, SadFactory.constant(0));
		case trap_on_dif:
			SadExpression roperand = (SadExpression) 
				SadParser.cir_parse((CirNode) mutation.get_parameter());
			return SadFactory.not_equals(CBasicTypeImpl.bool_type, loperand, roperand);
		default: throw new IllegalArgumentException("Invalid: " + mutation.toString());
		}
	}
	
	@Override
	protected void get_infect(CirTree tree, AstMutation mutation, SadVertex reach_node) throws Exception {
		AstNode location = mutation.get_location();
		CirStatement statement = this.find_beg_stmt(tree, location);
		CirExpression expression = this.find_result(tree, location);
		
		SadExpression condition = this.get_constraint(mutation, expression);
		SadAssertion constraint = SadFactory.assert_condition(statement, condition);
		SadAssertion state_error = SadFactory.trap_statement(statement);
		this.connect(reach_node, state_error, constraint);
	}

}
