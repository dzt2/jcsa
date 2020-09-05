package com.jcsa.jcmutest.selang.lang.desc;

import com.jcsa.jcmutest.selang.SedKeywords;
import com.jcsa.jcmutest.selang.lang.SedNode;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SedConjunctDescriptions extends SedDescriptions {

	public SedConjunctDescriptions(CirStatement statement) throws Exception {
		super(statement, SedKeywords.conjunct);
	}

	@Override
	protected SedNode construct() throws Exception {
		return new SedConjunctDescriptions(this.
				get_statement().get_cir_statement());
	}

}
