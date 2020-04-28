package com.jcsa.jcparse.lang.symbol.impl;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symbol.SymComputeExpression;
import com.jcsa.jcparse.lang.symbol.SymExpression;
import com.jcsa.jcparse.lang.symbol.SymNode;

public class SymComputeExpressionImpl extends SymExpressionImpl implements SymComputeExpression {

	private COperator operator;
	protected SymComputeExpressionImpl(CType data_type, COperator operator) throws IllegalArgumentException {
		super(data_type);
		if(operator == null)
			throw new IllegalArgumentException("Invalid operator: null");
		else { this.operator = operator; }
	}
	@Override
	public COperator get_operator() { return this.operator; }
	@Override
	public int number_of_operands() { return this.number_of_children(); }
	@Override
	public SymExpression get_operand(int k) throws IndexOutOfBoundsException {
		return (SymExpression) this.get_child(k);
	}
	@Override
	public void add_operand(SymExpression operand) throws IllegalArgumentException {
		this.add_child((SymNodeImpl) operand);
	}
	
	private String operator_code() {
		switch(this.operator) {
		case positive:		return "pos";
		case negative:		return "neg";
		case bit_not:		return "~";
		case address_of:	return "adr";
		case logic_not:		return "!";
		case arith_add:		return "+";
		case arith_sub:		return "-";
		case arith_mul:		return "*";
		case arith_div:		return "/";
		case arith_mod:		return "%";
		case bit_and:		return "&";
		case bit_or:		return "|";
		case bit_xor:		return "^";
		case left_shift:	return "<<";
		case righ_shift:	return ">>";
		case greater_tn:	return ">";
		case greater_eq:	return ">=";
		case smaller_tn:	return "<";
		case smaller_eq:	return "<=";
		case equal_with:	return "==";
		case not_equals:	return "!=";
		case logic_and:		return "&&";
		case logic_or:		return "||";
		default: throw new IllegalArgumentException("Invalid operator: " + this.operator);
		}
	}
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		
		buffer.append("(");
		buffer.append(this.operator_code());
		for(SymNode operand : this.get_children()) {
			buffer.append(" ");
			buffer.append(operand.toString());
		}
		buffer.append(")");
		
		return buffer.toString();
	}
	
}
