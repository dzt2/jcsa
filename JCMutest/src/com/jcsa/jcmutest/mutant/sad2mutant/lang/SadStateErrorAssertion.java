package com.jcsa.jcmutest.mutant.sad2mutant.lang;

import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * <code>
 * 	|-- SadStateErrorAssertion											<br>
 * 	|--	|--	SadMutStatementAssertion									<br>
 * 	|--	|--	|--	SadSetStatementAssertion	assert#stmt:set_stmt(target)<br>
 * 	|--	|--	|--	SadDelStatementAssertion	assert#stmt:del_stmt()		<br>
 * 	|--	|--	|--	SadTrapStatementAssertion	assert#stmt:trap_stmt()		<br>
 * 	|--	|--	SadMutExpressionAssertion									<br>
 * 	|--	|--	|--	SadSetExpressionAssertion	assert#stmt:set_expr(e,e)	<br>
 * 	|--	|--	|--	SadInsOperatorAssertion		assert#stmt:ins_oprt(e,o)	<br>
 * 	|--	|--	|--	SadInsOperandAssertion		assert#stmt:ins_oprd(e,o,e)	<br>
 * 	|--	|--	|--	SadAddOperandAssertion		assert#stmt:add_oprd(e,o,e)	<br>
 * </code>
 * @author yukimula
 *
 */
public abstract class SadStateErrorAssertion extends SadAssertion {

	protected SadStateErrorAssertion(CirNode source) {
		super(source);
	}
	
	@Override
	public String generate_code() throws Exception {
		return "assert#" + this.get_location().generate_code() + 
							"::{" + this.generate_content() + "}";
	}
	
	protected abstract String generate_content() throws Exception;
	
}
