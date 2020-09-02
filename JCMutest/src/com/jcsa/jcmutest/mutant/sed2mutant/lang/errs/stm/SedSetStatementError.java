package com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.stm;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.token.SedLabel;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * set_stmt(orig_stmt, muta_stmt): the muta_stmt is incorrectly executed following
 * the execution of the orig_stmt in the program under test.
 * @author yukimula
 *
 */
public class SedSetStatementError extends SedStatementError {

	public SedSetStatementError(CirStatement location, 
			CirStatement orig_statement,
			CirStatement muta_statement) {
		super(location, orig_statement);
		this.add_child(new SedLabel(null, muta_statement));
	}
	
	/**
	 * @return the label of the statement that will be incorrectly executed
	 * 		   following the original statement being executed.
	 */
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
		return new SedSetStatementError(
				this.get_location().get_cir_statement(),
				this.get_orig_statement().get_cir_statement(),
				this.get_muta_statement().get_cir_statement());
	}

}
