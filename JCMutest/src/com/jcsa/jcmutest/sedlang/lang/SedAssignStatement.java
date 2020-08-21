package com.jcsa.jcmutest.sedlang.lang;

import com.jcsa.jcmutest.sedlang.SedNode;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * assign_statement |-- expression := expression ;
 * @author yukimula
 *
 */
public class SedAssignStatement extends SedStatement {
	
	/* definition */
	protected SedAssignStatement(CirNode source) {
		super(source);
	}
	/**
	 * @return the expression to be defined
	 */
	public SedExpression get_lvalue() {
		return (SedExpression) this.get_child(0);
	}
	/**
	 * @return the expression to assign the left-value
	 */
	public SedExpression get_rvalue() {
		return (SedExpression) this.get_child(1);
	}
	
	/* implementations */
	@Override
	protected SedNode copy_self() {
		return new SedAssignStatement(this.get_source());
	}
	@Override
	protected String generate_content() throws Exception {
		return this.get_lvalue().generate_code() + 
				" := " + this.get_rvalue().generate_code();
	}
	
}
