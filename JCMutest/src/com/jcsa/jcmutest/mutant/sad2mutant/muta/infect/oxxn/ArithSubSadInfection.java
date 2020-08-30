package com.jcsa.jcmutest.mutant.sad2mutant.muta.infect.oxxn;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadAssertion;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadExpression;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadFactory;
import com.jcsa.jcmutest.mutant.sad2mutant.muta.SadVertex;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;

public class ArithSubSadInfection extends OXXNSadInfection {

	/**
	 * [y != 0]
	 */
	protected void set_arith_add(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		SadExpression condition; SadAssertion constraint, state_error;
		List<SadAssertion> state_errors = new ArrayList<SadAssertion>();
		
		condition = this.not_equals(roperand, 0);
		constraint = SadFactory.assert_condition(statement, condition);
		state_errors.add(SadFactory.set_expression(statement, expression, 
				arith_add(expression.get_data_type(), loperand, roperand)));
		state_errors.add(SadFactory.add_operand(statement, expression, COperator.
				arith_add, this.arith_mul(expression.get_data_type(), 2, roperand)));
		state_error = SadFactory.conjunct(statement, state_errors);
		
		this.connect(source, state_error, constraint);
	}

	@Override
	protected void set_arith_sub(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void set_arith_mul(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void set_arith_div(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void set_arith_mod(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void set_bitws_and(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void set_bitws_ior(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void set_bitws_xor(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void set_bitws_lsh(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void set_bitws_rsh(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void set_logic_and(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void set_logic_ior(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void set_greater_tn(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void set_greater_eq(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void set_smaller_tn(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void set_smaller_eq(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void set_equal_with(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void set_not_equals(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void cmp_arith_add(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void cmp_arith_sub(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void cmp_arith_mul(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void cmp_arith_div(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void cmp_arith_mod(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void cmp_bitws_and(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void cmp_bitws_ior(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void cmp_bitws_xor(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void cmp_bitws_lsh(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void cmp_bitws_rsh(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void cmp_logic_and(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void cmp_logic_ior(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void cmp_greater_tn(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void cmp_greater_eq(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void cmp_smaller_tn(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void cmp_smaller_eq(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void cmp_equal_with(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void cmp_not_equals(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
