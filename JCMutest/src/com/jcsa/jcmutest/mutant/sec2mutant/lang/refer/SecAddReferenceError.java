package com.jcsa.jcmutest.mutant.sec2mutant.lang.refer;

import com.jcsa.jcmutest.mutant.sec2mutant.SecKeywords;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.token.SecExpression;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.token.SecOperator;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.token.SecType;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.sym.SymExpression;

public class SecAddReferenceError extends SecReferenceError {

	public SecAddReferenceError(CirStatement statement, CirExpression 
			orig_reference, COperator operator, SymExpression operand)
			throws Exception {
		super(statement, SecKeywords.add_reference, orig_reference);
		SecType type = this.get_orig_reference().get_type();
		switch(operator) {
		case arith_add:
		case arith_sub:
		{
			switch(type.get_vtype()) {
			case cchar:
			case csign:
			case usign:
			case creal:
			case caddr:	break;
			default: throw new IllegalArgumentException(type.generate_code());
			}
			break;
		}
		case arith_mul:
		case arith_div:
		{
			switch(type.get_vtype()) {
			case cchar:
			case csign:
			case usign:
			case creal:	break;
			default: throw new IllegalArgumentException(type.generate_code());
			}
			break;
		}
		case arith_mod:
		case bit_and:
		case bit_or:
		case bit_xor:
		case left_shift:
		case righ_shift:
		{
			switch(type.get_vtype()) {
			case cchar:
			case csign:
			case usign: break;
			default: throw new IllegalArgumentException(type.generate_code());
			}
			break;
		}
		default: throw new IllegalArgumentException(operator.toString());
		}
		this.add_child(new SecOperator(operator));
		this.add_child(new SecExpression(operand));
	}
	
	/**
	 * @return {+, -, *, /, %, &, |, ^, <<, >>}
	 */
	public SecOperator get_operator() { return (SecOperator) this.get_child(3); }
	
	/**
	 * @return operand being added behind the original expression
	 */
	public SecExpression get_operand() { return (SecExpression) this.get_child(4); }
	
	@Override
	protected String generate_content() throws Exception {
		return "(" + this.get_orig_reference().generate_code() +
				", " + this.get_operator().generate_code() + ", "
				+ this.get_operand().generate_code() + ")";
	}
	
}
