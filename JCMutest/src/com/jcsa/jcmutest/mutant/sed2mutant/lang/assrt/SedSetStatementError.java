package com.jcsa.jcmutest.mutant.sed2mutant.lang.assrt;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.token.SedLabel;

/**
 * set_stmt(orig, muta)
 * @author yukimula
 *
 */
public class SedSetStatementError extends SedStatementError {
	
	public SedLabel get_muta_statement() {
		return (SedLabel) this.get_child(2);
	}

	@Override
	protected String generate_content() throws Exception {
		return "set_stmt(" + this.get_orig_statement().generate_code()
				+ ", " + this.get_muta_statement().generate_code() + ")";
	}

	@Override
	protected SedNode clone_self() {
		return new SedSetStatementError();
	}
	
}
