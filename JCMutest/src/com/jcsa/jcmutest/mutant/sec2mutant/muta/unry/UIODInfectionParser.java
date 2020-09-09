package com.jcsa.jcmutest.mutant.sec2mutant.muta.unry;

import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecFactory;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.SecInfectionParser;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIncreAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;

public class UIODInfectionParser extends SecInfectionParser {

	@Override
	protected CirStatement get_statement() throws Exception {
		return this.get_end_statement(location);
	}
	
	private void delete_prev_inc() throws Exception { 
		CirAssignStatement statement = (CirAssignStatement) get_cir_nodes(
					this.location, CirIncreAssignStatement.class).get(0);
		CirExpression orig_expression = statement.get_rvalue();
		this.add_infection(
				SecFactory.assert_constraint(statement, Boolean.TRUE, true), 
				SecFactory.add_expression(statement, 
				orig_expression, COperator.arith_add, Integer.valueOf(-1)));
	}
	
	private void delete_prev_dec() throws Exception {
		CirAssignStatement statement = (CirAssignStatement) get_cir_nodes(
				this.location, CirIncreAssignStatement.class).get(0);
		CirExpression orig_expression = statement.get_rvalue();
		this.add_infection(
				SecFactory.assert_constraint(statement, Boolean.TRUE, true), 
				SecFactory.add_expression(statement, 
				orig_expression, COperator.arith_add, Integer.valueOf(1)));
	}
	
	private void delete_post_inc() throws Exception {
		CirAssignStatement statement = (CirAssignStatement) get_cir_nodes(
				this.location, CirIncreAssignStatement.class).get(0);
		CirExpression orig_expression = statement.get_rvalue();
		this.add_infection(
				SecFactory.assert_constraint(statement, Boolean.TRUE, true), 
				SecFactory.add_expression(statement, 
				orig_expression, COperator.arith_add, Integer.valueOf(-1)));
	}
	
	private void delete_post_dec() throws Exception {
		CirAssignStatement statement = (CirAssignStatement) get_cir_nodes(
				this.location, CirIncreAssignStatement.class).get(0);
		CirExpression orig_expression = statement.get_rvalue();
		this.add_infection(
				SecFactory.assert_constraint(statement, Boolean.TRUE, true), 
				SecFactory.add_expression(statement, 
				orig_expression, COperator.arith_add, Integer.valueOf(1)));
	}
	
	@Override
	protected void generate_infections() throws Exception {
		switch(this.mutation.get_operator()) {
		case delete_prev_inc:	this.delete_prev_inc(); break;
		case delete_prev_dec:	this.delete_prev_dec(); break;
		case delete_post_inc:	this.delete_post_inc(); break;
		case delete_post_dec:	this.delete_post_dec(); break;
		default: throw new IllegalArgumentException(this.mutation.toString());
		}
	}

}
