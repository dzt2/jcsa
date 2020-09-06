package com.jcsa.jcmutest.selang.muta.oprt;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.selang.lang.desc.SedDescription;
import com.jcsa.jcmutest.selang.lang.expr.SedExpression;
import com.jcsa.jcmutest.selang.util.SedFactory;
import com.jcsa.jcparse.lang.lexical.COperator;

public class SetArithAddProcess extends AbsSetOperatorProcess {

	@Override
	protected void arith_add() throws Exception {
		throw new UnsupportedOperationException(this.statement.generate_code(true));
	}

	@Override
	protected void arith_sub() throws Exception {
		/**
		 * roperand != 0 ==>
		 * [true]	trapping() 
		 * [false]	app_expr(expression, -, 2 * roperand) 
		 **/
		SedDescription constraint, init_error; SedExpression condition, operand;
		condition = SedFactory.not_equals(roperand, Integer.valueOf(0));
		constraint = SedFactory.condition_constraint(statement, condition, true);
		if(this.compare_or_mutate) {
			init_error = SedFactory.trp_statement(statement);
		}
		else {
			operand = SedFactory.arith_mul(expression.get_data_type(), roperand, Integer.valueOf(2));
			init_error = SedFactory.app_expression(statement, expression, COperator.arith_sub, operand);
		}
		this.infection.add_infection_pair(constraint, init_error);
	}

	@Override
	protected void arith_mul() throws Exception {
		/**
		 * 	if [true]:
		 * 	{
		 * 		{loperand != 0 or roperand != 0} ==> trapping()
		 * 	}
		 * 	else
		 * 	{
		 * 		{loperand == 0 and roperand != 0} or {loperand != 0 or roperand == 0} ==> mut_expr(expr, 0)
		 * 		{loperand != 0 and roperand != 0} ==> mut_expr(expr, loperand * roperand)
		 * 	}
		 * **/
		SedDescription constraint, init_error; 
		SedExpression condition, lcondition, rcondition;
		
		if(this.compare_or_mutate) {
			lcondition = SedFactory.not_equals(loperand, Integer.valueOf(0));
			rcondition = SedFactory.not_equals(roperand, Integer.valueOf(0));
			condition = SedFactory.logic_ior(lcondition, rcondition);
			constraint = SedFactory.condition_constraint(statement, condition, true);
			init_error = SedFactory.trp_statement(statement);
			this.infection.add_infection_pair(constraint, init_error);
		}
		else {
			lcondition = SedFactory.logic_and(
					SedFactory.equal_with(loperand, Integer.valueOf(0)), 
					SedFactory.not_equals(roperand, Integer.valueOf(0)));
			rcondition = SedFactory.logic_and(
					SedFactory.not_equals(loperand, Integer.valueOf(0)), 
					SedFactory.equal_with(roperand, Integer.valueOf(0)));
			List<SedDescription> constraints = new ArrayList<SedDescription>();
			constraints.add(SedFactory.condition_constraint(statement, lcondition, true));
			constraints.add(SedFactory.condition_constraint(statement, rcondition, true));
			constraint = SedFactory.disjunct(statement, constraints);
			init_error = SedFactory.mut_expression(statement,
					expression, (SedExpression) SedFactory.fetch(Integer.valueOf(0)));
			this.infection.add_infection_pair(constraint, init_error);
			
			condition = SedFactory.logic_and(
					SedFactory.not_equals(loperand, Integer.valueOf(0)), 
					SedFactory.not_equals(roperand, Integer.valueOf(0)));
			constraint = SedFactory.condition_constraint(statement, condition, true);
			init_error = SedFactory.mut_expression(statement, expression, 
					SedFactory.arith_mul(expression.get_data_type(), loperand, roperand));
			this.infection.add_infection_pair(constraint, init_error);
		}
	}
	
	@Override
	protected void arith_div() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void arith_mod() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void bitws_and() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void bitws_ior() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void bitws_xor() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void bitws_lsh() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void bitws_rsh() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void logic_and() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void logic_ior() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void greater_tn() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void greater_eq() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void smaller_tn() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void smaller_eq() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void equal_with() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void not_equals() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void assignment() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
