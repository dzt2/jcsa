package com.jcsa.jcmutest.mutant.sec2mutant.util.prog.unay;

import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecFactory;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecStateError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.cons.SecConstraint;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecAddExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecInsExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecSetExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecUnyExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.util.prog.SecExpressionPropagator;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;

public class SecLogicNotPropagator extends SecExpressionPropagator {
	
	@Override
	protected void propagate_set_expression(SecSetExpressionError error) throws Exception {
		SymExpression muta_operand = error.get_muta_expression().get_expression();
		SymExpression muta_expression = SecFactory.sym_condition(muta_operand, false);
		SecStateError target_error = this.set_expression(muta_expression);
		this.append_propagation_pair(this.condition_constraint(), target_error);
	}
	
	@Override
	protected void propagate_add_expression(SecAddExpressionError error) throws Exception {
		CType type = error.get_orig_expression().get_expression().get_data_type();
		SymExpression loperand = error.get_orig_expression().get_expression();
		SymExpression roperand = error.get_operand().get_expression();
		COperator operator = error.get_operator().get_operator();
		SecStateError target_error; SymExpression muta_operand, muta_expression;
		SecConstraint constraint = this.condition_constraint();
		
		switch(operator) {
		case arith_add:
		{
			muta_operand = SymFactory.arith_add(type, loperand, roperand);
			muta_expression = SecFactory.sym_condition(muta_operand, false);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case arith_sub:
		{
			muta_operand = SymFactory.arith_sub(type, loperand, roperand);
			muta_expression = SecFactory.sym_condition(muta_operand, false);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case arith_mul:
		{
			constraint = this.conjunct_constraints(
					SymFactory.not_equals(loperand, Integer.valueOf(0)), 
					SymFactory.equal_with(roperand, Integer.valueOf(0)));
			target_error = this.set_expression(Boolean.TRUE);
			break;
		}
		case arith_div:
		{
			constraint = this.condition_constraint(SymFactory.
					not_equals(loperand, Integer.valueOf(0)));
			muta_operand = SymFactory.arith_div(type, loperand, roperand);
			muta_expression = SecFactory.sym_condition(muta_operand, false);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case arith_mod:
		{
			constraint = this.condition_constraint(SymFactory.
					not_equals(loperand, Integer.valueOf(0)));
			muta_operand = SymFactory.arith_mod(type, loperand, roperand);
			muta_expression = SecFactory.sym_condition(muta_operand, false);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case bit_and:
		{
			constraint = this.conjunct_constraints(
					SymFactory.not_equals(loperand, Integer.valueOf(0)), 
					SymFactory.equal_with(roperand, Integer.valueOf(0)));
			target_error = this.set_expression(Boolean.TRUE);
			break;
		}
		case bit_or:
		{
			constraint = this.conjunct_constraints(
					SymFactory.equal_with(loperand, Integer.valueOf(0)), 
					SymFactory.not_equals(roperand, Integer.valueOf(0)));
			target_error = this.set_expression(Boolean.FALSE);
			break;
		}
		case bit_xor:
		{
			constraint = this.conjunct_constraints(
					SymFactory.equal_with(loperand, Integer.valueOf(0)), 
					SymFactory.not_equals(roperand, Integer.valueOf(0)));
			target_error = this.set_expression(Boolean.FALSE);
			this.append_propagation_pair(constraint, target_error);
			
			constraint = this.conjunct_constraints(
					SymFactory.not_equals(loperand, Integer.valueOf(0)), 
					SymFactory.equal_with(loperand, roperand));
			target_error = this.set_expression(Boolean.TRUE);
			break;
		}
		case left_shift:
		{
			constraint = this.condition_constraint(
					SymFactory.not_equals(loperand, Integer.valueOf(0)));
			muta_operand = SymFactory.bitws_lsh(type, loperand, roperand);
			muta_expression = SecFactory.sym_condition(muta_operand, false);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case righ_shift:
		{
			constraint = this.condition_constraint(
					SymFactory.not_equals(loperand, Integer.valueOf(0)));
			muta_operand = SymFactory.bitws_rsh(type, loperand, roperand);
			muta_expression = SecFactory.sym_condition(muta_operand, false);
			target_error = this.set_expression(muta_expression);
			break;
		}
		default: throw new IllegalArgumentException(operator.toString());
		}
		
		this.append_propagation_pair(constraint, target_error);
	}
	
	@Override
	protected void propagate_ins_expression(SecInsExpressionError error) throws Exception {
		CType type = error.get_orig_expression().get_expression().get_data_type();
		SymExpression loperand = error.get_orig_expression().get_expression();
		SymExpression roperand = error.get_operand().get_expression();
		COperator operator = error.get_operator().get_operator();
		SecStateError target_error; SymExpression muta_operand, muta_expression;
		
		SecConstraint constraint = this.condition_constraint();
		switch(operator) {
		case arith_sub:
		{
			muta_operand = SymFactory.arith_sub(type, roperand, loperand);
			muta_expression = SecFactory.sym_condition(muta_operand, false);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case arith_div:
		{
			constraint = this.condition_constraint(
					SymFactory.not_equals(loperand, Integer.valueOf(0)));
			muta_operand = SymFactory.arith_div(type, roperand, loperand);
			muta_expression = SecFactory.sym_condition(muta_operand, false);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case arith_mod:
		{
			constraint = this.condition_constraint(
					SymFactory.not_equals(loperand, Integer.valueOf(0)));
			muta_operand = SymFactory.arith_mod(type, roperand, loperand);
			muta_expression = SecFactory.sym_condition(muta_operand, false);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case left_shift:
		{
			constraint = this.condition_constraint(
					SymFactory.not_equals(loperand, Integer.valueOf(0)));
			muta_operand = SymFactory.bitws_lsh(type, roperand, loperand);
			muta_expression = SecFactory.sym_condition(muta_operand, false);
			target_error = this.set_expression(muta_expression);
			break;
		}
		case righ_shift:
		{
			constraint = this.condition_constraint(
					SymFactory.not_equals(loperand, Integer.valueOf(0)));
			muta_operand = SymFactory.bitws_rsh(type, roperand, loperand);
			muta_expression = SecFactory.sym_condition(muta_operand, false);
			target_error = this.set_expression(muta_expression);
			break;
		}
		default: throw new IllegalArgumentException(operator.toString());
		}
		this.append_propagation_pair(constraint, target_error);
	}

	@Override
	protected void propagate_uny_expression(SecUnyExpressionError error) throws Exception {
		/* declarations */
		SymExpression operand = error.get_orig_expression().get_expression();
		COperator operator = error.get_operator().get_operator();
		SecStateError target_error; 
		SecConstraint constraint = this.condition_constraint();
		switch(operator) {
		case negative:
		{
			target_error = this.none_statement();
			break;
		}
		case bit_not:
		{
			constraint = this.condition_constraint(SymFactory.
					equal_with(operand, Integer.valueOf(0)));
			target_error = this.set_expression(Boolean.FALSE);
			break;
		}
		case logic_not:
		{
			target_error = this.uny_expression(COperator.logic_not);
			break;
		}
		default: throw new IllegalArgumentException(operator.toString());
		}
		this.append_propagation_pair(constraint, target_error);
	}

}
