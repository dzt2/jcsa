package com.jcsa.jcmutest.mutant.sta2mutant.muta.unry;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirAbstErrorState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirAbstractState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirConditionState;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.StateMutationParser;
import com.jcsa.jcparse.lang.irlang.expr.CirComputeExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIncreAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirSaveAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class UIORStateMutationParser extends StateMutationParser {
	
	/* {++x} */
	/**
	 * (++x, --x)
	 * @param mutation
	 * @throws Exception
	 */
	private void prev_inc_to_prev_dec(AstMutation mutation) throws Exception {
		CirAssignStatement inc_statement = (CirAssignStatement) 
				this.get_cir_node(mutation.get_location(), CirIncreAssignStatement.class);
		CirExpression expression = (CirComputeExpression) inc_statement.get_rvalue();
		CirConditionState constraint = CirAbstractState.cov_time(inc_statement.execution_of(), 1);
		CirAbstErrorState init_error = CirAbstErrorState.inc_expr(expression,Integer.valueOf(-2));
		this.put_infection_pair(constraint, init_error);
	}
	/**
	 * (++x, x++)
	 * @param mutation
	 * @throws Exception
	 */
	private void prev_inc_to_post_inc(AstMutation mutation) throws Exception {
		CirExpression use_expression = this.get_cir_expression(mutation.get_location());
		if(use_expression == null || use_expression.statement_of() == null) { return; }
		CirConditionState constraint = CirAbstractState.cov_time(use_expression.execution_of(), 1);
		CirAbstErrorState init_error = CirAbstractState.inc_expr(use_expression, Integer.valueOf(-1));
		this.put_infection_pair(constraint, init_error);
	}
	/**
	 * (++x, x--)
	 * @param mutation
	 * @throws Exception
	 */
	private void prev_inc_to_post_dec(AstMutation mutation) throws Exception {
		/* use-oriented mutation pattern */
		CirExpression use_expression = this.get_cir_expression(mutation.get_location());
		if(use_expression != null && use_expression.statement_of() != null) {
			CirConditionState constraint = CirAbstractState.cov_time(use_expression.execution_of(), 1);
			CirAbstErrorState error_0 = CirAbstractState.inc_expr(use_expression, Integer.valueOf(-1));
			this.put_infection_pair(constraint, error_0);
			
			/* new definition introduced */
			CirAbstErrorState error_1 = CirAbstractState.inc_vdef(
					use_expression, SymbolFactory.sym_expression(use_expression), Integer.valueOf(-2));
			this.put_infection_pair(constraint, error_1);
		}
		/* def-oriented mutation pattern */
		else {
			this.prev_inc_to_prev_dec(mutation);
		}
	}
	
	/* {--x} */
	/**
	 * (--x, ++x)
	 * @param mutation
	 * @throws Exception
	 */
	private void prev_dec_to_prev_inc(AstMutation mutation) throws Exception {
		CirAssignStatement inc_statement = (CirAssignStatement) 
				this.get_cir_node(mutation.get_location(), CirIncreAssignStatement.class);
		CirExpression expression = (CirComputeExpression) inc_statement.get_rvalue();
		CirConditionState constraint = CirAbstractState.cov_time(inc_statement.execution_of(), 1);
		CirAbstErrorState init_error = CirAbstErrorState.inc_expr(expression,Integer.valueOf(2));
		this.put_infection_pair(constraint, init_error);
	}
	/**
	 * (--x, x--)
	 * @param mutation
	 * @throws Exception
	 */
	private void prev_dec_to_post_dec(AstMutation mutation) throws Exception {
		CirExpression use_expression = this.get_cir_expression(mutation.get_location());
		if(use_expression == null || use_expression.statement_of() == null) { return; }
		CirConditionState constraint = CirAbstractState.cov_time(use_expression.execution_of(), 1);
		CirAbstErrorState init_error = CirAbstractState.inc_expr(use_expression, Integer.valueOf(1));
		this.put_infection_pair(constraint, init_error);
	}
	/**
	 * (--x, x++)
	 * @param mutation
	 * @throws Exception
	 */
	private void prev_dec_to_post_inc(AstMutation mutation) throws Exception {
		/* use-oriented mutation pattern */
		CirExpression use_expression = this.get_cir_expression(mutation.get_location());
		if(use_expression != null && use_expression.statement_of() != null) {
			CirConditionState constraint = CirAbstractState.cov_time(use_expression.execution_of(), 1);
			CirAbstErrorState error_0 = CirAbstractState.inc_expr(use_expression, Integer.valueOf(1));
			this.put_infection_pair(constraint, error_0);
			
			/* new definition introduced */
			CirAbstErrorState error_1 = CirAbstractState.inc_vdef(
					use_expression, SymbolFactory.sym_expression(use_expression), Integer.valueOf(2));
			this.put_infection_pair(constraint, error_1);
		}
		/* def-oriented mutation pattern */
		else {
			this.prev_dec_to_prev_inc(mutation);
		}
	}
	
	/* {x++} */
	/**
	 * (x++, x--)
	 * @param mutation
	 * @throws Exception
	 */
	private void post_inc_to_post_dec(AstMutation mutation) throws Exception {
		CirAssignStatement inc_statement = (CirAssignStatement) 
				this.get_cir_node(mutation.get_location(), CirIncreAssignStatement.class);
		CirExpression expression = (CirComputeExpression) inc_statement.get_rvalue();
		CirConditionState constraint = CirAbstractState.cov_time(inc_statement.execution_of(), 1);
		CirAbstErrorState init_error = CirAbstErrorState.inc_expr(expression,Integer.valueOf(-2));
		this.put_infection_pair(constraint, init_error);
	}
	/**
	 * (x++, ++x)
	 * @param mutation
	 * @throws Exception
	 */
	private void post_inc_to_prev_inc(AstMutation mutation) throws Exception {
		CirAssignStatement sav_statement = (CirAssignStatement) 
				this.get_cir_node(mutation.get_location(), CirSaveAssignStatement.class);
		CirExpression expression = sav_statement.get_rvalue();
		CirConditionState constraint = CirAbstractState.cov_time(sav_statement.execution_of(), 1);
		CirAbstErrorState init_error = CirAbstractState.inc_expr(expression, Integer.valueOf(1));
		this.put_infection_pair(constraint, init_error);
	}
	/**
	 * (x++, --x)
	 * @param mutation
	 * @throws Exception
	 */
	private void post_inc_to_prev_dec(AstMutation mutation) throws Exception {
		{
			CirAssignStatement inc_statement = (CirAssignStatement) 
					this.get_cir_node(mutation.get_location(), CirIncreAssignStatement.class);
			CirExpression expression = (CirComputeExpression) inc_statement.get_rvalue();
			CirConditionState constraint = CirAbstractState.cov_time(inc_statement.execution_of(), 1);
			CirAbstErrorState init_error = CirAbstErrorState.inc_expr(expression,Integer.valueOf(-2));
			this.put_infection_pair(constraint, init_error);
		}
		
		{
			CirAssignStatement sav_statement = (CirAssignStatement) 
					this.get_cir_node(mutation.get_location(), CirSaveAssignStatement.class);
			CirExpression expression = sav_statement.get_rvalue();
			CirConditionState constraint = CirAbstractState.cov_time(sav_statement.execution_of(), 1);
			CirAbstErrorState init_error = CirAbstractState.inc_expr(expression, Integer.valueOf(-1));
			this.put_infection_pair(constraint, init_error);
		}
	}
	
	/* {x--} */
	/**
	 * (x--, x++)
	 * @param mutation
	 * @throws Exception
	 */
	private void post_dec_to_post_inc(AstMutation mutation) throws Exception {
		CirAssignStatement inc_statement = (CirAssignStatement) 
				this.get_cir_node(mutation.get_location(), CirIncreAssignStatement.class);
		CirExpression expression = (CirComputeExpression) inc_statement.get_rvalue();
		CirConditionState constraint = CirAbstractState.cov_time(inc_statement.execution_of(), 1);
		CirAbstErrorState init_error = CirAbstErrorState.inc_expr(expression,Integer.valueOf(2));
		this.put_infection_pair(constraint, init_error);
	}
	/**
	 * (x--, --x)
	 * @param mutation
	 * @throws Exception
	 */
	private void post_dec_to_prev_dec(AstMutation mutation) throws Exception {
		CirAssignStatement sav_statement = (CirAssignStatement) 
				this.get_cir_node(mutation.get_location(), CirSaveAssignStatement.class);
		CirExpression expression = sav_statement.get_rvalue();
		CirConditionState constraint = CirAbstractState.cov_time(sav_statement.execution_of(), 1);
		CirAbstErrorState init_error = CirAbstractState.inc_expr(expression, Integer.valueOf(-1));
		this.put_infection_pair(constraint, init_error);
	}
	/**
	 * (x--, ++x)
	 * @param mutation
	 * @throws Exception
	 */
	private void post_dec_to_prev_inc(AstMutation mutation) throws Exception {
		{
			CirAssignStatement inc_statement = (CirAssignStatement) 
					this.get_cir_node(mutation.get_location(), CirIncreAssignStatement.class);
			CirExpression expression = (CirComputeExpression) inc_statement.get_rvalue();
			CirConditionState constraint = CirAbstractState.cov_time(inc_statement.execution_of(), 1);
			CirAbstErrorState init_error = CirAbstErrorState.inc_expr(expression,Integer.valueOf(2));
			this.put_infection_pair(constraint, init_error);
		}
		{
			CirAssignStatement sav_statement = (CirAssignStatement) 
					this.get_cir_node(mutation.get_location(), CirSaveAssignStatement.class);
			CirExpression expression = sav_statement.get_rvalue();
			CirConditionState constraint = CirAbstractState.cov_time(sav_statement.execution_of(), 1);
			CirAbstErrorState init_error = CirAbstractState.inc_expr(expression, Integer.valueOf(1));
			this.put_infection_pair(constraint, init_error);
		}
	}
	
	/* implement */
	@Override
	protected CirStatement find_reach_point(AstMutation mutation) throws Exception {
		return this.get_end_statement(mutation.get_location());
	}
	@Override
	protected void generate_infections(AstMutation mutation) throws Exception {
		switch(mutation.get_operator()) {
		/* ++x */
		case prev_inc_to_prev_dec:	this.prev_inc_to_prev_dec(mutation); break;
		case prev_inc_to_post_inc:	this.prev_inc_to_post_inc(mutation); break;
		case prev_inc_to_post_dec:	this.prev_inc_to_post_dec(mutation); break;
		/* --x */
		case prev_dec_to_prev_inc:	this.prev_dec_to_prev_inc(mutation); break;
		case prev_dec_to_post_dec:	this.prev_dec_to_post_dec(mutation); break;
		case prev_dec_to_post_inc:	this.prev_dec_to_post_inc(mutation); break;
		/* x++ */
		case post_inc_to_post_dec:	this.post_inc_to_post_dec(mutation); break;
		case post_inc_to_prev_inc:	this.post_inc_to_prev_inc(mutation); break;
		case post_inc_to_prev_dec:	this.post_inc_to_prev_dec(mutation); break;
		/* x-- */
		case post_dec_to_post_inc:	this.post_dec_to_post_inc(mutation); break;
		case post_dec_to_prev_dec:	this.post_dec_to_prev_dec(mutation); break;
		case post_dec_to_prev_inc:	this.post_dec_to_prev_inc(mutation); break;
		/* error */
		default:	throw new IllegalArgumentException("Invalid: " + mutation.get_operator());
		}
	}
	
}
