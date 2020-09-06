package com.jcsa.jcmutest.selang.muta;

import java.util.ArrayList;
import java.util.List;

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

public class UIORInfectParser extends SedInfectParser {

	@Override
	protected CirStatement faulty_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		AstExpression location = (AstExpression) mutation.get_location();
		return cir_tree.get_localizer().beg_statement(location);
	}
	
	private void prev_inc_to_prev_dec(CirTree cir_tree, CirStatement statement, 
			AstExpression expression, SedInfection infection) throws Exception {
		SedDescription constraint = SedFactory.condition_constraint(statement, Boolean.TRUE, true);
		
		CirAssignStatement inc_statement = (CirAssignStatement) cir_tree.get_localizer().
				get_cir_nodes(expression, CirIncreAssignStatement.class).get(0);
		CirExpression inc_expression = inc_statement.get_rvalue();
		SedDescription init_error = SedFactory.app_expression(statement, inc_expression, 
				COperator.arith_sub, (SedExpression) SedFactory.fetch(Integer.valueOf(2)));
		
		infection.add_infection_pair(constraint, init_error);
	}
	private void prev_inc_to_post_inc(CirTree cir_tree, CirStatement statement, 
			AstExpression expression, SedInfection infection) throws Exception {
		SedDescription constraint = SedFactory.condition_constraint(statement, Boolean.TRUE, true);
		
		CirExpression use_expression = cir_tree.get_cir_range(expression).get_result();
		SedDescription init_error = SedFactory.app_expression(statement, use_expression, 
				COperator.arith_sub, (SedExpression) SedFactory.fetch(Integer.valueOf(1)));
		
		infection.add_infection_pair(constraint, init_error);
	}
	private void prev_inc_to_post_dec(CirTree cir_tree, CirStatement statement, 
			AstExpression expression, SedInfection infection) throws Exception {
		SedDescription constraint = SedFactory.condition_constraint(statement, Boolean.TRUE, true);
		List<SedDescription> init_errors = new ArrayList<SedDescription>();
		
		CirAssignStatement inc_statement = (CirAssignStatement) cir_tree.get_localizer().
				get_cir_nodes(expression, CirIncreAssignStatement.class).get(0);
		CirExpression inc_expression = inc_statement.get_rvalue();
		init_errors.add(SedFactory.app_expression(statement, inc_expression, 
				COperator.arith_sub, (SedExpression) SedFactory.fetch(Integer.valueOf(2))));
		
		CirExpression use_expression = cir_tree.get_cir_range(expression).get_result();
		init_errors.add(SedFactory.app_expression(statement, use_expression, 
				COperator.arith_sub, (SedExpression) SedFactory.fetch(Integer.valueOf(1))));
		
		SedDescription init_error = SedFactory.conjunct(statement, init_errors);
		infection.add_infection_pair(constraint, init_error);
	}
	
	private void prev_dec_to_prev_inc(CirTree cir_tree, CirStatement statement, 
			AstExpression expression, SedInfection infection) throws Exception {
		SedDescription constraint = SedFactory.condition_constraint(statement, Boolean.TRUE, true);
		
		CirAssignStatement inc_statement = (CirAssignStatement) cir_tree.get_localizer().
				get_cir_nodes(expression, CirIncreAssignStatement.class).get(0);
		CirExpression inc_expression = inc_statement.get_rvalue();
		SedDescription init_error = SedFactory.app_expression(statement, inc_expression, 
				COperator.arith_add, (SedExpression) SedFactory.fetch(Integer.valueOf(2)));
		
		infection.add_infection_pair(constraint, init_error);
	}
	private void prev_dec_to_post_dec(CirTree cir_tree, CirStatement statement, 
			AstExpression expression, SedInfection infection) throws Exception {
		SedDescription constraint = SedFactory.condition_constraint(statement, Boolean.TRUE, true);
		
		CirExpression use_expression = cir_tree.get_cir_range(expression).get_result();
		SedDescription init_error = SedFactory.app_expression(statement, use_expression, 
				COperator.arith_add, (SedExpression) SedFactory.fetch(Integer.valueOf(1)));
		
		infection.add_infection_pair(constraint, init_error);
	}
	private void prev_dec_to_post_inc(CirTree cir_tree, CirStatement statement, 
			AstExpression expression, SedInfection infection) throws Exception {
		SedDescription constraint = SedFactory.condition_constraint(statement, Boolean.TRUE, true);
		List<SedDescription> init_errors = new ArrayList<SedDescription>();
		
		CirAssignStatement inc_statement = (CirAssignStatement) cir_tree.get_localizer().
				get_cir_nodes(expression, CirIncreAssignStatement.class).get(0);
		CirExpression inc_expression = inc_statement.get_rvalue();
		init_errors.add(SedFactory.app_expression(statement, inc_expression, 
				COperator.arith_add, (SedExpression) SedFactory.fetch(Integer.valueOf(2))));
		
		CirExpression use_expression = cir_tree.get_cir_range(expression).get_result();
		init_errors.add(SedFactory.app_expression(statement, use_expression, 
				COperator.arith_add, (SedExpression) SedFactory.fetch(Integer.valueOf(1))));
		
		SedDescription init_error = SedFactory.conjunct(statement, init_errors);
		infection.add_infection_pair(constraint, init_error);
	}
	
	private void post_inc_to_post_dec(CirTree cir_tree, CirStatement statement, 
			AstExpression expression, SedInfection infection) throws Exception {
		SedDescription constraint = SedFactory.condition_constraint(statement, Boolean.TRUE, true);
		
		CirAssignStatement inc_statement = (CirAssignStatement) cir_tree.get_localizer().
				get_cir_nodes(expression, CirIncreAssignStatement.class).get(0);
		CirExpression inc_expression = inc_statement.get_rvalue();
		SedDescription init_error = SedFactory.app_expression(statement, inc_expression, 
				COperator.arith_sub, (SedExpression) SedFactory.fetch(Integer.valueOf(2)));

		infection.add_infection_pair(constraint, init_error);
	}
	private void post_inc_to_prev_inc(CirTree cir_tree, CirStatement statement, 
			AstExpression expression, SedInfection infection) throws Exception {
		SedDescription constraint = SedFactory.condition_constraint(statement, Boolean.TRUE, true);
		
		CirExpression use_expression = cir_tree.get_cir_range(expression).get_result();
		SedDescription init_error = SedFactory.app_expression(statement, use_expression, 
				COperator.arith_add, (SedExpression) SedFactory.fetch(Integer.valueOf(1)));
		
		infection.add_infection_pair(constraint, init_error);
	}
	private void post_inc_to_prev_dec(CirTree cir_tree, CirStatement statement, 
			AstExpression expression, SedInfection infection) throws Exception {
		SedDescription constraint = SedFactory.condition_constraint(statement, Boolean.TRUE, true);
		List<SedDescription> init_errors = new ArrayList<SedDescription>();
		
		CirAssignStatement inc_statement = (CirAssignStatement) cir_tree.get_localizer().
				get_cir_nodes(expression, CirIncreAssignStatement.class).get(0);
		CirExpression inc_expression = inc_statement.get_rvalue();
		init_errors.add(SedFactory.app_expression(statement, inc_expression, 
				COperator.arith_sub, (SedExpression) SedFactory.fetch(Integer.valueOf(2))));
		
		CirExpression use_expression = cir_tree.get_cir_range(expression).get_result();
		init_errors.add(SedFactory.app_expression(statement, use_expression, 
				COperator.arith_sub, (SedExpression) SedFactory.fetch(Integer.valueOf(1))));
		
		SedDescription init_error = SedFactory.conjunct(statement, init_errors);
		infection.add_infection_pair(constraint, init_error);
	}
	
	private void post_dec_to_post_inc(CirTree cir_tree, CirStatement statement, 
			AstExpression expression, SedInfection infection) throws Exception {
		SedDescription constraint = SedFactory.condition_constraint(statement, Boolean.TRUE, true);
		
		CirAssignStatement inc_statement = (CirAssignStatement) cir_tree.get_localizer().
				get_cir_nodes(expression, CirIncreAssignStatement.class).get(0);
		CirExpression inc_expression = inc_statement.get_rvalue();
		SedDescription init_error = SedFactory.app_expression(statement, inc_expression, 
				COperator.arith_add, (SedExpression) SedFactory.fetch(Integer.valueOf(2)));

		infection.add_infection_pair(constraint, init_error);
	}
	private void post_dec_to_prev_dec(CirTree cir_tree, CirStatement statement, 
			AstExpression expression, SedInfection infection) throws Exception {
		SedDescription constraint = SedFactory.condition_constraint(statement, Boolean.TRUE, true);
		
		CirExpression use_expression = cir_tree.get_cir_range(expression).get_result();
		SedDescription init_error = SedFactory.app_expression(statement, use_expression, 
				COperator.arith_sub, (SedExpression) SedFactory.fetch(Integer.valueOf(1)));
		
		infection.add_infection_pair(constraint, init_error);
	}
	private void post_dec_to_prev_inc(CirTree cir_tree, CirStatement statement, 
			AstExpression expression, SedInfection infection) throws Exception {
		SedDescription constraint = SedFactory.condition_constraint(statement, Boolean.TRUE, true);
		List<SedDescription> init_errors = new ArrayList<SedDescription>();
		
		CirAssignStatement inc_statement = (CirAssignStatement) cir_tree.get_localizer().
				get_cir_nodes(expression, CirIncreAssignStatement.class).get(0);
		CirExpression inc_expression = inc_statement.get_rvalue();
		init_errors.add(SedFactory.app_expression(statement, inc_expression, 
				COperator.arith_add, (SedExpression) SedFactory.fetch(Integer.valueOf(2))));
		
		CirExpression use_expression = cir_tree.get_cir_range(expression).get_result();
		init_errors.add(SedFactory.app_expression(statement, use_expression, 
				COperator.arith_add, (SedExpression) SedFactory.fetch(Integer.valueOf(1))));
		
		SedDescription init_error = SedFactory.conjunct(statement, init_errors);
		infection.add_infection_pair(constraint, init_error);
	}
	
	@Override
	protected void add_infections(CirTree cir_tree, CirStatement statement, 
			AstMutation mutation, SedInfection infection) throws Exception {
		AstExpression expression = (AstExpression) mutation.get_location();
		switch(mutation.get_operator()) {
		case prev_inc_to_prev_dec:	
			this.prev_inc_to_prev_dec(cir_tree, statement, expression, infection); break;
		case prev_inc_to_post_inc:
			this.prev_inc_to_post_inc(cir_tree, statement, expression, infection); break;
		case prev_inc_to_post_dec:
			this.prev_inc_to_post_dec(cir_tree, statement, expression, infection); break;
		case prev_dec_to_prev_inc:
			this.prev_dec_to_prev_inc(cir_tree, statement, expression, infection); break;
		case prev_dec_to_post_dec:
			this.prev_dec_to_post_dec(cir_tree, statement, expression, infection); break;
		case prev_dec_to_post_inc:
			this.prev_dec_to_post_inc(cir_tree, statement, expression, infection); break;
		case post_inc_to_post_dec:
			this.post_inc_to_post_dec(cir_tree, statement, expression, infection); break;
		case post_inc_to_prev_inc:
			this.post_inc_to_prev_inc(cir_tree, statement, expression, infection); break;
		case post_inc_to_prev_dec:
			this.post_inc_to_prev_dec(cir_tree, statement, expression, infection); break;
		case post_dec_to_post_inc:
			this.post_dec_to_post_inc(cir_tree, statement, expression, infection); break;
		case post_dec_to_prev_dec:
			this.post_dec_to_prev_dec(cir_tree, statement, expression, infection); break;
		case post_dec_to_prev_inc:
			this.post_dec_to_prev_inc(cir_tree, statement, expression, infection); break;
		default: throw new IllegalArgumentException("Invalid: " + mutation);
		}
	}

}
