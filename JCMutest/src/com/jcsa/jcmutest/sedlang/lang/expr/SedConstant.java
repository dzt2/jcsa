package com.jcsa.jcmutest.sedlang.lang.expr;

import com.jcsa.jcmutest.sedlang.lang.SedNode;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.lexical.CConstant;

public class SedConstant extends SedBasicExpression {

	private CConstant constant;
	public SedConstant(CirExpression cir_expression, CType 
			data_type, CConstant constant) throws Exception {
		super(cir_expression, data_type);
		if(constant == null)
			throw new IllegalArgumentException("Invalid constant");
		else
			this.constant = constant;
	}
	
	/**
	 * @return the constant of the expression
	 */
	public CConstant get_constant() { return this.constant; }

	@Override
	public String generate_code() throws Exception {
		switch(this.constant.get_type().get_tag()) {
		case c_bool:	return this.constant.get_bool().toString();
		case c_char:
		case c_uchar:	return "" + ((int) constant.get_char().charValue());
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:	return this.constant.get_integer().toString();
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:	return this.constant.get_long().toString();
		case c_float:	return this.constant.get_float().toString();
		case c_double:
		case c_ldouble:	return this.constant.get_double().toString();
		default: throw new IllegalArgumentException("Invalid constant");
		}
	}

	@Override
	protected SedNode construct() throws Exception {
		return new SedConstant(this.get_cir_expression(), 
				this.get_data_type(), this.constant);
	}
	
}
