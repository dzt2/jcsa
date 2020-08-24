package com.jcsa.jcmutest.mutant.sad2mutant.lang;

import com.jcsa.jcmutest.mutant.sad2mutant.SadNode;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.lexical.CConstant;

/**
 * constant |-- {constant: CConstant}
 * @author yukimula
 *
 */
public class SadConstant extends SadBasicExpression {
	
	private CConstant constant;
	protected SadConstant(CirNode source, CType data_type, CConstant constant) {
		super(source, data_type);
		this.constant = constant;
	}
	
	/**
	 * @return the constant hold in this node
	 */
	public CConstant get_constant() { return this.constant; }

	@Override
	public String generate_code() throws Exception {
		switch(this.constant.get_type().get_tag()) {
		case c_bool:
			return this.constant.get_bool().toString();
		case c_char:
		case c_uchar:
			return "\'" + ((int) constant.get_char().charValue()) + "\'";
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:
			return this.constant.get_integer().toString();
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:
			return this.constant.get_long().toString();
		case c_float:
			return this.constant.get_float().toString();
		case c_double:
		case c_ldouble:
			return this.constant.get_double().toString();
		default: throw new IllegalArgumentException("Invalid type: " + constant);
		}
	}

	@Override
	protected SadNode clone_self() {
		return new SadConstant(this.get_cir_source(), 
				this.get_data_type(), this.constant);
	}
	
}
