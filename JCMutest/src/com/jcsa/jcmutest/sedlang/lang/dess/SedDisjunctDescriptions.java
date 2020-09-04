package com.jcsa.jcmutest.sedlang.lang.dess;

import com.jcsa.jcmutest.sedlang.SedKeywords;
import com.jcsa.jcmutest.sedlang.lang.SedNode;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SedDisjunctDescriptions extends SedDescriptions {

	public SedDisjunctDescriptions(CirStatement statement) throws Exception {
		super(statement, SedKeywords.disjunct);
	}

	@Override
	protected SedNode construct() throws Exception {
		return new SedDisjunctDescriptions(
				this.get_statement().get_cir_statement());
	}

}
