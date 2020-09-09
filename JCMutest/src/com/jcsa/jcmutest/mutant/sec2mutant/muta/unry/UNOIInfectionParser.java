package com.jcsa.jcmutest.mutant.sec2mutant.muta.unry;

import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecFactory;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecConstraint;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecDescription;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.SecInfectionParser;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.sym.SymFactory;

public class UNOIInfectionParser extends SecInfectionParser {

	@Override
	protected CirStatement get_statement() throws Exception {
		return this.get_cir_value(this.location).statement_of();
	}
	
	private void insert_arith_neg() throws Exception { 
		CirExpression expression = this.get_cir_value(this.location);
		SecConstraint constraint = SecFactory.assert_constraint(statement, 
				SymFactory.not_equals(expression, Integer.valueOf(0)), true);
		SecDescription init_error = SecFactory.
					uny_expression(statement, expression, COperator.negative);
		this.add_infection(constraint, init_error);
	}
	
	private void insert_bitws_rsv() throws Exception {
		CirExpression expression = this.get_cir_value(this.location);
		SecConstraint constraint = 
				SecFactory.assert_constraint(statement, Boolean.TRUE, true);
		SecDescription init_error = SecFactory.
				uny_expression(statement, expression, COperator.bit_not);
		this.add_infection(constraint, init_error);
	}
	
	private void insert_logic_not() throws Exception { 
		CirExpression expression = this.get_cir_value(this.location);
		SecConstraint constraint = 
				SecFactory.assert_constraint(statement, Boolean.TRUE, true);
		SecDescription init_error = SecFactory.
				uny_expression(statement, expression, COperator.logic_not);
		this.add_infection(constraint, init_error);
	}
	
	private void insert_abs_value() throws Exception {
		CirExpression expression = this.get_cir_value(this.location);
		SecConstraint constraint = SecFactory.assert_constraint(statement, 
				SymFactory.smaller_tn(expression, Integer.valueOf(0)), true);
		SecDescription init_error = SecFactory.
					uny_expression(statement, expression, COperator.negative);
		this.add_infection(constraint, init_error);
	}
	
	private void insert_nabs_value() throws Exception {
		CirExpression expression = this.get_cir_value(this.location);
		SecConstraint constraint = SecFactory.assert_constraint(statement, 
				SymFactory.greater_tn(expression, Integer.valueOf(0)), true);
		SecDescription init_error = SecFactory.
					uny_expression(statement, expression, COperator.negative);
		this.add_infection(constraint, init_error);
	}
	
	@Override
	protected void generate_infections() throws Exception {
		switch(this.mutation.get_operator()) {
		case insert_arith_neg:	this.insert_arith_neg(); 	break;
		case insert_bitws_rsv:	this.insert_bitws_rsv(); 	break;
		case insert_logic_not:	this.insert_logic_not(); 	break;
		case insert_abs_value:	this.insert_abs_value(); 	break;
		case insert_nabs_value:	this.insert_nabs_value(); 	break;
		default: throw new IllegalArgumentException(this.mutation.toString());
		}
	}

}
