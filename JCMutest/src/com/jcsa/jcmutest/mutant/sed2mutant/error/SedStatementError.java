package com.jcsa.jcmutest.mutant.sed2mutant.error;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.token.SedLabel;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * 	|--	SedStatementError				{orig_statement: SedLabel}		<br>
 * 	|--	|--	SedAddStatementError		add_stmt(stmt)					<br>
 * 	|--	|--	SedDelStatementError		del_stmt(stmt)					<br>
 * 	|--	|--	SedSetStatementError		set_stmt(stmt, stmt)			<br>
 * 	@author dzt2
 *
 */
public abstract class SedStatementError extends SedStateError {
	
	private SedLabel orig_statement;
	protected SedStatementError(CirStatement statement,
			CirStatement orig_statement) throws Exception {
		super(statement);
		this.orig_statement = this.get_sed_statement(orig_statement);
	}
	/**
	 * @return the original statement being mutated in the state error
	 */
	public SedLabel get_orig_statement() { return this.orig_statement; }
	
}
