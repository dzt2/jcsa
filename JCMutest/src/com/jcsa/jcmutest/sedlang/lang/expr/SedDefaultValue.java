package com.jcsa.jcmutest.sedlang.lang.expr;

import com.jcsa.jcmutest.sedlang.lang.SedNode;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;

public class SedDefaultValue extends SedBasicExpression {
	
	public static final String AnyBoolean = "@ANYBOOL";
	public static final String AnyCharacter = "@AMYCHAR";
	public static final String AnyNumeric = "@ANYNUM";
	public static final String AnyPositive = "@ANYPOS";
	public static final String AnyNegative = "@ANYNEG";
	public static final String AnySequence = "@ANYSEQ";
	public static final String AnyAddress = "@ANYADDR";

	private String name;
	public SedDefaultValue(CirExpression cir_expression, 
			CType data_type, String name) throws Exception {
		super(cir_expression, data_type);
		
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
