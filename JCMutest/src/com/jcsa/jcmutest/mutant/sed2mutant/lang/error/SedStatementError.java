package com.jcsa.jcmutest.mutant.sed2mutant.lang.error;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.token.SedLabel;

/**
 * 	|--	SedStatementError				{orig_statement: SedLabel}		<br>
 * 	|--	|--	SedAddStatementError		add_stmt(orig_stmt)				<br>
 * 	|--	|--	SedDelStatementError		del_stmt(orig_stmt)				<br>
 * 	|--	|--	SedSetStatementError		set_stmt(orig_stmt, muta_stmt)	<br>
 * @author yukimula
 *
 */
public abstract class SedStatementError extends SedStateError {
	
	/**
	 * @return the statement on which the error requires to influence
	 */
	public SedLabel get_orig_statement() {
		return (SedLabel) this.get_child(1);
	}
	
}
