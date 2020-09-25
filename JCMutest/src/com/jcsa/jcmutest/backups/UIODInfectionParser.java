package com.jcsa.jcmutest.backups;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIncreAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class UIODInfectionParser extends SecInfectionParser {

	@Override
	protected CirStatement find_location(AstMutation mutation) throws Exception {
		return this.get_beg_statement(mutation.get_location());
	}
	
	private boolean delete_prev_inc(AstMutation mutation) throws Exception {
		CirAssignStatement statement = (CirAssignStatement) this.
				get_cir_node(mutation.get_location(), CirIncreAssignStatement.class);
		CirExpression expression = statement.get_rvalue();
		
		SecConstraint constraint = this.get_constraint(Boolean.TRUE, true);
		SecStateError init_error = this.sub_expression(expression, Integer.valueOf(1));
		this.add_infection(constraint, init_error);
		return true;
	}
	
	private boolean delete_prev_dec(AstMutation mutation) throws Exception {
		CirAssignStatement statement = (CirAssignStatement) this.
				get_cir_node(mutation.get_location(), CirIncreAssignStatement.class);
		CirExpression expression = statement.get_rvalue();
		
		SecConstraint constraint = this.get_constraint(Boolean.TRUE, true);
		SecStateError init_error = this.add_expression(expression, Integer.valueOf(1));
		this.add_infection(constraint, init_error);
		return true;
	}
	
	private boolean delete_post_inc(AstMutation mutation) throws Exception {
		CirAssignStatement statement = (CirAssignStatement) this.
				get_cir_node(mutation.get_location(), CirIncreAssignStatement.class);
		CirExpression expression = statement.get_rvalue();
		
		SecConstraint constraint = this.get_constraint(Boolean.TRUE, true);
		SecStateError init_error = this.sub_expression(expression, Integer.valueOf(1));
		this.add_infection(constraint, init_error);
		return true;
	}
	
	private boolean delete_post_dec(AstMutation mutation) throws Exception {
		CirAssignStatement statement = (CirAssignStatement) this.
				get_cir_node(mutation.get_location(), CirIncreAssignStatement.class);
		CirExpression expression = statement.get_rvalue();
		
		SecConstraint constraint = this.get_constraint(Boolean.TRUE, true);
		SecStateError init_error = this.add_expression(expression, Integer.valueOf(1));
		this.add_infection(constraint, init_error);
		return true;
	}
	
	@Override
	protected boolean generate_infections(CirStatement statement, AstMutation mutation) throws Exception {
		switch(mutation.get_operator()) {
		case delete_prev_inc:	return this.delete_prev_inc(mutation);
		case delete_prev_dec:	return this.delete_prev_dec(mutation);
		case delete_post_inc:	return this.delete_post_inc(mutation);
		case delete_post_dec:	return this.delete_post_dec(mutation);
		default:				return false;
		}
	}

}
