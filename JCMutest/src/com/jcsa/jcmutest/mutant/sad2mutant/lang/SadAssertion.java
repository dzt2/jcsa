package com.jcsa.jcmutest.mutant.sad2mutant.lang;

import com.jcsa.jcmutest.mutant.sad2mutant.SadNode;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * <code>
 * 	+------------------------------------------------------------------+<br>
 * 	SadAssertion														<br>
 * 	|--	SadConstraintAssertion				{location: SadStatement}	<br>
 * 	|--	|--	SadExecuteOnAssertion										<br>
 * 	|--	|--	SadConditionAssertion										<br>
 * 	|--	SadMutationAssertion				{location: SadStatement}	<br>
 * 	|--	|--	SadSetExpressionAssertion									<br>
 * 	|--	|--	SadSetLabelAssertion										<br>
 * 	|--	|--	SadAddOperandAssertion										<br>
 * 	|--	|--	SadInsOperandAssertion										<br>
 * 	|--	|--	SadInsOperatorAssertion										<br>
 * 	|--	SadCompositeAssertion				{assertions: SadAssertion*}	<br>
 * 	|--	|--	SadConjunctAssertion										<br>
 * 	|--	|--	SadDisjunctAssertion										<br>
 * 	+------------------------------------------------------------------+<br>
 * </code>
 * 
 * @author yukimula
 *
 */
public abstract class SadAssertion extends SadNode {

	protected SadAssertion(CirNode source) {
		super(source);
	}
	
	/**
	 * @return the statement at which the assertion is performed.
	 */
	public SadStatement get_location() {
		return (SadStatement) this.get_child(0);
	}
	
}
