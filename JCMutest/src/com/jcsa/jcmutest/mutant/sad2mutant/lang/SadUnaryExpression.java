package com.jcsa.jcmutest.mutant.sad2mutant.lang;

import com.jcsa.jcmutest.mutant.sad2mutant.SadNode;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * unary_expression |-- {+,-,~,!,&,*,cast} expression
 * @author yukimula
 *
 */
public class SadUnaryExpression extends SadExpression {
	
	protected SadUnaryExpression(CirNode source, CType data_type, COperator operator) {
		super(source, data_type);
		this.add_child(new SadOperator(operator));
	}
	
	/**
	 * @return {+,-,~,!,&,*,cast}
	 */
	public SadOperator get_operator() { return (SadOperator) this.get_child(0); }
	/**
	 * @return the operand under the unary expression
	 */
	public SadExpression get_operand() { return (SadExpression) this.get_child(1); }

	@Override
	public String generate_code() throws Exception {
		String operand = this.get_operand().generate_code();
		switch(this.get_operator().get_operator()) {
		case positive:		return operand;
		case negative:		return "-(" + operand + ")";
		case bit_not:		return "~(" + operand + ")";
		case logic_not:		return "!(" + operand + ")";
		case address_of:	return "&(" + operand + ")";
		case dereference:	return "*(" + operand + ")";
		case assign:		return "@(" + operand + ")";
		default: throw new IllegalArgumentException("Invalid: " + this.get_operator());
		}
	}

	@Override
	protected SadNode clone_self() {
		return new SadUnaryExpression(this.get_cir_source(), this.
				get_data_type(), this.get_operator().get_operator());
	}

}
