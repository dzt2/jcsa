package com.jcsa.jcmutest.sedlang.lang;

import com.jcsa.jcmutest.sedlang.SedNode;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * call_statement |-- call function on argument_list to label.
 * @author yukimula
 *
 */
public class SedCallStatement extends SedStatement {
	
	/* definitions */
	protected SedCallStatement(CirNode source) {
		super(source);
	}
	/**
	 * @return the function being called in the statement
	 */
	public SedExpression get_function() {
		return (SedExpression) this.get_child(0);
	}
	/**
	 * @return the arguments applied on calling the function
	 */
	public SedArgumentList get_argument_list() {
		return (SedArgumentList) this.get_child(1);
	}
	/**
	 * @return the label of the next statement after call it.
	 */
	public SedLabel get_call_label() {
		return (SedLabel) this.get_child(2);
	}
	
	/* implementation */
	@Override
	protected SedNode copy_self() {
		return new SedCallStatement(this.get_source());
	}
	@Override
	protected String generate_content() throws Exception {
		return "call " + this.get_function().generate_code()
				+ " on " + this.get_argument_list().generate_code()
				+ " to " + this.get_call_label().generate_code();
	}
	

}
