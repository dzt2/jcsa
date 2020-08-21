package com.jcsa.jcmutest.sedlang.lang;

import com.jcsa.jcmutest.sedlang.SedNode;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * if_statement |-- if expression then label else label.
 * @author yukimula
 *
 */
public class SedIfStatement extends SedStatement {
	
	/* definitions */
	protected SedIfStatement(CirNode source) {
		super(source);
	}
	/**
	 * @return the condition to determine its branch
	 */
	public SedExpression get_condition() {
		return (SedExpression) this.get_child(0);
	}
	/**
	 * @return label of the first statement in true branch
	 */
	public SedLabel get_true_label() {
		return (SedLabel) this.get_child(1);
	}
	/**
	 * @return label of the first statement in false branch
	 */
	public SedLabel get_false_label() {
		return (SedLabel) this.get_child(2);
	}
	
	/* implementations */
	@Override
	protected SedNode copy_self() {
		return new SedIfStatement(this.get_source());
	}
	@Override
	protected String generate_content() throws Exception {
		return "if " + this.get_condition().generate_code()
				+ " then " + this.get_true_label().generate_code()
				+ " else " + this.get_false_label().generate_code();
	}
	
}
