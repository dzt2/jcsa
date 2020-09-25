package com.jcsa.jcmutest.backups;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncreUnaryExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIncreAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirSaveAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class UIODStateMutationParser extends StateMutationParser {
	
	@Override
	protected CirStatement find_beg_statement(AstMutation mutation) throws Exception {
		AstExpression location = (AstExpression) mutation.get_location();
		if(location instanceof AstIncreUnaryExpression) {
			return (CirStatement) this.get_cir_node(
					location, CirIncreAssignStatement.class);
		}
		else {
			return (CirStatement) this.get_cir_node(
					location, CirSaveAssignStatement.class);
		}
	}
	
	@Override
	protected CirStatement find_end_statement(AstMutation mutation) throws Exception {
		CirStatement statement;
		statement = this.get_cir_expression(mutation.get_location()).statement_of();
		if(statement == null) {
			statement = (CirStatement) this.get_cir_node(
					mutation.get_location(), CirIncreAssignStatement.class);
		}
		return statement;
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
