package com.jcsa.jcmutest.sedlang;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * unary_expression |-- operator expression
 * @author yukimula
 *
 */
public class SedUnaryExpression extends SedExpression {
	
	protected SedUnaryExpression(CirNode source, CType data_type) {
		super(source, data_type);
	}
	
	/* getters */
	/**
	 * @return the unary operator 
	 */
	public SedOperator get_operator() { return (SedOperator) this.get_child(0); }
	/**
	 * @return the unary operand
	 */
	public SedExpression get_operand() { return (SedExpression) this.get_child(1); }
	
	@Override
	protected SedNode copy_self() {
		return new SedUnaryExpression(this.get_source(), this.get_data_type());
	}
	
	@Override
	protected String generate_code() throws Exception {
		return "(" + this.get_operator().generate_code() + " " + 
						this.get_operand().generate_code() + ")";
	}
	
}
