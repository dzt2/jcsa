package com.jcsa.jcmutest.selang.lang.expr;

import com.jcsa.jcmutest.selang.lang.SedNode;
import com.jcsa.jcmutest.selang.lang.tokn.SedOperator;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

public class SedBinaryExpression extends SedExpression {

	public SedBinaryExpression(CirExpression cir_expression, 
			CType data_type, COperator operator) throws Exception {
		super(cir_expression, data_type);
		if(operator == null)
			throw new IllegalArgumentException("Invalid operator");
		else {
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
			case logic_and:
			case logic_or:
			case greater_tn:
			case greater_eq:
			case smaller_tn:
			case smaller_eq:
			case equal_with:
			case not_equals: this.add_child(new SedOperator(operator)); break;
			default: throw new IllegalArgumentException("Invalid operator");
			}
		}
	}
	
	/**
	 * @return binary operator
	 */
	public SedOperator get_operator() { 
		return (SedOperator) this.get_child(0); 
	}
	
	/**
	 * @return left-operand
	 */
	public SedExpression get_loperand() { 
		return (SedExpression) this.get_child(1); 
	}
	
	/**
	 * @return righ-operand
	 */
	public SedExpression get_roperand() { 
		return (SedExpression) this.get_child(2); 
	}

	@Override
	public String generate_code() throws Exception {
		return "(" + this.get_loperand().generate_code() + ")" + 
				" " + this.get_operator().generate_code() + " " +
				"(" + this.get_roperand().generate_code() + ")";
	}

	@Override
	protected SedNode construct() throws Exception {
		return new SedBinaryExpression(this.get_cir_expression(), 
				this.get_data_type(), this.get_operator().get_operator());
	}

}
