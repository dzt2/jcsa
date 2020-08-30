package com.jcsa.jcmutest.mutant.sed2mutant.lang.expr;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.CirNode;

public class SedDefaultValue extends SedBasicExpression {

	public SedDefaultValue(CirNode cir_source, CType data_type) {
		super(cir_source, data_type);
	}

	@Override
	protected SedNode clone_self() {
		return new SedDefaultValue(this.get_cir_source(), this.get_data_type());
	}

	@Override
	public String generate_code() throws Exception {
		return "[?]";
	}

}
