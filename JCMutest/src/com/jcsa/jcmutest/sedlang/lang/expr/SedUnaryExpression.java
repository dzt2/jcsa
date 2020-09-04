package com.jcsa.jcmutest.sedlang.lang.expr;

import com.jcsa.jcmutest.sedlang.lang.SedNode;
import com.jcsa.jcmutest.sedlang.lang.token.SedOperator;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

public class SedUnaryExpression extends SedExpression {

	public SedUnaryExpression(CirExpression cir_expression, 
			CType data_type, COperator operator) throws Exception {
		super(cir_expression, data_type);
		if(operator == null)
			throw new IllegalArgumentException("Invalid operator");
		switch(operator) {
		case positive:
		case negative:
		case bit_not:
		case logic_not:
		case address_of:
		case dereference:
		case assign:	this.add_child(new SedOperator(operator)); break;
		default: throw new IllegalArgumentException("Invalid operator");
		}
		
	}
	
	/**
	 * @return the unary operator
	 */
	public SedOperator get_operator() {
		return (SedOperator) this.get_child(0);
	}
	
	/**
	 * @return the unary operand
	 */
	public SedExpression get_operand() {
		return (SedExpression) this.get_child(1);
	}
	
	@Override
	public String generate_code() throws Exception {
		return this.get_operator().generate_code() + "(" 
				+ this.get_operand().generate_code() + ")";
	}
	
	@Override
	protected SedNode construct() throws Exception {
		return new SedUnaryExpression(
				this.get_cir_expression(), 
				this.get_data_type(), 
				this.get_operator().get_operator());
	}
	
}
