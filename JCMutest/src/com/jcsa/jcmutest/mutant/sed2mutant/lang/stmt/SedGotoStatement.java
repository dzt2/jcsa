package com.jcsa.jcmutest.mutant.sed2mutant.lang.stmt;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.token.SedLabel;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * label: goto label;
 * @author yukimula
 *
 */
public class SedGotoStatement extends SedStatement {

	public SedGotoStatement(CirNode cir_source) {
		super(cir_source);
	}
	
	/**
	 * @return the label of statement being pointed from the goto
	 */
	public SedLabel get_target_label() {
		return (SedLabel) this.get_child(1);
	}

	@Override
	protected String generate_content() throws Exception {
		return "goto " + this.get_target_label().generate_code();
	}

	@Override
	protected SedNode clone_self() {
		return new SedGotoStatement(this.get_cir_source());
	}
	
	
}
