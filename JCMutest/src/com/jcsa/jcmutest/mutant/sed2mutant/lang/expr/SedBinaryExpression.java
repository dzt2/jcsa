package com.jcsa.jcmutest.mutant.sed2mutant.lang.expr;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.token.SedOperator;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * SadBinaryExpression	{+, -, *, /, %, &, |, ^, <<, >>, &&, ||, <, <=, >, >=, ==, !=}
 * @author yukimula
 *
 */
public class SedBinaryExpression extends SedExpression {

	public SedBinaryExpression(CirNode cir_source, 
			CType data_type, COperator operator) {
		super(cir_source, data_type);
		this.add_child(new SedOperator(operator));
	}
	
	/**
	 * @return the binary operator
	 */
	public SedOperator get_operator() {
		return (SedOperator) this.get_child(0);
	}
	/**
	 * @return the left-operand
	 */
	public SedExpression get_loperand() {
		return (SedExpression) this.get_child(1);
	}
	/**
	 * @return the right-operand
	 */
	public SedExpression get_roperand() {
		return (SedExpression) this.get_child(2);
	}

	@Override
	protected SedNode clone_self() {
		return new SedBinaryExpression(this.get_cir_source(), this.
				get_data_type(), this.get_operator().get_operator());
	}

	@Override
	public String generate_code() throws Exception {
		return "(" + this.get_loperand().generate_code() + ")"
				+ " " + this.get_operator().generate_code() + " "
				+ "(" + this.get_roperand().generate_code() + ")";
	}

}
