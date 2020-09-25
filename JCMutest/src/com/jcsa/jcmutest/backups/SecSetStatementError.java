package com.jcsa.jcmutest.backups;

import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SecSetStatementError extends SecStatementError {

	public SecSetStatementError(CirStatement statement, 
			CirStatement orig_statement,
			CirStatement muta_statement) throws Exception {
		super(statement, SecKeywords.set_statement, orig_statement);
		this.add_child(new SecStatement(muta_statement));
	}
	
	/**
	 * @return the statement being executed to follow the original statement.
	 */
	public SecStatement get_muta_statement() { 
		return (SecStatement) this.get_child(3);
	}

	@Override
	protected String generate_content() throws Exception {
		return "(" + this.get_orig_statement().generate_code() +
				", " + this.get_muta_statement().generate_code() + ")";
	}

}
