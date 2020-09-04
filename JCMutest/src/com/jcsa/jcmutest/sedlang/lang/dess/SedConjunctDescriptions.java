package com.jcsa.jcmutest.sedlang.lang.dess;

import com.jcsa.jcmutest.sedlang.SedKeywords;
import com.jcsa.jcmutest.sedlang.lang.SedNode;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SedConjunctDescriptions extends SedDescriptions {

	public SedConjunctDescriptions(CirStatement statement) throws Exception {
		super(statement, SedKeywords.conjunct);
	}

	@Override
	protected SedNode construct() throws Exception {
		return new SedConjunctDescriptions(
				this.get_statement().get_cir_statement());
	}

}
