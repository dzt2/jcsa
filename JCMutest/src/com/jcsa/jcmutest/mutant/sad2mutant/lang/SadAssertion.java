package com.jcsa.jcmutest.mutant.sad2mutant.lang;

import com.jcsa.jcmutest.mutant.sad2mutant.SadNode;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * assert ... at statement;<br>
 * <code>
 * 	+------------------------------------------------------------------+<br>
 * 	SadAssertion						{location: SadStatement}		<br>
 * 	|--	SadExecuteOnAssertion											<br>
 * 	|--	SadConditionAssertion											<br>
 * 	|--	SadSetExpressionAssertion										<br>
 * 	|--	SadSetLabelAssertion											<br>
 * 	|--	SadAddOperandAssertion											<br>
 * 	|--	SadInsOperandAssertion											<br>
 * 	|--	SadAddOperatorAssertion											<br>
 * 	|--	SadInsOperatorAssertion											<br>
 * 	|--	SadMutExpressionAssertion										<br>
 * 	+------------------------------------------------------------------+<br>
 * </code>
 * @author yukimula
 *
 */
public abstract class SadAssertion extends SadNode {
	
	protected SadAssertion(CirNode source) {
		super(source);
	}
	
	/**
	 * @return the location in which the assertion is evaluated
	 */
	public SadStatement get_location() {
		return (SadStatement) this.get_child(0);
	}
	
	@Override
	public String generate_code() throws Exception {
		return "assert " + this.generate_content() + " at " + 
						this.get_location().generate_code();
	}
	
	/**
	 * @return assertion-content
	 * @throws Exception
	 */
	protected abstract String generate_content() throws Exception;
	
}
