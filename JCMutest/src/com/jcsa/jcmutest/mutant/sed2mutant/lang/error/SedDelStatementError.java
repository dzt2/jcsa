package com.jcsa.jcmutest.mutant.sed2mutant.lang.error;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * del_stmt(statement) in which the statement is not executed when it should be.
 * 
 * @author yukimula
 *
 */
public class SedDelStatementError extends SedStatementError {

	public SedDelStatementError(CirStatement location, CirStatement orig_statement) {
		super(location, orig_statement);
	}

	@Override
	public String generate_content() throws Exception {
		return "del_stmt(" + this.get_orig_statement().generate_code() + ")";
	}

	@Override
	protected SedNode clone_self() {
		return new SedDelStatementError(
				this.get_location().get_cir_statement(), 
				this.get_orig_statement().get_cir_statement());
	}

}
