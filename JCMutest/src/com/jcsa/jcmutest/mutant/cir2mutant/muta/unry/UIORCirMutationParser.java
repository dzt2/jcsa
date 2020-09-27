package com.jcsa.jcmutest.mutant.cir2mutant.muta.unry;

import java.util.Map;

import com.jcsa.jcmutest.mutant.cir2mutant.model.CirConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.model.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.model.CirStateError;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.CirMutationParser;
import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirComputeExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIncreAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;

public class UIORCirMutationParser extends CirMutationParser {
	
	@Override
	protected CirStatement get_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return (CirStatement) this.
				get_cir_node(cir_tree, mutation.get_location(), CirIncreAssignStatement.class);
	}
	
	private void prev_inc_to_prev_dec(CirMutations mutations, CirTree cir_tree, AstMutation mutation, 
			Map<CirStateError, CirConstraint> infections) throws Exception {
		CirAssignStatement inc_statement = (CirAssignStatement) this.get_cir_node(
				cir_tree, mutation.get_location(), CirIncreAssignStatement.class);
		CirComputeExpression inc_expression = (CirComputeExpression) inc_statement.get_rvalue();
		SymExpression muta_expression = SymFactory.
				arith_sub(inc_expression.get_data_type(), inc_expression, Integer.valueOf(2));
		infections.put(mutations.expr_error(inc_expression, muta_expression), 
				mutations.expression_constraint(inc_statement, Boolean.TRUE, true));
	}
	private void prev_inc_to_post_inc(CirMutations mutations, CirTree cir_tree, AstMutation mutation, 
			Map<CirStateError, CirConstraint> infections) throws Exception {
		CirExpression use_expression = this.get_cir_expression(cir_tree, mutation.get_location());
		if(use_expression != null && use_expression.statement_of() != null) {
			SymExpression muta_expression = SymFactory.arith_sub(
					use_expression.get_data_type(), use_expression, Integer.valueOf(1));
			infections.put(mutations.expr_error(use_expression, muta_expression), 
					mutations.expression_constraint(use_expression.statement_of(), use_expression, true));
		}
	}
	private void prev_inc_to_post_dec(CirMutations mutations, CirTree cir_tree, AstMutation mutation, 
			Map<CirStateError, CirConstraint> infections) throws Exception {
		CirAssignStatement inc_statement = (CirAssignStatement) this.get_cir_node(
				cir_tree, mutation.get_location(), CirIncreAssignStatement.class);
		CirComputeExpression inc_expression = (CirComputeExpression) inc_statement.get_rvalue();
		SymExpression muta_expression = SymFactory.
				arith_sub(inc_expression.get_data_type(), inc_expression, Integer.valueOf(2));
		infections.put(mutations.expr_error(inc_expression, muta_expression), 
				mutations.expression_constraint(inc_statement, Boolean.TRUE, true));
		
		CirExpression use_expression = this.get_cir_expression(cir_tree, mutation.get_location());
		if(use_expression != null && use_expression.statement_of() != null) {
			muta_expression = SymFactory.arith_add(
					use_expression.get_data_type(), use_expression, Integer.valueOf(1));
			infections.put(mutations.expr_error(use_expression, muta_expression), 
					mutations.expression_constraint(use_expression.statement_of(), use_expression, true));
		}
	}
	
	private void prev_dec_to_prev_inc(CirMutations mutations, CirTree cir_tree, AstMutation mutation, 
			Map<CirStateError, CirConstraint> infections) throws Exception {
		CirAssignStatement inc_statement = (CirAssignStatement) this.get_cir_node(
				cir_tree, mutation.get_location(), CirIncreAssignStatement.class);
		CirComputeExpression inc_expression = (CirComputeExpression) inc_statement.get_rvalue();
		SymExpression muta_expression = SymFactory.
				arith_add(inc_expression.get_data_type(), inc_expression, Integer.valueOf(2));
		infections.put(mutations.expr_error(inc_expression, muta_expression), 
				mutations.expression_constraint(inc_statement, Boolean.TRUE, true));
	}
	private void prev_dec_to_post_dec(CirMutations mutations, CirTree cir_tree, AstMutation mutation, 
			Map<CirStateError, CirConstraint> infections) throws Exception {
		CirExpression use_expression = this.get_cir_expression(cir_tree, mutation.get_location());
		if(use_expression != null && use_expression.statement_of() != null) {
			SymExpression muta_expression = SymFactory.arith_add(
					use_expression.get_data_type(), use_expression, Integer.valueOf(1));
			infections.put(mutations.expr_error(use_expression, muta_expression), 
					mutations.expression_constraint(use_expression.statement_of(), use_expression, true));
		}
	}
	private void prev_dec_to_post_inc(CirMutations mutations, CirTree cir_tree, AstMutation mutation, 
			Map<CirStateError, CirConstraint> infections) throws Exception {
		CirAssignStatement inc_statement = (CirAssignStatement) this.get_cir_node(
				cir_tree, mutation.get_location(), CirIncreAssignStatement.class);
		CirComputeExpression inc_expression = (CirComputeExpression) inc_statement.get_rvalue();
		SymExpression muta_expression = SymFactory.
				arith_add(inc_expression.get_data_type(), inc_expression, Integer.valueOf(2));
		infections.put(mutations.expr_error(inc_expression, muta_expression), 
				mutations.expression_constraint(inc_statement, Boolean.TRUE, true));
		
		CirExpression use_expression = this.get_cir_expression(cir_tree, mutation.get_location());
		if(use_expression != null && use_expression.statement_of() != null) {
			muta_expression = SymFactory.arith_sub(
					use_expression.get_data_type(), use_expression, Integer.valueOf(1));
			infections.put(mutations.expr_error(use_expression, muta_expression), 
					mutations.expression_constraint(use_expression.statement_of(), use_expression, true));
		}
	}
	
	private void post_inc_to_post_dec(CirMutations mutations, CirTree cir_tree, AstMutation mutation, 
			Map<CirStateError, CirConstraint> infections) throws Exception {
		CirAssignStatement inc_statement = (CirAssignStatement) this.get_cir_node(
				cir_tree, mutation.get_location(), CirIncreAssignStatement.class);
		CirComputeExpression inc_expression = (CirComputeExpression) inc_statement.get_rvalue();
		SymExpression muta_expression = SymFactory.
				arith_sub(inc_expression.get_data_type(), inc_expression, Integer.valueOf(2));
		infections.put(mutations.expr_error(inc_expression, muta_expression), 
				mutations.expression_constraint(inc_statement, Boolean.TRUE, true));
	}
	private void post_inc_to_prev_inc(CirMutations mutations, CirTree cir_tree, AstMutation mutation, 
			Map<CirStateError, CirConstraint> infections) throws Exception {
		CirExpression use_expression = this.get_cir_expression(cir_tree, mutation.get_location());
		if(use_expression != null && use_expression.statement_of() != null) {
			SymExpression muta_expression = SymFactory.arith_add(
					use_expression.get_data_type(), use_expression, Integer.valueOf(1));
			infections.put(mutations.expr_error(use_expression, muta_expression), 
					mutations.expression_constraint(use_expression.statement_of(), use_expression, true));
		}
	}
	private void post_inc_to_prev_dec(CirMutations mutations, CirTree cir_tree, AstMutation mutation, 
			Map<CirStateError, CirConstraint> infections) throws Exception {
		CirAssignStatement inc_statement = (CirAssignStatement) this.get_cir_node(
				cir_tree, mutation.get_location(), CirIncreAssignStatement.class);
		CirComputeExpression inc_expression = (CirComputeExpression) inc_statement.get_rvalue();
		SymExpression muta_expression = SymFactory.
				arith_sub(inc_expression.get_data_type(), inc_expression, Integer.valueOf(2));
		infections.put(mutations.expr_error(inc_expression, muta_expression), 
				mutations.expression_constraint(inc_statement, Boolean.TRUE, true));
		
		CirExpression use_expression = this.get_cir_expression(cir_tree, mutation.get_location());
		if(use_expression != null && use_expression.statement_of() != null) {
			muta_expression = SymFactory.arith_sub(
					use_expression.get_data_type(), use_expression, Integer.valueOf(1));
			infections.put(mutations.expr_error(use_expression, muta_expression), 
					mutations.expression_constraint(use_expression.statement_of(), use_expression, true));
		}
	}
	
	private void post_dec_to_post_inc(CirMutations mutations, CirTree cir_tree, AstMutation mutation, 
			Map<CirStateError, CirConstraint> infections) throws Exception {
		CirAssignStatement inc_statement = (CirAssignStatement) this.get_cir_node(
				cir_tree, mutation.get_location(), CirIncreAssignStatement.class);
		CirComputeExpression inc_expression = (CirComputeExpression) inc_statement.get_rvalue();
		SymExpression muta_expression = SymFactory.
				arith_add(inc_expression.get_data_type(), inc_expression, Integer.valueOf(2));
		infections.put(mutations.expr_error(inc_expression, muta_expression), 
				mutations.expression_constraint(inc_statement, Boolean.TRUE, true));
	}
	private void post_dec_to_prev_dec(CirMutations mutations, CirTree cir_tree, AstMutation mutation, 
			Map<CirStateError, CirConstraint> infections) throws Exception {
		CirExpression use_expression = this.get_cir_expression(cir_tree, mutation.get_location());
		if(use_expression != null && use_expression.statement_of() != null) {
			SymExpression muta_expression = SymFactory.arith_sub(
					use_expression.get_data_type(), use_expression, Integer.valueOf(1));
			infections.put(mutations.expr_error(use_expression, muta_expression), 
					mutations.expression_constraint(use_expression.statement_of(), use_expression, true));
		}
	}
	private void post_dec_to_prev_inc(CirMutations mutations, CirTree cir_tree, AstMutation mutation, 
			Map<CirStateError, CirConstraint> infections) throws Exception {
		CirAssignStatement inc_statement = (CirAssignStatement) this.get_cir_node(
				cir_tree, mutation.get_location(), CirIncreAssignStatement.class);
		CirComputeExpression inc_expression = (CirComputeExpression) inc_statement.get_rvalue();
		SymExpression muta_expression = SymFactory.
				arith_add(inc_expression.get_data_type(), inc_expression, Integer.valueOf(2));
		infections.put(mutations.expr_error(inc_expression, muta_expression), 
				mutations.expression_constraint(inc_statement, Boolean.TRUE, true));
		
		CirExpression use_expression = this.get_cir_expression(cir_tree, mutation.get_location());
		if(use_expression != null && use_expression.statement_of() != null) {
			muta_expression = SymFactory.arith_add(
					use_expression.get_data_type(), use_expression, Integer.valueOf(1));
			infections.put(mutations.expr_error(use_expression, muta_expression), 
					mutations.expression_constraint(use_expression.statement_of(), use_expression, true));
		}
	}
	
	@Override
	protected void generate_infections(CirMutations mutations, CirTree cir_tree, CirStatement statement,
			AstMutation mutation, Map<CirStateError, CirConstraint> infections) throws Exception {
		switch(mutation.get_operator()) {
		case prev_inc_to_prev_dec:	this.prev_inc_to_prev_dec(mutations, cir_tree, mutation, infections); break;
		case prev_inc_to_post_inc:	this.prev_inc_to_post_inc(mutations, cir_tree, mutation, infections); break;
		case prev_inc_to_post_dec:	this.prev_inc_to_post_dec(mutations, cir_tree, mutation, infections); break;
		case prev_dec_to_prev_inc:	this.prev_dec_to_prev_inc(mutations, cir_tree, mutation, infections); break;
		case prev_dec_to_post_dec:	this.prev_dec_to_post_dec(mutations, cir_tree, mutation, infections); break;
		case prev_dec_to_post_inc:	this.prev_dec_to_post_inc(mutations, cir_tree, mutation, infections); break;
		case post_inc_to_post_dec:	this.post_inc_to_post_dec(mutations, cir_tree, mutation, infections); break;
		case post_inc_to_prev_inc:	this.post_inc_to_prev_inc(mutations, cir_tree, mutation, infections); break;
		case post_inc_to_prev_dec:	this.post_inc_to_prev_dec(mutations, cir_tree, mutation, infections); break;
		case post_dec_to_post_inc:	this.post_dec_to_post_inc(mutations, cir_tree, mutation, infections); break;
		case post_dec_to_prev_dec:	this.post_dec_to_prev_dec(mutations, cir_tree, mutation, infections); break;
		case post_dec_to_prev_inc:	this.post_dec_to_prev_inc(mutations, cir_tree, mutation, infections); break;
		default: throw new IllegalArgumentException("Invalid operator: " + mutation.get_operator());
		}
	}
	
}
