package com.jcsa.jcmutest.mutant.sad2mutant.lang;

import com.jcsa.jcmutest.mutant.sad2mutant.SadNode;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * bin_expression |-- expression {-,/,%,<<,>>,<,<=,>,>=,==,!=} expression
 * @author yukimula
 *
 */
public class SadBinaryExpression extends SadExpression {

	private COperator operator;
	protected SadBinaryExpression(CirNode source, CType data_type, COperator operator) {
		super(source, data_type);
		this.operator = operator;
	}
	
	/**
	 * @return {-,/,%,<<,>>,<,<=,>,>=,==,!=}
	 */
	public COperator get_operator() { return this.operator; }
	/**
	 * @return the left-operand
	 */
	public SadExpression get_loperand() {
		return (SadExpression) this.get_child(0);
	}
	/**
	 * @return the right-operand
	 */
	public SadExpression get_roperand() {
		return (SadExpression) this.get_child(1);
	}
	
	@Override
	public String generate_code() throws Exception {
		String opcode;
		switch(this.operator) {
		case arith_sub:		opcode = "-";	break;
		case arith_div:		opcode = "/";	break;
		case arith_mod:		opcode = "%";	break;
		case left_shift:	opcode = "<<";	break;
		case righ_shift:	opcode = ">>";	break;
		case greater_tn:	opcode = ">";	break;
		case greater_eq:	opcode = ">=";	break;
		case smaller_tn:	opcode = "<";	break;
		case smaller_eq:	opcode = "<=";	break;
		case equal_with:	opcode = "==";	break;
		case not_equals:	opcode = "!=";	break;
		default: throw new IllegalArgumentException("Invalid: " + this.operator);
		}
		return "(" + this.get_loperand().generate_code() + ") "
				+ opcode +
				"(" + this.get_roperand().generate_code() + ")";
	}
	
	@Override
	protected SadNode clone_self() {
		return new SadBinaryExpression(this.get_cir_source(), 
				this.get_data_type(), this.operator);
	}

}
