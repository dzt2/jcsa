package com.jcsa.jcmutest.mutant.sec2mutant.lang.refs;

import com.jcsa.jcmutest.mutant.sec2mutant.SecKeywords;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecFactory;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecDescription;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.token.SecExpression;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.token.SecOperator;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.sym.SymComputation;
import com.jcsa.jcparse.lang.sym.SymConstant;
import com.jcsa.jcparse.lang.sym.SymContexts;
import com.jcsa.jcparse.lang.sym.SymEvaluator;
import com.jcsa.jcparse.lang.sym.SymExpression;

public class SecAddReferenceError extends SecReferenceError {

	public SecAddReferenceError(CirStatement statement, 
			CirExpression orig_expression, COperator 
			operator, SymExpression operand) throws Exception {
		super(statement, SecKeywords.add_refr, orig_expression);
		switch(operator) {
		case arith_add:
		case arith_sub:
		case arith_mul:
		case arith_div:
		case arith_mod:
		case bit_and:
		case bit_or:
		case bit_xor:
		case left_shift:
		case righ_shift:
		{
			this.add_child(new SecOperator(operator));
			this.add_child(new SecExpression(operand));
			break;
		}
		default: throw new IllegalArgumentException(operator.toString());
		}
	}
	
	public SecOperator get_operator() { return (SecOperator) this.get_child(3); }
	
	public SecExpression get_operand() { return (SecExpression) this.get_child(4); }

	@Override
	protected String generate_content() throws Exception {
		return "(" + this.get_orig_reference().generate_code() + ", "
				+ this.get_operator().generate_code() + ", "
				+ this.get_operand().generate_code() + ")";
	}

	/**
	 * @param operand
	 * @param value
	 * @return whether operand must-be equal with the value
	 * @throws Exception
	 */
	private boolean compare_constant(SymExpression operand, boolean value) throws Exception {
		if(operand instanceof SymConstant) {
			return ((SymConstant) operand).get_bool() == value;
		}
		else {
			return false;
		}
	}
	
	/**
	 * @param operand
	 * @param value
	 * @return whether operand must-be equal with the value
	 * @throws Exception
	 */
	private boolean compare_constant(SymExpression operand, long value) throws Exception {
		if(operand instanceof SymConstant) {
			return SymComputation.compare((SymConstant) operand, value);
		}
		else {
			return false;
		}
	}
	
	public SecDescription optimize(SymContexts contexts) throws Exception {
		SymExpression operand = SymEvaluator.evaluate_on(
				this.get_operand().get_expression(), contexts);
		COperator operator = this.get_operator().get_operator();
		CirStatement statement = this.get_location().get_statement();
		CirExpression orig_expression = this.
				get_orig_reference().get_expression().get_cir_source();
		switch(operator) {
		case arith_add:
		case arith_sub:
		{
			if(this.compare_constant(operand, 0)) {
				return SecFactory.pass_statement(statement);
			}
			else {
				return SecFactory.add_reference(statement, orig_expression, operator, operand);
			}
		}
		case arith_mul:
		case arith_div:
		{
			if(this.compare_constant(operand, 1)) {
				return SecFactory.pass_statement(statement);
			}
			else {
				return SecFactory.add_reference(statement, orig_expression, operator, operand);
			}
		}
		case arith_mod:
		{
			return SecFactory.add_reference(statement, orig_expression, operator, operand);
		}
		case bit_and:
		{
			if(this.compare_constant(operand, ~0L)) {
				return SecFactory.pass_statement(statement);
			}
			else {
				return SecFactory.add_reference(statement, orig_expression, operator, operand);
			}
		}
		case bit_or:
		case bit_xor:
		case left_shift:
		case righ_shift:
		{
			if(this.compare_constant(operand, 0)) {
				return SecFactory.pass_statement(statement);
			}
			else {
				return SecFactory.add_reference(statement, orig_expression, operator, operand);
			}
		}
		case logic_and:
		{
			if(this.compare_constant(operand, true)) {
				return SecFactory.pass_statement(statement);
			}
			else {
				return SecFactory.add_reference(statement, orig_expression, operator, operand);
			}
		}
		case logic_or:
		{
			if(this.compare_constant(operand, false)) {
				return SecFactory.pass_statement(statement);
			}
			else {
				return SecFactory.add_reference(statement, orig_expression, operator, operand);
			}
		}
		default: throw new IllegalArgumentException(operator.toString());
		}
	}
	
}
