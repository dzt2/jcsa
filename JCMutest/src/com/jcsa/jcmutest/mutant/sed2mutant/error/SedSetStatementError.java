package com.jcsa.jcmutest.mutant.sed2mutant.error;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.token.SedLabel;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * set_stmt(s1, s2): the s2 is executed following the execution of s1
 * even though it is not executed in the correct version of program.
 * @author yukimula
 *
 */
public class SedSetStatementError extends SedStatementError {
	
	private SedLabel muta_statement;
	protected SedSetStatementError(CirStatement statement, 
			CirStatement orig_statement, 
			CirStatement muta_statement) throws Exception {
		super(statement, orig_statement);
		this.muta_statement = this.get_sed_statement(muta_statement);
	}
	/**
	 * @return the statement following the execution of original statement
	 * 		   even though it should not be executed in original program.
	 */
	public SedLabel get_muta_statement() { return this.muta_statement; }
	
	@Override
	protected String generate_content() throws Exception {
		return "set_cstmt(" + this.get_orig_statement().generate_code()
				+ ", " + this.muta_statement.generate_code() + ")";
	}

}
