package com.jcsa.jcmutest.mutant.sec2mutant.muta.unry;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecConstraint;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecDescription;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.SecInfectionParser;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIncreAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class UIORInfectionParser extends SecInfectionParser {

	@Override
	protected CirStatement find_location(AstMutation mutation) throws Exception {
		return this.get_end_statement(mutation.get_location());
	}
	
	private boolean prev_inc_to_prev_dec(AstMutation mutation) throws Exception {
		SecConstraint constraint = this.get_constraint(Boolean.TRUE, true);
		CirAssignStatement inc_statement = (CirAssignStatement) get_cir_node(
					mutation.get_location(), CirIncreAssignStatement.class);
		CirExpression inc_expression = inc_statement.get_rvalue();
		SecDescription init_error = this.sub_expression(inc_expression, Integer.valueOf(2));
		this.add_infection(constraint, init_error); return true;
	}
	private boolean prev_inc_to_post_inc(AstMutation mutation) throws Exception {
		SecConstraint constraint = this.get_constraint(Boolean.TRUE, true);
		CirExpression use_expression = this.get_cir_expression(mutation.get_location());
		if(use_expression.statement_of() != null) {
			SecDescription init_error = this.sub_expression(use_expression, Integer.valueOf(1));
			this.add_infection(constraint, init_error);
			return true;
		}
		else {
			return false;
		}
	}
	private boolean prev_inc_to_post_dec(AstMutation mutation) throws Exception {
		SecConstraint constraint = this.get_constraint(Boolean.TRUE, true);
		List<SecDescription> init_errors = new ArrayList<SecDescription>();
		
		/* inc_expression */
		CirAssignStatement inc_statement = (CirAssignStatement) get_cir_node(
				mutation.get_location(), CirIncreAssignStatement.class);
		CirExpression inc_expression = inc_statement.get_rvalue();
		init_errors.add(this.sub_expression(inc_expression, Integer.valueOf(2)));
		
		/* use_expression */
		CirExpression use_expression = this.get_cir_expression(mutation.get_location());
		if(use_expression.statement_of() != null) {
			init_errors.add(this.sub_expression(use_expression, Integer.valueOf(1)));
		}
		
		if(init_errors.isEmpty()) {
			return false;
		}
		else if(init_errors.size() == 1) {
			this.add_infection(constraint, init_errors.get(0));
		}
		else {
			this.add_infection(constraint, this.conjunct(init_errors));
		}
		return true;
	}
	
	private boolean prev_dec_to_prev_inc(AstMutation mutation) throws Exception {
		SecConstraint constraint = this.get_constraint(Boolean.TRUE, true);
		CirAssignStatement inc_statement = (CirAssignStatement) get_cir_node(
					mutation.get_location(), CirIncreAssignStatement.class);
		CirExpression inc_expression = inc_statement.get_rvalue();
		SecDescription init_error = this.add_expression(inc_expression, Integer.valueOf(2));
		this.add_infection(constraint, init_error); return true;
	}
	private boolean prev_dec_to_post_dec(AstMutation mutation) throws Exception {
		SecConstraint constraint = this.get_constraint(Boolean.TRUE, true);
		CirExpression use_expression = this.get_cir_expression(mutation.get_location());
		if(use_expression.statement_of() != null) {
			SecDescription init_error = this.add_expression(use_expression, Integer.valueOf(1));
			this.add_infection(constraint, init_error);
			return true;
		}
		else {
			return false;
		}
	}
	private boolean prev_dec_to_post_inc(AstMutation mutation) throws Exception {
		SecConstraint constraint = this.get_constraint(Boolean.TRUE, true);
		List<SecDescription> init_errors = new ArrayList<SecDescription>();
		
		/* inc_expression */
		CirAssignStatement inc_statement = (CirAssignStatement) get_cir_node(
				mutation.get_location(), CirIncreAssignStatement.class);
		CirExpression inc_expression = inc_statement.get_rvalue();
		init_errors.add(this.add_expression(inc_expression, Integer.valueOf(2)));
		
		/* use_expression */
		CirExpression use_expression = this.get_cir_expression(mutation.get_location());
		if(use_expression.statement_of() != null) {
			init_errors.add(this.add_expression(use_expression, Integer.valueOf(1)));
		}
		
		if(init_errors.isEmpty()) {
			return false;
		}
		else if(init_errors.size() == 1) {
			this.add_infection(constraint, init_errors.get(0));
		}
		else {
			this.add_infection(constraint, this.conjunct(init_errors));
		}
		return true;
	}
	
	private boolean post_inc_to_post_dec(AstMutation mutation) throws Exception {
		SecConstraint constraint = this.get_constraint(Boolean.TRUE, true);
		CirAssignStatement inc_statement = (CirAssignStatement) get_cir_node(
					mutation.get_location(), CirIncreAssignStatement.class);
		CirExpression inc_expression = inc_statement.get_rvalue();
		SecDescription init_error = this.sub_expression(inc_expression, Integer.valueOf(2));
		this.add_infection(constraint, init_error); return true;
	}
	private boolean post_inc_to_prev_inc(AstMutation mutation) throws Exception {
		SecConstraint constraint = this.get_constraint(Boolean.TRUE, true);
		CirExpression use_expression = this.get_cir_expression(mutation.get_location());
		if(use_expression.statement_of() != null) {
			SecDescription init_error = this.add_expression(use_expression, Integer.valueOf(1));
			this.add_infection(constraint, init_error);
			return true;
		}
		else {
			return false;
		}
	}
	private boolean post_inc_to_prev_dec(AstMutation mutation) throws Exception {
		SecConstraint constraint = this.get_constraint(Boolean.TRUE, true);
		List<SecDescription> init_errors = new ArrayList<SecDescription>();
		
		CirAssignStatement inc_statement = (CirAssignStatement) get_cir_node(
				mutation.get_location(), CirIncreAssignStatement.class);
		CirExpression inc_expression = inc_statement.get_rvalue();
		init_errors.add(this.sub_expression(inc_expression, Integer.valueOf(2)));
		
		CirExpression use_expression = this.get_cir_expression(mutation.get_location());
		if(use_expression.statement_of() != null) {
			init_errors.add(this.sub_expression(use_expression, Integer.valueOf(1)));
		}
		
		if(init_errors.isEmpty()) {
			return false;
		}
		else if(init_errors.size() == 1) {
			this.add_infection(constraint, init_errors.get(0));
		}
		else {
			this.add_infection(constraint, this.conjunct(init_errors));
		}
		return true;
	}
	
	private boolean post_dec_to_post_inc(AstMutation mutation) throws Exception {
		SecConstraint constraint = this.get_constraint(Boolean.TRUE, true);
		CirAssignStatement inc_statement = (CirAssignStatement) get_cir_node(
					mutation.get_location(), CirIncreAssignStatement.class);
		CirExpression inc_expression = inc_statement.get_rvalue();
		SecDescription init_error = this.add_expression(inc_expression, Integer.valueOf(2));
		this.add_infection(constraint, init_error); return true;
	}
	private boolean post_dec_to_prev_dec(AstMutation mutation) throws Exception {
		SecConstraint constraint = this.get_constraint(Boolean.TRUE, true);
		CirExpression use_expression = this.get_cir_expression(mutation.get_location());
		if(use_expression.statement_of() != null) {
			SecDescription init_error = this.sub_expression(use_expression, Integer.valueOf(1));
			this.add_infection(constraint, init_error);
			return true;
		}
		else {
			return false;
		}
	}
	private boolean post_dec_to_prev_inc(AstMutation mutation) throws Exception {
		SecConstraint constraint = this.get_constraint(Boolean.TRUE, true);
		List<SecDescription> init_errors = new ArrayList<SecDescription>();
		
		CirAssignStatement inc_statement = (CirAssignStatement) get_cir_node(
				mutation.get_location(), CirIncreAssignStatement.class);
		CirExpression inc_expression = inc_statement.get_rvalue();
		init_errors.add(this.add_expression(inc_expression, Integer.valueOf(2)));
		
		CirExpression use_expression = this.get_cir_expression(mutation.get_location());
		if(use_expression.statement_of() != null) {
			init_errors.add(this.add_expression(use_expression, Integer.valueOf(1)));
		}
		
		if(init_errors.isEmpty()) {
			return false;
		}
		else if(init_errors.size() == 1) {
			this.add_infection(constraint, init_errors.get(0));
		}
		else {
			this.add_infection(constraint, this.conjunct(init_errors));
		}
		return true;
	}
	
	@Override
	protected boolean generate_infections(CirStatement statement, AstMutation mutation) throws Exception {
		switch(mutation.get_operator()) {
		case prev_inc_to_prev_dec:	return this.prev_inc_to_prev_dec(mutation);
		case prev_inc_to_post_inc:	return this.prev_inc_to_post_inc(mutation);
		case prev_inc_to_post_dec:	return this.prev_inc_to_post_dec(mutation);
		
		case prev_dec_to_prev_inc:	return this.prev_dec_to_prev_inc(mutation);
		case prev_dec_to_post_dec:	return this.prev_dec_to_post_dec(mutation);
		case prev_dec_to_post_inc:	return this.prev_dec_to_post_inc(mutation);
		
		case post_inc_to_post_dec:	return this.post_inc_to_post_dec(mutation);
		case post_inc_to_prev_inc:	return this.post_inc_to_prev_inc(mutation);
		case post_inc_to_prev_dec:	return this.post_inc_to_prev_dec(mutation);
		
		case post_dec_to_post_inc:	return this.post_dec_to_post_inc(mutation);
		case post_dec_to_prev_dec:	return this.post_dec_to_prev_dec(mutation);
		case post_dec_to_prev_inc:	return this.post_dec_to_prev_inc(mutation);
		default:					return false;
		}
	}

}
