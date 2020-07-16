package com.jcsa.jcparse.lang.sym;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * {+, -, ~, !, &, *, assign}
 * @author yukimula
 *
 */
public class SymUnaryExpression extends SymExpression {
	
	private COperator operator;
	
	protected SymUnaryExpression(CType data_type, COperator operator) {
		super(data_type);
		this.operator = operator;
	}
	
	/**
	 * @return {positive, negative, ~, !, &, *, assign}
	 */
	public COperator get_operator() {
		return this.operator;
	}
	
	/**
	 * @return the only operand in the expression
	 */
	public SymExpression get_operand() {
		return (SymExpression) this.get_child(0);
	}

	@Override
	protected SymNode new_self() {
		return new SymUnaryExpression(this.get_data_type(), this.operator);
	}

	@Override
	protected String generate_code(boolean ast_style) throws Exception {
		String operand = this.get_operand().generate_code(ast_style);
		operand = "(" + operand + ")";
		switch(this.operator) {
		case positive:		return operand;
		case negative:		return "-" + operand;
		case bit_not:		return "~" + operand;
		case logic_not:		return "!" + operand;
		case address_of:	return "&" + operand;
		case dereference:	return "*" + operand;
		case assign:		return "(" + this.get_data_type().generate_code() + ") " + operand;
		default: throw new IllegalArgumentException("Invalid operator");
		}
	}
	
}
