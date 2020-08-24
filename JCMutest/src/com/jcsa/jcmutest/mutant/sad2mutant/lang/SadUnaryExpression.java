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
	
	private COperator operator;
	protected SadUnaryExpression(CirNode source, CType data_type, COperator operator) {
		super(source, data_type);
		this.operator = operator;
	}
	
	/**
	 * @return {+,-,~,!,&,*,cast}
	 */
	public COperator get_operator() { return this.operator; }
	/**
	 * @return the operand under the unary expression
	 */
	public SadExpression get_operand() { return (SadExpression) this.get_child(0); }

	@Override
	public String generate_code() throws Exception {
		String operand = this.get_operand().generate_code();
		switch(this.operator) {
		case positive:		return operand;
		case negative:		return "-(" + operand + ")";
		case bit_not:		return "~(" + operand + ")";
		case logic_not:		return "!(" + operand + ")";
		case address_of:	return "&(" + operand + ")";
		case dereference:	return "*(" + operand + ")";
		case assign:		return "@(" + operand + ")";
		default: throw new IllegalArgumentException("Invalid: " + this.operator);
		}
	}

	@Override
	protected SadNode clone_self() {
		return new SadUnaryExpression(this.get_cir_source(), 
				this.get_data_type(), this.operator);
	}

}
