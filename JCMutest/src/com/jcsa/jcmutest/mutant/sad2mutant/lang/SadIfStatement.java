package com.jcsa.jcmutest.mutant.sad2mutant.lang;

import com.jcsa.jcmutest.mutant.sad2mutant.SadNode;
import com.jcsa.jcparse.lang.irlang.CirNode;

public class SadIfStatement extends SadStatement {

	protected SadIfStatement(CirNode source) {
		super(source);
	}
	
	/**
	 * @return the condition to decide the branch
	 */
	public SadExpression get_condition() {
		return (SadExpression) this.get_child(1);
	}
	
	/**
	 * @return the label of statement in true branch
	 */
	public SadLabel get_true_label() {
		return (SadLabel) this.get_child(2);
	}
	
	/**
	 * @return the label of statement in false branch
	 */
	public SadLabel get_false_label() {
		return (SadLabel) this.get_child(3);
	}

	@Override
	protected String generate_content() throws Exception {
		return "if(" + this.get_condition().generate_code() + ") then "
				+ this.get_true_label().generate_code() + " else "
				+ this.get_false_label().generate_code() + ";";
	}

	@Override
	protected SadNode clone_self() {
		return new SadIfStatement(this.get_cir_source());
	}
	
}
