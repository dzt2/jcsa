package com.jcsa.jcmutest.mutant.sad2mutant.lang;

import com.jcsa.jcmutest.mutant.sad2mutant.SadNode;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * seed#statement::set_label(source,target)
 * @author yukimula
 *
 */
public class SadSetLabelAssertion extends SadMutationAssertion {

	protected SadSetLabelAssertion(CirNode source) {
		super(source);
	}
	
	/**
	 * @return the statement to be muated with its flow
	 */
	public SadStatement get_source_statement() {
		return (SadStatement) this.get_child(0);
	}
	
	/**
	 * @return the statement to be replaced as next one
	 */
	public SadStatement get_target_statement() {
		return (SadStatement) this.get_child(1);
	}

	@Override
	protected String generate_content() throws Exception {
		return "set_label(" + this.get_source_statement().generate_code()
				+ ", " + this.get_target_statement().generate_code() + ")";
	}

	@Override
	protected SadNode clone_self() {
		return new SadSetLabelAssertion(this.get_cir_source());
	}

}
