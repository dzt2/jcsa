package com.jcsa.jcmutest.mutant.sec2mutant.muta.unry;

import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecFactory;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecConstraint;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecDescription;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.SecInfectionParser;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.sym.SymFactory;

public class UNODInfectionParser extends SecInfectionParser {

	@Override
	protected CirStatement get_statement() throws Exception {
		return this.get_cir_value(this.location).statement_of();
	}
	
	private void delete_arith_neg() throws Exception { 
		CirExpression expression = this.get_cir_value(this.location);
		SecConstraint constraint = SecFactory.assert_constraint(statement, 
				SymFactory.not_equals(expression, Integer.valueOf(0)), true);
		SecDescription init_error = SecFactory.
					uny_expression(statement, expression, COperator.negative);
		this.add_infection(constraint, init_error);
	}
	
	private void delete_bitws_rsv() throws Exception {
		CirExpression expression = this.get_cir_value(this.location);
		SecConstraint constraint = 
				SecFactory.assert_constraint(statement, Boolean.TRUE, true);
		SecDescription init_error = SecFactory.
				uny_expression(statement, expression, COperator.bit_not);
		this.add_infection(constraint, init_error);
	}
	
	private void delete_logic_not() throws Exception { 
		CirExpression expression = this.get_cir_value(this.location);
		SecConstraint constraint = 
				SecFactory.assert_constraint(statement, Boolean.TRUE, true);
		SecDescription init_error = SecFactory.
				uny_expression(statement, expression, COperator.logic_not);
		this.add_infection(constraint, init_error);
	}
	
	@Override
	protected void generate_infections() throws Exception {
		switch(this.mutation.get_operator()) {
		case delete_arith_neg:	this.delete_arith_neg(); 	break;
		case delete_bitws_rsv:	this.delete_bitws_rsv(); 	break;
		case delete_logic_not:	this.delete_logic_not(); 	break;
		default: throw new IllegalArgumentException(this.mutation.toString());
		}
	}

}
