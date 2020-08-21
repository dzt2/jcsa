package com.jcsa.jcmutest.sedlang.lang;

import com.jcsa.jcmutest.sedlang.SedNode;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * goto_statement |-- goto label
 * @author yukimula
 *
 */
public class SedGotoStatement extends SedStatement {
	
	/* definitions */
	protected SedGotoStatement(CirNode source) {
		super(source);
	}
	/**
	 * @return the label to which the statement goes
	 */
	public SedLabel get_label() {
		return (SedLabel) this.get_child(0);
	}
	
	/* implementations */
	@Override
	protected SedNode copy_self() {
		return new SedGotoStatement(this.get_source());
	}
	@Override
	protected String generate_content() throws Exception {
		return "goto " + this.get_label().generate_code();
	}
	
}
