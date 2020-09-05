package com.jcsa.jcmutest.selang.lang.desc;

import com.jcsa.jcmutest.selang.SedKeywords;
import com.jcsa.jcmutest.selang.lang.SedNode;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SedDisjunctDescriptions extends SedDescriptions {

	public SedDisjunctDescriptions(CirStatement statement) throws Exception {
		super(statement, SedKeywords.disjunct);
	}

	@Override
	protected SedNode construct() throws Exception {
		return new SedDisjunctDescriptions(this.
				get_statement().get_cir_statement());
	}

}
