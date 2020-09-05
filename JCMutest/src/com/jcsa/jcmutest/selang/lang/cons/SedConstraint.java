package com.jcsa.jcmutest.selang.lang.cons;

import com.jcsa.jcmutest.selang.SedKeywords;
import com.jcsa.jcmutest.selang.lang.SedDescription;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * 	|--	SedConstraint														<br>
 * 	|--	|--	SedExecutionConstraint		exec(statement, integer)			<br>
 * 	|--	|--	SedConditionConstraint		assert(statement, expression)		<br>
 * @author yukimula
 *
 */
public abstract class SedConstraint extends SedDescription {

	public SedConstraint(CirStatement statement, SedKeywords keyword) throws Exception {
		super(statement, keyword);
	}

}
