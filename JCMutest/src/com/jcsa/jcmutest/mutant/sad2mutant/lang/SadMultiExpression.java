package com.jcsa.jcmutest.mutant.sad2mutant.lang;

import com.jcsa.jcmutest.mutant.sad2mutant.SadNode;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * multi_expression |-- {+,*,&,|,^,&&,||} expression+
 * @author yukimula
 *
 */
public class SadMultiExpression extends SadExpression {

	private COperator operator;
	protected SadMultiExpression(CirNode source, CType data_type, COperator operator) {
		super(source, data_type);
		this.operator = operator;
	}
	
	/* getters */
	/**
	 * @return {+,*,&,|,^,&&,||}
	 */
	public COperator get_operator() { return this.operator; }
	/**
	 * @return the number of operands within the expression
	 */
	public int number_of_operands() { return this.number_of_children(); }
	/**
	 * @param k
	 * @return the kth operand under the expression
	 * @throws IndexOutOfBoundsException
	 */
	public SadExpression get_operand(int k) throws IndexOutOfBoundsException {
		return (SadExpression) this.get_child(k);
	}
	
	@Override
	public String generate_code() throws Exception {
		String opcode;
		switch(this.operator) {
		case arith_add:		opcode = " + ";		break;
		case arith_mul:		opcode = " * ";		break;
		case bit_and:		opcode = " & ";		break;
		case bit_or:		opcode = " | ";		break;
		case bit_xor:		opcode = " ^ ";		break;
		case logic_and:		opcode = " && ";	break;
		case logic_or:		opcode = " || ";	break;
		default: throw new IllegalArgumentException("Invalid: " + this.operator);
		}
		
		StringBuilder buffer = new StringBuilder();
		for(int k = 0; k < this.number_of_operands(); k++) {
			buffer.append("(");
			buffer.append(this.get_operand(k).generate_code());
			buffer.append(")");
			if(k < this.number_of_operands() - 1) {
				buffer.append(opcode);
			}
		}
		return buffer.toString();
	}
	
	@Override
	protected SadNode clone_self() {
		return new SadMultiExpression(this.get_cir_source(), 
					this.get_data_type(), this.operator);
	}
	
}
