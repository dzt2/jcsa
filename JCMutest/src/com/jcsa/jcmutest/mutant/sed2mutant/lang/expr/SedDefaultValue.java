package com.jcsa.jcmutest.mutant.sed2mutant.lang.expr;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * default value represents any constant.
 * 
 * @author yukimula
 *
 */
public class SedDefaultValue extends SedBasicExpression {
	
	/** for any value without considering its data type **/
	public static final String AnyValue = "#ANY_VAL";
	/** for any positive value of numeric data type **/
	public static final String AnyPosValue = "#ANY_POS";
	/** for any negative value of numeric data type **/
	public static final String AnyNegValue = "#ANY_NEG";
	
	private String name;
	public SedDefaultValue(CirNode cir_source, CType data_type, String name) {
		super(cir_source, data_type);
		this.name = name;
	}
	
	/**
	 * @return the identifier of the default value
	 */
	public String get_name() { return this.name; }
	
	@Override
	protected SedNode clone_self() {
		return new SedDefaultValue(
				this.get_cir_source(), 
				this.get_data_type(), 
				this.name);
	}

	@Override
	public String generate_code() throws Exception {
		return "[" + this.name + "]";
	}
	
}
