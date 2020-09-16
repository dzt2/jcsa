package com.jcsa.jcmutest.mutant.sec2mutant.util.prog;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecStateError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecAddExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecInsExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecSetExpressionError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.expr.SecUnyExpressionError;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirArgumentList;
import com.jcsa.jcparse.lang.irlang.stmt.CirCallStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;

public class SecArgumentsPropagator extends SecExpressionPropagator {
	
	private void propagate_via_call_wait(CirExpression argument, SymExpression muta_argument) throws Exception {
		if(argument.get_parent() instanceof CirArgumentList) {
			CirCallStatement call_stmt = 
					(CirCallStatement) argument.get_parent().get_parent();
			CirArgumentList alist = (CirArgumentList) argument.get_parent();
			
			SymExpression function = SymFactory.parse(call_stmt.get_function());
			List<Object> arguments = new ArrayList<Object>();
			for(int k = 0; k < alist.number_of_arguments(); k++) {
				if(alist.get_argument(k) != argument) {
					arguments.add(SymFactory.parse(alist.get_argument(k)));
				}
				else {
					arguments.add(muta_argument);
				}
			}
			
			SymExpression muta_expression = SymFactory.call_expression(
					this.target_expression().get_data_type(), function, arguments);
			SecStateError target_error = this.set_expression(muta_expression);
			this.append_propagation_pair(this.condition_constraint(), target_error);
		}
		else {
			throw new IllegalArgumentException("Not an argument");
		}
	}

	@Override
	protected void propagate_set_expression(SecSetExpressionError error) throws Exception {
		CirExpression argument = error.get_orig_expression().get_expression().get_cir_source();
		SymExpression muta_argument = error.get_muta_expression().get_expression();
		this.propagate_via_call_wait(argument, muta_argument);
	}

	@Override
	protected void propagate_add_expression(SecAddExpressionError error) throws Exception {
		SymExpression ori_operand = error.get_orig_expression().get_expression();
		SymExpression add_operand = error.get_operand().get_expression();
		COperator operator = error.get_operator().get_operator();
		CirExpression argument = ori_operand.get_cir_source();
		CType type = argument.get_data_type(); SymExpression muta_argument;
		
		switch(operator) {
		case arith_add:
		{
			muta_argument = SymFactory.arith_add(type, ori_operand, add_operand);
			break;
		}
		case arith_sub:
		{
			muta_argument = SymFactory.arith_sub(type, ori_operand, add_operand);
			break;
		}
		case arith_mul:
		{
			muta_argument = SymFactory.arith_mul(type, ori_operand, add_operand);
			break;
		}
		case arith_div:
		{
			muta_argument = SymFactory.arith_div(type, ori_operand, add_operand);
			break;
		}
		case arith_mod:
		{
			muta_argument = SymFactory.arith_mod(type, ori_operand, add_operand);
			break;
		}
		case bit_and:
		{
			muta_argument = SymFactory.bitws_and(type, ori_operand, add_operand);
			break;
		}
		case bit_or:
		{
			muta_argument = SymFactory.bitws_ior(type, ori_operand, add_operand);
			break;
		}
		case bit_xor:
		{
			muta_argument = SymFactory.bitws_xor(type, ori_operand, add_operand);
			break;
		}
		case left_shift:
		{
			muta_argument = SymFactory.bitws_lsh(type, ori_operand, add_operand);
			break;
		}
		case righ_shift:
		{
			muta_argument = SymFactory.bitws_rsh(type, ori_operand, add_operand);
			break;
		}
		default: throw new IllegalArgumentException(operator.toString());
		}
		
		this.propagate_via_call_wait(argument, muta_argument);
	}

	@Override
	protected void propagate_ins_expression(SecInsExpressionError error) throws Exception {
		SymExpression ori_operand = error.get_orig_expression().get_expression();
		SymExpression add_operand = error.get_operand().get_expression();
		COperator operator = error.get_operator().get_operator();
		CirExpression argument = ori_operand.get_cir_source();
		CType type = argument.get_data_type(); SymExpression muta_argument;
		
		switch(operator) {
		case arith_sub:
		{
			muta_argument = SymFactory.arith_sub(type, add_operand, ori_operand);
			break;
		}
		case arith_div:
		{
			muta_argument = SymFactory.arith_mul(type, add_operand, ori_operand);
			break;
		}
		case arith_mod:
		{
			muta_argument = SymFactory.arith_div(type, add_operand, ori_operand);
			break;
		}
		case left_shift:
		{
			muta_argument = SymFactory.bitws_lsh(type, add_operand, ori_operand);
			break;
		}
		case righ_shift:
		{
			muta_argument = SymFactory.bitws_rsh(type, add_operand, ori_operand);
			break;
		}
		default: throw new IllegalArgumentException(operator.toString());
		}
		
		this.propagate_via_call_wait(argument, muta_argument);
	}

	@Override
	protected void propagate_uny_expression(SecUnyExpressionError error) throws Exception {
		SymExpression operand = error.get_orig_expression().get_expression();
		COperator operator = error.get_operator().get_operator();
		CirExpression argument = operand.get_cir_source();
		CType type = argument.get_data_type(); SymExpression muta_argument;
		
		switch(operator) {
		case negative:
		{
			muta_argument = SymFactory.arith_neg(type, operand);
			break;
		}
		case bit_not:
		{
			muta_argument = SymFactory.bitws_rsv(type, operand);
			break;
		}
		case logic_not:
		{
			muta_argument = SymFactory.logic_not(operand);
			break;
		}
		default: throw new IllegalArgumentException(operator.toString());
		}
		
		this.propagate_via_call_wait(argument, muta_argument);
	}

}
