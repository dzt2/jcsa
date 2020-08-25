package com.jcsa.jcmutest.mutant.sad2mutant.lang;

import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * assert at location : #content
 * <code>
 * 	SadConstraintAssertion				{location: SadStatement}	<br>
 * 	|--	SadExecuteOnAssertion										<br>
 * 	|--	SadConditionAssertion										<br>
 * </code>
 * 
 * @author yukimula
 *
 */
public abstract class SadConstraintAssertion extends SadAssertion {
	
	protected SadConstraintAssertion(CirNode source) {
		super(source);
	}
	
	@Override
	public String generate_code() throws Exception {
		return "assert#" + this.get_location().generate_code() + 
							"::{" + this.generate_content() + "}";
	}
	
	protected abstract String generate_content() throws Exception;
	
}
