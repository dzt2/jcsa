package com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.stm;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.errs.SedStateError;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.token.SedLabel;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * <code>
 * 	|--	SedStatementError				{orig_statement: SedLabel}			<br>
 * 	|--	|--	SedAddStatementError		add_stmt(orig_stmt)					<br>
 * 	|--	|--	SedDelStatementError		del_stmt(orig_stmt)					<br>
 * 	|--	|--	SedSetStatementError		set_stmt(orig_stmt, muta_stmt)		<br>
 * </code>
 * @author yukimula
 *
 */
public abstract class SedStatementError extends SedStateError {

	public SedStatementError(CirStatement 
			location, CirStatement orig_statement) {
		super(location);
		this.add_child(new SedLabel(null, orig_statement));
	}
	
	/**
	 * @return the statement that needs to be mutated
	 */
	public SedLabel get_orig_statement() {
		return (SedLabel) this.get_child(1);
	}
	
}
