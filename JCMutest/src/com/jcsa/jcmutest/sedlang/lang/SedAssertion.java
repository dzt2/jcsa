package com.jcsa.jcmutest.sedlang.lang;

import com.jcsa.jcmutest.sedlang.SedNode;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * <code>
 * 	+--------------------------------------------------------------------------+<br>
 * 	|--	<i>SedAssertion</i>														<br>
 * 	|--	|--	SedAssertOnCondition												<br>
 * 	|--	|--	SedExecuteOnStatement												<br>
 * 	+--------------------------------------------------------------------------+<br>
 * </code>
 * @author yukimula
 *
 */
public abstract class SedAssertion extends SedNode {
	
	protected SedAssertion(CirNode source) {
		super(source);
	}
	
}
