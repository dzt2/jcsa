package com.jcsa.jcmutest.mutant.sed2mutant.lang.error;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.token.SedLabel;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * <code>
 * 	SedStatementError				{orig_statement: SedLabel}		<br>
 * 	|--	SedAddStatementError		add_stmt(statement)				<br>
 * 	|--	SedDelStatementError		del_stmt(statement)				<br>
 * 	|--	SedMutStatementError		mut_stmt(statement, statement)	<br>
 * </code>
 * @author yukimula
 *
 */
public abstract class SedStatementError extends SedStateError {

	public SedStatementError(CirStatement location, 
					CirStatement orig_statement) {
		super(location);
		this.add_child(new SedLabel(null, orig_statement));
	}
	
	/**
	 * @return the statement in which the error is seeded
	 */
	public SedLabel get_orig_statement() {
		return (SedLabel) this.get_child(1);
	}
	
}
