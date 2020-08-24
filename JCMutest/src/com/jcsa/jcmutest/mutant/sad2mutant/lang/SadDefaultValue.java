package com.jcsa.jcmutest.mutant.sad2mutant.lang;

import com.jcsa.jcmutest.mutant.sad2mutant.SadNode;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * default_value |-- [?]
 * @author yukimula
 *
 */
public class SadDefaultValue extends SadBasicExpression {

	protected SadDefaultValue(CirNode source, CType data_type) {
		super(source, data_type);
	}

	@Override
	public String generate_code() throws Exception {
		return "[?]";
	}

	@Override
	protected SadNode clone_self() {
		return new SadDefaultValue(this.get_cir_source(), this.get_data_type());
	}

}
