package com.jcsa.jcmutest.mutant.sec2mutant.muta.unry;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecFactory;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecConstraint;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecDescription;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.SecInfectionParser;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIncreAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirSaveAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;

public class UIORInfectionParser extends SecInfectionParser {

	@Override
	protected CirStatement get_statement() throws Exception {
		return this.get_beg_statement(this.location);
	}
	
	private void prev_inc_to_prev_dec() throws Exception {
		SecConstraint constraint = SecFactory.assert_constraint(statement, Boolean.TRUE, true);
		CirAssignStatement inc_statement = 
				(CirAssignStatement) get_cir_nodes(location, CirIncreAssignStatement.class).get(0);
		CirExpression inc_expression = inc_statement.get_rvalue();
		SecDescription init_error = SecFactory.
				add_expression(statement, inc_expression, COperator.arith_add, Integer.valueOf(-2));
		this.add_infection(constraint, init_error);
	}
	private void prev_inc_to_post_inc() throws Exception {
		SecConstraint constraint = SecFactory.assert_constraint(statement, Boolean.TRUE, true);
		CirExpression use_expression = this.get_cir_value(this.location);
		if(use_expression.statement_of() != null) {
			SecDescription init_error = SecFactory.add_expression(
					statement, use_expression, COperator.arith_add, Integer.valueOf(-1));
			this.add_infection(constraint, init_error);
		}
	}
	private void prev_inc_to_post_dec() throws Exception {
		SecConstraint constraint = 
				SecFactory.assert_constraint(statement, Boolean.TRUE, true);
		List<SecDescription> init_errors = new ArrayList<SecDescription>();
		
		CirAssignStatement inc_statement = 
				(CirAssignStatement) get_cir_nodes(location, CirIncreAssignStatement.class).get(0);
		CirExpression inc_expression = inc_statement.get_rvalue();
		init_errors.add(SecFactory.add_expression(statement, inc_expression, COperator.arith_add, Integer.valueOf(-2)));
		
		CirExpression use_expression = this.get_cir_value(this.location);
		if(use_expression.statement_of() != null) {
			init_errors.add(SecFactory.add_expression(statement, use_expression, COperator.arith_add, Integer.valueOf(-1)));
		}
		
		if(init_errors.size() == 1) {
			this.add_infection(constraint, init_errors.get(0));
		}
		else {
			this.add_infection(constraint, SecFactory.conjunct(statement, init_errors));
		}
	}
	
	private void prev_dec_to_prev_inc() throws Exception {
		SecConstraint constraint = SecFactory.assert_constraint(statement, Boolean.TRUE, true);
		CirAssignStatement inc_statement = 
				(CirAssignStatement) get_cir_nodes(location, CirIncreAssignStatement.class).get(0);
		CirExpression inc_expression = inc_statement.get_rvalue();
		SecDescription init_error = SecFactory.
				add_expression(statement, inc_expression, COperator.arith_add, Integer.valueOf(2));
		this.add_infection(constraint, init_error);
	}
	private void prev_dec_to_post_dec() throws Exception {
		SecConstraint constraint = SecFactory.assert_constraint(statement, Boolean.TRUE, true);
		CirExpression use_expression = this.get_cir_value(this.location);
		if(use_expression.statement_of() != null) {
			SecDescription init_error = SecFactory.add_expression(
					statement, use_expression, COperator.arith_add, Integer.valueOf(1));
			this.add_infection(constraint, init_error);
		}
	}
	private void prev_dec_to_post_inc() throws Exception {
		SecConstraint constraint = 
				SecFactory.assert_constraint(statement, Boolean.TRUE, true);
		List<SecDescription> init_errors = new ArrayList<SecDescription>();
		
		CirAssignStatement inc_statement = 
				(CirAssignStatement) get_cir_nodes(location, CirIncreAssignStatement.class).get(0);
		CirExpression inc_expression = inc_statement.get_rvalue();
		init_errors.add(SecFactory.add_expression(statement, inc_expression, COperator.arith_add, Integer.valueOf(2)));
		
		CirExpression use_expression = this.get_cir_value(this.location);
		if(use_expression.statement_of() != null) {
			init_errors.add(SecFactory.add_expression(statement, use_expression, COperator.arith_add, Integer.valueOf(1)));
		}
		
		if(init_errors.size() == 1) {
			this.add_infection(constraint, init_errors.get(0));
		}
		else {
			this.add_infection(constraint, SecFactory.conjunct(statement, init_errors));
		}
	}
	
	private void post_inc_to_post_dec() throws Exception {
		SecConstraint constraint = SecFactory.assert_constraint(statement, Boolean.TRUE, true);
		CirAssignStatement inc_statement = 
				(CirAssignStatement) get_cir_nodes(location, CirIncreAssignStatement.class).get(0);
		CirExpression inc_expression = inc_statement.get_rvalue();
		SecDescription init_error = SecFactory.
				add_expression(statement, inc_expression, COperator.arith_add, Integer.valueOf(-2));
		this.add_infection(constraint, init_error);
	}
	private void post_inc_to_prev_inc() throws Exception {
		CirAssignStatement sav_statement = (CirAssignStatement) this.
				get_cir_nodes(location, CirSaveAssignStatement.class).get(0);
		SecConstraint constraint = SecFactory.assert_constraint(statement, Boolean.TRUE, true);
		this.add_infection(constraint, SecFactory.add_expression(
				statement, sav_statement.get_rvalue(), COperator.arith_add, Integer.valueOf(1)));
	}
	private void post_inc_to_prev_dec() throws Exception {
		SecConstraint constraint = SecFactory.assert_constraint(statement, Boolean.TRUE, true);
		List<SecDescription> init_errors = new ArrayList<SecDescription>();
		
		CirAssignStatement inc_statement = 
				(CirAssignStatement) get_cir_nodes(location, CirIncreAssignStatement.class).get(0);
		CirExpression inc_expression = inc_statement.get_rvalue();
		init_errors.add(SecFactory.
				add_expression(statement, inc_expression, COperator.arith_add, Integer.valueOf(-2)));
		
		CirAssignStatement sav_statement = (CirAssignStatement) this.
				get_cir_nodes(location, CirSaveAssignStatement.class).get(0);
		init_errors.add(SecFactory.add_expression(
				statement, sav_statement.get_rvalue(), COperator.arith_add, Integer.valueOf(-1)));
		
		this.add_infection(constraint, SecFactory.conjunct(statement, init_errors));
	}
	
	private void post_dec_to_post_inc() throws Exception {
		SecConstraint constraint = SecFactory.assert_constraint(statement, Boolean.TRUE, true);
		CirAssignStatement inc_statement = 
				(CirAssignStatement) get_cir_nodes(location, CirIncreAssignStatement.class).get(0);
		CirExpression inc_expression = inc_statement.get_rvalue();
		SecDescription init_error = SecFactory.
				add_expression(statement, inc_expression, COperator.arith_add, Integer.valueOf(2));
		this.add_infection(constraint, init_error);
	}
	private void post_dec_to_prev_dec() throws Exception {
		CirAssignStatement sav_statement = (CirAssignStatement) this.
				get_cir_nodes(location, CirSaveAssignStatement.class).get(0);
		SecConstraint constraint = SecFactory.assert_constraint(statement, Boolean.TRUE, true);
		this.add_infection(constraint, SecFactory.add_expression(
				statement, sav_statement.get_rvalue(), COperator.arith_add, Integer.valueOf(-1)));
	}
	private void post_dec_to_prev_inc() throws Exception {
		SecConstraint constraint = SecFactory.assert_constraint(statement, Boolean.TRUE, true);
		List<SecDescription> init_errors = new ArrayList<SecDescription>();
		
		CirAssignStatement inc_statement = 
				(CirAssignStatement) get_cir_nodes(location, CirIncreAssignStatement.class).get(0);
		CirExpression inc_expression = inc_statement.get_rvalue();
		init_errors.add(SecFactory.
				add_expression(statement, inc_expression, COperator.arith_add, Integer.valueOf(2)));
		
		CirAssignStatement sav_statement = (CirAssignStatement) this.
				get_cir_nodes(location, CirSaveAssignStatement.class).get(0);
		init_errors.add(SecFactory.add_expression(
				statement, sav_statement.get_rvalue(), COperator.arith_add, Integer.valueOf(1)));
		
		this.add_infection(constraint, SecFactory.conjunct(statement, init_errors));
	}
	
	@Override
	protected void generate_infections() throws Exception {
		switch(this.mutation.get_operator()) {
		case prev_inc_to_prev_dec:	this.prev_inc_to_prev_dec(); break;
		case prev_inc_to_post_inc:	this.prev_inc_to_post_inc(); break;
		case prev_inc_to_post_dec:	this.prev_inc_to_post_dec(); break;
		case prev_dec_to_prev_inc:	this.prev_dec_to_prev_inc(); break;
		case prev_dec_to_post_dec:	this.prev_dec_to_post_dec(); break;
		case prev_dec_to_post_inc:	this.prev_dec_to_post_inc(); break;
		case post_inc_to_post_dec:	this.post_inc_to_post_dec(); break;
		case post_inc_to_prev_inc:	this.post_inc_to_prev_inc(); break;
		case post_inc_to_prev_dec:	this.post_inc_to_prev_dec(); break;
		case post_dec_to_post_inc:	this.post_dec_to_post_inc(); break;
		case post_dec_to_prev_dec:	this.post_dec_to_prev_dec(); break;
		case post_dec_to_prev_inc:	this.post_dec_to_prev_inc(); break;
		default: throw new IllegalArgumentException(mutation.toString());
		}
	}

}
