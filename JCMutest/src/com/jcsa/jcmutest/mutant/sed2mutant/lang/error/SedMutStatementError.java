package com.jcsa.jcmutest.mutant.sed2mutant.lang.error;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.token.SedLabel;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * mut_stmt(orig_stmt, muta_stmt) in which original statement is linked to the 
 * mutation statement in which the following should not be in the correct case.
 * 
 * @author yukimula
 *
 */
public class SedMutStatementError extends SedStatementError {

	public SedMutStatementError(CirStatement location, 
			CirStatement orig_statement, CirStatement muta_statement) {
		super(location, orig_statement);
		this.add_child(new SedLabel(null, muta_statement));
	}
	
	/**
	 * @return the statement being executed from the original one
	 */
	public SedLabel get_muta_statement() {
		return (SedLabel) this.get_child(2);
	}

	@Override
	public String generate_content() throws Exception {
		return "mut_stmt(" + this.get_orig_statement().generate_code()
				+ ", " + this.get_muta_statement().generate_code() + ")";
	}

	@Override
	protected SedNode clone_self() {
		return new SedMutStatementError(
				this.get_location().get_cir_statement(),
				this.get_orig_statement().get_cir_statement(),
				this.get_muta_statement().get_cir_statement());
	}
	
}
