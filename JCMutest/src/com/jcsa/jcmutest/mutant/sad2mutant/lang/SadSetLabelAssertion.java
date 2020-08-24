package com.jcsa.jcmutest.mutant.sad2mutant.lang;

import com.jcsa.jcmutest.mutant.sad2mutant.SadNode;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * assert set_label(label) at statement
 * 
 * @author yukimula
 *
 */
public class SadSetLabelAssertion extends SadAssertion {

	protected SadSetLabelAssertion(CirNode source) {
		super(source);
	}
	
	/**
	 * @return the label of the statement to which the 
	 * 		   mutation statement tries to achieve
	 */
	public SadLabel get_target_label() {
		return (SadLabel) this.get_child(1);
	}

	@Override
	protected String generate_content() throws Exception {
		return "set_label(" + this.get_target_label().generate_code() + ")";
	}

	@Override
	protected SadNode clone_self() {
		return new SadSetLabelAssertion(this.get_cir_source());
	}
	
}
