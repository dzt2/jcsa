package com.jcsa.jcmutest.sedlang.lang.desc;

import com.jcsa.jcmutest.sedlang.SedKeywords;
import com.jcsa.jcmutest.sedlang.lang.SedNode;

public class SedDisjunctDescriptions extends SedDescriptions {

	public SedDisjunctDescriptions() throws Exception {
		super(SedKeywords.disjunct);
	}

	@Override
	protected SedNode construct() throws Exception {
		return new SedDisjunctDescriptions();
	}

}
