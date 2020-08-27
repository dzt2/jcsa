package com.jcsa.jcmutest.mutant.sad2mutant.muta.infect;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadAssertion;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadExpression;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadFactory;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadParser;
import com.jcsa.jcmutest.mutant.sad2mutant.muta.SadInfection;
import com.jcsa.jcmutest.mutant.sad2mutant.muta.SadVertex;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * 	execute(statement, 1)
 * 	assert(switch_condition == case_condition)
 * 	trapping()
 * 	
 * 	@author yukimula
 *
 */
public class CTRPSadInfection extends SadInfection {
	
	/**
	 * @param statement
	 * @param loperand
	 * @param roperand
	 * @return assert#statement:(loperand == roperand)
	 * @throws Exception
	 */
	private SadAssertion equal_with(CirStatement statement, 
			CirExpression loperand, CirExpression roperand) throws Exception {
		SadExpression sad_loperand = (SadExpression) SadParser.cir_parse(loperand);
		SadExpression sad_roperand = (SadExpression) SadParser.cir_parse(roperand);
		SadExpression sad_condition = SadFactory.
					equal_with(CBasicTypeImpl.bool_type, sad_loperand, sad_roperand);
		return SadFactory.assert_condition(statement, sad_condition);
	}
	
	@Override
	protected void get_infect(CirTree tree, AstMutation mutation, SadVertex reach_node) throws Exception {
		CirStatement statement = this.find_beg_stmt(tree, mutation.get_location());
		CirExpression swit_expression = this.find_result(tree, mutation.get_location());
		CirExpression case_expression = this.find_result(tree, (AstNode) mutation.get_parameter());
		SadAssertion constraint = this.equal_with(statement, swit_expression, case_expression);
		SadAssertion state_error = SadFactory.trap_statement(statement);
		this.connect(reach_node, state_error, constraint);
	}
	
}
