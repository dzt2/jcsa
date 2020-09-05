package com.jcsa.jcmutest.selang.lang.expr;

import com.jcsa.jcmutest.selang.lang.SedNode;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;

public class SedDefaultValue extends SedBasicExpression {
	
	public static final String AnyBoolean = "@ANYBOOL";
	public static final String AnyCharacter = "@ANYCHAR";
	public static final String AnyInteger = "@ANYINT";
	public static final String AnyDouble = "@ANYREAL";
	public static final String AnyPosInteger = "@ANYPIN";
	public static final String AnyNegInteger = "@ANYNIN";
	public static final String AnyPosDouble = "@ANYPRE";
	public static final String AnyNegDouble = "@ANYNRE";
	public static final String AnySequence = "@ANYSEQ";
	public static final String AnyAddress = "@ANYADDR";

	private String name;
	public SedDefaultValue(CirExpression cir_expression, 
			CType data_type, String name) throws Exception {
		super(cir_expression, data_type);
		this.name = name;
	}
	
	/**
	 * @return the name of the default-value
	 */
	public String get_name() {
		return this.name;
	}

	@Override
	public String generate_code() throws Exception {
		return this.name;
	}

	@Override
	protected SedNode construct() throws Exception {
		return new SedDefaultValue(this.get_cir_expression(), 
				this.get_data_type(), this.name);
	}
	
}
