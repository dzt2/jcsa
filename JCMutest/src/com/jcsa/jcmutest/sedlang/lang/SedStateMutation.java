package com.jcsa.jcmutest.sedlang.lang;

import com.jcsa.jcmutest.sedlang.SedNode;

/**
 * <code>
 * 	+--------------------------------------------------------------------------+<br>
 * 	|--	<i>SedStateMutation</i>													<br>
 * 	|--	|--	SedSetExpression													<br>
 * 	|--	|--	SedAddExpression													<br>
 * 	|--	|--	SedInsExpression													<br>
 * 	|--	|--	SedSetOperator														<br>
 * 	|--	|--	SedAddOperator														<br>
 * 	|--	|--	SedInsOperator														<br>
 * 	|--	|--	SedMutExpression													<br>
 * 	+--------------------------------------------------------------------------+<br>
 * </code>
 * @author yukimula
 *
 */
public abstract class SedStateMutation extends SedNode {
	
	/* definition */
	protected SedStateMutation() {
		super(null);
	}
	
}
