package com.jcsa.jcmutest.selang.muta.oprt;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.selang.lang.desc.SedDescription;
import com.jcsa.jcmutest.selang.lang.expr.SedExpression;
import com.jcsa.jcmutest.selang.util.SedFactory;
import com.jcsa.jcparse.lang.lexical.COperator;

public class SetArithAddProcess extends AbsSetOperatorProcess {
	
	@Override
	protected void assignment() throws Exception {
		throw new UnsupportedOperationException(
				this.expression.generate_code(true));
	}
	
	@Override
	protected void arith_add() throws Exception {
		throw new UnsupportedOperationException(
				this.expression.generate_code(true));
	}

	@Override
	protected void arith_sub() throws Exception {
		/**
		 * 	if(true)
		 * 	{
		 * 		[y != 0] --> trap_stmt(statement)
		 * 	}
		 * 	else
		 * 	{
		 * 		[y != 0] --> app_expr(expression, -, 2 * y)
		 * 	}
		 */
		SedDescription constraint, init_error; SedExpression condition;
		condition = SedFactory.not_equals(this.roperand, Integer.valueOf(0));
		constraint = SedFactory.condition_constraint(statement, condition, true);
		
		if(this.compare_or_mutate) {
			init_error = SedFactory.trp_statement(statement);
		}
		else {
			init_error = SedFactory.app_expression(statement, expression, COperator.arith_sub, 
				SedFactory.arith_mul(expression.get_data_type(), Integer.valueOf(2), roperand));
		}
		this.infection.add_infection_pair(constraint, init_error);
	}

	@Override
	protected void arith_mul() throws Exception {
		/**
		 * 	if(true)
		 * 	{
		 * 		[true]	==> trap_stmt(stmt)
		 * 	}
		 * 	else
		 * 	{
		 * 		[x == 0 || y == 0]	--> mut_expr(0)
		 * 		[x == 1 || y == 1]	
		 * 	}
		 */
		SedDescription constraint, init_error; SedExpression condition;
		List<SedDescription> constraints = new ArrayList<SedDescription>();
		if(this.compare_or_mutate) {
			
		}
		else {
			
		}
	}

	@Override
	protected void arith_div() throws Exception {
		// TODO implement this method
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
	
}
