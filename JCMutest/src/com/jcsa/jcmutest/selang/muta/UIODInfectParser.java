package com.jcsa.jcmutest.selang.muta;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.selang.lang.desc.SedDescription;
import com.jcsa.jcmutest.selang.lang.expr.SedExpression;
import com.jcsa.jcmutest.selang.util.SedFactory;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIncreAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;

public class UIODInfectParser extends SedInfectParser {

	@Override
	protected CirStatement faulty_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return cir_tree.get_localizer().beg_statement(mutation.get_location());
	}
	
	private void delete_prev_inc(CirTree cir_tree, CirStatement statement, 
			AstExpression expression, SedInfection infection) throws Exception {
		SedDescription constraint = SedFactory.condition_constraint(statement, Boolean.TRUE, true);
		
		CirAssignStatement inc_statement = (CirAssignStatement) cir_tree.get_localizer().
				get_cir_nodes(expression, CirIncreAssignStatement.class).get(0);
		CirExpression inc_expression = inc_statement.get_rvalue();
		SedDescription init_error = SedFactory.app_expression(statement, inc_expression, 
				COperator.arith_sub, (SedExpression) SedFactory.fetch(Integer.valueOf(1)));
		
		infection.add_infection_pair(constraint, init_error);
	}
	private void delete_prev_dec(CirTree cir_tree, CirStatement statement, 
			AstExpression expression, SedInfection infection) throws Exception {
		SedDescription constraint = SedFactory.condition_constraint(statement, Boolean.TRUE, true);
		
		CirAssignStatement inc_statement = (CirAssignStatement) cir_tree.get_localizer().
				get_cir_nodes(expression, CirIncreAssignStatement.class).get(0);
		CirExpression inc_expression = inc_statement.get_rvalue();
		SedDescription init_error = SedFactory.app_expression(statement, inc_expression, 
				COperator.arith_add, (SedExpression) SedFactory.fetch(Integer.valueOf(1)));
		
		infection.add_infection_pair(constraint, init_error);
	}
	private void delete_post_inc(CirTree cir_tree, CirStatement statement, 
			AstExpression expression, SedInfection infection) throws Exception {
		SedDescription constraint = SedFactory.condition_constraint(statement, Boolean.TRUE, true);
		
		CirAssignStatement inc_statement = (CirAssignStatement) cir_tree.get_localizer().
				get_cir_nodes(expression, CirIncreAssignStatement.class).get(0);
		CirExpression inc_expression = inc_statement.get_rvalue();
		SedDescription init_error = SedFactory.app_expression(statement, inc_expression, 
				COperator.arith_sub, (SedExpression) SedFactory.fetch(Integer.valueOf(1)));
		
		infection.add_infection_pair(constraint, init_error);
	}
	private void delete_post_dec(CirTree cir_tree, CirStatement statement, 
			AstExpression expression, SedInfection infection) throws Exception {
		SedDescription constraint = SedFactory.condition_constraint(statement, Boolean.TRUE, true);
		
		CirAssignStatement inc_statement = (CirAssignStatement) cir_tree.get_localizer().
				get_cir_nodes(expression, CirIncreAssignStatement.class).get(0);
		CirExpression inc_expression = inc_statement.get_rvalue();
		SedDescription init_error = SedFactory.app_expression(statement, inc_expression, 
				COperator.arith_add, (SedExpression) SedFactory.fetch(Integer.valueOf(1)));
		
		infection.add_infection_pair(constraint, init_error);
	}

	@Override
	protected void add_infections(CirTree cir_tree, CirStatement statement, AstMutation mutation,
			SedInfection infection) throws Exception {
		AstExpression expression = (AstExpression) mutation.get_location();
		switch(mutation.get_operator()) {
		case delete_prev_inc: this.delete_prev_inc(cir_tree, statement, expression, infection); break;
		case delete_prev_dec: this.delete_prev_dec(cir_tree, statement, expression, infection); break;
		case delete_post_inc: this.delete_post_inc(cir_tree, statement, expression, infection); break;
		case delete_post_dec: this.delete_post_dec(cir_tree, statement, expression, infection); break;
		default: throw new IllegalArgumentException(mutation.toString());
		}
	}

}
