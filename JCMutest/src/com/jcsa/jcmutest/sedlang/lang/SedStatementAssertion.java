package com.jcsa.jcmutest.sedlang.lang;

import com.jcsa.jcmutest.sedlang.SedNode;

/**
 * label::assert_for(int)
 * @author yukimula
 *
 */
public class SedStatementAssertion extends SedAssertion {
	
	/* definition */
	protected SedStatementAssertion() {
		super(null);
	}
	/**
	 * @return the label of the statement being asserted 
	 */
	public SedLabel get_statement() {
		return (SedLabel) this.get_child(0);
	}
	/**
	 * @return integer times required for executing the statement 
	 */
	public SedConstant get_times() {
		return (SedConstant) this.get_child(1);
	}
	
	/* implementation */
	@Override
	protected SedNode copy_self() {
		return new SedStatementAssertion();
	}
	@Override
	public String generate_code() throws Exception {
		return this.get_statement().generate_code() + 
				"::assert_for(" + this.get_times().generate_code() + ")";
	}
	
}
