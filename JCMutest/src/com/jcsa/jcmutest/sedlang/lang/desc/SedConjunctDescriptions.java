package com.jcsa.jcmutest.sedlang.lang.desc;

import com.jcsa.jcmutest.sedlang.SedKeywords;
import com.jcsa.jcmutest.sedlang.lang.SedNode;

public class SedConjunctDescriptions extends SedDescriptions {

	public SedConjunctDescriptions() throws Exception {
		super(SedKeywords.conjunct);
	}

	@Override
	protected SedNode construct() throws Exception {
		return new SedConjunctDescriptions();
	}

}
