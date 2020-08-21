package com.jcsa.jcmutest.sedlang.lang;

import com.jcsa.jcmutest.sedlang.SedNode;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * binary_expression |-- operator expression expression
 * @author yukimula
 *
 */
public class SedBinaryExpression extends SedExpression {
	
	/* definitions */
	protected SedBinaryExpression(CirNode source, CType data_type) {
		super(source, data_type);
	}
	/**
	 * @return {-,/,%,<<,>>,<,<=,>,>=,==,!=}
	 */
	public SedOperator get_operator() { return (SedOperator) this.get_child(0); }
	/**
	 * @return left-operand
	 */
	public SedExpression get_loperand() { return (SedExpression) this.get_child(1); }
	/**
	 * @return right-operand
	 */
	public SedExpression get_roperand() { return (SedExpression) this.get_child(2); }
	
	/* implementation */
	@Override
	protected SedNode copy_self() {
		return new SedBinaryExpression(this.get_source(), this.get_data_type());
	}
	@Override
	public String generate_code() throws Exception {
		return "(" + this.get_operator().generate_code() 
				+ " " + this.get_loperand().generate_code()
				+ " " + this.get_roperand().generate_code() + ")";
	}
	
}
