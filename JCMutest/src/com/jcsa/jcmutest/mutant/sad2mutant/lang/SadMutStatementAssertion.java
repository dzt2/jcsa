package com.jcsa.jcmutest.mutant.sad2mutant.lang;

import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * <code>
 * 	SadMutStatementAssertion									<br>
 * 	|--	SadSetStatementAssertion	assert#stmt:set_stmt(target)<br>
 * 	|--	SadDelStatementAssertion	assert#stmt:del_stmt()		<br>
 * 	|--	SadTrapStatementAssertion	assert#stmt:trap_stmt()		<br>
 * </code>
 * @author yukimula
 *
 */
public abstract class SadMutStatementAssertion extends SadStateErrorAssertion {

	protected SadMutStatementAssertion(CirNode source) {
		super(source);
	}

}
