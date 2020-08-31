package com.jcsa.jcmutest.mutant.sed2mutant.lang.error;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.token.SedLabel;

/**
 * set_stmt(statement, statement)
 * @author dzt2
 *
 */
public class SedSetStatementError extends SedStatementError {
	
	public SedLabel get_muta_statement() {
		return (SedLabel) this.get_child(2);
	}

	@Override
	protected SedNode clone_self() {
		return new SedSetStatementError();
	}

	@Override
	public String generate_code() throws Exception {
		return "set_stmt(" + this.get_orig_statement().generate_code() + 
				", " + this.get_muta_statement().generate_code() + ")";
	}
	
}
