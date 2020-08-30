package com.jcsa.jcmutest.mutant.sed2mutant.lang.expr;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.lexical.CConstant;

public class SedConstant extends SedBasicExpression {
	
	private CConstant constant;
	public SedConstant(CirNode cir_source, 
			CType data_type, CConstant constant) {
		super(cir_source, data_type);
		this.constant = constant;
	}
	
	/**
	 * @return the constant that the expression defines
	 */
	public CConstant get_constant() { return this.constant; }

	@Override
	protected SedNode clone_self() {
		return new SedConstant(this.get_cir_source(), 
				this.get_data_type(), this.constant);
	}

	@Override
	public String generate_code() throws Exception {
		switch(this.constant.get_type().get_tag()) {
		case c_bool:	return constant.get_bool().toString();
		case c_char:
		case c_uchar:	return "" + ((int) constant.get_char().charValue());
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:	return constant.get_integer().toString();
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:	return constant.get_long().toString();
		case c_float:	return constant.get_float().toString();
		case c_double:
		case c_ldouble:	return constant.get_double().toString();
		default: 		return "[?]";
		}
	}
	
}
