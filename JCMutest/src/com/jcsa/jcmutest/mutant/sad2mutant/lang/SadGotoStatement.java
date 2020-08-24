package com.jcsa.jcmutest.mutant.sad2mutant.lang;

import com.jcsa.jcmutest.mutant.sad2mutant.SadNode;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * label: goto label;
 * @author yukimula
 *
 */
public class SadGotoStatement extends SadStatement {

	protected SadGotoStatement(CirNode source) {
		super(source);
	}
	
	/**
	 * @return the label of the target statement
	 */
	public SadLabel get_target_label() {
		return (SadLabel) this.get_child(1);
	}

	@Override
	protected String generate_content() throws Exception {
		return "goto " + this.get_target_label().generate_code() + ";";
	}

	@Override
	protected SadNode clone_self() {
		return new SadGotoStatement(this.get_cir_source());
	}
	
}
