package com.jcsa.jcmutest.sedlang;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * default_value |-- [?]
 * @author yukimula
 *
 */
public class SedDefaultValue extends SedBasicExpression {

	protected SedDefaultValue(CirNode source, CType data_type) {
		super(source, data_type);
	}

	@Override
	protected SedNode copy_self() {
		return new SedDefaultValue(this.get_source(), this.get_data_type());
	}

	@Override
	protected String generate_code() throws Exception {
		return "[?]";
	}

}
