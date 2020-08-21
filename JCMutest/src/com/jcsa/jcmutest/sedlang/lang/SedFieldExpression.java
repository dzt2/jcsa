package com.jcsa.jcmutest.sedlang.lang;

import com.jcsa.jcmutest.sedlang.SedNode;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * field_expression |-- expression . field
 * @author yukimula
 *
 */
public class SedFieldExpression extends SedExpression {

	protected SedFieldExpression(CirNode source, CType data_type) {
		super(source, data_type);
	}
	/**
	 * @return the body of the field-expression
	 */
	public SedExpression get_body() { return (SedExpression) this.get_child(0); }
	/**
	 * @return the field to fetch data from the body
	 */
	public SedField get_field() { return (SedField) this.get_child(1); }
	
	/* implementation */
	@Override
	protected SedNode copy_self() {
		return new SedFieldExpression(this.get_source(), this.get_data_type());
	}
	@Override
	public String generate_code() throws Exception {
		return this.get_body().generate_code() + "." + this.get_field().generate_code();
	}
	
}
