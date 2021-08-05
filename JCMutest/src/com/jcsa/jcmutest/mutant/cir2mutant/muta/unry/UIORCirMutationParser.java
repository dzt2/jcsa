package com.jcsa.jcmutest.mutant.cir2mutant.muta.unry;

import java.util.Map;

import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAttribute;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.CirMutationParser;
import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirComputeExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIncreAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class UIORCirMutationParser extends CirMutationParser {

	@Override
	protected CirStatement get_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return this.get_end_statement(cir_tree, mutation.get_location());
	}

	private void prev_inc_to_prev_dec(CirTree cir_tree, AstMutation mutation,
			Map<CirAttribute, CirAttribute> infections) throws Exception {
		CirAssignStatement inc_statement = (CirAssignStatement) this.get_cir_node(
				cir_tree, mutation.get_location(), CirIncreAssignStatement.class);
		CirComputeExpression inc_expression = (CirComputeExpression) inc_statement.get_rvalue();
		SymbolExpression muta_expression = SymbolFactory.
				arith_sub(inc_expression.get_data_type(), inc_expression, Integer.valueOf(2));
		infections.put(CirAttribute.new_value_error(inc_expression, muta_expression),
						CirAttribute.new_cover_count(inc_statement.execution_of(), 1));
	}
	private void prev_inc_to_post_inc(CirTree cir_tree, AstMutation mutation,
			Map<CirAttribute, CirAttribute> infections) throws Exception {
		CirExpression use_expression = this.get_cir_expression(cir_tree, mutation.get_location());
		if(use_expression != null && use_expression.statement_of() != null) {
			SymbolExpression muta_expression = SymbolFactory.arith_sub(
					use_expression.get_data_type(), use_expression, Integer.valueOf(1));
			infections.put(CirAttribute.new_value_error(use_expression, muta_expression),
							CirAttribute.new_cover_count(use_expression.execution_of(), 1));
		}
	}
	private void prev_inc_to_post_dec(CirTree cir_tree, AstMutation mutation,
			Map<CirAttribute, CirAttribute> infections) throws Exception {
		CirAssignStatement inc_statement = (CirAssignStatement) this.get_cir_node(
				cir_tree, mutation.get_location(), CirIncreAssignStatement.class);
		CirComputeExpression inc_expression = (CirComputeExpression) inc_statement.get_rvalue();
		SymbolExpression muta_expression = SymbolFactory.
				arith_sub(inc_expression.get_data_type(), inc_expression, Integer.valueOf(2));
		infections.put(CirAttribute.new_value_error(inc_expression, muta_expression),
						CirAttribute.new_cover_count(inc_statement.execution_of(), 1));

		CirExpression use_expression = this.get_cir_expression(cir_tree, mutation.get_location());
		if(use_expression != null && use_expression.statement_of() != null) {
			muta_expression = SymbolFactory.arith_add(
					use_expression.get_data_type(), use_expression, Integer.valueOf(1));
			infections.put(CirAttribute.new_value_error(use_expression, muta_expression),
							CirAttribute.new_cover_count(use_expression.execution_of(), 1));
		}
	}

	private void prev_dec_to_prev_inc(CirTree cir_tree, AstMutation mutation,
			Map<CirAttribute, CirAttribute> infections) throws Exception {
		CirAssignStatement inc_statement = (CirAssignStatement) this.get_cir_node(
				cir_tree, mutation.get_location(), CirIncreAssignStatement.class);
		CirComputeExpression inc_expression = (CirComputeExpression) inc_statement.get_rvalue();
		SymbolExpression muta_expression = SymbolFactory.
				arith_add(inc_expression.get_data_type(), inc_expression, Integer.valueOf(2));
		infections.put(CirAttribute.new_value_error(inc_expression, muta_expression),
				CirAttribute.new_cover_count(inc_statement.execution_of(), 1));
	}
	private void prev_dec_to_post_dec(CirTree cir_tree, AstMutation mutation,
			Map<CirAttribute, CirAttribute> infections) throws Exception {
		CirExpression use_expression = this.get_cir_expression(cir_tree, mutation.get_location());
		if(use_expression != null && use_expression.statement_of() != null) {
			SymbolExpression muta_expression = SymbolFactory.arith_add(
					use_expression.get_data_type(), use_expression, Integer.valueOf(1));
			infections.put(CirAttribute.new_value_error(use_expression, muta_expression),
					CirAttribute.new_cover_count(use_expression.execution_of(), 1));
		}
	}
	private void prev_dec_to_post_inc(CirTree cir_tree, AstMutation mutation,
			Map<CirAttribute, CirAttribute> infections) throws Exception {
		CirAssignStatement inc_statement = (CirAssignStatement) this.get_cir_node(
				cir_tree, mutation.get_location(), CirIncreAssignStatement.class);
		CirComputeExpression inc_expression = (CirComputeExpression) inc_statement.get_rvalue();
		SymbolExpression muta_expression = SymbolFactory.
				arith_add(inc_expression.get_data_type(), inc_expression, Integer.valueOf(2));
		infections.put(CirAttribute.new_value_error(inc_expression, muta_expression),
				CirAttribute.new_cover_count(inc_statement.execution_of(), 1));

		CirExpression use_expression = this.get_cir_expression(cir_tree, mutation.get_location());
		if(use_expression != null && use_expression.statement_of() != null) {
			muta_expression = SymbolFactory.arith_sub(
					use_expression.get_data_type(), use_expression, Integer.valueOf(1));
			infections.put(CirAttribute.new_value_error(use_expression, muta_expression),
					CirAttribute.new_cover_count(use_expression.execution_of(), 1));
		}
	}

	private void post_inc_to_post_dec(CirTree cir_tree, AstMutation mutation,
			Map<CirAttribute, CirAttribute> infections) throws Exception {
		CirAssignStatement inc_statement = (CirAssignStatement) this.get_cir_node(
				cir_tree, mutation.get_location(), CirIncreAssignStatement.class);
		CirComputeExpression inc_expression = (CirComputeExpression) inc_statement.get_rvalue();
		SymbolExpression muta_expression = SymbolFactory.
				arith_sub(inc_expression.get_data_type(), inc_expression, Integer.valueOf(2));
		infections.put(CirAttribute.new_value_error(inc_expression, muta_expression),
				CirAttribute.new_cover_count(inc_statement.execution_of(), 1));
	}
	private void post_inc_to_prev_inc(CirTree cir_tree, AstMutation mutation,
			Map<CirAttribute, CirAttribute> infections) throws Exception {
		CirExpression use_expression = this.get_cir_expression(cir_tree, mutation.get_location());
		if(use_expression != null && use_expression.statement_of() != null) {
			SymbolExpression muta_expression = SymbolFactory.arith_add(
					use_expression.get_data_type(), use_expression, Integer.valueOf(1));
			infections.put(CirAttribute.new_value_error(use_expression, muta_expression),
					CirAttribute.new_cover_count(use_expression.execution_of(), 1));
		}
	}
	private void post_inc_to_prev_dec(CirTree cir_tree, AstMutation mutation,
			Map<CirAttribute, CirAttribute> infections) throws Exception {
		CirAssignStatement inc_statement = (CirAssignStatement) this.get_cir_node(
				cir_tree, mutation.get_location(), CirIncreAssignStatement.class);
		CirComputeExpression inc_expression = (CirComputeExpression) inc_statement.get_rvalue();
		SymbolExpression muta_expression = SymbolFactory.
				arith_sub(inc_expression.get_data_type(), inc_expression, Integer.valueOf(2));
		infections.put(CirAttribute.new_value_error(inc_expression, muta_expression),
				CirAttribute.new_cover_count(inc_statement.execution_of(), 1));

		CirExpression use_expression = this.get_cir_expression(cir_tree, mutation.get_location());
		if(use_expression != null && use_expression.statement_of() != null) {
			muta_expression = SymbolFactory.arith_sub(
					use_expression.get_data_type(), use_expression, Integer.valueOf(1));
			infections.put(CirAttribute.new_value_error(use_expression, muta_expression),
					CirAttribute.new_cover_count(use_expression.execution_of(), 1));
		}
	}

	private void post_dec_to_post_inc(CirTree cir_tree, AstMutation mutation,
			Map<CirAttribute, CirAttribute> infections) throws Exception {
		CirAssignStatement inc_statement = (CirAssignStatement) this.get_cir_node(
				cir_tree, mutation.get_location(), CirIncreAssignStatement.class);
		CirComputeExpression inc_expression = (CirComputeExpression) inc_statement.get_rvalue();
		SymbolExpression muta_expression = SymbolFactory.
				arith_add(inc_expression.get_data_type(), inc_expression, Integer.valueOf(2));
		infections.put(CirAttribute.new_value_error(inc_expression, muta_expression),
				CirAttribute.new_cover_count(inc_statement.execution_of(), 1));
	}
	private void post_dec_to_prev_dec(CirTree cir_tree, AstMutation mutation,
			Map<CirAttribute, CirAttribute> infections) throws Exception {
		CirExpression use_expression = this.get_cir_expression(cir_tree, mutation.get_location());
		if(use_expression != null && use_expression.statement_of() != null) {
			SymbolExpression muta_expression = SymbolFactory.arith_sub(
					use_expression.get_data_type(), use_expression, Integer.valueOf(1));
			infections.put(CirAttribute.new_value_error(use_expression, muta_expression),
					CirAttribute.new_cover_count(use_expression.execution_of(), 1));
		}
	}
	private void post_dec_to_prev_inc(CirTree cir_tree, AstMutation mutation,
			Map<CirAttribute, CirAttribute> infections) throws Exception {
		CirAssignStatement inc_statement = (CirAssignStatement) this.get_cir_node(
				cir_tree, mutation.get_location(), CirIncreAssignStatement.class);
		CirComputeExpression inc_expression = (CirComputeExpression) inc_statement.get_rvalue();
		SymbolExpression muta_expression = SymbolFactory.
				arith_add(inc_expression.get_data_type(), inc_expression, Integer.valueOf(2));
		infections.put(CirAttribute.new_value_error(inc_expression, muta_expression),
				CirAttribute.new_cover_count(inc_statement.execution_of(), 1));

		CirExpression use_expression = this.get_cir_expression(cir_tree, mutation.get_location());
		if(use_expression != null && use_expression.statement_of() != null) {
			muta_expression = SymbolFactory.arith_add(
					use_expression.get_data_type(), use_expression, Integer.valueOf(1));
			infections.put(CirAttribute.new_value_error(use_expression, muta_expression),
					CirAttribute.new_cover_count(use_expression.execution_of(), 1));
		}
	}

	@Override
	protected void generate_infections(CirTree cir_tree, CirStatement statement,
			AstMutation mutation, Map<CirAttribute, CirAttribute> infections) throws Exception {
		switch(mutation.get_operator()) {
		case prev_inc_to_prev_dec:	this.prev_inc_to_prev_dec(cir_tree, mutation, infections); break;
		case prev_inc_to_post_inc:	this.prev_inc_to_post_inc(cir_tree, mutation, infections); break;
		case prev_inc_to_post_dec:	this.prev_inc_to_post_dec(cir_tree, mutation, infections); break;
		case prev_dec_to_prev_inc:	this.prev_dec_to_prev_inc(cir_tree, mutation, infections); break;
		case prev_dec_to_post_dec:	this.prev_dec_to_post_dec(cir_tree, mutation, infections); break;
		case prev_dec_to_post_inc:	this.prev_dec_to_post_inc(cir_tree, mutation, infections); break;
		case post_inc_to_post_dec:	this.post_inc_to_post_dec(cir_tree, mutation, infections); break;
		case post_inc_to_prev_inc:	this.post_inc_to_prev_inc(cir_tree, mutation, infections); break;
		case post_inc_to_prev_dec:	this.post_inc_to_prev_dec(cir_tree, mutation, infections); break;
		case post_dec_to_post_inc:	this.post_dec_to_post_inc(cir_tree, mutation, infections); break;
		case post_dec_to_prev_dec:	this.post_dec_to_prev_dec(cir_tree, mutation, infections); break;
		case post_dec_to_prev_inc:	this.post_dec_to_prev_inc(cir_tree, mutation, infections); break;
		default: throw new IllegalArgumentException("Invalid operator: " + mutation.get_operator());
		}
	}

}
