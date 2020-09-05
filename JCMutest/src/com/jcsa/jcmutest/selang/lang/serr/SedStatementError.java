package com.jcsa.jcmutest.selang.lang.serr;

import com.jcsa.jcmutest.selang.SedKeywords;
import com.jcsa.jcmutest.selang.lang.SedDescription;
import com.jcsa.jcmutest.selang.lang.tokn.SedStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * <code>
 * 	|--	SedStatementError				{orig_statement: SedStatement}		<br>
 * 	|--	|--	SedAddStatementError		add_stmt(statement)					<br>
 * 	|--	|--	SedDelStatementError		del_stmt(statement)					<br>
 * 	|--	|--	SedSetStatementError		set_stmt(statement, statement)		<br>
 * 	|--	|--	SedMutStatementError		mut_stmt(statement, statement)		<br>
 * </code>
 * @author yukimula
 *
 */
public abstract class SedStatementError extends SedDescription {

	public SedStatementError(CirStatement statement, SedKeywords 
			keyword, CirStatement orig_statement) throws Exception {
		super(statement, keyword);
		this.add_child(new SedStatement(orig_statement));
	}
	
	/**
	 * @return the statement that is replaced with the error
	 */
	public SedStatement get_orig_statement() {
		return (SedStatement) this.get_child(2);
	}

}
